package com.pcstore.repository.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pcstore.repository.iRevenueRepository;

/**
 * Implementation of RevenueRepository interface for handling revenue-related database operations
 */
public class RevenueRepository implements iRevenueRepository {
    private static final Logger logger = Logger.getLogger(RevenueRepository.class.getName());
    private Connection connection;
    
    /**
     * Constructor with database connection parameter
     * 
     * @param connection Database connection
     */
    public RevenueRepository(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * Get revenue data for products within a date range
     * 
     * @param fromDateTime Start date and time
     * @param toDateTime End date and time
     * @return List of maps containing revenue data
     */
    @Override
    public List<Map<String, Object>> getRevenueData(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        List<Map<String, Object>> revenueData = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("    e.FullName AS employeeName, ");
        sql.append("    p.ProductID AS productId, ");
        sql.append("    p.ProductName AS productName, ");
        sql.append("    SUM(id.Quantity) AS quantity, ");
        sql.append("    id.UnitPrice AS unitPrice, ");
        sql.append("    SUM(id.Quantity * id.UnitPrice) AS revenue ");
        sql.append("FROM InvoiceDetails id ");
        sql.append("JOIN Invoices i ON id.InvoiceID = i.InvoiceID ");
        sql.append("JOIN Employees e ON i.EmployeeID = e.EmployeeID ");
        sql.append("JOIN Products p ON id.ProductID = p.ProductID ");
        sql.append("WHERE i.StatusID = 3 "); // Assuming 3 is the status for completed/paid invoices
        sql.append("AND i.InvoiceDate BETWEEN ? AND ? ");
        sql.append("GROUP BY e.FullName, p.ProductID, p.ProductName, id.UnitPrice ");
        sql.append("ORDER BY revenue DESC");
        
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.setTimestamp(1, Timestamp.valueOf(fromDateTime));
            statement.setTimestamp(2, Timestamp.valueOf(toDateTime));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("employeeName", resultSet.getString("employeeName"));
                    row.put("productId", resultSet.getString("productId"));
                    row.put("productName", resultSet.getString("productName"));
                    row.put("quantity", resultSet.getInt("quantity"));
                    row.put("unitPrice", resultSet.getBigDecimal("unitPrice"));
                    row.put("revenue", resultSet.getBigDecimal("revenue"));
                    revenueData.add(row);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving revenue data", e);
            e.printStackTrace();
        }
        
        return revenueData;
    }
    
    /**
     * Get revenue for a specific product within a date range
     * 
     * @param productId Product ID
     * @param fromDateTime Start date and time
     * @param toDateTime End date and time
     * @return Total revenue for the product
     */
    @Override
    public BigDecimal getRevenueByProduct(String productId, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        BigDecimal revenue = BigDecimal.ZERO;
        String sql = "SELECT SUM(id.Quantity * id.UnitPrice) AS revenue " +
                     "FROM InvoiceDetails id " +
                     "JOIN Invoices i ON id.InvoiceID = i.InvoiceID " +
                     "WHERE i.StatusID = 3 " + // Completed/paid invoices
                     "AND id.ProductID = ? " +
                     "AND i.InvoiceDate BETWEEN ? AND ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            statement.setTimestamp(2, Timestamp.valueOf(fromDateTime));
            statement.setTimestamp(3, Timestamp.valueOf(toDateTime));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    revenue = resultSet.getBigDecimal("revenue");
                    if (revenue == null) {
                        revenue = BigDecimal.ZERO;
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving revenue for product: " + productId, e);
            e.printStackTrace();
        }
        
        return revenue;
    }
    
    /**
     * Get quantity sold for a specific product within a date range
     * 
     * @param productId Product ID
     * @param fromDateTime Start date and time
     * @param toDateTime End date and time
     * @return Total quantity sold for the product
     */
    @Override
    public int getQuantitySoldByProduct(String productId, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        int quantity = 0;
        String sql = "SELECT SUM(id.Quantity) AS quantity " +
                     "FROM InvoiceDetails id " +
                     "JOIN Invoices i ON id.InvoiceID = i.InvoiceID " +
                     "WHERE i.StatusID = 3 " + // Completed/paid invoices
                     "AND id.ProductID = ? " +
                     "AND i.InvoiceDate BETWEEN ? AND ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            statement.setTimestamp(2, Timestamp.valueOf(fromDateTime));
            statement.setTimestamp(3, Timestamp.valueOf(toDateTime));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    quantity = resultSet.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving quantity sold for product: " + productId, e);
            e.printStackTrace();
        }
        
        return quantity;
    }
    
    /**
     * Get top selling products within a date range
     * 
     * @param limit Number of top products to retrieve
     * @param fromDateTime Start date and time
     * @param toDateTime End date and time
     * @return List of maps containing top selling products data
     */
    @Override
    public List<Map<String, Object>> getTopSellingProducts(int limit, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        List<Map<String, Object>> topProducts = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("    p.ProductID AS productId, ");
        sql.append("    p.ProductName AS productName, ");
        sql.append("    SUM(id.Quantity) AS quantity, ");
        sql.append("    SUM(id.Quantity * id.UnitPrice) AS revenue ");
        sql.append("FROM InvoiceDetails id ");
        sql.append("JOIN Invoices i ON id.InvoiceID = i.InvoiceID ");
        sql.append("JOIN Products p ON id.ProductID = p.ProductID ");
        sql.append("WHERE i.StatusID = 3 "); // Completed/paid invoices
        sql.append("AND i.InvoiceDate BETWEEN ? AND ? ");
        sql.append("GROUP BY p.ProductID, p.ProductName ");
        sql.append("ORDER BY quantity DESC ");
        
        // Check if the database supports LIMIT clause (SQL Server uses TOP instead)
        // For SQL Server:
        String dbProduct = "";
        try {
            dbProduct = connection.getMetaData().getDatabaseProductName().toLowerCase();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Could not determine database type", e);
        }
        
        if (dbProduct.contains("microsoft") || dbProduct.contains("sql server")) {
            // For SQL Server
            sql.insert(0, "SELECT TOP " + limit + " ");
            sql.delete(7, 16); // Remove the original SELECT part
        } else {
            // For MySQL, PostgreSQL, etc.
            sql.append("LIMIT ?");
        }
        
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.setTimestamp(1, Timestamp.valueOf(fromDateTime));
            statement.setTimestamp(2, Timestamp.valueOf(toDateTime));
            
            // Set the limit parameter for databases that use LIMIT
            if (!dbProduct.contains("microsoft") && !dbProduct.contains("sql server")) {
                statement.setInt(3, limit);
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("productId", resultSet.getString("productId"));
                    row.put("productName", resultSet.getString("productName"));
                    row.put("quantity", resultSet.getInt("quantity"));
                    row.put("revenue", resultSet.getBigDecimal("revenue"));
                    topProducts.add(row);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving top selling products", e);
            e.printStackTrace();
        }
        
        return topProducts;
    }

    @Override
    public List<Map<String, Object>> getEmployeeRevenueData(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        List<Map<String, Object>> employeeRevenueData = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("    e.EmployeeID AS employeeId, ");
        sql.append("    e.FullName AS employeeName, ");
        sql.append("    COUNT(DISTINCT i.InvoiceID) AS invoiceCount, ");
        sql.append("    SUM(id.Quantity) AS productCount, ");
        sql.append("    SUM(id.Quantity * id.UnitPrice) AS revenue ");
        sql.append("FROM Employees e ");
        sql.append("JOIN Invoices i ON e.EmployeeID = i.EmployeeID ");
        sql.append("JOIN InvoiceDetails id ON i.InvoiceID = id.InvoiceID ");
        sql.append("WHERE i.StatusID = 3 "); // Assuming 3 is the status for completed/paid invoices
        sql.append("AND i.InvoiceDate BETWEEN ? AND ? ");
        sql.append("GROUP BY e.EmployeeID, e.FullName ");
        sql.append("ORDER BY revenue DESC");
        
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.setTimestamp(1, Timestamp.valueOf(fromDateTime));
            statement.setTimestamp(2, Timestamp.valueOf(toDateTime));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("employeeId", resultSet.getString("employeeId"));
                    row.put("employeeName", resultSet.getString("employeeName"));
                    row.put("invoiceCount", resultSet.getInt("invoiceCount"));
                    row.put("productCount", resultSet.getInt("productCount"));
                    row.put("revenue", resultSet.getBigDecimal("revenue"));
                    employeeRevenueData.add(row);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving employee revenue data", e);
            e.printStackTrace();
        }
        
        return employeeRevenueData;
    }

    @Override
    public BigDecimal getRevenueByEmployee(String employeeId, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        BigDecimal revenue = BigDecimal.ZERO;
        String sql = "SELECT SUM(id.Quantity * id.UnitPrice) AS revenue " +
                     "FROM InvoiceDetails id " +
                     "JOIN Invoices i ON id.InvoiceID = i.InvoiceID " +
                     "WHERE i.StatusID = 3 " + // Completed/paid invoices
                     "AND i.EmployeeID = ? " +
                     "AND i.InvoiceDate BETWEEN ? AND ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employeeId);
            statement.setTimestamp(2, Timestamp.valueOf(fromDateTime));
            statement.setTimestamp(3, Timestamp.valueOf(toDateTime));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    revenue = resultSet.getBigDecimal("revenue");
                    if (revenue == null) {
                        revenue = BigDecimal.ZERO;
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving revenue for employee: " + employeeId, e);
            e.printStackTrace();
        }
        
        return revenue;
    }

    @Override
    public int getNumberOfSalesByEmployee(String employeeId, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        int count = 0;
        String sql = "SELECT COUNT(DISTINCT i.InvoiceID) AS sales_count " +
                     "FROM Invoices i " +
                     "WHERE i.StatusID = 3 " + // Completed/paid invoices
                     "AND i.EmployeeID = ? " +
                     "AND i.InvoiceDate BETWEEN ? AND ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employeeId);
            statement.setTimestamp(2, Timestamp.valueOf(fromDateTime));
            statement.setTimestamp(3, Timestamp.valueOf(toDateTime));
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt("sales_count");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving sales count for employee: " + employeeId, e);
            e.printStackTrace();
        }
        
        return count;
    }

    @Override
    public List<Map<String, Object>> getTopPerformingEmployees(int limit, LocalDateTime fromDateTime,
            LocalDateTime toDateTime) {
        List<Map<String, Object>> topEmployees = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("    e.EmployeeID AS employeeId, ");
        sql.append("    e.FullName AS employeeName, ");
        sql.append("    COUNT(DISTINCT i.InvoiceID) AS invoiceCount, ");
        sql.append("    SUM(id.Quantity) AS productCount, ");
        sql.append("    SUM(id.Quantity * id.UnitPrice) AS revenue ");
        sql.append("FROM Employees e ");
        sql.append("JOIN Invoices i ON e.EmployeeID = i.EmployeeID ");
        sql.append("JOIN InvoiceDetails id ON i.InvoiceID = id.InvoiceID ");
        sql.append("WHERE i.StatusID = 3 "); // Completed/paid invoices
        sql.append("AND i.InvoiceDate BETWEEN ? AND ? ");
        sql.append("GROUP BY e.EmployeeID, e.FullName ");
        sql.append("ORDER BY revenue DESC ");
        
        // Check if the database supports LIMIT clause (SQL Server uses TOP instead)
        String dbProduct = "";
        try {
            dbProduct = connection.getMetaData().getDatabaseProductName().toLowerCase();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Could not determine database type", e);
        }
        
        if (dbProduct.contains("microsoft") || dbProduct.contains("sql server")) {
            // For SQL Server
            sql.insert(0, "SELECT TOP " + limit + " ");
            sql.delete(7, 16); // Remove the original SELECT part
        } else {
            // For MySQL, PostgreSQL, etc.
            sql.append("LIMIT ?");
        }
        
        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.setTimestamp(1, Timestamp.valueOf(fromDateTime));
            statement.setTimestamp(2, Timestamp.valueOf(toDateTime));
            
            // Set the limit parameter for databases that use LIMIT
            if (!dbProduct.contains("microsoft") && !dbProduct.contains("sql server")) {
                statement.setInt(3, limit);
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("employeeId", resultSet.getString("employeeId"));
                    row.put("employeeName", resultSet.getString("employeeName"));
                    row.put("invoiceCount", resultSet.getInt("invoiceCount"));
                    row.put("productCount", resultSet.getInt("productCount"));
                    row.put("revenue", resultSet.getBigDecimal("revenue"));
                    topEmployees.add(row);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving top performing employees", e);
            e.printStackTrace();
        }
        
        return topEmployees;
    }
}