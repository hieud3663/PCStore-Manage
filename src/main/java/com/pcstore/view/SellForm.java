/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.pcstore.controller.PaymentController;
import com.pcstore.controller.SellController;
import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.Invoice;
import com.pcstore.model.Product;
import com.pcstore.model.enums.PaymentMethodEnum;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.utils.JDialogInputUtils;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.SessionManager;
import com.pcstore.utils.TableStyleUtil;
import com.pcstore.utils.TextFieldSearch;

/**
 *
 * @author MSII
 */
public class SellForm extends JPanel {


    
    private SellController sellController;
    private Connection connection;
    private RepositoryFactory repositoryFactory;

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

    // private TextFieldSearch textFieldSearch = new TextFieldSearch();
    
    /**
     * Creates new form Sell
     */
    
    public SellForm() {
        listSelectProductIDs = new ArrayList<>();

        initComponents();

        setupCusmizeTable();

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            repositoryFactory = RepositoryFactory.getInstance(connection);
            sellController = new SellController(this, connection, repositoryFactory);
            
            // Khởi tạo một giao dịch mới với ID nhân viên hiện tại
            Employee employee = SessionManager.getInstance().getCurrentUser().getEmployee();
            // String employeeId = "NV001";

            sellController.initializeSale(employee);
            
            updateCartDisplay();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khởi tạo form bán hàng: " + e.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }


        loadAllProducts();

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


    private void setupCusmizeTable(){
        
        tableListProductSorter = TableStyleUtil.applyDefaultStyle(tableListProduct);
        
        //kích thước cột cho bảng sản phẩm
        if (tableListProduct.getColumnModel().getColumnCount() > 0) {
            TableStyleUtil.setupColumnWidths(tableListProduct, 
                    120, // Mã sản phẩm
                    250, // Tên sản phẩm
                    150, // Phân loại
                    150, // Hãng sản xuất
                    100, // Số lượng tồn kho
                    125  // Giá bán
            );
        }
    }

    private void btnPayMouseClicked(MouseEvent evt) {                                        
        saveInvoiceToPay();
    }                                   

    private void tableListProductMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableListProductMouseClicked
        if (evt.getClickCount() == 2) {
            addToCart();
        }
    }//GEN-LAST:event_tableListProductMouseClicked

    private void tableCartMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableCartMouseClicked
        int row = tableCart.rowAtPoint(evt.getPoint());
        if (row >= 0) {
            String productId = tableCart.getValueAt(row, 2).toString();
            Boolean isSelected = (Boolean) tableCart.getValueAt(row, 0);
            if (isSelected != null && isSelected) {
                tableCart.setValueAt(Boolean.FALSE, row, 0); 
                listSelectProductIDs.remove(productId);
            } else {
                tableCart.setValueAt(Boolean.TRUE, row, 0); 
                listSelectProductIDs.add(productId);
            }
        }

        //Nếu click duoble thì sửa số lượng sản phẩm
        if (evt.getClickCount() == 2) {
            int selectedRow = tableCart.getSelectedRow();
            if (selectedRow >= 0) {
                String productId = tableCart.getValueAt(selectedRow, 2).toString();
                Integer quantity = JDialogInputUtils.showInputDialogInt(this, 
                    "Nhập số lượng sản phẩm:", 
                    String.valueOf(tableCart.getValueAt(selectedRow, 4)));
                sellController.updateProductQuantityInCart(productId, quantity);
                

                // Cập nhật số lượng trong controller
                boolean updated = sellController.updateProductQuantityInCart(productId, quantity);
                        
                if (updated) {
                    updateCartDisplay();
                } else {
                    JOptionPane.showMessageDialog(SellForm.this,
                        "Không thể cập nhật số lượng. Số lượng vượt quá tồn kho hoặc có lỗi khác.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    
                    updateCartDisplay();
                }
            }
        }
    }//GEN-LAST:event_tableCartMouseClicked

    private void btnDeleteItemCartMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteItemCartMouseClicked
        // Xóa sản phẩm đã chọn trong giỏ hàng
        if (listSelectProductIDs.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn sản phẩm để xóa khỏi giỏ hàng.", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa sản phẩm đã chọn khỏi giỏ hàng không?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            for (String productId : listSelectProductIDs) {
                
                sellController.removeProductFromCart(productId);

                // listSelectProductIDs.remove(productId);
            }
            listSelectProductIDs.clear();
        }

        updateCartDisplay();
    }//GEN-LAST:event_btnDeleteItemCartMouseClicked

    private void txtNameKHFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNameKHFocusGained
        Customer customer = sellController.searchCustomerByPhone(txtPhoneNumberKH.getText().trim());
        if (customer != null) {
            txtNameKH.setText(customer.getFullName());
            txtPointKH.setText(String.valueOf(customer.getPoints()));
            sellController.addCustomerToSale(customer); // Lưu khách hàng hiện tại vào controller
        } else {
            txtNameKH.setText("Khách vãng lai");
            txtPointKH.setText("0");
        }
    }//GEN-LAST:event_txtNameKHFocusGained

    private void btnResetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnResetMouseClicked
        int confirm = JOptionPane.showConfirmDialog(this,"Bạn có chắc chắn muốn làm mới giỏ hàng không?", "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) 
            resetSaleForm();

    }//GEN-LAST:event_btnResetMouseClicked

    private void btnApplyVoucherMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnApplyVoucherMouseClicked
        Customer customer = sellController.getCurrentInvoice().getCustomer();
    
        if (customer == null || "Khách vãng lai".equalsIgnoreCase(customer.getFullName())) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn khách hàng trước khi áp dụng ưu đãi điểm.", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int points = customer.getPoints();
        if (points < 10000) {
            JOptionPane.showMessageDialog(this, 
                "Khách hàng chưa đủ điểm để áp dụng ưu đãi. Cần ít nhất 10,000 điểm.", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Khách hàng có " + points + " điểm tích lũy.\nBạn có muốn sử dụng điểm để giảm giá không?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            BigDecimal discountAmount = sellController.applyPointsDiscount(true);
            
            if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                updateDiscountAmount(discountAmount);
            }
        }else{
            BigDecimal discountAmount = sellController.applyPointsDiscount(false);
            updateDiscountAmount(discountAmount);

        }
    }//GEN-LAST:event_btnApplyVoucherMouseClicked

    //Cập nhật lại ô giảm giá
    private void updateDiscountAmount(BigDecimal discountAmount) {
        // Cập nhật hiển thị
        NumberFormat formatter = LocaleManager.getInstance().getNumberFormatter();
        txtDiscountAmount.setText(formatter.format(discountAmount));
        // Cập nhật tổng tiền
        BigDecimal totalAfterDiscount = sellController.calculateTotalAfterDiscount();
        txtTotalAmount.setText(formatter.format(totalAfterDiscount) + " đ");
        
        txtDiscountAmount.setText("-" + formatter.format(discountAmount) + " đ");
    }

    // Trong phương thức initComponents() hoặc constructor của form bán hàng
    private void loadAllProducts() {
        List<Product> allProducts = sellController.searchProducts("");  // Truyền chuỗi rỗng để lấy tất cả
        sellController.updateProductTable(tableListProduct, allProducts);    // tblProducts là JTable hiển thị sản phẩm
    }


    // Phương thức hỗ trợ để cập nhật hiển thị giỏ hàng
    private void updateCartDisplay() {
        // Cập nhật bảng giỏ hàng
        try {
            sellController.updateCartTable(tableCart);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // Sử dụng LocaleManager
        NumberFormat formatter = LocaleManager.getInstance().getNumberFormatter();
        BigDecimal total = sellController.calculateTotal();
        txtTotalAmount.setText(formatter.format(total) + " đ");
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

    // Phương thức để thêm sản phẩm đã chọn vào giỏ hàng
    private void addToCart() {
        int selectedRow = tableListProduct.getSelectedRow();
        if (selectedRow >= 0) {
            String productId = tableListProduct.getValueAt(selectedRow, 0).toString();
            
            // Hỏi số lượng
            Integer quantityStr = JDialogInputUtils.showInputDialogInt(this, 
                "Nhập số lượng sản phẩm:", 
                "1");
                
            if (quantityStr != null) {
                try {
                    int quantity = quantityStr;
                    if (quantity > 0) {
                        boolean added = sellController.addProductToCart(productId, quantity);
                        if (added) {
                            updateCartDisplay();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Số lượng phải lớn hơn 0",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "Số lượng không hợp lệ",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn sản phẩm để thêm vào giỏ hàng",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }



    private Customer insertCustomer(String FullNameCustomer, String PhoneNumberCustomer) {
        // Kiểm tra xem khách hàng đã tồn tại trong cơ sở dữ liệu chưa
        Customer existingCustomer = sellController.searchCustomerByPhone(PhoneNumberCustomer);
        if (existingCustomer != null) {
            return existingCustomer;
        }

        // Nếu chưa có khách hàng, thì tạo khách hàng mới nhé
        Customer customer = new Customer();
        if (FullNameCustomer.isEmpty() || FullNameCustomer.equalsIgnoreCase("Khách vãng lai")) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập tên khách hàng", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        customer.setFullName(FullNameCustomer);
        customer.setPhoneNumber(PhoneNumberCustomer);
        customer.setPoints(0); // Hoặc lấy từ cơ sở dữ liệu nếu cần
        return customer;
    }

    
    private void saveInvoiceToPay() {
        if (sellController.getCartItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Giỏ hàng trống. Vui lòng thêm sản phẩm trước khi thanh toán.", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            boolean checkUpdateInvoive = sellController.updateCurrentInvoice();
            if (!checkUpdateInvoive) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi cập nhật hóa đơn. Vui lòng thử lại.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            //===================Lưu thông tin khách hàng=========================
            String FullNameCustomer = txtNameKH.getText().trim();
            String PhoneNumberCustomer = txtPhoneNumberKH.getText().trim();
    
            if(!PhoneNumberCustomer.isEmpty()) {
                Customer customer = insertCustomer(FullNameCustomer, PhoneNumberCustomer);
                if (customer == null) {
                    return; 
                }
                sellController.addCustomerToSale(customer);
            }
            //====================================================================
    
            // Xác nhận lưu hóa đơn
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn thanh toán hóa đơn không?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return; 
            }
    
            // Lưu hóa đơn trước khi thanh toán 
            Invoice saveInvoice = sellController.saveInvoice(PaymentMethodEnum.CASH); //Mặc định là tiền mặt
            if (saveInvoice == null) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi lưu hóa đơn. Vui lòng thử lại.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JOptionPane.showMessageDialog(this, 
                "Lưu hóa đơn thành công! ID: " + saveInvoice.getInvoiceId(), 
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            DashboardForm dashboard = DashboardForm.getInstance();
            PayForm payForm = new PayForm(dashboard, true);
            
            PaymentController paymentController = new PaymentController(payForm, saveInvoice);
            paymentController.showPaymentForm();
            
            if (paymentController.isPaymentSuccessful()) {
                
                saveInvoice.setPaymentMethod(paymentController.getCurrentPayment().getPaymentMethod());
                sellController.completeSale(saveInvoice);
                
                sellController.exportInvoiceToPDF(paymentController.getCurrentPayment());
    
                JOptionPane.showMessageDialog(this, 
                    "Thanh toán thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
                // Reset form bán hàng
                resetSaleForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Thanh toán không thành công!", 
                    "Thất bại", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi xử lý thanh toán: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void resetSaleForm() {

        boolean check = sellController.initializeSale(employee);
        if (!check) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khởi tạo hóa đơn mới. Vui lòng thử lại.", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Xóa giỏ hàng
        sellController.clearCart();
        
        loadAllProducts();

        // Cập nhật hiển thị giỏ hàng
        updateCartDisplay();
        
        // Xóa thông tin khách hàng
        txtNameKH.setText("Khách vãng lai");
        txtPhoneNumberKH.setText("");
        txtPointKH.setText("0");
        txtDiscountAmount.setText("0 đ");
    }


        
    public TableRowSorter<TableModel> getTableListProductSorter() {
        return tableListProductSorter;
    }

}
