/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

/**
 *
 * @author MSII
 */
public class EmployeeManageForm extends javax.swing.JPanel {

    private javax.swing.JTabbedPane jTabbed;
    private com.k33ptoo.components.KGradientPanel panelEmployee;
    private com.k33ptoo.components.KGradientPanel panelUser;

    public EmployeeManageForm() {
        initComponents();
        initComponentsV2();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jTabbed = new com.pcstore.utils.MaterialTabbed();
        panelEmployee = new com.k33ptoo.components.KGradientPanel();
        panelUser = new com.k33ptoo.components.KGradientPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(1157, 724));
        setLayout(new java.awt.BorderLayout());

        jTabbed.setBackground(new java.awt.Color(255, 255, 255));
        jTabbed.setForeground(new java.awt.Color(30, 113, 195));
        jTabbed.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbed.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        panelEmployee.setBackground(new java.awt.Color(255, 255, 255));
        panelEmployee.setkBorderColor(new java.awt.Color(0, 204, 204));
        panelEmployee.setkBorderRadius(30);
        panelEmployee.setkBorderSize(2.0F);
        panelEmployee.setkFillBackground(false);
        panelEmployee.setLayout(new java.awt.BorderLayout());
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/pcstore/resources/vi_VN"); // NOI18N
        jTabbed.addTab(bundle.getString("txtEmployee"), new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/employees.png")), panelEmployee); // NOI18N

        panelUser.setBackground(new java.awt.Color(255, 255, 255));
        panelUser.setkBorderColor(new java.awt.Color(0, 204, 204));
        panelUser.setkBorderRadius(30);
        panelUser.setkBorderSize(2.0F);
        panelUser.setkFillBackground(false);
        panelUser.setLayout(new java.awt.BorderLayout());
        jTabbed.addTab("User", new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/user-add_2.png")), panelUser); // NOI18N

        add(jTabbed, java.awt.BorderLayout.CENTER);
        jTabbed.getAccessibleContext().setAccessibleName(bundle.getString("txtEmployee")); // NOI18N
    }

    private void initComponentsV2(){
        panelEmployee.add(new EmployeeForm(), java.awt.BorderLayout.CENTER);
        panelUser.add(new UserForm(), java.awt.BorderLayout.CENTER);
    }

}
