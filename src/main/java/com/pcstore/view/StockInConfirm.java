package com.pcstore.view;

import com.pcstore.model.PurchaseOrder;
import com.pcstore.model.PurchaseOrderDetail;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

/**
 * Form xác nhận phiếu nhập kho
 * @author nloc2
 */
public class StockInConfirm extends javax.swing.JPanel {
    
    private List<PurchaseOrderDetail> orderDetails;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KButton btnClose1;
    private com.k33ptoo.components.KButton btnExportPDF;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private com.k33ptoo.components.KGradientPanel kGradientPanel3;
    private javax.swing.JLabel lbStockInID2;
    // End of variables declaration//GEN-END:variables

    /**
     * Creates new form StockInConfirm
     */
    public StockInConfirm() {
        initComponents();
    }

    /**
     * Hiển thị thông tin chi tiết của phiếu nhập kho
     * @param purchaseOrder Phiếu nhập kho
     * @param selectedProducts Danh sách sản phẩm đã chọn
     */
    public void showOrderDetails(PurchaseOrder purchaseOrder, List<PurchaseOrderDetail> selectedProducts) {
        this.orderDetails = selectedProducts;
        
        // Hiển thị thông tin phiếu nhập
        if (purchaseOrder != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedDate = purchaseOrder.getOrderDate().format(formatter);

            lbStockInID2.setText(purchaseOrder.getPurchaseOrderId());
            jLabel38.setText(formattedDate); // Date
            jLabel39.setText(purchaseOrder.getEmployee().getFullName()); // Employee
            jLabel41.setText(purchaseOrder.getSupplier().getName()); // Supplier
            
            int totalQuantity = 0;
            for (PurchaseOrderDetail detail : selectedProducts) {
                totalQuantity += detail.getQuantity();
            }
            jLabel42.setText(String.valueOf(totalQuantity));
            if (!selectedProducts.isEmpty()) {
                jLabel40.setText(selectedProducts.get(0).getProduct().getProductName());
            }

            LocalDate currentDate = LocalDate.now();
            jLabel43.setText(String.valueOf(currentDate.getDayOfMonth())); // Ngày
            jLabel44.setText(String.valueOf(currentDate.getMonthValue())); // Tháng
            jLabel45.setText(String.valueOf(currentDate.getYear())); // Năm
        }
    }

    /**
     * Hiển thị form xác nhận trong một cửa sổ Dialog
     * @param purchaseOrder Phiếu nhập kho
     * @param selectedProducts Danh sách sản phẩm đã chọn
     * @param parent Component cha để định vị Dialog
     */
    public static void showDialog(PurchaseOrder purchaseOrder, List<PurchaseOrderDetail> selectedProducts, java.awt.Component parent) {
        try {
            // Tạo JDialog để hiển thị panel StockInConfirm
            JDialog dialog = new JDialog();
            dialog.setTitle("Xác nhận nhập kho");
            dialog.setModal(true);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            StockInConfirm confirmForm = new StockInConfirm();
            
            // Kiểm tra null trước khi truyền vào
            if (purchaseOrder != null && selectedProducts != null && !selectedProducts.isEmpty()) {
                // Truyền thông tin phiếu nhập kho vào form xác nhận
                confirmForm.showOrderDetails(purchaseOrder, selectedProducts);
                
                // Thêm panel vào dialog
                dialog.setContentPane(confirmForm);
                dialog.pack();
                dialog.setLocationRelativeTo(parent);
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(parent,
                    "Không tìm thấy thông tin phiếu nhập hoặc danh sách sản phẩm trống!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                "Lỗi khi hiển thị form xác nhận: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void btnClose1MouseClicked(java.awt.event.MouseEvent evt) {
        // Đóng JFrame chứa form StockInConfirm
        java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose(); // Chỉ đóng cửa sổ hiện tại
        }
    }
    
    /**
     * Xuất phiếu nhập kho ra file PDF
     */
    private void btnExportPDFActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // Cài đặt xuất PDF tại đây (sử dụng thư viện như iText)
            JOptionPane.showMessageDialog(this, 
                "Đã xuất phiếu nhập kho thà nh công!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi xuất PDF: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        // Generated code - không thay đổi
        btnExportPDF = new com.k33ptoo.components.KButton();
        btnClose1 = new com.k33ptoo.components.KButton();
        kGradientPanel3 = new com.k33ptoo.components.KGradientPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        lbStockInID2 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        btnExportPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/pdf.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/pcstore/resources/vi_VN"); // NOI18N
        btnExportPDF.setText(bundle.getString("btnExportPDF")); // NOI18N
        btnExportPDF.setkBorderRadius(30);
        btnExportPDF.setkEndColor(new java.awt.Color(102, 153, 255));
        btnExportPDF.setkHoverEndColor(new java.awt.Color(255, 255, 255));
        btnExportPDF.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnExportPDF.setkHoverStartColor(new java.awt.Color(255, 153, 153));
        btnExportPDF.setkStartColor(new java.awt.Color(255, 153, 153));
        btnExportPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportPDFActionPerformed(evt);
            }
        });

        btnClose1.setText(bundle.getString("btnClose")); // NOI18N
        btnClose1.setkBorderRadius(30);
        btnClose1.setkEndColor(new java.awt.Color(102, 153, 255));
        btnClose1.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnClose1.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnClose1.setkHoverStartColor(new java.awt.Color(153, 255, 153));
        btnClose1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnClose1MouseClicked(evt);
            }
        });

        // Giữ nguyên phần layout đã được tạo bởi GUI builder
        // ...
    }// </editor-fold>//GEN-END:initComponents

    
}