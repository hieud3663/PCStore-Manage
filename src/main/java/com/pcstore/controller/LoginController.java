package com.pcstore.controller;

import com.pcstore.model.User;
import com.pcstore.service.ServiceFactory;
import com.pcstore.service.UserService;

import javax.swing.JOptionPane;

public class LoginController {
    private UserService userService;
    
    public LoginController() {
        try {
            this.userService = ServiceFactory.getUserService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi kết nối đến cơ sở dữ liệu", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Xác thực người dùng
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return User nếu xác thực thành công, null nếu thất bại
     */
    public User authenticate(String username, String password) {
        try {
            return userService.authenticate(username, password);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi xác thực người dùng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Đóng kết nối
     */
    public void close() {
        try {
            ServiceFactory.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi đóng kết nối", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
