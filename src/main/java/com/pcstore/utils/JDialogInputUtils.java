package com.pcstore.utils;

import java.awt.Component;

import javax.swing.JOptionPane;

public class JDialogInputUtils {
    

    //Kiểm tra chuỗi có phải là số nguyên dương hay không
    public static Integer showInputDialogInt(Component parent, String message, String defaultValue) {
        String input = JOptionPane.showInputDialog(parent, message, "Input value", JOptionPane.QUESTION_MESSAGE, null, null, defaultValue).toString();
        if (input == null) {
            return null; // Người dùng đã nhấn "Hủy"
        }
        try {
            int value = Integer.parseInt(input);
            if (value < 0) {
                throw new NumberFormatException("Giá trị phải là số nguyên dương");
            }
            return value;
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Giá trị không hợp lệ. Vui lòng nhập lại.", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
            return showInputDialogInt(parent, message, defaultValue); // Gọi lại hàm để yêu cầu nhập lại
        }

    }
}
