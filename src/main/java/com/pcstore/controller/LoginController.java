package com.pcstore.controller;

import com.pcstore.model.User;
import com.pcstore.service.ServiceFactory;
import com.pcstore.service.UserService;
import com.pcstore.utils.SessionManager;
import com.pcstore.view.LoginForm;

import java.sql.SQLException;

import javax.swing.JOptionPane;

/**
 * Controller xử lý logic đăng nhập hệ thống
 */
public class LoginController {
    private UserService userService;
    private LoginForm loginView;
    /**
     * Khởi tạo controller và các service cần thiết
     */
    public LoginController() {
        try {
            this.userService = ServiceFactory.getUserService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi kết nối đến cơ sở dữ liệu", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Lấy thông tin người dùng hiện tại
     * @return User hiện tại
     * @throws SQLException 
     */

     public LoginController(LoginForm loginView) throws SQLException {
        this.userService = ServiceFactory.getUserService();
        this.loginView = loginView; 
    }
    
    /**
     * Xác thực người dùng
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return User nếu xác thực thành công, null nếu thất bại
     */
    public User authenticate(String username, String password) {
        try {
            User user = userService.authenticate(username, password);
            
            if (user != null) {
                // Lưu thông tin người dùng hiện tại vào SessionManager
                SessionManager.getInstance().setCurrentUser(user);
            }
            
            return user;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi xác thực người dùng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
    
    /**
     * Kiểm tra thông tin đăng nhập hợp lệ
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return true nếu hợp lệ, false nếu không
     */
    public boolean validateLoginInfo(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tên đăng nhập không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (password == null || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Mật khẩu không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
}
