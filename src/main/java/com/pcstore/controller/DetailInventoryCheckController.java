package com.pcstore.controller;

import java.awt.Component;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


import com.pcstore.model.Employee;
import com.pcstore.model.InventoryCheck;
import com.pcstore.model.InventoryCheckDetail;
import com.pcstore.model.Product;
import com.pcstore.model.enums.InventoryCheckStatus;
import com.pcstore.service.EmployeeService;
import com.pcstore.service.InventoryCheckService;
import com.pcstore.service.ProductService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.ButtonUtils;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.JExcel;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.TableUtils;
import com.pcstore.utils.BillDataUtils;
import com.pcstore.utils.BillPrintUtils;

import com.pcstore.view.DetailInventoryCheckForm;

import raven.toast.Notifications;

/**
 * Controller điều khiển form chi tiết phiếu kiểm kê
 */
public class DetailInventoryCheckController {
    private DetailInventoryCheckForm view;
    private InventoryCheckService inventoryCheckService;
    private EmployeeService employeeService;
    private ProductService productService;
    private Connection connection;

    private InventoryCheck currentInventoryCheck;
    private List<InventoryCheckDetail> inventoryCheckDetails;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
    private final DecimalFormat numberFormat = new DecimalFormat("#,###");
    private final ResourceBundle bundle = LocaleManager.getInstance().getResourceBundle();

    // Các trạng thái của phiếu kiểm kê
    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_CANCELLED = "CANCELLED";

    private TableRowSorter<TableModel> tableRowSorter;

    public DetailInventoryCheckController(DetailInventoryCheckForm view) {
        this.view = view;
        initializeServices();
        initializeView();
        setupEventListeners();
    }

    /**
     * Constructor với ID phiếu kiểm kê
     */
    public DetailInventoryCheckController(DetailInventoryCheckForm view, String inventoryCheckId) {
        this.view = view;
        initializeServices();
        setupEventListeners();
        loadInventoryCheck(inventoryCheckId);
        initializeView();

    }

    private void initializeServices() {
        try {
            this.inventoryCheckService = ServiceFactory.getInstance().getInventoryCheckService();
            this.employeeService = ServiceFactory.getInstance().getEmployeeService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_INIT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initializeView() {
        setupTable(0);
        setupInventoryDetailTable();

        setupProgressSteps();

        setFormEditable(false);
    }

    private void setupTable(int type){
        tableRowSorter = TableUtils.applyDefaultStyle(view.getTableProducts());

        TableUtils.setupColumnWidths(view.getTableProducts(),
                40, 200, 120, 120, 80, 80, 100, 120);

        TableUtils.setNumberColumns(tableRowSorter, 0, 4, 5, 6, 7);
        TableUtils.disableSortingForColumns(tableRowSorter, 8);

        if (type == 1){
            TableUtils.addDeleteButton(view.getTableProducts(), 8,
                        (table, modelRow, column, value) -> {
                            handleDeleteDetail(value);
                        }, 2
                    );
        }

    }

    private void setupProgressSteps() {
        String[] steps = {
                bundle.getString("inventory.check.detail.controller.create"),
                bundle.getString("inventory.check.detail.controller.input"),
                bundle.getString("inventory.check.detail.controller.closing"),
                bundle.getString("inventory.check.detail.controller.complete")
        };

        view.getProgressStepsPanel().setSteps(steps);
        updateProgressSteps();
    }

    private void setupEventListeners() {
        // Sự kiện nút cập nhật
        view.getBtnUpdate().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUpdateInventoryCheck();
            }
        });

        // Sự kiện nút hoàn thành
        view.getBtnComplete().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCompleteInventoryCheck();
            }
        });

        // Sự kiện nút import Excel
        view.getBtnImportExcel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleImportExcel();
            }
        });

        // Sự kiện nút export Excel
        view.getBtnExportExcel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleExportExcel();
            }
        });

        // Sự kiện nút in
        view.getBtnPrint().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePrint();
            }
        });

        // Sự kiện tìm kiếm
        view.getTextFieldSearch().getTxtSearchField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });

    }

    /**
     * Tải thông tin phiếu kiểm kê theo mã
     */
    public void loadInventoryCheck(String inventoryCheckCode) {
        try {
            Optional<InventoryCheck> inventoryCheckOpt = inventoryCheckService.findInventoryCheckByCode(inventoryCheckCode);

            if (inventoryCheckOpt.isPresent()) {
                this.currentInventoryCheck = inventoryCheckOpt.get();
                loadInventoryCheckDetails();
                populateFormWithData();
                updateProgressSteps();
                updateButtonStates();
            } else {
                JOptionPane.showMessageDialog(view,
                        ErrorMessage.DETAIL_INVENTORY_CHECK_NOT_FOUND.toString(),
                        ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
                view.dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_LOAD_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Tải chi tiết kiểm kê
     */
    private void loadInventoryCheckDetails() {
        try {
            this.inventoryCheckDetails = inventoryCheckService.getCheckDetails(currentInventoryCheck.getId());
            populateInventoryDetailTable();
            updateSummaryData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_LOAD_DETAILS_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Điền dữ liệu vào form
     */
    private void populateFormWithData() {
        if (currentInventoryCheck == null) return;

        String title = bundle.getString("inventory.detail.label.title") + " %s";

        if (title != null && !title.isEmpty()) {
            view.getLbTitle().setText(String.format(title, currentInventoryCheck.getCheckCode()));
        } else {
            view.getLbTitle().setText("CHI TIẾT PHIẾU KIỂM KÊ - " + currentInventoryCheck.getCheckCode());
        }

        view.getTxtInventoryName().setText(currentInventoryCheck.getCheckName());

        if (currentInventoryCheck.getCheckDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            view.getTxtCreateDate().setText(currentInventoryCheck.getCheckDate().format(formatter));
        }

        view.getTxtNotes().setText(currentInventoryCheck.getNotes() != null ?
                currentInventoryCheck.getNotes() : "");

        loadEmployees();

        if (currentInventoryCheck.getEmployee() != null) {
            view.getCbbChecker().setSelectedItem(currentInventoryCheck.getEmployee().getFullName());
        }
    }

    /**
     * Tải danh sách nhân viên
     */
    private void loadEmployees() {
        try {
            view.getCbbChecker().removeAllItems();

            List<Employee> employees = employeeService.findAllEmployees();
            for (Employee employee : employees) {
                view.getCbbChecker().addItem(employee.getFullName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_LOAD_EMPLOYEES_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Điền dữ liệu vào bảng chi tiết kiểm kê
     */
    private void populateInventoryDetailTable() {
        DefaultTableModel model = (DefaultTableModel) view.getTableProducts().getModel();
        model.setRowCount(0);

        if (inventoryCheckDetails == null || inventoryCheckDetails.isEmpty()) {
            return;
        }

        int index = 1;
        for (InventoryCheckDetail detail : inventoryCheckDetails) {
            BigDecimal unitPrice = detail.getProduct().getPrice();

            String actualQuantityStr = null;
            BigDecimal totalValue = BigDecimal.ZERO;

            if (detail.getActualQuantity() != null && detail.getActualQuantity() >= 0) {
                actualQuantityStr = numberFormat.format(detail.getActualQuantity());
                totalValue = unitPrice.multiply(new BigDecimal(detail.getActualQuantity()));
            }

            Object[] row = new Object[]{
                    index++,
                    detail.getProduct().getProductName(),
                    detail.getProduct().getProductId(),
                    "code test",
                    numberFormat.format(detail.getSystemQuantity()),
                    actualQuantityStr, 
                    currencyFormat.format(unitPrice),
                    currencyFormat.format(totalValue)
            };
            model.addRow(row);
        }
    }

    /**
     * Thiết lập editor có validation cho cột số lượng thực tế
     */
    private void setupActualQuantityEditor(int columnIndex) {
        DefaultCellEditor actualQuantityEditor = new DefaultCellEditor(new JTextField()) {
            private JTextField textField;

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                textField = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);

                textField.setCursor(new Cursor(Cursor.TEXT_CURSOR));

                for (KeyListener listener : textField.getKeyListeners()) {
                    textField.removeKeyListener(listener);
                }

                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                            e.consume();
                        }
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            if (validateAndUpdateActualQuantity(table, row, textField.getText())) {
                                stopCellEditing();
                            }
                        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            cancelCellEditing();
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        validateAndUpdateActualQuantity(table, row, textField.getText());
                    }
                });

                return textField;
            }

            @Override
            public boolean stopCellEditing() {
                return super.stopCellEditing();
            }

            @Override
            public Object getCellEditorValue() {
                String text = textField.getText().trim();
                try {
                    return Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }

            @Override
            public boolean isCellEditable(EventObject e) {
                if (e instanceof MouseEvent) {
                    MouseEvent me = (MouseEvent) e;
                    return me.getClickCount() == 1; 
                }
                return super.isCellEditable(e);
            }
        };

        view.getTableProducts().getColumnModel().getColumn(columnIndex).setCellEditor(actualQuantityEditor);
    }

    private void setupInventoryDetailTable() {
        
        for (int i = 0; i < view.getTableProducts().getColumnCount(); i++) {
            if (i != 5) { // Cột Thực tế
                view.getTableProducts().getColumnModel().getColumn(i).setCellEditor(null);

            } else {
                if (currentInventoryCheck != null &&
                        (STATUS_DRAFT.equals(currentInventoryCheck.getStatus()) ||
                                STATUS_IN_PROGRESS.equals(currentInventoryCheck.getStatus()))) {
                    setupActualQuantityEditor(i);
                } else {
                    DefaultTableModel currentModel = (DefaultTableModel) view.getTableProducts().getModel();

                    java.util.Vector<String> columnNames = new java.util.Vector<>();
                    for (int col = 0; col < currentModel.getColumnCount(); col++) {
                        columnNames.add(currentModel.getColumnName(col));
                    }

                    view.getTableProducts().setModel(new DefaultTableModel(
                            currentModel.getDataVector(),
                            columnNames) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    });

                    TableUtils.setNumberColumns(tableRowSorter, 0, 4, 5, 6, 7);
                }
            }
        }
        setupTable(1);
        setupActualQuantityRenderer();
        highlightIncompleteRows();

    }

    /**
     * Thiết lập renderer cho cột số lượng thực tế với cursor TEXT
     */
    private void setupActualQuantityRenderer() {
        DefaultTableCellRenderer actualQuantityRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Thiết lập cursor text cho cột thực tế
                if (column == 5) {
                    c.setCursor(new Cursor(Cursor.TEXT_CURSOR));

                    if (!isSelected && row < table.getRowCount()) {
                        try {
                            String productId = table.getValueAt(row, 2).toString();

                            InventoryCheckDetail detail = findDetailByProductId(productId);

                            if (detail != null) {
                                if (detail.getActualQuantity() == null || detail.getActualQuantity() < 0) {
                                    c.setBackground(new Color(255, 255, 200)); // Màu vàng nhạt
                                    c.setForeground(Color.BLACK);

                                    if (c instanceof JLabel) {
                                        ((JLabel) c).setToolTipText("Click to edit quantity");
                                        ((JLabel) c).setBorder(
                                                javax.swing.BorderFactory.createLineBorder(Color.GRAY, 2));
                                        
                                    }
                                } else {
                                    c.setBackground(table.getBackground());
                                    c.setForeground(table.getForeground());

                                    if (c instanceof JLabel) {
                                        ((JLabel) c).setToolTipText("Click to edit quantity");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // Xử lý nếu không tìm thấy ProductID
                            c.setBackground(table.getBackground());
                            c.setForeground(table.getForeground());
                        }
                    }
                } else if (column != 8) { // KHÔNG áp dụng cho cột nút xóa
                    c.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    c.setBackground(table.getBackground());
                    c.setForeground(table.getForeground());
                }

                if (c instanceof JLabel) {
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                }
                return c;
        }
    };

        // Áp dụng renderer cho tất cả các cột
        for (int i = 0; i < view.getTableProducts().getColumnCount(); i++) {
            if (i == 8 ) continue; 
            view.getTableProducts().getColumnModel().getColumn(i).setCellRenderer(actualQuantityRenderer);
        }
    }

    /**
     * Thiết lập highlight cho các ô chưa nhập đầy đủ
     */
    private void highlightIncompleteRows() {
        DefaultTableCellRenderer warningRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 5) { 
                    try {
                        String productId = table.getValueAt(row, 2).toString();

                        InventoryCheckDetail detail = findDetailByProductId(productId);

                        if (detail != null && column == 5) {
                            if (detail.getActualQuantity() == null || detail.getActualQuantity() < 0) {
                                c.setBackground(new Color(255, 255, 200)); // Màu vàng nhạt
                                c.setForeground(Color.BLACK);
                            } else if (detail.getActualQuantity() == 0) {
                                c.setBackground(new Color(255, 200, 200)); // Màu đỏ nhạt
                                c.setForeground(Color.BLACK);
                                 ((JLabel) c).setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                            } else {
                                c.setBackground(new Color(255, 255, 200)); // Màu vàng nhạt
                                c.setForeground(Color.BLACK);
                                ((JLabel) c).setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                                ((JLabel) c).setToolTipText("Click to edit quantity");
                                
                            }
                        }
                    } catch (Exception e) {
                        c.setBackground(table.getBackground());
                        c.setForeground(table.getForeground());
                    }
                }

                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        };

        // Áp dụng renderer cho cột số lượng thực tế
        view.getTableProducts().getColumnModel().getColumn(5).setCellRenderer(warningRenderer);
    }

    /**
     * Tìm InventoryCheckDetail theo Product ID
     */
    private InventoryCheckDetail findDetailByProductId(String productId) {
        if (inventoryCheckDetails == null || productId == null) {
            return null;
        }

        for (InventoryCheckDetail detail : inventoryCheckDetails) {
            if (detail.getProduct().getProductId().equals(productId)) {
                return detail;
            }
        }
        return null;
    }

    /**
     * Validate và cập nhật số lượng thực tế
     */
    private boolean validateAndUpdateActualQuantity(JTable table, int row, String inputValue) {
        try {
            // Nếu dòng không hợp lệ, không xử lý
            if (row < 0 || row >= table.getRowCount()) {
                return false;
            }

            if (inputValue == null || inputValue.trim().isEmpty()) {
                Notifications.getInstance().show(Notifications.Type.ERROR,
                        ErrorMessage.DETAIL_INVENTORY_CHECK_QUANTITY_EMPTY.toString());
                return false;
            }

            int actualQuantity;
            try {
                actualQuantity = Integer.parseInt(inputValue.trim());
            } catch (NumberFormatException e) {
                Notifications.getInstance().show(Notifications.Type.ERROR,
                        ErrorMessage.DETAIL_INVENTORY_CHECK_QUANTITY_INVALID.toString());
                return false;
            }

            if (actualQuantity < 0) {
                Notifications.getInstance().show(Notifications.Type.ERROR,
                        ErrorMessage.DETAIL_INVENTORY_CHECK_QUANTITY_NEGATIVE.toString());
                return false;
            }

            if (actualQuantity > 999999) {
                Notifications.getInstance().show(Notifications.Type.WARNING,
                        ErrorMessage.DETAIL_INVENTORY_CHECK_QUANTITY_TOO_LARGE.toString());
                return false;
            }

            // Quan trọng: Đảm bảo lấy productId từ đúng dòng đang chỉnh sửa
            String productId = table.getValueAt(row, 2).toString();

            //Cập nhật SL Thực tế vào bảng tạm
            if (updateActualQuantityByProductId(productId, actualQuantity)) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setValueAt(numberFormat.format(actualQuantity), row, 5);

                updateRowTotalValueByProductId(table, row, productId, actualQuantity);

                updateSummaryData();

                return true;
            }

            return false;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(table,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_UPDATE_QUANTITY_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật số lượng thực tế theo ProductID
     */
    private boolean updateActualQuantityByProductId(String productId, int actualQuantity) {
        try {
            InventoryCheckDetail detail = findDetailByProductId(productId);
            if (detail == null) {
                return false;
            }

            detail.setActualQuantity(actualQuantity);

            // Tính toán chênh lệch
            int discrepancy = actualQuantity - detail.getSystemQuantity();
            detail.setDiscrepancy(discrepancy);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật tổng giá trị cho dòng theo ProductID
     */
    private void updateRowTotalValueByProductId(JTable table, int row, String productId, int actualQuantity) {
        try {
            InventoryCheckDetail detail = findDetailByProductId(productId);
            if (detail == null) {
                return;
            }

            BigDecimal unitPrice = detail.getProduct().getPrice();
            BigDecimal totalValue = unitPrice.multiply(new BigDecimal(actualQuantity));

            // Cập nhật cột tổng giá trị (cột 7)
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setValueAt(currencyFormat.format(totalValue), row, 7);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Validate tất cả dữ liệu trong bảng trước khi hoàn thành
     */
    private boolean validateAllActualQuantities() {
        if (inventoryCheckDetails == null || inventoryCheckDetails.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_VALIDATION_NO_DATA.toString(),
                    ErrorMessage.WARNING_TITLE.toString(), JOptionPane.WARNING_MESSAGE);
            return false;
        }

        int unfinishedCount = 0;
        for (int i = 0; i < inventoryCheckDetails.size(); i++) {
            InventoryCheckDetail detail = inventoryCheckDetails.get(i);

            // Kiểm tra các sản phẩm chưa nhập số lượng thực tế
            if (detail.getActualQuantity() == null || detail.getActualQuantity() < 0) {
                unfinishedCount++;
            }
        }

        if (unfinishedCount > 0) {
            int result = JOptionPane.showConfirmDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_VALIDATION_CONFIRM.format(unfinishedCount),
                    ErrorMessage.CONFIRM_TITLE.toString(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            return result == JOptionPane.YES_OPTION;
        }

        return true;
    }



    /**
     * Cập nhật dữ liệu tổng kết
     */
    private void updateSummaryData() {
        if (inventoryCheckDetails == null || inventoryCheckDetails.isEmpty()) {
            view.getLbTotalValue().setText("0");
            view.getLbTotalIncreaseValue().setText("0");
            view.getLbTotalDecreaseValue().setText("0");
            view.getLbTotalDifferenceValue().setText("0");
            return;
        }

        int totalActual = 0;
        int totalIncrease = 0;
        int totalDecrease = 0;
        int totalDifference = 0;

        for (InventoryCheckDetail detail : inventoryCheckDetails) {
            int discrepancy = detail.getActualQuantity() - detail.getSystemQuantity();

            if (discrepancy > 0) {
                totalIncrease += discrepancy;
            } else if (discrepancy < 0) {
                totalDecrease += Math.abs(discrepancy);
            }

            totalDifference += Math.abs(discrepancy);
            totalActual += (detail.getActualQuantity() != null ? detail.getActualQuantity() : 0);
        }

        view.getLbTotalValue().setText(String.valueOf(totalActual));
        view.getLbTotalIncreaseValue().setText(String.valueOf(totalIncrease));
        view.getLbTotalDecreaseValue().setText(String.valueOf(totalDecrease));
        view.getLbTotalDifferenceValue().setText(String.valueOf(totalDifference));
    }

    /**
     * Cập nhật progress steps theo trạng thái
     */
    private void updateProgressSteps() {
        if (currentInventoryCheck == null) return;

        int currentStep = 2;
        String status = currentInventoryCheck.getStatus();

        switch (status) {
            case STATUS_DRAFT:
                currentStep = 2;
                break;
            case STATUS_IN_PROGRESS:
                currentStep = 3;
                break;
            case STATUS_COMPLETED:
                currentStep = 4;
                break;
            case STATUS_CANCELLED:
                currentStep = 1;
                break;
            default:
                currentStep = 1;
        }

        view.getProgressStepsPanel().setCurrentStep(currentStep);
    }

    /**
     * Cập nhật trạng thái các nút
     */
    private void updateButtonStates() {
        if (currentInventoryCheck == null) return;

        String status = currentInventoryCheck.getStatus();

        boolean canUpdate = STATUS_DRAFT.equals(status) || STATUS_IN_PROGRESS.equals(status);
        ButtonUtils.setKButtonEnabled(view.getBtnUpdate(), canUpdate);

        boolean canComplete = STATUS_IN_PROGRESS.equals(status);
        ButtonUtils.setKButtonEnabled(view.getBtnComplete(), canComplete);

        boolean canImport = !STATUS_COMPLETED.equals(status) && !STATUS_CANCELLED.equals(status);
        ButtonUtils.setKButtonEnabled(view.getBtnImportExcel(), canImport);

        boolean formEditable = !STATUS_COMPLETED.equals(status) && !STATUS_CANCELLED.equals(status);
        setFormEditable(formEditable);

        boolean canExport = (view.getTableProducts().getRowCount() > 0);
        ButtonUtils.setKButtonEnabled(view.getBtnExportExcel(), canExport);

    }

    /**
     * Thiết lập form có thể chỉnh sửa hay không
     */
    private void setFormEditable(boolean editable) {
        view.getTxtInventoryName().setEditable(editable);
        view.getTxtCreateDate().setEditable(false); // Ngày luôn không chỉnh sửa được
        view.getCbbChecker().setEnabled(editable);
        view.getTxtNotes().setEditable(editable);
        view.getBtnChooseDate().setEnabled(editable);
    }

    private boolean saveDetailsToDatabase() {
        if (inventoryCheckDetails == null) return false;

        boolean checkSuccess = true;
        for (InventoryCheckDetail detail : inventoryCheckDetails) {
            if (detail.getActualQuantity() != null) {
                try {
                    inventoryCheckService.updateCheckDetail(detail);
                } catch (Exception e) {
                    checkSuccess = false;
                    e.printStackTrace();
                }
            }
        }
        return checkSuccess;
    }

    /**
     * Xử lý cập nhật phiếu kiểm kê
     */
    private void handleUpdateInventoryCheck() {
        try {
            if (!validateInput()) {
                return;
            }

            int result = JOptionPane.showConfirmDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_UPDATE_CONFIRM.toString(),
                    ErrorMessage.DETAIL_INVENTORY_CHECK_UPDATE_CONFIRM_TITLE.toString(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (result != JOptionPane.YES_OPTION) {
                return;
            }

            updateInventoryCheckFromForm();

            if (STATUS_DRAFT.equals(currentInventoryCheck.getStatus())) {
                currentInventoryCheck.setStatus(STATUS_IN_PROGRESS);
            }

            InventoryCheck updatedCheck = inventoryCheckService.updateInventoryCheck(currentInventoryCheck);
            this.currentInventoryCheck = updatedCheck;

            JOptionPane.showMessageDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_UPDATE_SUCCESS.toString(),
                    ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);

            populateFormWithData();

            boolean detailSaved = saveDetailsToDatabase();
            if (!detailSaved) {
                JOptionPane.showMessageDialog(view,
                        ErrorMessage.DETAIL_INVENTORY_CHECK_UPDATE_DETAILS_WARNING.toString(),
                        ErrorMessage.WARNING_TITLE.toString(), JOptionPane.WARNING_MESSAGE);
            }

            refreshForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_UPDATE_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Xử lý hoàn thành phiếu kiểm kê
     */
    private void handleCompleteInventoryCheck() {
        try {
            if (inventoryCheckDetails == null || inventoryCheckDetails.isEmpty()) {
                JOptionPane.showMessageDialog(view,
                        ErrorMessage.DETAIL_INVENTORY_CHECK_COMPLETE_NO_DETAILS.get(),
                        ErrorMessage.WARNING_TITLE.get(), JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Hiển thị thông tin tổng kết
            StringBuilder summary = new StringBuilder();
            summary.append(bundle.getString("inventory.check.detail.controller.summary.info")).append("\n");
            summary.append("- ").append(bundle.getString("inventory.check.detail.controller.summary.total")).append(": ").append(inventoryCheckDetails.size()).append("\n");
            summary.append("- ").append(bundle.getString("inventory.check.detail.controller.summary.increase")).append(": ").append(view.getLbTotalIncreaseValue().getText()).append("\n");
            summary.append("- ").append(bundle.getString("inventory.check.detail.controller.summary.decrease")).append(": ").append(view.getLbTotalDecreaseValue().getText()).append("\n");
            summary.append("- ").append(bundle.getString("inventory.check.detail.controller.summary.difference")).append(": ").append(view.getLbTotalDifferenceValue().getText()).append("\n\n");
            summary.append(bundle.getString("inventory.check.detail.controller.summary.confirm")).append("\n");
            summary.append(bundle.getString("inventory.check.detail.controller.summary.note"));

            int result = JOptionPane.showConfirmDialog(view,
                    summary.toString(),
                    bundle.getString("inventory.check.detail.controller.confirm.complete.title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {

                if (!saveDetailsToDatabase()) {
                    JOptionPane.showMessageDialog(view,
                            ErrorMessage.DETAIL_INVENTORY_CHECK_COMPLETE_SAVE_ERROR.get(),
                            ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = inventoryCheckService.completeInventoryCheck(currentInventoryCheck.getId());

                if (success) {
                    JOptionPane.showMessageDialog(view,
                            ErrorMessage.DETAIL_INVENTORY_CHECK_COMPLETE_SUCCESS.get(),
                            bundle.getString("inventory.check.detail.controller.success.title"), JOptionPane.INFORMATION_MESSAGE);

                    // Refresh form
                    refreshForm();
                } else {
                    JOptionPane.showMessageDialog(view,
                            ErrorMessage.DETAIL_INVENTORY_CHECK_COMPLETE_ERROR.get(),
                            ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_COMPLETE_EXCEPTION.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Xử lý import Excel
     */
    
    private void handleImportExcel() {
        if (currentInventoryCheck == null) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_IMPORT_NO_CURRENT.toString(),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        if (!STATUS_DRAFT.equals(currentInventoryCheck.getStatus()) && 
            !STATUS_IN_PROGRESS.equals(currentInventoryCheck.getStatus())) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_IMPORT_STATUS_INVALID.toString(),
                    ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle(ErrorMessage.DETAIL_INVENTORY_CHECK_IMPORT_FILE_TITLE.toString());
        
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Excel files (*.xlsx, *.xls)", "xlsx", "xls"));
        
        int result = fileChooser.showOpenDialog(view);
        
        if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                
                // Đọc file Excel
                JExcel jExcel = new JExcel();
                List<List<Object>> data = jExcel.fromExcel(selectedFile.getAbsolutePath());

                // System.out.println(data.toString());
                

                if (data == null || data.isEmpty()) {
                    JOptionPane.showMessageDialog(view,
                            ErrorMessage.DETAIL_INVENTORY_CHECK_IMPORT_FILE_INVALID.toString(),
                            ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int invalidRows = 0;
                int updatedRows = 0;
                
                // Bỏ qua các dòng thừa
                for (int i = 1; i < data.size(); i++) {
                    List<Object> row = data.get(i);
                    
                    if (row.size() < 6) {
                        invalidRows++;
                        continue;
                    }
                    
                    try {
                        String productId = row.get(2).toString(); // Cột mã sản phẩm
                        String actualQuantityStr = row.get(5).toString(); // Cột số lượng thực tế
                        
                        if (productId == null || productId.trim().isEmpty() || 
                            actualQuantityStr == null || actualQuantityStr.trim().isEmpty()) {
                            invalidRows++;
                            continue;
                        }

                        int actualQuantity = Integer.parseInt(actualQuantityStr.trim());
                        
                        boolean success = updateActualQuantityByProductId( productId, actualQuantity);

                        if (success) updatedRows++; else invalidRows++;

                    } catch (NumberFormatException e) {
                        invalidRows++;
                        continue;
                    } catch (Exception e) {
                        invalidRows++;
                        JOptionPane.showMessageDialog(view,
                                ErrorMessage.DETAIL_INVENTORY_CHECK_IMPORT_ROW_ERROR.format(i + 1, e.getMessage()),
                                ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
                    }
                }

                populateInventoryDetailTable();
                populateFormWithData();
                updateSummaryData();
                
                String message = ErrorMessage.DETAIL_INVENTORY_CHECK_IMPORT_RESULT.format(updatedRows, invalidRows);

                JOptionPane.showMessageDialog(view, 
                        message,
                        ErrorMessage.DETAIL_INVENTORY_CHECK_IMPORT_RESULT_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(view,
                        ErrorMessage.DETAIL_INVENTORY_CHECK_IMPORT_ERROR.format(e.getMessage()),
                        ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * Xử lý export Excel
     */
    private void handleExportExcel() {
        if (inventoryCheckDetails == null || inventoryCheckDetails.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_NO_DATA.toString(),
                    ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTable table = view.getTableProducts();
        int rowCnt = view.getTableProducts().getRowCount();
        String[] header = new String[]{
            ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_HEADERS_STT.get(),
            ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_HEADERS_PRODUCT_NAME.get(),
            ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_HEADERS_PRODUCT_ID.get(),
            ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_HEADERS_BARCODE.get(),
            ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_HEADERS_SYSTEM_QTY.get(),
            ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_HEADERS_ACTUAL_QTY.get(),
            ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_HEADERS_UNIT_PRICE.get(),
            ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_HEADERS_TOTAL_PRICE.get()
        };

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_METADATA_WAREHOUSE.get(), ErrorMessage.DETAIL_INVENTORY_CHECK_MAIN_WAREHOUSE.get());
        metadata.put(ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_METADATA_CODE.get(), currentInventoryCheck.getCheckCode());
        metadata.put(ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_METADATA_NAME.get(), currentInventoryCheck.getCheckName());
        metadata.put(ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_METADATA_CREATOR.get(), currentInventoryCheck.getEmployee() != null ? currentInventoryCheck.getEmployee().getFullName() : ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_METADATA_NO_CREATOR.get());
        metadata.put(ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_METADATA_CREATE_DATE.get(), currentInventoryCheck.getCreatedAt() != null ? currentInventoryCheck.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_METADATA_NO_DATE.get());
        metadata.put(ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_METADATA_NOTES.get(), bundle.getString("inventory.check.detail.controller.export.note"));

        Object data[][] = new Object[rowCnt][header.length];
        try{
            for(int i=0; i<rowCnt; i++){
                data[i][0] = table.getValueAt(i, 0); // STT
                data[i][1] = table.getValueAt(i, 1); // Tên sản phẩm
                data[i][2] = table.getValueAt(i, 2); // Mã sản phẩm
                data[i][3] = table.getValueAt(i, 3); // Barcode
                data[i][4] = table.getValueAt(i, 4); // SL Tồn kho
                data[i][5] = table.getValueAt(i, 5); // SL thực tế
                data[i][6] = table.getValueAt(i, 6).toString().replaceAll("[^0-9.]", "");
                data[i][7] = table.getValueAt(i, 7).toString().replaceAll("[^0-9.]", "");
            }

            List<String> headerList = Arrays.asList(header);
            List<List<Object>> dataList = Arrays.stream(data)
                    .map(Arrays::asList)
                    .collect(Collectors.toList());

            String fileName = "DANH_SACH_SAN_PHAM_KIEM_KE_" + currentInventoryCheck.getCheckCode();

            JExcel jExcel = new JExcel();
            String success = jExcel.toExcel(headerList, dataList, ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_SHEET_TITLE.get(), metadata, fileName);

            if (success != null) {
                Notifications.getInstance().show(Notifications.Type.SUCCESS,
                        ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_SUCCESS.format(fileName));
            } else {
                JOptionPane.showMessageDialog(view,
                        ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_ERROR.toString(),
                        ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            }

        }catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_EXPORT_EXCEPTION.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }
    }

    /**
     * Xử lý in phiếu - Sử dụng Map
     */
    private void handlePrint() {
        // Validate dữ liệu
        if (!BillPrintUtils.validatePrintData(view, currentInventoryCheck, inventoryCheckDetails)) {
            return;
        }

        try {
            Map<String, Object> printData = createPrintDataMap();

            String defaultFileName = "PhieuKiemKe_" + currentInventoryCheck.getCheckCode();
            BillPrintUtils.printBill(view, "bill_inventory_check_template", printData, defaultFileName);
            //Sửa lại reposive nhé

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_PRINT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Tạo Map dữ liệu cho việc in phiếu
     */
    private Map<String, Object> createPrintDataMap() {
        Map<String, Object> data = new HashMap<>();

        // Thông tin cơ bản
        data.put("inventoryCheckCode", currentInventoryCheck.getCheckCode());
        data.put("inventoryCheckName", currentInventoryCheck.getCheckName());
        data.put("checkDate", currentInventoryCheck.getCheckDate());
        data.put("createdDate", currentInventoryCheck.getCreatedAt());
        data.put("status", currentInventoryCheck.getStatus());
        data.put("notes", currentInventoryCheck.getNotes());
        data.put("warehouseName", ErrorMessage.DETAIL_INVENTORY_CHECK_MAIN_WAREHOUSE.get());

        // Thông tin nhân viên
        if (currentInventoryCheck.getEmployee() != null) {
            data.put("checkerName", currentInventoryCheck.getEmployee().getFullName());
            data.put("createdBy", currentInventoryCheck.getEmployee().getFullName());
        } else {
            data.put("checkerName", "");
            data.put("createdBy", "");
        }

        data.put("inventoryItems", createInventoryItemsList());

        // Thống kê
        Map<String, Integer> summary = calculateSummary();
        data.put("totalActual", summary.get("totalActual"));
        data.put("totalIncrease", summary.get("totalIncrease"));
        data.put("totalDecrease", summary.get("totalDecrease"));
        data.put("totalDifference", summary.get("totalDifference"));

        return data;
    }

    private List<Map<String, Object>> createInventoryItemsList() {
        List<Map<String, Object>> items = new ArrayList<>();

        if (inventoryCheckDetails == null || inventoryCheckDetails.isEmpty()) {
            return items;
        }
        String status= InventoryCheckStatus.COMPLETED.getDbValue();
        for (InventoryCheckDetail detail : inventoryCheckDetails) {
            Map<String, Object> item = new HashMap<>();
            item.put("productName", detail.getProduct().getProductName());
            item.put("productId", detail.getProduct().getProductId());
            item.put("systemQuantity", detail.getSystemQuantity());

            item.put("actualQuantity", currentInventoryCheck.getStatus().equals(status) ? detail.getActualQuantity() : "");

            item.put("barcode", detail.getProduct().getBarcode() != null ? detail.getProduct().getBarcode() : " ");
            item.put("discrepancy", currentInventoryCheck.getStatus().equals(status) ? detail.getDiscrepancy() : ""); 
            item.put("notes", detail.getReason() != null ? detail.getReason() : "");
            items.add(item);
        }
        return items;
    }

    private Map<String, Integer> calculateSummary() {
        Map<String, Integer> summary = new HashMap<>();
        summary.put("totalActual", 0);
        summary.put("totalIncrease", 0);
        summary.put("totalDecrease", 0);
        summary.put("totalDifference", 0);

        if (inventoryCheckDetails == null || inventoryCheckDetails.isEmpty()) {
            return summary;
        }

        for (InventoryCheckDetail detail : inventoryCheckDetails) {
            int actualQuantity = detail.getActualQuantity() != null ? detail.getActualQuantity() : 0;
            int systemQuantity = detail.getSystemQuantity();

            summary.put("totalActual", summary.get("totalActual") + actualQuantity);

            if (actualQuantity > systemQuantity) {
                summary.put("totalIncrease", summary.get("totalIncrease") + (actualQuantity - systemQuantity));
            } else if (actualQuantity < systemQuantity) {
                summary.put("totalDecrease", summary.get("totalDecrease") + (systemQuantity - actualQuantity));
            }

            summary.put("totalDifference", summary.get("totalDifference") + Math.abs(actualQuantity - systemQuantity));
        }

        return summary;
    }


    private void handleDeleteDetail(Object value){
        if (value == null) return;
        String productID = value.toString();

        try {

            InventoryCheckDetail deleteDetail = inventoryCheckDetails.stream()
                    .filter(detail -> detail.getProduct().getProductId().equals(productID))
                    .findFirst()
                    .orElse(null);
            if (deleteDetail == null) {
                // Notifications.getInstance().show(Notifications.Type.ERROR,
                //         "Không tìm thấy sản phẩm có mã: " + productID + " trong phiếu kiểm kê!");
                return;
            }

            int result = JOptionPane.showConfirmDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_CONFIRM.format(productID),
                    ErrorMessage.DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_CONFIRM_TITLE.toString(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (result != JOptionPane.YES_OPTION) 
                return;

            InventoryCheckStatus status = InventoryCheckStatus.fromDbValue(currentInventoryCheck.getStatus());

            if (status != null && !status.canDeleteDetail()) {
                Notifications.getInstance().show(Notifications.Type.WARNING,
                        ErrorMessage.DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_STATUS_INVALID.format(status.getDisplayText(bundle)));
                return;
            }

            boolean success = inventoryCheckService.deleteCheckDetail(deleteDetail.getId());

            if (success) {
                Notifications.getInstance().show(Notifications.Type.SUCCESS, 
                        ErrorMessage.DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_SUCCESS.toString());
                inventoryCheckDetails.remove(deleteDetail);
                refreshForm();
            } else {
                Notifications.getInstance().show(Notifications.Type.ERROR, 
                        ErrorMessage.DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_FAILED.toString());
            }

        } catch (Exception e) {
            Notifications.getInstance().show(Notifications.Type.ERROR,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_DELETE_PRODUCT_ERROR.format(e.getMessage()));
            e.printStackTrace();
        }
    }



    /**
     * Thực hiện tìm kiếm trong bảng
     */
    private void performSearch() {
        String searchText = view.getTextFieldSearch().getText().trim().toLowerCase();

        TableUtils.applyFilter(tableRowSorter, searchText, 1, 2, 3);

    }


    /**
     * Cập nhật thông tin phiếu kiểm kê từ form
     */
    private void updateInventoryCheckFromForm() throws Exception {
        if (currentInventoryCheck == null) return;

        currentInventoryCheck.setCheckName(view.getTxtInventoryName().getText().trim());

        currentInventoryCheck.setNotes(view.getTxtNotes().getText().trim());
        String selectedEmployeeName = (String) view.getCbbChecker().getSelectedItem();
        if (selectedEmployeeName != null) {
            List<Employee> employees = employeeService.findAllEmployees();
            for (Employee emp : employees) {
                if (emp.getFullName().equals(selectedEmployeeName)) {
                    currentInventoryCheck.setEmployee(emp);
                    break;
                }
            }
        }

        currentInventoryCheck.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Validate dữ liệu đầu vào
     */
    private boolean validateInput() {
        // Kiểm tra tên phiếu kiểm kê
        if (view.getTxtInventoryName().getText().trim().isEmpty()) {
            view.getTxtInventoryName().requestFocus();

            Notifications.getInstance().show(Notifications.Type.ERROR,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_NAME_EMPTY.toString());

            return false;
        }

        // Kiểm tra nhân viên được chọn
        if (view.getCbbChecker().getSelectedItem() == null) {
            Notifications.getInstance().show(Notifications.Type.ERROR,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_EMPLOYEE_EMPTY.toString());

            view.getCbbChecker().requestFocus();
            return false;
        }

        return true;
    }


    /**
     * Refresh dữ liệu form
     */
    public void refreshForm() {
        if (currentInventoryCheck != null) {
            loadInventoryCheck(currentInventoryCheck.getCheckCode());
            // updateProgressSteps();
            // updateButtonStates();
            // populateFormWithData();
            updateSummaryData();

        }
        TableUtils.refreshSorter(view.getTableProducts());
    }

    /**
     * Bắt đầu kiểm kê (chuyển từ DRAFT sang IN_PROGRESS)
     */
    public boolean startInventoryCheck() {
        try {
            if (currentInventoryCheck == null) return false;

            boolean success = inventoryCheckService.startInventoryCheck(currentInventoryCheck.getId());
            if (success) {
                refreshForm();
                updateProgressSteps();
                return true;
            }
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_START_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Hủy phiếu kiểm kê
     */
    public boolean cancelInventoryCheck() {
        try {
            if (currentInventoryCheck == null) return false;

            int result = JOptionPane.showConfirmDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_CANCEL_CONFIRM.toString(),
                    ErrorMessage.DETAIL_INVENTORY_CHECK_CANCEL_CONFIRM_TITLE.toString(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                boolean success = inventoryCheckService.cancelInventoryCheck(currentInventoryCheck.getId());
                if (success) {
                    refreshForm();
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.DETAIL_INVENTORY_CHECK_CANCEL_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Cleanup resources
     */
    public void dispose() {
        // Cleanup resources nếu cần
    }

    // Getter methods
    public InventoryCheck getCurrentInventoryCheck() {
        return currentInventoryCheck;
    }

    public List<InventoryCheckDetail> getInventoryCheckDetails() {
        return inventoryCheckDetails;
    }

    public InventoryCheckService getInventoryCheckService() {
        return inventoryCheckService;
    }

    public EmployeeService getEmployeeService() {
        return employeeService;
    }
}