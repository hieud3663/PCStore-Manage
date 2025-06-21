package com.pcstore.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.formdev.flatlaf.extras.FlatSVGIcon;


public class TableUtils {
    
    // Định nghĩa các màu sắc cảnh báo
    public static final Color ZERO_QUANTITY_COLOR = new Color(255, 102, 102); // Màu đỏ nhạt
    public static final Color LOW_QUANTITY_COLOR = new Color(255, 204, 102);  // Màu cam nhạt

    // Định nghĩa các màu sắc cho trạng thái hóa đơn
    public static final Color PROCESSING_COLOR = new Color(255, 204, 102);  // Màu cam nhạt cho "Đang xử lý"
    public static final Color CANCELLED_COLOR = new Color(255, 102, 102);   // Màu đỏ nhạt cho "Đã hủy"
    public static final Color COMPLETED_COLOR = new Color(204, 255, 204);   // Màu xanh nhạt cho "Đã hoàn thành"
        


    public static TableRowSorter<TableModel> applyDefaultStyle(JTable table) {
        applyHeaderStyle(table);
        
        applyCenterAlignment(table);
        
        TableRowSorter<TableModel> sorter = setupSorting(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Thêm comparator đặc biệt cho cột STT
        for (int i = 0; i < table.getColumnCount(); i++) {
            
                final int columnIndex = i;
                sorter.setComparator(columnIndex, new Comparator<Object>() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        if (o1 instanceof Integer && o2 instanceof Integer) {
                            return ((Integer) o1).compareTo((Integer) o2);
                        }
                        try {
                            int val1 = Integer.parseInt(o1.toString());
                            int val2 = Integer.parseInt(o2.toString());
                            return Integer.compare(val1, val2);
                        } catch (NumberFormatException e) {
                            return o1.toString().compareTo(o2.toString());
                        }
                    }
                });
        }
        
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setSelectionForeground(Color.BLACK);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 12);
        TableUtils.setSelectedRowFont(table, boldFont);
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


    /*
     * Thiết lập cột có số
     */
    public static void setNumberColumns(TableRowSorter<TableModel> tableSort, int... columns) {
        for (int column : columns) {
            tableSort.setSortable(column, true);
            tableSort.setComparator(column, new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    if (o1 instanceof Number && o2 instanceof Number) {
                        return Double.compare(((Number) o1).doubleValue(), ((Number) o2).doubleValue());
                    }else{
                        try {
                            String v1 = o1.toString().replaceAll("[^0-9.]", "").replaceAll("\\.", "");
                            String v2 = o2.toString().replaceAll("[^0-9.]", "").replaceAll("\\.", "");

                            double d1 = Double.parseDouble(v1);
                            double d2 = Double.parseDouble(v2);
                            
                            return Double.compare(d1, d2);
                        } catch (Exception e) {
                            return o1.toString().compareTo(o2.toString());
                        }
                    }
                }
            });
        }
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
     * @param columns Các cột cần tìm kiếm (nếu không chỉ định, tìm trên tất cả các cột)
     */
    public static void applyFilter(TableRowSorter<TableModel> sorter, String searchText, int... columns) {
        if (searchText == null || searchText.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                String regex = "(?i)" + Pattern.quote(searchText.trim()); 
                
                if (columns.length > 0) {
                    List<RowFilter<TableModel, Object>> filters = new ArrayList<>();
                    for (int column : columns) {
                        filters.add(RowFilter.regexFilter(regex, column));
                    }
                    sorter.setRowFilter(RowFilter.orFilter(filters));
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter(regex));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    

    //refresh sorter cho bảng
    
    public static void refreshSorter(JTable table) {
        if (table.getRowSorter() != null) {
            table.getRowSorter().setSortKeys(null);
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

    /**
     * Áp dụng định dạng màu sắc cho bảng sản phẩm
     * - Số lượng = 0: Màu đỏ
     * - Số lượng < 3: Màu cam
     * 
     * @param table Bảng sản phẩm cần áp dụng định dạng
     * @param quantityColumnIndex Chỉ số cột chứa thông tin số lượng
     * @return TableRowSorter đã được tạo
     */
    public static TableRowSorter<TableModel> applyProductTableStyle(JTable table, final int quantityColumnIndex) {
        
        // Áp dụng các style cơ bản từ lớp cha
        TableRowSorter<TableModel> sorter = applyDefaultStyle(table);
        
        // Tạo custom renderer cho việc hiển thị màu sắc theo số lượng
        DefaultTableCellRenderer quantityRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) { // Chỉ áp dụng màu nền tùy chỉnh khi dòng không được chọn
                    // Lấy giá trị số lượng từ cột số lượng
                    Object quantityObj = table.getValueAt(row, quantityColumnIndex);
                    int quantity = 0;
                    
                    // Chuyển đổi giá trị số lượng sang kiểu int
                    if (quantityObj != null) {
                        try {
                            if (quantityObj instanceof Integer) {
                                quantity = (Integer) quantityObj;
                            } else {
                                quantity = Integer.parseInt(quantityObj.toString());
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    // Áp dụng màu sắc dựa trên số lượng
                    if (quantity == 0) {
                        c.setBackground(ZERO_QUANTITY_COLOR);
                    } else if (quantity < 3) {
                        c.setBackground(LOW_QUANTITY_COLOR);
                    } else {
                        c.setBackground(table.getBackground());
                    }
                }
                
                // Duy trì căn giữa nội dung
                ((JLabel)c).setHorizontalAlignment(JLabel.CENTER);
                
                return c;
            }
        };
        
        // Áp dụng renderer cho tất cả các cột
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(quantityRenderer);
        }
        
        return sorter;
    }
    
    /**
     * Tô màu cho từng ô trong cột số lượng (thay vì toàn bộ dòng)
     * 
     * @param table Bảng sản phẩm cần áp dụng định dạng
     * @param quantityColumnIndex Chỉ số cột chứa thông tin số lượng
     */
    public static void applyQuantityColumnStyle(JTable table, final int quantityColumnIndex) {
        DefaultTableCellRenderer quantityRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected && column == quantityColumnIndex) {
                    int quantity = 0;
                    if (value != null) {
                        try {
                            if (value instanceof Integer) {
                                quantity = (Integer) value;
                            } else {
                                quantity = Integer.parseInt(value.toString());
                            }
                        } catch (NumberFormatException e) {
                            // Xử lý lỗi nếu giá trị không phải số
                        }
                    }
                    
                    // Áp dụng màu sắc và in đậm chữ
                    if (quantity == 0) {
                        c.setBackground(ZERO_QUANTITY_COLOR);
                        c.setForeground(Color.WHITE);
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else if (quantity < 3) {
                        c.setBackground(LOW_QUANTITY_COLOR);
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setBackground(table.getBackground());
                        c.setForeground(table.getForeground());
                    }
                }
                
                // Duy trì căn giữa nội dung
                ((JLabel)c).setHorizontalAlignment(JLabel.CENTER);
                
                return c;
            }
        };
        
        // Chỉ áp dụng renderer cho cột số lượng
        table.getColumnModel().getColumn(quantityColumnIndex).setCellRenderer(quantityRenderer);
    }

        
    /**
     * Áp dụng định dạng màu sắc cho bảng hóa đơn dựa trên trạng thái
     * - Đang xử lý: Màu cam
     * - Đã hủy: Màu đỏ
     * 
     * @param table Bảng hóa đơn cần áp dụng định dạng
     * @param statusColumnIndex Chỉ số cột chứa thông tin trạng thái hóa đơn
     * @return TableRowSorter đã được tạo
     */
    public static TableRowSorter<TableModel> applyInvoiceTableStyle(JTable table, final int statusColumnIndex) {
        // Áp dụng các style cơ bản từ lớp cha
        TableRowSorter<TableModel> sorter = applyDefaultStyle(table);
        
        // Tạo custom renderer cho việc hiển thị màu sắc theo trạng thái
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) { // Chỉ áp dụng màu nền tùy chỉnh khi dòng không được chọn
                    // Lấy giá trị trạng thái từ cột trạng thái
                    Object statusObj = table.getValueAt(row, statusColumnIndex);
                    String status = "";
                    
                    if (statusObj != null) {
                        status = statusObj.toString().trim().toLowerCase();
                    }
                    
                    // Áp dụng màu sắc dựa trên trạng thái
                    if (status.contains("xử lý") || status.contains("processing") || status.contains("pending")) {
                        c.setBackground(PROCESSING_COLOR);
                    } else if (status.contains("hủy") || status.contains("cancelled") || status.contains("canceled")) {
                        c.setBackground(CANCELLED_COLOR);
                    } else if (status.contains("hoàn thành") || status.contains("completed") || status.contains("done")) {
                        c.setBackground(COMPLETED_COLOR);
                    } else {
                        c.setBackground(table.getBackground());
                    }
                }
                
                // Duy trì căn giữa nội dung
                ((JLabel)c).setHorizontalAlignment(JLabel.CENTER);
                
                return c;
            }
        };
        
        // Áp dụng renderer cho tất cả các cột
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(statusRenderer);
        }
        
        return sorter;
    }
    
    /**
     * Tùy chỉnh màu sắc cho bảng hóa đơn dựa trên loại enum hoặc hằng số
     * 
     * @param table Bảng hóa đơn cần áp dụng định dạng
     * @param statusColumnIndex Chỉ số cột chứa thông tin trạng thái
     * @param processingValue Giá trị tương ứng với trạng thái "Đang xử lý"
     * @param cancelledValue Giá trị tương ứng với trạng thái "Đã hủy"
     * @param completedValue Giá trị tương ứng với trạng thái "Đã hoàn thành"
     */
    public static void applyInvoiceStatusStyle(JTable table, final int statusColumnIndex, 
            final Object processingValue, final Object cancelledValue, final Object completedValue) {
        
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    Object statusObj = table.getValueAt(row, statusColumnIndex);
                    
                    if (statusObj != null) {
                        if (statusObj.equals(processingValue)) {
                            c.setBackground(PROCESSING_COLOR);
                        } else if (statusObj.equals(cancelledValue)) {
                            c.setBackground(CANCELLED_COLOR);
                        } else if (statusObj.equals(completedValue)) {
                            c.setBackground(COMPLETED_COLOR);
                        } else {
                            c.setBackground(table.getBackground());
                        }
                    }
                }
                
                // Duy trì căn giữa nội dung
                ((JLabel)c).setHorizontalAlignment(JLabel.CENTER);
                
                return c;
            }
        };
        
        // Áp dụng renderer cho tất cả các cột
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(statusRenderer);
        }
    }
    
    


    /**
     * Interface cho action listener của button xóa
     */
    @FunctionalInterface
    public interface DeleteButtonActionListener {
        /**
         * Được gọi khi button xóa được click
         * @param table Bảng chứa button
         * @param modelRow Chỉ số hàng trong model (đã convert từ view)
         * @param column Chỉ số cột
         * @param firstColumnValue Giá trị cột đầu tiên (thường là ID hoặc mã)
         */
        void onDeleteClicked(JTable table, int modelRow, int column, Object firstColumnValue);
    }
    

    public static void addDeleteButton(JTable table, int columnIndex, DeleteButtonActionListener deleteListener){
        addDeleteButton(table, columnIndex, deleteListener, 0);
    }

    /**
     * Thêm nút xóa dạng Label vào cột chỉ định với hover tự động
     * @param table Bảng cần thêm nút xóa
     * @param columnIndex Chỉ số cột cần thêm nút xóa
     * @param deleteListener Action listener khi nút xóa được click
     */
    public static void addDeleteButton(JTable table, int columnIndex, DeleteButtonActionListener deleteListener, int columnValue) {
        if (columnIndex < 0 || columnIndex >= table.getColumnCount()) {
            return;
        }

        FlatSVGIcon deleteIcon = new FlatSVGIcon("com/pcstore/resources/icon/delete-2.svg", 16, 16);

        final int[] hoverPosition = {-1, -1}; // [row, column]
        
        TableCellRenderer deleteRenderer = new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                JLabel deleteLabel = new JLabel("",JLabel.CENTER);
                deleteLabel.setOpaque(true);
                deleteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                deleteLabel.setIcon(deleteIcon);
                // deleteLabel.setToolTipText("Xóa");
                deleteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                deleteLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                deleteLabel.setForeground(new Color(120, 120, 120)); // Màu xám nhạt
                
                // Kiểm tra nếu đang hover vào ô này
                boolean isHovering = (hoverPosition[0] == row && hoverPosition[1] == column);
                
                if (isHovering) {
                    deleteLabel.setBackground(new Color(255, 51, 51)); 
                    deleteLabel.setForeground(Color.WHITE);
                    // deleteLabel.setText("Xóa"); 
                    deleteLabel.setIcon(deleteIcon);
                    deleteLabel.setFont(new Font("Segoe UI", Font.BOLD,14));
                } else if (isSelected) {
                    deleteLabel.setBackground(table.getSelectionBackground());
                    deleteLabel.setForeground(table.getSelectionForeground());
                } else {
                    deleteLabel.setBackground(table.getBackground());
                    deleteLabel.setForeground(new Color(120, 120, 120)); 
                }
                
                return deleteLabel;
            }
        };
        
        DefaultCellEditor deleteEditor = new DefaultCellEditor(new JCheckBox()) {
            private JLabel deleteLabel;
            
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                
                deleteLabel = new JLabel("", JLabel.CENTER);
                deleteLabel.setOpaque(true);
                deleteLabel.setIcon(deleteIcon);
                deleteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                deleteLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                deleteLabel.setBackground(new Color(255, 51, 51));
                deleteLabel.setForeground(Color.WHITE);
                deleteLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                // Xử lý click ngay lập tức
                SwingUtilities.invokeLater(() -> {
                    int modelRow = table.convertRowIndexToModel(row);
                    Object firstColumnValue = table.getModel().getValueAt(modelRow, columnValue);
                    
                    if (deleteListener != null) {
                        deleteListener.onDeleteClicked(table, modelRow, column, firstColumnValue);
                    }
                    
                    stopCellEditing();
                });
                
                return deleteLabel;
            }
            
            @Override
            public Object getCellEditorValue() {
                return "";
            }
            
            @Override
            public boolean isCellEditable(java.util.EventObject e) {
                return true;
            }
            
            @Override
            public boolean shouldSelectCell(java.util.EventObject anEvent) {
                return false; // Không select cell
            }
            
            @Override
            public boolean stopCellEditing() {
                return super.stopCellEditing();
            }
        };
        
        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                
                if (column == columnIndex && row >= 0) {
                    if (hoverPosition[0] != row || hoverPosition[1] != column) {
                        hoverPosition[0] = row;
                        hoverPosition[1] = column;
                        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        table.repaint();
                    }
                } else {
                    if (hoverPosition[0] != -1 || hoverPosition[1] != -1) {
                        hoverPosition[0] = -1;
                        hoverPosition[1] = -1;
                        table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        table.repaint();
                    }
                }
            }
        });
        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                
                if (column == columnIndex && row >= 0) {
                    if (!table.isEditing()) {
                        int modelRow = table.convertRowIndexToModel(row);
                        Object firstColumnValue = table.getModel().getValueAt(modelRow, columnValue);
                        
                        if (deleteListener != null) {
                            // deleteListener.onDeleteClicked(table, modelRow, column, firstColumnValue);
                        }
                    }
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (hoverPosition[0] != -1 || hoverPosition[1] != -1) {
                    hoverPosition[0] = -1;
                    hoverPosition[1] = -1;
                    table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    table.repaint();
                }
            }
        });
        
        // Áp dụng renderer và editor cho cột
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(deleteRenderer);
        table.getColumnModel().getColumn(columnIndex).setCellEditor(deleteEditor);
        
        table.getColumnModel().getColumn(columnIndex).setPreferredWidth(60);
        table.getColumnModel().getColumn(columnIndex).setMaxWidth(60);
        table.getColumnModel().getColumn(columnIndex).setMinWidth(60);
        
        if (table.getRowSorter() instanceof TableRowSorter) {
            TableRowSorter<?> sorter = (TableRowSorter<?>) table.getRowSorter();
            sorter.setSortable(columnIndex, false);
        }
    }
    
    
}