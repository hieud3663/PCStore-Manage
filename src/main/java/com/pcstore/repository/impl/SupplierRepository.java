package com.pcstore.Repository.impl;

import com.pcstore.Repository.Repository;
import com.pcstore.model.Supplier;
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
 * Repository implementation cho Supplier entity
 */
public class SupplierRepository implements Repository<Supplier, String> {
    private Connection connection;
    
    public SupplierRepository(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public Supplier add(Supplier supplier) {
        String sql = "INSERT INTO Suppliers (SupplierID, Name, PhoneNumber, Email, Address) " +
                     "VALUES (?, ?, ?, ?, ?)";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplier.getSupplierId());
            statement.setString(2, supplier.getName());
            statement.setString(3, supplier.getPhoneNumber());
            statement.setString(4, supplier.getEmail());
            statement.setString(5, supplier.getAddress());
            
            statement.executeUpdate();
            
            LocalDateTime now = LocalDateTime.now();
            supplier.setCreatedAt(now);
            supplier.setUpdatedAt(now);
            
            return supplier;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding supplier", e);
        }
    }
    
    @Override
    public Supplier update(Supplier supplier) {
        String sql = "UPDATE Suppliers SET Name = ?, PhoneNumber = ?, Email = ?, Address = ? " +
                    "WHERE SupplierID = ?";
                    
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getPhoneNumber());
            statement.setString(3, supplier.getEmail());
            statement.setString(4, supplier.getAddress());
            statement.setString(5, supplier.getSupplierId());
            
            statement.executeUpdate();
            
            supplier.setUpdatedAt(LocalDateTime.now());
            return supplier;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating supplier", e);
        }
    }
    
    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM Suppliers WHERE SupplierID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting supplier", e);
        }
    }
    
    @Override
    public Optional<Supplier> findById(String id) {
        String sql = "SELECT * FROM Suppliers WHERE SupplierID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToSupplier(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding supplier by ID", e);
        }
    }
    
    @Override
    public List<Supplier> findAll() {
        String sql = "SELECT * FROM Suppliers";
        List<Supplier> suppliers = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
             
            while (resultSet.next()) {
                suppliers.add(mapResultSetToSupplier(resultSet));
            }
            return suppliers;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all suppliers", e);
        }
    }
    
    @Override
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM Suppliers WHERE SupplierID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if supplier exists", e);
        }
    }
    
    // Tìm nhà cung cấp theo tên
    public List<Supplier> findByName(String name) {
        String sql = "SELECT * FROM Suppliers WHERE Name LIKE ?";
        List<Supplier> suppliers = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + name + "%");
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                suppliers.add(mapResultSetToSupplier(resultSet));
            }
            return suppliers;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding suppliers by name", e);
        }
    }
    
    // Tìm nhà cung cấp theo email
    public Optional<Supplier> findByEmail(String email) {
        String sql = "SELECT * FROM Suppliers WHERE Email = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToSupplier(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding supplier by email", e);
        }
    }
    
    // Tìm nhà cung cấp theo số điện thoại
    public Optional<Supplier> findByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM Suppliers WHERE PhoneNumber = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, phoneNumber);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToSupplier(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding supplier by phone number", e);
        }
    }
    
    // Tạo mã nhà cung cấp tự động
    public String generateSupplierId() {
        String sql = "SELECT MAX(CAST(SUBSTRING(SupplierID, 4, LEN(SupplierID)) AS INT)) FROM Suppliers WHERE SupplierID LIKE 'NCC%'";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
             
            int maxId = 0;
            if (resultSet.next() && resultSet.getObject(1) != null) {
                maxId = resultSet.getInt(1);
            }
            
            // Tạo ID mới dạng NCC01, NCC02, ...
            return String.format("NCC%02d", maxId + 1);
        } catch (SQLException e) {
            throw new RuntimeException("Error generating supplier ID", e);
        }
    }
    
    // Lấy số lượng sản phẩm của mỗi nhà cung cấp
    public int getProductCountBySupplier(String supplierId) {
        String sql = "SELECT COUNT(*) FROM Products WHERE SupplierID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplierId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting product count by supplier", e);
        }
    }
    
    private Supplier mapResultSetToSupplier(ResultSet resultSet) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(resultSet.getString("SupplierID"));
        supplier.setName(resultSet.getString("Name"));
        supplier.setPhoneNumber(resultSet.getString("PhoneNumber"));
        supplier.setEmail(resultSet.getString("Email"));
        supplier.setAddress(resultSet.getString("Address"));
        
        // Products và PurchaseOrders sẽ được load khi cần thiết thông qua ProductRepository và PurchaseOrderRepository
        
        return supplier;
    }
}