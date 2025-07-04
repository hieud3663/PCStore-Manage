/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import javax.swing.JPasswordField;

import com.pcstore.controller.UserController;

/**
 *
 * @author MSII
 */
public class UserForm extends javax.swing.JPanel {

    private UserController userController;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KButton btnAdd;
    private com.k33ptoo.components.KButton btnDelete;
    private com.k33ptoo.components.KButton btnRefresh;
    private com.k33ptoo.components.KButton btnResetPassword;
    private com.k33ptoo.components.KButton btnUpdate;
    private javax.swing.JComboBox<String> cbbRole;
    private javax.swing.JComboBox<String> cbbStatus;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPaneListUser;
    private javax.swing.JLabel labelESC;
    private javax.swing.JLabel lbEmail;
    private javax.swing.JLabel lbIDUser;
    private javax.swing.JLabel lbLastlogin;
    private javax.swing.JLabel lbName;
    private javax.swing.JLabel lbPassword;
    private javax.swing.JLabel lbPhonenumber;
    private javax.swing.JLabel lbRole;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JLabel lbUsername;
    private javax.swing.JPanel paneIDUser;
    private javax.swing.JPanel panelAction;
    private javax.swing.JPanel panelDetail;
    private javax.swing.JPanel panelEmail;
    private javax.swing.JPanel panelEmpty;
    private javax.swing.JPanel panelEmptyAction;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelInfoDetail;
    private javax.swing.JPanel panelLastlogin;
    private javax.swing.JPanel panelListUser;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel panelName;
    private javax.swing.JPanel panelPassword;
    private javax.swing.JPanel panelPhonenumber;
    private javax.swing.JPanel panelRole;
    private javax.swing.JPanel panelSearch;
    private javax.swing.JPanel panelStatus;
    private javax.swing.JPanel panelUsername;
    private javax.swing.JTable tableListUser;
    private com.pcstore.utils.TextFieldSearch textFieldSearch;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtIDUser;
    private javax.swing.JTextField txtLastlogin;
    private javax.swing.JTextField txtNameEmployee;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextField txtPhonenumber;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
    /**
     * Creates new form UserForm
     */
    public UserForm() {
        initComponents();

        userController = new UserController(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMain = new javax.swing.JPanel();
        panelHeader = new javax.swing.JPanel();
        lbTitle = new javax.swing.JLabel();
        panelAction = new javax.swing.JPanel();
        panelEmptyAction = new javax.swing.JPanel();
        btnAdd = new com.k33ptoo.components.KButton();
        btnUpdate = new com.k33ptoo.components.KButton();
        btnDelete = new com.k33ptoo.components.KButton();
        btnRefresh = new com.k33ptoo.components.KButton();
        jPanel1 = new javax.swing.JPanel();
        btnResetPassword = new com.k33ptoo.components.KButton();
        panelDetail = new javax.swing.JPanel();
        panelInfoDetail = new javax.swing.JPanel();
        paneIDUser = new javax.swing.JPanel();
        lbIDUser = new javax.swing.JLabel();
        txtIDUser = new javax.swing.JTextField();
        panelName = new javax.swing.JPanel();
        lbName = new javax.swing.JLabel();
        txtNameEmployee = new javax.swing.JTextField();
        panelStatus = new javax.swing.JPanel();
        lbStatus = new javax.swing.JLabel();
        cbbStatus = new javax.swing.JComboBox<>();
        panelUsername = new javax.swing.JPanel();
        lbUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        panelPhonenumber = new javax.swing.JPanel();
        lbPhonenumber = new javax.swing.JLabel();
        txtPhonenumber = new javax.swing.JTextField();
        panelRole = new javax.swing.JPanel();
        lbRole = new javax.swing.JLabel();
        cbbRole = new javax.swing.JComboBox<>();
        panelPassword = new javax.swing.JPanel();
        lbPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        panelEmail = new javax.swing.JPanel();
        lbEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        panelLastlogin = new javax.swing.JPanel();
        lbLastlogin = new javax.swing.JLabel();
        txtLastlogin = new javax.swing.JTextField();
        panelEmpty = new javax.swing.JPanel();
        labelESC = new javax.swing.JLabel();
        panelSearch = new javax.swing.JPanel();
        textFieldSearch = new com.pcstore.utils.TextFieldSearch();
        panelListUser = new javax.swing.JPanel();
        jScrollPaneListUser = new javax.swing.JScrollPane();
        tableListUser = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        panelMain.setBackground(new java.awt.Color(255, 255, 255));
        panelMain.setPreferredSize(new java.awt.Dimension(1197, 713));
        panelMain.setLayout(new javax.swing.BoxLayout(panelMain, javax.swing.BoxLayout.Y_AXIS));

        panelHeader.setOpaque(false);
        panelHeader.setPreferredSize(new java.awt.Dimension(100, 40));
        panelHeader.setLayout(new java.awt.BorderLayout());

        lbTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(30, 113, 195));
        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        lbTitle.setText(bundle.getString("txtMenuIUser")); // NOI18N
        lbTitle.setToolTipText(bundle.getString("txtMenuSell")); // NOI18N
        lbTitle.setPreferredSize(new java.awt.Dimension(172, 30));
        panelHeader.add(lbTitle, java.awt.BorderLayout.PAGE_START);

        panelMain.add(panelHeader);

        panelAction.setOpaque(false);
        panelAction.setPreferredSize(new java.awt.Dimension(1197, 60));
        panelAction.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        panelEmptyAction.setBackground(new java.awt.Color(255, 255, 255));
        panelEmptyAction.setPreferredSize(new java.awt.Dimension(110, 20));
        panelAction.add(panelEmptyAction);

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/user-add_2.png"))); // NOI18N
        btnAdd.setText(bundle.getString("btnAddUser")); // NOI18N
        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnAdd.setIconTextGap(15);
        btnAdd.setkAllowGradient(false);
        btnAdd.setkBackGroundColor(new java.awt.Color(26, 162, 106));
        btnAdd.setkBorderRadius(20);
        btnAdd.setkEndColor(new java.awt.Color(0, 255, 51));
        btnAdd.setkHoverColor(new java.awt.Color(26, 190, 62));
        btnAdd.setkHoverEndColor(new java.awt.Color(0, 204, 255));
        btnAdd.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnAdd.setkHoverStartColor(new java.awt.Color(0, 204, 255));
        btnAdd.setkStartColor(new java.awt.Color(0, 204, 255));
        btnAdd.setPreferredSize(new java.awt.Dimension(185, 35));
        panelAction.add(btnAdd);

        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/user-pen.png"))); // NOI18N
        btnUpdate.setText(bundle.getString("btnUpdate")); // NOI18N
        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnUpdate.setIconTextGap(15);
        btnUpdate.setkAllowGradient(false);
        btnUpdate.setkBackGroundColor(new java.awt.Color(86, 167, 233));
        btnUpdate.setkBorderRadius(20);
        btnUpdate.setkEndColor(new java.awt.Color(0, 153, 153));
        btnUpdate.setkHoverColor(new java.awt.Color(122, 196, 235));
        btnUpdate.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnUpdate.setkHoverStartColor(new java.awt.Color(102, 102, 255));
        btnUpdate.setPreferredSize(new java.awt.Dimension(185, 35));
        panelAction.add(btnUpdate);

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/trash.png"))); // NOI18N
        btnDelete.setText(bundle.getString("btnDelete")); // NOI18N
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnDelete.setIconTextGap(10);
        btnDelete.setkAllowGradient(false);
        btnDelete.setkBackGroundColor(new java.awt.Color(226, 21, 29));
        btnDelete.setkBorderRadius(20);
        btnDelete.setkEndColor(new java.awt.Color(255, 102, 51));
        btnDelete.setkHoverColor(new java.awt.Color(252, 83, 0));
        btnDelete.setkHoverEndColor(new java.awt.Color(255, 0, 51));
        btnDelete.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnDelete.setkHoverStartColor(new java.awt.Color(255, 102, 0));
        btnDelete.setkStartColor(new java.awt.Color(255, 0, 51));
        btnDelete.setMinimumSize(new java.awt.Dimension(140, 25));
        btnDelete.setPreferredSize(new java.awt.Dimension(140, 35));
        btnDelete.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/trash.png"))); // NOI18N
        panelAction.add(btnDelete);

        btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/refresh.png"))); // NOI18N
        btnRefresh.setText(bundle.getString("btnRefresh")); // NOI18N
        btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnRefresh.setIconTextGap(20);
        btnRefresh.setkAllowGradient(false);
        btnRefresh.setkBackGroundColor(new java.awt.Color(144, 194, 244));
        btnRefresh.setkBorderRadius(20);
        btnRefresh.setkEndColor(new java.awt.Color(153, 204, 255));
        btnRefresh.setkHoverColor(new java.awt.Color(144, 206, 245));
        btnRefresh.setkHoverEndColor(new java.awt.Color(102, 204, 255));
        btnRefresh.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnRefresh.setkHoverStartColor(new java.awt.Color(102, 204, 255));
        btnRefresh.setkStartColor(new java.awt.Color(153, 204, 255));
        btnRefresh.setPreferredSize(new java.awt.Dimension(140, 35));
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        panelAction.add(btnRefresh);

        jPanel1.setMinimumSize(new java.awt.Dimension(100, 30));
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(300, 35));
        jPanel1.setLayout(new java.awt.BorderLayout());

        btnResetPassword.setText(bundle.getString("btnResetPassword")); // NOI18N
        btnResetPassword.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnResetPassword.setkAllowGradient(false);
        btnResetPassword.setkBackGroundColor(new java.awt.Color(220, 144, 0));
        btnResetPassword.setkHoverColor(new java.awt.Color(255, 178, 37));
        btnResetPassword.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnResetPassword.setPreferredSize(new java.awt.Dimension(140, 45));
        jPanel1.add(btnResetPassword, java.awt.BorderLayout.LINE_END);

        panelAction.add(jPanel1);

        panelMain.add(panelAction);

        panelDetail.setBackground(new java.awt.Color(255, 255, 255));
        panelDetail.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("txtDetails"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(24, 100, 178))); // NOI18N
        panelDetail.setLayout(new java.awt.BorderLayout());

        panelInfoDetail.setBackground(new java.awt.Color(255, 255, 255));
        panelInfoDetail.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 50, 1, 50));
        panelInfoDetail.setForeground(new java.awt.Color(30, 113, 195));
        panelInfoDetail.setPreferredSize(new java.awt.Dimension(400, 280));
        panelInfoDetail.setLayout(new java.awt.GridLayout(4, 3, 30, 23));

        paneIDUser.setOpaque(false);
        paneIDUser.setLayout(new java.awt.BorderLayout(15, 0));

        lbIDUser.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbIDUser.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbIDUser.setText(bundle.getString("lbID")); // NOI18N
        lbIDUser.setPreferredSize(new java.awt.Dimension(149, 16));
        paneIDUser.add(lbIDUser, java.awt.BorderLayout.LINE_START);

        txtIDUser.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        txtIDUser.setForeground(new java.awt.Color(30, 113, 195));
        txtIDUser.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        txtIDUser.setDisabledTextColor(new java.awt.Color(51, 51, 51));
        txtIDUser.setEnabled(false);
        paneIDUser.add(txtIDUser, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(paneIDUser);

        panelName.setOpaque(false);
        panelName.setLayout(new java.awt.BorderLayout(15, 0));

        lbName.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbName.setText(bundle.getString("lbEmployee")); // NOI18N
        lbName.setPreferredSize(new java.awt.Dimension(149, 16));
        panelName.add(lbName, java.awt.BorderLayout.LINE_START);

        txtNameEmployee.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNameEmployee.setForeground(new java.awt.Color(30, 113, 195));
        txtNameEmployee.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        txtNameEmployee.setEnabled(false);
        txtNameEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNameEmployeeActionPerformed(evt);
            }
        });
        panelName.add(txtNameEmployee, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelName);

        panelStatus.setOpaque(false);
        panelStatus.setLayout(new java.awt.BorderLayout(15, 0));

        lbStatus.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lbStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbStatus.setText(bundle.getString("lbStatus")); // NOI18N
        lbStatus.setPreferredSize(new java.awt.Dimension(149, 16));
        panelStatus.add(lbStatus, java.awt.BorderLayout.LINE_START);

        cbbStatus.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        cbbStatus.setForeground(new java.awt.Color(30, 113, 195));
        cbbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Inactive" }));
        cbbStatus.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        panelStatus.add(cbbStatus, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelStatus);

        panelUsername.setOpaque(false);
        panelUsername.setLayout(new java.awt.BorderLayout(15, 0));

        lbUsername.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbUsername.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbUsername.setText(bundle.getString("lbUsername")); // NOI18N
        lbUsername.setPreferredSize(new java.awt.Dimension(149, 16));
        panelUsername.add(lbUsername, java.awt.BorderLayout.LINE_START);

        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtUsername.setForeground(new java.awt.Color(30, 113, 195));
        txtUsername.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        txtUsername.setMinimumSize(new java.awt.Dimension(135, 23));
        txtUsername.setName(""); // NOI18N
        txtUsername.setPreferredSize(new java.awt.Dimension(135, 23));
        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });
        panelUsername.add(txtUsername, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelUsername);

        panelPhonenumber.setOpaque(false);
        panelPhonenumber.setPreferredSize(new java.awt.Dimension(64, 35));
        panelPhonenumber.setLayout(new java.awt.BorderLayout(15, 1));

        lbPhonenumber.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbPhonenumber.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbPhonenumber.setText(bundle.getString("lbPhoneNumber")); // NOI18N
        lbPhonenumber.setPreferredSize(new java.awt.Dimension(149, 16));
        panelPhonenumber.add(lbPhonenumber, java.awt.BorderLayout.LINE_START);

        txtPhonenumber.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtPhonenumber.setForeground(new java.awt.Color(30, 113, 195));
        txtPhonenumber.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        txtPhonenumber.setEnabled(false);
        panelPhonenumber.add(txtPhonenumber, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelPhonenumber);

        panelRole.setOpaque(false);
        panelRole.setLayout(new java.awt.BorderLayout(15, 1));

        lbRole.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbRole.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbRole.setText(bundle.getString("lbRole")); // NOI18N
        lbRole.setPreferredSize(new java.awt.Dimension(149, 16));
        panelRole.add(lbRole, java.awt.BorderLayout.LINE_START);

        cbbRole.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        cbbRole.setForeground(new java.awt.Color(30, 113, 195));
        cbbRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbbRole.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        cbbRole.setPreferredSize(new java.awt.Dimension(64, 23));
        panelRole.add(cbbRole, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelRole);

        panelPassword.setOpaque(false);
        panelPassword.setLayout(new java.awt.BorderLayout(15, 1));

        lbPassword.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbPassword.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbPassword.setText(bundle.getString("lbPassword")); // NOI18N
        lbPassword.setPreferredSize(new java.awt.Dimension(149, 16));
        panelPassword.add(lbPassword, java.awt.BorderLayout.LINE_START);

        txtPassword.setBackground(new java.awt.Color(242, 242, 242));
        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtPassword.setForeground(new java.awt.Color(30, 113, 195));
        txtPassword.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        txtPassword.setEnabled(false);
        panelPassword.add(txtPassword, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelPassword);

        panelEmail.setOpaque(false);
        panelEmail.setPreferredSize(new java.awt.Dimension(64, 35));
        panelEmail.setLayout(new java.awt.BorderLayout(15, 1));

        lbEmail.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        lbEmail.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbEmail.setText(bundle.getString("lbEmail")); // NOI18N
        lbEmail.setPreferredSize(new java.awt.Dimension(149, 16));
        panelEmail.add(lbEmail, java.awt.BorderLayout.LINE_START);

        txtEmail.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtEmail.setForeground(new java.awt.Color(30, 113, 195));
        txtEmail.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        txtEmail.setEnabled(false);
        panelEmail.add(txtEmail, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelEmail);

        panelLastlogin.setOpaque(false);
        panelLastlogin.setLayout(new java.awt.BorderLayout(15, 0));

        lbLastlogin.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lbLastlogin.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbLastlogin.setText(bundle.getString("lbLastlogin")); // NOI18N
        lbLastlogin.setPreferredSize(new java.awt.Dimension(149, 16));
        panelLastlogin.add(lbLastlogin, java.awt.BorderLayout.LINE_START);

        txtLastlogin.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtLastlogin.setForeground(new java.awt.Color(30, 113, 195));
        txtLastlogin.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(153, 153, 153)));
        txtLastlogin.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtLastlogin.setEnabled(false);
        txtLastlogin.setOpaque(true);
        txtLastlogin.setPreferredSize(new java.awt.Dimension(100, 2));
        panelLastlogin.add(txtLastlogin, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelLastlogin);

        panelEmpty.setOpaque(false);
        panelEmpty.setLayout(new java.awt.BorderLayout(15, 0));

        labelESC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelESC.setForeground(new java.awt.Color(255, 0, 51));
        labelESC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/exclamation.png"))); // NOI18N
        labelESC.setText(bundle.getString("labelNoteESC")); // NOI18N
        panelEmpty.add(labelESC, java.awt.BorderLayout.PAGE_END);

        panelInfoDetail.add(panelEmpty);

        panelDetail.add(panelInfoDetail, java.awt.BorderLayout.CENTER);

        panelMain.add(panelDetail);

        panelSearch.setBackground(new java.awt.Color(255, 255, 255));
        panelSearch.setPreferredSize(new java.awt.Dimension(1197, 70));
        panelSearch.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 20));

        textFieldSearch.setPreferredSize(new java.awt.Dimension(450, 31));
        panelSearch.add(textFieldSearch);

        panelMain.add(panelSearch);

        panelListUser.setBackground(new java.awt.Color(255, 255, 255));
        panelListUser.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), bundle.getString("txtListUser"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(24, 100, 178))); // NOI18N
        panelListUser.setMinimumSize(new java.awt.Dimension(1197, 200));
        panelListUser.setOpaque(false);
        panelListUser.setPreferredSize(new java.awt.Dimension(1197, 400));
        panelListUser.setLayout(new java.awt.BorderLayout());

        tableListUser.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "ID", "Họ và tên", "Tên đăng nhập", "SĐT", "Email", "Phân quyền", "Trạng thái", "Đăng nhập lần cuối"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableListUser.getTableHeader().setReorderingAllowed(false);
        jScrollPaneListUser.setViewportView(tableListUser);
        if (tableListUser.getColumnModel().getColumnCount() > 0) {
            tableListUser.getColumnModel().getColumn(0).setPreferredWidth(40);
            tableListUser.getColumnModel().getColumn(0).setMaxWidth(40);
            tableListUser.getColumnModel().getColumn(1).setPreferredWidth(70);
            tableListUser.getColumnModel().getColumn(1).setMaxWidth(70);
            tableListUser.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("lbUsername")); // NOI18N
            tableListUser.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("lbPhoneNumber")); // NOI18N
            tableListUser.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("lbEmail")); // NOI18N
            tableListUser.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("lbRole")); // NOI18N
            tableListUser.getColumnModel().getColumn(7).setHeaderValue(bundle.getString("lbStatus")); // NOI18N
            tableListUser.getColumnModel().getColumn(8).setHeaderValue(bundle.getString("lbLastlogin")); // NOI18N
        }

        panelListUser.add(jScrollPaneListUser, java.awt.BorderLayout.CENTER);

        panelMain.add(panelListUser);

        add(panelMain, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsernameActionPerformed

    private void txtNameEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNameEmployeeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNameEmployeeActionPerformed

    public com.k33ptoo.components.KButton getBtnResetPassword() {
        return btnResetPassword;
    }

    public com.k33ptoo.components.KButton getBtnAdd() {
        return btnAdd;
    }

    public com.k33ptoo.components.KButton getBtnUpdate() {
        return btnUpdate;
    }

    public com.k33ptoo.components.KButton getBtnDelete() {
        return btnDelete;
    }

    public com.k33ptoo.components.KButton getBtnRefresh() {
        return btnRefresh;
    }
    
    public javax.swing.JTable getTableListUser() {
        return tableListUser;
    }

    public javax.swing.JTextField getTxtIDUser() {
        return txtIDUser;
    }

    public javax.swing.JTextField getTxtNameEmployee() {
        return txtNameEmployee;
    }

    public javax.swing.JTextField getTxtUsername() {
        return txtUsername;
    }

    public javax.swing.JTextField getTxtPhonenumber() {
        return txtPhonenumber;
    }

    public javax.swing.JTextField getTxtEmail() {
        return txtEmail;
    }

    public javax.swing.JTextField getTxtLastlogin() {
        return txtLastlogin;
    }

    public javax.swing.JComboBox<String> getCbbRole() {
        return cbbRole;
    }
    
    public javax.swing.JComboBox<String> getCbbStatus() {
        return cbbStatus;
    }
    
    public javax.swing.JPasswordField getTxtPassword() {
        return (JPasswordField) txtPassword;
    }
    
    public javax.swing.JLabel getLabelESC() {
        return labelESC;
    }
    
    public com.pcstore.utils.TextFieldSearch getTextFieldSearch() {
        return textFieldSearch;
    }

    
    public javax.swing.JPanel getPanelPassword() {
        return panelPassword;
    }

    
}
