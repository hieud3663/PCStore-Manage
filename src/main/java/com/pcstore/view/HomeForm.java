/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import com.pcstore.controller.HomeController;

/**
 *
 * @author MSII
 */
public class HomeForm extends javax.swing.JPanel {

    private HomeController homeController;

    private javax.swing.JLabel lbAvatar;
    private javax.swing.JLabel lbEmployeeID;
    private javax.swing.JLabel lbLoginAt;
    private javax.swing.JLabel lbName;
    private javax.swing.JLabel lbOrderMonth;
    private javax.swing.JLabel lbOrderToday;
    private javax.swing.JLabel lbPosition;
    private javax.swing.JLabel lbProfitMonth;
    private javax.swing.JLabel lbProfitToday;
    private javax.swing.JLabel lbProfitWeek;
    private javax.swing.JLabel lbTitleInfo;
    private javax.swing.JLabel lbTitleRevenue;
    private com.k33ptoo.components.KGradientPanel panelBody;
    private javax.swing.JPanel panelChart;
    private javax.swing.JPanel panelHeader;
    private com.k33ptoo.components.KGradientPanel panelMain;
    private com.k33ptoo.components.KGradientPanel panelProfile;
    private com.k33ptoo.components.KGradientPanel panelRevenue;
    private javax.swing.JPanel panelSubRevenue;
    private javax.swing.JPanel panelTitleBody;
    private javax.swing.JPanel pnAvatar;
    private javax.swing.JPanel pnHeaderInfo;
    private javax.swing.JPanel pnHeaderRevenue;
    private javax.swing.JPanel pnInfo;
    private javax.swing.JLabel txtEmployeeID;
    private javax.swing.JLabel txtLoginAt;
    private javax.swing.JLabel txtName;
    private javax.swing.JLabel txtOrderMonth;
    private javax.swing.JLabel txtOrderToday;
    private javax.swing.JLabel txtPosition;
    private javax.swing.JLabel txtProfitMonth;
    private javax.swing.JLabel txtProfitToday;

    public HomeForm() {
        initComponents();
        homeController = HomeController.getInstance(this);
    }


    @SuppressWarnings("unchecked")
    private void initComponents() {

        panelMain = new com.k33ptoo.components.KGradientPanel();
        panelHeader = new javax.swing.JPanel();
        panelProfile = new com.k33ptoo.components.KGradientPanel();
        pnHeaderInfo = new javax.swing.JPanel();
        lbTitleInfo = new javax.swing.JLabel();
        pnAvatar = new javax.swing.JPanel();
        lbAvatar = new javax.swing.JLabel();
        pnInfo = new javax.swing.JPanel();
        lbName = new javax.swing.JLabel();
        txtName = new javax.swing.JLabel();
        lbEmployeeID = new javax.swing.JLabel();
        txtEmployeeID = new javax.swing.JLabel();
        lbPosition = new javax.swing.JLabel();
        txtPosition = new javax.swing.JLabel();
        lbLoginAt = new javax.swing.JLabel();
        txtLoginAt = new javax.swing.JLabel();
        panelRevenue = new com.k33ptoo.components.KGradientPanel();
        pnHeaderRevenue = new javax.swing.JPanel();
        lbTitleRevenue = new javax.swing.JLabel();
        panelSubRevenue = new javax.swing.JPanel();
        lbOrderToday = new javax.swing.JLabel();
        txtOrderToday = new javax.swing.JLabel();
        lbProfitToday = new javax.swing.JLabel();
        txtProfitToday = new javax.swing.JLabel();
        lbOrderMonth = new javax.swing.JLabel();
        txtOrderMonth = new javax.swing.JLabel();
        lbProfitMonth = new javax.swing.JLabel();
        txtProfitMonth = new javax.swing.JLabel();
        panelBody = new com.k33ptoo.components.KGradientPanel();
        panelTitleBody = new javax.swing.JPanel();
        lbProfitWeek = new javax.swing.JLabel();
        panelChart = new javax.swing.JPanel();

        setBackground(new java.awt.Color(204, 209, 220));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setLayout(new java.awt.BorderLayout());

        panelMain.setBackground(new java.awt.Color(204, 209, 220));
        panelMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panelMain.setkAlpha(2.0F);
        panelMain.setkBorderColor(new java.awt.Color(0, 255, 102));
        panelMain.setkFillBackground(false);
        panelMain.setkShowBorder(false);
        panelMain.setOpaque(false);
        panelMain.setLayout(new java.awt.BorderLayout(10, 10));

        panelHeader.setBackground(new java.awt.Color(204, 209, 220));
        panelHeader.setPreferredSize(new java.awt.Dimension(100, 250));
        panelHeader.setLayout(new java.awt.BorderLayout(10, 0));

        panelProfile.setBackground(new java.awt.Color(204, 209, 220));
        panelProfile.setkBorderRadius(20);
        panelProfile.setkEndColor(new java.awt.Color(255, 255, 255));
        panelProfile.setkStartColor(new java.awt.Color(255, 255, 255));
        panelProfile.setkTransparentControls(false);
        panelProfile.setPreferredSize(new java.awt.Dimension(420, 0));
        panelProfile.setLayout(new javax.swing.BoxLayout(panelProfile, javax.swing.BoxLayout.Y_AXIS));

        pnHeaderInfo.setOpaque(false);
        pnHeaderInfo.setPreferredSize(new java.awt.Dimension(650, 25));
        pnHeaderInfo.setLayout(new java.awt.BorderLayout());

        lbTitleInfo.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbTitleInfo.setForeground(new java.awt.Color(51, 8, 176));
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        lbTitleInfo.setText(bundle.getString("lbHeaderInfo")); // NOI18N
        lbTitleInfo.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        pnHeaderInfo.add(lbTitleInfo, java.awt.BorderLayout.CENTER);

        panelProfile.add(pnHeaderInfo);

        pnAvatar.setOpaque(false);
        pnAvatar.setPreferredSize(new java.awt.Dimension(420, 90));
        pnAvatar.setLayout(new java.awt.BorderLayout());

        lbAvatar.setBackground(new java.awt.Color(255, 255, 255));
        lbAvatar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbAvatar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/profile.png"))); // NOI18N
        pnAvatar.add(lbAvatar, java.awt.BorderLayout.CENTER);

        panelProfile.add(pnAvatar);

        pnInfo.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 15, 15, 5));
        pnInfo.setOpaque(false);
        pnInfo.setLayout(new java.awt.GridLayout(4, 2, 5, 5));

        lbName.setBackground(new java.awt.Color(255, 255, 255));
        lbName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/employee-man-alt.png"))); // NOI18N
        lbName.setText(bundle.getString("lbEmployeeName")); // NOI18N
        lbName.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        pnInfo.add(lbName);

        txtName.setBackground(new java.awt.Color(255, 255, 255));
        txtName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtName.setText("...");
        txtName.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        pnInfo.add(txtName);

        lbEmployeeID.setBackground(new java.awt.Color(255, 255, 255));
        lbEmployeeID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbEmployeeID.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbEmployeeID.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/id-badge.png"))); // NOI18N
        lbEmployeeID.setText(bundle.getString("lbEmployeeID")); // NOI18N
        lbEmployeeID.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        pnInfo.add(lbEmployeeID);

        txtEmployeeID.setBackground(new java.awt.Color(255, 255, 255));
        txtEmployeeID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtEmployeeID.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtEmployeeID.setText("...");
        txtEmployeeID.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        pnInfo.add(txtEmployeeID);

        lbPosition.setBackground(new java.awt.Color(255, 255, 255));
        lbPosition.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbPosition.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbPosition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/chair-office.png"))); // NOI18N
        lbPosition.setText(bundle.getString("lbPositionHome")); // NOI18N
        lbPosition.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        pnInfo.add(lbPosition);

        txtPosition.setBackground(new java.awt.Color(255, 255, 255));
        txtPosition.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtPosition.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtPosition.setText("...");
        txtPosition.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        pnInfo.add(txtPosition);

        lbLoginAt.setBackground(new java.awt.Color(255, 255, 255));
        lbLoginAt.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbLoginAt.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbLoginAt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/pending.png"))); // NOI18N
        lbLoginAt.setText(bundle.getString("lbLoginAt")); // NOI18N
        lbLoginAt.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        pnInfo.add(lbLoginAt);

        txtLoginAt.setBackground(new java.awt.Color(255, 255, 255));
        txtLoginAt.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtLoginAt.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtLoginAt.setText("...");
        txtLoginAt.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        pnInfo.add(txtLoginAt);

        panelProfile.add(pnInfo);

        panelHeader.add(panelProfile, java.awt.BorderLayout.LINE_START);

        panelRevenue.setBackground(new java.awt.Color(204, 209, 220));
        panelRevenue.setkBorderRadius(20);
        panelRevenue.setkBorderSize(2.0F);
        panelRevenue.setkEndColor(new java.awt.Color(255, 255, 255));
        panelRevenue.setkStartColor(new java.awt.Color(255, 255, 255));
        panelRevenue.setkTransparentControls(false);
        panelRevenue.setPreferredSize(new java.awt.Dimension(650, 200));
        panelRevenue.setLayout(new javax.swing.BoxLayout(panelRevenue, javax.swing.BoxLayout.Y_AXIS));

        pnHeaderRevenue.setOpaque(false);
        pnHeaderRevenue.setPreferredSize(new java.awt.Dimension(650, 25));
        pnHeaderRevenue.setLayout(new java.awt.BorderLayout());

        lbTitleRevenue.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbTitleRevenue.setForeground(new java.awt.Color(51, 8, 176));
        lbTitleRevenue.setText(bundle.getString("lbRevenueHome")); // NOI18N
        lbTitleRevenue.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        pnHeaderRevenue.add(lbTitleRevenue, java.awt.BorderLayout.CENTER);

        panelRevenue.add(pnHeaderRevenue);

        panelSubRevenue.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 50, 10, 50));
        panelSubRevenue.setOpaque(false);
        panelSubRevenue.setPreferredSize(new java.awt.Dimension(650, 220));
        panelSubRevenue.setLayout(new java.awt.GridLayout(6, 2, 10, 10));

        lbOrderToday.setBackground(new java.awt.Color(255, 255, 255));
        lbOrderToday.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbOrderToday.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbOrderToday.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/cart-shopping-fast.png"))); // NOI18N
        lbOrderToday.setText(bundle.getString("lbOrderToday")); // NOI18N
        lbOrderToday.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        panelSubRevenue.add(lbOrderToday);

        txtOrderToday.setBackground(new java.awt.Color(255, 255, 255));
        txtOrderToday.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtOrderToday.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtOrderToday.setText("...");
        txtOrderToday.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        panelSubRevenue.add(txtOrderToday);

        lbProfitToday.setBackground(new java.awt.Color(255, 255, 255));
        lbProfitToday.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbProfitToday.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbProfitToday.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/coin-up-arrow.png"))); // NOI18N
        lbProfitToday.setText(bundle.getString("lbProfitToday")); // NOI18N
        lbProfitToday.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        panelSubRevenue.add(lbProfitToday);

        txtProfitToday.setBackground(new java.awt.Color(255, 255, 255));
        txtProfitToday.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtProfitToday.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtProfitToday.setText("...");
        txtProfitToday.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        panelSubRevenue.add(txtProfitToday);

        lbOrderMonth.setBackground(new java.awt.Color(255, 255, 255));
        lbOrderMonth.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbOrderMonth.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbOrderMonth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/cart-shopping-fast.png"))); // NOI18N
        lbOrderMonth.setText(bundle.getString("lbOrderMonth")); // NOI18N
        lbOrderMonth.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        panelSubRevenue.add(lbOrderMonth);

        txtOrderMonth.setBackground(new java.awt.Color(255, 255, 255));
        txtOrderMonth.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtOrderMonth.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtOrderMonth.setText("...");
        txtOrderMonth.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        panelSubRevenue.add(txtOrderMonth);

        lbProfitMonth.setBackground(new java.awt.Color(255, 255, 255));
        lbProfitMonth.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbProfitMonth.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbProfitMonth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/coin-up-arrow.png"))); // NOI18N
        lbProfitMonth.setText(bundle.getString("lbProfitMonth")); // NOI18N
        lbProfitMonth.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        panelSubRevenue.add(lbProfitMonth);

        txtProfitMonth.setBackground(new java.awt.Color(255, 255, 255));
        txtProfitMonth.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtProfitMonth.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtProfitMonth.setText("...");
        txtProfitMonth.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        panelSubRevenue.add(txtProfitMonth);

        panelRevenue.add(panelSubRevenue);

        panelHeader.add(panelRevenue, java.awt.BorderLayout.CENTER);

        panelMain.add(panelHeader, java.awt.BorderLayout.PAGE_START);

        panelBody.setBackground(new java.awt.Color(204, 209, 220));
        panelBody.setkBorderRadius(20);
        panelBody.setkBorderSize(2.0F);
        panelBody.setkEndColor(new java.awt.Color(255, 255, 255));
        panelBody.setkStartColor(new java.awt.Color(255, 255, 255));
        panelBody.setkTransparentControls(false);
        panelBody.setPreferredSize(new java.awt.Dimension(1080, 360));
        panelBody.setLayout(new javax.swing.BoxLayout(panelBody, javax.swing.BoxLayout.Y_AXIS));

        panelTitleBody.setOpaque(false);
        panelTitleBody.setPreferredSize(new java.awt.Dimension(1080, 20));
        panelTitleBody.setLayout(new java.awt.BorderLayout());

        lbProfitWeek.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbProfitWeek.setForeground(new java.awt.Color(51, 8, 176));
        lbProfitWeek.setText(bundle.getString("lbRevenueWeek")); // NOI18N
        lbProfitWeek.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        panelTitleBody.add(lbProfitWeek, java.awt.BorderLayout.CENTER);

        panelBody.add(panelTitleBody);

        panelChart.setOpaque(false);
        panelChart.setPreferredSize(new java.awt.Dimension(1080, 300));
        panelChart.setLayout(new java.awt.BorderLayout());
        panelBody.add(panelChart);

        panelMain.add(panelBody, java.awt.BorderLayout.CENTER);

        add(panelMain, java.awt.BorderLayout.CENTER);
    }


    public javax.swing.JLabel getLbAvatar() {
        return lbAvatar;
    }

    public javax.swing.JLabel getLbEmployeeID() {
        return lbEmployeeID;
    }

    public javax.swing.JLabel getLbLoginAt() {
        return lbLoginAt;
    }

    public javax.swing.JLabel getLbName() {
        return lbName;
    }

    public javax.swing.JLabel getLbOrderMonth() {
        return lbOrderMonth;
    }

    public javax.swing.JLabel getLbOrderToday() {
        return lbOrderToday;
    }

    public javax.swing.JLabel getLbPosition() {
        return lbPosition;
    }

    public javax.swing.JLabel getLbProfitMonth() {
        return lbProfitMonth;
    }

    public javax.swing.JLabel getLbProfitToday() {
        return lbProfitToday;
    }

    public javax.swing.JLabel getLbProfitWeek() {
        return lbProfitWeek;
    }

    public javax.swing.JLabel getLbTitleInfo() {
        return lbTitleInfo;
    }

    public javax.swing.JLabel getLbTitleRevenue() {
        return lbTitleRevenue;
    }

    public com.k33ptoo.components.KGradientPanel getPanelBody() {
        return panelBody;
    }

    public javax.swing.JPanel getPanelChart() {
        return panelChart;
    }

    public javax.swing.JPanel getPanelHeader() {
        return panelHeader;
    }

    public com.k33ptoo.components.KGradientPanel getPanelMain() {
        return panelMain;
    }

    public com.k33ptoo.components.KGradientPanel getPanelProfile() {
        return panelProfile;
    }

    public com.k33ptoo.components.KGradientPanel getPanelRevenue() {
        return panelRevenue;
    }

    public javax.swing.JPanel getPanelSubRevenue() {
        return panelSubRevenue;
    }

    public javax.swing.JPanel getPanelTitleBody() {
        return panelTitleBody;
    }

    public javax.swing.JPanel getPnAvatar() {
        return pnAvatar;
    }

    public javax.swing.JPanel getPnHeaderInfo() {
        return pnHeaderInfo;
    }

    public javax.swing.JPanel getPnHeaderRevenue() {
        return pnHeaderRevenue;
    }

    public javax.swing.JPanel getPnInfo() {
        return pnInfo;
    }

    public javax.swing.JLabel getTxtEmployeeID() {
        return txtEmployeeID;
    }

    public javax.swing.JLabel getTxtLoginAt() {
        return txtLoginAt;
    }

    public javax.swing.JLabel getTxtName() {
        return txtName;
    }

    public javax.swing.JLabel getTxtOrderMonth() {
        return txtOrderMonth;
    }

    public javax.swing.JLabel getTxtOrderToday() {
        return txtOrderToday;
    }

    public javax.swing.JLabel getTxtPosition() {
        return txtPosition;
    }

    public javax.swing.JLabel getTxtProfitMonth() {
        return txtProfitMonth;
    }

    public javax.swing.JLabel getTxtProfitToday() {
        return txtProfitToday;
    }
}
