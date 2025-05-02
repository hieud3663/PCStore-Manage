/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pcstore.view;

import com.pcstore.controller.ProductController;
import com.pcstore.model.Category;
import com.pcstore.model.Supplier;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 *
 * @author nloc2
 */
public class ProductForm extends javax.swing.JPanel {

    private ProductController controller;
    private JComboBox<Category> categoryComboBox;


    /**
     * Creates new form Product
     */
    public ProductForm() {
        initComponents();

        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // 1. Khởi tạo combobox và các thành phần UI cơ bản
        categoryComboBox = (JComboBox<Category>) cbbClassfication;
        

        // 2. Khởi tạo controller TRƯỚC khi sử dụng trong các sự kiện
        controller = new ProductController(this);
        
        // 3. Thiết lập các thuộc tính và listeners cho UI
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
        
        
        // Thêm ràng buộc nhập liệu cho trường số lượng (chỉ cho nhập số)
        txtQuantity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    evt.consume();
                }
            }
        });
        txtTechnicalSpecifications.setLineWrap(true);
        txtTechnicalSpecifications.setWrapStyleWord(true);
        
        txtDescribe.setLineWrap(true);
        txtDescribe.setWrapStyleWord(true);
        
        // Cần thiết để tránh các vấn đề về focus
        txtTechnicalSpecifications.setCaretPosition(0);
        txtDescribe.setCaretPosition(0);
        
        // Thêm ràng buộc nhập liệu cho trường giá (số và dấu chấm)
        txtPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    evt.consume();
                }
            }
        });
        
        // Thêm sự kiện khi thay đổi danh mục để cập nhật ID tạm thời
        categoryComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryComboBoxActionPerformed(evt);
            }
        });
        for (ActionListener al : btnAdd4.getActionListeners()) {
            btnAdd4.removeActionListener(al);
        }
        
        // Thêm sự kiện cho các nút
        btnAdd4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (controller.isAddingProduct()) {
                    // Nếu đang ở chế độ thêm, thực hiện thêm sản phẩm
                    controller.addProduct();
                } else {
                    // Nếu đang ở chế độ bình thường, chuyển sang chế độ thêm
                    controller.handleAddButtonClick();
                }
            }
        });
        
        kButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kButton1ActionPerformed(evt);
            }
        });
        
        kButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kButton2ActionPerformed(evt);
            }
        });
        
        kButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kButton3ActionPerformed(evt);
            }
        });
        
        // Thêm sự kiện cho tìm kiếm
        try {
            textFieldSearch1.getBtnSearch().addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    textFieldSearch1ActionPerformed(evt);
                }
            });
        } catch (Exception e) {
            // Fallback nếu không có phương thức getBtnSearch()
            textFieldSearch1.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    textFieldSearch1ActionPerformed(null);
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    textFieldSearch1ActionPerformed(null);
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    textFieldSearch1ActionPerformed(null);
                }
            });
            System.err.println("Warning: Không thể truy cập getBtnSearch(). Sử dụng phương án dự phòng: " + e.getMessage());
        }
        
        // Thêm sự kiện click vào bảng để hiển thị chi tiết sản phẩm
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        
        // 4. Mọi listener sử dụng controller phải được đặt sau khi controller đã khởi tạo
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (controller != null && controller.isAddingProduct()) {
                    // Kiểm tra xem click có phải trên nút thêm không
                    java.awt.Point p = evt.getPoint();
                    if (btnAdd4 != null && !btnAdd4.getBounds().contains(p)) {
                        controller.cancelAddProduct();
                    }
                }
            }
        });
        java.awt.event.KeyAdapter escKeyListener = new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (controller != null && controller.isAddingProduct()) {
                        controller.cancelAddProduct();
                    }
                }
            }
        };

// Thêm KeyListener để bắt phím ESC để hủy thêm sản phẩm
    // Thay thế đoạn code hiện tại trong ProductForm.java từ dòng 174-193 với đoạn sau:
// Thêm KeyListener để bắt phím ESC để hủy thêm sản phẩm
KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
    new java.awt.KeyEventDispatcher() {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            // Chỉ xử lý khi phím được nhấn xuống (KEY_PRESSED)
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                // Nếu đang ở chế độ thêm sản phẩm, hủy thêm
                if (controller != null && controller.isAddingProduct()) {
                    controller.cancelAddProduct();
                    return true; // Đã xử lý sự kiện
                }
            }
            return false; // Không xử lý sự kiện
        }
    }
);
    // Đảm bảo form có thể nhận được sự kiện từ bàn phím
    setFocusable(true);
        
        // 5. Load dữ liệu ban đầu
        controller.loadProducts();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        panelHeader = new javax.swing.JPanel();
        kPanelSearch = new com.k33ptoo.components.KGradientPanel();
        textFieldSearch1 = new com.pcstore.utils.TextFieldSearch();
        kGradientPanel2 = new com.k33ptoo.components.KGradientPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        cbbSortCustomer = new javax.swing.JComboBox<>();
        cbbSort = new javax.swing.JComboBox<>();
        btnResetSort = new javax.swing.JButton();
        panelBody = new javax.swing.JPanel();
        kGradientPanel1 = new com.k33ptoo.components.KGradientPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        panelDetails = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtProductID = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        cbbClassfication = new javax.swing.JComboBox<>();
        jPanel9 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtPrice = new javax.swing.JTextField();
        panelDetail2 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtTechnicalSpecifications = new javax.swing.JTextArea();
        panelDetail = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescribe = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        kButton3 = new com.k33ptoo.components.KButton();
        kButton2 = new com.k33ptoo.components.KButton();
        kButton1 = new com.k33ptoo.components.KButton();
        btnAdd4 = new com.k33ptoo.components.KButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(1153, 713));
        setPreferredSize(new java.awt.Dimension(1153, 713));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 25)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/pcstore/resources/vi_VN"); // NOI18N
        jLabel2.setText(bundle.getString("txtMenuProduct")); // NOI18N
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel2.setMaximumSize(new java.awt.Dimension(32828, 1600000));
        jLabel2.setMinimumSize(new java.awt.Dimension(850, 34));
        jLabel2.setName(""); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(900, 30));
        add(jLabel2);

        panelHeader.setBackground(new java.awt.Color(153, 255, 0));
        panelHeader.setMaximumSize(new java.awt.Dimension(328791, 1000));
        panelHeader.setMinimumSize(new java.awt.Dimension(771, 50));
        panelHeader.setOpaque(false);
        panelHeader.setPreferredSize(new java.awt.Dimension(771, 80));
        panelHeader.setLayout(new javax.swing.BoxLayout(panelHeader, javax.swing.BoxLayout.LINE_AXIS));

        kPanelSearch.setkFillBackground(false);
        kPanelSearch.setMaximumSize(new java.awt.Dimension(100, 200));
        kPanelSearch.setMinimumSize(new java.awt.Dimension(100, 41));
        kPanelSearch.setOpaque(false);

        textFieldSearch1.setPreferredSize(new java.awt.Dimension(650, 31));
        kPanelSearch.add(textFieldSearch1);

        panelHeader.add(kPanelSearch);

        kGradientPanel2.setkFillBackground(false);
        kGradientPanel2.setLayout(new java.awt.BorderLayout());

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setPreferredSize(new java.awt.Dimension(500, 70));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setText(bundle.getString("lbSort")); // NOI18N
        jPanel5.add(jLabel12);

        cbbSortCustomer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<Không>", "Giá Bán", "Phân Loại" }));
        cbbSortCustomer.setPreferredSize(new java.awt.Dimension(150, 30));
        jPanel5.add(cbbSortCustomer);

        cbbSort.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<Không>", "Tăng dần", "Giảm giần" }));
        cbbSort.setPreferredSize(new java.awt.Dimension(100, 30));
        jPanel5.add(cbbSort);

        btnResetSort.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/refresh.png"))); // NOI18N
        btnResetSort.setPreferredSize(new java.awt.Dimension(50, 25));
        btnResetSort.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnResetSortMouseClicked(evt);
            }
        });
        jPanel5.add(btnResetSort);

        kGradientPanel2.add(jPanel5, java.awt.BorderLayout.PAGE_START);

        panelHeader.add(kGradientPanel2);

        add(panelHeader);

        panelBody.setBackground(new java.awt.Color(255, 255, 255));
        panelBody.setBorder(javax.swing.BorderFactory.createEmptyBorder(21, 10, 21, 10));
        panelBody.setMinimumSize(new java.awt.Dimension(850, 612));
        panelBody.setPreferredSize(new java.awt.Dimension(850, 471));
        panelBody.setLayout(new javax.swing.BoxLayout(panelBody, javax.swing.BoxLayout.LINE_AXIS));

        kGradientPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 20), "Danh Sách Sản Phẩm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 18))); // NOI18N
        kGradientPanel1.setkBorderRadius(20);
        kGradientPanel1.setkFillBackground(false);
        kGradientPanel1.setMinimumSize(new java.awt.Dimension(650, 570));
        kGradientPanel1.setOpaque(false);
        kGradientPanel1.setPreferredSize(new java.awt.Dimension(650, 570));
        kGradientPanel1.setLayout(new java.awt.BorderLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã Sản Phẩm", "Tên Sản Phẩm", "Phân Loại", "Số Lượng", "Giá Bán", "Thông Số Kỹ Thuật", "Mô Tả"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.setRowHeight(32);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        kGradientPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panelBody.add(kGradientPanel1);

        panelDetails.setBackground(new java.awt.Color(255, 255, 255));
        panelDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("titleDetailInformation"))); // NOI18N
        panelDetails.setPreferredSize(new java.awt.Dimension(450, 470));
        panelDetails.setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setMinimumSize(new java.awt.Dimension(230, 400));
        jPanel2.setPreferredSize(new java.awt.Dimension(400, 400));
        jPanel2.setLayout(new java.awt.GridLayout(5, 2, 40, 40));

        jPanel6.setPreferredSize(new java.awt.Dimension(250, 50));
        jPanel6.setLayout(new java.awt.GridLayout(2, 0));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText(bundle.getString("lbProductID")); // NOI18N
        jLabel3.setOpaque(true);
        jPanel6.add(jLabel3);
        jPanel6.add(txtProductID);

        jPanel2.add(jPanel6);

        jPanel3.setPreferredSize(new java.awt.Dimension(250, 50));
        jPanel3.setLayout(new java.awt.GridLayout(2, 0));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText(bundle.getString("lbProductName")); // NOI18N
        jLabel4.setOpaque(true);
        jPanel3.add(jLabel4);
        jPanel3.add(txtProductName);

        jPanel2.add(jPanel3);

        jPanel7.setPreferredSize(new java.awt.Dimension(250, 50));
        jPanel7.setLayout(new java.awt.GridLayout(2, 0));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText(bundle.getString("lbClassfication")); // NOI18N
        jLabel5.setOpaque(true);
        jPanel7.add(jLabel5);

        cbbClassfication.setModel(new javax.swing.DefaultComboBoxModel<Category>());
        jPanel7.add(cbbClassfication);

        jPanel2.add(jPanel7);

        jPanel9.setPreferredSize(new java.awt.Dimension(250, 50));
        jPanel9.setLayout(new java.awt.GridLayout(2, 0));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText(bundle.getString("lbQuantity")); // NOI18N
        jLabel7.setOpaque(true);
        jPanel9.add(jLabel7);
        jPanel9.add(txtQuantity);

        jPanel2.add(jPanel9);

        jPanel10.setPreferredSize(new java.awt.Dimension(250, 50));
        jPanel10.setLayout(new java.awt.GridLayout(2, 0));

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText(bundle.getString("lbPrice")); // NOI18N
        jLabel8.setOpaque(true);
        jPanel10.add(jLabel8);
        jPanel10.add(txtPrice);

        jPanel2.add(jPanel10);

        panelDetail2.setAlignmentY(30.0F);
        panelDetail2.setMinimumSize(new java.awt.Dimension(200, 80));
        panelDetail2.setName(""); // NOI18N
        panelDetail2.setPreferredSize(new java.awt.Dimension(300, 100));
        panelDetail2.setLayout(new javax.swing.BoxLayout(panelDetail2, javax.swing.BoxLayout.Y_AXIS));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText(bundle.getString("lbSpecfication")); // NOI18N
        jLabel14.setOpaque(true);
        panelDetail2.add(jLabel14);

        jScrollPane4.setMinimumSize(new java.awt.Dimension(16, 50));

        txtTechnicalSpecifications.setColumns(20);
        txtTechnicalSpecifications.setRows(5);
        txtTechnicalSpecifications.setAutoscrolls(false);
        jScrollPane4.setViewportView(txtTechnicalSpecifications);

        panelDetail2.add(jScrollPane4);


        jPanel2.add(panelDetail2);
        panelDetail2.getAccessibleContext().setAccessibleDescription("");

        panelDetail.setMinimumSize(new java.awt.Dimension(200, 80));
        panelDetail.setPreferredSize(new java.awt.Dimension(300, 100));
        panelDetail.setLayout(new javax.swing.BoxLayout(panelDetail, javax.swing.BoxLayout.Y_AXIS));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText(bundle.getString("lbDetail")); // NOI18N
        jLabel11.setOpaque(true);
        panelDetail.add(jLabel11);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(16, 50));

        txtDescribe.setColumns(20);
        txtDescribe.setRows(5);
        jScrollPane2.setViewportView(txtDescribe);

        panelDetail.add(jScrollPane2);

        jPanel2.add(panelDetail);

        panelDetails.add(jPanel2, java.awt.BorderLayout.CENTER);

        panelBody.add(panelDetails);

        add(panelBody);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        kButton3.setText(bundle.getString("btnExport")); // NOI18N
        kButton3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        kButton3.setkAllowGradient(false);
        kButton3.setkBackGroundColor(new java.awt.Color(0, 204, 102));
        kButton3.setkBorderRadius(30);
        kButton3.setkEndColor(new java.awt.Color(0, 153, 153));
        kButton3.setkHoverEndColor(new java.awt.Color(0, 204, 204));
        kButton3.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        kButton3.setkHoverStartColor(new java.awt.Color(0, 204, 204));
        kButton3.setkStartColor(new java.awt.Color(102, 255, 0));
        kButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kButton3ActionPerformed(evt);
            }
        });
        jPanel1.add(kButton3);

        kButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/trash.png"))); // NOI18N
        kButton2.setText(bundle.getString("btnDeleteFromListProduct")); // NOI18N
        kButton2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        kButton2.setkAllowGradient(false);
        kButton2.setkBackGroundColor(new java.awt.Color(255, 51, 51));
        kButton2.setkBorderRadius(30);
        kButton2.setkEndColor(new java.awt.Color(255, 51, 51));
        kButton2.setkHoverEndColor(new java.awt.Color(255, 204, 204));
        kButton2.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        kButton2.setkHoverStartColor(new java.awt.Color(255, 51, 51));
        kButton2.setkStartColor(new java.awt.Color(255, 204, 204));
        jPanel1.add(kButton2);

        kButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/refresh.png"))); // NOI18N
        kButton1.setText(bundle.getString("btnUpdate")); // NOI18N
        kButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        kButton1.setkBorderRadius(30);
        kButton1.setkEndColor(new java.awt.Color(102, 153, 255));
        kButton1.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        kButton1.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        kButton1.setkHoverStartColor(new java.awt.Color(153, 255, 154));
        jPanel1.add(kButton1);

        btnAdd4.setText(bundle.getString("btnAddProduct")); // NOI18N
        btnAdd4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnAdd4.setkAllowGradient(false);
        btnAdd4.setkBackGroundColor(new java.awt.Color(0, 102, 255));
        btnAdd4.setkBorderRadius(30);
        btnAdd4.setkEndColor(new java.awt.Color(102, 153, 255));
        btnAdd4.setkHoverEndColor(new java.awt.Color(102, 153, 255));
        btnAdd4.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnAdd4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdd4ActionPerformed(evt);
            }
        });
        jPanel1.add(btnAdd4);

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents
                                

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {
        // Xử lý sự kiện khi nhấn Enter trong jTextField6
        String inputText = txtPrice.getText();
        System.out.println("Nội dung nhập: " + inputText);
        // Thêm logic xử lý khác nếu cần
    }


    private void kButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        controller.exportToExcel();
    }
    
    private void kButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn cập nhật sản phẩm này?",
                "Xác nhận cập nhật",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
    
        if (confirm == JOptionPane.YES_OPTION) {
            controller.updateProduct(); // Gọi phương thức cập nhật sản phẩm
        }
    }

    private void kButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        controller.deleteProduct();
    }

    private void btnResetSortMouseClicked(java.awt.event.MouseEvent evt) {
        cbbSortCustomer.setSelectedIndex(0);
        cbbSort.setSelectedIndex(0);
        controller.loadProducts(); // Tải lại danh sách không sắp xếp
    }

    /**
 * Cập nhật ID tạm thời khi thay đổi danh mục
 */
private void categoryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
    if (controller.isAddingProduct()) {
        Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
        if (selectedCategory != null) {
            txtProductID.setText(selectedCategory.getCategoryId() + "xxx"); // Hiển thị ID mẫu
        } else {
            txtProductID.setText("xxxxx"); // Mẫu mặc định
        }
    }
}
    private void textFieldSearch1ActionPerformed(java.awt.event.ActionEvent evt) {
        controller.searchProducts(textFieldSearch1.getText());
    }

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
        controller.displaySelectedProduct();
    }
    
    /**
     * Sắp xếp sản phẩm theo tiêu chí được chọn
     */
    private void sortProducts() {
        String sortField = cbbSortCustomer.getSelectedItem().toString();
        String sortOrder = cbbSort.getSelectedItem().toString();
        
        if (!"<Không>".equals(sortField) && !"<Không>".equals(sortOrder)) {
            controller.sortProducts(sortField, sortOrder);
        }
    }

    private void btnAdd4ActionPerformed(java.awt.event.ActionEvent evt) {
        if (controller.isAddingProduct()) {
            // Nếu đang ở chế độ thêm, thực hiện thêm sản phẩm
            controller.addProduct();
        } else {
            // Nếu đang ở chế độ bình thường, chuyển sang chế độ thêm
            controller.handleAddButtonClick();
        }
    }

    // Thêm các getter cho các thành phần giao diện
    public javax.swing.JTable getTable() {
        return jTable1;
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

    public javax.swing.JTextField getQuantityField() {
        return txtQuantity;  // Quantity
    }

    public javax.swing.JTextField getPriceField() {
        return txtPrice;  // Price
    }

    public javax.swing.JTextArea getSpecificationsArea() {
        return txtTechnicalSpecifications;  // Specifications
    }

    public javax.swing.JTextArea getDescriptionArea() {
        return txtDescribe;  // Description
    }

    public com.pcstore.utils.TextFieldSearch getTextFieldSearch() {
        return textFieldSearch1;
    }

    public com.k33ptoo.components.KButton getBtnAdd() {
        return btnAdd4;
    }

    public com.k33ptoo.components.KButton getBtnUpdate() {
        return kButton1;
    }

    public com.k33ptoo.components.KButton getBtnDelete() {
        return kButton2;
    }
    public com.k33ptoo.components.KButton getBtnExport() {
        return kButton3;
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

    /**
     * Cập nhật dữ liệu cho bảng sản phẩm
     * @param data Dữ liệu hiển thị
     * @param columns Tên các cột
     */
    public void updateTable(Object[][] data, String[] columns) {
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        txtQuantity.setText("0"); // Số lượng
        txtPrice.setText("0"); // Giá
        txtTechnicalSpecifications.setText(""); // Thông số kỹ thuật
        txtDescribe.setText(""); // Mô tả
        
        // Cập nhật ID tạm thời
        categoryComboBoxActionPerformed(null);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KButton btnAdd4;
    private javax.swing.JButton btnResetSort;
    private JComboBox<Category> cbbClassfication;
    private javax.swing.JComboBox<String> cbbSort;
    private javax.swing.JComboBox<String> cbbSortCustomer;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;

    private com.k33ptoo.components.KButton kButton1;
    private com.k33ptoo.components.KButton kButton2;
    private com.k33ptoo.components.KButton kButton3;
    private com.k33ptoo.components.KGradientPanel kGradientPanel1;
    private com.k33ptoo.components.KGradientPanel kGradientPanel2;
    private com.k33ptoo.components.KGradientPanel kPanelSearch;
    private javax.swing.JPanel panelBody;
    private javax.swing.JPanel panelDetail;
    private javax.swing.JPanel panelDetail2;
    private javax.swing.JPanel panelDetails;
    private javax.swing.JPanel panelHeader;
    private com.pcstore.utils.TextFieldSearch textFieldSearch1;
    private javax.swing.JTextArea txtDescribe;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtProductID;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextArea txtTechnicalSpecifications;
    // End of variables declaration//GEN-END:variables
}
