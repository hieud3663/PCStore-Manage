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
    // Đảm bảo truy vấn chọn rõ cột Specifications và Description
    String sql = "SELECT p.ProductID, p.ProductName, p.CategoryID, p.StockQuantity, " +
                 "p.Price, p.Specifications, p.Description, " +
                 "c.CategoryName FROM Products p " +
                 "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                 "WHERE p.ProductID = ?";
    
    System.out.println("Executing SQL: " + sql + " with ID: " + id); // Debug log
    
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, id);
        
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                // Debug log
                try {
                    String specs = resultSet.getString("Specifications");
                    System.out.println("DB value for Specifications: " + (specs == null ? "NULL" : "'" + specs + "'"));
                } catch (Exception e) {
                    System.err.println("Error reading Specifications: " + e.getMessage());
                }
                
                Product product = mapResultSetToProduct(resultSet);
                return Optional.of(product);
            }
            return Optional.empty();
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return Optional.empty();
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
 * Tìm sản phẩm theo tên hoặc mã chứa từ khóa
 * @param keyword Từ khóa tìm kiếm
 * @return Danh sách sản phẩm phù hợp
 * @throws SQLException Nếu có lỗi truy vấn SQL
 */
public List<Product> findByNameOrIdContaining(String keyword) throws SQLException {
    List<Product> products = new ArrayList<>();
    String sql = "SELECT p.*, s.supplier_id, s.name as supplier_name, s.email, s.phone, s.address " +
                 "FROM products p " +
                 "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                 "WHERE p.product_id LIKE ? OR p.product_name LIKE ?";
    
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        String searchPattern = "%" + keyword + "%";
        stmt.setString(1, searchPattern);
        stmt.setString(2, searchPattern);
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getString("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setStockQuantity(rs.getInt("stock_quantity"));
                // Xử lý thông tin nhà cung cấp
                String supplierId = rs.getString("supplier_id");
                if (supplierId != null) {
                    Supplier supplier = new Supplier();
                    supplier.setSupplierId(supplierId);
                    supplier.setName(rs.getString("supplier_name"));
                    supplier.setEmail(rs.getString("email"));
                    supplier.setAddress(rs.getString("address"));
                    product.setSupplier(supplier);
                }
                
                products.add(product);
            }
        }
    }
    
    return products;
}


    /**
     * Tìm tất cả sản phẩm trong cơ sở dữ liệu
     * @return danh sách sản phẩm
     */   
    @Override
public List<Product> findAll() {
    // Sửa truy vấn - kiểm tra tên cột thực tế trong DB
    String sql = "SELECT p.*, c.CategoryName "
            + "FROM Products p "
            + "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID";
    
    // Hoặc nếu bảng Suppliers có cột tên khác:
    // String sql = "SELECT p.*, c.CategoryName, s.CompanyName as SupplierName "
    // + "FROM Products p "
    // + "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID "
    // + "LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID";
    
    List<Product> products = new ArrayList<>();
    
    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(sql)) {
        
        while (resultSet.next()) {
            try {
                Product product = mapResultSetToProduct(resultSet);
                products.add(product);
            } catch (Exception e) {
                System.err.println("Lỗi khi xử lý sản phẩm từ ResultSet: " + e.getMessage());
            }
        }
    } catch (SQLException e) {
        System.err.println("SQL Exception trong findAll: " + e.getMessage());
        e.printStackTrace();
    }
    
    return products;
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

    // Phương thức để tăng/giảm số lượng tồn kho
    public boolean adjustStockQuantity(String productId, int adjustment) {
        String sql = "UPDATE Products SET StockQuantity = StockQuantity + ? WHERE ProductID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, adjustment); // Giá trị dương để tăng, âm để giảm
            statement.setString(2, productId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error adjusting stock quantity", e);
        }
    }
    
    // Phương thức chuyển ResultSet thành đối tượng Product
    private Product mapResultSetToProduct(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        try{
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
                    product.setSpecifications("");
                    System.err.println("Error reading Specifications: " + e.getMessage());
                }
                
                try {
                    String desc = resultSet.getString("Description");
                    product.setDescription(desc);
                } catch (SQLException e) {
                    product.setDescription("");
                }
                
                // Xử lý Category
                categoryId = resultSet.getString("CategoryID");
                if (categoryId != null) {
                    Category category = new Category();
                    category.setCategoryId(categoryId);
                    
                    try {
                        category.setCategoryName(resultSet.getString("CategoryName"));
                    } catch (SQLException e) {
                        category.setCategoryName("Unknown");
                    }
                    
                    product.setCategory(category);
                }
                
                // QUAN TRỌNG: KHÔNG đọc Supplier
            }
        } catch (SQLException e) {
            throw new SQLException("Error mapping product from ResultSet", e);
        }
        
        return product;
    }
}