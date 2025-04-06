package com.pcstore.utils;

import java.awt.Component;

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

}
