package com.pcstore.repository.impl;

import com.pcstore.repository.Repository;
import com.pcstore.model.Category;

import java.sql.*;
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
        String sql = "INSERT INTO Categories (CategoryID, CategoryName, Description, Status, CreatedAt) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, category.getCategoryId());
            statement.setString(2, category.getCategoryName());
            statement.setString(3, category.getDescription());
            statement.setString(4, category.getStatus()); // Thêm dòng này
            java.sql.Timestamp now = java.sql.Timestamp.valueOf(java.time.LocalDateTime.now());
            statement.setTimestamp(5, now);


            int rows = statement.executeUpdate();
            if (rows > 0) {
                category.setCreatedAt(now.toLocalDateTime());
                category.setUpdatedAt(now.toLocalDateTime());
                return category;
            } else {
                throw new RuntimeException("Error adding category");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding category", e);
        }
    }
    
    @Override
    public Category update(Category category) {
        String sql = "UPDATE Categories SET CategoryName = ?, Description = ?, Status = ? WHERE CategoryID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, category.getCategoryName());
            statement.setString(2, category.getDescription());
            statement.setString(3, category.getStatus()); // Thêm dòng này
            statement.setString(4, category.getCategoryId());
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
    
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getString("CategoryID"));
        category.setCategoryName(rs.getString("CategoryName"));
        category.setDescription(rs.getString("Description"));
        category.setStatus(rs.getString("Status")); // Thêm dòng này
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            category.setCreatedAt(createdAt.toLocalDateTime());
        }
        return category;
    }
}