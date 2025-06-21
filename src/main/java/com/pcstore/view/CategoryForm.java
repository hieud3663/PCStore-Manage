package com.pcstore.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.pcstore.utils.TableUtils;
import com.pcstore.utils.LocaleManager;

/**
 * Modern Category Management Form
 * @author nloc2
 */
public class CategoryForm extends JPanel {
    
    // Components
    private JPanel headerPanel;
    private JPanel searchPanel;
    private JPanel contentPanel;
    private JPanel tablePanel;
    private JPanel detailsPanel;
    private JPanel actionPanel;
    
    private JLabel titleLabel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    
    private JTable categoryTable;
    private JScrollPane tableScrollPane;
    
    private JTextField txtCategoryID;
    private JTextField txtCategoryName;
    private JTextArea txtDescription;
    private JCheckBox chkActive;
    private JLabel lblCreatedAt;
    private JLabel lblUpdatedAt;
    
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnClose;
    
    // Colors for modern design
    private static final Color PRIMARY_COLOR = new Color(64, 123, 255);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private static final Color LIGHT_GRAY = new Color(248, 249, 250);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    
    public CategoryForm() {
        initializeComponents();
        setupLayout();
        setupTable();
        setupEventListeners();
        applyModernStyling();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Header Panel
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        titleLabel = new JLabel("Quản Lý Danh Mục Sản Phẩm");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Search Panel
        searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        searchPanel.setBackground(LIGHT_GRAY);
        searchPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        searchField.setToolTipText("Tìm kiếm danh mục...");
        
        searchButton = createModernButton("Tìm kiếm", PRIMARY_COLOR, "/icon/search.png");
        refreshButton = createModernButton("Làm mới", SECONDARY_COLOR, "/icon/refresh.png");
        
        // Content Panel
        contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Table Panel
        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            "Danh Sách Danh Mục",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_COLOR
        ));
        
        // Details Panel
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            "Thông Tin Danh Mục",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY_COLOR
        ));
        detailsPanel.setPreferredSize(new Dimension(400, 0));
        
        initializeDetailComponents();
        
        // Action Panel
        actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        actionPanel.setBackground(LIGHT_GRAY);
        actionPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        
        initializeActionButtons();
    }
    
    private void initializeDetailComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Category ID
        gbc.gridx = 0; gbc.gridy = 0;
        detailsPanel.add(createLabel("Mã danh mục:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtCategoryID = createModernTextField();
        txtCategoryID.setEditable(false);
        txtCategoryID.setBackground(LIGHT_GRAY);
        detailsPanel.add(txtCategoryID, gbc);
        
        // Category Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        detailsPanel.add(createLabel("Tên danh mục:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtCategoryName = createModernTextField();
        detailsPanel.add(txtCategoryName, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        detailsPanel.add(createLabel("Mô tả:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        txtDescription = new JTextArea(4, 20);
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDescription.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(txtDescription);
        descScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        detailsPanel.add(descScrollPane, gbc);
        
        // Active Status
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        gbc.gridwidth = 2;
        chkActive = new JCheckBox("Đang hoạt động");
        chkActive.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chkActive.setBackground(Color.WHITE);
        chkActive.setForeground(SUCCESS_COLOR);
        chkActive.setSelected(true);
        detailsPanel.add(chkActive, gbc);
        
        // Created At
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        detailsPanel.add(createLabel("Ngày tạo:"), gbc);
        
        gbc.gridx = 1;
        lblCreatedAt = createInfoLabel("Chưa có thông tin");
        detailsPanel.add(lblCreatedAt, gbc);
        
        // Updated At
        gbc.gridx = 0; gbc.gridy = 5;
        detailsPanel.add(createLabel("Cập nhật:"), gbc);
        
        gbc.gridx = 1;
        lblUpdatedAt = createInfoLabel("Chưa có thông tin");
        detailsPanel.add(lblUpdatedAt, gbc);
    }
    
    private void initializeActionButtons() {
        btnAdd = createModernButton("Thêm mới", SUCCESS_COLOR, "/icon/add.png");
        btnUpdate = createModernButton("Cập nhật", PRIMARY_COLOR, "/icon/edit.png");
        btnDelete = createModernButton("Xóa", DANGER_COLOR, "/icon/delete.png");
        btnClear = createModernButton("Làm mới", WARNING_COLOR, "/icon/clear.png");
        btnClose = createModernButton("Đóng", SECONDARY_COLOR, "/icon/close.png");
        
        actionPanel.add(btnAdd);
        actionPanel.add(btnUpdate);
        actionPanel.add(btnDelete);
        actionPanel.add(btnClear);
        actionPanel.add(btnClose);
    }
    
    private void setupLayout() {
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        
        tablePanel.add(createTableScrollPane(), BorderLayout.CENTER);
        
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        contentPanel.add(detailsPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.add(contentPanel, BorderLayout.CENTER);
        mainContent.add(actionPanel, BorderLayout.SOUTH);
        
        add(mainContent, BorderLayout.CENTER);
    }
    
    private JScrollPane createTableScrollPane() {
        categoryTable = new JTable();
        setupTableModel();
        
        tableScrollPane = new JScrollPane(categoryTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        
        return tableScrollPane;
    }
    
    private void setupTable() {
        categoryTable.setRowHeight(40);
        categoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryTable.setSelectionBackground(new Color(230, 240, 255));
        categoryTable.setSelectionForeground(Color.BLACK);
        categoryTable.setGridColor(BORDER_COLOR);
        categoryTable.setShowGrid(true);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Apply modern table styling
        TableUtils.applyDefaultStyle(categoryTable);
        
        // Add selection listener
        categoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedCategory();
            }
        });
    }
    
    private void setupTableModel() {
        String[] columnNames = {
            "Mã danh mục", "Tên danh mục", "Mô tả", "Trạng thái", "Ngày tạo", "Hành động"
        };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only action column is editable
            }
        };
        
        // Sample data
        model.addRow(new Object[]{
            "CAT001", "Laptop", "Máy tính xách tay", "Hoạt động", "01/01/2024", ""
        });
        model.addRow(new Object[]{
            "CAT002", "Desktop", "Máy tính để bàn", "Hoạt động", "01/01/2024", ""
        });
        model.addRow(new Object[]{
            "CAT003", "Phụ kiện", "Phụ kiện máy tính", "Hoạt động", "01/01/2024", ""
        });
        
        categoryTable.setModel(model);
        
        // Set column widths
        categoryTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        categoryTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        categoryTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        categoryTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        categoryTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        categoryTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        
        // Add custom renderer and editor for action column
        categoryTable.getColumn("Hành động").setCellRenderer(new ActionButtonRenderer());
        categoryTable.getColumn("Hành động").setCellEditor(new ActionButtonEditor());
    }
    
    private void setupEventListeners() {
        btnAdd.addActionListener(e -> addCategory());
        btnUpdate.addActionListener(e -> updateCategory());
        btnDelete.addActionListener(e -> deleteCategory());
        btnClear.addActionListener(e -> clearForm());
        btnClose.addActionListener(e -> closeForm());
        
        searchButton.addActionListener(e -> searchCategories());
        refreshButton.addActionListener(e -> refreshTable());
        
        // Add hover effects
        addHoverEffect(btnAdd);
        addHoverEffect(btnUpdate);
        addHoverEffect(btnDelete);
        addHoverEffect(btnClear);
        addHoverEffect(btnClose);
        addHoverEffect(searchButton);
        addHoverEffect(refreshButton);
    }
    
    private void applyModernStyling() {
        // Add subtle shadows and rounded corners using custom painting
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
    }
    
    // Helper methods for creating components
    private JButton createModernButton(String text, Color bgColor, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Try to load icon if path is provided
        try {
            if (iconPath != null && !iconPath.isEmpty()) {
                ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
                if (icon.getIconWidth() > 0) {
                    button.setIcon(icon);
                }
            }
        } catch (Exception e) {
            // Icon not found, continue without icon
        }
        
        return button;
    }
    
    private JTextField createModernTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return textField;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(52, 58, 64));
        return label;
    }
    
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(SECONDARY_COLOR);
        return label;
    }
    
    private void addHoverEffect(JButton button) {
        Color originalColor = button.getBackground();
        Color hoverColor = originalColor.darker();
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
    }
    
    // Action methods
    private void addCategory() {
        if (validateInput()) {
            // Add logic to save new category
            JOptionPane.showMessageDialog(this, 
                "Thêm danh mục mới thành công!", 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refreshTable();
        }
    }
    
    private void updateCategory() {
        if (categoryTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn danh mục cần cập nhật!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (validateInput()) {
            // Add logic to update category
            JOptionPane.showMessageDialog(this, 
                "Cập nhật danh mục thành công!", 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        }
    }
    
    private void deleteCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn danh mục cần xóa!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa danh mục này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (result == JOptionPane.YES_OPTION) {
            // Add logic to delete category
            ((DefaultTableModel) categoryTable.getModel()).removeRow(selectedRow);
            clearForm();
            JOptionPane.showMessageDialog(this, 
                "Xóa danh mục thành công!", 
                "Thành công", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void clearForm() {
        txtCategoryID.setText("");
        txtCategoryName.setText("");
        txtDescription.setText("");
        chkActive.setSelected(true);
        lblCreatedAt.setText("Chưa có thông tin");
        lblUpdatedAt.setText("Chưa có thông tin");
        categoryTable.clearSelection();
    }
    
    private void closeForm() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }
    
    private void searchCategories() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            refreshTable();
            return;
        }
        
        // Add search logic here
        JOptionPane.showMessageDialog(this, 
            "Tìm kiếm: " + searchText, 
            "Tìm kiếm", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void refreshTable() {
        // Add logic to reload data from database
        setupTableModel();
        clearForm();
    }
    
    private void loadSelectedCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) categoryTable.getModel();
            txtCategoryID.setText(model.getValueAt(selectedRow, 0).toString());
            txtCategoryName.setText(model.getValueAt(selectedRow, 1).toString());
            txtDescription.setText(model.getValueAt(selectedRow, 2).toString());
            chkActive.setSelected("Hoạt động".equals(model.getValueAt(selectedRow, 3).toString()));
            lblCreatedAt.setText(model.getValueAt(selectedRow, 4).toString());
            lblUpdatedAt.setText("01/01/2024"); // Sample date
        }
    }
    
    private boolean validateInput() {
        if (txtCategoryName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập tên danh mục!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            txtCategoryName.requestFocus();
            return false;
        }
        
        return true;
    }
    
    // Custom renderer for action buttons in table
    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton = new JButton("Sửa");
        private JButton deleteButton = new JButton("Xóa");
        
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
            
            editButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            editButton.setBackground(PRIMARY_COLOR);
            editButton.setForeground(Color.WHITE);
            editButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            editButton.setFocusPainted(false);
            
            deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            deleteButton.setBackground(DANGER_COLOR);
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            deleteButton.setFocusPainted(false);
            
            add(editButton);
            add(deleteButton);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    
    // Custom editor for action buttons in table
    class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel = new JPanel();
        private JButton editButton = new JButton("Sửa");
        private JButton deleteButton = new JButton("Xóa");
        private int currentRow;
        
        public ActionButtonEditor() {
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
            
            editButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            editButton.setBackground(PRIMARY_COLOR);
            editButton.setForeground(Color.WHITE);
            editButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            editButton.setFocusPainted(false);
            
            deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            deleteButton.setBackground(DANGER_COLOR);
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            deleteButton.setFocusPainted(false);
            
            editButton.addActionListener(e -> {
                categoryTable.setRowSelectionInterval(currentRow, currentRow);
                loadSelectedCategory();
                fireEditingStopped();
            });
            
            deleteButton.addActionListener(e -> {
                categoryTable.setRowSelectionInterval(currentRow, currentRow);
                deleteCategory();
                fireEditingStopped();
            });
            
            panel.add(editButton);
            panel.add(deleteButton);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }
    
    // Getter methods for external access
    public JTable getCategoryTable() { return categoryTable; }
    public JTextField getTxtCategoryID() { return txtCategoryID; }
    public JTextField getTxtCategoryName() { return txtCategoryName; }
    public JTextArea getTxtDescription() { return txtDescription; }
    public JCheckBox getChkActive() { return chkActive; }
    public JButton getBtnAdd() { return btnAdd; }
    public JButton getBtnUpdate() { return btnUpdate; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnClear() { return btnClear; }
    public JButton getBtnClose() { return btnClose; }
}