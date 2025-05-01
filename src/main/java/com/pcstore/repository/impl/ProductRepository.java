package com.pcstore.repository.impl;

import com.pcstore.repository.Repository;
import com.pcstore.model.Category;
import com.pcstore.model.Product;
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
        String sql = "INSERT INTO Products (ProductID, ProductName, CategoryID, SupplierID, Price, " +
                     "StockQuantity, Specifications, Description, Manufacturer) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getProductId());
            statement.setString(2, product.getProductName());
            statement.setString(3, product.getCategory() != null ? product.getCategory().getCategoryId() : null);
            statement.setString(4, product.getSupplier() != null ? 
                    (String) product.getSupplier().getId() : null);
            statement.setBigDecimal(5, product.getPrice());
            statement.setInt(6, product.getStockQuantity());
            statement.setString(7, product.getSpecifications());
            statement.setString(8, product.getDescription());
            
            statement.setString(9, product.getManufacturer());
            
            LocalDateTime now = LocalDateTime.now();
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
            
            statement.executeUpdate();
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding product", e);
        }
    }
    
    @Override
    public Product update(Product product) {
        String sql = "UPDATE Products SET ProductName = ?, Price = ?, StockQuantity = ?, " +
                "Specifications = ?, Description = ?, CategoryID = ?, SupplierID = ?, " +
                "UpdatedAt = ?, Manufacturer = ? " +
                "WHERE ProductID = ?";
                
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getProductName());
            statement.setBigDecimal(2, product.getPrice());
            statement.setInt(3, product.getStockQuantity());
            statement.setString(4, product.getSpecifications());
            statement.setString(5, product.getDescription());
            statement.setString(6, product.getCategory() != null ? product.getCategory().getCategoryId() : null);
            statement.setString(7, product.getSupplier() != null ? 
                    (String) product.getSupplier().getId() : null);
            
            product.setUpdatedAt(LocalDateTime.now());
            statement.setObject(8, product.getUpdatedAt());
            statement.setString(9, product.getManufacturer());
            statement.setString(10, product.getProductId());
            
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


    /**
     * Tìm sản phẩm theo tên
     * @param name tên sản phẩm
     * @return danh sách sản phẩm tìm thấy
     */
    // @Override
    public List<Product> findByName(String name) {
        String sql = "SELECT * FROM Products WHERE ProductName LIKE ?";
        List<Product> products = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + name + "%");
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                products.add(mapResultSetToProduct(resultSet));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding product by name", e);
        }
    }


    /**
     * Tìm tất cả sản phẩm trong cơ sở dữ liệu
     * @return danh sách sản phẩm
     */   
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

    //TÌm kiếm theo giá trị Id hoặc tên sản phẩm
    public List<Product> findByIdOrName(String idOrName) {
        String sql = "SELECT * FROM Products WHERE ProductID LIKE ? OR ProductName LIKE ?";
        List<Product> products = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + idOrName + "%");
            statement.setString(2, "%" + idOrName + "%");
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                products.add(mapResultSetToProduct(resultSet));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding product by ID or name", e);
        }
    }

    //Tìm kiếm theo giá trị Id, tên sản phẩm hoặc hãng sản xuất
    public List<Product> findByIdOrNameOrManufacturer(String keyword) {
        String sql = "SELECT * FROM Products WHERE ProductID LIKE ? OR ProductName LIKE ? OR Manufacturer LIKE ?";
        List<Product> products = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + keyword + "%");
            statement.setString(2, "%" + keyword + "%");
            statement.setString(3, "%" + keyword + "%");
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                products.add(mapResultSetToProduct(resultSet));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding product by keyword", e);
        }
    }

    // Cập nhật số lượng tồn kho của sản phẩm
    public boolean updateStockQuantity(String productId, int quantity) {
        String sql = "UPDATE Products SET StockQuantity = ? WHERE ProductID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setString(2, productId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating stock quantity", e);
        }
    }
    
    /**
     * Tìm sản phẩm theo hãng sản xuất
     * @param manufacturer tên hãng sản xuất
     * @return danh sách sản phẩm từ hãng sản xuất
     */
    public List<Product> findByManufacturer(String manufacturer) {
        String sql = "SELECT * FROM Products WHERE Manufacturer LIKE ?";
        List<Product> products = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + manufacturer + "%");
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                products.add(mapResultSetToProduct(resultSet));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by manufacturer", e);
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
        
        // Đọc trường Manufacturer
        product.setManufacturer(resultSet.getString("Manufacturer"));

        String categoryId = resultSet.getString("CategoryID");
        String supplierId = resultSet.getString("SupplierID");

        if (!categoryId.isEmpty()) {

            String sqlCategory = "SELECT * FROM Categories WHERE CategoryID = ?";
            try (PreparedStatement statement = connection.prepareStatement(sqlCategory)) {
                statement.setString(1, categoryId);
                ResultSet rsCategory = statement.executeQuery();
                if (rsCategory.next()) {
                    product.setCategory(new Category(categoryId, rsCategory.getString("CategoryName"))); // Chỉ cần ID và tên
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error finding category by ID", e);
            }
        } else {
            product.setCategory(null); // Hoặc có thể ném ngoại lệ nếu cần
        }

        if (!supplierId.isEmpty()) {
            String sqlSupplier = "SELECT * FROM Suppliers WHERE SupplierID = ?";
            try (PreparedStatement statement = connection.prepareStatement(sqlSupplier)) {
                statement.setString(1, supplierId);
                ResultSet rsSupplier = statement.executeQuery();
                if (rsSupplier.next()) {
                    product.setSupplier(new Supplier(supplierId, rsSupplier.getString("Name"), rsSupplier.getString("PhoneNumber"), rsSupplier.getString("Email"), rsSupplier.getString("Address"))); // Chỉ cần ID và tên
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error finding supplier by ID", e);
            }
        } else {
            product.setSupplier(null); // Hoặc có thể ném ngoại lệ nếu cần
        }
        
        // Lưu ý: Category và Supplier sẽ được load riêng hoặc lazy loaded;
        return product;
    }
}