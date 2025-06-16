package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import java.time.LocalDateTime;
import com.pcstore.utils.ErrorMessage;

/**
 * Class biểu diễn thông tin tài khoản người dùng
 */
public class User extends BaseTimeEntity {
    private String userId;
    private String username;
    private String password;
    private Integer roleID;
    private String roleName;
    private Employee employee;
    private boolean isActive;
    private LocalDateTime lastLogin;

    

    @Override
    public Object getId() {
        return userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.USERNAME_EMPTY.toString());
        }
        if (username.length() < 3) {
            throw new IllegalArgumentException(ErrorMessage.USERNAME_TOO_SHORT.toString());
        }
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.PASSWORD_EMPTY.toString());
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException(ErrorMessage.PASSWORD_TOO_SHORT.toString());
        }
        this.password = password;
    }


    public Integer getRoleID() {
        return roleID;
    }

    public void setRoleID(Integer roleID) {
        this.roleID = roleID;
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

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }


    public String getFullName() {
        return employee != null ? employee.getFullName() : null;
    }


    public boolean canLogin() {
        return isActive;
    }

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


    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
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