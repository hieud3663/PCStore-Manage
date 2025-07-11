/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import java.awt.*;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.pcstore.controller.InvoiceController;
import com.pcstore.utils.IActionButtonTableListener;
import com.pcstore.utils.TableActionCellComponent;
import com.pcstore.utils.TableUtils;
import com.pcstore.utils.TextFieldSearch;
import com.k33ptoo.components.*;
/**
 *
 * @author MSII
 */
public class InvoiceForm extends javax.swing.JPanel  {

   
    private InvoiceController controller;
    private TableRowSorter<TableModel> invoiceTableSorter;
    private TableRowSorter<TableModel> invoiceDetailTableSorter;
    
    private ResourceBundle bundle;

    private com.k33ptoo.components.KButton btnDeleteInvoice;
    private javax.swing.JButton btnExportExcel;
    private com.k33ptoo.components.KButton btnExportInvoice;
    private com.k33ptoo.components.KButton btnPaymentInvoice;
    private javax.swing.JScrollPane jScrollPaneDetail;
    private javax.swing.JScrollPane jScrollPaneInvoice;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel panelAction;
    private javax.swing.JPanel panelBody;
    private javax.swing.JPanel panelInovoiceDetail;
    private javax.swing.JPanel panelInvoice;
    private javax.swing.JPanel panelSearch;
    private javax.swing.JTable tableInvoice;
    private javax.swing.JTable tableInvoiceDetail;
    private com.pcstore.utils.TextFieldSearch textFieldSearch;
    private com.pcstore.utils.TextFieldSearch textFieldSearch1;
    
    public InvoiceForm() {
        initComponents();
      
        // setupSortableTables();
        initComponentsV2();

        initController();
    }
    
    

    private void initController() {
        try {
            // Create controller instance
            this.controller = InvoiceController.getInstance(this);
            
            // Initialize data
            this.controller.loadAllInvoices();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                    "Lỗi khi khởi tạo controller: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

   
    @SuppressWarnings("unchecked")
    private void initComponents() {

        textFieldSearch1 = new com.pcstore.utils.TextFieldSearch();
        lbTitle = new javax.swing.JLabel();
        panelSearch = new javax.swing.JPanel();
        textFieldSearch = new com.pcstore.utils.TextFieldSearch();
        btnExportExcel = new javax.swing.JButton();
        panelBody = new javax.swing.JPanel();
        panelInvoice = new javax.swing.JPanel();
        jScrollPaneInvoice = new javax.swing.JScrollPane();
        tableInvoice = new javax.swing.JTable();
        panelAction = new javax.swing.JPanel();
        btnExportInvoice = new com.k33ptoo.components.KButton();
        btnPaymentInvoice = new com.k33ptoo.components.KButton();
        btnDeleteInvoice = new com.k33ptoo.components.KButton();
        panelInovoiceDetail = new javax.swing.JPanel();
        jScrollPaneDetail = new javax.swing.JScrollPane();
        tableInvoiceDetail = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(1153, 676));
        setPreferredSize(new java.awt.Dimension(1153, 699));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        lbTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(30, 113, 195));
        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        lbTitle.setText(bundle.getString("txtMenuInvoice")); // NOI18N
        lbTitle.setFocusable(false);
        lbTitle.setPreferredSize(new java.awt.Dimension(1173, 50));
        add(lbTitle);

        panelSearch.setOpaque(false);
        panelSearch.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 50, 5));
        panelSearch.add(textFieldSearch);

        btnExportExcel.setBackground(new java.awt.Color(30, 113, 69));
        btnExportExcel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnExportExcel.setForeground(new java.awt.Color(255, 255, 255));
        btnExportExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/excel.png"))); // NOI18N
        btnExportExcel.setText(bundle.getString("btnExportExcel")); // NOI18N
        btnExportExcel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportExcelActionPerformed(evt);
            }
        });
        panelSearch.add(btnExportExcel);

        add(panelSearch);

        panelBody.setBackground(new java.awt.Color(255, 255, 255));
        panelBody.setMaximumSize(new java.awt.Dimension(99999999, 99999999));
        panelBody.setMinimumSize(new java.awt.Dimension(1137, 600));
        panelBody.setPreferredSize(new java.awt.Dimension(1137, 600));
        panelBody.setLayout(new java.awt.BorderLayout(0, 10));

        panelInvoice.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("txtInvoice"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 16), new java.awt.Color(30, 113, 195))); // NOI18N
        panelInvoice.setOpaque(false);
        panelInvoice.setLayout(new java.awt.BorderLayout());

        jScrollPaneInvoice.setBorder(null);
        jScrollPaneInvoice.setPreferredSize(new java.awt.Dimension(310, 369));

        tableInvoice.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "STT", "Mã hóa đơn", "Ngày tạo hóa đơn", "Nhân viên", "Tổng giảm giá", "Tổng tiền", "Phương thức thanh toán", "Khách hàng", "Số điện thoại", "Trạng thái", "Ghi chú"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableInvoice.setPreferredSize(new java.awt.Dimension(310, 369));
        tableInvoice.getTableHeader().setReorderingAllowed(false);
        jScrollPaneInvoice.setViewportView(tableInvoice);
        if (tableInvoice.getColumnModel().getColumnCount() > 0) {
            tableInvoice.getColumnModel().getColumn(0).setPreferredWidth(20);
            tableInvoice.getColumnModel().getColumn(0).setMaxWidth(20);
            tableInvoice.getColumnModel().getColumn(1).setPreferredWidth(60);
            tableInvoice.getColumnModel().getColumn(1).setMaxWidth(60);
            tableInvoice.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("txtNo")); // NOI18N
            tableInvoice.getColumnModel().getColumn(2).setPreferredWidth(80);
            tableInvoice.getColumnModel().getColumn(2).setMaxWidth(80);
            tableInvoice.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("txtInvoiceID")); // NOI18N
            tableInvoice.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("txtInvoiceCreateAt")); // NOI18N
            tableInvoice.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("txtInvoiceEmployee")); // NOI18N
            tableInvoice.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("txtInvoiceTotalDiscount")); // NOI18N
            tableInvoice.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("txtInvoiceTotalAmount")); // NOI18N
            tableInvoice.getColumnModel().getColumn(7).setHeaderValue(bundle.getString("txtInvoicePaymentMethod")); // NOI18N
            tableInvoice.getColumnModel().getColumn(8).setHeaderValue(bundle.getString("txtInvoiceCustomer")); // NOI18N
            tableInvoice.getColumnModel().getColumn(9).setHeaderValue(bundle.getString("txtPhonenumber")); // NOI18N
            tableInvoice.getColumnModel().getColumn(10).setHeaderValue(bundle.getString("txtInvoiceStatus")); // NOI18N
            tableInvoice.getColumnModel().getColumn(11).setPreferredWidth(80);
            tableInvoice.getColumnModel().getColumn(11).setMaxWidth(80);
            tableInvoice.getColumnModel().getColumn(11).setHeaderValue(bundle.getString("txtInvoiceNote")); // NOI18N
        }

        panelInvoice.add(jScrollPaneInvoice, java.awt.BorderLayout.CENTER);

        panelAction.setBackground(new java.awt.Color(255, 255, 255));
        panelAction.setPreferredSize(new java.awt.Dimension(100, 40));
        panelAction.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));

        btnExportInvoice.setText(bundle.getString("btnPrintInvoice")); // NOI18N
        btnExportInvoice.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnExportInvoice.setkBackGroundColor(new java.awt.Color(51, 204, 0));
        btnExportInvoice.setkBorderRadius(30);
        btnExportInvoice.setkEndColor(new java.awt.Color(51, 204, 0));
        btnExportInvoice.setkHoverEndColor(new java.awt.Color(0, 255, 51));
        btnExportInvoice.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnExportInvoice.setkHoverStartColor(new java.awt.Color(0, 153, 255));
        btnExportInvoice.setkStartColor(new java.awt.Color(51, 204, 0));
        btnExportInvoice.setPreferredSize(new java.awt.Dimension(185, 35));
        panelAction.add(btnExportInvoice);

        btnPaymentInvoice.setText(bundle.getString("btnPaymentInvoice")); // NOI18N
        btnPaymentInvoice.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnPaymentInvoice.setkBackGroundColor(new java.awt.Color(0, 204, 204));
        btnPaymentInvoice.setkBorderRadius(30);
        btnPaymentInvoice.setkEndColor(new java.awt.Color(0, 204, 204));
        btnPaymentInvoice.setkHoverEndColor(new java.awt.Color(0, 204, 204));
        btnPaymentInvoice.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnPaymentInvoice.setkHoverStartColor(new java.awt.Color(0, 153, 255));
        btnPaymentInvoice.setkStartColor(new java.awt.Color(0, 204, 204));
        btnPaymentInvoice.setPreferredSize(new java.awt.Dimension(185, 35));
        panelAction.add(btnPaymentInvoice);

        btnDeleteInvoice.setText(bundle.getString("btnDeleteInvoice")); // NOI18N
        btnDeleteInvoice.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDeleteInvoice.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDeleteInvoice.setkBorderRadius(30);
        btnDeleteInvoice.setkEndColor(new java.awt.Color(255, 51, 51));
        btnDeleteInvoice.setkHoverEndColor(new java.awt.Color(255, 102, 102));
        btnDeleteInvoice.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnDeleteInvoice.setkHoverStartColor(new java.awt.Color(255, 102, 102));
        btnDeleteInvoice.setkStartColor(new java.awt.Color(255, 51, 102));
        btnDeleteInvoice.setPreferredSize(new java.awt.Dimension(185, 35));
        panelAction.add(btnDeleteInvoice);

        panelInvoice.add(panelAction, java.awt.BorderLayout.PAGE_END);

        panelBody.add(panelInvoice, java.awt.BorderLayout.PAGE_START);

        panelInovoiceDetail.setBackground(new java.awt.Color(255, 255, 255));
        panelInovoiceDetail.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("InvoiceDetails"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 16), new java.awt.Color(30, 113, 195))); // NOI18N
        panelInovoiceDetail.setPreferredSize(new java.awt.Dimension(500, 120));
        panelInovoiceDetail.setLayout(new java.awt.BorderLayout());

        jScrollPaneDetail.setPreferredSize(new java.awt.Dimension(500, 120));

        tableInvoiceDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "STT", "Mã sản phẩm", "Tên sản phẩm", "Giá bán", "Số lượng", "Tổng tiền theo sản phẩm"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableInvoiceDetail.setPreferredSize(new java.awt.Dimension(500, 120));
        tableInvoiceDetail.getTableHeader().setReorderingAllowed(false);
        jScrollPaneDetail.setViewportView(tableInvoiceDetail);
        if (tableInvoiceDetail.getColumnModel().getColumnCount() > 0) {
            tableInvoiceDetail.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("txtNo")); // NOI18N
            tableInvoiceDetail.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("txtProductID")); // NOI18N
            tableInvoiceDetail.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("txtProductName")); // NOI18N
            tableInvoiceDetail.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("txtProductPrice")); // NOI18N
            tableInvoiceDetail.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("txtQuantity")); // NOI18N
            tableInvoiceDetail.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("txtInvoiceTotalAmountProduct")); // NOI18N
        }

        panelInovoiceDetail.add(jScrollPaneDetail, java.awt.BorderLayout.CENTER);

        panelBody.add(panelInovoiceDetail, java.awt.BorderLayout.CENTER);

        add(panelBody);
    }


    private void initComponentsV2(){

        // setupCusmizeTable();

        panelBody.removeAll();
        
        jScrollPaneInvoice.setPreferredSize(null);
        tableInvoice.setPreferredSize(null);

        setupCusmizeTable();

        // tách pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(panelInvoice);
        splitPane.setBottomComponent(panelInovoiceDetail);
        splitPane.setResizeWeight(0.6); 

       
        int initialHeight = panelBody.getHeight();
        if (initialHeight > 0) {
            splitPane.setDividerLocation((int)(initialHeight * 0.6)); 
        } else {
           
            splitPane.setDividerLocation(360); 
        }

        splitPane.setOneTouchExpandable(false); 
        splitPane.setEnabled(false); 
        
        panelBody.add(splitPane, BorderLayout.CENTER);

        panelBody.revalidate();
        panelBody.repaint();
        // setupCusmizeHeaderTable();
    }

    private void setupCusmizeTable(){
        // Áp dụng kiểu dáng bảng từ TableStyleUtil
        invoiceTableSorter = TableUtils.applyInvoiceTableStyle(tableInvoice, 10);

        invoiceDetailTableSorter = TableUtils.applyDefaultStyle(tableInvoiceDetail);
        
        TableUtils.setBooleanColumns(tableInvoice, 0); // Cột checkbox
        TableUtils.disableSortingForColumns(invoiceTableSorter, 0);
        
        // Thiết lập kích thước cột
        if (tableInvoice.getColumnModel().getColumnCount() > 0) {
            TableUtils.setupColumnWidths(tableInvoice, 20, 60, 80);
        }
    }

   
    private void btnExportExcelActionPerformed(java.awt.event.ActionEvent evt) {
        
    }

    
    public JTable getTableInvoice() {
        return tableInvoice;
    }


    public void setTableInvoice(JTable tableInvoice) {
        this.tableInvoice = tableInvoice;
    }


    public javax.swing.JTable getTableInvoiceDetail() {
        return tableInvoiceDetail;
    }


    public void setTableInvoiceDetail(JTable tableInvoiceDetail) {
        this.tableInvoiceDetail = tableInvoiceDetail;
    }



    public com.k33ptoo.components.KButton getBtnDeleteInvoice() {
        return btnDeleteInvoice;
    }


    public void setBtnDeleteInvoice(com.k33ptoo.components.KButton btnDeleteInvoice) {
        this.btnDeleteInvoice = btnDeleteInvoice;
    }


    public javax.swing.JButton getBtnExportExcel() {
        return btnExportExcel;
    }


    public TextFieldSearch getTextFieldSearch() {
        return textFieldSearch;
    }

    public JTextField getTxtSearchField(){
        return textFieldSearch.getTxtSearchField();
    }

    public KButton getBbtnSearch() {
        return textFieldSearch.getBtnSearch();
    }


    public void setTextFieldSearch(com.pcstore.utils.TextFieldSearch textFieldSearch) {
        this.textFieldSearch = textFieldSearch;
    }


    public void setBtnExportExcel(javax.swing.JButton btnExportExcel) {
        this.btnExportExcel = btnExportExcel;
    }


    public KButton getBtnExportInvoice() {
        return btnExportInvoice;
    }


    public void setBtnExportInvoice(KButton btnExportInvoice) {
        this.btnExportInvoice = btnExportInvoice;
    }

    public KButton getBtnPaymentInvoice() {
        return btnPaymentInvoice;
    }

    public TableRowSorter<TableModel> getInvoiceTableSorter() {
        return invoiceTableSorter;
    }


    public TableRowSorter<TableModel> getInvoiceDetailTableSorter() {
        return invoiceDetailTableSorter;
    }

}
