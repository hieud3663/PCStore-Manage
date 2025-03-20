package com.pcstore.dao.impl;

import com.pcstore.dao.DAO;
import com.pcstore.model.Return;
import com.pcstore.model.Invoice;
import com.pcstore.model.Product;
import com.pcstore.model.Customer;

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
 * DAO implementation for Return entity
 */
public class ReturnDAO implements DAO<Return, Integer> {
    private Connection connection;
    
    public ReturnDAO(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public Return add(Return returnObj) {
        String sql = "INSERT INTO Returns (InvoiceID, ProductID, CustomerID, ReturnDate, Reason, " +
                     "Quantity, RefundAmount, Status, ApprovedBy, Notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, returnObj.getInvoiceID());
            statement.setString(2, returnObj.getProductID());
            statement.setString(3, returnObj.getCustomerID());
            statement.setTimestamp(4, Timestamp.valueOf(returnObj.getReturnDate()));
            statement.setString(5, returnObj.getReason());
            statement.setInt(6, returnObj.getQuantity());
            statement.setBigDecimal(7, returnObj.getRefundAmount());
            statement.setString(8, returnObj.getStatus());
            statement.setString(9, returnObj.getApprovedBy());
            statement.setString(10, returnObj.getNotes());
            
            statement.executeUpdate();
            
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                returnObj.setReturnID(generatedId);
            }
            
            LocalDateTime now = LocalDateTime.now();
            returnObj.setCreatedAt(now);
            returnObj.setUpdatedAt(now);
            
            return returnObj;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm đơn trả hàng", e);
        }
    }
    
    @Override
    public Return update(Return returnObj) {
        String sql = "UPDATE Returns SET InvoiceID = ?, ProductID = ?, CustomerID = ?, " +
                     "ReturnDate = ?, Reason = ?, Quantity = ?, RefundAmount = ?, Status = ?, " +
                     "ApprovedBy = ?, Notes = ? WHERE ReturnID = ?";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, returnObj.getInvoiceID());
            statement.setString(2, returnObj.getProductID());
            statement.setString(3, returnObj.getCustomerID());
            statement.setTimestamp(4, Timestamp.valueOf(returnObj.getReturnDate()));
            statement.setString(5, returnObj.getReason());
            statement.setInt(6, returnObj.getQuantity());
            statement.setBigDecimal(7, returnObj.getRefundAmount());
            statement.setString(8, returnObj.getStatus());
            statement.setString(9, returnObj.getApprovedBy());
            statement.setString(10, returnObj.getNotes());
            statement.setInt(11, returnObj.getReturnID());
            
            statement.executeUpdate();
            
            returnObj.setUpdatedAt(LocalDateTime.now());
            return returnObj;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật đơn trả hàng", e);
        }
    }
    
    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Returns WHERE ReturnID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting return", e);
        }
    }
    
    @Override
    public Optional<Return> findById(Integer id) {
        String sql = "SELECT r.*, i.InvoiceNumber, p.ProductName, c.FullName as CustomerName, " +
                     "e.FullName as ApproverName " +
                     "FROM Returns r " +
                     "LEFT JOIN Invoices i ON r.InvoiceID = i.InvoiceID " +
                     "LEFT JOIN Products p ON r.ProductID = p.ProductID " +
                     "LEFT JOIN Customers c ON r.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON r.ApprovedBy = e.EmployeeID " +
                     "WHERE r.ReturnID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToReturn(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding return by ID", e);
        }
    }
    
    @Override
    public List<Return> findAll() {
        String sql = "SELECT r.*, i.InvoiceNumber, p.ProductName, c.FullName as CustomerName, " +
                     "e.FullName as ApproverName " +
                     "FROM Returns r " +
                     "LEFT JOIN Invoices i ON r.InvoiceID = i.InvoiceID " +
                     "LEFT JOIN Products p ON r.ProductID = p.ProductID " +
                     "LEFT JOIN Customers c ON r.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON r.ApprovedBy = e.EmployeeID";
        List<Return> returns = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                returns.add(mapResultSetToReturn(resultSet));
            }
            return returns;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all returns", e);
        }
    }
    
    @Override
    public boolean exists(Integer id) {
        String sql = "SELECT COUNT(*) FROM Returns WHERE ReturnID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if return exists", e);
        }
    }
    
    // Find returns by invoice
    public List<Return> findByInvoiceId(String invoiceId) {
        String sql = "SELECT r.*, i.InvoiceNumber, p.ProductName, c.FullName as CustomerName, " +
                     "e.FullName as ApproverName " +
                     "FROM Returns r " +
                     "LEFT JOIN Invoices i ON r.InvoiceID = i.InvoiceID " +
                     "LEFT JOIN Products p ON r.ProductID = p.ProductID " +
                     "LEFT JOIN Customers c ON r.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON r.ApprovedBy = e.EmployeeID " +
                     "WHERE r.InvoiceID = ?";
        
        List<Return> returns = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, invoiceId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                returns.add(mapResultSetToReturn(resultSet));
            }
            return returns;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding returns by invoice ID", e);
        }
    }
    
    // Find returns by customer
    public List<Return> findByCustomerId(String customerId) {
        String sql = "SELECT r.*, i.InvoiceNumber, p.ProductName, c.FullName as CustomerName, " +
                     "e.FullName as ApproverName " +
                     "FROM Returns r " +
                     "LEFT JOIN Invoices i ON r.InvoiceID = i.InvoiceID " +
                     "LEFT JOIN Products p ON r.ProductID = p.ProductID " +
                     "LEFT JOIN Customers c ON r.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON r.ApprovedBy = e.EmployeeID " +
                     "WHERE r.CustomerID = ?";
        
        List<Return> returns = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                returns.add(mapResultSetToReturn(resultSet));
            }
            return returns;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding returns by customer ID", e);
        }
    }
    
    // Find returns by product
    public List<Return> findByProductId(String productId) {
        String sql = "SELECT r.*, i.InvoiceNumber, p.ProductName, c.FullName as CustomerName, " +
                     "e.FullName as ApproverName " +
                     "FROM Returns r " +
                     "LEFT JOIN Invoices i ON r.InvoiceID = i.InvoiceID " +
                     "LEFT JOIN Products p ON r.ProductID = p.ProductID " +
                     "LEFT JOIN Customers c ON r.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON r.ApprovedBy = e.EmployeeID " +
                     "WHERE r.ProductID = ?";
        
        List<Return> returns = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                returns.add(mapResultSetToReturn(resultSet));
            }
            return returns;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding returns by product ID", e);
        }
    }
    
    // Find returns by status
    public List<Return> findByStatus(String status) {
        String sql = "SELECT r.*, i.InvoiceNumber, p.ProductName, c.FullName as CustomerName, " +
                     "e.FullName as ApproverName " +
                     "FROM Returns r " +
                     "LEFT JOIN Invoices i ON r.InvoiceID = i.InvoiceID " +
                     "LEFT JOIN Products p ON r.ProductID = p.ProductID " +
                     "LEFT JOIN Customers c ON r.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON r.ApprovedBy = e.EmployeeID " +
                     "WHERE r.Status = ?";
        
        List<Return> returns = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                returns.add(mapResultSetToReturn(resultSet));
            }
            return returns;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding returns by status", e);
        }
    }
    
    // Find returns by date range
    public List<Return> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT r.*, i.InvoiceNumber, p.ProductName, c.FullName as CustomerName, " +
                     "e.FullName as ApproverName " +
                     "FROM Returns r " +
                     "LEFT JOIN Invoices i ON r.InvoiceID = i.InvoiceID " +
                     "LEFT JOIN Products p ON r.ProductID = p.ProductID " +
                     "LEFT JOIN Customers c ON r.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON r.ApprovedBy = e.EmployeeID " +
                     "WHERE r.ReturnDate BETWEEN ? AND ?";
        
        List<Return> returns = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(startDate));
            statement.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                returns.add(mapResultSetToReturn(resultSet));
            }
            return returns;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding returns by date range", e);
        }
    }
    
    // Update return status
    public boolean updateStatus(int returnId, String status, String approvedBy, String notes) {
        String sql = "UPDATE Returns SET Status = ?, ApprovedBy = ?, Notes = ? WHERE ReturnID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setString(2, approvedBy);
            statement.setString(3, notes);
            statement.setInt(4, returnId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating return status", e);
        }
    }
    
    private Return mapResultSetToReturn(ResultSet resultSet) throws SQLException {
        Return returnObj = new Return();
        returnObj.setReturnID(resultSet.getInt("ReturnID"));
        returnObj.setInvoiceID(resultSet.getString("InvoiceID"));
        returnObj.setProductID(resultSet.getString("ProductID"));
        returnObj.setCustomerID(resultSet.getString("CustomerID"));
        
        Timestamp returnDate = resultSet.getTimestamp("ReturnDate");
        if (returnDate != null) {
            returnObj.setReturnDate(returnDate.toLocalDateTime());
        }
        
        returnObj.setReason(resultSet.getString("Reason"));
        returnObj.setQuantity(resultSet.getInt("Quantity"));
        returnObj.setRefundAmount(resultSet.getBigDecimal("RefundAmount"));
        returnObj.setStatus(resultSet.getString("Status"));
        returnObj.setApprovedBy(resultSet.getString("ApprovedBy"));
        returnObj.setNotes(resultSet.getString("Notes"));
        
        // Additional data from joins
        try {
            returnObj.setInvoiceNumber(resultSet.getString("InvoiceNumber"));
            returnObj.setProductName(resultSet.getString("ProductName"));
            returnObj.setCustomerName(resultSet.getString("CustomerName"));
            returnObj.setApproverName(resultSet.getString("ApproverName"));
        } catch (SQLException e) {
            // Ignore if these columns don't exist
        }
        
        // Get created and updated timestamps
        try {
            Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
            if (createdAt != null) {
                returnObj.setCreatedAt(createdAt.toLocalDateTime());
            }
            
            Timestamp updatedAt = resultSet.getTimestamp("UpdatedAt");
            if (updatedAt != null) {
                returnObj.setUpdatedAt(updatedAt.toLocalDateTime());
            }
        } catch (SQLException e) {
            // Ignore if these columns don't exist
        }
        
        return returnObj;
    }
}