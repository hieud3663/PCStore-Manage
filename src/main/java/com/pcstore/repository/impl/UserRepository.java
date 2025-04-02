package com.pcstore.Repository.impl;

import com.pcstore.Repository.Repository;
import com.pcstore.Repository.RepositoryFactory;
import com.pcstore.model.Employee;
import com.pcstore.model.User;
import com.pcstore.utils.PCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository implementation cho User entity
 */
public class UserRepository implements Repository<User, String> {
    private Connection connection;
    
    public UserRepository(Connection connection) {
        this.connection = connection;
    }
    
    // public UserRepository(Connection connection2, RepositoryFactory RepositoryFactory) {
    //     //TODO Auto-generated constructor stub
    // }

    @Override
    public User add(User user) {
        String sql = "INSERT INTO Users (Username, Password, RoleID, EmployeeID, Status) " +
                     "VALUES (?, ?, ?, ?, ?)";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setInt(3, user.getRoleID());
            statement.setString(4, user.getEmployeeId());
            statement.setBoolean(5, user.getStatus());
            
            statement.executeUpdate();
            
            LocalDateTime now = LocalDateTime.now();
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
            
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding user", e);
        }
    }
    
    @Override
    public User update(User user) {
        String sql = "UPDATE Users SET Password = ?, RoleID = ?, Status = ? " +
                    "WHERE Username = ?";
                    
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getPassword());
            statement.setInt(2, user.getRoleID());
            statement.setBoolean(3, user.getStatus());
            statement.setString(4, user.getUsername());
            
            statement.executeUpdate();
            
            user.setUpdatedAt(LocalDateTime.now());
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }
    
    @Override
    public boolean delete(String username) {
        String sql = "DELETE FROM Users WHERE Username = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }
    
    @Override
    public Optional<User> findById(String username) {
        String sql = "SELECT u.*, r.RoleName, e.FullName as EmployeeName " +
                     "FROM Users u " +
                     "LEFT JOIN Roles r ON u.RoleID = r.RoleID " +
                     "LEFT JOIN Employees e ON u.EmployeeID = e.EmployeeID " +
                     "WHERE u.Username = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToUser(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by username", e);
        }
    }
    
    @Override
    public List<User> findAll() {
        String sql = "SELECT u.*, r.RoleName, e.FullName as EmployeeName " +
                     "FROM Users u " +
                     "LEFT JOIN Roles r ON u.RoleID = r.RoleID " +
                     "LEFT JOIN Employees e ON u.EmployeeID = e.EmployeeID";
        List<User> users = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
             
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all users", e);
        }
    }
    
    @Override
    public boolean exists(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE Username = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if user exists", e);
        }
    }
    
    // Xác thực người dùng đăng nhập
    public User authenticate(String username, String password) {
        String sql = "SELECT u.*, r.RoleName, e.FullName as EmployeeName " +
                     "FROM Users u " +
                     "join UserRoles ur on u.UserID = ur.UserID "+
                     "LEFT JOIN Roles r ON ur.RoleID = r.RoleID " +
                     "LEFT JOIN Employees e ON u.EmployeeID = e.EmployeeID " +
                     "WHERE u.Username =  ? AND u.IsActive = 1";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                // System.err.println(resultSet.toString());
                
                String hashedPassword = resultSet.getString("PasswordHash");
                if (!PCrypt.checkPassword(password, hashedPassword)) {
                    return null;
                }
                String EmployeeId = resultSet.getString("EmployeeID");
                if(EmployeeId == null){
                    EmployeeId = "NV000";
                }

                String fullName = resultSet.getString("EmployeeName");
                if (fullName == null) {
                    fullName = "Admin";
                }

                Employee employee = new Employee();
                employee.setEmployeeId(EmployeeId);
                employee.setFullName(fullName);

                User user = mapResultSetToUser(resultSet);
                user.setEmployee(employee);
                
                return user;
                
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Xác thực người dùng thất bại: ( "+e.getMessage()+" )", e);
        }
    }
    
    // Cập nhật mật khẩu
    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE Users SET Password = ? WHERE Username = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newPassword);
            statement.setString(2, username);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating password", e);
        }
    }
    
    // Tìm người dùng theo vai trò
    public List<User> findByRole(int roleId) {
        String sql = "SELECT u.*, r.RoleName, e.FullName as EmployeeName " +
                     "FROM Users u " +
                     "LEFT JOIN Roles r ON u.RoleID = r.RoleID " +
                     "LEFT JOIN Employees e ON u.EmployeeID = e.EmployeeID " +
                     "WHERE u.RoleID = ?";
        
        List<User> users = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roleId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding users by role", e);
        }
    }
    
    // Tìm người dùng theo nhân viên
    public Optional<User> findByEmployeeId(String employeeId) {
        String sql = "SELECT u.*, r.RoleName, e.FullName as EmployeeName " +
                     "FROM Users u " +
                     "LEFT JOIN Roles r ON u.RoleID = r.RoleID " +
                     "LEFT JOIN Employees e ON u.EmployeeID = e.EmployeeID " +
                     "WHERE u.EmployeeID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employeeId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToUser(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by employee ID", e);
        }
    }
    
    // Đếm số lượng người dùng theo vai trò
    public int countByRole(int roleId) {
        String sql = "SELECT COUNT(*) FROM Users WHERE RoleID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roleId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting users by role", e);
        }
    }
    
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUsername(resultSet.getString("Username"));
        user.setPassword(resultSet.getString("PasswordHash"));
        // user.setRoleID(resultSet.getInt("RoleID"));
        user.setRoleName(resultSet.getString("RoleName"));

        String EmployeeId = resultSet.getString("EmployeeID");

        // user.setStatus(resultSet.getBoolean("Status"));

        String sql = "SELECT * FROM Employees WHERE EmployeeID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, EmployeeId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String employeeId = rs.getString("EmployeeID");
                String fullName = rs.getString("FullName");
                String position = rs.getString("Position");
                
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding employee by ID", e);
        }
        
       
        
        return user;
    }
}