package com.pcstore.repository.impl;

import com.pcstore.repository.Repository;
import com.pcstore.utils.LocaleManager;
import com.pcstore.model.Employee;
import com.pcstore.model.enums.EmployeePositionEnum;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Repository implementation cho Employee entity
 */
public class EmployeeRepository implements Repository<Employee, String> {
    private Connection connection;

    Properties prop = LocaleManager.getInstance().getProperties();
    
    public EmployeeRepository(Connection connection) {
        this.connection = connection;
    }

    public Employee save(Employee employee) {
        Optional<Employee> existingEmployee = findById(employee.getEmployeeId());
        if (existingEmployee.isPresent()) {
            return update(employee);
        } else {
            return add(employee);
        }
    }
    
    @Override
    public Employee add(Employee employee) {
        String sql = "INSERT INTO Employees (EmployeeID, FullName, PhoneNumber, Email, Position, Gender, DateOfBirth, Avatar) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employee.getEmployeeId());
            statement.setString(2, employee.getFullName());
            statement.setString(3, employee.getPhoneNumber());
            statement.setString(4, employee.getEmail());
            statement.setString(5, employee.getPosition().getDisplayName());
            statement.setString(6, employee.getGender());
            statement.setString(7, employee.getAvatar());
            
            // Xử lý ngày sinh, chuyển LocalDate thành Date
            if (employee.getDateOfBirth() != null) {
                statement.setDate(8, employee.getDateOfBirth());
            } else {
                statement.setNull(8, Types.DATE);
            }
            
            statement.executeUpdate();
            
            LocalDateTime now = LocalDateTime.now();
            employee.setCreatedAt(now);
            employee.setUpdatedAt(now);
            
            return employee;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding employee: " + e);
        }
    }
    
    @Override
    public Employee update(Employee employee) {
        String sql = "UPDATE Employees SET FullName = ?, PhoneNumber = ?, Email = ?, Position = ?, " +
                     "Gender = ?, DateOfBirth = ?, Avatar = ?, UpdatedAt = ? WHERE EmployeeID = ?";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employee.getFullName());
            statement.setString(2, employee.getPhoneNumber());
            statement.setString(3, employee.getEmail());
            statement.setString(4, employee.getPosition().getDisplayName());
            statement.setString(5, employee.getGender());
            
            // Xử lý ngày sinh
            if (employee.getDateOfBirth() != null) {
                statement.setDate(6, employee.getDateOfBirth());
            } else {
                statement.setNull(6, Types.DATE);
            }
            statement.setString(7, employee.getAvatar());
            statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(9, employee.getEmployeeId());
            
            statement.executeUpdate();
            
            employee.setUpdatedAt(LocalDateTime.now());
            return employee;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating employee: " + e);
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
            throw new RuntimeException("Error deleting employeed: "+ e);
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
            throw new RuntimeException("Error finding employee by IDd: "+ e);
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
            throw new RuntimeException("Error finding all employeesd: "+ e);
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
            throw new RuntimeException("Error checking if employee existsd: "+ e);
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
            throw new RuntimeException("Error finding employees by positiond: "+ e);
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
            throw new RuntimeException("Error finding employee by emaild: "+ e);
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
            throw new RuntimeException("Error finding employee by phone numberd: "+ e);
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
            int newId = maxId + 1;
            return "NV" + String.format("%02d", newId);
        } catch (SQLException e) {
            throw new RuntimeException("Error generating employee IDd: "+ e);
        }
    }


     /**
     * Tìm nhân viên theo chức vụ
     * @param position Chức vụ cần tìm
     * @return Danh sách nhân viên có chức vụ trùng khớp
     */
    public List<Employee> findByPosition(String position) {
        String sql = "SELECT * FROM Employees WHERE Position = ?";
        List<Employee> employees = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, position);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                employees.add(mapResultSetToEmployee(resultSet));
            }
            return employees;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding employees by positiond: "+ e);
        }
    }


    /**
     * Tìm kiếm nhân viên theo từ khóa
     * @param searchTerm Từ khóa tìm kiếm
     * @return Danh sách nhân viên phù hợp
     */
    public List<Employee> search(String searchTerm) {
        String sql = "SELECT * FROM Employees WHERE FullName LIKE ? OR EmployeeID LIKE ?";
        List<Employee> employees = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                employees.add(mapResultSetToEmployee(resultSet));
            }
            return employees;
        } catch (SQLException e) {
            throw new RuntimeException("Error searching employeesd: "+ e);
        }
    }
    
    private Employee mapResultSetToEmployee(ResultSet resultSet) throws SQLException {
        Employee employee = new Employee();

        employee.setEmployeeId(resultSet.getString("EmployeeID"));
        employee.setFullName(resultSet.getString("FullName"));
        employee.setPhoneNumber(resultSet.getString("PhoneNumber"));
        employee.setEmail(resultSet.getString("Email"));

        String gender = resultSet.getString("Gender");

        employee.setGender(gender);
        
        // Đọc ngày sinh từ ResultSet
        Date birthDate = resultSet.getDate("DateOfBirth");
        if (birthDate != null) {
            employee.setDateOfBirth(birthDate);
        }
        
        employee.setAvatar(resultSet.getString("Avatar"));

        Timestamp createdTimestamp = resultSet.getTimestamp("CreatedAt");
        if (createdTimestamp != null) {
            employee.setCreatedAt(createdTimestamp.toLocalDateTime());
        }
        
        Timestamp updatedTimestamp = resultSet.getTimestamp("UpdatedAt");
        if (updatedTimestamp != null) {
            employee.setUpdatedAt(updatedTimestamp.toLocalDateTime());
        }
        
        // Chuyển đổi string position thành enum
        String positionStr = resultSet.getString("Position");
        for (EmployeePositionEnum position : EmployeePositionEnum.values()) {
            if (position.getDisplayName().equals(positionStr)) {
                employee.setPosition(position);
                break;
            }
        }
        
        return employee;
    }
}