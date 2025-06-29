/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.pcstore.view;

/**
 *
 * @author MSII
 */
public class DialogChooseEmployee extends javax.swing.JDialog {

    private com.k33ptoo.components.KButton btnClose;
    private com.k33ptoo.components.KButton btnConfirm;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel panelAction;
    private javax.swing.JPanel panelBody;
    private javax.swing.JPanel panelEmpty;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelMain;
    private javax.swing.JPanel pnEmptyAction;
    private javax.swing.JTable tableListEmployee;
    private com.pcstore.utils.TextFieldSearch textFieldSearch;

    
    public DialogChooseEmployee(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

   
    @SuppressWarnings("unchecked")

    private void initComponents() {

        panelMain = new javax.swing.JPanel();
        panelHeader = new javax.swing.JPanel();
        lbTitle = new javax.swing.JLabel();
        textFieldSearch = new com.pcstore.utils.TextFieldSearch();
        panelEmpty = new javax.swing.JPanel();
        panelBody = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableListEmployee = new javax.swing.JTable();
        panelAction = new javax.swing.JPanel();
        pnEmptyAction = new javax.swing.JPanel();
        btnConfirm = new com.k33ptoo.components.KButton();
        btnClose = new com.k33ptoo.components.KButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        panelMain.setBackground(new java.awt.Color(255, 255, 255));
        panelMain.setPreferredSize(new java.awt.Dimension(400, 548));
        panelMain.setLayout(new javax.swing.BoxLayout(panelMain, javax.swing.BoxLayout.Y_AXIS));

        panelHeader.setOpaque(false);
        panelHeader.setPreferredSize(new java.awt.Dimension(445, 30));
        panelHeader.setLayout(new java.awt.BorderLayout());

        lbTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(30, 113, 195));
        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        lbTitle.setText(bundle.getString("titleListEmployee")); // NOI18N
        panelHeader.add(lbTitle, java.awt.BorderLayout.PAGE_START);

        panelMain.add(panelHeader);
        panelMain.add(textFieldSearch);

        panelEmpty.setOpaque(false);
        panelEmpty.setPreferredSize(new java.awt.Dimension(386, 20));

        javax.swing.GroupLayout panelEmptyLayout = new javax.swing.GroupLayout(panelEmpty);
        panelEmpty.setLayout(panelEmptyLayout);
        panelEmptyLayout.setHorizontalGroup(
            panelEmptyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        panelEmptyLayout.setVerticalGroup(
            panelEmptyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 16, Short.MAX_VALUE)
        );

        panelMain.add(panelEmpty);

        panelBody.setBackground(new java.awt.Color(255, 255, 255));
        panelBody.setPreferredSize(new java.awt.Dimension(445, 500));
        panelBody.setLayout(new java.awt.BorderLayout());

        tableListEmployee.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "", "STT", "Mã nhân viên", "Họ tên"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableListEmployee.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tableListEmployee);
        if (tableListEmployee.getColumnModel().getColumnCount() > 0) {
            tableListEmployee.getColumnModel().getColumn(0).setPreferredWidth(20);
            tableListEmployee.getColumnModel().getColumn(0).setMaxWidth(20);
            tableListEmployee.getColumnModel().getColumn(1).setPreferredWidth(50);
            tableListEmployee.getColumnModel().getColumn(1).setMaxWidth(50);
            tableListEmployee.getColumnModel().getColumn(2).setPreferredWidth(100);
            tableListEmployee.getColumnModel().getColumn(2).setMaxWidth(100);
            tableListEmployee.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("lbIDEmployee")); // NOI18N
            tableListEmployee.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("lbName")); // NOI18N
        }

        panelBody.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panelMain.add(panelBody);

        panelAction.setOpaque(false);

        pnEmptyAction.setOpaque(false);
        pnEmptyAction.setPreferredSize(new java.awt.Dimension(100, 35));
        pnEmptyAction.setRequestFocusEnabled(false);

        javax.swing.GroupLayout pnEmptyActionLayout = new javax.swing.GroupLayout(pnEmptyAction);
        pnEmptyAction.setLayout(pnEmptyActionLayout);
        pnEmptyActionLayout.setHorizontalGroup(
            pnEmptyActionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        pnEmptyActionLayout.setVerticalGroup(
            pnEmptyActionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
        );

        panelAction.add(pnEmptyAction);

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

        panelMain.add(panelAction);

        getContentPane().add(panelMain, java.awt.BorderLayout.CENTER);

        pack();
    }


    
    public com.k33ptoo.components.KButton getBtnClose() {
        return btnClose;
    }

    public com.k33ptoo.components.KButton getBtnConfirm() {
        return btnConfirm;
    }

    public javax.swing.JTable getTableListEmployee() {
        return tableListEmployee;
    }

    public com.pcstore.utils.TextFieldSearch getTextFieldSearch() {
        return textFieldSearch;
    }

}
