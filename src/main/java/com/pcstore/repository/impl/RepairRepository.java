package com.pcstore.repository.impl;

import com.pcstore.repository.Repository;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.Repair;
import com.pcstore.model.enums.RepairEnum;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository implementation cho RepairService entity
 */
public class RepairRepository implements Repository<Repair, Integer> {
    private Connection connection;
    
    public RepairRepository(Connection connection) {
        this.connection = connection;
    }
    
    // public RepairServiceRepository(Connection connection2, RepositoryFactory RepositoryFactory) {
    //     //TODO Auto-generated constructor stub
    // }

        @Override
    public Repair add(Repair repairService) {
        String sql = "INSERT INTO RepairServices (CustomerID, EmployeeID, DeviceName, " +
                     "Problem, DiagnosisResult, RepairCost, ReceiveDate, EstimatedCompletionDate, " +
                     "ActualCompletionDate, Status, Notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime now = LocalDateTime.now();
            repairService.setCreatedAt(now);
            repairService.setUpdatedAt(now);
            
            statement.setString(1, repairService.getCustomer().getCustomerId());
            statement.setString(2, repairService.getEmployee() != null ? 
                    repairService.getEmployee().getEmployeeId() : null);
            statement.setString(3, repairService.getDeviceName());
            statement.setString(4, repairService.getProblem());
            statement.setString(5, repairService.getDiagnosis());
            statement.setBigDecimal(6, repairService.getServiceFee());
            statement.setTimestamp(7, repairService.getReceiveDate() != null ? 
                    Timestamp.valueOf(repairService.getReceiveDate()) : null);
            statement.setTimestamp(8, null); // EstimatedCompletionDate không có trong model
            statement.setTimestamp(9, repairService.getCompletionDate() != null ? 
                    Timestamp.valueOf(repairService.getCompletionDate()) : null);
            statement.setString(10, repairService.getStatus().toString());
            statement.setString(11, repairService.getNotes());
            
            statement.executeUpdate();
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    repairService.setRepairServiceId(generatedKeys.getInt(1));
                }
            }
            
            return repairService;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm dịch vụ sửa chữa", e);
        }
    }
    
        @Override
    public Repair update(Repair repairService) {
        String sql = "UPDATE RepairServices SET CustomerID = ?, EmployeeID = ?, " +
                     "DeviceName = ?, Problem = ?, DiagnosisResult = ?, " +
                     "RepairCost = ?, ReceiveDate = ?, EstimatedCompletionDate = ?, " +
                     "ActualCompletionDate = ?, Status = ?, Notes = ? " +
                     "WHERE RepairID = ?";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            LocalDateTime now = LocalDateTime.now();
            repairService.setUpdatedAt(now);
            
            statement.setString(1, repairService.getCustomer().getCustomerId());
            statement.setString(2, repairService.getEmployee() != null ? 
                    repairService.getEmployee().getEmployeeId() : null);
            statement.setString(3, repairService.getDeviceName());
            statement.setString(4, repairService.getProblem());
            statement.setString(5, repairService.getDiagnosis());
            statement.setBigDecimal(6, repairService.getServiceFee());
            statement.setTimestamp(7, repairService.getReceiveDate() != null ? 
                    Timestamp.valueOf(repairService.getReceiveDate()) : null);
            statement.setTimestamp(8, null); // EstimatedCompletionDate không có trong model
            statement.setTimestamp(9, repairService.getCompletionDate() != null ? 
                    Timestamp.valueOf(repairService.getCompletionDate()) : null);
            statement.setString(10, repairService.getStatus().toString());
            statement.setString(11, repairService.getNotes());
            statement.setInt(12, repairService.getRepairServiceId());
            
            statement.executeUpdate();
            return repairService;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật dịch vụ sửa chữa", e);
        }
    }
    
    @Override
    public boolean delete(Integer repairServiceId) {
        String sql = "DELETE FROM RepairServices WHERE RepairID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, repairServiceId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting repair service", e);
        }
    }
    
    @Override
    public Optional<Repair> findById(Integer repairServiceId) {
        String sql = "SELECT rs.*, c.FullName as CustomerName, e.FullName as EmployeeName " +
                     "FROM RepairServices rs " +
                     "LEFT JOIN Customers c ON rs.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON rs.EmployeeID = e.EmployeeID " +
                     "WHERE rs.RepairID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, repairServiceId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToRepairService(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding repair service by ID", e);
        }
    }
    
    @Override
    public List<Repair> findAll() {
        String sql = "SELECT rs.*, c.FullName as CustomerName, e.FullName as EmployeeName " +
                     "FROM RepairServices rs " +
                     "LEFT JOIN Customers c ON rs.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON rs.EmployeeID = e.EmployeeID";
                     
        List<Repair> services = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
             
            while (resultSet.next()) {
                services.add(mapResultSetToRepairService(resultSet));
            }
            return services;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all repair services", e);
        }
    }
    
    @Override
    public boolean exists(Integer repairServiceId) {
        String sql = "SELECT COUNT(*) FROM RepairServices WHERE RepairID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, repairServiceId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if repair service exists", e);
        }
    }
    
    // Tìm kiếm dịch vụ sửa chữa theo khách hàng
    public List<Repair> findByCustomerId(String customerId) {
        String sql = "SELECT rs.*, c.FullName as CustomerName, e.FullName as EmployeeName " +
                     "FROM RepairServices rs " +
                     "LEFT JOIN Customers c ON rs.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON rs.EmployeeID = e.EmployeeID " +
                     "WHERE rs.CustomerID = ?";
                     
        List<Repair> services = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                services.add(mapResultSetToRepairService(resultSet));
            }
            return services;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding repair services by customer ID", e);
        }
    }
    
    // Tìm kiếm dịch vụ sửa chữa theo trạng thái
    public List<Repair> findByStatus(String status) {
        String sql = "SELECT rs.*, c.FullName as CustomerName, e.FullName as EmployeeName " +
                     "FROM RepairServices rs " +
                     "LEFT JOIN Customers c ON rs.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON rs.EmployeeID = e.EmployeeID " +
                     "WHERE rs.Status = ?";
                     
        List<Repair> services = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                services.add(mapResultSetToRepairService(resultSet));
            }
            return services;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding repair services by status", e);
        }
    }
    
    // Tìm kiếm dịch vụ sửa chữa theo nhân viên kỹ thuật
    public List<Repair> findByEmployeeId(String employeeId) {
        String sql = "SELECT rs.*, c.FullName as CustomerName, e.FullName as EmployeeName " +
                     "FROM RepairServices rs " +
                     "LEFT JOIN Customers c ON rs.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON rs.EmployeeID = e.EmployeeID " +
                     "WHERE rs.EmployeeID = ?";
                     
        List<Repair> services = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employeeId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                services.add(mapResultSetToRepairService(resultSet));
            }
            return services;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding repair services by employee ID", e);
        }
    }
    
    // Tìm kiếm dịch vụ đến hạn hoàn thành trong ngày
    public List<Repair> findDueToday() {
        String sql = "SELECT rs.*, c.FullName as CustomerName, e.FullName as EmployeeName " +
                     "FROM RepairServices rs " +
                     "LEFT JOIN Customers c ON rs.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON rs.EmployeeID = e.EmployeeID " +
                     "WHERE DATE(rs.EstimatedCompletionDate) = CURRENT_DATE AND rs.Status != 'Completed' AND rs.Status != 'Cancelled'";
                     
        List<Repair> services = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
             
            while (resultSet.next()) {
                services.add(mapResultSetToRepairService(resultSet));
            }
            return services;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding due repair services", e);
        }
    }
    
    // Cập nhật trạng thái dịch vụ
    public boolean updateStatus(Integer repairServiceId, String status) {
        String sql = "UPDATE RepairServices SET Status = ? WHERE RepairID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, repairServiceId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating repair service status", e);
        }
    }
    
    // Hoàn thành dịch vụ sửa chữa
    public boolean completeService(Integer repairServiceId, LocalDateTime completionDate, String notes, double finalCost) {
        String sql = "UPDATE RepairServices SET Status = 'Completed', ActualCompletionDate = ?, " +
                     "Notes = ?, RepairCost = ? WHERE RepairID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(completionDate));
            statement.setString(2, notes);
            statement.setBigDecimal(3, java.math.BigDecimal.valueOf(finalCost));
            statement.setInt(4, repairServiceId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error completing repair service", e);
        }
    }
    
    private Repair mapResultSetToRepairService(ResultSet resultSet) throws SQLException {
        Repair repairService = new Repair();
        repairService.setRepairServiceId(resultSet.getInt("RepairID"));
        
        // Dữ liệu khách hàng và nhân viên sẽ được lấy từ service layer
        // Ở đây chỉ lấy ID để tham chiếu

        String customerId = resultSet.getString("CustomerID");
        String employeeId = resultSet.getString("EmployeeID");
        
        if (customerId != null) {
            Customer customer = new Customer();
            customer.setCustomerId(customerId);
            repairService.setCustomer(customer);
        }
        
        if (employeeId != null) {
            Employee employee = new Employee();
            employee.setEmployeeId(employeeId);
            repairService.setEmployee(employee);
        }
        
        repairService.setDeviceName(resultSet.getString("DeviceName"));
        repairService.setProblem(resultSet.getString("Problem"));
        repairService.setDiagnosis(resultSet.getString("DiagnosisResult"));
        repairService.setServiceFee(resultSet.getBigDecimal("RepairCost"));
        repairService.setStatus(resultSet.getString("Status"));
        repairService.setNotes(resultSet.getString("Notes"));
        
        Timestamp receiveDate = resultSet.getTimestamp("ReceiveDate");
        if (receiveDate != null) {
            repairService.setReceiveDate(receiveDate.toLocalDateTime());
        }
        
        Timestamp completionDate = resultSet.getTimestamp("ActualCompletionDate");
        if (completionDate != null) {
            repairService.setCompletionDate(completionDate.toLocalDateTime());
        }
        
        // Lấy thông tin bổ sung nếu có join
        try {
            String customerName = resultSet.getString("CustomerName");
            String employeeName = resultSet.getString("EmployeeName");
            
            // Có thể lưu trữ tạm thời để hiển thị UI
            // Đối tượng đầy đủ sẽ được thiết lập trong service layer
        } catch (SQLException e) {
            // Bỏ qua nếu không có cột này
        }
        
        // Lấy thời gian tạo và cập nhật nếu có
        try {
            Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
            if (createdAt != null) {
                repairService.setCreatedAt(createdAt.toLocalDateTime());
            }
            
            Timestamp updatedAt = resultSet.getTimestamp("UpdatedAt");
            if (updatedAt != null) {
                repairService.setUpdatedAt(updatedAt.toLocalDateTime());
            }
        } catch (SQLException e) {
            // Bỏ qua nếu không có các cột này
        }
        
        return repairService;
    }
}