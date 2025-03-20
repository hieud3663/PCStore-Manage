package com.pcstore.dao.impl;

import com.pcstore.dao.DAO;
import com.pcstore.dao.DAOFactory;
import com.pcstore.model.Warranty;
import com.pcstore.model.Product;
import com.pcstore.model.Customer;
import com.pcstore.model.RepairService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO implementation cho Warranty entity
 */
public class WarrantyDAO implements DAO<Warranty, Integer> {
    private Connection connection;
    private DAOFactory daoFactory;
    
    public WarrantyDAO(Connection connection, DAOFactory daoFactory) {
        this.connection = connection;
        this.daoFactory = daoFactory;
    }
    
    @Override
    public Warranty add(Warranty warranty) {
        String sql = "INSERT INTO Warranties (ProductID, CustomerID, StartDate, EndDate, WarrantyTerms) " +
                     "VALUES (?, ?, ?, ?, ?)";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, warranty.getProduct() != null ? warranty.getProduct().getProductId() : null);
            statement.setString(2, warranty.getCustomer() != null ? warranty.getCustomer().getCustomerId() : null);
            statement.setObject(3, warranty.getStartDate());
            statement.setObject(4, warranty.getEndDate());
            statement.setString(5, warranty.getWarrantyTerms());
            
            statement.executeUpdate();
            
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                warranty.setWarrantyId(generatedId);
            }
            
            LocalDateTime now = LocalDateTime.now();
            warranty.setCreatedAt(now);
            warranty.setUpdatedAt(now);
            
            return warranty;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding warranty", e);
        }
    }
    
    @Override
    public Warranty update(Warranty warranty) {
        String sql = "UPDATE Warranties SET ProductID = ?, CustomerID = ?, StartDate = ?, " +
                     "EndDate = ?, WarrantyTerms = ? WHERE WarrantyID = ?";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, warranty.getProduct() != null ? warranty.getProduct().getProductId() : null);
            statement.setString(2, warranty.getCustomer() != null ? warranty.getCustomer().getCustomerId() : null);
            statement.setObject(3, warranty.getStartDate());
            statement.setObject(4, warranty.getEndDate());
            statement.setString(5, warranty.getWarrantyTerms());
            statement.setInt(6, warranty.getWarrantyId());
            
            statement.executeUpdate();
            
            warranty.setUpdatedAt(LocalDateTime.now());
            return warranty;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating warranty", e);
        }
    }
    
    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Warranties WHERE WarrantyID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting warranty", e);
        }
    }
    
    @Override
    public Optional<Warranty> findById(Integer id) {
        String sql = "SELECT w.*, p.ProductName, c.FullName as CustomerName " +
                     "FROM Warranties w " +
                     "LEFT JOIN Products p ON w.ProductID = p.ProductID " +
                     "LEFT JOIN Customers c ON w.CustomerID = c.CustomerID " +
                     "WHERE w.WarrantyID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToWarranty(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding warranty by ID", e);
        }
    }
    
    @Override
    public List<Warranty> findAll() {
        String sql = "SELECT w.*, p.ProductName, c.FullName as CustomerName " +
                     "FROM Warranties w " +
                     "LEFT JOIN Products p ON w.ProductID = p.ProductID " +
                     "LEFT JOIN Customers c ON w.CustomerID = c.CustomerID";
        List<Warranty> warranties = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                warranties.add(mapResultSetToWarranty(resultSet));
            }
            return warranties;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all warranties", e);
        }
    }
    
    @Override
    public boolean exists(Integer id) {
        String sql = "SELECT COUNT(*) FROM Warranties WHERE WarrantyID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if warranty exists", e);
        }
    }
    
    // Tìm bảo hành theo khách hàng
    public List<Warranty> findByCustomerId(String customerId) {
        String sql = "SELECT w.*, p.ProductName, c.FullName as CustomerName " +
                     "FROM Warranties w " +
                     "LEFT JOIN Products p ON w.ProductID = p.ProductID " +
                     "LEFT JOIN Customers c ON w.CustomerID = c.CustomerID " +
                     "WHERE w.CustomerID = ?";
        List<Warranty> warranties = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                warranties.add(mapResultSetToWarranty(resultSet));
            }
            return warranties;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding warranties by customer", e);
        }
    }
    
    // Tìm bảo hành theo sản phẩm
    public List<Warranty> findByProductId(String productId) {
        String sql = "SELECT w.*, p.ProductName, c.FullName as CustomerName " +
                     "FROM Warranties w " +
                     "LEFT JOIN Products p ON w.ProductID = p.ProductID " +
                     "LEFT JOIN Customers c ON w.CustomerID = c.CustomerID " +
                     "WHERE w.ProductID = ?";
        List<Warranty> warranties = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                warranties.add(mapResultSetToWarranty(resultSet));
            }
            return warranties;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding warranties by product", e);
        }
    }
    
    // Tìm bảo hành còn hiệu lực
    public List<Warranty> findActiveWarranties() {
        String sql = "SELECT w.*, p.ProductName, c.FullName as CustomerName " +
                     "FROM Warranties w " +
                     "LEFT JOIN Products p ON w.ProductID = p.ProductID " +
                     "LEFT JOIN Customers c ON w.CustomerID = c.CustomerID " +
                     "WHERE w.EndDate >= ?";
        List<Warranty> warranties = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, LocalDate.now());
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                warranties.add(mapResultSetToWarranty(resultSet));
            }
            return warranties;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active warranties", e);
        }
    }
    
    // Tìm bảo hành hết hạn
    public List<Warranty> findExpiredWarranties() {
        String sql = "SELECT w.*, p.ProductName, c.FullName as CustomerName " +
                     "FROM Warranties w " +
                     "LEFT JOIN Products p ON w.ProductID = p.ProductID " +
                     "LEFT JOIN Customers c ON w.CustomerID = c.CustomerID " +
                     "WHERE w.EndDate < ?";
        List<Warranty> warranties = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, LocalDate.now());
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                warranties.add(mapResultSetToWarranty(resultSet));
            }
            return warranties;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding expired warranties", e);
        }
    }
    
    // Tìm bảo hành sắp hết hạn
    public List<Warranty> findWarrantiesAboutToExpire(int daysThreshold) {
        String sql = "SELECT w.*, p.ProductName, c.FullName as CustomerName " +
                     "FROM Warranties w " +
                     "LEFT JOIN Products p ON w.ProductID = p.ProductID " +
                     "LEFT JOIN Customers c ON w.CustomerID = c.CustomerID " +
                     "WHERE w.EndDate BETWEEN ? AND ?";
        List<Warranty> warranties = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            LocalDate today = LocalDate.now();
            statement.setObject(1, today);
            statement.setObject(2, today.plusDays(daysThreshold));
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                warranties.add(mapResultSetToWarranty(resultSet));
            }
            return warranties;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding warranties about to expire", e);
        }
    }
    
    private Warranty mapResultSetToWarranty(ResultSet resultSet) throws SQLException {
        Warranty warranty = new Warranty();
        warranty.setWarrantyId(resultSet.getInt("WarrantyID"));
        warranty.setStartDate(resultSet.getObject("StartDate", LocalDate.class));
        warranty.setEndDate(resultSet.getObject("EndDate", LocalDate.class));
        warranty.setWarrantyTerms(resultSet.getString("WarrantyTerms"));
        
        // Tạo đối tượng Product với thông tin cơ bản
        Product product = new Product();
        product.setProductId(resultSet.getString("ProductID"));
        product.setProductName(resultSet.getString("ProductName"));
        warranty.setProduct(product);
        
        // Tạo đối tượng Customer với thông tin cơ bản
        Customer customer = new Customer();
        customer.setCustomerId(resultSet.getString("CustomerID"));
        customer.setFullName(resultSet.getString("CustomerName"));
        warranty.setCustomer(customer);
        
        // Các thông tin liên quan khác như RepairService sẽ được load khi cần
        
        return warranty;
    }
}