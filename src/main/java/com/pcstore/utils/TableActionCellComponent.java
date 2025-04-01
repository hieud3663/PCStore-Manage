package com.pcstore.utils;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Component kết hợp cả chức năng renderer và editor cho các nút hành động trong JTable
 */
public class TableActionCellComponent extends AbstractCellEditor 
        implements TableCellRenderer, TableCellEditor {
    
    private static final long serialVersionUID = 1L;
    
    // Tạo 2 panel riêng biệt cho renderer và editor
    private JPanel panelRenderer;
    private JPanel panelEditor;
    
    // Nút cho renderer (chỉ để hiển thị)
    private JButton btnViewRenderer;
    private JButton btnEditRenderer;
    private JButton btnDeleteRenderer;
    
    // Nút cho editor (xử lý sự kiện)
    private JButton btnViewEditor;
    private JButton btnEditEditor;
    private JButton btnDeleteEditor;
    
    private int row;
    private IActionButtonTableListener listener;
    
    /**
     * Khởi tạo component với listener xử lý sự kiện các nút
     * @param listener Listener xử lý sự kiện các nút
     */
    public TableActionCellComponent(IActionButtonTableListener listener) {
        this.listener = listener;
        
        // Tạo panel và nút cho renderer
        createRendererPanel();
        
        // Tạo panel và nút cho editor
        createEditorPanel();
    }
    
    /**
     * Tạo panel và các nút chỉ để hiển thị (không xử lý sự kiện)
     */
    private void createRendererPanel() {
        panelRenderer = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        
        // Tạo nút Xem
        btnViewRenderer = createButton("Xem", "/com/pcstore/resources/icon/eye.png");
        
        // Tạo nút Sửa
        btnEditRenderer = createButton("Sửa", "/com/pcstore/resources/icon/edit.png");
        
        // Tạo nút Xóa
        btnDeleteRenderer = createButton("Xóa", "/com/pcstore/resources/icon/delete.png");
        
        // Thêm nút vào panel
        panelRenderer.add(btnViewRenderer);
        panelRenderer.add(btnEditRenderer);
        panelRenderer.add(btnDeleteRenderer);
    }
    
    /**
     * Tạo panel và các nút có khả năng xử lý sự kiện
     */
    private void createEditorPanel() {
        panelEditor = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        
        // Tạo nút Xem
        btnViewEditor = createButton("Xem", "/com/pcstore/resources/icon/eye.png");
        btnViewEditor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null) {
                    listener.onViewButtonClick(row);
                }
                fireEditingStopped();
            }
        });
        
        // Tạo nút Sửa
        btnEditEditor = createButton("Sửa", "/com/pcstore/resources/icon/edit.png");
        btnEditEditor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null) {
                    listener.onEditButtonClick(row);
                }
                fireEditingStopped();
            }
        });
        
        // Tạo nút Xóa
        btnDeleteEditor = createButton("Xóa", "/com/pcstore/resources/icon/delete.png");
        btnDeleteEditor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null) {
                    listener.onDeleteButtonClick(row);
                }
                fireEditingStopped();
            }
        });
        
        // Thêm nút vào panel
        panelEditor.add(btnViewEditor);
        panelEditor.add(btnEditEditor);
        panelEditor.add(btnDeleteEditor);
    }
    
    /**
     * Helper method để tạo một nút với text và icon
     */
    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFocusPainted(false);
        // button.setPreferredSize(new Dimension(60, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        try {
            button.setIcon(new javax.swing.ImageIcon(getClass().getResource(iconPath)));
            button.setText(""); // Nếu có icon thì không hiển thị text
        } catch (Exception e) {
            // Nếu không tìm thấy icon, hiển thị text
        }
        
        return button;
    }
    
    // Triển khai phương thức của TableCellRenderer
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        // Cập nhật trạng thái hiển thị cho renderer
        if (isSelected) {
            panelRenderer.setBackground(table.getSelectionBackground());
            btnViewRenderer.setBackground(table.getSelectionBackground());
            btnEditRenderer.setBackground(table.getSelectionBackground());
            btnDeleteRenderer.setBackground(table.getSelectionBackground());
        } else {
            panelRenderer.setBackground(table.getBackground());
            btnViewRenderer.setBackground(UIManager.getColor("Button.background"));
            btnEditRenderer.setBackground(UIManager.getColor("Button.background"));
            btnDeleteRenderer.setBackground(UIManager.getColor("Button.background"));
        }
        
        return panelRenderer;
    }
    
    // Triển khai phương thức của TableCellEditor
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        // Lưu dòng hiện tại và cập nhật giao diện cho editor
        this.row = row;
        
        panelEditor.setBackground(table.getSelectionBackground());
        
        return panelEditor;
    }
    
    @Override
    public boolean isCellEditable(EventObject e) {
        return true;
    }
    
    @Override
    public Object getCellEditorValue() {
        return "";
    }
    
    /**
     * Helper method để thiết lập cột chứa nút hành động trong bảng
     * @param table Bảng cần thiết lập
     * @param columnIndex Chỉ số cột chứa nút hành động
     * @param listener Listener xử lý sự kiện nút
     */
    public static void setupActionColumn(JTable table, int columnIndex, IActionButtonTableListener listener) {
        // Tạo một instance của component
        TableActionCellComponent actionComponent = new TableActionCellComponent(listener);
        
        // Thiết lập renderer và editor cho cột
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(actionComponent);
        table.getColumnModel().getColumn(columnIndex).setCellEditor(actionComponent);
        
        // Điều chỉnh chiều cao hàng để hiển thị các nút
        table.setRowHeight(35);
        
        // Điều chỉnh kích thước cột
        // table.getColumnModel().getColumn(columnIndex).setPreferredWidth(200);
        // table.getColumnModel().getColumn(columnIndex).setMinWidth(180);
        
        // Đảm bảo cột đó có thể chỉnh sửa
        if (table.getModel() instanceof javax.swing.table.DefaultTableModel) {
            ((javax.swing.table.DefaultTableModel) table.getModel()).isCellEditable(0, columnIndex);
        }
    }
}