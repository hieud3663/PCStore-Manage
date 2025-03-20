package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Class biểu diễn thông tin tài khoản người dùng
 */
public class User extends BaseTimeEntity {
    private Integer userId;
    private String username;
    private String password;
    private boolean isActive;
    private LocalDateTime lastLogin;
    private Set<String> roles = new HashSet<>();
    private Employee employee;

    @Override
    public Object getId() {
        return userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }
        if (username.length() < 3) {
            throw new IllegalArgumentException("Tên đăng nhập phải có ít nhất 3 ký tự");
        }
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự");
        }
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void addRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Vai trò không được để trống");
        }
        this.roles.add(role);
    }

    public void removeRole(String role) {
        this.roles.remove(role);
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        // Xử lý mối quan hệ hai chiều
        if (this.employee != employee) {
            Employee oldEmployee = this.employee;
            this.employee = employee;
            
            if (oldEmployee != null) {
                oldEmployee.setUser(null);
            }
            
            if (employee != null) {
                employee.setUser(this);
            }
        }
    }

    // Phương thức kiểm tra tài khoản có một vai trò cụ thể hay không
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    // Phương thức kiểm tra tài khoản có quyền admin không
    public boolean isAdmin() {
        return roles.contains("ADMIN");
    }

    // Phương thức kiểm tra tài khoản có thể đăng nhập không
    public boolean canLogin() {
        return isActive;
    }

    // Phương thức cập nhật thời gian đăng nhập cuối
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
        this.setUpdatedAt(this.lastLogin);
    }

    // Phương thức tạo tài khoản mới
    public static User createNew(String username, String password, Employee employee) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // Trong thực tế cần mã hóa mật khẩu
        user.setEmployee(employee);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    // Phương thức kích hoạt tài khoản
    public void activate() {
        this.isActive = true;
        this.setUpdatedAt(LocalDateTime.now());
    }

    // Phương thức vô hiệu hóa tài khoản
    public void deactivate() {
        this.isActive = false;
        this.setUpdatedAt(LocalDateTime.now());
    }
}