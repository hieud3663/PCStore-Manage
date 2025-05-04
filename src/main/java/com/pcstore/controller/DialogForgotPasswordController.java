package com.pcstore.controller;

import com.pcstore.model.User;
import com.pcstore.service.UserService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.EmailUtils;
import com.pcstore.utils.PCrypt;
import com.pcstore.view.DialogForgotPasswordForm;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller để xử lý chức năng quên mật khẩu
 */
public class DialogForgotPasswordController {
    private DialogForgotPasswordForm dialog;
    private UserService userService;
    private String generatedOTP;
    private User currentUser;
    
    private Timer otpTimer;
    private int countdownSeconds = 60; 

    /**
     * Constructor khởi tạo controller với form dialog
     * @param dialog DialogForgotPasswordForm để xử lý sự kiện
     */
    public DialogForgotPasswordController(DialogForgotPasswordForm dialog) {
        this.dialog = dialog;
        try {
            this.userService = ServiceFactory.getUserService();
            setupEventListeners();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(dialog, "Lỗi kết nối đến cơ sở dữ liệu: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Thiết lập các sự kiện cho form
     */
    private void setupEventListeners() {
            dialog.getLbSendOTP().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (dialog.getLbSendOTP().isEnabled()) {
                    handleSendOTP();
                }
            }
        });
        
        dialog.getBtnConfirm().addActionListener(e -> handleConfirmButton());
        
        dialog.getBtnClose().addActionListener(e -> {
            if (otpTimer != null) {
                otpTimer.cancel();
            }
            dialog.dispose();
        });
        
        dialog.getCheckShowPassword().addActionListener(e -> togglePasswordVisibility());
    }
    
    /**
     * Xử lý sự kiện gửi mã OTP
     */
    private void handleSendOTP() {
        String email = dialog.getTxtEmail().getText().trim();
        
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Vui lòng nhập địa chỉ email!", 
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(dialog, "Địa chỉ email không hợp lệ!", 
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Optional<User> userOptional = userService.findUserByEmail(email);
         
            currentUser = userOptional.orElse(null);
            
            if (currentUser == null) {
                JOptionPane.showMessageDialog(dialog, "Email không tồn tại trong hệ thống!", 
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            generatedOTP = generateOTP();
            
            boolean sent = sendOTPEmail(email, generatedOTP);
            
            if (sent) {
                startCountdown();
                
                dialog.getTxtOTP().setEnabled(true);
                dialog.getBtnConfirm().setEnabled(true);
                
                dialog.getTxtEmail().setEnabled(false);
                
                JOptionPane.showMessageDialog(dialog, "Mã OTP đã được gửi đến email của bạn!", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dialog, "Không thể gửi mã OTP. Vui lòng thử lại sau!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi hệ thống: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * Bắt đầu đếm ngược sau khi gửi OTP
     */
    private void startCountdown() {
        if (otpTimer != null) {
            otpTimer.cancel();
        }
        
        dialog.getLbSendOTP().setEnabled(false);
        
        countdownSeconds = 60;
        
        otpTimer = new Timer();
        otpTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                countdownSeconds--;
                
                SwingUtilities.invokeLater(() -> {
                    if (countdownSeconds > 0) {
                        dialog.getLbSendOTP().setText("Gửi lại (" + countdownSeconds + ")");
                    } else {
                        dialog.getLbSendOTP().setText("Gửi OTP");
                        dialog.getLbSendOTP().setEnabled(true);
                        dialog.getTxtEmail().setEnabled(true);
                        otpTimer.cancel();
                    }
                });
            }
        }, 0, 1000); 
    }
    
    /**
     * Xử lý sự kiện nhấn nút Confirm
     */
    private void handleConfirmButton() {
        String otp = dialog.getTxtOTP().getText().trim();
        String newPassword = new String(dialog.getTxtNewPassword().getPassword());
        String confirmPassword = new String(dialog.getTxtConfirmPassword().getPassword());
        
        if (otp.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!", 
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (generatedOTP == null || !generatedOTP.equals(otp)) {
            JOptionPane.showMessageDialog(dialog, "Mã OTP không chính xác!", 
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(dialog, "Mật khẩu xác nhận không khớp với mật khẩu mới!", 
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!isStrongPassword(newPassword)) {
            JOptionPane.showMessageDialog(dialog, 
                    "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt!", 
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String hashedPassword = PCrypt.hashPassword(newPassword);
            
            boolean updated = userService.updatePassword(currentUser.getUsername(), hashedPassword);
            
            if (updated) {
                if (otpTimer != null) {
                    otpTimer.cancel();
                }
                
                JOptionPane.showMessageDialog(dialog, "Đặt lại mật khẩu thành công!", 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Không thể cập nhật mật khẩu. Vui lòng thử lại sau!", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi hệ thống: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * Kiểm tra độ mạnh của mật khẩu
     * @param password Mật khẩu cần kiểm tra
     * @return true nếu mật khẩu đủ mạnh
     */
    private boolean isStrongPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecialChar = true;
            }
        }
        
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }
    
    /**
     * Kiểm tra định dạng email
     * @param email Email cần kiểm tra
     * @return true nếu email hợp lệ
     */
    private boolean isValidEmail(String email) {
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(regex);
    }
    
    /**
     * Tạo mã OTP ngẫu nhiên
     * @return Mã OTP dạng chuỗi
     */
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); 
        return String.valueOf(otp);
    }
    
    /**
     * Gửi email chứa mã OTP
     * @param email Địa chỉ email nhận
     * @param otp Mã OTP
     * @return true nếu gửi thành công
     */
    private boolean sendOTPEmail(String email, String otp) {
        try {
            String subject = "Mã xác thực đặt lại mật khẩu";
            String message = "Mã xác thực của bạn là: " + otp + "\n\n"
                    + "Vui lòng không chia sẻ mã này với người khác.\n"
                    + "Mã có hiệu lực trong vòng 60 giây.";
            
            EmailUtils.sendEmail(email, subject, message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Bật/tắt hiển thị mật khẩu
     */
    private void togglePasswordVisibility() {
        boolean show = dialog.getCheckShowPassword().isSelected();
        dialog.getTxtNewPassword().setEchoChar(show ? (char) 0 : '•');
        dialog.getTxtConfirmPassword().setEchoChar(show ? (char) 0 : '•');
    }
}