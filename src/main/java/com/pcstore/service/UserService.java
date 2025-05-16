package com.pcstore.service;

import com.pcstore.model.User;
import com.pcstore.repository.impl.UserRepository;
import com.pcstore.utils.ErrorMessage;

import java.sql.Connection;
import java.sql.SQLException;
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
            user.updateLastLogin();
            userRepository.update(user);
        }
        return user;

    }


    public User saveUser(User user) {
        if (userRepository.exists(user.getUsername())) {
            throw new IllegalArgumentException(String.format(ErrorMessage.USERNAME_EXISTS_EN, user.getUsername()));
        }
        
        validateUserBasicInfo(user);
        
        String newUserId = userRepository.generateUserId();
        user.setUserId(newUserId);
        
        return userRepository.save(user);
    }
    
    /**
     * Thêm người dùng mới
     * @param user Thông tin người dùng
     * @return Người dùng đã được thêm
     */
    public User addUser(User user) {
        // Kiểm tra tồn tại
        if (userRepository.exists(user.getUsername())) {
            throw new IllegalArgumentException(String.format(ErrorMessage.USERNAME_EXISTS, user.getUsername()));
        }
        
        validateUserBasicInfo(user);
        
        return userRepository.add(user);
    }
    
    /**
     * Cập nhật thông tin người dùng
     * @param user Thông tin người dùng mới
     * @return Người dùng đã được cập nhật
     */
    public User updateUser(User user) {
        if (!userRepository.exists(user.getUsername())) {
            throw new IllegalArgumentException(String.format(ErrorMessage.USER_NOT_EXISTS, user.getUsername()));
        }
        
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
        if (!userRepository.exists(username)) {
            throw new IllegalArgumentException(String.format(ErrorMessage.USER_NOT_EXISTS, username));
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.PASSWORD_EMPTY);
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException(ErrorMessage.PASSWORD_TOO_SHORT);
        }
        
        return userRepository.updatePassword(username, newPassword);
    }
    
    /**
     * Xóa người dùng
     * @param userId ID của người dùng cần xóa
     * @return true nếu xóa thành công
     */
    public boolean deleteUser(String userId) {
        // Đoạn code này giả định rằng userId là username
        return userRepository.delete(userId);
    }
    
    /**
     * Tìm người dùng theo tên đăng nhập
     * @param username Tên đăng nhập
     * @return Optional người dùng
     */
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByIdOrUsername(username);
    }
    
    /**
     * Tìm người dùng theo ID
     * @param userId ID của người dùng
     * @return Optional người dùng
     * @throws SQLException Nếu có lỗi khi truy vấn database
     */
    public Optional<User> getUserById(String userId) throws SQLException {
        return userRepository.findByIdOrUsername(userId);
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
     * @throws SQLException Nếu có lỗi khi truy vấn database
     */
    public List<User> getAllUsers() throws SQLException {
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
            throw new IllegalArgumentException(String.format(ErrorMessage.USER_NOT_EXISTS, username));
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
            throw new IllegalArgumentException(String.format(ErrorMessage.USER_NOT_EXISTS, username));
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
            throw new IllegalArgumentException(ErrorMessage.USERNAME_EMPTY);
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.PASSWORD_EMPTY);
        }
    }

    /**
     * Kiểm tra xem người dùng có tồn tại hay không
     * @param username Tên đăng nhập cần kiểm tra
     * @return true nếu username đã tồn tại, false nếu chưa tồn tại
     */
    public boolean isUsernameExists(String username) {
        return userRepository.exists(username);
    }

    //generate id
    public String generateUserId() {
        return userRepository.generateUserId();
    }
    
    //Tìm email
    public Optional<User> findUserByEmail(String email) {
        
        return userRepository.findByEmail(email);
    }
}