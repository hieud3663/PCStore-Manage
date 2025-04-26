/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import com.pcstore.controller.ReturnController;
import com.pcstore.model.Return;
import com.pcstore.service.ServiceFactory;
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

    /**
     * Creates new form ReturnService
     */
    public ReturnServiceForm() {
        // Khởi tạo bản dịch trạng thái từ tiếng Anh sang tiếng Việt
        statusTranslation = new HashMap<>();
        statusTranslation.put("Pending", "Đang chờ xử lý");
        statusTranslation.put("Approved", "Đã phê duyệt");
        statusTranslation.put("Rejected", "Đã từ chối");
        statusTranslation.put("Completed", "Đã hoàn thành");
        
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
                "Không thể kết nối đến cơ sở dữ liệu: " + ex.getMessage(),
                "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupTable() {
        // Thiết lập mô hình bảng
        tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0);
        
        // Thiết lập tiêu đề cột
        String[] columnNames = {
            "Mã Trả Hàng", "Mã Sản Phẩm", "Tên Sản Phẩm", 
            "Số Lượng", "Lý Do", "Ngày Trả", "Trạng Thái"
        };
        
        tableModel.setColumnIdentifiers(columnNames);
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
            
            // Gọi controller để lấy tất cả đơn trả hàng
            List<Return> returns = returnController.getAllReturns();
            displayReturns(returns);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu đơn trả hàng: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Thêm đơn trả hàng mới vào bảng
     * @param returnObj Đơn trả hàng mới
     */
    public void addReturnToTable(Return returnObj) {
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
            jTable1.scrollRectToVisible(jTable1.getCellRect(lastRow, 0, true));
            jTable1.setRowSelectionInterval(lastRow, lastRow);
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
        if (jTable1.getColumnCount() > 0) {
            // Đặt kích thước cột ID
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(70);
            // Đặt kích thước cột ProductID
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
            // Đặt kích thước cột ProductName
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
            // Đặt kích thước cột Quantity
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(70);
            // Đặt kích thước cột Reason (rộng hơn để hiển thị đủ lý do)
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(200);
            // Đặt kích thước cột Date
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(150);
            // Đặt kích thước cột Status
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(120);
        }
    }

    private void searchReturns() {
        String keyword = jTextField1.getText().trim();
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
                    "Không tìm thấy đơn trả hàng nào phù hợp với từ khóa: " + keyword,
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tìm kiếm: " + ex.getMessage(),
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

    private void showReturnDetails() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một đơn trả hàng để xem chi tiết",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Lấy ID của đơn trả hàng được chọn
        Integer returnId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        // Hiển thị chi tiết đơn trả hàng
        try {
            Optional<Return> returnOpt = returnController.getReturnById(returnId);
            if (returnOpt.isPresent()) {
                showReturnDetailDialog(returnOpt.get());
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy thông tin đơn trả hàng với ID: " + returnId, 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải chi tiết đơn trả hàng: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReturnDetailDialog(Return returnObj) {
        // Tạo dialog hiển thị chi tiết đơn trả hàng
        javax.swing.JDialog detailDialog = new javax.swing.JDialog();
        detailDialog.setTitle("Chi tiết đơn trả hàng #" + returnObj.getReturnId());
        detailDialog.setModal(true);
        detailDialog.setSize(700, 500);
        detailDialog.setLocationRelativeTo(this);
        
        // Tạo panel hiển thị thông tin chi tiết
        javax.swing.JPanel detailPanel = new javax.swing.JPanel();
        detailPanel.setLayout(new java.awt.BorderLayout());
        
        // Tạo các thành phần UI để hiển thị thông tin
        javax.swing.JPanel infoPanel = new javax.swing.JPanel(new java.awt.GridLayout(0, 2, 10, 10));
        infoPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Thêm các thông tin chi tiết
        addDetailRow(infoPanel, "Mã đơn trả hàng:", returnObj.getReturnId().toString());
        addDetailRow(infoPanel, "Sản phẩm:", returnObj.getInvoiceDetail().getProduct().getProductName());
        addDetailRow(infoPanel, "Mã sản phẩm:", returnObj.getInvoiceDetail().getProduct().getProductId());
        addDetailRow(infoPanel, "Số lượng:", String.valueOf(returnObj.getQuantity()));
        addDetailRow(infoPanel, "Lý do trả:", returnObj.getReason());
        addDetailRow(infoPanel, "Ngày trả:", returnObj.getReturnDate().format(dateFormatter));
        
        // Hiển thị trạng thái đã dịch sang tiếng Việt
        String status = returnObj.getStatus();
        String translatedStatus = statusTranslation.getOrDefault(status, status);
        addDetailRow(infoPanel, "Trạng thái:", translatedStatus);
        
        addDetailRow(infoPanel, "Ghi chú:", returnObj.getNotes() != null ? returnObj.getNotes() : "");
        
        // Thêm panel chứa nút đóng
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Nút đóng dialog
        javax.swing.JButton closeButton = new javax.swing.JButton("Đóng");
        closeButton.addActionListener(e -> detailDialog.dispose());
        buttonPanel.add(closeButton);
        
        // Thêm các panel vào dialog
        detailPanel.add(infoPanel, java.awt.BorderLayout.CENTER);
        detailPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);
        detailDialog.add(detailPanel);
        
        // Hiển thị dialog
        detailDialog.setVisible(true);
    }

    private void addDetailRow(javax.swing.JPanel panel, String label, String value) {
        panel.add(new javax.swing.JLabel(label));
        javax.swing.JTextField field = new javax.swing.JTextField(value);
        field.setEditable(false);
        panel.add(field);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        btnReturnInformationLookup = new com.k33ptoo.components.KButton();
        btnDetailReturnCard = new com.k33ptoo.components.KButton();
        btnReturnProduct2 = new com.k33ptoo.components.KButton();
        btnRemoveReturn = new com.k33ptoo.components.KButton();

        jScrollPane2.setViewportView(jEditorPane1);

        setMinimumSize(new java.awt.Dimension(1053, 713));
        setPreferredSize(new java.awt.Dimension(1153, 713));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/pcstore/resources/vi_VN"); // NOI18N
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("ReTurnServicere"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        jScrollPane1.setAutoscrolls(true);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Sản Phẩm", "Tên Sản Phẩm", "Số Lượng", "Lý Do", "Ngày Đổi/Trả", "Phân Loại", "Trạng Thái"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Tìm Kiếm"));

        jTextField1.setToolTipText("");
        jTextField1.setMargin(new java.awt.Insets(2, 6, 2, 0));
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        btnReturnInformationLookup.setText(bundle.getString("btnReturnInformationLookup")); // NOI18N
        btnReturnInformationLookup.setkBackGroundColor(new java.awt.Color(102, 153, 255));
        btnReturnInformationLookup.setkEndColor(new java.awt.Color(102, 153, 255));
        btnReturnInformationLookup.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnReturnInformationLookup.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnReturnInformationLookup.setkHoverStartColor(new java.awt.Color(153, 255, 153));
        btnReturnInformationLookup.setkStartColor(new java.awt.Color(102, 153, 255));
        btnReturnInformationLookup.setMargin(new java.awt.Insets(2, 14, 0, 14));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReturnInformationLookup, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReturnInformationLookup, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        btnDetailReturnCard.setText(bundle.getString("btnDetailReturnCard")); // NOI18N
        btnDetailReturnCard.setkEndColor(new java.awt.Color(102, 153, 255));
        btnDetailReturnCard.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnDetailReturnCard.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnDetailReturnCard.setkHoverStartColor(new java.awt.Color(153, 255, 153));
        btnDetailReturnCard.setkIndicatorThickness(255);
        btnDetailReturnCard.setkStartColor(new java.awt.Color(102, 153, 255));

        btnReturnProduct2.setText(bundle.getString("btnReturnProduct")); // NOI18N
        btnReturnProduct2.setkBorderRadius(30);
        btnReturnProduct2.setkEndColor(new java.awt.Color(0, 255, 51));
        btnReturnProduct2.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnReturnProduct2.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnReturnProduct2.setkHoverStartColor(new java.awt.Color(153, 255, 153));
        btnReturnProduct2.setkStartColor(new java.awt.Color(0, 204, 255));

        btnRemoveReturn.setText(bundle.getString("btnRemoveRepair")); // NOI18N
        btnRemoveReturn.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnRemoveReturn.setkBorderRadius(30);
        btnRemoveReturn.setkEndColor(new java.awt.Color(255, 102, 51));
        btnRemoveReturn.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnRemoveReturn.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnRemoveReturn.setkHoverStartColor(new java.awt.Color(153, 255, 153));
        btnRemoveReturn.setkStartColor(new java.awt.Color(255, 0, 51));
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 43, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnDetailReturnCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1030, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnReturnProduct2, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRemoveReturn, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(232, 232, 232)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnReturnProduct2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnRemoveReturn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDetailReturnCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 1110, 630));
    }// </editor-fold>//GEN-END:initComponents

    private void btnRemoveReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveReturnActionPerformed
        try {
            // Kiểm tra xem có hàng nào được chọn không
            int selectedRow = jTable1.getSelectedRow();
            if (selectedRow == -1) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một đơn trả hàng để xóa.",
                    "Thông báo",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Lấy ID của đơn trả hàng được chọn
            Object returnIdObj = jTable1.getValueAt(selectedRow, 0);
            if (returnIdObj == null || returnIdObj.toString().isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Đơn trả hàng không có ID hợp lệ.",
                    "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            Integer returnId;
            try {
                returnId = Integer.parseInt(returnIdObj.toString());
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "ID đơn trả hàng không hợp lệ: " + returnIdObj,
                    "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra controller
            if (returnController == null) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Không thể kết nối tới hệ thống. Vui lòng thử lại sau.",
                    "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kiểm tra trạng thái đơn trả hàng trước khi xóa
            Optional<Return> returnOpt = returnController.getReturnById(returnId);
            if (!returnOpt.isPresent()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Không tìm thấy đơn trả hàng với ID: " + returnId,
                    "Thông báo",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            Return returnObj = returnOpt.get();
            String status = returnObj.getStatus();
            
            // Chỉ cho phép xóa đơn trả hàng ở trạng thái "Pending" (Đang chờ xử lý)
            if (!"Pending".equals(status)) {
                String translatedStatus = statusTranslation.getOrDefault(status, status);
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Chỉ có thể xóa đơn trả hàng ở trạng thái 'Đang chờ xử lý'.\n" +
                    "Đơn trả hàng hiện tại đang ở trạng thái: " + translatedStatus,
                    "Không thể xóa",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Xác nhận trước khi xóa
            int choice = javax.swing.JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa đơn trả hàng này không?\n" +
                "Thông tin đơn trả hàng:\n" +
                "- Mã đơn: " + returnId + "\n" +
                "- Sản phẩm: " + returnObj.getInvoiceDetail().getProduct().getProductName() + "\n" +
                "- Số lượng: " + returnObj.getQuantity() + "\n" +
                "- Lý do trả: " + returnObj.getReason(),
                "Xác nhận xóa",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE);

            if (choice != javax.swing.JOptionPane.YES_OPTION) {
                return; // Người dùng đã hủy việc xóa
            }

            // Tiến hành xóa đơn trả hàng
            boolean success = returnController.deleteReturn(returnId);

            if (success) {
                // Hiển thị thông báo thành công
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Đã xóa đơn trả hàng thành công!",
                    "Thành công",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);

                // Cập nhật lại bảng
                loadAllReturns();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Không thể xóa đơn trả hàng. Vui lòng thử lại sau.",
                    "Lỗi",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this,
                "Lỗi khi xóa đơn trả hàng: " + e.getMessage(),
                "Lỗi",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnRemoveReturnActionPerformed

    private void btnRemoveReturnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRemoveReturnMouseClicked
        // Gọi phương thức xử lý sự kiện action để tránh lặp code
        btnRemoveReturnActionPerformed(null);
    }//GEN-LAST:event_btnRemoveReturnMouseClicked

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        searchReturns();
    }//GEN-LAST:event_jTextField1ActionPerformed

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
        btnReturnProduct2.addActionListener(this::btnReturnProductActionPerformed);
        btnDetailReturnCard.addActionListener(this::btnDetailReturnCardActionPerformed);
    }

    private void initComponentsCustom() {
        initComponents();
        addListeners();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KButton btnDetailReturnCard;
    private com.k33ptoo.components.KButton btnRemoveReturn;
    private com.k33ptoo.components.KButton btnReturnInformationLookup;
    private com.k33ptoo.components.KButton btnReturnProduct2;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
