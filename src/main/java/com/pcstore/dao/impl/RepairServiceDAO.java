package com.pcstore.dao.impl;

import com.pcstore.dao.DAO;
import com.pcstore.model.RepairService;
import com.pcstore.model.enums.RepairStatusEnum;
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
import java.util.UUID;

/**
 * DAO implementation cho RepairService entity
 */
public class RepairServiceDAO implements DAO<RepairService, String> {
    private Connection connection;
    
    public RepairServiceDAO(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public RepairService add(RepairService repairService) {
        String sql = "INSERT INTO RepairServices (ServiceID, CustomerID, ProductDescription, " +
                     "IssueDescription, Status, EstimatedCost, StartDate, EstimatedCompletionDate, " +
                     "TechnicianID, Notes, CreatedAt, UpdatedAt) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Generate a new ID if not provided
            if (repairService.getId() == null) {
                repairService.setId(UUID.randomUUID().toString());
            }
            
            LocalDateTime now = LocalDateTime.now();
            repairService.setCreatedAt(now);
            repairService.setUpdatedAt(now);
            
            statement.setString(1, repairService.getId());
            statement.setString(2, repairService.getCustomerId());
            statement.setString(3, repairService.getProductDescription());
            statement.setString(4, repairService.getIssueDescription());
            statement.setString(5, repairService.getStatus().toString());
            statement.setBigDecimal(6, repairService.getEstimatedCost());
            statement.setTimestamp(7, repairService.getStartDate() != null ? 
                    Timestamp.valueOf(repairService.getStartDate()) : null);
            statement.setTimestamp(8, repairService.getEstimatedCompletionDate() != null ? 
                    Timestamp.valueOf(repairService.getEstimatedCompletionDate()) : null);
            statement.setString(9, repairService.getTechnicianId());
            statement.setString(10, repairService.getNotes());
            statement.setTimestamp(11, Timestamp.valueOf(repairService.getCreatedAt()));
            statement.setTimestamp(12, Timestamp.valueOf(repairService.getUpdatedAt()));
            
            statement.executeUpdate();
            return repairService;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding repair service", e);
        }
    }
    
    @Override
    public RepairService update(RepairService repairService) {
        String sql = "UPDATE RepairServices SET CustomerID = ?, ProductDescription = ?, " +
                     "IssueDescription = ?, Status = ?, EstimatedCost = ?, StartDate = ?, " +
                     "EstimatedCompletionDate = ?, ActualCompletionDate = ?, TechnicianID = ?, " +
                     "Notes = ?, FinalCost = ?, UpdatedAt = ? " +
                     "WHERE ServiceID = ?";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            LocalDateTime now = LocalDateTime.now();
            repairService.setUpdatedAt(now);
            
            statement.setString(1, repairService.getCustomerId());
            statement.setString(2, repairService.getProductDescription());
            statement.setString(3, repairService.getIssueDescription());
            statement.setString(4, repairService.getStatus().toString());
            statement.setBigDecimal(5, repairService.getEstimatedCost());
            statement.setTimestamp(6, repairService.getStartDate() != null ? 
                    Timestamp.valueOf(repairService.getStartDate()) : null);
            statement.setTimestamp(7, repairService.getEstimatedCompletionDate() != null ? 
                    Timestamp.valueOf(repairService.getEstimatedCompletionDate()) : null);
            statement.setTimestamp(8, repairService.getActualCompletionDate() != null ? 
                    Timestamp.valueOf(repairService.getActualCompletionDate()) : null);
            statement.setString(9, repairService.getTechnicianId());
            statement.setString(10, repairService.getNotes());
            statement.setBigDecimal(11, repairService.getFinalCost());
            statement.setTimestamp(12, Timestamp.valueOf(repairService.getUpdatedAt()));
            statement.setString(13, repairService.getId());
            
            statement.executeUpdate();
            return repairService;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating repair service", e);
        }
    }
    
    @Override
    public boolean delete(String serviceId) {
        String sql = "DELETE FROM RepairServices WHERE ServiceID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, serviceId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting repair service", e);
        }
    }
    
    @Override
    public Optional<RepairService> findById(String serviceId) {
        String sql = "SELECT rs.*, c.FullName as CustomerName, e.FullName as TechnicianName " +
                     "FROM RepairServices rs " +
                     "LEFT JOIN Customers c ON rs.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON rs.TechnicianID = e.EmployeeID " +
                     "WHERE ServiceID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, serviceId);
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
    public List<RepairService> findAll() {
        String sql = "SELECT rs.*, c.FullName as CustomerName, e.FullName as TechnicianName " +
                     "FROM RepairServices rs " +
                     "LEFT JOIN Customers c ON rs.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON rs.TechnicianID = e.EmployeeID";
                     
        List<RepairService> services = new ArrayList<>();
        
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
    public boolean exists(String serviceId) {
        String sql = "SELECT COUNT(*) FROM RepairServices WHERE ServiceID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, serviceId);
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
    public List<RepairService> findByCustomerId(String customerId) {
        String sql = "SELECT rs.*, c.FullName as CustomerName, e.FullName as TechnicianName " +
                     "FROM RepairServices rs " +
                     "LEFT JOIN Customers c ON rs.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON rs.TechnicianID = e.EmployeeID " +
                     "WHERE rs.CustomerID = ?";
                     
        List<RepairService> services = new ArrayList<>();
        
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
    public List<RepairService> findByStatus(RepairStatusEnum status) {
        String sql = "SELECT rs.*, c.FullName as CustomerName, e.FullName as TechnicianName " +
                     "FROM RepairServices rs " +
                     "LEFT JOIN Customers c ON rs.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON rs.TechnicianID = e.EmployeeID " +
                     "WHERE rs.Status = ?";
                     
        List<RepairService> services = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.toString());
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                services.add(mapResultSetToRepairService(resultSet));
            }
            return services;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding repair services by status", e);
        }
    }
    
    // Tìm kiếm dịch vụ sửa chữa theo kỹ thuật viên
    public List<RepairService> findByTechnician(String technicianId) {
        String sql = "SELECT rs.*, c.FullName as CustomerName, e.FullName as TechnicianName " +
                     "FROM RepairServices rs " +
                     "LEFT JOIN Customers c ON rs.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON rs.TechnicianID = e.EmployeeID " +
                     "WHERE rs.TechnicianID = ?";
                     
        List<RepairService> services = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, technicianId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                services.add(mapResultSetToRepairService(resultSet));
            }
            return services;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding repair services by technician ID", e);
        }
    }
    
    // Tìm kiếm dịch vụ đến hạn trong ngày
    public List<RepairService> findDueToday() {
        String sql = "SELECT rs.*, c.FullName as CustomerName, e.FullName as TechnicianName " +
                     "FROM RepairServices rs " +
                     "LEFT JOIN Customers c ON rs.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON rs.TechnicianID = e.EmployeeID " +
                     "WHERE DATE(rs.EstimatedCompletionDate) = CURRENT_DATE AND rs.Status != 'COMPLETED'";
                     
        List<RepairService> services = new ArrayList<>();
        
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
    public boolean updateStatus(String serviceId, RepairStatusEnum status) {
        String sql = "UPDATE RepairServices SET Status = ?, UpdatedAt = ? WHERE ServiceID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.toString());
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(3, serviceId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating repair service status", e);
        }
    }
    
    // Hoàn thành dịch vụ sửa chữa
    public boolean completeService(String serviceId, LocalDateTime completionDate, String notes, double finalCost) {
        String sql = "UPDATE RepairServices SET Status = ?, ActualCompletionDate = ?, " +
                     "Notes = ?, FinalCost = ?, UpdatedAt = ? WHERE ServiceID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, RepairStatusEnum.COMPLETED.toString());
            statement.setTimestamp(2, Timestamp.valueOf(completionDate));
            statement.setString(3, notes);
            statement.setBigDecimal(4, java.math.BigDecimal.valueOf(finalCost));
            statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(6, serviceId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error completing repair service", e);
        }
    }
    
    private RepairService mapResultSetToRepairService(ResultSet resultSet) throws SQLException {
        RepairService service = new RepairService();
        service.setId(resultSet.getString("ServiceID"));
        service.setCustomerId(resultSet.getString("CustomerID"));
        service.setProductDescription(resultSet.getString("ProductDescription"));
        service.setIssueDescription(resultSet.getString("IssueDescription"));
        service.setStatus(RepairStatusEnum.valueOf(resultSet.getString("Status")));
        service.setEstimatedCost(resultSet.getBigDecimal("EstimatedCost"));
        
        Timestamp startDate = resultSet.getTimestamp("StartDate");
        if (startDate != null) {
            service.setStartDate(startDate.toLocalDateTime());
        }
        
        Timestamp estimatedDate = resultSet.getTimestamp("EstimatedCompletionDate");
        if (estimatedDate != null) {
            service.setEstimatedCompletionDate(estimatedDate.toLocalDateTime());
        }
        
        Timestamp actualDate = resultSet.getTimestamp("ActualCompletionDate");
        if (actualDate != null) {
            service.setActualCompletionDate(actualDate.toLocalDateTime());
        }
        
        service.setTechnicianId(resultSet.getString("TechnicianID"));
        service.setNotes(resultSet.getString("Notes"));
        service.setFinalCost(resultSet.getBigDecimal("FinalCost"));
        
        // Additional data from joins
        try {
            service.setCustomerName(resultSet.getString("CustomerName"));
            service.setTechnicianName(resultSet.getString("TechnicianName"));
        } catch (SQLException e) {
            // Ignore if these columns don't exist
        }
        
        // Get created and updated timestamps
        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        if (createdAt != null) {
            service.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = resultSet.getTimestamp("UpdatedAt");
        if (updatedAt != null) {
            service.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return service;
    }
}