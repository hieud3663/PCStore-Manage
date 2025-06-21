package com.pcstore.repository.impl;

import com.pcstore.repository.Repository;
import com.pcstore.model.Category;
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
 * Repository implementation cho Category entity
 */
public class CategoryRepository implements Repository<Category, String> {
    private Connection connection;
    
    public CategoryRepository(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public Category add(Category category) {
        String sql = "INSERT INTO Categories (CategoryName) VALUES (?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, category.getCategoryName());
            
            statement.executeUpdate();
            
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                String generatedId = generatedKeys.getString(1);
                category.setCategoryId(generatedId);
            }
            
            LocalDateTime now = LocalDateTime.now();
            category.setCreatedAt(now);
            category.setUpdatedAt(now);
            
            return category;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding category", e);
        }
    }
    
    @Override
    public Category update(Category category) {
        String sql = "UPDATE Categories SET CategoryName = ? WHERE CategoryID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, category.getCategoryName());
            statement.setString(2, category.getCategoryId());
            
            statement.executeUpdate();
            
            category.setUpdatedAt(LocalDateTime.now());
            return category;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating category", e);
        }
    }
    
    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM Categories WHERE CategoryID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting category", e);
        }
    }
    
    @Override
    public Optional<Category> findById(String id) {
        String sql = "SELECT * FROM Categories WHERE CategoryID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToCategory(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding category by ID", e);
        }
    }
    
    @Override
    public List<Category> findAll() {
        String sql = "SELECT * FROM Categories";
        List<Category> categories = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                categories.add(mapResultSetToCategory(resultSet));
            }
            return categories;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all categories", e);
        }
    }
    
    @Override
    public boolean exists(String id) {
        String sql = "SELECT COUNT(*) FROM Categories WHERE CategoryID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if category exists", e);
        }
    }
    
    // Tìm danh mục theo tên
    public Optional<Category> findByName(String categoryName) {
        String sql = "SELECT * FROM Categories WHERE CategoryName = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoryName);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToCategory(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding category by name", e);
        }
    }
    
    // Tìm tất cả sản phẩm thuộc danh mục này
    public List<Integer> getProductCountByCategory() {
        String sql = "SELECT c.CategoryID, COUNT(p.ProductID) as ProductCount " +
                     "FROM Categories c " +
                     "LEFT JOIN Products p ON c.CategoryID = p.CategoryID " +
                     "GROUP BY c.CategoryID";
        List<Integer> productCounts = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                productCounts.add(resultSet.getInt("ProductCount"));
            }
            return productCounts;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting product count by category", e);
        }
    }

    //search by keyword
    public List<Category> searchByKeyword(String keyword) {
        String sql = "SELECT * FROM Categories WHERE CategoryName LIKE ?";
        List<Category> categories = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + keyword + "%");
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                categories.add(mapResultSetToCategory(resultSet));
            }
            return categories;
        } catch (SQLException e) {
            throw new RuntimeException("Error searching categories by keyword", e);
        }
    }
    
    private Category mapResultSetToCategory(ResultSet resultSet) throws SQLException {
        Category category = new Category();
        category.setCategoryId(resultSet.getString("CategoryID"));
        category.setCategoryName(resultSet.getString("CategoryName"));
        
        // Products sẽ được load khi cần thông qua ProductRepository
        
        return category;
    }
}