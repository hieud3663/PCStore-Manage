package com.pcstore.repository.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pcstore.repository.iRevenueRepository;

/**
 * Implementation of RevenueRepository interface for handling revenue-related
 * database operations
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
     * @param toDateTime   End date and time
     * @return List of maps containing revenue data
     */
    @Override
    public List<Map<String, Object>> getRevenueData(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        List<Map<String, Object>> revenueData = new ArrayList<>();
        String sql = "SELECT " +
                "    p.ProductID AS productId, " +
                "    p.ProductName AS productName, " +
                "    COALESCE(SUM(id.Quantity), 0) AS quantity, " +
                "    COALESCE(id.CostPrice, 0) AS costPrice, " +
                "    COALESCE(id.UnitPrice, 0) AS unitPrice, " +
                "    COALESCE(SUM(id.DiscountAmount), 0) AS discountAmount, " +
                "    COALESCE(SUM(id.Quantity * id.UnitPrice), 0) AS totalAmount, " +
                "    COALESCE(SUM(id.Quantity * id.UnitPrice), 0) - COALESCE(SUM(id.DiscountAmount), 0) AS revenue " +
                "FROM InvoiceDetails id " +
                "JOIN Invoices i ON id.InvoiceID = i.InvoiceID " +
                "JOIN Products p ON id.ProductID = p.ProductID " +
                "WHERE i.StatusID = 3 " +
                "AND i.InvoiceDate BETWEEN ? AND ? " +
                "GROUP BY p.ProductID, p.ProductName, id.CostPrice, id.UnitPrice " +
                "ORDER BY revenue DESC";

        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.setTimestamp(1, Timestamp.valueOf(fromDateTime));
            statement.setTimestamp(2, Timestamp.valueOf(toDateTime));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("productId", resultSet.getString("productId"));
                    row.put("productName", resultSet.getString("productName"));
                    row.put("quantity", resultSet.getInt("quantity"));
                    row.put("costPrice", resultSet.getBigDecimal("costPrice"));
                    row.put("discountAmount", resultSet.getBigDecimal("discountAmount"));
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
     * @param productId    Product ID
     * @param fromDateTime Start date and time
     * @param toDateTime   End date and time
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
     * @param productId    Product ID
     * @param fromDateTime Start date and time
     * @param toDateTime   End date and time
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
     * @param limit        Number of top products to retrieve
     * @param fromDateTime Start date and time
     * @param toDateTime   End date and time
     * @return List of maps containing top selling products data
     */
    @Override
    public List<Map<String, Object>> getTopSellingProducts(int limit, LocalDateTime fromDateTime,
            LocalDateTime toDateTime) {
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

    public List<Map<String, Object>> getEmployeeDailyRevenueData(String employeeId,
            LocalDateTime fromDate,
            LocalDateTime toDate) {
        List<Map<String, Object>> result = new ArrayList<>();

        String sql = "SELECT CAST(i.InvoiceDate AS DATE) AS InvoiceDate, " +
                "SUM(id.Quantity) AS ProductCount, " +
                "SUM(id.Quantity * id.UnitPrice) AS Revenue " +
                "FROM Invoices i " +
                "JOIN InvoiceDetails id ON i.InvoiceID = id.InvoiceID " +
                "WHERE i.EmployeeID = ? " +
                "AND i.InvoiceDate BETWEEN ? AND ? " +
                "AND i.StatusID = 3 " + // Chỉ tính hóa đơn hoàn thành
                "GROUP BY CAST(i.InvoiceDate AS DATE) " +
                "ORDER BY CAST(i.InvoiceDate AS DATE)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employeeId);
            statement.setObject(2, fromDate);
            statement.setObject(3, toDate);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Map<String, Object> data = new HashMap<>();

                // Lấy ngày từ kết quả truy vấn
                Date sqlDate = rs.getDate("InvoiceDate");
                LocalDate date = sqlDate.toLocalDate();

                data.put("date", date);
                data.put("productCount", rs.getInt("ProductCount"));
                data.put("revenue", rs.getBigDecimal("Revenue"));

                result.add(data);
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting daily revenue data for employee", e);
        }
    }

    /**
     * Tính tổng doanh thu trong khoảng thời gian
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Tổng doanh thu
     */
    public BigDecimal calculateTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT SUM(id.Quantity * id.UnitPrice) AS TotalRevenue " +
                "FROM Invoices i " +
                "JOIN InvoiceDetails id ON i.InvoiceID = id.InvoiceID " +
                "WHERE i.InvoiceDate BETWEEN ? AND ? " +
                "AND i.StatusID = 3";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, startDate);
            statement.setObject(2, endDate);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                BigDecimal total = resultSet.getBigDecimal("TotalRevenue");
                return total != null ? total : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tính tổng doanh thu trong khoảng thời gian", e);
            throw new RuntimeException("Lỗi khi tính tổng doanh thu trong khoảng thời gian", e);
        }
    }

    /**
     * Lấy dữ liệu doanh thu theo ngày trong khoảng thời gian
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Danh sách dữ liệu doanh thu theo ngày
     */
    public List<Map<String, Object>> getDailyRevenueData(LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> result = new ArrayList<>();

        String sql = "SELECT CAST(i.InvoiceDate AS DATE) AS SaleDate, " +
                "COUNT(DISTINCT i.InvoiceID) AS OrderCount, " +
                "SUM(id.Quantity) AS ProductCount, " +
                "SUM(id.Quantity * id.UnitPrice) AS Revenue " +
                "FROM Invoices i " +
                "JOIN InvoiceDetails id ON i.InvoiceID = id.InvoiceID " +
                "WHERE i.InvoiceDate BETWEEN ? AND ? " +
                "AND i.StatusID IN (1, 2, 3) " + // Chỉ tính hóa đơn đã xác nhận/thanh toán/hoàn thành
                "GROUP BY CAST(i.InvoiceDate AS DATE) " +
                "ORDER BY CAST(i.InvoiceDate AS DATE)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, startDate);
            statement.setObject(2, endDate);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Map<String, Object> dailyData = new HashMap<>();

                // Lấy ngày từ kết quả truy vấn (chuyển từ java.sql.Date sang LocalDate)
                Date sqlDate = rs.getDate("SaleDate");
                LocalDate date = sqlDate.toLocalDate();

                dailyData.put("date", date);
                dailyData.put("orderCount", rs.getInt("OrderCount"));
                dailyData.put("productCount", rs.getInt("ProductCount"));
                dailyData.put("revenue", rs.getBigDecimal("Revenue"));

                result.add(dailyData);
            }

            return result;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy dữ liệu doanh thu theo ngày", e);
            throw new RuntimeException("Lỗi khi lấy dữ liệu doanh thu theo ngày", e);
        }
    }

    /**
     * Lấy dữ liệu doanh thu nhân viên theo khoảng thời gian cho analytics
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Map với key là employeeId và value là Map chứa thông tin nhân viên và
     *         doanh thu
     */
    public Map<String, Map<String, Object>> getEmployeeRevenueByDateRange(LocalDate startDate, LocalDate endDate)
            throws SQLException {
        Map<String, Map<String, Object>> employeeRevenueMap = new java.util.LinkedHashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("    e.EmployeeID AS employeeId, ");
        sql.append("    e.FullName AS fullName, ");
        sql.append("    COUNT(DISTINCT i.InvoiceID) AS totalInvoices, ");
        sql.append("    COUNT(DISTINCT i.CustomerID) AS customersServed, ");
        sql.append("    COALESCE(SUM(i.TotalAmount), 0) AS totalRevenue ");
        sql.append("FROM Employees e ");
        sql.append("LEFT JOIN Invoices i ON e.EmployeeID = i.EmployeeID ");
        sql.append("    AND i.InvoiceDate >= ? AND i.InvoiceDate <= ? ");
        sql.append("    AND i.StatusID = 3 "); // 3 = Completed status
        sql.append("GROUP BY e.EmployeeID, e.FullName ");
        sql.append("ORDER BY totalRevenue DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate.plusDays(1))); // Include end date

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String employeeId = rs.getString("employeeId");
                    Map<String, Object> employeeData = new HashMap<>();

                    employeeData.put("fullName", rs.getString("fullName"));
                    employeeData.put("totalInvoices", rs.getInt("totalInvoices"));
                    employeeData.put("customersServed", rs.getInt("customersServed"));
                    employeeData.put("totalRevenue", rs.getBigDecimal("totalRevenue"));

                    employeeRevenueMap.put(employeeId, employeeData);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy dữ liệu doanh thu nhân viên theo khoảng thời gian", e);
            throw e;
        }

        return employeeRevenueMap;
    }

    /**
     * Lấy sản phẩm bán chạy nhất trong khoảng thời gian
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Map chứa thông tin sản phẩm bán chạy nhất
     */
    public Map<String, Object> getBestSellingProduct(LocalDateTime startDate, LocalDateTime endDate)
            throws SQLException {
        Map<String, Object> result = new HashMap<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP 1 ");
        sql.append("    p.ProductID, ");
        sql.append("    p.ProductName, ");
        sql.append("    SUM(id.Quantity) AS totalQuantity, ");
        sql.append("    SUM(id.Quantity * id.UnitPrice) AS totalRevenue ");
        sql.append("FROM Products p ");
        sql.append("INNER JOIN InvoiceDetails id ON p.ProductID = id.ProductID ");
        sql.append("INNER JOIN Invoices i ON id.InvoiceID = i.InvoiceID ");
        sql.append("WHERE i.InvoiceDate >= ? AND i.InvoiceDate <= ? ");
        sql.append("    AND i.StatusID = 3 "); // 3 = Completed status
        sql.append("GROUP BY p.ProductID, p.ProductName ");
        sql.append("ORDER BY totalQuantity DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.put("productId", rs.getString("ProductID"));
                    result.put("productName", rs.getString("ProductName"));
                    result.put("totalQuantity", rs.getInt("totalQuantity"));
                    result.put("totalRevenue", rs.getBigDecimal("totalRevenue"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy sản phẩm bán chạy nhất", e);
            throw e;
        }

        return result;
    }

    /**
     * Lấy sản phẩm bán chậm nhất trong khoảng thời gian (có doanh thu > 0)
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Map chứa thông tin sản phẩm bán chậm nhất
     */
    public Map<String, Object> getSlowSellingProduct(LocalDateTime startDate, LocalDateTime endDate)
            throws SQLException {
        Map<String, Object> result = new HashMap<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP 1 ");
        sql.append("    p.ProductID, ");
        sql.append("    p.ProductName, ");
        sql.append("    SUM(id.Quantity) AS totalQuantity, ");
        sql.append("    SUM(id.Quantity * id.UnitPrice) AS totalRevenue ");
        sql.append("FROM Products p ");
        sql.append("INNER JOIN InvoiceDetails id ON p.ProductID = id.ProductID ");
        sql.append("INNER JOIN Invoices i ON id.InvoiceID = i.InvoiceID ");
        sql.append("WHERE i.InvoiceDate >= ? AND i.InvoiceDate <= ? ");
        sql.append("    AND i.StatusID = 3 ");
        sql.append("GROUP BY p.ProductID, p.ProductName ");
        sql.append("ORDER BY totalQuantity ASC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.put("productId", rs.getString("ProductID"));
                    result.put("productName", rs.getString("ProductName"));
                    result.put("totalQuantity", rs.getInt("totalQuantity"));
                    result.put("totalRevenue", rs.getBigDecimal("totalRevenue"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy sản phẩm bán chậm nhất", e);
            throw e;
        }

        return result;
    }

    /**
     * Tính tỷ suất lợi nhuận tổng trong khoảng thời gian
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Tỷ suất lợi nhuận (%)
     */
    public BigDecimal calculateProfitMargin(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("    COALESCE(SUM(id.Quantity * id.UnitPrice), 0) AS totalRevenue, ");
        sql.append("    COALESCE(SUM(id.Quantity * ISNULL(pod.UnitCost, p.Price * 0.7)), 0) AS totalCost ");
        sql.append("FROM InvoiceDetails id ");
        sql.append("INNER JOIN Invoices i ON id.InvoiceID = i.InvoiceID ");
        sql.append("INNER JOIN Products p ON id.ProductID = p.ProductID ");
        sql.append("LEFT JOIN ( ");
        sql.append("    SELECT pod.ProductID, AVG(pod.UnitCost) AS UnitCost ");
        sql.append("    FROM PurchaseOrderDetails pod ");
        sql.append("    INNER JOIN PurchaseOrders po ON pod.PurchaseOrderID = po.PurchaseOrderID ");
        sql.append("    WHERE po.Status = 'Completed' "); // Status column with 'Completed' value
        sql.append("    GROUP BY pod.ProductID ");
        sql.append(") pod ON p.ProductID = pod.ProductID ");
        sql.append("WHERE i.InvoiceDate >= ? AND i.InvoiceDate <= ? ");
        sql.append("    AND i.StatusID = 3");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal totalRevenue = rs.getBigDecimal("totalRevenue");
                    BigDecimal totalCost = rs.getBigDecimal("totalCost");

                    if (totalRevenue != null && totalCost != null &&
                            totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal profit = totalRevenue.subtract(totalCost);
                        return profit.divide(totalRevenue, 4, java.math.RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("100"));
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tính tỷ suất lợi nhuận", e);
            throw e;
        }

        return BigDecimal.ZERO;
    }

    /*
     * Tính tổng lợi nhuận trong khoảng thời gian
     */
    public Map<String, Object> calculateTotalProfit(LocalDateTime startDate, LocalDateTime endDate)
            throws SQLException {
        String sql = "SELECT " +
                "    SUM(COALESCE(id.Quantity * id.UnitPrice, 0) - COALESCE(id.DiscountAmount, 0) - COALESCE(id.Quantity * id.CostPrice, 0)) AS totalProfit, "
                +
                "    CASE  " +
                "        WHEN SUM(COALESCE(id.Quantity * id.UnitPrice, 0) - COALESCE(id.DiscountAmount, 0)) = 0 THEN 0 "
                +
                "        ELSE (SUM(COALESCE(id.Quantity * id.UnitPrice, 0) - COALESCE(id.DiscountAmount, 0) - COALESCE(id.Quantity * id.CostPrice, 0)) * 100.0) /  "
                +
                "             SUM(COALESCE(id.Quantity * id.UnitPrice, 0) - COALESCE(id.DiscountAmount, 0)) " +
                "    END AS profitMarginPercent " +
                "FROM InvoiceDetails id " +
                "JOIN Invoices i ON id.InvoiceID = i.InvoiceID " +
                "WHERE i.StatusID = 3 " +
                "AND i.InvoiceDate BETWEEN ? AND ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal totalProfit = rs.getBigDecimal("totalProfit");
                    BigDecimal profitMarginPercent = rs.getBigDecimal("profitMarginPercent");

                    Map<String, Object> result = new HashMap<>();
                    result.put("totalProfit", totalProfit != null ? totalProfit : BigDecimal.ZERO);
                    result.put("profitMarginPercent",
                            profitMarginPercent != null ? profitMarginPercent : BigDecimal.ZERO);
                    return result;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi tính tổng lợi nhuận", e);
            throw e;
        }

        return Collections.emptyMap();
    }

    /**
     * Lấy danh sách top sản phẩm bán chạy với thông tin chi tiết
     * 
     * @param limit     Số lượng sản phẩm
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Danh sách sản phẩm bán chạy
     */
    public List<Map<String, Object>> getTopBestSellingProducts(int limit, LocalDateTime startDate,
            LocalDateTime endDate) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP ").append(limit).append(" ");
        sql.append("    p.ProductID, ");
        sql.append("    p.ProductName, ");
        sql.append("    c.CategoryName, ");
        sql.append("    SUM(id.Quantity) AS totalQuantity, ");
        sql.append("    SUM(id.Quantity * id.UnitPrice) AS totalRevenue, ");
        sql.append("    AVG(id.UnitPrice) AS avgPrice ");
        sql.append("FROM Products p ");
        sql.append("INNER JOIN InvoiceDetails id ON p.ProductID = id.ProductID ");
        sql.append("INNER JOIN Invoices i ON id.InvoiceID = i.InvoiceID ");
        sql.append("LEFT JOIN Categories c ON p.CategoryID = c.CategoryID ");
        sql.append("WHERE i.InvoiceDate >= ? AND i.InvoiceDate <= ? ");
        sql.append("    AND i.StatusID = 3 ");
        sql.append("GROUP BY p.ProductID, p.ProductName, c.CategoryName ");
        sql.append("ORDER BY totalQuantity DESC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> productData = new HashMap<>();
                    productData.put("productId", rs.getString("ProductID"));
                    productData.put("productName", rs.getString("ProductName"));
                    productData.put("categoryName", rs.getString("CategoryName"));
                    productData.put("totalQuantity", rs.getInt("totalQuantity"));
                    productData.put("totalRevenue", rs.getBigDecimal("totalRevenue"));
                    productData.put("avgPrice", rs.getBigDecimal("avgPrice"));

                    result.add(productData);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách sản phẩm bán chạy", e);
            throw e;
        }

        return result;
    }

    /**
     * Lấy danh sách sản phẩm bán chậm với thông tin chi tiết
     * 
     * @param limit     Số lượng sản phẩm
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Danh sách sản phẩm bán chậm
     */
    public List<Map<String, Object>> getSlowSellingProducts(int limit, LocalDateTime startDate, LocalDateTime endDate)
            throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP ").append(limit).append(" ");
        sql.append("    p.ProductID, ");
        sql.append("    p.ProductName, ");
        sql.append("    c.CategoryName, ");
        sql.append("    SUM(id.Quantity) AS totalQuantity, ");
        sql.append("    SUM(id.Quantity * id.UnitPrice) AS totalRevenue, ");
        sql.append("    AVG(id.UnitPrice) AS avgPrice ");
        sql.append("FROM Products p ");
        sql.append("INNER JOIN InvoiceDetails id ON p.ProductID = id.ProductID ");
        sql.append("INNER JOIN Invoices i ON id.InvoiceID = i.InvoiceID ");
        sql.append("LEFT JOIN Categories c ON p.CategoryID = c.CategoryID ");
        sql.append("WHERE i.InvoiceDate >= ? AND i.InvoiceDate <= ? ");
        sql.append("    AND i.StatusID = 3 ");
        sql.append("GROUP BY p.ProductID, p.ProductName, c.CategoryName ");
        sql.append("ORDER BY totalQuantity ASC");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> productData = new HashMap<>();
                    productData.put("productId", rs.getString("ProductID"));
                    productData.put("productName", rs.getString("ProductName"));
                    productData.put("categoryName", rs.getString("CategoryName"));
                    productData.put("totalQuantity", rs.getInt("totalQuantity"));
                    productData.put("totalRevenue", rs.getBigDecimal("totalRevenue"));
                    productData.put("avgPrice", rs.getBigDecimal("avgPrice"));

                    result.add(productData);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy danh sách sản phẩm bán chậm", e);
            throw e;
        }

        return result;
    }

    /*
     * Thống kê hóa dơn theo khoảng thời gian
     */
    public List<Map<String, Object>> getInvoiceStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> statistics = new ArrayList<>();
        String sql = "SELECT i.*, c.FullName as CustomerName, c.PhoneNumber as CustomerPhone, e.FullName as EmployeeName, e.PhoneNumber as EmployeePhone, SUM(id.Quantity) as TotalQuantity "
                +
                "FROM Invoices i " +
                "LEFT JOIN Customers c ON i.CustomerID = c.CustomerID " +
                "LEFT JOIN Employees e ON i.EmployeeID = e.EmployeeID " +
                "JOIN InvoiceDetails id ON i.InvoiceID = id.InvoiceID " +
                "WHERE i.InvoiceDate BETWEEN ? AND ? " +
                "GROUP BY i.InvoiceID, i.InvoiceDate, i.TotalAmount, i.StatusID, i.CustomerID, i.EmployeeID, i.Notes, i.PaymentMethodID, i.DiscountAmount, c.FullName, c.PhoneNumber, e.FullName, e.PhoneNumber "
                +
                "ORDER BY i.InvoiceDate DESC";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(startDate));
            statement.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("invoiceId", resultSet.getString("InvoiceID"));
                    row.put("invoiceDate", resultSet.getTimestamp("InvoiceDate").toLocalDateTime());
                    row.put("totalAmount", resultSet.getBigDecimal("TotalAmount"));
                    row.put("statusId", resultSet.getInt("StatusID"));
                    row.put("customerId", resultSet.getString("CustomerID"));
                    row.put("employeeId", resultSet.getString("EmployeeID"));
                    row.put("notes", resultSet.getString("Notes"));
                    row.put("paymentMethodId", resultSet.getInt("PaymentMethodID"));
                    row.put("discountAmount", resultSet.getBigDecimal("DiscountAmount"));
                    row.put("customerName", resultSet.getString("CustomerName"));
                    row.put("customerPhone", resultSet.getString("CustomerPhone"));
                    row.put("employeeName", resultSet.getString("EmployeeName"));
                    row.put("employeePhone", resultSet.getString("EmployeePhone"));
                    row.put("totalQuantity", resultSet.getInt("TotalQuantity"));

                    statistics.add(row);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving invoice statistics", e);
            e.printStackTrace();
        }

        return statistics;
    }
}