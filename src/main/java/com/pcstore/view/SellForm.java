/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.formdev.flatlaf.FlatClientProperties;
import com.pcstore.components.SwitchButton;
import com.pcstore.controller.SellController;
import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.Product;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.SessionManager;
import com.pcstore.utils.TableUtils;

/**
 * @author MSII
 */
public class SellForm extends JPanel {


    private SellController sellController;
    private Employee employee;

    private List<String> listSelectProductIDs; // Danh sách tất cả sản phẩm

    private TableRowSorter<TableModel> tableListProductSorter;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KButton btnApplyVoucher;
    private javax.swing.JButton btnDeleteItemCart;
    private com.k33ptoo.components.KButton btnPay;
    private com.k33ptoo.components.KButton btnReset;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel labelCustomerName;
    private javax.swing.JLabel labelCustomerPhone;
    private javax.swing.JLabel labelCustomerPoint;
    private javax.swing.JLabel labelCustomerTotalAmonut;
    private javax.swing.JLabel labelDiscount;
    private javax.swing.JLabel labelTitle;
    private javax.swing.JLabel lbNote;
    private javax.swing.JPanel paneBody;
    private javax.swing.JPanel panelButtonAction;
    private com.k33ptoo.components.KGradientPanel panelCart;
    private javax.swing.JPanel panelCustomerDiscount;
    private com.k33ptoo.components.KGradientPanel panelCustomerInfo;
    private javax.swing.JPanel panelCustomerName;
    private javax.swing.JPanel panelCustomerPhone;
    private javax.swing.JPanel panelCustomerPoint;
    private javax.swing.JPanel panelCustomerTotalAmount;
    private javax.swing.JPanel panelDelete;
    private javax.swing.JPanel panelListProduct;
    private com.k33ptoo.components.KGradientPanel panelMain;
    private javax.swing.JPanel panelNav;
    private javax.swing.JPanel panelNote;
    private javax.swing.JPanel panelRight;
    private com.k33ptoo.components.KGradientPanel panelTitle;
    private javax.swing.JTable tableCart;
    private javax.swing.JTable tableListProduct;
    private com.pcstore.utils.TextFieldSearch textFieldSearch;
    private javax.swing.JTextField txtDiscountAmount;
    private javax.swing.JTextField txtNameKH;
    private javax.swing.JTextField txtPhoneNumberKH;
    private javax.swing.JTextField txtPointKH;
    private javax.swing.JTextField txtTotalAmount;
    // End of variables declaration//GEN-END:variables

    private SwitchButton sbtnUsePoint;
    private ResourceBundle bundle = LocaleManager.getInstance().getResourceBundle();
    // private TextFieldSearch textFieldSearch = new TextFieldSearch();


    /**
     * Creates new form Sell
     */

    public SellForm() {
        listSelectProductIDs = new ArrayList<>();

        initComponents();
        initComponentsV2();

        btnDeleteItemCart.setCursor(new Cursor(Cursor.HAND_CURSOR));

        setupCusmizeTable();

        try {

            sellController = new SellController(this);

            // Khởi tạo một giao dịch mới với ID nhân viên hiện tại
            Employee employee = SessionManager.getInstance().getCurrentUser().getEmployee();

            sellController.initializeSale(employee);

            updateCartDisplay();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, ErrorMessage.FORM_INIT_ERROR.toString().formatted(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
        }

        //sự kiện tìm kiếm
        textFieldSearch.getBtnSearch().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchProducts();
            }
        });
        textFieldSearch.getTxtSearchField().addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchProducts();
            }
        });

        employee = SessionManager.getInstance().getCurrentUser().getEmployee();
        resetSaleForm();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMain = new com.k33ptoo.components.KGradientPanel();
        panelTitle = new com.k33ptoo.components.KGradientPanel();
        labelTitle = new javax.swing.JLabel();
        panelNav = new javax.swing.JPanel();
        textFieldSearch = new com.pcstore.utils.TextFieldSearch();
        panelNote = new javax.swing.JPanel();
        lbNote = new javax.swing.JLabel();
        paneBody = new javax.swing.JPanel();
        panelListProduct = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableListProduct = new javax.swing.JTable();
        panelRight = new javax.swing.JPanel();
        panelCart = new com.k33ptoo.components.KGradientPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableCart = new javax.swing.JTable();
        panelDelete = new javax.swing.JPanel();
        btnDeleteItemCart = new javax.swing.JButton();
        panelCustomerInfo = new com.k33ptoo.components.KGradientPanel();
        panelCustomerName = new javax.swing.JPanel();
        labelCustomerName = new javax.swing.JLabel();
        txtNameKH = new javax.swing.JTextField();
        panelCustomerPoint = new javax.swing.JPanel();
        labelCustomerPoint = new javax.swing.JLabel();
        txtPointKH = new javax.swing.JTextField();
        panelCustomerPhone = new javax.swing.JPanel();
        labelCustomerPhone = new javax.swing.JLabel();
        txtPhoneNumberKH = new javax.swing.JTextField();
        btnApplyVoucher = new com.k33ptoo.components.KButton();
        panelCustomerDiscount = new javax.swing.JPanel();
        labelDiscount = new javax.swing.JLabel();
        txtDiscountAmount = new javax.swing.JTextField();
        panelCustomerTotalAmount = new javax.swing.JPanel();
        labelCustomerTotalAmonut = new javax.swing.JLabel();
        txtTotalAmount = new javax.swing.JTextField();
        panelButtonAction = new javax.swing.JPanel();
        btnReset = new com.k33ptoo.components.KButton();
        btnPay = new com.k33ptoo.components.KButton();

        setLayout(new java.awt.BorderLayout());

        panelMain.setBackground(new java.awt.Color(255, 255, 255));
        panelMain.setAlignmentX(2.0F);
        panelMain.setAlignmentY(2.0F);
        panelMain.setkEndColor(new java.awt.Color(153, 255, 153));
        panelMain.setkFillBackground(false);
        panelMain.setkStartColor(new java.awt.Color(102, 153, 255));
        panelMain.setMinimumSize(new java.awt.Dimension(1153, 713));
        panelMain.setPreferredSize(new java.awt.Dimension(1153, 713));
        panelMain.setLayout(new javax.swing.BoxLayout(panelMain, javax.swing.BoxLayout.Y_AXIS));

        panelTitle.setkBorderRadius(12);
        panelTitle.setkFillBackground(false);
        panelTitle.setOpaque(false);
        panelTitle.setPreferredSize(new java.awt.Dimension(1153, 50));

        labelTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        labelTitle.setForeground(new java.awt.Color(0, 54, 204));
        labelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/pcstore/resources/vi_VN"); // NOI18N
        labelTitle.setText(bundle.getString("txtMenuSell")); // NOI18N
        labelTitle.setToolTipText(bundle.getString("txtMenuSell")); // NOI18N
        panelTitle.add(labelTitle);

        panelMain.add(panelTitle);

        panelNav.setMinimumSize(new java.awt.Dimension(1153, 50));
        panelNav.setOpaque(false);
        panelNav.setPreferredSize(new java.awt.Dimension(1153, 50));
        panelNav.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        panelNav.add(textFieldSearch);

        panelMain.add(panelNav);

        panelNote.setOpaque(false);
        panelNote.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lbNote.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lbNote.setText(bundle.getString("lbNoteSell")); // NOI18N
        panelNote.add(lbNote);

        panelMain.add(panelNote);

        paneBody.setPreferredSize(new java.awt.Dimension(100, 300));
        paneBody.setLayout(new javax.swing.BoxLayout(paneBody, javax.swing.BoxLayout.LINE_AXIS));

        panelListProduct.setBackground(new java.awt.Color(255, 255, 255));
        panelListProduct.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), bundle.getString("titileBorderListProduct"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13), new java.awt.Color(0, 61, 179))); // NOI18N
        panelListProduct.setMinimumSize(new java.awt.Dimension(500, 39));
        panelListProduct.setPreferredSize(new java.awt.Dimension(800, 425));
        panelListProduct.setLayout(new java.awt.BorderLayout());

        tableListProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã sản phẩm", "Tên sản phẩm", "Phân loại", "Hãng sản xuất", "Số lượng tồn kho", "Giá bán"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Long.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableListProduct.getTableHeader().setReorderingAllowed(false);
        tableListProduct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableListProductMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableListProduct);
        if (tableListProduct.getColumnModel().getColumnCount() > 0) {
            tableListProduct.getColumnModel().getColumn(0).setResizable(false);
            tableListProduct.getColumnModel().getColumn(0).setPreferredWidth(8);
            tableListProduct.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("txtProductID")); // NOI18N
            tableListProduct.getColumnModel().getColumn(1).setResizable(false);
            tableListProduct.getColumnModel().getColumn(1).setPreferredWidth(60);
            tableListProduct.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("txtProductName")); // NOI18N
            tableListProduct.getColumnModel().getColumn(2).setResizable(false);
            tableListProduct.getColumnModel().getColumn(2).setPreferredWidth(30);
            tableListProduct.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("txtProductCategory")); // NOI18N
            tableListProduct.getColumnModel().getColumn(3).setResizable(false);
            tableListProduct.getColumnModel().getColumn(3).setPreferredWidth(30);
            tableListProduct.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("txtProductManufacturer")); // NOI18N
            tableListProduct.getColumnModel().getColumn(4).setResizable(false);
            tableListProduct.getColumnModel().getColumn(4).setPreferredWidth(15);
            tableListProduct.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("txtProductQuantity")); // NOI18N
            tableListProduct.getColumnModel().getColumn(5).setResizable(false);
            tableListProduct.getColumnModel().getColumn(5).setPreferredWidth(40);
            tableListProduct.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("txtProductPrice")); // NOI18N
        }

        panelListProduct.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        paneBody.add(panelListProduct);

        panelRight.setLayout(new javax.swing.BoxLayout(panelRight, javax.swing.BoxLayout.Y_AXIS));

        panelCart.setBackground(new java.awt.Color(255, 255, 255));
        panelCart.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), bundle.getString("lbCart"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13), new java.awt.Color(0, 61, 179))); // NOI18N
        panelCart.setkFillBackground(false);
        panelCart.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setPreferredSize(new java.awt.Dimension(452, 200));

        tableCart.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "", "STT", "ID", "Tên sản phẩm", "Số lượng", "Giá bán", "Thành tiền"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableCart.getTableHeader().setReorderingAllowed(false);
        tableCart.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableCartMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tableCart);
        if (tableCart.getColumnModel().getColumnCount() > 0) {
            tableCart.getColumnModel().getColumn(0).setPreferredWidth(10);
            tableCart.getColumnModel().getColumn(0).setMaxWidth(10);
            tableCart.getColumnModel().getColumn(1).setMinWidth(20);
            tableCart.getColumnModel().getColumn(1).setPreferredWidth(40);
            tableCart.getColumnModel().getColumn(1).setMaxWidth(1000);
            tableCart.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("txtProductNo")); // NOI18N
            tableCart.getColumnModel().getColumn(2).setPreferredWidth(60);
            tableCart.getColumnModel().getColumn(2).setMaxWidth(60);
            tableCart.getColumnModel().getColumn(3).setResizable(false);
            tableCart.getColumnModel().getColumn(3).setPreferredWidth(20);
            tableCart.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("txtProductName")); // NOI18N
            tableCart.getColumnModel().getColumn(4).setResizable(false);
            tableCart.getColumnModel().getColumn(4).setPreferredWidth(5);
            tableCart.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("txtProductQuantirySell")); // NOI18N
            tableCart.getColumnModel().getColumn(5).setResizable(false);
            tableCart.getColumnModel().getColumn(5).setPreferredWidth(10);
            tableCart.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("txtProductPrice")); // NOI18N
            tableCart.getColumnModel().getColumn(6).setResizable(false);
            tableCart.getColumnModel().getColumn(6).setPreferredWidth(6);
            tableCart.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("txtTotalAmount")); // NOI18N
        }

        panelCart.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        panelDelete.setBackground(new java.awt.Color(255, 255, 255));
        panelDelete.setMinimumSize(new java.awt.Dimension(10, 20));
        panelDelete.setPreferredSize(new java.awt.Dimension(100, 25));
        panelDelete.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 0));

        btnDeleteItemCart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/delete.png"))); // NOI18N
        btnDeleteItemCart.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDeleteItemCartMouseClicked(evt);
            }
        });
        panelDelete.add(btnDeleteItemCart);

        panelCart.add(panelDelete, java.awt.BorderLayout.PAGE_START);

        panelRight.add(panelCart);

        panelCustomerInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("lbInfoInvoice"))); // NOI18N
        panelCustomerInfo.setkEndColor(new java.awt.Color(255, 255, 255));
        panelCustomerInfo.setkStartColor(new java.awt.Color(255, 255, 255));
        panelCustomerInfo.setMinimumSize(new java.awt.Dimension(449, 251));
        panelCustomerInfo.setOpaque(false);
        panelCustomerInfo.setLayout(new java.awt.GridLayout(4, 2, 15, 20));

        panelCustomerName.setMinimumSize(new java.awt.Dimension(200, 22));
        panelCustomerName.setOpaque(false);
        panelCustomerName.setLayout(new javax.swing.BoxLayout(panelCustomerName, javax.swing.BoxLayout.LINE_AXIS));

        labelCustomerName.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        labelCustomerName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelCustomerName.setText(bundle.getString("lbName")); // NOI18N
        labelCustomerName.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 5));
        panelCustomerName.add(labelCustomerName);

        txtNameKH.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtNameKH.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtNameKH.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(102, 102, 102)));
        txtNameKH.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtNameKHFocusGained(evt);
            }
        });
        panelCustomerName.add(txtNameKH);

        panelCustomerInfo.add(panelCustomerName);

        panelCustomerPoint.setOpaque(false);
        panelCustomerPoint.setLayout(new javax.swing.BoxLayout(panelCustomerPoint, javax.swing.BoxLayout.LINE_AXIS));

        labelCustomerPoint.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        labelCustomerPoint.setText(bundle.getString("lbPointKH")); // NOI18N
        labelCustomerPoint.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        panelCustomerPoint.add(labelCustomerPoint);

        txtPointKH.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtPointKH.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtPointKH.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(102, 102, 102)));
        txtPointKH.setEnabled(false);
        panelCustomerPoint.add(txtPointKH);

        panelCustomerInfo.add(panelCustomerPoint);

        panelCustomerPhone.setOpaque(false);
        panelCustomerPhone.setLayout(new javax.swing.BoxLayout(panelCustomerPhone, javax.swing.BoxLayout.LINE_AXIS));

        labelCustomerPhone.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        labelCustomerPhone.setText(bundle.getString("lbPhoneNumber")); // NOI18N
        labelCustomerPhone.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 20));
        panelCustomerPhone.add(labelCustomerPhone);

        txtPhoneNumberKH.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtPhoneNumberKH.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtPhoneNumberKH.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(102, 102, 102)));
        panelCustomerPhone.add(txtPhoneNumberKH);

        panelCustomerInfo.add(panelCustomerPhone);

        btnApplyVoucher.setText(bundle.getString("btnApplyVoucher")); // NOI18N
        btnApplyVoucher.setToolTipText("");
        btnApplyVoucher.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnApplyVoucher.setIconTextGap(3);
        btnApplyVoucher.setkBorderRadius(45);
        btnApplyVoucher.setkEndColor(new java.awt.Color(102, 153, 255));
        btnApplyVoucher.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnApplyVoucher.setkHoverStartColor(new java.awt.Color(0, 153, 153));
        btnApplyVoucher.setkShowFocusBorder(true);
        btnApplyVoucher.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnApplyVoucherMouseClicked(evt);
            }
        });
        panelCustomerInfo.add(btnApplyVoucher);

        panelCustomerDiscount.setOpaque(false);
        panelCustomerDiscount.setLayout(new javax.swing.BoxLayout(panelCustomerDiscount, javax.swing.BoxLayout.LINE_AXIS));

        labelDiscount.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        labelDiscount.setText(bundle.getString("lbDiscountAmount")); // NOI18N
        labelDiscount.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        panelCustomerDiscount.add(labelDiscount);

        txtDiscountAmount.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        txtDiscountAmount.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtDiscountAmount.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(102, 102, 102)));
        txtDiscountAmount.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDiscountAmount.setEnabled(false);
        panelCustomerDiscount.add(txtDiscountAmount);

        panelCustomerInfo.add(panelCustomerDiscount);

        panelCustomerTotalAmount.setOpaque(false);
        panelCustomerTotalAmount.setLayout(new javax.swing.BoxLayout(panelCustomerTotalAmount, javax.swing.BoxLayout.LINE_AXIS));

        labelCustomerTotalAmonut.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        labelCustomerTotalAmonut.setText(bundle.getString("lbTotalAmount")); // NOI18N
        panelCustomerTotalAmount.add(labelCustomerTotalAmonut);

        txtTotalAmount.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtTotalAmount.setForeground(new java.awt.Color(0, 255, 51));
        txtTotalAmount.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtTotalAmount.setText("0 đ");
        txtTotalAmount.setBorder(null);
        txtTotalAmount.setDisabledTextColor(new java.awt.Color(51, 255, 0));
        txtTotalAmount.setEnabled(false);
        panelCustomerTotalAmount.add(txtTotalAmount);

        panelCustomerInfo.add(panelCustomerTotalAmount);

        panelRight.add(panelCustomerInfo);

        panelButtonAction.setOpaque(false);
        panelButtonAction.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 5));

        btnReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/refresh.png"))); // NOI18N
        btnReset.setText(bundle.getString("btnReset")); // NOI18N
        btnReset.setToolTipText("");
        btnReset.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnReset.setIconTextGap(45);
        btnReset.setInheritsPopupMenu(true);
        btnReset.setkBorderRadius(40);
        btnReset.setkEndColor(new java.awt.Color(0, 153, 255));
        btnReset.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnReset.setkHoverStartColor(new java.awt.Color(0, 153, 255));
        btnReset.setkShowFocusBorder(true);
        btnReset.setkStartColor(new java.awt.Color(0, 204, 255));
        btnReset.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnResetMouseClicked(evt);
            }
        });
        panelButtonAction.add(btnReset);

        btnPay.setText(bundle.getString("btnPay")); // NOI18N
        btnPay.setToolTipText("");
        btnPay.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnPay.setIconTextGap(3);
        btnPay.setkBorderRadius(45);
        btnPay.setkEndColor(new java.awt.Color(69, 195, 91));
        btnPay.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnPay.setkHoverStartColor(new java.awt.Color(0, 153, 153));
        btnPay.setkShowFocusBorder(true);
        btnPay.setkStartColor(new java.awt.Color(69, 195, 91));
        btnPay.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnPayMouseClicked(evt);
            }
        });
        panelButtonAction.add(btnPay);

        panelRight.add(panelButtonAction);

        paneBody.add(panelRight);

        panelMain.add(paneBody);

        add(panelMain, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    private void setupCusmizeTable() {

        tableListProduct.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableListProductSorter = TableUtils.applyProductTableStyle(tableListProduct, 4);

        //kích thước cột cho bảng sản phẩm
        if (tableListProduct.getColumnModel().getColumnCount() > 0) {
            TableUtils.setupColumnWidths(tableListProduct,
                    120, // Mã sản phẩm
                    250, // Tên sản phẩm
                    150, // Phân loại
                    150, // Hãng sản xuất
                    100, // Số lượng tồn kho
                    125  // Giá bán
            );
        }
    }


    private void initComponentsV2() {
        // Thay thế đoạn code khởi tạo btnApplyVoucher hiện tại với code sau
        // Tạo panel chứa cả label và switch button
        panelCustomerInfo.remove(btnApplyVoucher);
        JPanel panelUsePoints = new JPanel();
        panelUsePoints.setOpaque(false);
        panelUsePoints.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        // Thêm label
        JLabel lblUsePoints = new JLabel(bundle.getString("lblUsePoints"));
        lblUsePoints.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelUsePoints.add(lblUsePoints);

        // Thêm switch button
        sbtnUsePoint = new SwitchButton();
        sbtnUsePoint.setPreferredSize(new Dimension(60, 25));
        // sbtnUsePoint.setBackground(new Color(69, 195, 91)); // Màu xanh lá
        sbtnUsePoint.addEventSelected(new com.pcstore.components.EventSwitchSelected() {
            @Override
            public void onSelected(boolean selected) {
                sellController.processPointDiscount();
            }
        });
        panelUsePoints.add(sbtnUsePoint);

        // Thêm panel vào panelCustomerInfo
        panelCustomerInfo.add(panelUsePoints);
    }

    private void btnPayMouseClicked(MouseEvent evt) {
        sellController.prepareInvoiceToPay();
    }

    private void tableListProductMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableListProductMouseClicked
        if (evt.getClickCount() == 2) {
            sellController.addToCart();
        }
    }//GEN-LAST:event_tableListProductMouseClicked

    private void tableCartMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableCartMouseClicked
        sellController.processProductQuantityInCart(evt);
    }//GEN-LAST:event_tableCartMouseClicked

    private void btnDeleteItemCartMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteItemCartMouseClicked
        sellController.deleteItemCart();
    }//GEN-LAST:event_btnDeleteItemCartMouseClicked

    private void txtNameKHFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameKHFocusGained
        Customer customer = sellController.searchCustomerByPhone(txtPhoneNumberKH.getText().trim());
        if (customer != null) {
            txtNameKH.setText(customer.getFullName());
            txtPointKH.setText(String.valueOf(customer.getPoints()));
        } else {
            txtNameKH.setText(null);
            txtNameKH.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, bundle.getString("placeholder.customer.name"));
            txtPointKH.setText("0");
        }
        sellController.addCustomerToSale(customer);

    }//GEN-LAST:event_txtNameKHFocusGained

    private void btnResetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnResetMouseClicked
        int confirm = JOptionPane.showConfirmDialog(this,
                ErrorMessage.CONFIRM_RESET_CART.toString(),
                ErrorMessage.CONFIRM_TITLE.toString(), JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION)
            resetSaleForm();

    }//GEN-LAST:event_btnResetMouseClicked

    private void btnApplyVoucherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnApplyVoucherMouseClicked
        sellController.processPointDiscount();
    }//GEN-LAST:event_btnApplyVoucherMouseClicked

    //Cập nhật lại ô giảm giá
    public void updateDiscountAmount(BigDecimal discountAmount) {
        NumberFormat formatter = LocaleManager.getInstance().getCurrencyFormatter();
        txtDiscountAmount.setText(formatter.format(discountAmount));
        BigDecimal totalAfterDiscount = sellController.calculateTotalAfterDiscount();
        txtTotalAmount.setText(formatter.format(totalAfterDiscount));

        if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            txtDiscountAmount.setForeground(new Color(255, 0, 0)); // Màu đỏ
            txtDiscountAmount.setText("-" + formatter.format(discountAmount));
        } else {
            txtDiscountAmount.setForeground(new Color(0, 0, 0)); // Màu đen
            txtDiscountAmount.setText("");
        }
    }

    // Phương thức hỗ trợ để cập nhật hiển thị giỏ hàng
    public void updateCartDisplay() {
        // Cập nhật bảng giỏ hàng
        try {
            sellController.updateCartTable(tableCart);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Sử dụng LocaleManager
        NumberFormat formatter = LocaleManager.getInstance().getCurrencyFormatter();
        BigDecimal total = sellController.calculateTotal();
        txtTotalAmount.setText(formatter.format(total));
        sellController.updateUsePointsButtonState();
    }

    private void searchProducts() {
        String query = textFieldSearch.getTxtSearchField().getText().trim();
        List<Product> products = sellController.searchProducts(query);

        updateProductTable(products);
    }

    private void updateProductTable(List<Product> products) {
        DefaultTableModel model = (DefaultTableModel) tableListProduct.getModel();
        model.setRowCount(0);

        for (Product product : products) {
            Object[] row = {
                    product.getProductId(),
                    product.getProductName(),
                    product.getCategory() != null ? product.getCategory().getCategoryName() : "",
                    product.getSupplier() != null ? product.getSupplier().getName() : "",
                    product.getQuantityInStock(),
                    product.getPrice()
            };
            model.addRow(row);
        }
    }

    public void resetSaleForm() {

        boolean check = sellController.initializeSale(employee);
        if (!check) {
            JOptionPane.showMessageDialog(this,
                    ErrorMessage.INVOICE_CREATE_ERROR.toString(),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return;
        }

        sellController.clearCart();
        sellController.loadAllProducts();
        updateCartDisplay();

        // Xóa thông tin khách hàng
        txtNameKH.setText("Khách vãng lai");
        txtPhoneNumberKH.setText("");
        txtPointKH.setText("0");
        txtDiscountAmount.setText("0 đ");
    }

    /**
     * Đặt lại giảm giá về 0
     */
    public void resetDiscount() {
        // Đặt số tiền giảm giá về 0
        BigDecimal zeroDiscount = new BigDecimal("0");
        // Cập nhật giao diện với số tiền giảm giá = 0
        updateDiscountAmount(zeroDiscount);
    }

    public TableRowSorter<TableModel> getTableListProductSorter() {
        return tableListProductSorter;
    }

    public com.k33ptoo.components.KButton getBtnApplyVoucher() {
        return btnApplyVoucher;
    }

    public javax.swing.JButton getBtnDeleteItemCart() {
        return btnDeleteItemCart;
    }

    public SwitchButton getSbtnUsePoint() {
        return sbtnUsePoint;
    }

    public com.k33ptoo.components.KButton getBtnPay() {
        return btnPay;
    }

    public com.k33ptoo.components.KButton getBtnReset() {
        return btnReset;
    }

    public javax.swing.JTable getTableCart() {
        return tableCart;
    }

    public javax.swing.JTable getTableListProduct() {
        return tableListProduct;
    }

    public javax.swing.JTextField getTxtNameKH() {
        return txtNameKH;
    }

    public javax.swing.JTextField getTxtPhoneNumberKH() {
        return txtPhoneNumberKH;
    }

    public javax.swing.JTextField getTxtPointKH() {
        return txtPointKH;
    }

    public javax.swing.JTextField getTxtDiscountAmount() {
        return txtDiscountAmount;
    }

    public javax.swing.JTextField getTxtTotalAmount() {
        return txtTotalAmount;
    }

    public javax.swing.JTextField getTxtSearch() {
        return textFieldSearch.getTxtSearchField();
    }

    public void setTxtSearch(javax.swing.JTextField txtSearch) {
        this.textFieldSearch.setTxtSearchField(txtSearch);
    }

    public void setSellController(SellController sellController) {
        this.sellController = sellController;
    }

    public List<String> getListSelectProductIDs() {
        return listSelectProductIDs;
    }


}
