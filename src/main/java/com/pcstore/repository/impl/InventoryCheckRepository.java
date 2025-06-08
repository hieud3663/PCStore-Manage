package com.pcstore.repository.impl;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pcstore.model.Employee;
import com.pcstore.model.InventoryCheck;
import com.pcstore.model.InventoryCheckDetail;
import com.pcstore.repository.Repository;

/**
 * Repository implementation cho InventoryCheck entity
 */
public class InventoryCheckRepository implements Repository<InventoryCheck, Integer> {
    private Connection connection;

    public InventoryCheckRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public InventoryCheck add(InventoryCheck inventoryCheck) {
        String sql = "INSERT INTO InventoryChecks (CheckCode, EmployeeID, CheckName, CheckDate, CheckType, Status, Notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, inventoryCheck.getCheckCode());
            statement.setString(2, inventoryCheck.getEmployee() != null ? 
                    inventoryCheck.getEmployee().getEmployeeId() : null);
            statement.setString(3, inventoryCheck.getCheckName());
            statement.setObject(4, inventoryCheck.getCheckDate());
            statement.setString(5, inventoryCheck.getCheckType());
            statement.setString(6, inventoryCheck.getStatus());
            statement.setString(7, inventoryCheck.getNotes());
            
            statement.executeUpdate();
            
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                inventoryCheck.setCheckId(generatedKeys.getInt(1));
            }
            
            return inventoryCheck;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding inventory check", e);
        }
    }

    @Override
    public InventoryCheck update(InventoryCheck inventoryCheck) {
        String sql = "UPDATE InventoryChecks SET CheckCode = ?, EmployeeID = ?, CheckName = ?, " +
                     "CheckDate = ?, CheckType = ?, Status = ?, Notes = ?, UpdatedAt = ? " +
                     "WHERE InventoryCheckID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, inventoryCheck.getCheckCode());
            statement.setString(2, inventoryCheck.getEmployee() != null ? 
                    inventoryCheck.getEmployee().getEmployeeId() : null);
            statement.setString(3, inventoryCheck.getCheckName());
            statement.setObject(4, inventoryCheck.getCheckDate());
            statement.setString(5, inventoryCheck.getCheckType());
            statement.setString(6, inventoryCheck.getStatus());
            statement.setString(7, inventoryCheck.getNotes());
            statement.setObject(8, LocalDateTime.now());
            statement.setInt(9, inventoryCheck.getId());
            
            statement.executeUpdate();
            return inventoryCheck;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating inventory check", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM InventoryChecks WHERE InventoryCheckID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting inventory check", e);
        }
    }

    @Override
    public Optional<InventoryCheck> findById(Integer id) {
        String sql = "SELECT ic.*, e.FullName as EmployeeName " +
                     "FROM InventoryChecks ic " +
                     "LEFT JOIN Employees e ON ic.EmployeeID = e.EmployeeID " +
                     "WHERE ic.InventoryCheckID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToInventoryCheck(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inventory check by ID", e);
        }
    }

    @Override
    public List<InventoryCheck> findAll() {
        String sql = "SELECT ic.*, e.FullName as EmployeeName " +
                     "FROM InventoryChecks ic " +
                     "LEFT JOIN Employees e ON ic.EmployeeID = e.EmployeeID " +
                     "ORDER BY ic.CreatedAt DESC";
        
        List<InventoryCheck> inventoryChecks = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                inventoryChecks.add(mapResultSetToInventoryCheck(resultSet));
            }
            return inventoryChecks;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all inventory checks", e);
        }
    }

    @Override
    public boolean exists(Integer id) {
        String sql = "SELECT COUNT(*) FROM InventoryChecks WHERE InventoryCheckID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking inventory check existence", e);
        }
    }

    /**
     * Tìm kiểm kê theo trạng thái
     * @param status Trạng thái cần tìm
     * @return Danh sách kiểm kê theo trạng thái
     */
    public List<InventoryCheck> findByStatus(String status) {
        String sql = "SELECT ic.*, e.FullName as EmployeeName " +
                     "FROM InventoryChecks ic " +
                     "LEFT JOIN Employees e ON ic.EmployeeID = e.EmployeeID " +
                     "WHERE ic.Status = ? " +
                     "ORDER BY ic.CreatedAt DESC";
        
        List<InventoryCheck> inventoryChecks = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                inventoryChecks.add(mapResultSetToInventoryCheck(resultSet));
            }
            return inventoryChecks;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inventory checks by status", e);
        }
    }

    /**
     * Tìm kiểm kê theo nhân viên
     * @param employeeId ID nhân viên
     * @return Danh sách kiểm kê của nhân viên
     */
    public List<InventoryCheck> findByEmployeeId(String employeeId) {
        String sql = "SELECT ic.*, e.FullName as EmployeeName " +
                     "FROM InventoryChecks ic " +
                     "LEFT JOIN Employees e ON ic.EmployeeID = e.EmployeeID " +
                     "WHERE ic.EmployeeID = ? " +
                     "ORDER BY ic.CreatedAt DESC";
        
        List<InventoryCheck> inventoryChecks = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employeeId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                inventoryChecks.add(mapResultSetToInventoryCheck(resultSet));
            }
            return inventoryChecks;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inventory checks by employee", e);
        }
    }

    /**
     * Tìm kiểm kê theo loại
     * @param checkType Loại kiểm kê
     * @return Danh sách kiểm kê theo loại
     */
    public List<InventoryCheck> findByCheckType(String checkType) {
        String sql = "SELECT ic.*, e.FullName as EmployeeName " +
                     "FROM InventoryChecks ic " +
                     "LEFT JOIN Employees e ON ic.EmployeeID = e.EmployeeID " +
                     "WHERE ic.CheckType = ? " +
                     "ORDER BY ic.CreatedAt DESC";
        
        List<InventoryCheck> inventoryChecks = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, checkType);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                inventoryChecks.add(mapResultSetToInventoryCheck(resultSet));
            }
            return inventoryChecks;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inventory checks by type", e);
        }
    }

    /**
     * Tìm kiểm kê theo mã code
     * @param checkCode Mã kiểm kê
     * @return Optional chứa kiểm kê nếu tìm thấy
     */
    public Optional<InventoryCheck> findByCheckCode(String checkCode) {
        String sql = "SELECT ic.*, e.FullName as EmployeeName " +
                     "FROM InventoryChecks ic " +
                     "LEFT JOIN Employees e ON ic.EmployeeID = e.EmployeeID " +
                     "WHERE ic.CheckCode = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, checkCode);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToInventoryCheck(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inventory check by code", e);
        }
    }

    /**
     * Tạo mã kiểm kê tự động
     * @return Mã kiểm kê mới
     */
    public String generateCheckCode() {
        String sql = "SELECT MAX(CAST(SUBSTRING(CheckCode, 3, LEN(CheckCode)-2) AS INT)) " +
                     "FROM InventoryChecks WHERE CheckCode LIKE 'KK%'";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            int maxId = 0;
            if (resultSet.next() && resultSet.getObject(1) != null) {
                maxId = resultSet.getInt(1);
            }
            
            // Tạo ID mới dạng KK001, KK002, ...
            return String.format("KK%03d", maxId + 1);
        } catch (SQLException e) {
            throw new RuntimeException("Error generating check code", e);
        }
    }

    /**
     * Cập nhật trạng thái kiểm kê
     * @param checkId ID kiểm kê
     * @param status Trạng thái mới
     * @return true nếu cập nhật thành công
     */
    public boolean updateStatus(Integer checkId, String status) {
        String sql = "UPDATE InventoryChecks SET Status = ?, UpdatedAt = ? WHERE InventoryCheckID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setObject(2, LocalDateTime.now());
            statement.setInt(3, checkId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating inventory check status", e);
        }
    }

    /**
     * Map ResultSet sang InventoryCheck object
     */
    private InventoryCheck mapResultSetToInventoryCheck(ResultSet resultSet) throws SQLException {
        InventoryCheck inventoryCheck = new InventoryCheck();
        inventoryCheck.setCheckId(resultSet.getInt("InventoryCheckID"));
        inventoryCheck.setCheckCode(resultSet.getString("CheckCode"));
        inventoryCheck.setCheckName(resultSet.getString("CheckName"));
        inventoryCheck.setCheckType(resultSet.getString("CheckType"));
        inventoryCheck.setStatus(resultSet.getString("Status"));
        inventoryCheck.setNotes(resultSet.getString("Notes"));
        
        // Xử lý ngày tháng
        Timestamp checkDate = resultSet.getTimestamp("CheckDate");
        if (checkDate != null) {
            inventoryCheck.setCheckDate(checkDate.toLocalDateTime());
        }
        
        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        if (createdAt != null) {
            inventoryCheck.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("UpdatedAt");
        if (updatedAt != null) {
            inventoryCheck.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        // Xử lý Employee
        String employeeId = resultSet.getString("EmployeeID");
        if (employeeId != null) {
            Employee employee = new Employee();
            employee.setEmployeeId(employeeId);
            employee.setFullName(resultSet.getString("EmployeeName"));
            inventoryCheck.setEmployee(employee);
        }
        
        return inventoryCheck;
    }
}
