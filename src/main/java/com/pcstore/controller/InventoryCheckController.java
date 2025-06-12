package com.pcstore.controller;

import com.pcstore.model.enums.InventoryCheckStatus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.pcstore.model.InventoryCheck;
import com.pcstore.service.EmployeeService;
import com.pcstore.service.InventoryCheckService;
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.TableUtils;
import com.pcstore.view.AddInventoryCheckForm;
import com.pcstore.view.DetailInventoryCheckForm;
import com.pcstore.view.InventoryCheckForm;

import raven.toast.Notifications;

/**
 * Controller điều khiển form quản lý kiểm kê
 */
public class InventoryCheckController {
    private InventoryCheckForm inventoryCheckForm;
    private InventoryCheckService inventoryCheckService;
    private EmployeeService employeeService;
    private Connection connection;
    private TableRowSorter<TableModel> tableSorter;
    private ResourceBundle bundle;

    public InventoryCheckController(InventoryCheckForm view) {
        this.inventoryCheckForm = view;
        this.bundle = LocaleManager.getInstance().getResourceBundle();
        initializeServices();
        initializeView();
        setupEventListeners();
        loadInventoryChecks();
    }

    private void initializeServices() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            this.inventoryCheckService = new InventoryCheckService(connection);
            this.employeeService = new EmployeeService(connection);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(inventoryCheckForm,
                    "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initializeView() {
        // Thiết lập bảng
        setupTable();

        // Thiết lập ComboBox trạng thái
        setupStatusComboBox();

    }

    private void setupTable() {
        // Áp dụng style mặc định cho bảng
        tableSorter = TableUtils.applyDefaultStyle(inventoryCheckForm.getTableListInventory());
        TableUtils.setBooleanColumns(inventoryCheckForm.getTableListInventory(), 0);

        TableUtils.addDeleteButton(inventoryCheckForm.getTableListInventory(), 7,
                (table, modelRow, column, value) -> {
                    handleDeleteInventoryCheck(value);
                });

        TableUtils.disableSortingForColumns(tableSorter, 0, 7);

        TableUtils.setupColumnWidths(inventoryCheckForm.getTableListInventory(), 30, 120, 200, 150, 200, 150, 120, 60);
    }

    private void setupStatusComboBox() {
        inventoryCheckForm.getCbbStatus().removeAllItems();

        // Thêm tất cả trạng thái vào ComboBox
        for (InventoryCheckStatus status : InventoryCheckStatus.values()) {
            inventoryCheckForm.getCbbStatus().addItem(status.getDisplayText(bundle));
        }
    }

    private void setupEventListeners() {
        // Sự kiện nút thêm
        inventoryCheckForm.getBtnAdd().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddInventoryCheck();
            }
        });

        // Sự kiện thay đổi trạng thái filter
        inventoryCheckForm.getCbbStatus().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterByStatus();
            }
        });

        // Sự kiện tìm kiếm
        inventoryCheckForm.getTextFieldSearch().getTxtSearchField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });

        // Sự kiện double click vào bảng để xem chi tiết
        inventoryCheckForm.getTableListInventory().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleViewDetail();
                }
            }
        });
    }

    /**
     * Tải danh sách phiếu kiểm kê
     */
    public void loadInventoryChecks() {
        try {
            List<InventoryCheck> inventoryChecks = inventoryCheckService.getAllInventoryChecks();
            updateTableData(inventoryChecks);
            updateSummary(inventoryChecks.size());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(inventoryCheckForm,
                    "Lỗi tải dữ liệu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật dữ liệu bảng
     */
    private void updateTableData(List<InventoryCheck> inventoryChecks) {
        DefaultTableModel model = (DefaultTableModel) inventoryCheckForm.getTableListInventory().getModel();
        model.setRowCount(0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (InventoryCheck check : inventoryChecks) {
            Object[] row = new Object[]{
                    false, // Checkbox
                    check.getCheckCode(),
                    check.getCheckName(),
                    check.getCreatedAt() != null ? check.getCreatedAt().format(formatter) : "",
                    check.getEmployee() != null ? check.getEmployee().getFullName() : "",
                    check.getCheckDate() != null ? check.getCheckDate().format(formatter) : "",
                    getStatusDisplayText(check.getStatus()),
                    null // Nút xóa
            };
            model.addRow(row);
        }
    }

    /**
     * Chuyển đổi trạng thái từ database sang hiển thị
     */
    private String getStatusDisplayText(String dbStatus) {
        InventoryCheckStatus status = InventoryCheckStatus.fromDbValue(dbStatus);
        return status != null ? status.getDisplayText(bundle) : dbStatus;
    }

    /**
     * Chuyển đổi trạng thái từ hiển thị sang database
     */
    private String getStatusDatabaseValue(String displayStatus) {
        InventoryCheckStatus status = InventoryCheckStatus.fromDisplayText(displayStatus, bundle);
        return status != null ? status.getDbValue() : displayStatus;
    }

    /**
     * Cập nhật thông tin tổng kết
     */
    private void updateSummary(int count) {
        inventoryCheckForm.getTxtSum().setText(String.valueOf(count));
    }

    /**
     * Xử lý thêm phiếu kiểm kê mới
     */
    private void handleAddInventoryCheck() {
        try {
            AddInventoryCheckForm addForm = new AddInventoryCheckForm(
                    (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(inventoryCheckForm),
                    true);

            addForm.setVisible(true);

            loadInventoryChecks();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(inventoryCheckForm,
                    "Lỗi mở form thêm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Xử lý xem chi tiết phiếu kiểm kê
     */
    private void handleViewDetail() {
        int selectedRow = inventoryCheckForm.getTableListInventory().getSelectedRow();

        try {
            int modelRow = inventoryCheckForm.getTableListInventory().convertRowIndexToModel(selectedRow);
            String checkCode = (String) inventoryCheckForm.getTableListInventory().getModel().getValueAt(modelRow, 1);


            Optional<InventoryCheck> inventoryCheckOpt = inventoryCheckService.findInventoryCheckByCode(checkCode);

            if (inventoryCheckOpt.isPresent()) {
                DetailInventoryCheckForm detailForm = new DetailInventoryCheckForm(
                        (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(inventoryCheckForm),
                        true);

                new DetailInventoryCheckController(detailForm, inventoryCheckOpt.get().getCheckCode());

                detailForm.setVisible(true);
                loadInventoryChecks();
            } else {
                JOptionPane.showMessageDialog(inventoryCheckForm,
                        "Không tìm thấy phiếu kiểm kê với mã: " + checkCode,
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(inventoryCheckForm,
                    "Lỗi mở form chi tiết: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Xử lý xóa phiếu kiểm kê
     */
    private void handleDeleteInventoryCheck(Object checkCodeObj) {
        if (checkCodeObj == null) return;

        String checkCode = checkCodeObj.toString();

        try {
            Optional<InventoryCheck> inventoryCheckOpt = inventoryCheckService.findInventoryCheckByCode(checkCode);

            if (!inventoryCheckOpt.isPresent()) {
                JOptionPane.showMessageDialog(inventoryCheckForm,
                        "Không tìm thấy phiếu kiểm kê với mã: " + checkCode,
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            InventoryCheck inventoryCheck = inventoryCheckOpt.get();
            InventoryCheckStatus status = InventoryCheckStatus.fromDbValue(inventoryCheck.getStatus());

            // Sử dụng enum để kiểm tra có thể xóa không
            if (status != null && !status.canDelete()) {
                JOptionPane.showMessageDialog(inventoryCheckForm,
                        "Không thể xóa phiếu kiểm kê với trạng thái: " + status.getDisplayText(bundle),
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Xác nhận xóa
            int result = JOptionPane.showConfirmDialog(inventoryCheckForm,
                    "Bạn có chắc chắn muốn xóa phiếu kiểm kê '" + inventoryCheck.getCheckName() +
                            "' (Mã: " + checkCode + ")?\n\nHành động này không thể hoàn tác!",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                boolean success = inventoryCheckService.deleteInventoryCheck(inventoryCheck.getId());

                if (success) {
                    Notifications.getInstance().show(Notifications.Type.SUCCESS, "Xóa phiếu kiểm kê thành công!");
                    loadInventoryChecks();
                } else {
                    JOptionPane.showMessageDialog(inventoryCheckForm,
                            "Xóa phiếu kiểm kê thất bại!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(inventoryCheckForm,
                    "Lỗi xóa phiếu kiểm kê: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Lọc theo trạng thái
     */
    private void filterByStatus() {
        String selectedStatusDisplay = (String) inventoryCheckForm.getCbbStatus().getSelectedItem();
        InventoryCheckStatus selectedStatus = InventoryCheckStatus.fromDisplayText(selectedStatusDisplay, bundle);

        if (selectedStatus == InventoryCheckStatus.ALL) {
            loadInventoryChecks();
        } else {
            try {
                String dbStatus = selectedStatus.getDbValue();
                List<InventoryCheck> filteredChecks = inventoryCheckService.getInventoryChecksByStatus(dbStatus);
                updateTableData(filteredChecks);
                updateSummary(filteredChecks.size());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(inventoryCheckForm,
                        "Lỗi lọc dữ liệu: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * Thực hiện tìm kiếm
     */
    private void performSearch() {
        String searchText = inventoryCheckForm.getTextFieldSearch().getText().trim();

        if (tableSorter != null) {
            // Mã phiếu (1), Tên phiếu (2), Người tạo (4)
            TableUtils.applyFilter(tableSorter, searchText, 1, 2, 4);
        }
    }

    /**
     * Refresh dữ liệu từ bên ngoài
     */
    public void refreshData() {
        loadInventoryChecks();
    }

    /**
     * Lấy danh sách phiếu kiểm kê đã chọn
     */
    public List<String> getSelectedInventoryCheckCodes() {
        java.util.List<String> selectedCodes = new java.util.ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) inventoryCheckForm.getTableListInventory().getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean isSelected = (Boolean) model.getValueAt(i, 0);
            if (isSelected != null && isSelected) {
                String checkCode = (String) model.getValueAt(i, 1);
                selectedCodes.add(checkCode);
            }
        }

        return selectedCodes;
    }

    /**
     * Xử lý chọn/bỏ chọn tất cả
     */
    public void toggleSelectAll(boolean selectAll) {
        DefaultTableModel model = (DefaultTableModel) inventoryCheckForm.getTableListInventory().getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(selectAll, i, 0);
        }
    }

    /**
     * Lấy thống kê theo trạng thái
     */
    public void showStatistics() {
        try {
            long draftCount = inventoryCheckService.countInventoryChecksByStatus(InventoryCheckStatus.DRAFT.getDbValue());
            long inProgressCount = inventoryCheckService.countInventoryChecksByStatus(InventoryCheckStatus.IN_PROGRESS.getDbValue());
            long completedCount = inventoryCheckService.countInventoryChecksByStatus(InventoryCheckStatus.COMPLETED.getDbValue());
            long cancelledCount = inventoryCheckService.countInventoryChecksByStatus(InventoryCheckStatus.CANCELLED.getDbValue());

            String message = String.format(
                    "Thống kê phiếu kiểm kê:\n\n" +
                            "• %s: %d\n" +
                            "• %s: %d\n" +
                            "• %s: %d\n" +
                            "• %s: %d\n\n" +
                            "Tổng cộng: %d",
                    InventoryCheckStatus.DRAFT.getDisplayText(bundle), draftCount,
                    InventoryCheckStatus.IN_PROGRESS.getDisplayText(bundle), inProgressCount,
                    InventoryCheckStatus.COMPLETED.getDisplayText(bundle), completedCount,
                    InventoryCheckStatus.CANCELLED.getDisplayText(bundle), cancelledCount,
                    draftCount + inProgressCount + completedCount + cancelledCount
            );

            JOptionPane.showMessageDialog(inventoryCheckForm, message,
                    "Thống kê", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(inventoryCheckForm,
                    "Lỗi lấy thống kê: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Xuất dữ liệu ra Excel
     */
    public void exportToExcel() {
        // TODO: Implement export functionality
        JOptionPane.showMessageDialog(inventoryCheckForm,
                "Chức năng xuất Excel đang được phát triển",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Cleanup resources
     */
    public void dispose() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}