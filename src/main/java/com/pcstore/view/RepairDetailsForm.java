/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import com.pcstore.utils.ErrorMessage;

/**
 *
 * @author DUC ANH
 */
public class RepairDetailsForm extends javax.swing.JPanel {


     // Variables declaration - do not modify//GEN-BEGIN:variables
     private javax.swing.JLabel lbCost;
     private javax.swing.JLabel lbDeviceName;
     private javax.swing.JLabel lbDisplayNameCustomer;
     private javax.swing.JLabel lbEmailCustomer;
     private javax.swing.JLabel lbIDCustomer;
     private javax.swing.JLabel lbNote;
     private javax.swing.JLabel lbPhonenumber1;
     private javax.swing.JLabel lbPosition1;
     private javax.swing.JLabel lbStatus;
     private javax.swing.JPanel paneIDCustomer;
     private javax.swing.JPanel panelCost;
     private javax.swing.JPanel panelDeviceName;
     private javax.swing.JPanel panelEmailCustome;
     private com.k33ptoo.components.KGradientPanel panelMain;
     private javax.swing.JPanel panelNameCustomer;
     private javax.swing.JPanel panelNote;
     private javax.swing.JPanel panelPhonenumberCustomer;
     private javax.swing.JPanel panelRepairProblem;
     private javax.swing.JPanel panelStatus;
     private javax.swing.JTextField txtCost;
     private javax.swing.JTextField txtDeviceName;
     private javax.swing.JTextField txtEmailCustomer;
     private javax.swing.JTextField txtIDCustomer;
     private javax.swing.JTextField txtNameCustomer;
     private javax.swing.JTextField txtNote;
     private javax.swing.JTextField txtPhonenumberEmployee1;
     private javax.swing.JTextField txtRepairProblem;
     private javax.swing.JTextField txtStatus;
     // End of variables declaration//GEN-END:variables
    /**
     * Creates new form RepairDetailsForm
     */
    public RepairDetailsForm() {
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
        paneIDCustomer = new javax.swing.JPanel();
        lbIDCustomer = new javax.swing.JLabel();
        txtIDCustomer = new javax.swing.JTextField();
        panelNameCustomer = new javax.swing.JPanel();
        lbDisplayNameCustomer = new javax.swing.JLabel();
        txtNameCustomer = new javax.swing.JTextField();
        panelPhonenumberCustomer = new javax.swing.JPanel();
        lbPhonenumber1 = new javax.swing.JLabel();
        txtPhonenumberEmployee1 = new javax.swing.JTextField();
        panelEmailCustome = new javax.swing.JPanel();
        lbEmailCustomer = new javax.swing.JLabel();
        txtEmailCustomer = new javax.swing.JTextField();
        panelDeviceName = new javax.swing.JPanel();
        txtDeviceName = new javax.swing.JTextField();
        lbPosition1 = new javax.swing.JLabel();
        panelRepairProblem = new javax.swing.JPanel();
        lbDeviceName = new javax.swing.JLabel();
        txtRepairProblem = new javax.swing.JTextField();
        panelStatus = new javax.swing.JPanel();
        lbStatus = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        panelNote = new javax.swing.JPanel();
        lbNote = new javax.swing.JLabel();
        txtNote = new javax.swing.JTextField();
        panelCost = new javax.swing.JPanel();
        lbCost = new javax.swing.JLabel();
        txtCost = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        panelMain.setBackground(new java.awt.Color(255, 255, 255));
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        panelMain.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("txtDetail"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 24))); // NOI18N
        panelMain.setkBorderRadius(15);
        panelMain.setkEndColor(new java.awt.Color(0, 0, 0));
        panelMain.setkFillBackground(false);
        panelMain.setkStartColor(new java.awt.Color(0, 0, 0));
        panelMain.setPreferredSize(new java.awt.Dimension(400, 420));
        panelMain.setLayout(new java.awt.GridLayout(6, 2, 50, 15));

        paneIDCustomer.setOpaque(false);
        paneIDCustomer.setLayout(new java.awt.GridLayout(2, 0));

        lbIDCustomer.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbIDCustomer.setText(bundle.getString("lbIDCustomer")); // NOI18N
        paneIDCustomer.add(lbIDCustomer);

        txtIDCustomer.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtIDCustomer.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(102, 102, 102)));
        txtIDCustomer.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        txtIDCustomer.setEnabled(false);
        paneIDCustomer.add(txtIDCustomer);

        panelMain.add(paneIDCustomer);

        panelNameCustomer.setOpaque(false);
        panelNameCustomer.setLayout(new java.awt.BorderLayout(0, 1));

        lbDisplayNameCustomer.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbDisplayNameCustomer.setText(bundle.getString("lbDisplayNameCustomer")); // NOI18N
        panelNameCustomer.add(lbDisplayNameCustomer, java.awt.BorderLayout.CENTER);

        txtNameCustomer.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNameCustomer.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(102, 102, 102)));
        panelNameCustomer.add(txtNameCustomer, java.awt.BorderLayout.PAGE_END);

        panelMain.add(panelNameCustomer);

        panelPhonenumberCustomer.setOpaque(false);
        panelPhonenumberCustomer.setPreferredSize(new java.awt.Dimension(64, 35));
        panelPhonenumberCustomer.setLayout(new java.awt.BorderLayout(0, 1));

        lbPhonenumber1.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbPhonenumber1.setText(bundle.getString("lbPhoneNumberCustomer")); // NOI18N
        panelPhonenumberCustomer.add(lbPhonenumber1, java.awt.BorderLayout.PAGE_START);

        txtPhonenumberEmployee1.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtPhonenumberEmployee1.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(102, 102, 102)));
        panelPhonenumberCustomer.add(txtPhonenumberEmployee1, java.awt.BorderLayout.CENTER);

        panelMain.add(panelPhonenumberCustomer);

        panelEmailCustome.setOpaque(false);
        panelEmailCustome.setLayout(new java.awt.BorderLayout(0, 1));

        lbEmailCustomer.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbEmailCustomer.setText(bundle.getString("lbEmailCustomer")); // NOI18N
        panelEmailCustome.add(lbEmailCustomer, java.awt.BorderLayout.PAGE_START);

        txtEmailCustomer.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtEmailCustomer.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(102, 102, 102)));
        panelEmailCustome.add(txtEmailCustomer, java.awt.BorderLayout.CENTER);

        panelMain.add(panelEmailCustome);

        panelDeviceName.setOpaque(false);
        panelDeviceName.setLayout(new java.awt.BorderLayout(0, 1));

        txtDeviceName.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtDeviceName.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(102, 102, 102)));
        panelDeviceName.add(txtDeviceName, java.awt.BorderLayout.PAGE_END);

        lbPosition1.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbPosition1.setText(bundle.getString("lbDisplayNameDevice")); // NOI18N
        panelDeviceName.add(lbPosition1, java.awt.BorderLayout.PAGE_START);

        panelMain.add(panelDeviceName);

        panelRepairProblem.setOpaque(false);
        panelRepairProblem.setLayout(new java.awt.BorderLayout(2, 1));

        lbDeviceName.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbDeviceName.setText(bundle.getString("lbDisplayRepairProblem")); // NOI18N
        panelRepairProblem.add(lbDeviceName, java.awt.BorderLayout.PAGE_START);

        txtRepairProblem.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtRepairProblem.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(102, 102, 102)));
        txtRepairProblem.setMinimumSize(new java.awt.Dimension(135, 23));
        txtRepairProblem.setName(""); // NOI18N
        txtRepairProblem.setPreferredSize(new java.awt.Dimension(135, 23));
        panelRepairProblem.add(txtRepairProblem, java.awt.BorderLayout.CENTER);

        panelMain.add(panelRepairProblem);

        panelStatus.setOpaque(false);
        panelStatus.setLayout(new java.awt.BorderLayout(0, 1));

        lbStatus.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbStatus.setText(bundle.getString("lbStatus")); // NOI18N
        panelStatus.add(lbStatus, java.awt.BorderLayout.CENTER);

        txtStatus.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtStatus.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(102, 102, 102)));
        panelStatus.add(txtStatus, java.awt.BorderLayout.PAGE_END);

        panelMain.add(panelStatus);

        panelNote.setOpaque(false);
        panelNote.setLayout(new java.awt.BorderLayout(0, 1));

        lbNote.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbNote.setText(bundle.getString("lbNote")); // NOI18N
        panelNote.add(lbNote, java.awt.BorderLayout.CENTER);

        txtNote.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNote.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(102, 102, 102)));
        panelNote.add(txtNote, java.awt.BorderLayout.PAGE_END);

        panelMain.add(panelNote);

        panelCost.setOpaque(false);
        panelCost.setLayout(new java.awt.GridLayout(2, 0));

        lbCost.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbCost.setText(bundle.getString("lbDisplayCost")); // NOI18N
        panelCost.add(lbCost);

        txtCost.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtCost.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(102, 102, 102)));
        txtCost.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        txtCost.setEnabled(false);
        panelCost.add(txtCost);

        panelMain.add(panelCost);

        add(panelMain, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Hiển thị chi tiết một dịch vụ sửa chữa
     * @param repair Đối tượng dịch vụ sửa chữa cần hiển thị
     */
    public void setRepairDetails(com.pcstore.model.Repair repair) {
        try {
            if (repair == null) {
                clearFields();
                return;
            }
            
            // Thông tin khách hàng
            if (repair.getCustomer() != null) {
                txtIDCustomer.setText(repair.getCustomer().getCustomerId());
                txtNameCustomer.setText(repair.getCustomer().getFullName());
                txtPhonenumberEmployee1.setText(repair.getCustomer().getPhoneNumber());
                
                // Email có thể null
                String email = repair.getCustomer().getEmail();
                txtEmailCustomer.setText(email != null ? email : "");
            } else {
                // Xóa thông tin khách hàng nếu không có
                txtIDCustomer.setText("");
                txtNameCustomer.setText("");
                txtPhonenumberEmployee1.setText("");
                txtEmailCustomer.setText("");
            }
            
            // Thông tin thiết bị
            txtDeviceName.setText(repair.getDeviceName());
            txtRepairProblem.setText(repair.getProblem());
            
            // Thông tin trạng thái
            String status = repair.getStatus() != null ? repair.getStatus().getStatus() : "N/A";
            txtStatus.setText(status);
            
            // Thông tin chi phí
            String cost = repair.getServiceFee() != null ? repair.getServiceFee().toString() : "0";
            txtCost.setText(cost);
            
            // Ghi chú
            txtNote.setText(repair.getNotes() != null ? repair.getNotes() : "");
            
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this,
                ErrorMessage.REPAIR_FORM_DETAIL_ERROR + ": " + e.getMessage(),
                "Lỗi",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xóa tất cả các trường dữ liệu
     */
    private void clearFields() {
        txtIDCustomer.setText("");
        txtNameCustomer.setText("");
        txtPhonenumberEmployee1.setText("");
        txtEmailCustomer.setText("");
        txtDeviceName.setText("");
        txtRepairProblem.setText("");
        txtStatus.setText("");
        txtCost.setText("");
        txtNote.setText("");
    }
    public void addCloseButton(javax.swing.JDialog parent) {
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        
        com.k33ptoo.components.KButton btnClose = new com.k33ptoo.components.KButton();
        btnClose.setText("Đóng");
        btnClose.setkBackGroundColor(new java.awt.Color(102, 153, 255));
        btnClose.setkEndColor(new java.awt.Color(102, 153, 255));
        btnClose.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnClose.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnClose.setkHoverStartColor(new java.awt.Color(153, 255, 153));
        btnClose.setkStartColor(new java.awt.Color(102, 153, 255));
        btnClose.addActionListener(e -> parent.dispose());
        
        buttonPanel.add(btnClose);
        
        // Thêm nút vào form
        this.add(buttonPanel, java.awt.BorderLayout.SOUTH);
    }
   
}
