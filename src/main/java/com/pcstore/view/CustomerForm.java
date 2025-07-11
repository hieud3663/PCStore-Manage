/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.pcstore.controller.CustomerController;
import com.pcstore.utils.TableUtils;

public class CustomerForm extends javax.swing.JPanel {


    private TableRowSorter<TableModel> tableListCustomerSorter;
    private CustomerController customerController;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KButton btnAdd;
    private com.k33ptoo.components.KButton btnDelete;
    private com.k33ptoo.components.KButton btnExportExcel;
    private com.k33ptoo.components.KButton btnRefresh;
    private javax.swing.JButton btnResetSort;
    private com.k33ptoo.components.KButton btnUpdate;
    private javax.swing.JComboBox<String> cbbSort;
    private javax.swing.JComboBox<String> cbbSortCustomer;
    private javax.swing.JLabel labeCreateUpdate;
    private javax.swing.JLabel labelCreateAt;
    private javax.swing.JLabel labelCustmerPhone;
    private javax.swing.JLabel labelCustomerEmail;
    private javax.swing.JLabel labelCustomerID;
    private javax.swing.JLabel labelCustomerName;
    private javax.swing.JLabel labelCustomerPoint;
    private javax.swing.JLabel labelESC;
    private javax.swing.JLabel labelSort;
    private javax.swing.JLabel lableTitle;
    private javax.swing.JLabel lbNote;
    private javax.swing.JPanel panelAction;
    private javax.swing.JPanel panelCreateAt;
    private javax.swing.JPanel panelCreateUpdate;
    private javax.swing.JPanel panelCustomerEmail;
    private javax.swing.JPanel panelCustomerGender;
    private javax.swing.JPanel panelCustomerID;
    private javax.swing.JPanel panelCustomerName;
    private javax.swing.JPanel panelCustomerPhone;
    private javax.swing.JPanel panelCustomerPoint;
    private javax.swing.JPanel panelEmpty;
    private javax.swing.JPanel panelExport;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelInfoDetail;
    private javax.swing.JPanel panelListCustomer;
    private javax.swing.JPanel panelNote;
    private javax.swing.JPanel panelSort;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tableCustomers;
    private com.pcstore.utils.TextFieldSearch textFieldSearch;
    private javax.swing.JTextField txtCreateAt;
    private javax.swing.JTextField txtCreateUpdate;
    private javax.swing.JTextField txtCustomerEmail;
    private javax.swing.JTextField txtCustomerID;
    private javax.swing.JTextField txtCustomerName;
    private javax.swing.JTextField txtCustomerPhone;
    private javax.swing.JTextField txtCustomerPoint;
    // End of variables declaration//GEN-END:variables
    
    public CustomerForm() {
        initComponents();
        setupCusmizeTable();
        customerController = new CustomerController(this);  
        labelESC.setVisible(false);  
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelHeader = new javax.swing.JPanel();
        lableTitle = new javax.swing.JLabel();
        panelAction = new javax.swing.JPanel();
        btnAdd = new com.k33ptoo.components.KButton();
        btnUpdate = new com.k33ptoo.components.KButton();
        btnDelete = new com.k33ptoo.components.KButton();
        btnRefresh = new com.k33ptoo.components.KButton();
        panelExport = new javax.swing.JPanel();
        btnExportExcel = new com.k33ptoo.components.KButton();
        panelInfoDetail = new javax.swing.JPanel();
        panelCustomerID = new javax.swing.JPanel();
        labelCustomerID = new javax.swing.JLabel();
        txtCustomerID = new javax.swing.JTextField();
        panelCustomerPhone = new javax.swing.JPanel();
        labelCustmerPhone = new javax.swing.JLabel();
        txtCustomerPhone = new javax.swing.JTextField();
        panelCreateUpdate = new javax.swing.JPanel();
        labeCreateUpdate = new javax.swing.JLabel();
        txtCreateUpdate = new javax.swing.JTextField();
        panelCustomerName = new javax.swing.JPanel();
        labelCustomerName = new javax.swing.JLabel();
        txtCustomerName = new javax.swing.JTextField();
        panelCustomerEmail = new javax.swing.JPanel();
        labelCustomerEmail = new javax.swing.JLabel();
        txtCustomerEmail = new javax.swing.JTextField();
        panelCreateAt = new javax.swing.JPanel();
        labelCreateAt = new javax.swing.JLabel();
        txtCreateAt = new javax.swing.JTextField();
        panelCustomerPoint = new javax.swing.JPanel();
        labelCustomerPoint = new javax.swing.JLabel();
        txtCustomerPoint = new javax.swing.JTextField();
        panelCustomerGender = new javax.swing.JPanel();
        panelEmpty = new javax.swing.JPanel();
        labelESC = new javax.swing.JLabel();
        panelSort = new javax.swing.JPanel();
        textFieldSearch = new com.pcstore.utils.TextFieldSearch();
        labelSort = new javax.swing.JLabel();
        cbbSortCustomer = new javax.swing.JComboBox<>();
        cbbSort = new javax.swing.JComboBox<>();
        btnResetSort = new javax.swing.JButton();
        panelNote = new javax.swing.JPanel();
        lbNote = new javax.swing.JLabel();
        panelListCustomer = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        tableCustomers = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1197, 713));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        panelHeader.setOpaque(false);
        panelHeader.setPreferredSize(new java.awt.Dimension(100, 40));
        panelHeader.setLayout(new java.awt.BorderLayout());

        lableTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lableTitle.setForeground(new java.awt.Color(30, 113, 195));
        lableTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        lableTitle.setText(bundle.getString("lbMenuCustomer")); // NOI18N
        panelHeader.add(lableTitle, java.awt.BorderLayout.CENTER);

        add(panelHeader);

        panelAction.setOpaque(false);
        panelAction.setPreferredSize(new java.awt.Dimension(1197, 60));
        panelAction.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/user-add_2.png"))); // NOI18N
        btnAdd.setText(bundle.getString("btnAddCustomer")); // NOI18N
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
        btnDelete.setText(bundle.getString("btnDeleteCustomer")); // NOI18N
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnDelete.setIconTextGap(5);
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

        panelExport.setBackground(new java.awt.Color(255, 255, 255));
        panelExport.setOpaque(false);
        panelExport.setPreferredSize(new java.awt.Dimension(350, 35));
        panelExport.setLayout(new java.awt.BorderLayout());

        btnExportExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/xls.png"))); // NOI18N
        btnExportExcel.setText(bundle.getString("btnExportExcel")); // NOI18N
        btnExportExcel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnExportExcel.setkAllowGradient(false);
        btnExportExcel.setkBackGroundColor(new java.awt.Color(26, 176, 114));
        btnExportExcel.setkHoverColor(new java.awt.Color(26, 204, 89));
        btnExportExcel.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnExportExcel.setPreferredSize(new java.awt.Dimension(120, 45));
        panelExport.add(btnExportExcel, java.awt.BorderLayout.LINE_END);

        panelAction.add(panelExport);

        add(panelAction);

        panelInfoDetail.setBackground(new java.awt.Color(255, 255, 255));
        panelInfoDetail.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("txtDetails"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(30, 113, 195))); // NOI18N
        panelInfoDetail.setPreferredSize(new java.awt.Dimension(400, 200));
        panelInfoDetail.setLayout(new java.awt.GridLayout(4, 3, 30, 23));

        panelCustomerID.setOpaque(false);
        panelCustomerID.setLayout(new java.awt.BorderLayout(15, 0));

        labelCustomerID.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labelCustomerID.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelCustomerID.setText(bundle.getString("txtCustomerID")); // NOI18N
        labelCustomerID.setPreferredSize(new java.awt.Dimension(120, 16));
        panelCustomerID.add(labelCustomerID, java.awt.BorderLayout.LINE_START);

        txtCustomerID.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtCustomerID.setForeground(new java.awt.Color(30, 113, 195));
        txtCustomerID.setBorder(null);
        txtCustomerID.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCustomerID.setEnabled(false);
        txtCustomerID.setOpaque(true);
        txtCustomerID.setPreferredSize(new java.awt.Dimension(100, 2));
        panelCustomerID.add(txtCustomerID, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelCustomerID);

        panelCustomerPhone.setOpaque(false);
        panelCustomerPhone.setLayout(new java.awt.BorderLayout(15, 0));

        labelCustmerPhone.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labelCustmerPhone.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelCustmerPhone.setText(bundle.getString("lbPhoneNumber")); // NOI18N
        labelCustmerPhone.setPreferredSize(new java.awt.Dimension(120, 16));
        panelCustomerPhone.add(labelCustmerPhone, java.awt.BorderLayout.LINE_START);

        txtCustomerPhone.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCustomerPhone.setForeground(new java.awt.Color(30, 113, 195));
        txtCustomerPhone.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCustomerPhone.setOpaque(true);
        txtCustomerPhone.setPreferredSize(new java.awt.Dimension(100, 2));
        panelCustomerPhone.add(txtCustomerPhone, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelCustomerPhone);

        panelCreateUpdate.setOpaque(false);
        panelCreateUpdate.setLayout(new java.awt.BorderLayout(15, 0));

        labeCreateUpdate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labeCreateUpdate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labeCreateUpdate.setText(bundle.getString("lbCreateUpdate")); // NOI18N
        labeCreateUpdate.setPreferredSize(new java.awt.Dimension(120, 16));
        panelCreateUpdate.add(labeCreateUpdate, java.awt.BorderLayout.LINE_START);

        txtCreateUpdate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCreateUpdate.setForeground(new java.awt.Color(30, 113, 195));
        txtCreateUpdate.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCreateUpdate.setEnabled(false);
        txtCreateUpdate.setOpaque(true);
        txtCreateUpdate.setPreferredSize(new java.awt.Dimension(100, 2));
        panelCreateUpdate.add(txtCreateUpdate, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelCreateUpdate);

        panelCustomerName.setOpaque(false);
        panelCustomerName.setLayout(new java.awt.BorderLayout(15, 0));

        labelCustomerName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labelCustomerName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelCustomerName.setText(bundle.getString("lbName")); // NOI18N
        labelCustomerName.setPreferredSize(new java.awt.Dimension(120, 16));
        panelCustomerName.add(labelCustomerName, java.awt.BorderLayout.LINE_START);

        txtCustomerName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCustomerName.setForeground(new java.awt.Color(30, 113, 195));
        txtCustomerName.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCustomerName.setOpaque(true);
        txtCustomerName.setPreferredSize(new java.awt.Dimension(100, 2));
        panelCustomerName.add(txtCustomerName, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelCustomerName);

        panelCustomerEmail.setOpaque(false);
        panelCustomerEmail.setLayout(new java.awt.BorderLayout(15, 0));

        labelCustomerEmail.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labelCustomerEmail.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelCustomerEmail.setText(bundle.getString("lbEmail")); // NOI18N
        labelCustomerEmail.setPreferredSize(new java.awt.Dimension(120, 16));
        panelCustomerEmail.add(labelCustomerEmail, java.awt.BorderLayout.LINE_START);

        txtCustomerEmail.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCustomerEmail.setForeground(new java.awt.Color(30, 113, 195));
        txtCustomerEmail.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCustomerEmail.setOpaque(true);
        txtCustomerEmail.setPreferredSize(new java.awt.Dimension(100, 2));
        panelCustomerEmail.add(txtCustomerEmail, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelCustomerEmail);

        panelCreateAt.setOpaque(false);
        panelCreateAt.setLayout(new java.awt.BorderLayout(15, 0));

        labelCreateAt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labelCreateAt.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelCreateAt.setText(bundle.getString("lbCreateAt")); // NOI18N
        labelCreateAt.setPreferredSize(new java.awt.Dimension(120, 16));
        panelCreateAt.add(labelCreateAt, java.awt.BorderLayout.LINE_START);

        txtCreateAt.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCreateAt.setForeground(new java.awt.Color(30, 113, 195));
        txtCreateAt.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCreateAt.setEnabled(false);
        txtCreateAt.setOpaque(true);
        txtCreateAt.setPreferredSize(new java.awt.Dimension(100, 2));
        panelCreateAt.add(txtCreateAt, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelCreateAt);

        panelCustomerPoint.setOpaque(false);
        panelCustomerPoint.setLayout(new java.awt.BorderLayout(15, 0));

        labelCustomerPoint.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labelCustomerPoint.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelCustomerPoint.setText(bundle.getString("lbPoint")); // NOI18N
        labelCustomerPoint.setPreferredSize(new java.awt.Dimension(120, 16));
        panelCustomerPoint.add(labelCustomerPoint, java.awt.BorderLayout.LINE_START);

        txtCustomerPoint.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtCustomerPoint.setForeground(new java.awt.Color(30, 113, 195));
        txtCustomerPoint.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCustomerPoint.setOpaque(true);
        txtCustomerPoint.setPreferredSize(new java.awt.Dimension(100, 2));
        panelCustomerPoint.add(txtCustomerPoint, java.awt.BorderLayout.CENTER);

        panelInfoDetail.add(panelCustomerPoint);

        panelCustomerGender.setOpaque(false);
        panelCustomerGender.setLayout(new java.awt.BorderLayout(15, 0));
        panelInfoDetail.add(panelCustomerGender);

        panelEmpty.setOpaque(false);
        panelEmpty.setLayout(new java.awt.BorderLayout(15, 0));

        labelESC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelESC.setForeground(new java.awt.Color(255, 0, 51));
        labelESC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/exclamation.png"))); // NOI18N
        labelESC.setText(bundle.getString("labelNoteESC")); // NOI18N
        panelEmpty.add(labelESC, java.awt.BorderLayout.PAGE_END);

        panelInfoDetail.add(panelEmpty);

        add(panelInfoDetail);

        panelSort.setBackground(new java.awt.Color(255, 255, 255));
        panelSort.setPreferredSize(new java.awt.Dimension(1197, 70));
        panelSort.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 20));

        textFieldSearch.setPreferredSize(new java.awt.Dimension(450, 31));
        panelSort.add(textFieldSearch);

        labelSort.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labelSort.setText(bundle.getString("lbSort")); // NOI18N
        panelSort.add(labelSort);

        cbbSortCustomer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<Không>", "Tên khách hàng", "Điểm" }));
        cbbSortCustomer.setPreferredSize(new java.awt.Dimension(150, 30));
        panelSort.add(cbbSortCustomer);

        cbbSort.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<Không>", "Tăng dần", "Giảm giần" }));
        cbbSort.setPreferredSize(new java.awt.Dimension(100, 30));
        panelSort.add(cbbSort);

        btnResetSort.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/refresh.png"))); // NOI18N
        btnResetSort.setPreferredSize(new java.awt.Dimension(50, 25));
        btnResetSort.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnResetSortMouseClicked(evt);
            }
        });
        panelSort.add(btnResetSort);

        add(panelSort);

        panelNote.setBackground(new java.awt.Color(255, 255, 255));
        panelNote.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lbNote.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lbNote.setText(bundle.getString("lbNoteCustomer")); // NOI18N
        panelNote.add(lbNote);

        add(panelNote);

        panelListCustomer.setBackground(new java.awt.Color(255, 255, 255));
        panelListCustomer.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), bundle.getString("txtListCustomer"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(30, 113, 195))); // NOI18N
        panelListCustomer.setMinimumSize(new java.awt.Dimension(1197, 200));
        panelListCustomer.setOpaque(false);
        panelListCustomer.setPreferredSize(new java.awt.Dimension(1197, 400));
        panelListCustomer.setLayout(new java.awt.BorderLayout());

        scrollPane.setMinimumSize(new java.awt.Dimension(1197, 200));
        scrollPane.setName(""); // NOI18N
        scrollPane.setViewportView(tableCustomers);

        tableCustomers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Mã khách hàng", "Tên khách hàng", "Số điện thoại", "Email", "Tích điểm", "Thời gian tạo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableCustomers.setMinimumSize(new java.awt.Dimension(1197, 400));
        tableCustomers.getTableHeader().setReorderingAllowed(false);
        scrollPane.setViewportView(tableCustomers);
        if (tableCustomers.getColumnModel().getColumnCount() > 0) {
            tableCustomers.getColumnModel().getColumn(0).setPreferredWidth(10);
            tableCustomers.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("txtCustomerID")); // NOI18N
            tableCustomers.getColumnModel().getColumn(1).setPreferredWidth(30);
            tableCustomers.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("txtCustomerName")); // NOI18N
            tableCustomers.getColumnModel().getColumn(2).setPreferredWidth(10);
            tableCustomers.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("txtPhonenumber")); // NOI18N
            tableCustomers.getColumnModel().getColumn(3).setPreferredWidth(20);
            tableCustomers.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("txtCustomerPoint")); // NOI18N
            tableCustomers.getColumnModel().getColumn(5).setPreferredWidth(20);
            tableCustomers.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("txtCreateAt")); // NOI18N
        }

        panelListCustomer.add(scrollPane, java.awt.BorderLayout.CENTER);

        add(panelListCustomer);
    }// </editor-fold>//GEN-END:initComponents

    private void setupCusmizeTable(){
        tableListCustomerSorter = TableUtils.applyDefaultStyle(tableCustomers);

        //Thiết lập kích thước cột

    }

    private void btnResetSortMouseClicked(java.awt.event.MouseEvent evt) {
        cbbSortCustomer.setSelectedIndex(0);
        cbbSort.setSelectedIndex(0);
    }

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {
        
    }

    
    public TableRowSorter<TableModel> getTableListCustomerSorter() {
        return tableListCustomerSorter;
    }

    public com.k33ptoo.components.KButton getBtnAddCustomer() {
        return btnAdd;
    }

    public com.k33ptoo.components.KButton getBtnDeleteCustomer() {
        return btnDelete;
    }

    public javax.swing.JButton getBtnExportExcel() {
        return btnExportExcel;
    }

    public com.k33ptoo.components.KButton getBtnRefresh() {
        return btnRefresh;
    }

    public javax.swing.JButton getBtnResetSort() {
        return btnResetSort;
    }

    public com.k33ptoo.components.KButton getBtnUpdate() {
        return btnUpdate;
    }

    public javax.swing.JComboBox<String> getCbbSort() {
        return cbbSort;
    }

    public javax.swing.JComboBox<String> getCbbSortCustomer() {
        return cbbSortCustomer;
    }

    public javax.swing.JTable getTableCustomers() {
        return tableCustomers;
    }

    public com.pcstore.utils.TextFieldSearch getTextFieldSearch() {
        return textFieldSearch;
    }

    public javax.swing.JTextField getTxtCreateAt() {
        return txtCreateAt;
    }

    public javax.swing.JTextField getTxtCreateUpdate() {
        return txtCreateUpdate;
    }

    public javax.swing.JTextField getTxtCustomerEmail() {
        return txtCustomerEmail;
    }


    public javax.swing.JTextField getTxtCustomerID() {
        return txtCustomerID;
    }

    public javax.swing.JTextField getTxtCustomerName() {
        return txtCustomerName;
    }

    public javax.swing.JTextField getTxtCustomerPhone() {
        return txtCustomerPhone;
    }

    public javax.swing.JTextField getTxtCustomerPoint() {
        return txtCustomerPoint;
    }

    
    public javax.swing.JLabel getLabelESC() {
        return labelESC;
    }
   
}
