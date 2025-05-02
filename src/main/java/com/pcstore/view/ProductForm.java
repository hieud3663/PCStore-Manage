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

        labelESC.setVisible(false);

        tableListProduct.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
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
        for (ActionListener al : btnAdd.getActionListeners()) {
            btnAdd.removeActionListener(al);
        }
        
        // Thêm sự kiện cho các nút
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
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
        
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        
        btnExportExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportExcelActionPerformed(evt);
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
        tableListProduct.addMouseListener(new java.awt.event.MouseAdapter() {
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
                    if (btnAdd != null && !btnAdd.getBounds().contains(p)) {
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

        panelTitle = new javax.swing.JPanel();
        lbTitle = new javax.swing.JLabel();
        panelHeader = new javax.swing.JPanel();
        kPanelSearch = new com.k33ptoo.components.KGradientPanel();
        textFieldSearch1 = new com.pcstore.utils.TextFieldSearch();
        panelSortMain = new com.k33ptoo.components.KGradientPanel();
        panelSort = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
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
        panelSepecification = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtTechnicalSpecifications = new javax.swing.JTextArea();
        panelDescription = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescribe = new javax.swing.JTextArea();
        labelESC = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(1153, 713));
        setPreferredSize(new java.awt.Dimension(1153, 713));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        panelTitle.setBackground(new java.awt.Color(255, 255, 255));
        panelTitle.setPreferredSize(new java.awt.Dimension(100, 40));
        panelTitle.setLayout(new java.awt.BorderLayout());

        lbTitle.setFont(new java.awt.Font("Segoe UI", 1, 25)); // NOI18N
        lbTitle.setForeground(new java.awt.Color(0, 76, 192));
        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/pcstore/resources/vi_VN"); // NOI18N
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

        textFieldSearch1.setPreferredSize(new java.awt.Dimension(650, 31));
        kPanelSearch.add(textFieldSearch1);

        panelHeader.add(kPanelSearch);

        panelSortMain.setkFillBackground(false);
        panelSortMain.setOpaque(false);
        panelSortMain.setLayout(new java.awt.BorderLayout());

        panelSort.setBackground(new java.awt.Color(255, 255, 255));
        panelSort.setPreferredSize(new java.awt.Dimension(500, 70));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setText(bundle.getString("lbSort")); // NOI18N
        panelSort.add(jLabel12);

        cbbSortCustomer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<Không>", "Giá Bán", "Phân Loại" }));
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

        panelSortMain.add(panelSort, java.awt.BorderLayout.PAGE_START);

        panelHeader.add(panelSortMain);

        add(panelHeader);

        panelBtn.setOpaque(false);
        panelBtn.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

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
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
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
        btnExportExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportExcelActionPerformed(evt);
            }
        });
        panelBtn.add(btnExportExcel);

        add(panelBtn);

        panelBody.setBackground(new java.awt.Color(255, 255, 255));
        panelBody.setBorder(javax.swing.BorderFactory.createEmptyBorder(21, 10, 21, 10));
        panelBody.setMinimumSize(new java.awt.Dimension(850, 612));
        panelBody.setPreferredSize(new java.awt.Dimension(850, 471));
        panelBody.setLayout(new javax.swing.BoxLayout(panelBody, javax.swing.BoxLayout.LINE_AXIS));

        panelListProduct.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 20), "Danh Sách Sản Phẩm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(0, 76, 192))); // NOI18N
        panelListProduct.setkBorderRadius(20);
        panelListProduct.setkFillBackground(false);
        panelListProduct.setMinimumSize(new java.awt.Dimension(650, 570));
        panelListProduct.setOpaque(false);
        panelListProduct.setPreferredSize(new java.awt.Dimension(650, 570));
        panelListProduct.setLayout(new java.awt.BorderLayout());

        tableListProduct.setModel(new javax.swing.table.DefaultTableModel(
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
        tableListProduct.setPreferredSize(null);
        tableListProduct.setRowHeight(32);
        tableListProduct.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tableListProduct);

        panelListProduct.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panelBody.add(panelListProduct);

        panelDetails.setBackground(new java.awt.Color(255, 255, 255));
        panelDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("titleDetailInformation"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(0, 76, 192))); // NOI18N
        panelDetails.setPreferredSize(new java.awt.Dimension(450, 470));
        panelDetails.setLayout(new java.awt.BorderLayout());

        pnDetail.setBackground(new java.awt.Color(255, 255, 255));
        pnDetail.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 5, 20, 5));
        pnDetail.setMinimumSize(new java.awt.Dimension(230, 400));
        pnDetail.setPreferredSize(new java.awt.Dimension(400, 400));
        pnDetail.setLayout(new java.awt.GridLayout(4, 2, 20, 40));

        jPanel6.setPreferredSize(new java.awt.Dimension(250, 50));
        jPanel6.setLayout(new java.awt.GridLayout(2, 0));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText(bundle.getString("lbProductID")); // NOI18N
        jLabel3.setOpaque(true);
        jPanel6.add(jLabel3);

        txtProductID.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtProductID.setForeground(new java.awt.Color(0, 76, 192));
        jPanel6.add(txtProductID);

        pnDetail.add(jPanel6);

        jPanel3.setPreferredSize(new java.awt.Dimension(250, 50));
        jPanel3.setLayout(new java.awt.GridLayout(2, 0));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText(bundle.getString("lbProductName")); // NOI18N
        jLabel4.setOpaque(true);
        jPanel3.add(jLabel4);

        txtProductName.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtProductName.setForeground(new java.awt.Color(0, 76, 192));
        jPanel3.add(txtProductName);

        pnDetail.add(jPanel3);

        jPanel7.setPreferredSize(new java.awt.Dimension(250, 50));
        jPanel7.setLayout(new java.awt.GridLayout(2, 0));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText(bundle.getString("lbClassfication")); // NOI18N
        jLabel5.setOpaque(true);
        jPanel7.add(jLabel5);

        cbbClassfication.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cbbClassfication.setForeground(new java.awt.Color(0, 76, 192));
        jPanel7.add(cbbClassfication);

        pnDetail.add(jPanel7);

        jPanel9.setPreferredSize(new java.awt.Dimension(250, 50));
        jPanel9.setLayout(new java.awt.GridLayout(2, 0));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText(bundle.getString("lbQuantity")); // NOI18N
        jLabel7.setOpaque(true);
        jPanel9.add(jLabel7);

        txtQuantity.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtQuantity.setForeground(new java.awt.Color(0, 76, 192));
        jPanel9.add(txtQuantity);

        pnDetail.add(jPanel9);

        jPanel10.setPreferredSize(new java.awt.Dimension(250, 50));
        jPanel10.setLayout(new java.awt.GridLayout(2, 0));

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText(bundle.getString("lbPrice")); // NOI18N
        jLabel8.setOpaque(true);
        jPanel10.add(jLabel8);

        txtPrice.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtPrice.setForeground(new java.awt.Color(0, 76, 192));
        jPanel10.add(txtPrice);

        pnDetail.add(jPanel10);

        panelSepecification.setAlignmentY(30.0F);
        panelSepecification.setMinimumSize(new java.awt.Dimension(200, 80));
        panelSepecification.setName(""); // NOI18N
        panelSepecification.setOpaque(false);
        panelSepecification.setPreferredSize(new java.awt.Dimension(300, 100));
        panelSepecification.setLayout(new javax.swing.BoxLayout(panelSepecification, javax.swing.BoxLayout.Y_AXIS));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText(bundle.getString("lbSpecfication")); // NOI18N
        jLabel14.setOpaque(true);
        panelSepecification.add(jLabel14);

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

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText(bundle.getString("lbDetail")); // NOI18N
        jLabel11.setOpaque(true);
        jLabel11.setPreferredSize(new java.awt.Dimension(80, 20));
        panelDescription.add(jLabel11);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(16, 50));

        txtDescribe.setColumns(20);
        txtDescribe.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txtDescribe.setForeground(new java.awt.Color(0, 76, 192));
        txtDescribe.setRows(5);
        jScrollPane2.setViewportView(txtDescribe);

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
                                


    private void btnExportExcelActionPerformed(java.awt.event.ActionEvent evt) {
        controller.exportToExcel();
    }
    
    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn cập nhật sản phẩm này?",
                "Xác nhận cập nhật",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
    
        if (confirm == JOptionPane.YES_OPTION) {
            controller.updateProduct(); // Gọi phương thức cập nhật sản phẩm
        }
    }

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {
        controller.deleteProduct();
    }

    private void btnResetSortMouseClicked(java.awt.event.MouseEvent evt) {
        cbbSortCustomer.setSelectedIndex(0);
        cbbSort.setSelectedIndex(0);
        controller.loadProducts(); 
    }

    /**
     * Cập nhật ID tạm thời khi thay đổi danh mục
     */
    private void categoryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        if (controller.isAddingProduct()) {
            Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
            if (selectedCategory != null) {
                txtProductID.setText(controller.generateProductID(selectedCategory)); 
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

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {
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
        txtQuantity.setText("0"); // Số lượng
        txtPrice.setText("0"); // Giá
        txtTechnicalSpecifications.setText(""); // Thông số kỹ thuật
        txtDescribe.setText(""); // Mô tả
        
        // Cập nhật ID tạm thời
        categoryComboBoxActionPerformed(null);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KButton btnAdd;
    private com.k33ptoo.components.KButton btnDelete;
    private com.k33ptoo.components.KButton btnExportExcel;
    private javax.swing.JButton btnResetSort;
    private com.k33ptoo.components.KButton btnUpdate;
    private javax.swing.JComboBox<Category> cbbClassfication;
    private javax.swing.JComboBox<String> cbbSort;
    private javax.swing.JComboBox<String> cbbSortCustomer;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private com.k33ptoo.components.KGradientPanel kPanelSearch;
    private javax.swing.JLabel labelESC;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel panelBody;
    private javax.swing.JPanel panelBtn;
    private javax.swing.JPanel panelDescription;
    private javax.swing.JPanel panelDetails;
    private javax.swing.JPanel panelHeader;
    private com.k33ptoo.components.KGradientPanel panelListProduct;
    private javax.swing.JPanel panelSepecification;
    private javax.swing.JPanel panelSort;
    private com.k33ptoo.components.KGradientPanel panelSortMain;
    private javax.swing.JPanel panelTitle;
    private javax.swing.JPanel pnDetail;
    private javax.swing.JTable tableListProduct;
    private com.pcstore.utils.TextFieldSearch textFieldSearch1;
    private javax.swing.JTextArea txtDescribe;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtProductID;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextArea txtTechnicalSpecifications;
    // End of variables declaration//GEN-END:variables
}
