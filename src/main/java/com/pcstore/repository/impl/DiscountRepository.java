package com.pcstore.repository.impl;

import com.pcstore.repository.Repository;
import com.pcstore.model.Discount;
import com.pcstore.model.Category;
import com.pcstore.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository implementation cho Discount entity, ánh xạ với bảng Promotions trong database
 */
public class DiscountRepository implements Repository<Discount, Integer> {
    private Connection connection;
    
    public DiscountRepository(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public Discount add(Discount discount) {
        String sql = "INSERT INTO Promotions (PromotionName, DiscountType, DiscountValue, " +
                     "StartDate, EndDate, MinimumPurchase, IsActive, Description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime now = LocalDateTime.now();
            discount.setCreatedAt(now);
            discount.setUpdatedAt(now);
            
            // Set discount type based on percentage flag
            String discountType = discount.isPercentage() ? "Percentage" : "Fixed Amount";
            
            // Set discount value based on type
            BigDecimal discountValue;
            if (discount.isPercentage()) {
                discountValue = BigDecimal.valueOf(discount.getDiscountPercentage());
            } else {
                discountValue = discount.getDiscountAmount();
            }
            
            statement.setString(1, discount.getDiscountCode());
            statement.setString(2, discountType);
            statement.setObject(3, discountValue);
            statement.setTimestamp(4, Timestamp.valueOf(discount.getStartDate()));
            statement.setTimestamp(5, Timestamp.valueOf(discount.getEndDate()));
            statement.setObject(6, discount.getMinPurchaseAmount());
            statement.setBoolean(7, discount.isActive());
            statement.setString(8, discount.getDescription());
            
            statement.executeUpdate();
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    discount.setDiscountId(generatedKeys.getInt(1));
                }
            }
            
            // Save discount associations if any
            saveDiscountProductAssociations(discount);
            saveDiscountCategoryAssociations(discount);
            
            return discount;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm khuyến mãi", e);
        }
    }
    
    @Override
    public Discount update(Discount discount) {
        String sql = "UPDATE Promotions SET PromotionName = ?, DiscountType = ?, DiscountValue = ?, " +
                     "StartDate = ?, EndDate = ?, MinimumPurchase = ?, IsActive = ?, Description = ? " +
                     "WHERE PromotionID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            discount.setUpdatedAt(LocalDateTime.now());
            
            // Set discount type based on percentage flag
            String discountType = discount.isPercentage() ? "Percentage" : "Fixed Amount";
            
            // Set discount value based on type
            BigDecimal discountValue;
            if (discount.isPercentage()) {
                discountValue = BigDecimal.valueOf(discount.getDiscountPercentage());
            } else {
                discountValue = discount.getDiscountAmount();
            }
            
            statement.setString(1, discount.getDiscountCode());
            statement.setString(2, discountType);
            statement.setObject(3, discountValue);
            statement.setTimestamp(4, Timestamp.valueOf(discount.getStartDate()));
            statement.setTimestamp(5, Timestamp.valueOf(discount.getEndDate()));
            statement.setObject(6, discount.getMinPurchaseAmount());
            statement.setBoolean(7, discount.isActive());
            statement.setString(8, discount.getDescription());
            statement.setInt(9, discount.getDiscountId());
            
            statement.executeUpdate();
            
            // Update discount associations
            // First remove existing associations
            deleteDiscountProductAssociations(discount.getDiscountId());
            deleteDiscountCategoryAssociations(discount.getDiscountId());
            
            // Then add new associations
            saveDiscountProductAssociations(discount);
            saveDiscountCategoryAssociations(discount);
            
            return discount;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật khuyến mãi", e);
        }
    }
    
    @Override
    public boolean delete(Integer id) {
        // First, delete related associations
        deleteDiscountProductAssociations(id);
        deleteDiscountCategoryAssociations(id);
        
        // Then delete the discount
        String sql = "DELETE FROM Promotions WHERE PromotionID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa khuyến mãi", e);
        }
    }
    
    @Override
    public Optional<Discount> findById(Integer id) {
        String sql = "SELECT * FROM Promotions WHERE PromotionID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                Discount discount = mapResultSetToDiscount(resultSet);
                
                // Load discount associations
                loadDiscountProductAssociations(discount);
                loadDiscountCategoryAssociations(discount);
                
                return Optional.of(discount);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm khuyến mãi theo ID", e);
        }
    }
    
    @Override
    public List<Discount> findAll() {
        String sql = "SELECT * FROM Promotions";
        List<Discount> discounts = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                Discount discount = mapResultSetToDiscount(resultSet);
                
                // Load discount associations
                loadDiscountProductAssociations(discount);
                loadDiscountCategoryAssociations(discount);
                
                discounts.add(discount);
            }
            return discounts;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy tất cả khuyến mãi", e);
        }
    }
    
    @Override
    public boolean exists(Integer id) {
        String sql = "SELECT COUNT(*) FROM Promotions WHERE PromotionID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra tồn tại khuyến mãi", e);
        }
    }
    
    // Phương thức lấy danh sách khuyến mãi đang hoạt động
    public List<Discount> findActiveDiscounts() {
        String sql = "SELECT * FROM Promotions WHERE IsActive = 1 AND " +
                     "GETDATE() BETWEEN StartDate AND EndDate";
        List<Discount> discounts = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                Discount discount = mapResultSetToDiscount(resultSet);
                
                // Load discount associations
                loadDiscountProductAssociations(discount);
                loadDiscountCategoryAssociations(discount);
                
                discounts.add(discount);
            }
            return discounts;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy khuyến mãi đang hoạt động", e);
        }
    }
    
    // Phương thức lấy danh sách khuyến mãi theo mã code
    public Optional<Discount> findByCode(String code) {
        String sql = "SELECT * FROM Promotions WHERE PromotionName = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                Discount discount = mapResultSetToDiscount(resultSet);
                
                // Load discount associations
                loadDiscountProductAssociations(discount);
                loadDiscountCategoryAssociations(discount);
                
                return Optional.of(discount);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm khuyến mãi theo mã", e);
        }
    }
    
    // Phương thức lấy danh sách khuyến mãi áp dụng cho sản phẩm
    public List<Discount> findByProductId(String productId) {
        String sql = "SELECT p.* FROM Promotions p " +
                     "JOIN PromotionProducts pp ON p.PromotionID = pp.PromotionID " +
                     "WHERE pp.ProductID = ? AND p.IsActive = 1 AND " +
                     "GETDATE() BETWEEN p.StartDate AND p.EndDate";
        
        List<Discount> discounts = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Discount discount = mapResultSetToDiscount(resultSet);
                
                // Load discount associations
                loadDiscountProductAssociations(discount);
                loadDiscountCategoryAssociations(discount);
                
                discounts.add(discount);
            }
            return discounts;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy khuyến mãi theo sản phẩm", e);
        }
    }
    
    // Phương thức lấy danh sách khuyến mãi áp dụng cho danh mục
    public List<Discount> findByCategoryId(String categoryId) {
        String sql = "SELECT p.* FROM Promotions p " +
                     "JOIN PromotionCategories pc ON p.PromotionID = pc.PromotionID " +
                     "WHERE pc.CategoryID = ? AND p.IsActive = 1 AND " +
                     "GETDATE() BETWEEN p.StartDate AND p.EndDate";
        
        List<Discount> discounts = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoryId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Discount discount = mapResultSetToDiscount(resultSet);
                
                // Load discount associations
                loadDiscountProductAssociations(discount);
                loadDiscountCategoryAssociations(discount);
                
                discounts.add(discount);
            }
            return discounts;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy khuyến mãi theo danh mục", e);
        }
    }
    
    // Phương thức cập nhật trạng thái khuyến mãi
    public boolean updateStatus(Integer discountId, boolean isActive) {
        String sql = "UPDATE Promotions SET IsActive = ? WHERE PromotionID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, isActive);
            statement.setInt(2, discountId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật trạng thái khuyến mãi", e);
        }
    }
    
    // Phương thức tăng số lần sử dụng khuyến mãi
    public boolean incrementUsageCount(Integer discountId) {
        String sql = "UPDATE Promotions SET UsageCount = UsageCount + 1 WHERE PromotionID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, discountId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tăng số lần sử dụng khuyến mãi", e);
        }
    }
    
    // Phương thức ánh xạ ResultSet sang đối tượng Discount
    private Discount mapResultSetToDiscount(ResultSet resultSet) throws SQLException {
        Discount discount = new Discount();
        
        discount.setDiscountId(resultSet.getInt("PromotionID"));
        discount.setDiscountCode(resultSet.getString("PromotionName"));
        discount.setDescription(resultSet.getString("Description"));
        
        // Set discount type and value
        String discountType = resultSet.getString("DiscountType");
        BigDecimal discountValue = resultSet.getBigDecimal("DiscountValue");
        
        if ("Percentage".equals(discountType)) {
            discount.setPercentage(true);
            discount.setDiscountPercentage(discountValue.doubleValue());
        } else {
            discount.setPercentage(false);
            discount.setDiscountAmount(discountValue);
        }
        
        // Set dates
        Timestamp startDate = resultSet.getTimestamp("StartDate");
        if (startDate != null) {
            discount.setStartDate(startDate.toLocalDateTime());
        }
        
        Timestamp endDate = resultSet.getTimestamp("EndDate");
        if (endDate != null) {
            discount.setEndDate(endDate.toLocalDateTime());
        }
        
        // Set minimum purchase
        discount.setMinPurchaseAmount(resultSet.getBigDecimal("MinimumPurchase"));
        
        // Set active status
        discount.setActive(resultSet.getBoolean("IsActive"));
        
        // Set usage count if exists in table
        try {
            discount.setUsageCount(resultSet.getInt("UsageCount"));
        } catch (SQLException e) {
            // If column doesn't exist, set to 0
            discount.setUsageCount(0);
        }
        
        return discount;
    }
    
    // Phương thức lưu liên kết giữa khuyến mãi và sản phẩm
    private void saveDiscountProductAssociations(Discount discount) {
        if (discount.getDiscountId() == null || discount.getApplicableProducts().isEmpty()) {
            return;
        }
        
        String sql = "INSERT INTO PromotionProducts (PromotionID, ProductID) VALUES (?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Product product : discount.getApplicableProducts()) {
                statement.setInt(1, discount.getDiscountId());
                statement.setString(2, product.getProductId());
                statement.addBatch();
            }
            
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lưu liên kết khuyến mãi và sản phẩm", e);
        }
    }
    
    // Phương thức lưu liên kết giữa khuyến mãi và danh mục
    private void saveDiscountCategoryAssociations(Discount discount) {
        if (discount.getDiscountId() == null || discount.getApplicableCategories().isEmpty()) {
            return;
        }
        
        String sql = "INSERT INTO PromotionCategories (PromotionID, CategoryID) VALUES (?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Category category : discount.getApplicableCategories()) {
                statement.setInt(1, discount.getDiscountId());
                statement.setString(2, category.getCategoryId());
                statement.addBatch();
            }
            
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lưu liên kết khuyến mãi và danh mục", e);
        }
    }
    
    // Phương thức xóa liên kết giữa khuyến mãi và sản phẩm
    private void deleteDiscountProductAssociations(Integer discountId) {
        String sql = "DELETE FROM PromotionProducts WHERE PromotionID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, discountId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa liên kết khuyến mãi và sản phẩm", e);
        }
    }
    
    // Phương thức xóa liên kết giữa khuyến mãi và danh mục
    private void deleteDiscountCategoryAssociations(Integer discountId) {
        String sql = "DELETE FROM PromotionCategories WHERE PromotionID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, discountId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa liên kết khuyến mãi và danh mục", e);
        }
    }
    
    // Phương thức load danh sách sản phẩm áp dụng cho khuyến mãi
    private void loadDiscountProductAssociations(Discount discount) {
        String sql = "SELECT p.* FROM Products p " +
                     "JOIN PromotionProducts pp ON p.ProductID = pp.ProductID " +
                     "WHERE pp.PromotionID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, discount.getDiscountId());
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Product product = new Product();
                product.setProductId(resultSet.getString("ProductID"));
                product.setProductName(resultSet.getString("ProductName"));
                // Chỉ set các thuộc tính cần thiết, không load toàn bộ thông tin sản phẩm
                
                discount.addApplicableProduct(product);
            }
        } catch (SQLException e) {
            // Bảng PromotionProducts có thể chưa được tạo, bỏ qua lỗi
            System.err.println("Cảnh báo: Không thể load sản phẩm cho khuyến mãi. " + e.getMessage());
        }
    }
    
    // Phương thức load danh sách danh mục áp dụng cho khuyến mãi
    private void loadDiscountCategoryAssociations(Discount discount) {
        String sql = "SELECT c.* FROM Categories c " +
                     "JOIN PromotionCategories pc ON c.CategoryID = pc.CategoryID " +
                     "WHERE pc.PromotionID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, discount.getDiscountId());
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Category category = new Category();
                category.setCategoryId(resultSet.getString("CategoryID"));
                category.setCategoryName(resultSet.getString("CategoryName"));
                // Chỉ set các thuộc tính cần thiết, không load toàn bộ thông tin danh mục
                
                discount.addApplicableCategory(category);
            }
        } catch (SQLException e) {
            // Bảng PromotionCategories có thể chưa được tạo, bỏ qua lỗi
            System.err.println("Cảnh báo: Không thể load danh mục cho khuyến mãi. " + e.getMessage());
        }
    }
    
    // Phương thức lấy danh sách khuyến mãi áp dụng cho sản phẩm dựa vào giá
    public List<Discount> findApplicableDiscounts(String productId, BigDecimal price) {
        List<Discount> applicableDiscounts = new ArrayList<>();
        
        // Lấy tất cả khuyến mãi áp dụng cho sản phẩm
        List<Discount> productDiscounts = findByProductId(productId);
        
        // Lấy danh mục của sản phẩm
        String categoryId = getCategoryIdForProduct(productId);
        
        // Lấy tất cả khuyến mãi áp dụng cho danh mục
        List<Discount> categoryDiscounts = new ArrayList<>();
        if (categoryId != null) {
            categoryDiscounts = findByCategoryId(categoryId);
        }
        
        // Lấy tất cả khuyến mãi áp dụng cho tất cả sản phẩm (không có sản phẩm hoặc danh mục cụ thể)
        List<Discount> generalDiscounts = findGeneralDiscounts();
        
        // Kết hợp tất cả khuyến mãi và lọc theo giá
        List<Discount> allDiscounts = new ArrayList<>();
        allDiscounts.addAll(productDiscounts);
        allDiscounts.addAll(categoryDiscounts);
        allDiscounts.addAll(generalDiscounts);
        
        // Loại bỏ khuyến mãi trùng lặp và kiểm tra điều kiện giá
        for (Discount discount : allDiscounts) {
            if (!applicableDiscounts.contains(discount) && 
                (discount.getMinPurchaseAmount() == null || 
                price.compareTo(discount.getMinPurchaseAmount()) >= 0)) {
                applicableDiscounts.add(discount);
            }
        }
        
        return applicableDiscounts;
    }
    
    // Phương thức lấy mã danh mục cho sản phẩm
    private String getCategoryIdForProduct(String productId) {
        String sql = "SELECT CategoryID FROM Products WHERE ProductID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getString("CategoryID");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy mã danh mục cho sản phẩm", e);
        }
    }
    
    // Phương thức lấy danh sách khuyến mãi áp dụng cho tất cả sản phẩm
    private List<Discount> findGeneralDiscounts() {
        String sql = "SELECT p.* FROM Promotions p " +
                    "WHERE p.PromotionID NOT IN (SELECT DISTINCT PromotionID FROM PromotionProducts) " +
                    "AND p.PromotionID NOT IN (SELECT DISTINCT PromotionID FROM PromotionCategories) " +
                    "AND p.IsActive = 1 AND GETDATE() BETWEEN p.StartDate AND p.EndDate";
        
        List<Discount> discounts = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                discounts.add(mapResultSetToDiscount(resultSet));
            }
            return discounts;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy khuyến mãi áp dụng chung", e);
        }
    }
    
    // Phương thức lấy danh sách khuyến mãi sắp hết hạn
    public List<Discount> findDiscountsAboutToExpire(int daysThreshold) {
        String sql = "SELECT * FROM Promotions WHERE IsActive = 1 AND " +
                    "EndDate BETWEEN GETDATE() AND DATEADD(DAY, ?, GETDATE())";
        
        List<Discount> discounts = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, daysThreshold);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Discount discount = mapResultSetToDiscount(resultSet);
                
                // Load discount associations
                loadDiscountProductAssociations(discount);
                loadDiscountCategoryAssociations(discount);
                
                discounts.add(discount);
            }
            return discounts;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy khuyến mãi sắp hết hạn", e);
        }
    }

    public List<Discount> findByDateRange(LocalDate startDate, LocalDate endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByDateRange'");
    }
}