package com.pcstore.controller;

import java.sql.Connection;

import javax.swing.JOptionPane;

import com.pcstore.dao.DatabaseConnection;
import com.pcstore.dao.impl.UserDAO;
import com.pcstore.model.User;

public class LoginController {
     private UserDAO userDAO;
    private Connection connection;
    
    public LoginController() {
        try {
            DatabaseConnection db = new DatabaseConnection();
            this.connection = db.getConnection();
            this.userDAO = new UserDAO(this.connection);
        } catch (Exception e) {
            // e.printStackTrace();
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
            return userDAO.authenticate(username, password);
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
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi đóng kết nối", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
