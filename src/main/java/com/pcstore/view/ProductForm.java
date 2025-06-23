/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Panel;
import java.util.ResourceBundle;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;

import com.k33ptoo.components.KButton;
import com.pcstore.controller.ProductController;
import com.pcstore.model.Category;
import com.pcstore.model.Supplier;
import com.pcstore.utils.LocaleManager;

/**
 *
 * @author nloc2
 */
public class ProductForm extends javax.swing.JPanel {

    private ProductController controller;
    private JComboBox<Category> categoryComboBox;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KButton btnAdd;
    private com.k33ptoo.components.KButton btnDelete;
    private com.k33ptoo.components.KButton btnExportExcel;
    private javax.swing.JButton btnResetSort;
    private com.k33ptoo.components.KButton btnUpdate;
    private javax.swing.JComboBox<Category> cbbClassfication;
    private javax.swing.JComboBox<String> cbbSort;
    private javax.swing.JComboBox<String> cbbSortCustomer;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private com.k33ptoo.components.KGradientPanel kPanelSearch;
    private javax.swing.JLabel labelCostPrice;
    private javax.swing.JLabel labelDescription;
    private javax.swing.JLabel labelESC;
    private javax.swing.JLabel labelPrice;
    private javax.swing.JLabel labelProfit;
    private javax.swing.JLabel labelSepecification;
    private javax.swing.JLabel lableManufacturer;
    private javax.swing.JLabel lableProductID;
    private javax.swing.JLabel lbClassfication;
    private javax.swing.JLabel lbProductName;
    private javax.swing.JLabel lbSort;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel panelBody;
    private javax.swing.JPanel panelBtn;
    private javax.swing.JPanel panelClassfication;
    private javax.swing.JPanel panelCostPrice;
    private javax.swing.JPanel panelDescription;
    private javax.swing.JPanel panelDetails;
    private javax.swing.JPanel panelHeader;
    private com.k33ptoo.components.KGradientPanel panelListProduct;
    private javax.swing.JPanel panelManufacturer;
    private javax.swing.JPanel panelPrice;
    private javax.swing.JPanel panelProductID;
    private javax.swing.JPanel panelProductName;
    private javax.swing.JPanel panelProfit;
    private javax.swing.JPanel panelSepecification;
    private javax.swing.JPanel panelSort;
    private com.k33ptoo.components.KGradientPanel panelSortMain;
    private javax.swing.JPanel panelTitle;
    private javax.swing.JPanel pnDetail;
    private javax.swing.JTable tableListProduct;
    private com.pcstore.utils.TextFieldSearch textFieldSearch;
    private javax.swing.JTextField txtCostPrice;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtManufacturer;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtProductID;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtProfit;
    private javax.swing.JTextArea txtTechnicalSpecifications;
    // End of variables declaration//GEN-END:variables

    private KButton btnRefresh;
    
    public ProductForm() {
        initComponents();
        initComponentsV2();
        labelESC.setVisible(false);

        tableListProduct.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // 1. Khởi tạo combobox và các thành phần UI cơ bản
        categoryComboBox = (JComboBox<Category>) cbbClassfication;
        
        // 2. Thiết lập các thuộc tính cơ bản cho UI
        // Vô hiệu hóa trường ProductID
        txtProductID.setEditable(false);
        txtProductID.setBackground(new Color(240, 240, 240));
        
        // Thiết lập renderer cho combobox danh mục
        categoryComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category) {
                    setText(((Category) value).getCategoryName());
                }
                return this;
            }
        });
        
        // Cấu hình các TextArea
        txtTechnicalSpecifications.setLineWrap(true);
        txtTechnicalSpecifications.setWrapStyleWord(true);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        
        // Cần thiết để tránh các vấn đề về focus
        txtTechnicalSpecifications.setCaretPosition(0);
        txtDescription.setCaretPosition(0);

        txtProfit.setText(String.valueOf(LocaleManager.profitMargin * 100));

        // Đảm bảo form có thể nhận được sự kiện từ bàn phím
        setFocusable(true);
        
        // 3. Khởi tạo controller sau khi thiết lập UI
        controller = new ProductController(this);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTitle = new javax.swing.JPanel();
        lbTitle = new javax.swing.JLabel();
        panelHeader = new javax.swing.JPanel();
        kPanelSearch = new com.k33ptoo.components.KGradientPanel();
        textFieldSearch = new com.pcstore.utils.TextFieldSearch();
        panelSortMain = new com.k33ptoo.components.KGradientPanel();
        panelSort = new javax.swing.JPanel();
        lbSort = new javax.swing.JLabel();
        cbbSortCustomer = new javax.swing.JComboBox<>();
        cbbSort = new javax.swing.JComboBox<>();
        btnResetSort = new javax.swing.JButton();
        panelBtn = new javax.swing.JPanel();
        btnAdd = new com.k33ptoo.components.KButton();
        btnUpdate = new com.k33ptoo.components.KButton();
        btnDelete = new com.k33ptoo.components.KButton();
        btnExportExcel = new com.k33ptoo.components.KButton();
        panelBody = new javax.swing.JPanel();
        panelListProduct = new com.k33ptoo.components.KGradientPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableListProduct = new javax.swing.JTable();
        panelDetails = new javax.swing.JPanel();
        pnDetail = new javax.swing.JPanel();
        panelProductID = new javax.swing.JPanel();
        lableProductID = new javax.swing.JLabel();
        txtProductID = new javax.swing.JTextField();
        panelProductName = new javax.swing.JPanel();
        lbProductName = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        panelClassfication = new javax.swing.JPanel();
        lbClassfication = new javax.swing.JLabel();
        cbbClassfication = new javax.swing.JComboBox<>();
        panelManufacturer = new javax.swing.JPanel();
        lableManufacturer = new javax.swing.JLabel();
        txtManufacturer = new javax.swing.JTextField();
        panelCostPrice = new javax.swing.JPanel();
        labelCostPrice = new javax.swing.JLabel();
        txtCostPrice = new javax.swing.JTextField();
        panelPrice = new javax.swing.JPanel();
        labelPrice = new javax.swing.JLabel();
        txtPrice = new javax.swing.JTextField();
        panelProfit = new javax.swing.JPanel();
        labelProfit = new javax.swing.JLabel();
        txtProfit = new javax.swing.JTextField();
        panelSepecification = new javax.swing.JPanel();
        labelSepecification = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtTechnicalSpecifications = new javax.swing.JTextArea();
        panelDescription = new javax.swing.JPanel();
        labelDescription = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        labelESC = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(1153, 713));
        setPreferredSize(new java.awt.Dimension(1153, 713));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        panelTitle.setBackground(new java.awt.Color(255, 255, 255));
        panelTitle.setPreferredSize(new java.awt.Dimension(100, 30));
        panelTitle.setLayout(new java.awt.BorderLayout());

        lbTitle.setFont(new java.awt.Font("Segoe UI", 1, 25)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(0, 76, 192));
        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        lbTitle.setText(bundle.getString("txtMenuProduct")); // NOI18N
        lbTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbTitle.setMaximumSize(new java.awt.Dimension(32828, 1600000));
        lbTitle.setMinimumSize(new java.awt.Dimension(850, 34));
        lbTitle.setName(""); // NOI18N
        lbTitle.setPreferredSize(new java.awt.Dimension(900, 30));
        panelTitle.add(lbTitle, java.awt.BorderLayout.CENTER);

        add(panelTitle);

        panelHeader.setBackground(new java.awt.Color(153, 255, 0));
        panelHeader.setMaximumSize(new java.awt.Dimension(328791, 1000));
        panelHeader.setMinimumSize(new java.awt.Dimension(771, 50));
        panelHeader.setOpaque(false);
        panelHeader.setPreferredSize(new java.awt.Dimension(771, 50));
        panelHeader.setLayout(new javax.swing.BoxLayout(panelHeader, javax.swing.BoxLayout.LINE_AXIS));

        kPanelSearch.setkFillBackground(false);
        kPanelSearch.setMaximumSize(new java.awt.Dimension(100, 200));
        kPanelSearch.setMinimumSize(new java.awt.Dimension(100, 30));
        kPanelSearch.setOpaque(false);
        kPanelSearch.setPreferredSize(new java.awt.Dimension(660, 35));
        kPanelSearch.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        textFieldSearch.setPreferredSize(new java.awt.Dimension(650, 31));
        kPanelSearch.add(textFieldSearch);

        panelHeader.add(kPanelSearch);

        panelSortMain.setkFillBackground(false);
        panelSortMain.setOpaque(false);
        panelSortMain.setLayout(new java.awt.BorderLayout());

        panelSort.setBackground(new java.awt.Color(255, 255, 255));
        panelSort.setPreferredSize(new java.awt.Dimension(500, 70));

        lbSort.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lbSort.setText(bundle.getString("lbSort")); // NOI18N
        panelSort.add(lbSort);

        cbbSortCustomer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<Không>", "Giá Bán", "Phân Loại" }));
        cbbSortCustomer.setPreferredSize(new java.awt.Dimension(150, 30));
        panelSort.add(cbbSortCustomer);

        cbbSort.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<Không>", "Tăng dần", "Giảm giần" }));
        cbbSort.setPreferredSize(new java.awt.Dimension(100, 30));
        panelSort.add(cbbSort);

        btnResetSort.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/refresh.png"))); // NOI18N
        btnResetSort.setPreferredSize(new java.awt.Dimension(50, 25));
        panelSort.add(btnResetSort);

        panelSortMain.add(panelSort, java.awt.BorderLayout.PAGE_START);

        panelHeader.add(panelSortMain);

        add(panelHeader);

        panelBtn.setOpaque(false);
        panelBtn.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/plus.png"))); // NOI18N
        btnAdd.setText(bundle.getString("btnAddProduct")); // NOI18N
        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAdd.setIconTextGap(10);
        btnAdd.setkAllowGradient(false);
        btnAdd.setkBackGroundColor(new java.awt.Color(0, 179, 91));
        btnAdd.setkEndColor(new java.awt.Color(102, 153, 255));
        btnAdd.setkHoverColor(new java.awt.Color(0, 195, 75));
        btnAdd.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnAdd.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnAdd.setPreferredSize(new java.awt.Dimension(185, 40));
        panelBtn.add(btnAdd);

        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/refresh.png"))); // NOI18N
        btnUpdate.setText(bundle.getString("btnUpdate")); // NOI18N
        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUpdate.setkBackGroundColor(new java.awt.Color(102, 153, 255));
        btnUpdate.setkEndColor(new java.awt.Color(102, 153, 255));
        btnUpdate.setkHoverEndColor(new java.awt.Color(102, 102, 255));
        btnUpdate.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnUpdate.setkHoverStartColor(new java.awt.Color(153, 255, 154));
        btnUpdate.setPreferredSize(new java.awt.Dimension(185, 40));
        panelBtn.add(btnUpdate);

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/trash.png"))); // NOI18N
        btnDelete.setText(bundle.getString("btnDeleteFromListProduct")); // NOI18N
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDelete.setIconTextGap(10);
        btnDelete.setkAllowGradient(false);
        btnDelete.setkBackGroundColor(new java.awt.Color(255, 51, 51));
        btnDelete.setkEndColor(new java.awt.Color(255, 51, 51));
        btnDelete.setkHoverColor(new java.awt.Color(255, 85, 13));
        btnDelete.setkHoverEndColor(new java.awt.Color(255, 204, 204));
        btnDelete.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnDelete.setkHoverStartColor(new java.awt.Color(255, 51, 51));
        btnDelete.setkStartColor(new java.awt.Color(255, 204, 204));
        btnDelete.setPreferredSize(new java.awt.Dimension(150, 40));
        panelBtn.add(btnDelete);

        btnExportExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/xls.png"))); // NOI18N
        btnExportExcel.setText(bundle.getString("btnExport")); // NOI18N
        btnExportExcel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnExportExcel.setIconTextGap(10);
        btnExportExcel.setkAllowGradient(false);
        btnExportExcel.setkBackGroundColor(new java.awt.Color(0, 204, 102));
        btnExportExcel.setkEndColor(new java.awt.Color(0, 153, 153));
        btnExportExcel.setkHoverColor(new java.awt.Color(0, 158, 110));
        btnExportExcel.setkHoverEndColor(new java.awt.Color(0, 204, 204));
        btnExportExcel.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnExportExcel.setkHoverStartColor(new java.awt.Color(0, 204, 204));
        btnExportExcel.setkStartColor(new java.awt.Color(102, 255, 0));
        btnExportExcel.setPreferredSize(new java.awt.Dimension(150, 40));
        panelBtn.add(btnExportExcel);

        add(panelBtn);

        panelBody.setBackground(new java.awt.Color(255, 255, 255));
        panelBody.setBorder(javax.swing.BorderFactory.createEmptyBorder(21, 10, 21, 10));
        panelBody.setMinimumSize(new java.awt.Dimension(850, 612));
        panelBody.setPreferredSize(new java.awt.Dimension(870, 550));
        panelBody.setLayout(new javax.swing.BoxLayout(panelBody, javax.swing.BoxLayout.LINE_AXIS));

        panelListProduct.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 20), bundle.getString("tableListProduct"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(0, 76, 192))); // NOI18N
        panelListProduct.setkBorderRadius(20);
        panelListProduct.setkFillBackground(false);
        panelListProduct.setMinimumSize(new java.awt.Dimension(650, 600));
        panelListProduct.setOpaque(false);
        panelListProduct.setPreferredSize(new java.awt.Dimension(680, 600));
        panelListProduct.setLayout(new java.awt.BorderLayout());

        tableListProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã sản phẩm", "Tên sản phẩm", "Phân loại", "Hãng sản xuất", "Số lượng", "Giá vốn", "Giá bán tại cửa hàng", "Lợi nhuận"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableListProduct.setRowHeight(32);
        tableListProduct.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tableListProduct);
        if (tableListProduct.getColumnModel().getColumnCount() > 0) {
            tableListProduct.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("lbProductID")); // NOI18N
            tableListProduct.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("lbProductName")); // NOI18N
            tableListProduct.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("lbCategory")); // NOI18N
            tableListProduct.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("lbManufacturer")); // NOI18N
            tableListProduct.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("lbQuatity")); // NOI18N
            tableListProduct.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("lbCostPrice")); // NOI18N
            tableListProduct.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("lbPrice")); // NOI18N
            tableListProduct.getColumnModel().getColumn(7).setHeaderValue(bundle.getString("txtProfit")); // NOI18N
        }

        panelListProduct.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panelBody.add(panelListProduct);

        panelDetails.setBackground(new java.awt.Color(255, 255, 255));
        panelDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("titleDetailInformation"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(0, 76, 192))); // NOI18N
        panelDetails.setPreferredSize(new java.awt.Dimension(430, 430));
        panelDetails.setLayout(new java.awt.BorderLayout());

        pnDetail.setBackground(new java.awt.Color(255, 255, 255));
        pnDetail.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 5, 20, 5));
        pnDetail.setMinimumSize(new java.awt.Dimension(230, 400));
        pnDetail.setPreferredSize(new java.awt.Dimension(400, 400));
        pnDetail.setLayout(new java.awt.GridLayout(5, 2, 20, 25));

        panelProductID.setPreferredSize(new java.awt.Dimension(250, 50));
        panelProductID.setLayout(new java.awt.GridLayout(2, 0));

        lableProductID.setBackground(new java.awt.Color(255, 255, 255));
        lableProductID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lableProductID.setText(bundle.getString("lbProductID")); // NOI18N
        lableProductID.setOpaque(true);
        panelProductID.add(lableProductID);

        txtProductID.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtProductID.setForeground(new java.awt.Color(0, 76, 192));
        panelProductID.add(txtProductID);

        pnDetail.add(panelProductID);

        panelProductName.setPreferredSize(new java.awt.Dimension(250, 50));
        panelProductName.setLayout(new java.awt.GridLayout(2, 0));

        lbProductName.setBackground(new java.awt.Color(255, 255, 255));
        lbProductName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbProductName.setText(bundle.getString("lbProductName")); // NOI18N
        lbProductName.setOpaque(true);
        panelProductName.add(lbProductName);

        txtProductName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtProductName.setForeground(new java.awt.Color(0, 76, 192));
        panelProductName.add(txtProductName);

        pnDetail.add(panelProductName);

        panelClassfication.setPreferredSize(new java.awt.Dimension(250, 50));
        panelClassfication.setLayout(new java.awt.GridLayout(2, 0));

        lbClassfication.setBackground(new java.awt.Color(255, 255, 255));
        lbClassfication.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbClassfication.setText(bundle.getString("lbClassfication")); // NOI18N
        lbClassfication.setOpaque(true);
        panelClassfication.add(lbClassfication);

        cbbClassfication.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cbbClassfication.setForeground(new java.awt.Color(0, 76, 192));
        panelClassfication.add(cbbClassfication);

        pnDetail.add(panelClassfication);

        panelManufacturer.setPreferredSize(new java.awt.Dimension(250, 50));
        panelManufacturer.setLayout(new java.awt.GridLayout(2, 0));

        lableManufacturer.setBackground(new java.awt.Color(255, 255, 255));
        lableManufacturer.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lableManufacturer.setText(bundle.getString("lbManufacturer")); // NOI18N
        lableManufacturer.setOpaque(true);
        panelManufacturer.add(lableManufacturer);

        txtManufacturer.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtManufacturer.setForeground(new java.awt.Color(0, 76, 192));
        panelManufacturer.add(txtManufacturer);

        pnDetail.add(panelManufacturer);

        panelCostPrice.setPreferredSize(new java.awt.Dimension(250, 50));
        panelCostPrice.setLayout(new java.awt.GridLayout(2, 0));

        labelCostPrice.setBackground(new java.awt.Color(255, 255, 255));
        labelCostPrice.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        labelCostPrice.setText(bundle.getString("lbCostPrice")); // NOI18N
        labelCostPrice.setOpaque(true);
        panelCostPrice.add(labelCostPrice);

        txtCostPrice.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCostPrice.setForeground(new java.awt.Color(0, 76, 192));
        panelCostPrice.add(txtCostPrice);

        pnDetail.add(panelCostPrice);

        panelPrice.setPreferredSize(new java.awt.Dimension(250, 50));
        panelPrice.setLayout(new java.awt.GridLayout(2, 0));

        labelPrice.setBackground(new java.awt.Color(255, 255, 255));
        labelPrice.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        labelPrice.setText(bundle.getString("lbPrice")); // NOI18N
        labelPrice.setOpaque(true);
        panelPrice.add(labelPrice);

        txtPrice.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPrice.setForeground(new java.awt.Color(0, 76, 192));
        panelPrice.add(txtPrice);

        pnDetail.add(panelPrice);

        panelProfit.setPreferredSize(new java.awt.Dimension(250, 50));
        panelProfit.setLayout(new java.awt.GridLayout(2, 0));

        labelProfit.setBackground(new java.awt.Color(255, 255, 255));
        labelProfit.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        labelProfit.setText(bundle.getString("lbProfitRate")); // NOI18N
        labelProfit.setOpaque(true);
        panelProfit.add(labelProfit);

        txtProfit.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtProfit.setForeground(new java.awt.Color(0, 76, 192));
        panelProfit.add(txtProfit);

        pnDetail.add(panelProfit);

        panelSepecification.setAlignmentY(30.0F);
        panelSepecification.setMinimumSize(new java.awt.Dimension(200, 80));
        panelSepecification.setName(""); // NOI18N
        panelSepecification.setOpaque(false);
        panelSepecification.setPreferredSize(new java.awt.Dimension(300, 100));
        panelSepecification.setLayout(new javax.swing.BoxLayout(panelSepecification, javax.swing.BoxLayout.Y_AXIS));

        labelSepecification.setBackground(new java.awt.Color(255, 255, 255));
        labelSepecification.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        labelSepecification.setText(bundle.getString("lbSpecfication")); // NOI18N
        labelSepecification.setOpaque(true);
        panelSepecification.add(labelSepecification);

        jScrollPane4.setMinimumSize(new java.awt.Dimension(16, 50));

        txtTechnicalSpecifications.setColumns(20);
        txtTechnicalSpecifications.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtTechnicalSpecifications.setForeground(new java.awt.Color(0, 76, 192));
        txtTechnicalSpecifications.setRows(5);
        txtTechnicalSpecifications.setAutoscrolls(false);
        jScrollPane4.setViewportView(txtTechnicalSpecifications);

        panelSepecification.add(jScrollPane4);

        pnDetail.add(panelSepecification);
        panelSepecification.getAccessibleContext().setAccessibleDescription("");

        panelDescription.setMinimumSize(new java.awt.Dimension(200, 80));
        panelDescription.setOpaque(false);
        panelDescription.setPreferredSize(new java.awt.Dimension(300, 100));
        panelDescription.setLayout(new javax.swing.BoxLayout(panelDescription, javax.swing.BoxLayout.Y_AXIS));

        labelDescription.setBackground(new java.awt.Color(255, 255, 255));
        labelDescription.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        labelDescription.setText(bundle.getString("lbDetail")); // NOI18N
        labelDescription.setOpaque(true);
        labelDescription.setPreferredSize(new java.awt.Dimension(80, 20));
        panelDescription.add(labelDescription);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(16, 50));

        txtDescription.setColumns(20);
        txtDescription.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtDescription.setForeground(new java.awt.Color(0, 76, 192));
        txtDescription.setRows(5);
        jScrollPane2.setViewportView(txtDescription);

        panelDescription.add(jScrollPane2);

        pnDetail.add(panelDescription);

        labelESC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelESC.setForeground(new java.awt.Color(255, 0, 51));
        labelESC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/exclamation.png"))); // NOI18N
        labelESC.setText(bundle.getString("labelNoteESC")); // NOI18N
        pnDetail.add(labelESC);

        panelDetails.add(pnDetail, java.awt.BorderLayout.CENTER);

        panelBody.add(panelDetails);

        add(panelBody);
    }// </editor-fold>//GEN-END:initComponents

    private void initComponentsV2(){
        btnRefresh = new KButton();
        ResourceBundle bundle = LocaleManager.getInstance().getResourceBundle();
        // Thiết lập nút làm mới
        btnRefresh.setText(bundle.getString("btnRefresh"));
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setIcon(new ImageIcon(getClass().getResource("/com/pcstore/resources/icon/refresh.png")));
        btnRefresh.setkBorderRadius(30);
        btnRefresh.setkEndColor(new Color(0, 153, 255));
        btnRefresh.setkHoverEndColor(new Color(51, 153, 255));
        btnRefresh.setkHoverForeGround(new Color(255, 255, 255));
        btnRefresh.setkHoverStartColor(new Color(102, 204, 255));
        btnRefresh.setPreferredSize(new Dimension(120, 40));
        panelBtn.add(btnRefresh);
    }

    // Thêm các getter cho các thành phần giao diện
    public javax.swing.JTable getTable() {
        return tableListProduct;
    }

    public javax.swing.JTextField getIdField() {
        return txtProductID;  // ID
    }

    public javax.swing.JTextField getNameField() {
        return txtProductName;  // Name
    }

    public JComboBox<Category> getCategoryComboBox() {
        return categoryComboBox;  // Category
    }

    public JComboBox<Supplier> getSupplierComboBox() {
        return null;  // Supplier
    }

    public javax.swing.JTextField getTxtManufacturer() {
        return txtManufacturer;  // Manufacturer
    }   

    public javax.swing.JTextField getPriceField() {
        return txtPrice;  // Price
    }

    public javax.swing.JTextArea getSpecificationsArea() {
        return txtTechnicalSpecifications;  // Specifications
    }

    public javax.swing.JTextArea getDescriptionArea() {
        return txtDescription;  // Description
    }

    public com.pcstore.utils.TextFieldSearch getTextFieldSearch() {
        return textFieldSearch;
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
    public com.k33ptoo.components.KButton getBtnExport() {
        return btnExportExcel;
    }

    public javax.swing.JComboBox<String> getCbbSortCustomer() {
        return cbbSortCustomer;
    }

    public javax.swing.JComboBox<String> getCbbSort() {
        return cbbSort;
    }

    public javax.swing.JButton getBtnResetSort() {
        return btnResetSort;
    }

    public javax.swing.JPanel getDetailsPanel() {
        return panelDetails;
    }

    public javax.swing.JLabel getLabelESC() {
        return labelESC;
    }

    /**
     * Cập nhật dữ liệu cho bảng sản phẩm
     * @param data Dữ liệu hiển thị
     * @param columns Tên các cột
     */
    public void updateTable(Object[][] data, String[] columns) {
        tableListProduct.setModel(new javax.swing.table.DefaultTableModel(
            data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép sửa trực tiếp trên bảng
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4 || columnIndex == 5) {
                    return Integer.class;
                }
                return String.class;
            }
        });
    }

    /**
     * Reset form nhập liệu
     */
    public void resetForm() {
        txtProductName.setText(""); // Tên sản phẩm
        txtManufacturer.setText("0"); // Số lượng
        txtPrice.setText("0"); // Giá
        txtTechnicalSpecifications.setText(""); // Thông số kỹ thuật
        txtDescription.setText(""); // Mô tả
        txtProductID.setText(""); // ID
        txtCostPrice.setText(""); // Giá vốn
        txtProfit.setText(""); // Lợi nhuận
        
    }

    // Getter cho các trường mới
    public JTextField getTxtCostPrice() {
        return txtCostPrice;
    }

    public JTextField getTxtProfit() {
        return txtProfit;
    }

    public KButton getBtnRefresh() {
        return btnRefresh;
    }
    
    public javax.swing.JPanel getPanelSort(){
        return panelSort;
    }
}
