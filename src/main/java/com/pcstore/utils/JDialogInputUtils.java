package com.pcstore.utils;

import java.awt.Component;
import java.math.BigDecimal;

import javax.swing.JOptionPane;

public class JDialogInputUtils {
    
    //Kiểm tra chuỗi có phải là số nguyên dương hay không
    public static Integer showInputDialogInt(Component parent, String message, String defaultValue){
        // Hiển thị dialog nhập liệu
        Object result = JOptionPane.showInputDialog(parent, message, "Input value", 
                JOptionPane.QUESTION_MESSAGE, null, null, defaultValue);
        
        // Kiểm tra nếu người dùng nhấn Cancel hoặc đóng dialog
        if (result == null) {
            return null; // Trả về null khi người dùng hủy thao tác
        }
        
        String input = result.toString();
        if (input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return showInputDialogInt(parent, message, defaultValue); // Gọi lại hàm để yêu cầu nhập lại
        }
        
        try {
            int value = Integer.parseInt(input);
            if (value <= 0) {
                throw new NumberFormatException("Giá trị phải là lớn hơn 0");
            }
            return value;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, "Giá trị không hợp lệ. Vui lòng nhập lại.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return showInputDialogInt(parent, message, defaultValue); // Gọi lại hàm để yêu cầu nhập lại
        }
    }
    
    public static BigDecimal showInputDialogBigDecimal(Component parent, String message, String title ){
        // Hiển thị dialog nhập liệu
        Object result = JOptionPane.showInputDialog(parent, message, title, 
                JOptionPane.QUESTION_MESSAGE);
        
        // Kiểm tra nếu người dùng nhấn Cancel hoặc đóng dialog
        if (result == null) {
            return null; // Trả về null khi người dùng hủy thao tác
        }
        
        String input = result.toString();
        if (input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return showInputDialogBigDecimal(parent, message, title); // Gọi lại hàm để yêu cầu nhập lại
        }
        
        try {
            BigDecimal value = new BigDecimal(input);
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("Giá trị phải là lớn hơn 0");
            }
            return value;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, "Giá trị không hợp lệ. Vui lòng nhập lại.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return showInputDialogBigDecimal(parent, message, title); // Gọi lại hàm để yêu cầu nhập lại
        }
    }
}