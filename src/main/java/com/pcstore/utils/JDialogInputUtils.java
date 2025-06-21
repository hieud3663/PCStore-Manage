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
            JOptionPane.showMessageDialog(parent, ErrorMessage.FIELD_EMPTY.toString().formatted("Giá trị"), ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return showInputDialogInt(parent, message, defaultValue); // Gọi lại hàm để yêu cầu nhập lại
        }
        
        try {
            int value = Integer.parseInt(input);
            if (value <= 0) {
                throw new NumberFormatException(ErrorMessage.VALUE_MUST_BE_POSITIVE.toString());
            }
            return value;
        } catch (NumberFormatException e) {            
            JOptionPane.showMessageDialog(parent, ErrorMessage.INVALID_VALUE.toString(), 
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return showInputDialogInt(parent, message, defaultValue); // Gọi lại hàm để yêu cầu nhập lại
        }
    }
    
    public static BigDecimal showInputDialogBigDecimal(Component parent, String message, String defaultValue ){
        // Hiển thị dialog nhập liệu
        Object result = JOptionPane.showInputDialog(parent, message, "Input value", 
                JOptionPane.QUESTION_MESSAGE, null, null, defaultValue);
        
        // Kiểm tra nếu người dùng nhấn Cancel hoặc đóng dialog
        if (result == null) {
            return null; // Trả về null khi người dùng hủy thao tác
        }
        
        String input = result.toString();
        if (input.trim().isEmpty()) {            
            JOptionPane.showMessageDialog(parent, ErrorMessage.FIELD_EMPTY.toString().formatted("Giá trị"), ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return showInputDialogBigDecimal(parent, message, defaultValue); // Gọi lại hàm để yêu cầu nhập lại
        }
          try {
            BigDecimal value = new BigDecimal(input);
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException(ErrorMessage.VALUE_MUST_BE_POSITIVE.toString());
            }
            return value;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, ErrorMessage.INVALID_VALUE.toString(), 
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return showInputDialogBigDecimal(parent, message, defaultValue); // Gọi lại hàm để yêu cầu nhập lại
        }
    }

    public static int getPositiveIntegerInput(Component parent, String message, String defaultValue) {
        String input = JOptionPane.showInputDialog(parent, message, defaultValue, JOptionPane.QUESTION_MESSAGE);
        
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, ErrorMessage.FIELD_EMPTY.toString().formatted("Giá trị"), ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            throw new NumberFormatException(ErrorMessage.FIELD_EMPTY.toString().formatted("Giá trị"));
        }
          try {
            int value = Integer.parseInt(input.trim());
            if (value <= 0) {
                JOptionPane.showMessageDialog(parent, ErrorMessage.VALUE_MUST_BE_POSITIVE.toString(),
                        ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
                throw new NumberFormatException(ErrorMessage.VALUE_MUST_BE_POSITIVE.toString());
            }
            return value;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, ErrorMessage.INVALID_VALUE.toString(),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }
    
    public static double getPositiveDoubleInput(Component parent, String message, String defaultValue) {
        String input = JOptionPane.showInputDialog(parent, message, defaultValue, JOptionPane.QUESTION_MESSAGE);
        
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, ErrorMessage.FIELD_EMPTY.toString().formatted("Giá trị"), ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            throw new NumberFormatException(ErrorMessage.FIELD_EMPTY.toString().formatted("Giá trị"));
        }
          try {
            double value = Double.parseDouble(input.trim());
            if (value <= 0) {
                JOptionPane.showMessageDialog(parent, ErrorMessage.VALUE_MUST_BE_POSITIVE.toString(),
                        ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
                throw new NumberFormatException(ErrorMessage.VALUE_MUST_BE_POSITIVE.toString());
            }
            return value;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, ErrorMessage.INVALID_VALUE.toString(),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }
}