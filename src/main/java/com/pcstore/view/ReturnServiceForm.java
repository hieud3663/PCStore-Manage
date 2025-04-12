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
        tableModel = (DefaultTableModel) jTable3.getModel();
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
            jTable3.scrollRectToVisible(jTable3.getCellRect(lastRow, 0, true));
            jTable3.setRowSelectionInterval(lastRow, lastRow);
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
        if (jTable3.getColumnCount() > 0) {
            // Đặt kích thước cột ID
            jTable3.getColumnModel().getColumn(0).setPreferredWidth(70);
            // Đặt kích thước cột ProductID
            jTable3.getColumnModel().getColumn(1).setPreferredWidth(100);
            // Đặt kích thước cột ProductName
            jTable3.getColumnModel().getColumn(2).setPreferredWidth(200);
            // Đặt kích thước cột Quantity
            jTable3.getColumnModel().getColumn(3).setPreferredWidth(70);
            // Đặt kích thước cột Reason (rộng hơn để hiển thị đủ lý do)
            jTable3.getColumnModel().getColumn(4).setPreferredWidth(200);
            // Đặt kích thước cột Date
            jTable3.getColumnModel().getColumn(5).setPreferredWidth(150);
            // Đặt kích thước cột Status
            jTable3.getColumnModel().getColumn(6).setPreferredWidth(120);
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
        dialog.setSize(1040, 800);
        dialog.setLocationRelativeTo(this);
        dialog.add(addForm);
        dialog.setVisible(true);
    }

    private void showReturnDetails() {
        int selectedRow = jTable3.getSelectedRow();
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
        
        // Thêm các nút thao tác
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Nút phê duyệt
        javax.swing.JButton approveButton = new javax.swing.JButton("Phê duyệt");
        approveButton.addActionListener(e -> {
            if ("Pending".equals(returnObj.getStatus())) {
                String processorId = "ADMIN"; // Thay bằng ID của người dùng đang đăng nhập
                String notes = JOptionPane.showInputDialog(detailDialog, "Nhập ghi chú phê duyệt:");
                if (notes != null) {
                    try {
                        boolean success = returnController.approveReturn(returnObj.getReturnId(), processorId, notes);
                        if (success) {
                            JOptionPane.showMessageDialog(detailDialog, "Đã phê duyệt đơn trả hàng thành công");
                            detailDialog.dispose();
                            loadAllReturns(); // Cập nhật lại bảng
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(detailDialog, 
                            "Lỗi khi phê duyệt: " + ex.getMessage(), 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(detailDialog, 
                    "Chỉ có thể phê duyệt đơn trả hàng ở trạng thái Đang chờ xử lý", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Nút từ chối
        javax.swing.JButton rejectButton = new javax.swing.JButton("Từ chối");
        rejectButton.addActionListener(e -> {
            if ("Pending".equals(returnObj.getStatus())) {
                String processorId = "ADMIN"; // Thay bằng ID của người dùng đang đăng nhập
                String notes = JOptionPane.showInputDialog(detailDialog, "Nhập lý do từ chối:");
                if (notes != null) {
                    try {
                        boolean success = returnController.rejectReturn(returnObj.getReturnId(), processorId, notes);
                        if (success) {
                            JOptionPane.showMessageDialog(detailDialog, "Đã từ chối đơn trả hàng thành công");
                            detailDialog.dispose();
                            loadAllReturns(); // Cập nhật lại bảng
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(detailDialog, 
                            "Lỗi khi từ chối: " + ex.getMessage(), 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(detailDialog, 
                    "Chỉ có thể từ chối đơn trả hàng ở trạng thái Đang chờ xử lý", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Nút hoàn thành
        javax.swing.JButton completeButton = new javax.swing.JButton("Hoàn thành");
        completeButton.addActionListener(e -> {
            if ("Approved".equals(returnObj.getStatus())) {
                String processorId = "ADMIN"; // Thay bằng ID của người dùng đang đăng nhập
                String notes = JOptionPane.showInputDialog(detailDialog, "Nhập ghi chú hoàn thành:");
                if (notes != null) {
                    try {
                        boolean success = returnController.completeReturn(returnObj.getReturnId(), processorId, notes);
                        if (success) {
                            JOptionPane.showMessageDialog(detailDialog, "Đã hoàn thành đơn trả hàng thành công");
                            detailDialog.dispose();
                            loadAllReturns(); // Cập nhật lại bảng
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(detailDialog, 
                            "Lỗi khi hoàn thành: " + ex.getMessage(), 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(detailDialog, 
                    "Chỉ có thể hoàn thành đơn trả hàng ở trạng thái Đã phê duyệt", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Nút xóa đơn trả hàng
        javax.swing.JButton deleteButton = new javax.swing.JButton("Xóa");
        deleteButton.addActionListener(e -> {
            // Chỉ cho phép xóa đơn trả hàng ở trạng thái Đang chờ xử lý
            if ("Pending".equals(returnObj.getStatus())) {
                int confirm = JOptionPane.showConfirmDialog(
                    detailDialog, 
                    "Bạn có chắc chắn muốn xóa đơn trả hàng này không?", 
                    "Xác nhận xóa", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        boolean success = returnController.deleteReturn(returnObj.getReturnId());
                        if (success) {
                            JOptionPane.showMessageDialog(detailDialog, 
                                "Đã xóa đơn trả hàng thành công", 
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            detailDialog.dispose();
                            loadAllReturns(); // Cập nhật lại bảng
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(detailDialog, 
                            "Lỗi khi xóa đơn trả hàng: " + ex.getMessage(), 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(detailDialog, 
                    "Chỉ có thể xóa đơn trả hàng ở trạng thái Đang chờ xử lý", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // Nút đóng dialog
        javax.swing.JButton closeButton = new javax.swing.JButton("Đóng");
        closeButton.addActionListener(e -> detailDialog.dispose());
        
        // Thêm các nút vào panel theo trạng thái hiện tại
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(deleteButton);
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
        jTable3 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        btnReturnInformationLookup = new com.k33ptoo.components.KButton();
        btnDetailReturnCard = new com.k33ptoo.components.KButton();
        btnReturnProduct2 = new com.k33ptoo.components.KButton();

        jScrollPane2.setViewportView(jEditorPane1);

        setMinimumSize(new java.awt.Dimension(1053, 713));
        setPreferredSize(new java.awt.Dimension(1153, 713));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/pcstore/resources/vi_VN"); // NOI18N
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("ReTurnServicere"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N

        jScrollPane1.setAutoscrolls(true);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable3);

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
                .addGap(0, 4, Short.MAX_VALUE))
        );

        btnDetailReturnCard.setText(bundle.getString("btnDetailReturnCard")); // NOI18N
        btnDetailReturnCard.setkEndColor(new java.awt.Color(102, 153, 255));
        btnDetailReturnCard.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnDetailReturnCard.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnDetailReturnCard.setkHoverStartColor(new java.awt.Color(153, 255, 153));
        btnDetailReturnCard.setkIndicatorThickness(255);
        btnDetailReturnCard.setkStartColor(new java.awt.Color(102, 153, 255));

        btnReturnProduct2.setText(bundle.getString("btnReturnProduct")); // NOI18N
        btnReturnProduct2.setkEndColor(new java.awt.Color(102, 153, 255));
        btnReturnProduct2.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnReturnProduct2.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnReturnProduct2.setkHoverStartColor(new java.awt.Color(153, 255, 153));
        btnReturnProduct2.setkStartColor(new java.awt.Color(102, 153, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnDetailReturnCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnReturnProduct2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 410, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(43, 43, 43))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnReturnProduct2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDetailReturnCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1110, 630));
    }// </editor-fold>//GEN-END:initComponents

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
    private com.k33ptoo.components.KButton btnReturnInformationLookup;
    private com.k33ptoo.components.KButton btnReturnProduct2;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
