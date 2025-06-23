package com.pcstore.view;

import com.pcstore.controller.SupplierController;
import com.pcstore.model.Supplier;
import com.pcstore.service.ServiceFactory;
import com.pcstore.service.SupplierService;
import com.pcstore.utils.TableUtils;
import com.k33ptoo.components.KButton;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Form quản lý nhà cung cấp với thiết kế hiện đại, sắc nét
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
    private JTextField txtSupplierId, txtSupplierName, txtPhone, txtEmail, txtAddress, txtSearch;
    private KButton btnRefresh, btnSave, btnDelete, btnAdd;
    private boolean isEditing = false;
    private boolean isAddingNew = false;
    
    // Controller
    private SupplierController supplierController;

    /**
     * Tạo form quản lý nhà cung cấp
     */
    public SupplierForm() {
        setLayout(new BorderLayout());
        setBackground(new Color(242, 245, 250));
        
        // Khởi tạo giao diện
        createTopPanel();
        initComponents();
        
        // Khởi tạo controller
        supplierController = SupplierController.getInstance(this);
        
        // Khởi tạo trạng thái nút
        updateButtonStates();
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
        btnAdd.setText("Thêm");
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
        btnSave.setText("Lưu");
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
        btnDelete.setText("Xóa");
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
        btnRefresh.setText("Làm mới");
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
        String[] columns = {"Mã NCC", "Tên NCC", "SĐT", "Email", "Địa chỉ"};
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
            
            // Thêm sự kiện key listener để theo dõi khi người dùng bắt đầu chỉnh sửa
            f.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if ((!isEditing && !isAddingNew) && f.isEditable()) {
                        isEditing = true;
                        updateButtonStates();
                    }
                }
            });
        }

        // Tạo các label cho form
        JLabel lbId = new JLabel("Mã NCC:");
        JLabel lbName = new JLabel("Tên NCC:");
        JLabel lbPhone = new JLabel("SĐT:");
        JLabel lbEmail = new JLabel("Email:");
        JLabel lbAddress = new JLabel("Địa chỉ:");
        
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
        
        // Thêm các listeners cho các nút
        setupButtonListeners();
    }
    
    /**
     * Thiết lập sự kiện cho các nút
     */
    private void setupButtonListeners() {
        // Nút Làm mới
        btnRefresh.addActionListener(e -> {
            clearForm();
            isEditing = false;
            isAddingNew = false;
            updateButtonStates();
        });
        
        // Nút Lưu
        btnSave.addActionListener(e -> {
            if (isAddingNew) {
                // Thêm mới
                saveNewSupplier();
            } else if (isEditing) {
                // Cập nhật
                updateSupplier();
            }
            isEditing = false;
            isAddingNew = false;
            updateButtonStates();
        });
        
        // Nút Xóa
        btnDelete.addActionListener(e -> {
            deleteSupplier();
        });
        
        // Nút Thêm
        btnAdd.addActionListener(e -> {
            prepareForNewSupplier();
        });
        
        // Sự kiện chọn dòng trong bảng
        tableSuppliers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableSuppliers.getSelectedRow();
                if (selectedRow >= 0 && !isAddingNew) {
                    isAddingNew = false; // Hủy chế độ thêm mới nếu chọn một dòng
                    updateButtonStates(true);
                } else {
                    updateButtonStates(false);
                }
            }
        });
    }
    
    /**
     * Chuẩn bị form để thêm mới nhà cung cấp
     */
    private void prepareForNewSupplier() {
        clearForm();
        isAddingNew = true;
        isEditing = false;
        
        // Đặt ID tạm thời
        txtSupplierId.setText("[Tự động]");
        
        // Kích hoạt nút lưu, vô hiệu nút xóa
        btnSave.setEnabled(true);
        btnDelete.setEnabled(false);
        
        // Focus vào trường đầu tiên
        txtSupplierName.requestFocus();
    }
    
    /**
     * Cập nhật trạng thái kích hoạt/vô hiệu của các nút
     * @param isRowSelected có dòng nào được chọn không
     */
    private void updateButtonStates(boolean isRowSelected) {
        if (isAddingNew) {
            btnDelete.setEnabled(false);
            btnSave.setEnabled(true);
            btnAdd.setEnabled(false);
        } else {
            btnDelete.setEnabled(isRowSelected);
            btnSave.setEnabled(isEditing && isRowSelected);
            btnAdd.setEnabled(true);
        }
    }
    
    /**
     * Cập nhật trạng thái kích hoạt/vô hiệu của các nút
     */
    private void updateButtonStates() {
        updateButtonStates(tableSuppliers.getSelectedRow() >= 0);
    }
    
    /**
     * Lưu thông tin nhà cung cấp mới
     */
    private void saveNewSupplier() {
        // Kiểm tra dữ liệu nhập
        String name = txtSupplierName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();
        
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Tên và số điện thoại là bắt buộc.",
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Tạo đối tượng nhà cung cấp mới
            Supplier supplier = new Supplier();
            supplier.setName(name);
            supplier.setPhoneNumber(phone);
            supplier.setEmail(email);
            supplier.setAddress(address);
            
            // Gọi thêm mới từ controller
            supplierController.addNewSupplier(supplier);
            
            JOptionPane.showMessageDialog(this,
                    "Thêm mới nhà cung cấp thành công!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            
            // Làm mới bảng và form
            supplierController.loadAllSuppliers();
            clearForm();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi thêm mới: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Cập nhật thông tin nhà cung cấp hiện có
     */
    private void updateSupplier() {
        // Kiểm tra dữ liệu nhập
        String name = txtSupplierName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();
        
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Tên và số điện thoại là bắt buộc.",
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Nếu có ID (đang sửa)
        String id = txtSupplierId.getText().trim();
        if (!id.isEmpty() && !id.equals("[Tự động]")) {
            try {
                // Tạo đối tượng nhà cung cấp
                Supplier supplier = new Supplier();
                supplier.setSupplierId(id);
                supplier.setName(name);
                supplier.setPhoneNumber(phone);
                supplier.setEmail(email);
                supplier.setAddress(address);
                
                // Gọi cập nhật từ controller
                supplierController.updateSupplier(supplier);
                
                JOptionPane.showMessageDialog(this,
                        "Cập nhật thành công!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                
                // Làm mới bảng và form
                supplierController.loadAllSuppliers();
                clearForm();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi cập nhật: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Xóa nhà cung cấp
     */
    private void deleteSupplier() {
        int selectedRow = tableSuppliers.getSelectedRow();
        if (selectedRow >= 0) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa nhà cung cấp này không?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                // Chuyển đổi từ model view index sang model index nếu có sorter
                if (tableSuppliers.getRowSorter() != null) {
                    selectedRow = tableSuppliers.getRowSorter().convertRowIndexToModel(selectedRow);
                }
                
                String id = tableModel.getValueAt(selectedRow, 0).toString();
                try {
                    supplierController.deleteSupplier(id);
                    JOptionPane.showMessageDialog(this,
                            "Xóa thành công!",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    supplierController.loadAllSuppliers();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Lỗi khi xóa: " + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    /**
     * Xóa form
     */
    private void clearForm() {
        txtSupplierId.setText("");
        txtSupplierName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        tableSuppliers.clearSelection();
        isEditing = false;
        isAddingNew = false;
        updateButtonStates();
    }
        // Add this method to SupplierForm class
    
    /**
     * Reset trạng thái thêm mới
     */
    public void resetAddingState() {
        this.isAddingNew = false;
        this.isEditing = false;
        updateButtonStates();
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

    // Getters cho controller để truy cập components
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

    public JTextField getTxtSearch() {
        return txtSearch;
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
    
    // Getter để kiểm tra trạng thái
    public boolean isAddingNew() {
        return isAddingNew;
    }
}