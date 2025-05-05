/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.pcstore.view;

import javax.swing.JPasswordField;

/**
 *
 * @author MSII
 */
public class DialogChangePassword extends javax.swing.JDialog {

    private com.k33ptoo.components.KButton btnClose;
    private com.k33ptoo.components.KButton btnConfirm;
    private javax.swing.JCheckBox checkBoxShow;
    private com.k33ptoo.components.KGradientPanel kGradientPanel1;
    private com.k33ptoo.components.KGradientPanel kGradientPanel2;
    private javax.swing.JLabel lbConfrimPassword;
    private javax.swing.JLabel lbNewPassword;
    private javax.swing.JLabel lbOldPassword;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JLabel lbUsername;
    private javax.swing.JPanel panelAction;
    private javax.swing.JPanel panelBody;
    private javax.swing.JPanel panelConfirmPassword;
    private javax.swing.JPanel panelEmpty;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelNewPassword;
    private javax.swing.JPanel panelOldPassword;
    private javax.swing.JPanel panelShowPassword;
    private javax.swing.JPanel panelUsername;
    private javax.swing.JTextField txtConfrimPassword;
    private javax.swing.JTextField txtNewPassword;
    private javax.swing.JTextField txtOldPassword;
    private javax.swing.JTextField txtUsername;

    public DialogChangePassword(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        // Thiết lập mật khẩu bị ẩn mặc định
        ((JPasswordField)txtOldPassword).setEchoChar('•');
        ((JPasswordField)txtNewPassword).setEchoChar('•');
        ((JPasswordField)txtConfrimPassword).setEchoChar('•');
        
        // Thêm sự kiện cho checkbox hiển thị mật khẩu
        setupShowPasswordEvents();
    }
    
    
    // Thiết lập sự kiện hiển thị/ẩn mật khẩu
     
    private void setupShowPasswordEvents() {
        checkBoxShow.addActionListener(e -> {
            boolean showPassword = checkBoxShow.isSelected();
            
            if (showPassword) {
                ((JPasswordField)txtOldPassword).setEchoChar((char) 0);
                ((JPasswordField)txtNewPassword).setEchoChar((char) 0);
                ((JPasswordField)txtConfrimPassword).setEchoChar((char) 0);
            } else {
                ((JPasswordField)txtOldPassword).setEchoChar('•');
                ((JPasswordField)txtNewPassword).setEchoChar('•');
                ((JPasswordField)txtConfrimPassword).setEchoChar('•');
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        kGradientPanel2 = new com.k33ptoo.components.KGradientPanel();
        kGradientPanel1 = new com.k33ptoo.components.KGradientPanel();
        panelHeader = new javax.swing.JPanel();
        lbTitle = new javax.swing.JLabel();
        panelEmpty = new javax.swing.JPanel();
        panelBody = new javax.swing.JPanel();
        panelUsername = new javax.swing.JPanel();
        lbUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        panelOldPassword = new javax.swing.JPanel();
        lbOldPassword = new javax.swing.JLabel();
        txtOldPassword = new javax.swing.JPasswordField();
        panelNewPassword = new javax.swing.JPanel();
        lbNewPassword = new javax.swing.JLabel();
        txtNewPassword = new javax.swing.JPasswordField();
        panelConfirmPassword = new javax.swing.JPanel();
        lbConfrimPassword = new javax.swing.JLabel();
        txtConfrimPassword = new javax.swing.JPasswordField();
        panelShowPassword = new javax.swing.JPanel();
        checkBoxShow = new javax.swing.JCheckBox();
        panelAction = new javax.swing.JPanel();
        btnConfirm = new com.k33ptoo.components.KButton();
        btnClose = new com.k33ptoo.components.KButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/pcstore/resources/vi_VN"); // NOI18N
        setTitle(bundle.getString("titleChangePassword")); // NOI18N
        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(400, 500));

        kGradientPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        kGradientPanel2.setkAlpha(0.6F);
        kGradientPanel2.setkStartColor(new java.awt.Color(102, 153, 255));
        kGradientPanel2.setMinimumSize(new java.awt.Dimension(350, 500));
        kGradientPanel2.setPreferredSize(new java.awt.Dimension(350, 500));
        kGradientPanel2.setLayout(new java.awt.BorderLayout());

        kGradientPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 30, 10, 30));
        kGradientPanel1.setkBorderColor(new java.awt.Color(51, 255, 0));
        kGradientPanel1.setkBorderRadius(40);
        kGradientPanel1.setkEndColor(new java.awt.Color(255, 255, 255));
        kGradientPanel1.setkStartColor(new java.awt.Color(255, 255, 255));
        kGradientPanel1.setOpaque(false);
        kGradientPanel1.setPreferredSize(new java.awt.Dimension(400, 450));
        kGradientPanel1.setLayout(new javax.swing.BoxLayout(kGradientPanel1, javax.swing.BoxLayout.Y_AXIS));

        panelHeader.setBackground(new java.awt.Color(255, 255, 255));
        panelHeader.setOpaque(false);
        panelHeader.setPreferredSize(new java.awt.Dimension(322, 40));
        panelHeader.setLayout(new java.awt.BorderLayout());

        lbTitle.setBackground(new java.awt.Color(255, 255, 255));
        lbTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(30, 113, 195));
        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTitle.setText(bundle.getString("titleChangePassword")); // NOI18N
        lbTitle.setOpaque(true);
        panelHeader.add(lbTitle, java.awt.BorderLayout.CENTER);

        kGradientPanel1.add(panelHeader);

        panelEmpty.setBackground(new java.awt.Color(255, 255, 255));
        panelEmpty.setPreferredSize(new java.awt.Dimension(322, 20));

        javax.swing.GroupLayout panelEmptyLayout = new javax.swing.GroupLayout(panelEmpty);
        panelEmpty.setLayout(panelEmptyLayout);
        panelEmptyLayout.setHorizontalGroup(
            panelEmptyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 299, Short.MAX_VALUE)
        );
        panelEmptyLayout.setVerticalGroup(
            panelEmptyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        kGradientPanel1.add(panelEmpty);

        panelBody.setBackground(new java.awt.Color(255, 255, 255));
        panelBody.setPreferredSize(new java.awt.Dimension(322, 300));
        panelBody.setLayout(new java.awt.GridLayout(5, 1, 0, 10));

        panelUsername.setBackground(new java.awt.Color(255, 255, 255));
        panelUsername.setMinimumSize(new java.awt.Dimension(100, 40));
        panelUsername.setPreferredSize(new java.awt.Dimension(100, 40));
        panelUsername.setLayout(new java.awt.BorderLayout(10, 0));

        lbUsername.setBackground(new java.awt.Color(255, 255, 255));
        lbUsername.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lbUsername.setForeground(new java.awt.Color(102, 102, 102));
        lbUsername.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbUsername.setText(bundle.getString("lbUsername")); // NOI18N
        lbUsername.setOpaque(true);
        panelUsername.add(lbUsername, java.awt.BorderLayout.PAGE_START);

        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtUsername.setForeground(new java.awt.Color(30, 113, 195));
        txtUsername.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(204, 204, 204)));
        txtUsername.setEnabled(false);
        txtUsername.setPreferredSize(new java.awt.Dimension(200, 22));
        panelUsername.add(txtUsername, java.awt.BorderLayout.CENTER);

        panelBody.add(panelUsername);

        panelOldPassword.setBackground(new java.awt.Color(255, 255, 255));
        panelOldPassword.setMinimumSize(new java.awt.Dimension(100, 40));
        panelOldPassword.setPreferredSize(new java.awt.Dimension(100, 40));
        panelOldPassword.setLayout(new java.awt.BorderLayout(10, 0));

        lbOldPassword.setBackground(new java.awt.Color(255, 255, 255));
        lbOldPassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lbOldPassword.setForeground(new java.awt.Color(102, 102, 102));
        lbOldPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbOldPassword.setText(bundle.getString("lbOldPassword")); // NOI18N
        lbOldPassword.setOpaque(true);
        panelOldPassword.add(lbOldPassword, java.awt.BorderLayout.PAGE_START);

        txtOldPassword.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtOldPassword.setForeground(new java.awt.Color(30, 113, 195));
        txtOldPassword.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(204, 204, 204)));
        txtOldPassword.setPreferredSize(new java.awt.Dimension(200, 22));
        panelOldPassword.add(txtOldPassword, java.awt.BorderLayout.CENTER);

        panelBody.add(panelOldPassword);

        panelNewPassword.setBackground(new java.awt.Color(255, 255, 255));
        panelNewPassword.setMinimumSize(new java.awt.Dimension(100, 40));
        panelNewPassword.setPreferredSize(new java.awt.Dimension(100, 40));
        panelNewPassword.setLayout(new java.awt.BorderLayout(10, 0));

        lbNewPassword.setBackground(new java.awt.Color(255, 255, 255));
        lbNewPassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lbNewPassword.setForeground(new java.awt.Color(102, 102, 102));
        lbNewPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbNewPassword.setText(bundle.getString("lbNewPassword")); // NOI18N
        lbNewPassword.setOpaque(true);
        panelNewPassword.add(lbNewPassword, java.awt.BorderLayout.PAGE_START);

        txtNewPassword.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNewPassword.setForeground(new java.awt.Color(30, 113, 195));
        txtNewPassword.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(204, 204, 204)));
        txtNewPassword.setPreferredSize(new java.awt.Dimension(200, 22));
        panelNewPassword.add(txtNewPassword, java.awt.BorderLayout.CENTER);

        panelBody.add(panelNewPassword);

        panelConfirmPassword.setBackground(new java.awt.Color(255, 255, 255));
        panelConfirmPassword.setMinimumSize(new java.awt.Dimension(100, 40));
        panelConfirmPassword.setPreferredSize(new java.awt.Dimension(100, 40));
        panelConfirmPassword.setLayout(new java.awt.BorderLayout(10, 0));

        lbConfrimPassword.setBackground(new java.awt.Color(255, 255, 255));
        lbConfrimPassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lbConfrimPassword.setForeground(new java.awt.Color(102, 102, 102));
        lbConfrimPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbConfrimPassword.setText(bundle.getString("lbConfrimPassword")); // NOI18N
        lbConfrimPassword.setOpaque(true);
        panelConfirmPassword.add(lbConfrimPassword, java.awt.BorderLayout.PAGE_START);

        txtConfrimPassword.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtConfrimPassword.setForeground(new java.awt.Color(30, 113, 195));
        txtConfrimPassword.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(204, 204, 204)));
        txtConfrimPassword.setPreferredSize(new java.awt.Dimension(200, 22));
        panelConfirmPassword.add(txtConfrimPassword, java.awt.BorderLayout.CENTER);

        panelBody.add(panelConfirmPassword);

        panelShowPassword.setBackground(new java.awt.Color(255, 255, 255));
        panelShowPassword.setLayout(new java.awt.BorderLayout(10, 0));

        checkBoxShow.setBackground(new java.awt.Color(255, 255, 255));
        checkBoxShow.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        checkBoxShow.setForeground(new java.awt.Color(30, 113, 195));
        checkBoxShow.setText(bundle.getString("txtShowPassword")); // NOI18N
        panelShowPassword.add(checkBoxShow, java.awt.BorderLayout.LINE_END);

        panelBody.add(panelShowPassword);

        kGradientPanel1.add(panelBody);

        panelAction.setOpaque(false);
        panelAction.setPreferredSize(new java.awt.Dimension(100, 45));

        btnConfirm.setText(bundle.getString("btnConfirm")); // NOI18N
        btnConfirm.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnConfirm.setkAllowGradient(false);
        btnConfirm.setkBackGroundColor(new java.awt.Color(0, 189, 136));
        btnConfirm.setkHoverColor(new java.awt.Color(0, 196, 157));
        btnConfirm.setPreferredSize(new java.awt.Dimension(150, 35));
        panelAction.add(btnConfirm);

        btnClose.setText(bundle.getString("btnClose")); // NOI18N
        btnClose.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnClose.setkAllowGradient(false);
        btnClose.setkBackGroundColor(new java.awt.Color(255, 5, 32));
        btnClose.setkHoverColor(new java.awt.Color(255, 85, 86));
        btnClose.setPreferredSize(new java.awt.Dimension(70, 35));
        panelAction.add(btnClose);

        kGradientPanel1.add(panelAction);

        kGradientPanel2.add(kGradientPanel1, java.awt.BorderLayout.CENTER);

        getContentPane().add(kGradientPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }

    
    
    public com.k33ptoo.components.KButton getBtnConfirm() {
        return btnConfirm;
    }
    
    public com.k33ptoo.components.KButton getBtnClose() {
        return btnClose;
    }
    
    public javax.swing.JTextField getTxtUsername() {
        return txtUsername;
    }
    
    public javax.swing.JPasswordField getTxtOldPassword() {
        return (JPasswordField) txtOldPassword;
    }
    
    public javax.swing.JPasswordField getTxtNewPassword() {
        return (JPasswordField) txtNewPassword;
    }
    
    public javax.swing.JPasswordField getTxtConfrimPassword() {
        return (JPasswordField) txtConfrimPassword;
    }
    
    public javax.swing.JCheckBox getCheckBoxShow() {
        return checkBoxShow;
    }
    
}
