package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import java.time.LocalDateTime;

/**
 * Class biểu diễn thông tin tài khoản người dùng
 */
public class User extends BaseTimeEntity {
    private Integer userId;
    private String username;
    private String password;
    private boolean isActive;
    private LocalDateTime lastLogin;
    // private Set<String> roles = new HashSet<>();
    private Integer roleID;
    private String roleName;
    private Employee employee;
    private boolean status;

    

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

    public String getFullName() {
        return employee != null ? employee.getFullName() : null;
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

    public Integer getRoleID() {
        return roleID;
    }

    public void setRoleID(Integer roleID) {
        this.roleID = roleID;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getEmployeeId() {
        return employee.getEmployeeId();
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }


}