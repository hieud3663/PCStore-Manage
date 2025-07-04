/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import com.pcstore.controller.CategoryController;
import com.pcstore.service.CategoryService;
import com.pcstore.service.ServiceFactory;

import javax.swing.*;

/**
 *
 * @author DUC ANH
 */
public class CategoryFormNew extends javax.swing.JPanel {

   // connection là kết nối DB
    private CategoryController categoryController;
    private final java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle();
    /**
     * Creates new form CategoryFormNew
     */
    public CategoryFormNew() {
        initComponents();
        initController();

        txtDateCreate.setOpaque(true); // Cho phép đổi màu nền JLabel
        txtDateCreate.setBackground(new java.awt.Color(220, 220, 220)); // Ngày khởi tạo xám nhạt
    }

    private void initController() {
        try {
            CategoryService categoryService = ServiceFactory.getCategoryService();
            categoryController = new CategoryController(this, categoryService);
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, 
                bundle.getString("categoryForm.serviceConnectionError"), 
                bundle.getString("categoryForm.serviceConnectionErrorTitle"), 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnTitile = new com.k33ptoo.components.KGradientPanel();
        lbTitle = new javax.swing.JLabel();
        pnFuntion = new com.k33ptoo.components.KGradientPanel();
        btnAddCategory = new com.k33ptoo.components.KButton();
        btnUpdateCategory = new com.k33ptoo.components.KButton();
        btnDeleteCategory = new com.k33ptoo.components.KButton();
        btnRefreshCategory = new com.k33ptoo.components.KButton();
        pnMain = new com.k33ptoo.components.KGradientPanel();
        pnListCategory = new com.k33ptoo.components.KGradientPanel();
        scrollTB = new javax.swing.JScrollPane();
        TableList = new javax.swing.JTable();
        pnInform = new com.k33ptoo.components.KGradientPanel();
        pnCategoryCode = new javax.swing.JPanel();
        lbCategoryCode = new javax.swing.JLabel();
        txtCategoryCode = new javax.swing.JTextField();
        pnCategoryName = new javax.swing.JPanel();
        lbCategoryName = new javax.swing.JLabel();
        txtCategoryName = new javax.swing.JTextField();
        pnDescription = new javax.swing.JPanel();
        lbDescription = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        lbStatus = new javax.swing.JLabel();
        cbStatus = new javax.swing.JComboBox<>();
        pnDateCreate = new javax.swing.JPanel();
        lbDateCreate = new javax.swing.JLabel();
        txtDateCreate = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1009, 700));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        pnTitile.setBackground(new java.awt.Color(255, 255, 255));
        pnTitile.setkEndColor(new java.awt.Color(0, 153, 255));
        pnTitile.setkFillBackground(false);
        pnTitile.setkStartColor(new java.awt.Color(0, 153, 255));
        pnTitile.setMinimumSize(new java.awt.Dimension(401, 40));
        pnTitile.setName(""); // NOI18N
        pnTitile.setLayout(new java.awt.BorderLayout());

        lbTitle.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(0, 44, 214));
        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        lbTitle.setText(bundle.getString("ProductCategoryManagement")); // NOI18N
        lbTitle.setPreferredSize(new java.awt.Dimension(361, 80));
        pnTitile.add(lbTitle, java.awt.BorderLayout.CENTER);

        add(pnTitile);

        pnFuntion.setBackground(new java.awt.Color(255, 255, 255));
        pnFuntion.setkFillBackground(false);
        pnFuntion.setPreferredSize(new java.awt.Dimension(1076, 50));
        pnFuntion.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        btnAddCategory.setText(bundle.getString("btAddCategory")); // NOI18N
        btnAddCategory.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnAddCategory.setkAllowGradient(false);
        btnAddCategory.setkBackGroundColor(new java.awt.Color(0, 204, 0));
        btnAddCategory.setkEndColor(new java.awt.Color(0, 204, 0));
        btnAddCategory.setkHoverColor(new java.awt.Color(0, 78, 200));
        btnAddCategory.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnAddCategory.setkHoverStartColor(new java.awt.Color(0, 102, 0));
        btnAddCategory.setkStartColor(new java.awt.Color(0, 204, 0));
        btnAddCategory.setPreferredSize(new java.awt.Dimension(80, 35));
        btnAddCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCategoryActionPerformed(evt);
            }
        });
        pnFuntion.add(btnAddCategory);

        btnUpdateCategory.setText(bundle.getString("btUpdateCategory")); // NOI18N
        btnUpdateCategory.setEnabled(false);
        btnUpdateCategory.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnUpdateCategory.setkAllowGradient(false);
        btnUpdateCategory.setkBackGroundColor(new java.awt.Color(0, 153, 255));
        btnUpdateCategory.setkEndColor(new java.awt.Color(0, 153, 255));
        btnUpdateCategory.setkHoverColor(new java.awt.Color(0, 78, 200));
        btnUpdateCategory.setkHoverEndColor(new java.awt.Color(0, 102, 153));
        btnUpdateCategory.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnUpdateCategory.setkHoverStartColor(new java.awt.Color(0, 102, 204));
        btnUpdateCategory.setkStartColor(new java.awt.Color(0, 153, 255));
        btnUpdateCategory.setPreferredSize(new java.awt.Dimension(80, 35));
        btnUpdateCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateCategoryActionPerformed(evt);
            }
        });
        pnFuntion.add(btnUpdateCategory);

        btnDeleteCategory.setText(bundle.getString("btDeleteCategory")); // NOI18N
        btnDeleteCategory.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnDeleteCategory.setkAllowGradient(false);
        btnDeleteCategory.setkBackGroundColor(new java.awt.Color(204, 0, 0));
        btnDeleteCategory.setkEndColor(new java.awt.Color(204, 0, 0));
        btnDeleteCategory.setkHoverColor(new java.awt.Color(0, 78, 200));
        btnDeleteCategory.setkHoverEndColor(new java.awt.Color(102, 0, 0));
        btnDeleteCategory.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnDeleteCategory.setkHoverStartColor(new java.awt.Color(102, 0, 0));
        btnDeleteCategory.setkStartColor(new java.awt.Color(204, 0, 51));
        btnDeleteCategory.setPreferredSize(new java.awt.Dimension(80, 35));
        btnDeleteCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteCategoryActionPerformed(evt);
            }
        });
        pnFuntion.add(btnDeleteCategory);

        btnRefreshCategory.setText(bundle.getString("btRefreshCategory")); // NOI18N
        btnRefreshCategory.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnRefreshCategory.setkAllowGradient(false);
        btnRefreshCategory.setkBackGroundColor(new java.awt.Color(204, 204, 0));
        btnRefreshCategory.setkEndColor(new java.awt.Color(204, 204, 0));
        btnRefreshCategory.setkHoverColor(new java.awt.Color(0, 78, 200));
        btnRefreshCategory.setkHoverEndColor(new java.awt.Color(153, 153, 0));
        btnRefreshCategory.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnRefreshCategory.setkHoverStartColor(new java.awt.Color(153, 153, 0));
        btnRefreshCategory.setkStartColor(new java.awt.Color(204, 204, 0));
        btnRefreshCategory.setPreferredSize(new java.awt.Dimension(80, 35));
        btnRefreshCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshCategoryActionPerformed(evt);
            }
        });
        pnFuntion.add(btnRefreshCategory);

        add(pnFuntion);

        pnMain.setBackground(new java.awt.Color(255, 255, 255));
        pnMain.setkFillBackground(false);
        pnMain.setPreferredSize(new java.awt.Dimension(1150, 600));
        pnMain.setLayout(new javax.swing.BoxLayout(pnMain, javax.swing.BoxLayout.X_AXIS));

        pnListCategory.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("CategoryList"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 18), new java.awt.Color(0, 153, 255))); // NOI18N
        pnListCategory.setForeground(new java.awt.Color(255, 255, 255));
        pnListCategory.setToolTipText("");
        pnListCategory.setkFillBackground(false);
        pnListCategory.setOpaque(false);
        pnListCategory.setPreferredSize(new java.awt.Dimension(750, 492));
        pnListCategory.setLayout(new javax.swing.BoxLayout(pnListCategory, javax.swing.BoxLayout.LINE_AXIS));

        TableList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                bundle.getString("categoryForm.table.categoryCode"),
                bundle.getString("categoryForm.table.categoryName"), 
                bundle.getString("categoryForm.table.description"),
                bundle.getString("categoryForm.table.status"), 
                bundle.getString("categoryForm.table.dateCreate")
            }
        ));
        scrollTB.setViewportView(TableList);

        pnListCategory.add(scrollTB);

        pnMain.add(pnListCategory);

        pnInform.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("CategoryInformation"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 18), new java.awt.Color(51, 153, 255))); // NOI18N
        pnInform.setkFillBackground(false);
        pnInform.setOpaque(false);
        pnInform.setPreferredSize(new java.awt.Dimension(400, 449));
        pnInform.setLayout(new javax.swing.BoxLayout(pnInform, javax.swing.BoxLayout.Y_AXIS));

        pnCategoryCode.setOpaque(false);
        pnCategoryCode.setPreferredSize(new java.awt.Dimension(240, 10));
        pnCategoryCode.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbCategoryCode.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbCategoryCode.setText(bundle.getString("lbCategoryCode")); // NOI18N
        pnCategoryCode.add(lbCategoryCode, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        txtCategoryCode.setPreferredSize(new java.awt.Dimension(175, 30));
        pnCategoryCode.add(txtCategoryCode, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 10, 230, -1));

        pnInform.add(pnCategoryCode);

        pnCategoryName.setOpaque(false);
        pnCategoryName.setPreferredSize(new java.awt.Dimension(314, 30));
        pnCategoryName.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbCategoryName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbCategoryName.setText(bundle.getString("lbCategoryName")); // NOI18N
        pnCategoryName.add(lbCategoryName, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        txtCategoryName.setPreferredSize(new java.awt.Dimension(175, 30));
        pnCategoryName.add(txtCategoryName, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, 230, -1));

        pnInform.add(pnCategoryName);

        pnDescription.setOpaque(false);
        pnDescription.setPreferredSize(new java.awt.Dimension(400, 200));
        pnDescription.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbDescription.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbDescription.setText(bundle.getString("lbDescription")); // NOI18N
        pnDescription.add(lbDescription, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        txtDescription.setColumns(20);
        txtDescription.setRows(5);
        jScrollPane1.setViewportView(txtDescription);

        pnDescription.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, -1, -1));

        pnInform.add(pnDescription);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbStatus.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbStatus.setText(bundle.getString("lbStatus")); // NOI18N
        jPanel1.add(lbStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        cbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { 
            bundle.getString("categoryForm.status.active"), 
            bundle.getString("categoryForm.status.inactive") 
        }));
        jPanel1.add(cbStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, -1, -1));

        pnInform.add(jPanel1);

        pnDateCreate.setOpaque(false);
        pnDateCreate.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbDateCreate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbDateCreate.setText(bundle.getString("lbDateCreate")); // NOI18N
        pnDateCreate.add(lbDateCreate, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, 40));
        pnDateCreate.add(txtDateCreate, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, 230, 30));

        pnInform.add(pnDateCreate);

        pnMain.add(pnInform);

        add(pnMain);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCategoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddCategoryActionPerformed

    private void btnUpdateCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateCategoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnUpdateCategoryActionPerformed

    private void btnDeleteCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteCategoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDeleteCategoryActionPerformed

    private void btnRefreshCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshCategoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRefreshCategoryActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TableList;
    private com.k33ptoo.components.KButton btnAddCategory;
    private com.k33ptoo.components.KButton btnDeleteCategory;
    private com.k33ptoo.components.KButton btnRefreshCategory;
    private com.k33ptoo.components.KButton btnUpdateCategory;
    private javax.swing.JComboBox<String> cbStatus;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbCategoryCode;
    private javax.swing.JLabel lbCategoryName;
    private javax.swing.JLabel lbDateCreate;
    private javax.swing.JLabel lbDescription;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel pnCategoryCode;
    private javax.swing.JPanel pnCategoryName;
    private javax.swing.JPanel pnDateCreate;
    private javax.swing.JPanel pnDescription;
    private com.k33ptoo.components.KGradientPanel pnFuntion;
    private com.k33ptoo.components.KGradientPanel pnInform;
    private com.k33ptoo.components.KGradientPanel pnListCategory;
    private com.k33ptoo.components.KGradientPanel pnMain;
    private com.k33ptoo.components.KGradientPanel pnTitile;
    private javax.swing.JScrollPane scrollTB;
    private javax.swing.JTextField txtCategoryCode;
    private javax.swing.JTextField txtCategoryName;
    private javax.swing.JLabel txtDateCreate;
    private javax.swing.JTextArea txtDescription;
    // End of variables declaration//GEN-END:variables

    // Các biến và phương thức khác...

    // Getter cho CategoryController
    public CategoryController getCategoryController() {
        return categoryController;

    }

    public JTable getTableList() {
        return TableList;
    }
    public com.k33ptoo.components.KButton getBtnAddCategory() {
        return btnAddCategory;
    }
    public com.k33ptoo.components.KButton getBtnUpdateCategory() {
        return btnUpdateCategory;
    }
    public com.k33ptoo.components.KButton getBtnDeleteCategory() {
        return btnDeleteCategory;
    }
    public com.k33ptoo.components.KButton getBtnRefreshCategory() {
        return btnRefreshCategory;
    }
    public JComboBox<String> getCbStatus() {
        return cbStatus;
    }
    public JTextField getTxtCategoryCode() {
        return txtCategoryCode;
    }
    public JTextField getTxtCategoryName() {
        return txtCategoryName;
    }
    public JTextArea getTxtDescription() {
        return txtDescription;
    }
    public JLabel getTxtDateCreate() {
        return txtDateCreate;
    }
}