package com.pcstore.view;

import com.pcstore.controller.SupplierController;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.TableUtils;
import com.k33ptoo.components.KButton;

import java.awt.*;
import java.util.ResourceBundle;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Form quản lý nhà cung cấp với thiết kế hiện đại, sắc nét
 * View trong mô hình MVC - chỉ chứa giao diện, không xử lý logic
 * 
 * @author MSII
 */
public class SupplierForm extends JPanel {

    // Variables declaration
    private JPanel panelMain;
    private JPanel panelTop;
    private JPanel panelContent;
    private JPanel panelButtons;
    private JPanel panelFormWrap;
    private JLabel lbTitle;
    private JTable tableSuppliers;
    private DefaultTableModel tableModel;
    private JTextField txtSupplierId, txtSupplierName, txtPhone, txtEmail, txtAddress;
    private KButton btnRefresh, btnSave, btnDelete, btnAdd;

    private ResourceBundle bundle;
    
    // Controller
    private SupplierController controller;

    /**
     * Tạo form quản lý nhà cung cấp
     */
    public SupplierForm() {
        setLayout(new BorderLayout());
        setBackground(new Color(242, 245, 250));
        
        // Khởi tạo giao diện
        createTopPanel();
        initComponents();
        
        // Khởi tạo controller và gắn nó với view này
        controller = SupplierController.getInstance(this);
        bundle = LocaleManager.getInstance().getResourceBundle();
    }

    /**
     * Tạo panel tiêu đề phía trên
     */
    private void createTopPanel() {
        panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(new Color(255, 255, 255));
        panelTop.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 220, 240)),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)));
        panelTop.setPreferredSize(new Dimension(1200, 70));

        // Tạo tiêu đề với màu sắc đẹp hơn
        lbTitle = new JLabel("QUẢN LÝ NHÀ CUNG CẤP");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lbTitle.setForeground(new Color(47, 85, 151));
        lbTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        panelTop.add(lbTitle, BorderLayout.WEST);
    }

    /**
     * Khởi tạo các thành phần giao diện
     */
    private void initComponents() {
        // Main panel với bo góc đẹp
        panelMain = new RoundedPanel(20, new Color(255, 255, 255));
        panelMain.setLayout(new BorderLayout());
        panelMain.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Top panel
        panelMain.add(panelTop, BorderLayout.PAGE_START);
        
        // Buttons panel - Nút ở bên trái
        panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelButtons.setOpaque(false);
        
        // Tạo nút Thêm
        btnAdd = new KButton();
        btnAdd.setText(bundle.getString("supplierForm.button.add"));
        btnAdd.setkBackGroundColor(new Color(100, 180, 100));
        btnAdd.setkEndColor(new Color(80, 160, 80));
        btnAdd.setkStartColor(new Color(120, 200, 120));
        btnAdd.setkHoverStartColor(new Color(130, 210, 130));
        btnAdd.setkHoverEndColor(new Color(100, 180, 100));
        btnAdd.setkBorderRadius(15);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setPreferredSize(new Dimension(130, 35));
        
        // Tạo nút Lưu
        btnSave = new KButton();
        btnSave.setText(bundle.getString("supplierForm.button.save"));
        btnSave.setkBackGroundColor(new Color(80, 140, 220));
        btnSave.setkEndColor(new Color(80, 120, 220));
        btnSave.setkStartColor(new Color(100, 160, 240));
        btnSave.setkHoverStartColor(new Color(120, 180, 250));
        btnSave.setkHoverEndColor(new Color(100, 140, 230));
        btnSave.setkBorderRadius(15);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(130, 35));
        btnSave.setEnabled(false); // Ban đầu không thể lưu
        
        // Tạo nút Xóa
        btnDelete = new KButton();
        btnDelete.setText(bundle.getString("supplierForm.button.delete"));
        btnDelete.setkBackGroundColor(new Color(220, 80, 80));
        btnDelete.setkEndColor(new Color(200, 60, 60));
        btnDelete.setkStartColor(new Color(240, 100, 100));
        btnDelete.setkHoverStartColor(new Color(250, 120, 120));
        btnDelete.setkHoverEndColor(new Color(230, 80, 80));
        btnDelete.setkBorderRadius(15);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setPreferredSize(new Dimension(130, 35));
        btnDelete.setEnabled(false); // Ban đầu không thể xóa
        
        // Tạo nút Làm mới
        btnRefresh = new KButton();
        btnRefresh.setText(bundle.getString("supplierForm.button.refresh"));
        btnRefresh.setkBackGroundColor(new Color(90, 160, 200));
        btnRefresh.setkEndColor(new Color(70, 140, 180));
        btnRefresh.setkStartColor(new Color(100, 180, 220));
        btnRefresh.setkHoverStartColor(new Color(110, 190, 230));
        btnRefresh.setkHoverEndColor(new Color(90, 160, 200));
        btnRefresh.setkBorderRadius(15);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setPreferredSize(new Dimension(130, 35));
        
        // Thứ tự các nút: Thêm, Lưu, Xóa, Làm mới
        panelButtons.add(btnAdd);
        panelButtons.add(btnSave);
        panelButtons.add(btnDelete);
        panelButtons.add(btnRefresh);
        
        // Content panel
        panelContent = new JPanel(new BorderLayout(15, 15));
        panelContent.setOpaque(false);
        
        // Thêm panel buttons trên đầu content
        panelContent.add(panelButtons, BorderLayout.NORTH);

        // Table với thiết kế hiện đại
        String[] columns = {
            bundle.getString("supplierForm.table.supplierCode"), 
            bundle.getString("supplierForm.table.supplierName"), 
            bundle.getString("supplierForm.table.phone"), 
            bundle.getString("supplierForm.table.email"), 
            bundle.getString("supplierForm.table.address")
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
        };
        
        tableSuppliers = new JTable(tableModel);
        tableSuppliers.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableSuppliers.setRowHeight(35);
        tableSuppliers.setIntercellSpacing(new Dimension(5, 5));
        tableSuppliers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tableSuppliers.getTableHeader().setBackground(new Color(82, 148, 226));
        tableSuppliers.getTableHeader().setForeground(Color.WHITE);
        tableSuppliers.setSelectionBackground(new Color(220, 240, 255));
        tableSuppliers.setSelectionForeground(new Color(33, 33, 33));
        tableSuppliers.setShowGrid(false);
        tableSuppliers.setShowHorizontalLines(true);
        tableSuppliers.setShowVerticalLines(false);
        tableSuppliers.setGridColor(new Color(240, 240, 240));
        
        // Áp dụng style mặc định từ TableUtils
        TableUtils.applyDefaultStyle(tableSuppliers);
        
        // Tạo scroll pane với viền đẹp
        JScrollPane scrollPane = new JScrollPane(tableSuppliers);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 230, 240), 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panelContent.add(scrollPane, BorderLayout.CENTER);

        // Form nhập thông tin với gradient nhẹ
        panelFormWrap = new RoundedPanel(15, new Color(245, 248, 255));
        panelFormWrap.setLayout(new BorderLayout());
        panelFormWrap.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel form sử dụng grid layout để căn chỉnh đẹp
        JPanel panelForm = new JPanel(new GridLayout(2, 5, 15, 10));
        panelForm.setOpaque(false);

        txtSupplierId = new JTextField(); 
        txtSupplierId.setEnabled(false);
        txtSupplierId.setBackground(new Color(245, 245, 245));
        txtSupplierName = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtAddress = new JTextField();

        // Định dạng các field nhập liệu
        JTextField[] fields = {txtSupplierId, txtSupplierName, txtPhone, txtEmail, txtAddress};
        for (JTextField f : fields) {
            f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 200, 230), 1, true),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            f.setBackground(Color.WHITE);
            
            // Thêm key listener để báo cho controller về sự thay đổi
            f.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    if (controller != null) {
                        controller.handleFormFieldChange();
                    }
                }
            });
        }

        // Tạo các label cho form
        JLabel lbId = new JLabel(bundle.getString("supplierForm.label.supplierCode"));
        JLabel lbName = new JLabel(bundle.getString("supplierForm.label.supplierName"));
        JLabel lbPhone = new JLabel(bundle.getString("supplierForm.label.phone"));
        JLabel lbEmail = new JLabel(bundle.getString("supplierForm.label.email"));
        JLabel lbAddress = new JLabel(bundle.getString("supplierForm.label.address"));
        
        JLabel[] labels = {lbId, lbName, lbPhone, lbEmail, lbAddress};
        for (JLabel lb : labels) {
            lb.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lb.setForeground(new Color(60, 72, 107));
        }

        // Thêm các component vào form
        panelForm.add(lbId); panelForm.add(lbName);
        panelForm.add(lbPhone); panelForm.add(lbEmail); panelForm.add(lbAddress);
        panelForm.add(txtSupplierId); panelForm.add(txtSupplierName);
        panelForm.add(txtPhone); panelForm.add(txtEmail); panelForm.add(txtAddress);

        panelFormWrap.add(panelForm, BorderLayout.CENTER);
        panelContent.add(panelFormWrap, BorderLayout.SOUTH);

        // Thêm content vào main
        panelMain.add(panelContent, BorderLayout.CENTER);

        // Thêm main vào form với padding
        add(panelMain, BorderLayout.CENTER);
    }

    /**
     * Panel bo góc hiện đại
     */
    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            super();
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Getters cho các components
    public JTable getTableSuppliers() {
        return tableSuppliers;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getTxtSupplierId() {
        return txtSupplierId;
    }

    public JTextField getTxtSupplierName() {
        return txtSupplierName;
    }

    public JTextField getTxtPhone() {
        return txtPhone;
    }

    public JTextField getTxtEmail() {
        return txtEmail;
    }

    public JTextField getTxtAddress() {
        return txtAddress;
    }

    public KButton getBtnRefresh() {
        return btnRefresh;
    }
    
    public KButton getBtnSave() {
        return btnSave;
    }
    
    public KButton getBtnDelete() {
        return btnDelete;
    }
    
    public KButton getBtnAdd() {
        return btnAdd;
    }
    
    /**
     * Đặt controller cho view
     */
    public void setController(SupplierController controller) {
        this.controller = controller;
    }
}