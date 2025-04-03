package com.pcstore.service;

import com.pcstore.model.User;
import com.pcstore.repository.impl.UserRepository;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý logic nghiệp vụ liên quan đến người dùng
 */
public class UserService {
    private final UserRepository userRepository;
    
    /**
     * Khởi tạo service với repository
     * @param connection Kết nối đến database
     */
    public UserService(Connection connection) {
        this.userRepository = new UserRepository(connection);
    }
    
    /**
     * Khởi tạo service với repository đã có
     * @param userRepository Repository người dùng
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Xác thực người dùng
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return User nếu xác thực thành công, null nếu thất bại
     */
    public User authenticate(String username, String password) {
        User user = userRepository.authenticate(username, password);
        if (user != null) {
            // Cập nhật thời gian đăng nhập cuối cùng
            user.updateLastLogin();
            userRepository.update(user);
            
        }
        
        return user;
    }
    
    /**
     * Thêm người dùng mới
     * @param user Thông tin người dùng
     * @return Người dùng đã được thêm
     */
    public User addUser(User user) {
        // Kiểm tra tồn tại
        if (userRepository.exists(user.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập " + user.getUsername() + " đã tồn tại");
        }
        
        // Kiểm tra thông tin cơ bản
        validateUserBasicInfo(user);
        
        return userRepository.add(user);
    }
    
    /**
     * Cập nhật thông tin người dùng
     * @param user Thông tin người dùng mới
     * @return Người dùng đã được cập nhật
     */
    public User updateUser(User user) {
        // Kiểm tra tồn tại
        if (!userRepository.exists(user.getUsername())) {
            throw new IllegalArgumentException("Người dùng với tên đăng nhập " + user.getUsername() + " không tồn tại");
        }
        
        // Kiểm tra thông tin cơ bản
        validateUserBasicInfo(user);
        
        return userRepository.update(user);
    }
    
    /**
     * Cập nhật mật khẩu
     * @param username Tên đăng nhập
     * @param newPassword Mật khẩu mới
     * @return true nếu cập nhật thành công
     */
    public boolean updatePassword(String username, String newPassword) {
        // Kiểm tra tồn tại
        if (!userRepository.exists(username)) {
            throw new IllegalArgumentException("Người dùng với tên đăng nhập " + username + " không tồn tại");
        }
        
        // Kiểm tra mật khẩu mới
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự");
        }
        
        return userRepository.updatePassword(username, newPassword);
    }
    
    /**
     * Xóa người dùng
     * @param username Tên đăng nhập
     * @return true nếu xóa thành công
     */
    public boolean deleteUser(String username) {
        return userRepository.delete(username);
    }
    
    /**
     * Tìm người dùng theo tên đăng nhập
     * @param username Tên đăng nhập
     * @return Optional người dùng
     */
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findById(username);
    }
    
    /**
     * Tìm người dùng theo mã nhân viên
     * @param employeeId Mã nhân viên
     * @return Optional người dùng
     */
    public Optional<User> findUserByEmployeeId(String employeeId) {
        return userRepository.findByEmployeeId(employeeId);
    }
    
    /**
     * Lấy danh sách tất cả người dùng
     * @return Danh sách người dùng
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Lấy danh sách người dùng theo vai trò
     * @param roleId Mã vai trò
     * @return Danh sách người dùng
     */
    public List<User> getUsersByRole(int roleId) {
        return userRepository.findByRole(roleId);
    }
    
    /**
     * Đếm số lượng người dùng theo vai trò
     * @param roleId Mã vai trò
     * @return Số lượng người dùng
     */
    public int countUsersByRole(int roleId) {
        return userRepository.countByRole(roleId);
    }
    
    /**
     * Kích hoạt tài khoản người dùng
     * @param username Tên đăng nhập
     * @return true nếu kích hoạt thành công
     */
    public boolean activateUser(String username) {
        Optional<User> optionalUser = userRepository.findById(username);
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("Người dùng với tên đăng nhập " + username + " không tồn tại");
        }
        
        User user = optionalUser.get();
        user.activate();
        userRepository.update(user);
        
        return true;
    }
    
    /**
     * Vô hiệu hóa tài khoản người dùng
     * @param username Tên đăng nhập
     * @return true nếu vô hiệu hóa thành công
     */
    public boolean deactivateUser(String username) {
        Optional<User> optionalUser = userRepository.findById(username);
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("Người dùng với tên đăng nhập " + username + " không tồn tại");
        }
        
        User user = optionalUser.get();
        user.deactivate();
        userRepository.update(user);
        
        return true;
    }
    
    /**
     * Kiểm tra thông tin cơ bản của người dùng
     * @param user Người dùng cần kiểm tra
     */
    private void validateUserBasicInfo(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
    }
}