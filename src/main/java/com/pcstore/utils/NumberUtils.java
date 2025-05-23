package com.pcstore.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class NumberUtils {

    /**
     * Tạo Document Filter cho các trường tiền tệ, tự động định dạng khi nhập liệu
     */
    public static DocumentFilter createCurrencyFilter(JTextField field) {
        return new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                // Chỉ cho phép nhập số
                if (!text.matches("\\d*")) {
                    return;
                }
                
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String beforeOffset = currentText.substring(0, offset);
                String afterOffset = currentText.substring(offset + length);
                String newText = beforeOffset + text + afterOffset;
                
                // Loại bỏ tất cả dấu chấm và định dạng trước khi làm việc với giá trị
                newText = newText.replaceAll("\\.", "").replaceAll("\\,", "");
                
                // Nếu là chuỗi rỗng, chỉ đơn giản thay thế
                if (newText.isEmpty()) {
                    super.replace(fb, 0, fb.getDocument().getLength(), "", attrs);
                    return;
                }
                
                try {
                    // Parse và định dạng số
                    BigDecimal number = new BigDecimal(newText);
                    NumberFormat formatter = LocaleManager.getInstance().getNumberFormatter();
                    String formatted = formatter.format(number);
                    
                    // Thay thế toàn bộ nội dung
                    super.replace(fb, 0, fb.getDocument().getLength(), formatted, attrs);
                    
                    // Đặt vị trí con trỏ sau khi định dạng
                    SwingUtilities.invokeLater(() -> {
                        field.setCaretPosition(formatted.length());
                    });
                    
                } catch (NumberFormatException e) {
                    // Nếu không phải số hợp lệ, giữ nguyên
                    super.replace(fb, offset, length, text, attrs);
                }
            }
            
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
                replace(fb, offset, 0, text, attr);
            }
        };
    }

    /**
     * Áp dụng bộ lọc định dạng tiền tệ cho trường TextField
     * @param field TextField cần áp dụng định dạng
     */
    public static void applyCurrencyFilter(JTextField field) {
        DocumentFilter filter = createCurrencyFilter(field);
        AbstractDocument document = (AbstractDocument) field.getDocument();
        document.setDocumentFilter(filter);
    }

    /**
     * Đặt giá trị đã được định dạng vào trường, tạm thời bỏ qua filter
     * @param field Trường cần đặt giá trị
     * @param value Giá trị số
     * @param formatter Trình định dạng số
     */
    public static void setFormattedValue(JTextField field, BigDecimal value, NumberFormat formatter) {
        if (field == null) return;
        
        AbstractDocument doc = (AbstractDocument) field.getDocument();
        DocumentFilter oldFilter = doc.getDocumentFilter();
        
        try {
            // Tạm thời xóa filter
            doc.setDocumentFilter(null);

            // Đặt giá trị đã định dạng
            field.setText(value != null ? formatter.format(value) : "");
        }catch (Exception e) {
            // Xử lý ngoại lệ nếu cần
            e.printStackTrace(); 
        }finally {
            // Khôi phục filter
            doc.setDocumentFilter(oldFilter);
        }
    }
}
