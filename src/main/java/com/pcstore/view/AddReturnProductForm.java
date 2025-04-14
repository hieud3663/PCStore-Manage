package com.pcstore.view;

import com.pcstore.controller.ReturnController;
import com.pcstore.controller.InvoiceController;
import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Return;
import com.pcstore.service.ServiceFactory;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author DUC ANH
 */
public class AddReturnProductForm extends javax.swing.JPanel {

    private ReturnController returnController;
    private InvoiceController invoiceController;
    private DefaultTableModel invoiceTableModel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private ReturnServiceForm parentForm;

    /**
     * Creates new form AddReapairProduct
     */
    public AddReturnProductForm() {
        initComponents();
        initControllers();
        setupTable();
    }

    /**
     * Constructor để truyền form cha vào để cập nhật dữ liệu
     */
    public AddReturnProductForm(ReturnServiceForm parent) {
        this();
        this.parentForm = parent;
    }

    private void initControllers() {
        try {
            // Khởi tạo các controller cần thiết
            returnController = new ReturnController(
                ServiceFactory.getInstance().getConnection(),
                ServiceFactory.getInvoiceService(),
                ServiceFactory.getProductService()
            );
            
            invoiceController = new InvoiceController(
                ServiceFactory.getInstance().getConnection()
            );
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Không thể kết nối đến cơ sở dữ liệu: " + ex.getMessage(),
                "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setupTable() {
        // Thiết lập table model cho bảng hiển thị sản phẩm
        invoiceTableModel = (DefaultTableModel) jTable2.getModel();
        invoiceTableModel.setRowCount(0);
        
        // Thiết lập các tiêu đề cột
        String[] columnNames = {
            "Mã Sản Phẩm", "Tên Sản Phẩm", "Đơn Giá", "Số Lượng", "Ngày Mua", "Tên Khách Hàng", "SĐT"
        };
        
        invoiceTableModel.setColumnIdentifiers(columnNames);
    }
    
    /**
     * Tìm kiếm hóa đơn theo số điện thoại khách hàng
     */
    private void searchByPhoneNumber() {
        String phoneNumber = jTextField1.getText().trim();
        if (phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập số điện thoại khách hàng để tìm kiếm", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            // Tìm hóa đơn theo SĐT khách hàng
            List<Invoice> invoices = invoiceController.getInvoicesByCustomerPhone(phoneNumber);
            
            if (invoices.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy hóa đơn nào cho khách hàng với số điện thoại: " + phoneNumber, 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                invoiceTableModel.setRowCount(0);
                return;
            }
            
            // Hiển thị thông tin các sản phẩm trong hóa đơn
            displayInvoiceDetails(invoices);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tìm kiếm hóa đơn: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Hiển thị chi tiết các hóa đơn
     */
    private void displayInvoiceDetails(List<Invoice> invoices) {
        invoiceTableModel.setRowCount(0);
        
        for (Invoice invoice : invoices) {
            try {
                List<InvoiceDetail> details = invoiceController.getInvoiceDetails(invoice.getInvoiceId());
                
                for (InvoiceDetail detail : details) {
                    try {
                        // Kiểm tra xem sản phẩm này đã được trả hết chưa
                        int remainingQuantity = detail.getQuantity();
                        
                        // Tìm các đơn trả hàng hiện có cho sản phẩm này
                        List<Return> existingReturns = returnController.getReturnsByInvoiceDetail(
                            detail.getInvoiceDetailId());
                        
                        for (Return returnObj : existingReturns) {
                            // Chỉ tính những đơn ở trạng thái Approved hoặc Completed
                            if ("Approved".equals(returnObj.getStatus()) || 
                                "Completed".equals(returnObj.getStatus())) {
                                remainingQuantity -= returnObj.getQuantity();
                            }
                        }
                        
                        // Nếu còn số lượng có thể trả, hiển thị
                        if (remainingQuantity > 0) {
                            Object[] rowData = {
                                detail.getInvoiceDetailId(), // ID chi tiết hóa đơn ở cột đầu tiên (ẩn)
                                detail.getProduct().getProductId(),
                                detail.getProduct().getProductName(),
                                detail.getUnitPrice(),
                                remainingQuantity,  // Chỉ hiển thị số lượng còn có thể trả
                                invoice.getInvoiceDate().format(dateFormatter),
                                invoice.getCustomer() != null ? invoice.getCustomer().getFullName() : "Khách lẻ",
                                invoice.getCustomer() != null ? invoice.getCustomer().getPhoneNumber() : ""
                            };
                            invoiceTableModel.addRow(rowData);
                        }
                    } catch (Exception e) {
                        System.err.println("Lỗi khi tính số lượng có thể trả: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi lấy chi tiết hóa đơn: " + e.getMessage());
            }
        }
        
        // Cập nhật các tiêu đề cột
        if (jTable2.getColumnCount() >= 8) {
            invoiceTableModel.setColumnIdentifiers(new String[] {
                "ID Chi tiết", "Mã Sản Phẩm", "Tên Sản Phẩm", "Đơn Giá", 
                "Số Lượng Có Thể Trả", "Ngày Mua", "Tên Khách Hàng", "SĐT"
            });
            
            // Ẩn cột ID chi tiết hóa đơn
            jTable2.getColumnModel().getColumn(0).setMinWidth(0);
            jTable2.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable2.getColumnModel().getColumn(0).setWidth(0);
        }
    }
    
    /**
     * Xử lý chức năng trả hàng
     */
    private void createReturn() {
        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một sản phẩm để trả", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            // Lấy thông tin từ dòng được chọn
            Integer invoiceDetailId = (Integer) invoiceTableModel.getValueAt(selectedRow, 0);
            String productId = invoiceTableModel.getValueAt(selectedRow, 1).toString();
            String productName = invoiceTableModel.getValueAt(selectedRow, 2).toString();
            double unitPrice = Double.parseDouble(invoiceTableModel.getValueAt(selectedRow, 3).toString());
            int availableQuantity = Integer.parseInt(invoiceTableModel.getValueAt(selectedRow, 4).toString());
            
            // Hiển thị dialog để nhập thông tin trả hàng
            String input = JOptionPane.showInputDialog(this, 
                "Nhập số lượng sản phẩm muốn trả (tối đa " + availableQuantity + "):", 
                "1");
            
            if (input == null || input.trim().isEmpty()) {
                return; // Người dùng đã hủy
            }
            
            int returnQuantity = Integer.parseInt(input);
            
            if (returnQuantity <= 0 || returnQuantity > availableQuantity) {
                JOptionPane.showMessageDialog(this, 
                    "Số lượng không hợp lệ. Vui lòng nhập số từ 1 đến " + availableQuantity, 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Hiển thị dialog nhập lý do trả hàng
            String reason = JOptionPane.showInputDialog(this, 
                "Nhập lý do trả hàng:", 
                "Lý do trả hàng");
                
            if (reason == null || reason.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập lý do trả hàng", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Tạo đơn trả hàng
            Return returnObj = returnController.createReturn(
                invoiceDetailId, 
                returnQuantity, 
                reason, 
                null // Không có ghi chú bổ sung
            );
            
            if (returnObj != null) {
                // Hiển thị thông báo thành công với mã đơn trả
                JOptionPane.showMessageDialog(this, 
                    "Đã tạo đơn trả hàng thành công!\nMã đơn: " + returnObj.getReturnId(), 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // Nếu có form cha, cập nhật dữ liệu ở form đó
                if (parentForm != null) {
                    parentForm.loadAllReturns();
                }
                
                // Đóng form sau khi tạo đơn thành công
                if (getParent() instanceof javax.swing.JDialog) {
                    ((javax.swing.JDialog) getParent()).dispose();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Số lượng không hợp lệ. Vui lòng nhập một số nguyên.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tạo đơn trả hàng: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Thêm xử lý sự kiện cho các nút trong form
    private void addListeners() {
        btnReturnInformationLookup.addActionListener(e -> searchByPhoneNumber());
        btnWarranty.addActionListener(e -> createReturn());
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        kGradientPanel3 = new com.k33ptoo.components.KGradientPanel();
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        btnReturnInformationLookup = new com.k33ptoo.components.KButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        btnWarranty = new com.k33ptoo.components.KButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Sản Phẩm", "Tên Sản Phẩm", "Hãng Sản Xuất", "Ngày Mua", "Hạn Bảo Hành", "Tên Khách Hàng", "Số Điện Thoại"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/pcstore/resources/vi_VN"); // NOI18N
        kGradientPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("ReTurnService"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N
        kGradientPanel3.setkFillBackground(false);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Tìm Kiếm SĐT Khách Hàng\n"));

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

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Sản Phẩm", "Tên Sản Phẩm", "Hãng Sản Xuất", "Ngày Mua", "Tên Khách Hàng", "Số Điện Thoại", "Trạng Thái"
            }
        ));
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("cl1ProductCode")); // NOI18N
            jTable2.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("clNameProduct")); // NOI18N
            jTable2.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("clManufacturer")); // NOI18N
            jTable2.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("clDateOfPurchase")); // NOI18N
            jTable2.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("clNameCustomer")); // NOI18N
            jTable2.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("clStatus")); // NOI18N
        }

        btnWarranty.setText(bundle.getString("btnReturn")); // NOI18N
        btnWarranty.setkBackGroundColor(new java.awt.Color(102, 153, 255));
        btnWarranty.setkEndColor(new java.awt.Color(102, 153, 255));
        btnWarranty.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnWarranty.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnWarranty.setkHoverStartColor(new java.awt.Color(153, 255, 153));
        btnWarranty.setkStartColor(new java.awt.Color(102, 153, 255));
        btnWarranty.setMargin(new java.awt.Insets(2, 14, 0, 14));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnWarranty, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 886, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnWarranty, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout kGradientPanel3Layout = new javax.swing.GroupLayout(kGradientPanel3);
        kGradientPanel3.setLayout(kGradientPanel3Layout);
        kGradientPanel3Layout.setHorizontalGroup(
            kGradientPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(kGradientPanel3Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 496, Short.MAX_VALUE))
        );
        kGradientPanel3Layout.setVerticalGroup(
            kGradientPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(kGradientPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        searchByPhoneNumber();
    }//GEN-LAST:event_jTextField1ActionPerformed

    @Override
    public void addNotify() {
        super.addNotify();
        // Đảm bảo listeners được thêm sau khi form đã được khởi tạo đầy đủ
        addListeners();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KButton btnReturnInformationLookup;
    private com.k33ptoo.components.KButton btnWarranty;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private com.k33ptoo.components.KGradientPanel kGradientPanel3;
    // End of variables declaration//GEN-END:variables
}
