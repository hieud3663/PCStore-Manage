package com.pcstore.utils;

import com.pcstore.model.User;
import com.pcstore.model.enums.Roles;

/**
 * Lớp quản lý phiên đăng nhập của người dùng trong ứng dụng
 */
public class SessionManager {
    // Singleton pattern
    private static SessionManager instance;
    
    // Thông tin người dùng hiện tại đang đăng nhập
    private User currentUser;
    
    private SessionManager() {
        // Private constructor để ngăn việc khởi tạo trực tiếp
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean hasRole(String role) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.getRoleName() != null && currentUser.getRoleName().equalsIgnoreCase(role);
    }
    
    public boolean isAdmin() {
        return hasRole(Roles.ADMIN.getRoleName());
    }
    
    public boolean isManager() {
        return hasRole(Roles.MANAGER.getRoleName());
    }
    
    public boolean isSales() {
        return hasRole(Roles.SALES.getRoleName());
    }

    public boolean isStock() {
        return hasRole(Roles.STOCK.getRoleName());
    }

    public boolean isRepair() {
        return hasRole(Roles.REPAIR.getRoleName());
    }
    
    
    public void logout() {
        currentUser = null;
    }
}