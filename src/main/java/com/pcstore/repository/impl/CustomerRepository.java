package com.pcstore.repository.impl;

import com.pcstore.repository.Repository;
import com.pcstore.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository implementation cho Customer entity
 */
public class CustomerRepository implements Repository<Customer, String> {
    private Connection connection;
    private static final Logger logger = Logger.getLogger(CustomerRepository.class.getName());

    public CustomerRepository(Connection connection) {
        this.connection = connection;
    }

    public Customer save(Customer customer) {

        // Kiểm tra xem customer có tồn tại chưa đã
        Customer existingCustomer = findById(customer.getCustomerId()).orElse(null);
        if (existingCustomer != null) {
            return update(customer);
        } else {
            return add(customer);
        }

    }

    @Override
    public Customer add(Customer customer) {

        String sql = "INSERT INTO Customers (CustomerID, FullName, PhoneNumber, Email, Address, Point, CreatedAt, UpdatedAt) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            customer.setCustomerId(generateCustomerId());

            statement.setString(1, customer.getCustomerId());
            statement.setString(2, customer.getFullName());
            statement.setString(3, customer.getPhoneNumber());
            statement.setString(4, customer.getEmail());
            statement.setString(5, customer.getAddress());
            statement.setInt(6, customer.getPoints());

            LocalDateTime now = LocalDateTime.now();
            customer.setCreatedAt(now);
            customer.setUpdatedAt(now);

            statement.setObject(7, customer.getCreatedAt());
            statement.setObject(8, customer.getUpdatedAt());

            statement.executeUpdate();

            // Tạo mã khách hàng tự động
            return customer;
        } catch (SQLException e) {
            customer.setCustomerId(null); // Đặt lại mã khách hàng nếu có lỗi
            throw new RuntimeException("Error adding customer: " + e.getMessage(), e);
        }
    }

    @Override
    public Customer update(Customer customer) {
        String sql = "UPDATE Customers SET FullName = ?, PhoneNumber = ?, Email = ?, " +
                "Address = ?, Point = ?, UpdatedAt = ? WHERE CustomerID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customer.getFullName());
            statement.setString(2, customer.getPhoneNumber());
            statement.setString(3, customer.getEmail());
            statement.setString(4, customer.getAddress());
            statement.setInt(5, customer.getPoints());

            customer.setUpdatedAt(LocalDateTime.now());
            statement.setObject(6, customer.getUpdatedAt());

            statement.setString(7, customer.getCustomerId());

            statement.executeUpdate();
            return customer;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating customer", e);
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM Customers WHERE CustomerID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting customer", e);
        }
    }

    @Override
    public Optional<Customer> findById(String id) {
        String sql = "SELECT * FROM Customers WHERE CustomerID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToCustomer(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding customer by ID", e);
        }
    }

    @Override
    public List<Customer> findAll() {
        String sql = "SELECT * FROM Customers";
        List<Customer> customers = new ArrayList<>();

        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                customers.add(mapResultSetToCustomer(resultSet));
            }
            return customers;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all customers", e);
        }
    }

    @Override
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM Customers WHERE CustomerID = ? OR PhoneNumber = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if customer exists", e);
        }
    }

    // Tìm khách hàng theo số điện thoại
    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM Customers WHERE PhoneNumber = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, phoneNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToCustomer(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding customer by phone number", e);
        }
    }

    // Tìm khách hàng theo email
    public Optional<Customer> findByEmail(String email) {
        String sql = "SELECT * FROM Customers WHERE Email = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToCustomer(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding customer by email", e);
        }
    }

    // Tìm kiếm khách hàng theo tên (fuzzy search)
    public List<Customer> searchByName(String name) {
        String sql = "SELECT * FROM Customers WHERE FullName LIKE ?";
        List<Customer> customers = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + name + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                customers.add(mapResultSetToCustomer(resultSet));
            }
            return customers;
        } catch (SQLException e) {
            throw new RuntimeException("Error searching customers by name", e);
        }
    }

    // Tạo mã khách hàng tự động
    public String generateCustomerId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(CustomerID, 3, LEN(CustomerID)) AS INT)) FROM Customers WHERE CustomerID LIKE 'KH%'";

        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

            int maxId = 0;
            if (resultSet.next() && resultSet.getObject(1) != null) {
                maxId = resultSet.getInt(1);
            }

            // Tạo ID mới dạng KH01, KH02, ...
            return String.format("KH%02d", maxId + 1);
        } catch (SQLException e) {
            throw new RuntimeException("Error generating customer ID", e);
        }
    }

    private Customer mapResultSetToCustomer(ResultSet resultSet) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(resultSet.getString("CustomerID"));
        customer.setFullName(resultSet.getString("FullName"));
        customer.setPhoneNumber(resultSet.getString("PhoneNumber"));
        customer.setEmail(resultSet.getString("Email"));
        customer.setPoints(resultSet.getInt("Point"));
        customer.setAddress(resultSet.getString("Address"));
        customer.setCreatedAt(resultSet.getObject("CreatedAt", LocalDateTime.class));
        customer.setUpdatedAt(resultSet.getObject("UpdatedAt", LocalDateTime.class));

        return customer;
    }

    public Optional<Customer> findByPhone(String phoneNumber) {
        String sql = "SELECT CustomerID, FullName, PhoneNumber, Email, Address, CreatedAt, Point " +
                "FROM Customers WHERE PhoneNumber = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, phoneNumber);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getString("CustomerID"));
                    customer.setFullName(rs.getString("FullName"));
                    customer.setPhoneNumber(rs.getString("PhoneNumber"));
                    customer.setEmail(rs.getString("Email"));
                    customer.setAddress(rs.getString("Address"));

                    if (rs.getTimestamp("CreatedAt") != null) {
                        customer.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                    }

                    customer.setPoints(rs.getInt("Point"));

                    return Optional.of(customer);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding customer by phone: " + phoneNumber, e);
        }

        return Optional.empty();
    }

    /**
     * Tìm khách hàng theo ID cho mục đích hiển thị thẻ bảo hành
     * Đảm bảo trả về đầy đủ thông tin bao gồm địa chỉ
     *
     * @param customerId ID của khách hàng
     * @return Optional<Customer> với đầy đủ thông tin nếu tìm thấy
     */
    public Optional<Customer> findByIdWarranty(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            logger.warning("CustomerID is null or empty");
            return Optional.empty();
        }

        String sql = "SELECT CustomerID, FullName, PhoneNumber, Email, Address, CreatedAt, Point " +
                "FROM Customers WHERE CustomerID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getString("CustomerID"));
                    customer.setFullName(rs.getString("FullName"));
                    customer.setPhoneNumber(rs.getString("PhoneNumber"));
                    customer.setEmail(rs.getString("Email"));

                    // Đảm bảo đọc địa chỉ và xử lý null
                    String address = rs.getString("Address");
                    customer.setAddress(address != null ? address : "");

                    // Log địa chỉ để debug
                    logger.info("Customer found with ID: " + customerId + ", Address: "
                            + (address != null ? address : "null"));

                    if (rs.getTimestamp("CreatedAt") != null) {
                        customer.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                    }

                    customer.setPoints(rs.getInt("Point"));

                    return Optional.of(customer);
                } else {
                    logger.warning("No customer found with ID: " + customerId);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding customer by ID for warranty: " + customerId, e);
        }

        return Optional.empty();
    }

    /**
     * Lấy dữ liệu doanh thu khách hàng trong khoảng thời gian
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Danh sách dữ liệu doanh thu khách hàng
     */
    public List<Map<String, Object>> getCustomerRevenueData(LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> result = new ArrayList<>();

        String sql = "SELECT " +
                "    c.CustomerID, " +
                "    c.FullName, " +
                "    COUNT(DISTINCT i.InvoiceID) AS OrderCount, " +
                "    COALESCE(SUM(i.TotalAmount), 0) AS TotalAmount, " +
                "    COALESCE(SUM(i.DiscountAmount), 0) AS DiscountAmount, " +
                "    COALESCE(SUM(i.TotalAmount - ISNULL(i.DiscountAmount, 0)), 0) AS Revenue, " +
                "    COALESCE(return_data.ReturnCount, 0) AS ReturnCount, " +
                "    COALESCE(return_data.ReturnValue, 0) AS ReturnValue, " +
                "    COALESCE(SUM(i.TotalAmount - ISNULL(i.DiscountAmount, 0)), 0) - COALESCE(return_data.ReturnValue, 0) AS NetRevenue "
                +
                "FROM Customers c " +
                "    LEFT JOIN Invoices i ON c.CustomerID = i.CustomerID " +
                "        AND i.InvoiceDate BETWEEN ? AND ? " +
                "        AND i.StatusID = 3 " +
                "    LEFT JOIN (  " +
                "        SELECT " +
                "            i2.CustomerID, " +
                "            COUNT(DISTINCT r.ReturnID) AS ReturnCount, " +
                "            COALESCE(SUM(r.ReturnAmount), 0) AS ReturnValue " +
                "        FROM Returns r " +
                "            JOIN InvoiceDetails id ON r.InvoiceDetailID = id.InvoiceDetailID " +
                "            JOIN Invoices i2 ON id.InvoiceID = i2.InvoiceID " +
                "        WHERE r.ReturnDate BETWEEN ? AND ? " +
                "            AND r.Status = 'Completed' " +
                "        GROUP BY i2.CustomerID  " +
                "    ) return_data ON c.CustomerID = return_data.CustomerID " +
                "WHERE i.InvoiceID IS NOT NULL " +
                "GROUP BY c.CustomerID, c.FullName, return_data.ReturnCount, return_data.ReturnValue " +
                "ORDER BY NetRevenue DESC;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(startDate));
            statement.setTimestamp(2, Timestamp.valueOf(endDate));
            statement.setTimestamp(3, Timestamp.valueOf(startDate));
            statement.setTimestamp(4, Timestamp.valueOf(endDate));

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> customerData = new HashMap<>();
                    customerData.put("customerId", rs.getString("CustomerID"));
                    customerData.put("customerName", rs.getString("FullName"));
                    customerData.put("orderCount", rs.getInt("OrderCount"));
                    customerData.put("totalAmount", rs.getBigDecimal("TotalAmount"));
                    customerData.put("discountAmount", rs.getBigDecimal("DiscountAmount"));
                    customerData.put("revenue", rs.getBigDecimal("Revenue"));
                    customerData.put("returnCount", rs.getInt("ReturnCount"));
                    customerData.put("returnValue", rs.getBigDecimal("ReturnValue"));
                    customerData.put("netRevenue", rs.getBigDecimal("NetRevenue"));

                    result.add(customerData);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy dữ liệu doanh thu khách hàng", e);
            e.printStackTrace();
        }

        return result;
    }
}