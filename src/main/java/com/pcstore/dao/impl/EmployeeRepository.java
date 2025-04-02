package com.pcstore.Repository.impl;

import com.pcstore.Repository.Repository;
import com.pcstore.model.Employee;
import com.pcstore.model.enums.EmployeePositionEnum;
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
 * Repository implementation cho Employee entity
 */
public class EmployeeRepository implements Repository<Employee, String> {
    private Connection connection;
    
    public EmployeeRepository(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public Employee add(Employee employee) {
        String sql = "INSERT INTO Employees (EmployeeID, FullName, PhoneNumber, Email, Position) " +
                     "VALUES (?, ?, ?, ?, ?)";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employee.getEmployeeId());
            statement.setString(2, employee.getFullName());
            statement.setString(3, employee.getPhoneNumber());
            statement.setString(4, employee.getEmail());
            statement.setString(5, employee.getPosition().getDisplayName());
            
            statement.executeUpdate();
            
            LocalDateTime now = LocalDateTime.now();
            employee.setCreatedAt(now);
            employee.setUpdatedAt(now);
            
            return employee;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding employee", e);
        }
    }
    
    @Override
    public Employee update(Employee employee) {
        String sql = "UPDATE Employees SET FullName = ?, PhoneNumber = ?, Email = ?, Position = ? " +
                     "WHERE EmployeeID = ?";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employee.getFullName());
            statement.setString(2, employee.getPhoneNumber());
            statement.setString(3, employee.getEmail());
            statement.setString(4, employee.getPosition().getDisplayName());
            statement.setString(5, employee.getEmployeeId());
            
            statement.executeUpdate();
            
            employee.setUpdatedAt(LocalDateTime.now());
            return employee;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating employee", e);
        }
    }
    
    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM Employees WHERE EmployeeID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting employee", e);
        }
    }
    
    @Override
    public Optional<Employee> findById(String id) {
        String sql = "SELECT * FROM Employees WHERE EmployeeID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToEmployee(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding employee by ID", e);
        }
    }
    
    @Override
    public List<Employee> findAll() {
        String sql = "SELECT * FROM Employees";
        List<Employee> employees = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                employees.add(mapResultSetToEmployee(resultSet));
            }
            return employees;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all employees", e);
        }
    }
    
    @Override
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM Employees WHERE EmployeeID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if employee exists", e);
        }
    }
    
    // Tìm nhân viên theo vị trí
    public List<Employee> findByPosition(EmployeePositionEnum position) {
        String sql = "SELECT * FROM Employees WHERE Position = ?";
        List<Employee> employees = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, position.getDisplayName());
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                employees.add(mapResultSetToEmployee(resultSet));
            }
            return employees;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding employees by position", e);
        }
    }
    
    // Tìm nhân viên theo email
    public Optional<Employee> findByEmail(String email) {
        String sql = "SELECT * FROM Employees WHERE Email = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToEmployee(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding employee by email", e);
        }
    }
    
    // Tìm nhân viên theo số điện thoại
    public Optional<Employee> findByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM Employees WHERE PhoneNumber = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, phoneNumber);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToEmployee(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding employee by phone number", e);
        }
    }
    
    // Tạo mã nhân viên tự động
    public String generateEmployeeId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(EmployeeID, 3, LEN(EmployeeID)) AS INT)) FROM Employees WHERE EmployeeID LIKE 'NV%'";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            int maxId = 0;
            if (resultSet.next() && resultSet.getObject(1) != null) {
                maxId = resultSet.getInt(1);
            }
            
            // Tạo ID mới dạng NV01, NV02, ...
            return String.format("NV%02d", maxId + 1);
        } catch (SQLException e) {
            throw new RuntimeException("Error generating employee ID", e);
        }
    }
    
    private Employee mapResultSetToEmployee(ResultSet resultSet) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(resultSet.getString("EmployeeID"));
        employee.setFullName(resultSet.getString("FullName"));
        employee.setPhoneNumber(resultSet.getString("PhoneNumber"));
        employee.setEmail(resultSet.getString("Email"));
        
        // Chuyển đổi string position thành enum
        String positionStr = resultSet.getString("Position");
        for (EmployeePositionEnum position : EmployeePositionEnum.values()) {
            if (position.getDisplayName().equals(positionStr)) {
                employee.setPosition(position);
                break;
            }
        }
        
        // Các thuộc tính quan hệ sẽ được load khi cần thông qua các Repository tương ứng
        
        return employee;
    }
}