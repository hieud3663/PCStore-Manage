package com.pcstore.utils;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.RowSorter.SortKey;
import javax.swing.RowFilter;
import javax.swing.SortOrder;
import java.util.regex.Pattern;


public class TableStyleUtil {
    
    
    public static TableRowSorter<TableModel> applyDefaultStyle(JTable table) {
        applyHeaderStyle(table);
        
        applyCenterAlignment(table);
        
        TableRowSorter<TableModel> sorter = setupSorting(table);
        
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setSelectionForeground(Color.BLACK);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 12);
        TableStyleUtil.setSelectedRowFont(table, boldFont);
        table.getTableHeader().setReorderingAllowed(false);
        
        return sorter;
    }
    



    /**
     * style cho header bảng
     * @param table Bảng cần tùy chỉnh
     */
    public static void applyHeaderStyle(JTable table) {
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBackground(new Color(51, 153, 255));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                RowSorter<?> sorter = table.getRowSorter();
                if (sorter != null && sorter.getSortKeys().size() > 0) {
                    SortKey sortKey = sorter.getSortKeys().get(0);
                    if (sortKey.getColumn() == table.convertColumnIndexToModel(column)) {
                        String text = value.toString();
                        if (sortKey.getSortOrder() == SortOrder.ASCENDING) {
                            text += " ▲"; 
                        } else if (sortKey.getSortOrder() == SortOrder.DESCENDING) {
                            text += " ▼"; 
                        }
                        label.setText(text);
                    }
                }
                
                return label;
            }
        });
    }
    
    /**
     * Căn giữa nội dung 
     * @param table Bảng cần tùy chỉnh
     */
    public static void applyCenterAlignment(JTable table) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    /**
     * Thiết lập font cho hàng được chọn trong bảng
     * @param table Bảng cần tùy chỉnh
     * @param font Font áp dụng cho hàng được chọn
     */
    public static void setSelectedRowFont(JTable table, Font font) {
        // Lưu font mặc định của bảng để sử dụng cho các hàng không được chọn
        final Font defaultFont = table.getFont();
        
        // Tạo renderer tùy chỉnh
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Thiết lập font dựa vào trạng thái lựa chọn
                if (isSelected) {
                    c.setFont(font);
                } else {
                    c.setFont(defaultFont);
                }
                
                // Giữ căn chỉnh giữa nếu đã được áp dụng trước đó
                ((JLabel)c).setHorizontalAlignment(JLabel.CENTER);
                
                return c;
            }
        };
        
        // Áp dụng renderer tùy chỉnh cho tất cả các cột trong bảng
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }
    }
    
    /**
     * Thiết lập chức năng sắp xếp cho bảng
     * @param table Bảng cần thiết lập sắp xếp
     * @return TableRowSorter được tạo
     */
    public static TableRowSorter<TableModel> setupSorting(JTable table) {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        return sorter;
    }
    
    /**
     * Vô hiệu hóa sắp xếp cho các cột cụ thể
     * @param sorter TableRowSorter để thiết lập
     * @param columns Mảng chỉ số cột cần vô hiệu hóa sắp xếp
     */
    public static void disableSortingForColumns(TableRowSorter<TableModel> sorter, int... columns) {
        for (int column : columns) {
            sorter.setSortable(column, false);
        }
    }

    /**
     * Thiết lập các cột boolean hiển thị checkbox
     * @param table Bảng cần tùy chỉnh
     * @param columns Các cột boolean cần hiển thị checkbox
     */
    public static void setBooleanColumns(JTable table, int... columns) {
        for (int column : columns) {
            if (column >= 0 && column < table.getColumnCount()) {
                table.getColumnModel().getColumn(column).setCellRenderer(new DefaultTableCellRenderer() {
                    private final JCheckBox checkBox = new JCheckBox();
                    
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, 
                            boolean isSelected, boolean hasFocus, int row, int column) {
                        if (value instanceof Boolean) {
                            checkBox.setSelected((Boolean) value);
                            checkBox.setHorizontalAlignment(JCheckBox.CENTER);
                            checkBox.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                            return checkBox;
                        } else {
                            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        }
                    }
                });
                

                table.getColumnModel().getColumn(column).setCellEditor(new DefaultCellEditor(new JCheckBox()));
            }
        }
    }
    
    /**
     * Thiết lập bộ lọc cho bảng dựa trên từ khóa tìm kiếm
     * @param sorter TableRowSorter để thiết lập bộ lọc
     * @param searchText Từ khóa tìm kiếm
     * @param columns Các cột cần tìm kiếm
     */
    public static void applyFilter(TableRowSorter<TableModel> sorter, String searchText, int... columns) {
        if (searchText.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                RowFilter<TableModel, Object> filter = RowFilter.regexFilter("(?i)" + Pattern.quote(searchText.trim()));
                sorter.setRowFilter(filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Thiết lập kích thước cột và ẩn/hiện cột
     * @param table Bảng cần tùy chỉnh
     * @param columnWidths Mảng kích thước cho từng cột (0 hoặc âm để giữ nguyên kích thước)
     */
    public static void setupColumnWidths(JTable table, int... columnWidths) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnWidths.length && i < columnModel.getColumnCount(); i++) {
            if (columnWidths[i] > 0) {
                columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
                columnModel.getColumn(i).setMaxWidth(columnWidths[i]);
            }
        }
    }
    
    /**
     * Tùy chỉnh renderer cho 1 cột
     * @param table Bảng cần tùy chỉnh
     * @param renderer TableCellRenderer để áp dụng
     * @param columns Các cột cần áp dụng renderer
     */
    public static void setColumnRenderer(JTable table, TableCellRenderer renderer, int... columns) {
        for (int column : columns) {
            if (column >= 0 && column < table.getColumnCount()) {
                table.getColumnModel().getColumn(column).setCellRenderer(renderer);
            }
        }
    }
    
    /**
     * Tạo DefaultTableCellRenderer với căn lề tùy chỉnh
     * @param alignment Căn lề (SwingConstants.LEFT, CENTER, RIGHT)
     * @return DefaultTableCellRenderer với căn lề đã thiết lập
     */
    public static DefaultTableCellRenderer createAlignmentRenderer(int alignment) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(alignment);
        return renderer;
    }
}