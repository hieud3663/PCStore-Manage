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
import java.sql.Timestamp;
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
                     "StockQuantity, Specifications, Description, Manufacturer, CostPrice, AverageCostPrice, ProfitMargin, CreatedAt, UpdatedAt) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                     
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
            statement.setBigDecimal(10, product.getCostPrice());
            statement.setBigDecimal(11, product.getAverageCostPrice());
            statement.setBigDecimal(12, product.getProfitMargin());
            
            LocalDateTime now = LocalDateTime.now();
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
            
            statement.setObject(13, now);
            statement.setObject(14, now);
            
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
                "UpdatedAt = ?, Manufacturer = ?, CostPrice = ?, AverageCostPrice = ?, ProfitMargin = ? " +
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
            statement.setBigDecimal(10, product.getCostPrice());
            statement.setBigDecimal(11, product.getAverageCostPrice());
            statement.setBigDecimal(12, product.getProfitMargin());
            statement.setString(13, product.getProductId());
            
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
            throw new RuntimeException("Error deleting product: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<Product> findById(String id) {
        // Sửa truy vấn để bao gồm tất cả các cột cần thiết
        String sql = "SELECT p.*, c.CategoryName FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "WHERE p.ProductID = ?";
        
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Debug log
                    try {
                        String specs = resultSet.getString("Specifications");
                        // System.out.println("DB value for Specifications: " + (specs == null ? "NULL" : "'" + specs + "'"));
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
    public List<Product> findByName(String name) {
        String sql = "SELECT p.*, c.CategoryName FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "WHERE p.ProductName LIKE ?";
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
        String sql = "SELECT p.*, c.CategoryName, s.SupplierID, s.Name as supplier_name, s.Email, s.Phone, s.Address " +
                    "FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID " +
                    "WHERE p.ProductID LIKE ? OR p.ProductName LIKE ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
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
        // Sửa truy vấn để bao gồm tất cả các cột cần thiết
        String sql = "SELECT p.*, c.CategoryName "
                + "FROM Products p "
                + "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID";
        
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
    public List<Product> findByCategory(String categoryId) {
        String sql = "SELECT p.*, c.CategoryName FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "WHERE p.CategoryID = ?";
        List<Product> products = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoryId);
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
        String sql = "SELECT p.*, c.CategoryName FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "WHERE p.SupplierID = ?";
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
        String sql = "SELECT p.*, c.CategoryName FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "WHERE p.StockQuantity < ?";
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
        String sql = "SELECT p.*, c.CategoryName FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "WHERE p.ProductID LIKE ? OR p.ProductName LIKE ?";
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
        String sql = "SELECT p.*, c.CategoryName FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "WHERE p.ProductID LIKE ? OR p.ProductName LIKE ? OR p.Manufacturer LIKE ?";
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
        String sql = "SELECT p.*, c.CategoryName FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "WHERE p.Manufacturer LIKE ?";
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
    
    /**
     * Tạo mã sản phẩm mới tự động
     * @return ID sản phẩm mới theo định dạng (VD: 00001, 00002, 00003, ...)
     */
    public String generateProductID() {
        String sql = "SELECT ISNULL(MAX(CAST(ProductID AS INT)), 0) + 1 AS NextNumber " +
                     "FROM Products WHERE ISNUMERIC(ProductID) = 1";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int nextNumber = rs.getInt("NextNumber");
                // Format số với độ dài 5 chữ số, thêm số 0 vào đầu nếu cần
                return String.format("%05d", nextNumber);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo mã sản phẩm: " + e.getMessage());
        }
        
        // Trường hợp mặc định nếu có lỗi
        return "00001";
    }

    /**
     * Thêm sản phẩm mới vào CSDL mà không cần chỉ định ID
     * @param product Sản phẩm cần thêm
     * @return Sản phẩm đã được thêm vào CSDL với ID được tạo tự động
     */
    public Product addWithAutoId(Product product) {
        String sql = "INSERT INTO Products (ProductName, CategoryID, SupplierID, Price, " +
                    "StockQuantity, Specifications, Description, Manufacturer, CostPrice, " +
                    "AverageCostPrice, ProfitMargin, CreatedAt, UpdatedAt) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            // Thiết lập các giá trị thời gian
            LocalDateTime now = LocalDateTime.now();
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
            
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set các tham số như cũ
                statement.setString(1, product.getProductName());
                statement.setString(2, product.getCategory() != null ? product.getCategory().getCategoryId() : null);
                statement.setString(3, product.getSupplier() != null ? product.getSupplier().getSupplierId() : null);
                statement.setBigDecimal(4, product.getPrice());
                statement.setInt(5, product.getStockQuantity());
                statement.setString(6, product.getSpecifications());
                statement.setString(7, product.getDescription());
                statement.setString(8, product.getManufacturer());
                statement.setBigDecimal(9, product.getCostPrice());
                statement.setBigDecimal(10, product.getAverageCostPrice());
                statement.setBigDecimal(11, product.getProfitMargin());
                statement.setObject(12, now);
                statement.setObject(13, now);
                
                statement.executeUpdate();
                
                // Lấy sản phẩm mới nhất được thêm vào
                Optional<Product> newestProduct = findNewestProduct();
                if (newestProduct.isPresent()) {
                    product.setProductId(newestProduct.get().getProductId());
                }
                    
                return product;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm sản phẩm với ID tự động: " + e.getMessage(), e);
        }
    }


    /**
     * Tìm sản phẩm mới nhất được thêm vào
     * @return Optional chứa sản phẩm mới nhất
     */
    public Optional<Product> findNewestProduct() {
        String sql = "SELECT TOP 1 p.*, c.CategoryName, s.Name AS SupplierName " +
                    "FROM Products p " +
                    "LEFT JOIN Categories c ON p.CategoryID = c.CategoryID " +
                    "LEFT JOIN Suppliers s ON p.SupplierID = s.SupplierID " +
                    "ORDER BY CAST(p.ProductID AS INT) DESC";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    // Phương thức chuyển ResultSet thành đối tượng Product - đã sửa
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getString("ProductID"));
        product.setProductName(rs.getString("ProductName"));
        product.setPrice(rs.getBigDecimal("Price"));
        product.setStockQuantity(rs.getInt("StockQuantity"));
        
        // Đọc các trường có thể null một cách an toàn
        try {
            String specs = rs.getString("Specifications");
            product.setSpecifications(specs != null ? specs : "");
        } catch (SQLException e) {
            product.setSpecifications("");
        }
        
        try {
            product.setDescription(rs.getString("Description"));
        } catch (SQLException e) {
            product.setDescription("");
        }
        
        // Đọc các trường ngày tháng một cách an toàn
        try {
            product.setCreatedAt(rs.getObject("CreatedAt", LocalDateTime.class));
        } catch (SQLException e) {
            // Bỏ qua nếu trường không tồn tại
        }
        
        try {
            product.setUpdatedAt(rs.getObject("UpdatedAt", LocalDateTime.class));
        } catch (SQLException e) {
            // Bỏ qua nếu trường không tồn tại
        }
        
        // Đọc trường Manufacturer
        try {
            product.setManufacturer(rs.getString("Manufacturer"));
        } catch (SQLException e) {
            product.setManufacturer("");
        }

        // Xử lý Category
        try {
            String categoryId = rs.getString("CategoryID");
            if (categoryId != null && !categoryId.isEmpty()) {
                Category category = new Category();
                category.setCategoryId(categoryId);
                
                try {
                    category.setCategoryName(rs.getString("CategoryName"));
                } catch (SQLException e) {
                    category.setCategoryName("Unknown");
                }
                
                product.setCategory(category);
            }
        } catch (SQLException e) {
            // Bỏ qua nếu không có CategoryID
        }
        
        // Xử lý Supplier - chỉ khi cần
        try {
            String supplierId = rs.getString("SupplierID");
            if (supplierId != null && !supplierId.isEmpty()) {
                // Chỉ set ID của supplier, không query thêm
                Supplier supplier = new Supplier();
                supplier.setSupplierId(supplierId);
                product.setSupplier(supplier);
            }
        } catch (SQLException e) {
            // Bỏ qua nếu không có SupplierID
        }
        
        // Đọc các trường mới
        product.setCostPrice(rs.getBigDecimal("CostPrice"));
        product.setAverageCostPrice(rs.getBigDecimal("AverageCostPrice"));
        product.setProfitMargin(rs.getBigDecimal("ProfitMargin"));
        
        return product;
    }
}