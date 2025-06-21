/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import com.pcstore.controller.ReturnController;
import com.pcstore.model.Return;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.TableUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author DUC ANH
 */
public class ReturnServiceForm extends javax.swing.JPanel {

    private ReturnController returnController;
    private DefaultTableModel tableModel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Map<String, String> statusTranslation;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane ScrollPaneTable;
    private com.k33ptoo.components.KButton btnDetailReturnCard;
    private com.k33ptoo.components.KButton btnRemoveReturn;
    private com.k33ptoo.components.KButton btnReturnInformationLookup;
    private com.k33ptoo.components.KButton btnReturnProduct;
    private com.k33ptoo.components.KButton btnUpdateStatus;
    private javax.swing.JPanel panelBody;
    private javax.swing.JPanel pnReturnFunctions;
    private javax.swing.JPanel pnReturnMain;
    private javax.swing.JPanel pnSearch;
    private javax.swing.JTable tbReturn;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables


    /**
     * Creates new form ReturnService
     */
    public ReturnServiceForm() {
        initComponents();
        // Khởi tạo bản dịch trạng thái từ tiếng Anh sang tiếng Việt
        statusTranslation = new HashMap<>();
        statusTranslation.put("Pending", "Đang chờ xử lý");
        statusTranslation.put("Approved", "Đã phê duyệt");
        statusTranslation.put("Rejected", "Đã từ chối");
        statusTranslation.put("Completed", "Đã hoàn thành");
        
        addListeners();
        initComponentsCustom();
        initController();
        setupTable();
        loadAllReturns();
        
    }

    private void initController() {
        try {
            // Khởi tạo controller sử dụng ServiceFactory
            returnController = new ReturnController(
                ServiceFactory.getInstance().getConnection(),
                ServiceFactory.getInvoiceService(),
                ServiceFactory.getProductService()
            );
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                ErrorMessage.DB_CONNECTION_ERROR + ": " + ex.getMessage(),
                "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupTable() {
        // Thiết lập mô hình bảng
        tableModel = (DefaultTableModel) tbReturn.getModel();
        tableModel.setRowCount(0);
        
        // Thiết lập tiêu đề cột
        String[] columnNames = {
            "Mã Trả Hàng", "Mã Sản Phẩm", "Tên Sản Phẩm", 
            "Số Lượng", "Lý Do", "Ngày Trả", "Trạng Thái"
        };
        
        tableModel.setColumnIdentifiers(columnNames);
        TableUtils.applyDefaultStyle(tbReturn);
    }

    /**
     * Phương thức public để tải lại dữ liệu đơn trả hàng
     * Được gọi từ AddReturnProductForm sau khi tạo đơn trả hàng mới
     */
    public void loadAllReturns() {
        try {
            if (returnController == null) {
                return;
            }
            List<Return> returns = returnController.getAllReturns();
            displayReturns(returns);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                ErrorMessage.RETURN_LOAD_ERROR + ": " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Thêm đơn trả hàng mới vào bảng
     * @param returnObj Đơn trả hàng mới
     */
    public  void addReturnToTable(Return returnObj) {
        if (returnObj == null) return;
        
        String status = returnObj.getStatus();
        // Dịch trạng thái sang tiếng Việt nếu có
        String translatedStatus = statusTranslation.getOrDefault(status, status);
        
        Object[] rowData = {
            returnObj.getReturnId(),
            returnObj.getInvoiceDetail().getProduct().getProductId(),
            returnObj.getInvoiceDetail().getProduct().getProductName(),
            returnObj.getQuantity(),
            returnObj.getReason(),
            returnObj.getReturnDate().format(dateFormatter),
            translatedStatus
        };
        tableModel.addRow(rowData);
        
        // Cuộn đến dòng mới thêm vào
        int lastRow = tableModel.getRowCount() - 1;
        if (lastRow >= 0) {
            tbReturn.scrollRectToVisible(tbReturn.getCellRect(lastRow, 0, true));
            tbReturn.setRowSelectionInterval(lastRow, lastRow);
        }
    }

    private void displayReturns(List<Return> returns) {
        // Xóa dữ liệu cũ
        tableModel.setRowCount(0);
        
        // Hiển thị dữ liệu mới
        for (Return returnObj : returns) {
            String status = returnObj.getStatus();
            // Dịch trạng thái sang tiếng Việt nếu có
            String translatedStatus = statusTranslation.getOrDefault(status, status);
            
            Object[] rowData = {
                returnObj.getReturnId(),
                returnObj.getInvoiceDetail().getProduct().getProductId(),
                returnObj.getInvoiceDetail().getProduct().getProductName(),
                returnObj.getQuantity(),
                returnObj.getReason(),
                returnObj.getReturnDate().format(dateFormatter),
                translatedStatus
            };
            tableModel.addRow(rowData);
        }
        
        // Cập nhật lại kích thước của các cột để hiển thị tốt hơn
        if (tbReturn.getColumnCount() > 0) {
            // Đặt kích thước cột ID
            tbReturn.getColumnModel().getColumn(0).setPreferredWidth(70);
            // Đặt kích thước cột ProductID
            tbReturn.getColumnModel().getColumn(1).setPreferredWidth(100);
            // Đặt kích thước cột ProductName
            tbReturn.getColumnModel().getColumn(2).setPreferredWidth(200);
            // Đặt kích thước cột Quantity
            tbReturn.getColumnModel().getColumn(3).setPreferredWidth(70);
            // Đặt kích thước cột Reason (rộng hơn để hiển thị đủ lý do)
            tbReturn.getColumnModel().getColumn(4).setPreferredWidth(200);
            // Đặt kích thước cột Date
            tbReturn.getColumnModel().getColumn(5).setPreferredWidth(150);
            // Đặt kích thước cột Status
            tbReturn.getColumnModel().getColumn(6).setPreferredWidth(120);
        }
    }

    private void searchReturns() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadAllReturns();
            return;
        }
        try {
            List<Return> searchResults;
            
            // Kiểm tra xem từ khóa có phải là ID
            if (keyword.matches("\\d+")) {
                // Tìm theo ID
                Optional<Return> returnById = returnController.getReturnById(Integer.parseInt(keyword));
                searchResults = returnById.isPresent() ? 
                    List.of(returnById.get()) : List.of();
            } 
            // Kiểm tra xem từ khóa có phải là số điện thoại
            else if (keyword.matches("\\d{10,11}")) {
                // Tìm theo số điện thoại khách hàng
                searchResults = returnController.getReturnsByCustomer(keyword);
            } 
            // Kiểm tra xem có phải là mã sản phẩm không
            else if (keyword.matches("[A-Za-z0-9]+")) {
                // Tìm theo mã sản phẩm
                searchResults = returnController.getReturnsByProduct(keyword);
            } 
            else {
                // Tìm kiếm tổng hợp (theo từ khóa trong lý do, tên sản phẩm, v.v.)
                searchResults = returnController.searchReturns(keyword);
            }
            
            displayReturns(searchResults);
            if (searchResults.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    String.format(ErrorMessage.RETURN_NOT_FOUND_WITH_KEYWORD, keyword),
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                ErrorMessage.RETURN_SEARCH_ERROR + ": " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddReturnForm() {
        // Mở form thêm đơn trả hàng mới, truyền this để AddReturnProductForm có thể cập nhật lại dữ liệu
        AddReturnProductForm addForm = new AddReturnProductForm(this);
        
        // Hiển thị trong dialog
        javax.swing.JDialog dialog = new javax.swing.JDialog();
        dialog.setTitle("Thêm đơn trả hàng mới");
        dialog.setModal(true);
        dialog.setSize(1040, 700);
        dialog.setLocationRelativeTo(this);
        dialog.add(addForm);
        dialog.setVisible(true);
    }

    /**
     * Hiển thị chi tiết đơn trả hàng khi chọn một dòng và nhấn nút Chi tiết
     */
    private void showReturnDetails() {
        int selectedRow = tbReturn.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                ErrorMessage.RETURN_SELECT_ONE,
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Lấy ID của đơn trả hàng được chọn
        int modelRow = tbReturn.convertRowIndexToModel(selectedRow);
        Integer returnId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        // Hiển thị chi tiết đơn trả hàng
        try {
            Optional<Return> returnOpt = returnController.getReturnById(returnId);
            if (returnOpt.isPresent()) {
                showReturnDetailInDialog(returnOpt.get());
            } else {
                JOptionPane.showMessageDialog(this, 
                    String.format(ErrorMessage.RETURN_NOT_FOUND_WITH_ID, returnId), 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                ErrorMessage.RETURN_DETAIL_LOAD_ERROR + ": " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Hiển thị chi tiết đơn trả hàng trong Dialog sử dụng ReturnDetailForm
     * @param returnObj Đối tượng Return cần hiển thị
     */
    private void showReturnDetailInDialog(Return returnObj) {
        // Tạo dialog để hiển thị chi tiết
        javax.swing.JDialog detailDialog = new javax.swing.JDialog();
        detailDialog.setTitle("Chi tiết đơn trả hàng #" + returnObj.getReturnId());
        detailDialog.setModal(true);
        detailDialog.setSize(700, 500);
        detailDialog.setLocationRelativeTo(this);
        
        // Tạo ReturnDetailForm để hiển thị thông tin
        ReturnDetailForm detailForm = new ReturnDetailForm(returnObj);
        
        // Thêm panel chứa nút đóng ở dưới form chi tiết
        javax.swing.JPanel containerPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        containerPanel.add(detailForm, java.awt.BorderLayout.CENTER);
        
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        javax.swing.JButton closeButton = new javax.swing.JButton("Đóng");
        closeButton.addActionListener(e -> detailDialog.dispose());
        buttonPanel.add(closeButton);
        
        containerPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);
        
        // Thêm vào dialog và hiển thị
        detailDialog.add(containerPanel);
        detailDialog.setVisible(true);
    }

    private void addDetailRow(javax.swing.JPanel panel, String label, String value) {
        panel.add(new javax.swing.JLabel(label));
        javax.swing.JTextField field = new javax.swing.JTextField(value);
        field.setEditable(false);
        panel.add(field);
    }

    /**
     * Xử lý sự kiện khi click vào nút xóa đơn trả hàng
     */
    private void deleteSelectedReturn() {
        int selectedRow = tbReturn.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                ErrorMessage.RETURN_SELECT_ONE,
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Chuyển từ dòng hiển thị sang dòng thực trong model (quan trọng khi có sắp xếp)
        int modelRow = tbReturn.convertRowIndexToModel(selectedRow);
        
        // Lấy thông tin từ dòng được chọn
        Integer returnId = (Integer) tableModel.getValueAt(modelRow, 0);
        String productName = (String) tableModel.getValueAt(modelRow, 2);
        String status = (String) tableModel.getValueAt(modelRow, 6);
        
        // Kiểm tra trạng thái - chỉ cho phép xóa đơn trả có trạng thái "Đang chờ xử lý"
        if (!status.equals(statusTranslation.get("Pending"))) {
            JOptionPane.showMessageDialog(this, 
                ErrorMessage.RETURN_DELETE_ONLY_PENDING + "\n" +
                ErrorMessage.RETURN_CURRENT_STATUS + status, 
                "Không thể xóa", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Hiển thị dialog xác nhận
        int confirm = JOptionPane.showConfirmDialog(this, 
            String.format(ErrorMessage.RETURN_DELETE_CONFIRM, returnId, productName), 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            boolean result = returnController.deleteReturn(returnId);
            if (result) {
                tableModel.removeRow(modelRow);
                JOptionPane.showMessageDialog(this, 
                    ErrorMessage.RETURN_DELETE_SUCCESS, 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    ErrorMessage.RETURN_DELETE_FAIL, 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                ErrorMessage.RETURN_DELETE_ERROR + ": " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật trạng thái đơn trả hàng
     */
    private void updateReturnStatus() {
        int selectedRow = tbReturn.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                ErrorMessage.RETURN_SELECT_ONE,
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Chuyển từ dòng hiển thị sang dòng thực trong model
        int modelRow = tbReturn.convertRowIndexToModel(selectedRow);
        
        // Lấy thông tin từ dòng được chọn
        Integer returnId = (Integer) tableModel.getValueAt(modelRow, 0);
        String productName = (String) tableModel.getValueAt(modelRow, 2);
        String currentStatus = (String) tableModel.getValueAt(modelRow, 6);
        
        try {
            // Lấy thông tin đơn trả hiện tại
            Optional<Return> returnOptional = returnController.getReturnById(returnId);
            if (returnOptional.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    String.format(ErrorMessage.RETURN_NOT_FOUND_WITH_ID, returnId), 
                    "Thông báo", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Return returnObj = returnOptional.get();
            
            // Tạo danh sách các trạng thái có thể chuyển đổi
            String[] availableStatuses = {"Đang chờ xử lý", "Đã phê duyệt", "Đã từ chối", "Đã hoàn thành"};
            
            // Hiển thị dialog để chọn trạng thái mới
            String newStatus = (String) JOptionPane.showInputDialog(
                this,
                "Chọn trạng thái mới cho đơn trả hàng #" + returnId + " - " + productName,
                "Cập nhật trạng thái",
                JOptionPane.QUESTION_MESSAGE,
                null,
                availableStatuses,
                currentStatus
            );
            
            // Nếu người dùng không chọn hoặc hủy
            if (newStatus == null || newStatus.equals(currentStatus)) {
                return;
            }
            
            // Chuyển đổi từ tiếng Việt sang tiếng Anh để lưu vào DB
            String englishStatus = null;
            for (Map.Entry<String, String> entry : statusTranslation.entrySet()) {
                if (entry.getValue().equals(newStatus)) {
                    englishStatus = entry.getKey();
                    break;
                }
            }
            
            if (englishStatus == null) {
                JOptionPane.showMessageDialog(this, 
                    ErrorMessage.RETURN_STATUS_CONVERT_ERROR, 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Xử lý logic khi chuyển từ Approved (Đã phê duyệt) sang Completed (Đã hoàn thành)
            if ("Approved".equals(returnObj.getStatus()) && "Completed".equals(englishStatus)) {
                // Hiển thị xác nhận điều chỉnh kho
                int option = JOptionPane.showConfirmDialog(this, 
                    "Khi chuyển sang trạng thái 'Đã hoàn thành', hệ thống sẽ điều chỉnh kho.\n"
                    + "- Hoàn trả số lượng " + returnObj.getQuantity() + " sản phẩm vào kho.\n"
                    + "Bạn có chắc chắn muốn tiếp tục?",
                    "Xác nhận điều chỉnh kho", 
                    JOptionPane.YES_NO_OPTION);
                
                if (option != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            // Gọi controller để cập nhật trạng thái
            boolean result = returnController.updateReturnStatus(returnId, englishStatus);
            
            if (result) {
                tableModel.setValueAt(newStatus, modelRow, 6);
                JOptionPane.showMessageDialog(this, 
                    ErrorMessage.RETURN_STATUS_UPDATE_SUCCESS, 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadAllReturns();
            } else {
                JOptionPane.showMessageDialog(this, 
                    ErrorMessage.RETURN_STATUS_UPDATE_FAIL, 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                ErrorMessage.RETURN_STATUS_UPDATE_ERROR + ": " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnReturnMain = new javax.swing.JPanel();
        pnReturnFunctions = new javax.swing.JPanel();
        btnReturnProduct = new com.k33ptoo.components.KButton();
        btnRemoveReturn = new com.k33ptoo.components.KButton();
        btnDetailReturnCard = new com.k33ptoo.components.KButton();
        btnUpdateStatus = new com.k33ptoo.components.KButton();
        pnSearch = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        btnReturnInformationLookup = new com.k33ptoo.components.KButton();
        panelBody = new javax.swing.JPanel();
        ScrollPaneTable = new javax.swing.JScrollPane();
        tbReturn = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(1053, 713));
        setPreferredSize(new java.awt.Dimension(1153, 713));
        setLayout(new java.awt.BorderLayout());

        pnReturnMain.setBackground(new java.awt.Color(255, 255, 255));
        pnReturnMain.setLayout(new javax.swing.BoxLayout(pnReturnMain, javax.swing.BoxLayout.Y_AXIS));

        pnReturnFunctions.setBackground(new java.awt.Color(255, 255, 255));
        pnReturnFunctions.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        btnReturnProduct.setText(bundle.getString("btnReturnProduct")); // NOI18N
        btnReturnProduct.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnReturnProduct.setkAllowGradient(false);
        btnReturnProduct.setkBackGroundColor(new java.awt.Color(0, 190, 94));
        btnReturnProduct.setkBorderRadius(30);
        btnReturnProduct.setkEndColor(new java.awt.Color(0, 255, 51));
        btnReturnProduct.setkFocusColor(new java.awt.Color(255, 255, 255));
        btnReturnProduct.setkHoverColor(new java.awt.Color(0, 190, 94));
        btnReturnProduct.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnReturnProduct.setkHoverForeGround(new java.awt.Color(51, 204, 255));
        btnReturnProduct.setkStartColor(new java.awt.Color(0, 204, 255));
        btnReturnProduct.setPreferredSize(new java.awt.Dimension(150, 35));
        pnReturnFunctions.add(btnReturnProduct);

        btnRemoveReturn.setText(bundle.getString("btnRemoveRepair")); // NOI18N
        btnRemoveReturn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnRemoveReturn.setkAllowGradient(false);
        btnRemoveReturn.setkBackGroundColor(new java.awt.Color(255, 0, 51));
        btnRemoveReturn.setkBorderRadius(30);
        btnRemoveReturn.setkEndColor(new java.awt.Color(255, 102, 51));
        btnRemoveReturn.setkHoverColor(new java.awt.Color(255, 39, 51));
        btnRemoveReturn.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnRemoveReturn.setkHoverForeGround(new java.awt.Color(51, 204, 255));
        btnRemoveReturn.setkStartColor(new java.awt.Color(255, 0, 51));
        btnRemoveReturn.setPreferredSize(new java.awt.Dimension(150, 35));
        btnRemoveReturn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRemoveReturnMouseClicked(evt);
            }
        });
        btnRemoveReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveReturnActionPerformed(evt);
            }
        });
        pnReturnFunctions.add(btnRemoveReturn);

        btnDetailReturnCard.setText(bundle.getString("btnDetailReturnCard")); // NOI18N
        btnDetailReturnCard.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDetailReturnCard.setkAllowGradient(false);
        btnDetailReturnCard.setkBackGroundColor(new java.awt.Color(102, 153, 255));
        btnDetailReturnCard.setkEndColor(new java.awt.Color(102, 153, 255));
        btnDetailReturnCard.setkHoverColor(new java.awt.Color(102, 185, 241));
        btnDetailReturnCard.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnDetailReturnCard.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnDetailReturnCard.setkIndicatorThickness(255);
        btnDetailReturnCard.setPreferredSize(new java.awt.Dimension(150, 35));
        pnReturnFunctions.add(btnDetailReturnCard);

        btnUpdateStatus.setText(bundle.getString("btnUpdateStatus")); // NOI18N
        btnUpdateStatus.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdateStatus.setkAllowGradient(false);
        btnUpdateStatus.setkBackGroundColor(new java.awt.Color(255, 102, 0));
        btnUpdateStatus.setkHoverColor(new java.awt.Color(255, 153, 0));
        btnUpdateStatus.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnUpdateStatus.setkShowFocusBorder(true);
        btnUpdateStatus.setPreferredSize(new java.awt.Dimension(150, 35));
        pnReturnFunctions.add(btnUpdateStatus);

        pnSearch.setBackground(new java.awt.Color(255, 255, 255));
        pnSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("pnReturnSearch"))); // NOI18N

        txtSearch.setToolTipText("");
        txtSearch.setMargin(new java.awt.Insets(2, 6, 2, 0));
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

        btnReturnInformationLookup.setText(bundle.getString("btnReturnInformationLookup")); // NOI18N
        btnReturnInformationLookup.setkBackGroundColor(new java.awt.Color(102, 153, 255));
        btnReturnInformationLookup.setkEndColor(new java.awt.Color(102, 153, 255));
        btnReturnInformationLookup.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnReturnInformationLookup.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnReturnInformationLookup.setMargin(new java.awt.Insets(2, 14, 0, 14));

        javax.swing.GroupLayout pnSearchLayout = new javax.swing.GroupLayout(pnSearch);
        pnSearch.setLayout(pnSearchLayout);
        pnSearchLayout.setHorizontalGroup(
            pnSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReturnInformationLookup, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnSearchLayout.setVerticalGroup(
            pnSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnSearchLayout.createSequentialGroup()
                .addGroup(pnSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReturnInformationLookup, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        pnReturnFunctions.add(pnSearch);

        pnReturnMain.add(pnReturnFunctions);

        panelBody.setLayout(new java.awt.BorderLayout());

        ScrollPaneTable.setAutoscrolls(true);
        ScrollPaneTable.setMaximumSize(new java.awt.Dimension(32767, 1153));

        tbReturn.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Sản Phẩm", "Tên Sản Phẩm", "Số Lượng", "Lý Do", "Ngày Đổi/Trả", "Phân Loại", "Trạng Thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ScrollPaneTable.setViewportView(tbReturn);
        if (tbReturn.getColumnModel().getColumnCount() > 0) {
            tbReturn.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("txtReturnProductID")); // NOI18N
            tbReturn.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("txtReturnProductName")); // NOI18N
            tbReturn.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("txtReturnQuantity")); // NOI18N
            tbReturn.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("txtReturnReason")); // NOI18N
            tbReturn.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("txtReturnDateReturn")); // NOI18N
            tbReturn.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("txtReturnCategory")); // NOI18N
            tbReturn.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("txtReturnStatus")); // NOI18N
        }

        panelBody.add(ScrollPaneTable, java.awt.BorderLayout.CENTER);

        pnReturnMain.add(panelBody);

        add(pnReturnMain, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void btnRemoveReturnMouseClicked(java.awt.event.MouseEvent evt) {
        deleteSelectedReturn();
    }

    private void btnRemoveReturnActionPerformed(java.awt.event.ActionEvent evt) {
        deleteSelectedReturn();
    }

    private void btnReturnInformationLookupActionPerformed(java.awt.event.ActionEvent evt) {
        searchReturns();
    }

    private void btnReturnProductActionPerformed(java.awt.event.ActionEvent evt) {
        openAddReturnForm();
    }

    private void btnDetailReturnCardActionPerformed(java.awt.event.ActionEvent evt) {
        showReturnDetails();
    }

    private void addListeners() {
        btnReturnInformationLookup.addActionListener(this::btnReturnInformationLookupActionPerformed);
        btnReturnProduct.addActionListener(this::btnReturnProductActionPerformed);
        btnDetailReturnCard.addActionListener(this::btnDetailReturnCardActionPerformed);
        btnRemoveReturn.addActionListener(evt -> deleteSelectedReturn());
        // Thêm sự kiện cho nút cập nhật trạng thái
        btnUpdateStatus.addActionListener(evt -> updateReturnStatus());
    }

    private void initComponentsCustom() {
        initComponents();
                       
        addListeners();
    }

    
}
