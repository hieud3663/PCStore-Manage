package com.pcstore.repository.impl;

import com.pcstore.repository.Repository;
import com.pcstore.model.Customer;
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
 * Repository implementation cho Customer entity
 */
public class CustomerRepository implements Repository<Customer, String> {
    private Connection connection;
    
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
        

        String sql = "INSERT INTO Customers (CustomerID, FullName, PhoneNumber, Email, Address, Point, CreatedAt, UpdatedAt) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
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

            customer.setCustomerId(generateCustomerId());

            statement.executeUpdate();
            
             // Tạo mã khách hàng tự động
            return customer;
        } catch (SQLException e) {
            customer.setCustomerId(null); // Đặt lại mã khách hàng nếu có lỗi
            throw new RuntimeException("Error adding customer", e);
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
        String sql = "SELECT COUNT(*) FROM Customers WHERE CustomerID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
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
        
        // Không cần phải load invoices và repairServices ở đây
        // Chúng sẽ được load khi cần thông qua InvoiceRepository và RepairServiceRepository
        
        return customer;
    }
}