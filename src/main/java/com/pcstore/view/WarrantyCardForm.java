/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.pcstore.model.Warranty;

import java.time.format.DateTimeFormatter;

/**
 *
 * @author DUC ANH
 */
public class WarrantyCardForm extends javax.swing.JPanel {

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Date;
    private javax.swing.JLabel NameCustomer;
    private javax.swing.JLabel SDT;
    private javax.swing.JScrollPane ScrollTable;
    private javax.swing.JTable TableWarranty;
    private com.k33ptoo.components.KButton btnPrintCard;
    private javax.swing.JLabel lbDate;
    private javax.swing.JLabel lbName;
    private javax.swing.JLabel lbSDT;
    private javax.swing.JLabel lbSign;
    private javax.swing.JLabel lbTitile;
    private javax.swing.JLabel lbWarrantyCode;
    private com.k33ptoo.components.KGradientPanel panelMain;
    private javax.swing.JPanel pnDate;
    private javax.swing.JPanel pnHeader;
    private javax.swing.JPanel pnInfo;
    private javax.swing.JPanel pnSign;
    private javax.swing.JTextField txtSign;
    private javax.swing.JLabel warrantycode;
    // End of variables declaration//GEN-END:variables

    /**
     * Creates new form WarrantyCard
     */
    public WarrantyCardForm() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMain = new com.k33ptoo.components.KGradientPanel();
        ScrollTable = new javax.swing.JScrollPane();
        TableWarranty = new javax.swing.JTable();
        btnPrintCard = new com.k33ptoo.components.KButton();
        pnHeader = new javax.swing.JPanel();
        lbTitile = new javax.swing.JLabel();
        pnDate = new javax.swing.JPanel();
        lbDate = new javax.swing.JLabel();
        Date = new javax.swing.JLabel();
        pnSign = new javax.swing.JPanel();
        lbSign = new javax.swing.JLabel();
        txtSign = new javax.swing.JTextField();
        pnInfo = new javax.swing.JPanel();
        lbName = new javax.swing.JLabel();
        NameCustomer = new javax.swing.JLabel();
        lbWarrantyCode = new javax.swing.JLabel();
        warrantycode = new javax.swing.JLabel();
        lbSDT = new javax.swing.JLabel();
        SDT = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.BorderLayout());

        panelMain.setBackground(new java.awt.Color(255, 255, 255));
        panelMain.setkEndColor(new java.awt.Color(255, 255, 255));
        panelMain.setkFillBackground(false);
        panelMain.setkStartColor(new java.awt.Color(255, 255, 255));
        panelMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        TableWarranty.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "STT", "Tên sản Phẩm ", "Thời Gian Bảo Hành"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        ScrollTable.setViewportView(TableWarranty);
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        if (TableWarranty.getColumnModel().getColumnCount() > 0) {
            TableWarranty.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("txtWarrantyCardNumericalOrder")); // NOI18N
            TableWarranty.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("txtWarrantyNameProduct")); // NOI18N
            TableWarranty.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("txtWarrantyWarrantyPeriod")); // NOI18N
        }

        panelMain.add(ScrollTable, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 230, 622, 257));

        btnPrintCard.setText(bundle.getString("btnDetailCard")); // NOI18N
        btnPrintCard.setkBorderRadius(30);
        btnPrintCard.setkEndColor(new java.awt.Color(102, 153, 255));
        btnPrintCard.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnPrintCard.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnPrintCard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnPrintCardMouseClicked(evt);
            }
        });
        panelMain.add(btnPrintCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 500, -1, -1));

        pnHeader.setBackground(new java.awt.Color(255, 255, 255));

        lbTitile.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        lbTitile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTitile.setText(bundle.getString("lbWarrantyCard")); // NOI18N
        lbTitile.setToolTipText("");
        pnHeader.add(lbTitile);

        panelMain.add(pnHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1050, -1));

        pnDate.setBackground(new java.awt.Color(255, 255, 255));

        lbDate.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbDate.setText(bundle.getString("lbDateOfPurchase")); // NOI18N

        javax.swing.GroupLayout pnDateLayout = new javax.swing.GroupLayout(pnDate);
        pnDate.setLayout(pnDateLayout);
        pnDateLayout.setHorizontalGroup(
            pnDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnDateLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(Date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(76, Short.MAX_VALUE))
        );
        pnDateLayout.setVerticalGroup(
            pnDateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnDateLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Date, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addGap(27, 27, 27))
        );

        panelMain.add(pnDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, 200, -1));

        pnSign.setBackground(new java.awt.Color(255, 255, 255));
        pnSign.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbSign.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbSign.setText(bundle.getString("lbConfirmationSignatureCustomer")); // NOI18N
        lbSign.setPreferredSize(new java.awt.Dimension(87, 35));
        pnSign.add(lbSign, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 110, -1));
        pnSign.add(txtSign, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 275, 200));

        panelMain.add(pnSign, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 310, 240));

        pnInfo.setBackground(new java.awt.Color(255, 255, 255));
        pnInfo.setLayout(new java.awt.GridLayout(3, 2, 30, 10));

        lbName.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbName.setText(bundle.getString("NameCustomer")); // NOI18N
        pnInfo.add(lbName);

        NameCustomer.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        NameCustomer.setForeground(new java.awt.Color(102, 100, 239));
        NameCustomer.setText("                                                                                                                             ");
        pnInfo.add(NameCustomer);

        lbWarrantyCode.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbWarrantyCode.setText(bundle.getString("lbWarrantyCode")); // NOI18N
        pnInfo.add(lbWarrantyCode);

        warrantycode.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        warrantycode.setForeground(new java.awt.Color(102, 100, 239));
        warrantycode.setText("                                                                                                                             ");
        pnInfo.add(warrantycode);

        lbSDT.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        lbSDT.setText(bundle.getString("lbSDT")); // NOI18N
        pnInfo.add(lbSDT);

        SDT.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        SDT.setForeground(new java.awt.Color(102, 100, 239));
        SDT.setText("                                                                                                                             ");
        pnInfo.add(SDT);

        panelMain.add(pnInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 120, 510, 90));

        add(panelMain, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Set the customer name in the warranty card
     * @param name Customer name
     */
    public void setCustomerName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            NameCustomer.setText(name);
        } else {
            NameCustomer.setText("N/A");
        }
    }

    /**
     * Thiết lập mã bảo hành trên phiếu
     * @param code Mã bảo hành
     */
    public void setWarrantyCode(String code) {
        if (code != null && !code.trim().isEmpty()) {
            warrantycode.setText(code);
        } else {
            warrantycode.setText("Không có");
        }
    }

    /**
     * Thiết lập số điện thoại khách hàng trên phiếu
     * @param phone Số điện thoại khách hàng
     */
    public void setCustomerPhone(String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            SDT.setText(phone);
        } else {
            SDT.setText("Không có");
        }
    }

    /**
     * Thiết lập ngày mua hàng trên phiếu
     * @param date Ngày mua hàng
     */
    public void setPurchaseDate(String date) {
        if (date != null && !date.trim().isEmpty()) {
            Date.setText(date);
        } else {
            Date.setText("Không có");
        }
    }

    /**
     * Get the table that displays product information
     * @return The product table
     */
    public JTable getProductTable() {
        return TableWarranty;
    }

    /**
     * Get the customer name label
     * @return Customer name label
     */
    public JLabel getNameCustomerLabel() {
        return NameCustomer;
    }

    /**
     * Get the warranty code label
     * @return Warranty code label
     */
    public JLabel getWarrantyCodeLabel() {
        return warrantycode;
    }

    /**
     * Get the phone number label
     * @return Phone number label
     */
    public JLabel getSdtLabel() {
        return SDT;
    }

    /**
     * Get the purchase date label
     * @return Purchase date label
     */
    public JLabel getPurchaseDateLabel() {
        return Date;
    }

    /**
     * Hiển thị đầy đủ thông tin bảo hành
     * @param warranty Đối tượng bảo hành chứa tất cả thông tin
     */
    public void displayWarrantyInfo(Warranty warranty) {
        if (warranty == null) {
            return;
        }

        // Định dạng ngày tháng
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // Thiết lập thông tin cơ bản
        setCustomerName(warranty.getCustomerName());
        setWarrantyCode(warranty.getWarrantyId());
        setCustomerPhone(warranty.getCustomerPhone());
        
        // Thiết lập ngày mua hàng
        if (warranty.getStartDate() != null) {
            setPurchaseDate(warranty.getStartDate().format(formatter));
        } else {
            setPurchaseDate("Không có");
        }
        
        // Cấu hình bảng sản phẩm
        DefaultTableModel model = (DefaultTableModel) TableWarranty.getModel();
        model.setRowCount(0); // Xóa các dòng hiện có
        
        // Tính thời gian bảo hành
        String warrantyPeriod = "Không có";
        if (warranty.getStartDate() != null && warranty.getEndDate() != null) {
            long months = java.time.temporal.ChronoUnit.MONTHS.between(
                warranty.getStartDate(), warranty.getEndDate());
            String endDate = warranty.getEndDate().format(formatter);
            warrantyPeriod = months + " tháng (đến " + endDate + ")";
        }
        
        // Thêm thông tin sản phẩm vào bảng
        model.addRow(new Object[] {
            1, // STT
            warranty.getProductName() != null ? warranty.getProductName() : "Không có",
            warranty.getInvoiceDetail() != null ? warranty.getInvoiceDetail().getQuantity() : 1,
            warrantyPeriod
        });
    }

    /**
     * Method for the print button click event
     * @param evt Mouse event
     */
    private void btnPrintCardMouseClicked(java.awt.event.MouseEvent evt) {
        // Implement print functionality
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        javax.swing.JOptionPane.showMessageDialog(
            parentFrame, 
            "Chức năng đang được triển khai...",
            "Thông báo", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
        
        // Here you would add actual printing functionality
    }

    

    public javax.swing.JTable getTableWarranty() {
        return TableWarranty;
    }


}
