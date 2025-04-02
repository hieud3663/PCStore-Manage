package com.pcstore.Repository.impl;

import com.pcstore.Repository.Repository;
import com.pcstore.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

/**
 * Repository cụ thể cho Product, thực hiện các thao tác CRUD với bảng Product
 */
public class ProductRepository implements Repository<Product, String> {
    private Connection connection;
    
    public ProductRepository(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public Product add(Product product) {
        String sql = "INSERT INTO Products (ProductID, ProductName, Description, UnitPrice, " +
                     "QuantityInStock, CategoryID, SupplierID, WarrantyPeriod) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getProductId());
            statement.setString(2, product.getProductName());
            statement.setBigDecimal(3, product.getPrice());
            statement.setInt(4, product.getStockQuantity());
            statement.setString(5, product.getSpecifications());
            statement.setString(6, product.getDescription());
            statement.setInt(7, product.getCategory() != null ? 
                    (Integer) product.getCategory().getId() : null);
            statement.setString(8, product.getSupplier() != null ? 
                    (String) product.getSupplier().getId() : null);
            
            LocalDateTime now = LocalDateTime.now();
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
            
            statement.setObject(9, product.getCreatedAt());
            statement.setObject(10, product.getUpdatedAt());
            
            statement.executeUpdate();
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding product", e);
        }
    }
    
    @Override
    public Product update(Product product) {
        String sql = "UPDATE Products SET ProductName = ?, Price = ?, StockQuantity = ?, " +
                "Specifications = ?, Description = ?, CategoryID = ?, SupplierID = ?, UpdatedAt = ? " +
                "WHERE ProductID = ?";
                
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getProductName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getStockQuantity());
            statement.setString(4, product.getSpecifications());
            statement.setString(5, product.getDescription());
            statement.setInt(6, product.getCategory() != null ? 
                    (Integer) product.getCategory().getId() : null);
            statement.setString(7, product.getSupplier() != null ? 
                    (String) product.getSupplier().getId() : null);
            
            product.setUpdatedAt(LocalDateTime.now());
            statement.setObject(8, product.getUpdatedAt());
            statement.setString(9, product.getProductId());
            
            statement.executeUpdate();
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product", e);
        }
    }
    
    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM Products WHERE ProductID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product", e);
        }
    }
    
    @Override
    public Optional<Product> findById(String id) {
        String sql = "SELECT * FROM Products WHERE ProductID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToProduct(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding product by ID", e);
        }
    }
    
    @Override
    public List<Product> findAll() {
        String sql = "SELECT * FROM Products";
        List<Product> products = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                products.add(mapResultSetToProduct(resultSet));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all products", e);
        }
    }
    
    @Override
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM Products WHERE ProductID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if product exists", e);
        }
    }
    
    // Phương thức tìm sản phẩm theo danh mục
    public List<Product> findByCategory(Integer categoryId) {
        String sql = "SELECT * FROM Products WHERE CategoryID = ?";
        List<Product> products = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, categoryId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                products.add(mapResultSetToProduct(resultSet));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by category", e);
        }
    }
    
    // Phương thức tìm sản phẩm theo nhà cung cấp
    public List<Product> findBySupplier(String supplierId) {
        String sql = "SELECT * FROM Products WHERE SupplierID = ?";
        List<Product> products = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplierId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                products.add(mapResultSetToProduct(resultSet));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by supplier", e);
        }
    }
    
    // Phương thức tìm sản phẩm có số lượng tồn kho thấp hơn ngưỡng
    public List<Product> findLowStock(int threshold) {
        String sql = "SELECT * FROM Products WHERE StockQuantity < ?";
        List<Product> products = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, threshold);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                products.add(mapResultSetToProduct(resultSet));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding low stock products", e);
        }
    }
    
    // Phương thức chuyển ResultSet thành đối tượng Product
    private Product mapResultSetToProduct(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setProductId(resultSet.getString("ProductID"));
        product.setProductName(resultSet.getString("ProductName"));
        product.setPrice(resultSet.getBigDecimal("Price"));
        product.setStockQuantity(resultSet.getInt("StockQuantity"));
        product.setSpecifications(resultSet.getString("Specifications"));
        product.setDescription(resultSet.getString("Description"));
        product.setCreatedAt(resultSet.getObject("CreatedAt", LocalDateTime.class));
        product.setUpdatedAt(resultSet.getObject("UpdatedAt", LocalDateTime.class));
        
        // Lưu ý: Category và Supplier sẽ được load riêng hoặc lazy load
        
        return product;
    }
}