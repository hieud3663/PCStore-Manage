package com.pcstore.view;

import javax.swing.JPasswordField;

import com.pcstore.controller.DialogForgotPasswordController;

public class DialogForgotPasswordForm extends javax.swing.JDialog {

    private com.k33ptoo.components.KButton btnClose;
    private com.k33ptoo.components.KButton btnConfirm;
    private javax.swing.JCheckBox checkShowPassword;
    private javax.swing.JLabel lbConfirm;
    private javax.swing.JLabel lbEmail;
    private javax.swing.JLabel lbNewPassword;
    private javax.swing.JLabel lbOTP;
    private javax.swing.JLabel lbSendOTP;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel panelAction;
    private com.k33ptoo.components.KGradientPanel panelBody;
    private javax.swing.JPanel panelConfrim;
    private com.k33ptoo.components.KGradientPanel panelContent;
    private javax.swing.JPanel panelEmail;
    private javax.swing.JPanel panelEmpty;
    private com.k33ptoo.components.KGradientPanel panelMain;
    private javax.swing.JPanel panelNewPassword;
    private javax.swing.JPanel panelOTP;
    private javax.swing.JPanel panelTitle;
    private javax.swing.JTextField txtConfirmPassword;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNewPassword;
    private javax.swing.JTextField txtOTP;

   
    private DialogForgotPasswordController dialogForgotPasswordController;
    
    public DialogForgotPasswordForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setLocationRelativeTo(parent);
        initComponents();
        dialogForgotPasswordController = new DialogForgotPasswordController(this);
    }

    public DialogForgotPasswordForm() {
        super();
        initComponents();
    }

 
    @SuppressWarnings("unchecked")
    private void initComponents() {

        panelBody = new com.k33ptoo.components.KGradientPanel();
        panelMain = new com.k33ptoo.components.KGradientPanel();
        panelTitle = new javax.swing.JPanel();
        lbTitle = new javax.swing.JLabel();
        panelContent = new com.k33ptoo.components.KGradientPanel();
        panelEmail = new javax.swing.JPanel();
        txtEmail = new javax.swing.JTextField();
        lbEmail = new javax.swing.JLabel();
        lbSendOTP = new javax.swing.JLabel();
        panelOTP = new javax.swing.JPanel();
        txtOTP = new javax.swing.JTextField();
        lbOTP = new javax.swing.JLabel();
        panelNewPassword = new javax.swing.JPanel();
        lbNewPassword = new javax.swing.JLabel();
        txtNewPassword = new javax.swing.JPasswordField();
        panelConfrim = new javax.swing.JPanel();
        lbConfirm = new javax.swing.JLabel();
        txtConfirmPassword = new javax.swing.JPasswordField();
        panelEmpty = new javax.swing.JPanel();
        checkShowPassword = new javax.swing.JCheckBox();
        panelAction = new javax.swing.JPanel();
        btnConfirm = new com.k33ptoo.components.KButton();
        btnClose = new com.k33ptoo.components.KButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        setTitle(bundle.getString("ForgotPassword")); // NOI18N
        setMinimumSize(new java.awt.Dimension(435, 443));
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setPreferredSize(new java.awt.Dimension(435, 500));
        setResizable(false);
        setType(java.awt.Window.Type.POPUP);

        panelBody.setBackground(new java.awt.Color(255, 255, 255));
        panelBody.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 40, 20, 40));
        panelBody.setkAlpha(0.6F);
        panelBody.setkEndColor(new java.awt.Color(255, 51, 255));
        panelBody.setkStartColor(new java.awt.Color(102, 153, 255));
        panelBody.setMinimumSize(new java.awt.Dimension(435, 443));
        panelBody.setPreferredSize(new java.awt.Dimension(435, 443));
        panelBody.setLayout(new javax.swing.BoxLayout(panelBody, javax.swing.BoxLayout.Y_AXIS));

        panelMain.setBackground(new java.awt.Color(255, 255, 255));
        panelMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 30, 1, 30));
        panelMain.setkBorderColor(new java.awt.Color(0, 255, 102));
        panelMain.setkBorderRadius(30);
        panelMain.setkBorderSize(2.0F);
        panelMain.setkEndColor(new java.awt.Color(255, 255, 255));
        panelMain.setkStartColor(new java.awt.Color(255, 255, 255));
        panelMain.setOpaque(false);
        panelMain.setPreferredSize(new java.awt.Dimension(400, 165));
        panelMain.setLayout(new javax.swing.BoxLayout(panelMain, javax.swing.BoxLayout.Y_AXIS));

        panelTitle.setMinimumSize(new java.awt.Dimension(191, 27));
        panelTitle.setOpaque(false);
        panelTitle.setPreferredSize(new java.awt.Dimension(191, 27));
        panelTitle.setLayout(new java.awt.BorderLayout());

        lbTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(30, 113, 195));
        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTitle.setText(bundle.getString("titleResetPw")); // NOI18N
        panelTitle.add(lbTitle, java.awt.BorderLayout.CENTER);

        panelMain.add(panelTitle);

        panelContent.setBackground(new java.awt.Color(255, 255, 255));
        panelContent.setkBorderRadius(40);
        panelContent.setkEndColor(new java.awt.Color(255, 255, 255));
        panelContent.setkStartColor(new java.awt.Color(255, 255, 255));
        panelContent.setLayout(new java.awt.GridLayout(4, 0, 0, 20));

        panelEmail.setBackground(new java.awt.Color(255, 255, 255));
        panelEmail.setPreferredSize(new java.awt.Dimension(326, 50));
        panelEmail.setLayout(new java.awt.BorderLayout());

        txtEmail.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtEmail.setForeground(new java.awt.Color(30, 113, 195));
        txtEmail.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(204, 204, 204)));
        txtEmail.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        panelEmail.add(txtEmail, java.awt.BorderLayout.CENTER);

        lbEmail.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lbEmail.setForeground(new java.awt.Color(102, 102, 102));
        lbEmail.setText("Email");
        panelEmail.add(lbEmail, java.awt.BorderLayout.PAGE_START);

        lbSendOTP.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lbSendOTP.setForeground(new java.awt.Color(51, 102, 255));
        lbSendOTP.setText(bundle.getString("txtSendOTP")); // NOI18N
        lbSendOTP.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbSendOTP.setFocusable(false);
        panelEmail.add(lbSendOTP, java.awt.BorderLayout.LINE_END);

        panelContent.add(panelEmail);

        panelOTP.setBackground(new java.awt.Color(255, 255, 255));
        panelOTP.setPreferredSize(new java.awt.Dimension(258, 30));
        panelOTP.setLayout(new java.awt.BorderLayout());

        txtOTP.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtOTP.setForeground(new java.awt.Color(30, 113, 195));
        txtOTP.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(204, 204, 204)));
        txtOTP.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        panelOTP.add(txtOTP, java.awt.BorderLayout.CENTER);

        lbOTP.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lbOTP.setForeground(new java.awt.Color(102, 102, 102));
        lbOTP.setText(bundle.getString("txtOTP")); // NOI18N
        lbOTP.setPreferredSize(new java.awt.Dimension(103, 20));
        panelOTP.add(lbOTP, java.awt.BorderLayout.PAGE_START);

        panelContent.add(panelOTP);

        panelNewPassword.setBackground(new java.awt.Color(255, 255, 255));
        panelNewPassword.setPreferredSize(new java.awt.Dimension(258, 30));
        panelNewPassword.setLayout(new java.awt.BorderLayout());

        lbNewPassword.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lbNewPassword.setForeground(new java.awt.Color(102, 102, 102));
        lbNewPassword.setText(bundle.getString("txtPassword")); // NOI18N
        panelNewPassword.add(lbNewPassword, java.awt.BorderLayout.PAGE_START);

        txtNewPassword.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNewPassword.setForeground(new java.awt.Color(30, 113, 195));
        txtNewPassword.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(204, 204, 204)));
        txtNewPassword.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        panelNewPassword.add(txtNewPassword, java.awt.BorderLayout.CENTER);

        panelContent.add(panelNewPassword);

        panelConfrim.setBackground(new java.awt.Color(255, 255, 255));
        panelConfrim.setPreferredSize(new java.awt.Dimension(258, 30));
        panelConfrim.setLayout(new java.awt.BorderLayout());

        lbConfirm.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lbConfirm.setForeground(new java.awt.Color(102, 102, 102));
        lbConfirm.setText(bundle.getString("txtPasswordCf")); // NOI18N
        lbConfirm.setToolTipText(bundle.getString("txtPasswordCf")); // NOI18N
        panelConfrim.add(lbConfirm, java.awt.BorderLayout.PAGE_START);

        txtConfirmPassword.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtConfirmPassword.setForeground(new java.awt.Color(30, 113, 195));
        txtConfirmPassword.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(204, 204, 204)));
        txtConfirmPassword.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        panelConfrim.add(txtConfirmPassword, java.awt.BorderLayout.CENTER);

        panelContent.add(panelConfrim);

        panelMain.add(panelContent);

        panelEmpty.setBackground(new java.awt.Color(255, 255, 255));
        panelEmpty.setPreferredSize(new java.awt.Dimension(295, 20));
        panelEmpty.setLayout(new java.awt.BorderLayout());

        checkShowPassword.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        checkShowPassword.setForeground(new java.awt.Color(30, 113, 195));
        checkShowPassword.setText(bundle.getString("txtShowPassword")); // NOI18N
        panelEmpty.add(checkShowPassword, java.awt.BorderLayout.LINE_START);

        panelMain.add(panelEmpty);

        panelAction.setBackground(new java.awt.Color(255, 255, 255));
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
        btnClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCloseMouseClicked(evt);
            }
        });
        panelAction.add(btnClose);

        panelMain.add(panelAction);

        panelBody.add(panelMain);

        getContentPane().add(panelBody, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void btnCloseMouseClicked(java.awt.event.MouseEvent evt) {
        this.dispose();
    }

                  

    public com.k33ptoo.components.KButton getBtnClose() {
        return btnClose;
    }

    public com.k33ptoo.components.KButton getBtnConfirm() {
        return btnConfirm;
    }

    public javax.swing.JPasswordField getTxtConfirmPassword() {
        return (JPasswordField)txtConfirmPassword;
    }

    public javax.swing.JTextField getTxtEmail() {
        return txtEmail;
    }

    public javax.swing.JPasswordField getTxtNewPassword() {
        return (JPasswordField)txtNewPassword;
    }

    public javax.swing.JTextField getTxtOTP() {
        return txtOTP;
    }
    

    public javax.swing.JLabel getLbSendOTP() {
        return lbSendOTP;
    }
    

    public javax.swing.JCheckBox getCheckShowPassword() {
        return checkShowPassword;
    }

}
