/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;
import com.k33ptoo.components.*;
import com.pcstore.controller.PurchaseOrderController;
import com.pcstore.model.Employee;
import com.pcstore.model.Supplier;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.TextFieldSearch;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author nloc2
 */
public class PurchaseOrderForm extends JDialog {
    private PurchaseOrderController controller;


    private com.k33ptoo.components.KButton btnStockIn;
    private javax.swing.JComboBox<Supplier> cbbSupplier;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbEmloyeesCreate;
    private javax.swing.JLabel lbNote;
    private javax.swing.JLabel lbPurchaseOrderID;
    private javax.swing.JLabel lbSupplier;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JLabel lbTotalMoney;
    private javax.swing.JPanel panelBody;
    private javax.swing.JPanel panelFooter;
    private javax.swing.JPanel panelHeader;
    private com.k33ptoo.components.KGradientPanel panelMain;
    private javax.swing.JPanel panelTitle;
    private javax.swing.JTable productTable;
    private javax.swing.JTable selectedProductTable;
    private com.pcstore.utils.TextFieldSearch textFieldSearch;
    private javax.swing.JTextField txtEmloyeesCreate;
    private javax.swing.JTextField txtPurchaseOrderID;
    private javax.swing.JLabel txtTotalPrice;
    
    public PurchaseOrderForm() {
        super();
        initComponents();
    }

    public PurchaseOrderForm(Frame parent, boolean modal, Connection connection) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Phiếu Nhập Hàng");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Căn giữa dữ liệu bảng
        setupTableColumns();

        // Khởi tạo controller
        controller = new PurchaseOrderController(this, connection);
        
       
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        panelMain = new com.k33ptoo.components.KGradientPanel();
        panelTitle = new javax.swing.JPanel();
        lbTitle = new javax.swing.JLabel();
        panelHeader = new javax.swing.JPanel();
        lbPurchaseOrderID = new javax.swing.JLabel();
        txtPurchaseOrderID = new javax.swing.JTextField();
        lbEmloyeesCreate = new javax.swing.JLabel();
        txtEmloyeesCreate = new javax.swing.JTextField();
        lbSupplier = new javax.swing.JLabel();
        cbbSupplier = new javax.swing.JComboBox<>();
        textFieldSearch = new com.pcstore.utils.TextFieldSearch();
        lbNote = new javax.swing.JLabel();
        panelBody = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        selectedProductTable = new javax.swing.JTable();
        panelFooter = new javax.swing.JPanel();
        lbTotalMoney = new javax.swing.JLabel();
        txtTotalPrice = new javax.swing.JLabel();
        btnStockIn = new com.k33ptoo.components.KButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(1050, 680));
        setResizable(false);

        panelMain.setBackground(new java.awt.Color(255, 255, 255));
        panelMain.setkFillBackground(false);
        panelMain.setLayout(new javax.swing.BoxLayout(panelMain, javax.swing.BoxLayout.Y_AXIS));

        panelTitle.setBackground(new java.awt.Color(255, 255, 255));
        panelTitle.setPreferredSize(new java.awt.Dimension(1093, 50));
        panelTitle.setLayout(new java.awt.BorderLayout());

        lbTitle.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(0, 33, 190));
        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/pcstore/resources/vi_VN"); // NOI18N
        lbTitle.setText(bundle.getString("titlePurchaseOrder")); // NOI18N
        panelTitle.add(lbTitle, java.awt.BorderLayout.CENTER);

        panelMain.add(panelTitle);

        panelHeader.setBackground(new java.awt.Color(255, 255, 255));
        panelHeader.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 100, 1, 100));
        panelHeader.setMinimumSize(new java.awt.Dimension(188, 100));
        panelHeader.setLayout(new java.awt.GridLayout(3, 2, 10, 10));

        lbPurchaseOrderID.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbPurchaseOrderID.setText(bundle.getString("lbPurchaseOrderId")); // NOI18N
        lbPurchaseOrderID.setPreferredSize(new java.awt.Dimension(81, 30));
        panelHeader.add(lbPurchaseOrderID);

        txtPurchaseOrderID.setForeground(new java.awt.Color(51, 29, 204));
        txtPurchaseOrderID.setText(bundle.getString("txtPurchaseOrderID")); // NOI18N
        txtPurchaseOrderID.setEnabled(false);
        panelHeader.add(txtPurchaseOrderID);

        lbEmloyeesCreate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbEmloyeesCreate.setText(bundle.getString("lbEmloyeesCreate")); // NOI18N
        panelHeader.add(lbEmloyeesCreate);

        txtEmloyeesCreate.setForeground(new java.awt.Color(51, 29, 204));
        txtEmloyeesCreate.setText(bundle.getString("txtEmloyeesCreate")); // NOI18N
        txtEmloyeesCreate.setEnabled(false);
        panelHeader.add(txtEmloyeesCreate);

        lbSupplier.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbSupplier.setText(bundle.getString("lbSupplier")); // NOI18N
        panelHeader.add(lbSupplier);

        cbbSupplier.setForeground(new java.awt.Color(51, 29, 204));
        panelHeader.add(cbbSupplier);

        panelMain.add(panelHeader);

        textFieldSearch.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 55, 1, 600));
        panelMain.add(textFieldSearch);

        lbNote.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lbNote.setText(bundle.getString("lbNoteSell")); // NOI18N
        panelMain.add(lbNote);

        panelBody.setBackground(new java.awt.Color(255, 255, 255));
        panelBody.setPreferredSize(new java.awt.Dimension(913, 480));

        jScrollPane1.setPreferredSize(new java.awt.Dimension(550, 402));

        productTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Máy", "Tên Máy", "Đơn Giá", "SL Tồn Kho"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(productTable);
        if (productTable.getColumnModel().getColumnCount() > 0) {
            productTable.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("clProductID")); // NOI18N
            productTable.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("clProductName")); // NOI18N
            productTable.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("clPrice")); // NOI18N
            productTable.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("clQuantityInventory")); // NOI18N
        }

        panelBody.add(jScrollPane1);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(400, 402));

        selectedProductTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Máy", "Tên Máy", "Số Lượng", "Đơn Giá"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        selectedProductTable.setPreferredSize(new java.awt.Dimension(299, 380));
        jScrollPane2.setViewportView(selectedProductTable);
        if (selectedProductTable.getColumnModel().getColumnCount() > 0) {
            selectedProductTable.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("clProductID")); // NOI18N
            selectedProductTable.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("clProductName")); // NOI18N
            selectedProductTable.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("clQuantity")); // NOI18N
            selectedProductTable.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("clPrice")); // NOI18N
        }

        panelBody.add(jScrollPane2);

        panelMain.add(panelBody);

        panelFooter.setBackground(new java.awt.Color(255, 255, 255));
        panelFooter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 50, 5));

        lbTotalMoney.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        lbTotalMoney.setForeground(new java.awt.Color(0, 33, 190));
        lbTotalMoney.setText(bundle.getString("lbTotalMoney")); // NOI18N
        panelFooter.add(lbTotalMoney);

        txtTotalPrice.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtTotalPrice.setForeground(new java.awt.Color(255, 51, 51));
        txtTotalPrice.setText(bundle.getString("lbTotalPrice")); // NOI18N
        panelFooter.add(txtTotalPrice);

        btnStockIn.setText(bundle.getString("btnCreateStockIn")); // NOI18N
        btnStockIn.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnStockIn.setkBorderRadius(40);
        btnStockIn.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnStockIn.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        panelFooter.add(btnStockIn);

        panelMain.add(panelFooter);

        getContentPane().add(panelMain, java.awt.BorderLayout.CENTER);
    }

    /**
     * Thiết lập các cột cho bảng và thêm sự kiện double-click
     */
    private void setupTableColumns() {
        // Căn giữa các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Thiết lập căn giữa cho bảng sản phẩm
        for (int i = 0; i < productTable.getColumnCount(); i++) {
            productTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Thiết lập căn giữa cho bảng giỏ hàng
        for (int i = 0; i < selectedProductTable.getColumnCount(); i++) {
            selectedProductTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        javax.swing.Timer searchTimer = new javax.swing.Timer(500, e -> {
            if (controller != null) {
                controller.searchProducts();
            }
        });
        searchTimer.setRepeats(false); // Chỉ chạy một lần
        
        // Thêm sự kiện mouseClicked để xử lý double click trên bảng sản phẩm
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {  // Double click
                    int row = productTable.getSelectedRow();
                    if (row != -1 && controller != null) {  // Nếu có dòng được chọn
                        // Gọi phương thức thêm sản phẩm vào giỏ hàng
                        controller.addProductToCartByRowDirect(row);
                    }
                }
            }
        });
        
        // Thêm sự kiện double click để xóa sản phẩm khỏi giỏ hàng
        selectedProductTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {  // Double click
                    int row = selectedProductTable.getSelectedRow();
                    if (row != -1 && controller != null) {
                        int confirm = JOptionPane.showConfirmDialog(
                                PurchaseOrderForm.this,
                                "Bạn có muốn xóa sản phẩm này khỏi phiếu nhập không?",
                                "Xác nhận", JOptionPane.YES_NO_OPTION);
                        
                        if (confirm == JOptionPane.YES_OPTION) {
                            controller.removeProductFromCart(row);
                        }
                    }
                }
            }
        });

         // Kết nối DocumentListener cho thanh tìm kiếm
    textFieldSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            searchTimer.restart();
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            searchTimer.restart();
        }

        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            searchTimer.restart();
        }
    });
    
    // Thêm ActionListener cho nút tìm kiếm (nếu có)
    try {
        textFieldSearch.getBtnSearch().addActionListener(e -> {
            if (controller != null) {
                controller.searchProducts();
            }
        });
    } catch (Exception ex) {
        System.out.println("Không có phương thức getBtnSearch(): " + ex.getMessage());
    }

    }
    
    /**
     * Cập nhật ComboBox nhà cung cấp với danh sách từ database
     */
    public void updateSupplierComboBoxes(List<Supplier> suppliers) {
        // Xóa tất cả items hiện tại
        cbbSupplier.removeAllItems();
        
        // Thêm các nhà cung cấp vào combobox
        DefaultComboBoxModel<Supplier> model = new DefaultComboBoxModel<>();
        for (Supplier supplier : suppliers) {
            model.addElement(supplier);
        }
        
        // Đặt model mới cho combobox
        cbbSupplier.setModel(model);
        
        // Thiết lập renderer để hiển thị tên nhà cung cấp
        cbbSupplier.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Supplier) {
                    setText(((Supplier) value).getName());
                }
                return comp;
            }
        });
        
        // Chọn item đầu tiên nếu có
        if (cbbSupplier.getItemCount() > 0) {
            cbbSupplier.setSelectedIndex(0);
        }
    }

    /**
     * Xóa sản phẩm tại vị trí chỉ định khỏi giỏ hàng
     * @param row Dòng cần xóa
     */
    public void removeProductFromCart(int row) {
        if (controller != null) {
            controller.removeProductFromCart(row);
        }
    }

    // -------------------- CÁC PHƯƠNG THỨC GETTER --------------------

    /**
     * Lấy nhà cung cấp được chọn từ ComboBox
     */
    public Supplier getSelectedSupplier() {
        if (cbbSupplier.getSelectedItem() instanceof Supplier) {
            return (Supplier) cbbSupplier.getSelectedItem();
        }
        return null;
    }
    
    /**
     * Lấy ComboBox nhà cung cấp
     */
    public JComboBox<Supplier> getSupplierComboBox() {
        return (JComboBox<Supplier>) cbbSupplier;
    }

    /**
     * Lấy TextField ID phiếu nhập
     */
    public JTextField getPurchaseOrderIdField() {
        return txtPurchaseOrderID;
    }
        /**
     * Lấy combo box nhà cung cấp - Tương thích với controller cũ
     */
    public JComboBox<Supplier> getComboBoxSupplier() {
        return (JComboBox<Supplier>) cbbSupplier;
    }
    /**
     * Lấy TextField2 - Đồng bộ với getPurchaseOrderIdField()
     */
    public JTextField getTextField2() {
        return txtPurchaseOrderID;
    }

    /**
     * Lấy TextField nhân viên
     */
    public JTextField getEmployeeField() {
        return txtEmloyeesCreate;
    }
    
    /**
     * Lấy TextField4 - Đồng bộ với getEmployeeField()
     */
    public JTextField getTextField4() {
        return txtEmloyeesCreate;
    }

    /**
     * Lấy bảng sản phẩm
     */
    public JTable getTableProducts() {
        return productTable;
    }
    
    /**
     * Lấy JTable1 - Đồng bộ với getTableProducts()
     */
    public JTable getJTable1() {
        return productTable;
    }

    /**
     * Lấy bảng sản phẩm đã chọn
     */
    public JTable getTableSelectedProducts() {
        return selectedProductTable;
    }
    
    /**
     * Lấy JTable2 - Đồng bộ với getTableSelectedProducts()
     */
    public JTable getJTable2() {
        return selectedProductTable;
    }

    /**
     * Lấy label hiển thị tổng tiền
     */
    public JLabel getLabelTotalAmount() {
        return txtTotalPrice;
    }
    
    /**
     * Lấy TxtTotalPrice - Đồng bộ với getLabelTotalAmount()
     */
    public JLabel getTxtTotalPrice() {
        return txtTotalPrice;
    }

    /**
     * Lấy nút nhập kho
     */
    public KButton getBtnStockIn() {
        return btnStockIn;
    }
    
    /**
     * Lấy nút lưu - Đồng bộ với getBtnStockIn()
     */
    public JButton getBtnSave() {
        return btnStockIn;
    }
    
    /**
     * Lấy ô tìm kiếm
     */
    public TextFieldSearch getTextFieldSearch() {
        return textFieldSearch;
    }
    
    /**
     * Lấy label ID phiếu nhập
     */
    public JLabel getLblPurchaseOrderId() {
        return lbPurchaseOrderID;
    }

}