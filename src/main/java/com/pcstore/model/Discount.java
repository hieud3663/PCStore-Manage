package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class biểu diễn chương trình khuyến mãi
 */
public class Discount extends BaseTimeEntity {
    private Integer discountId;
    private String discountCode;
    private String description;
    private BigDecimal discountAmount;
    private BigDecimal minPurchaseAmount;
    private double discountPercentage;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isPercentage;
    private boolean isActive;
    private int usageLimit;
    private int usageCount;
    private List<Category> applicableCategories = new ArrayList<>();
    private List<Product> applicableProducts = new ArrayList<>();

    @Override
    public Object getId() {
        return discountId;
    }

    public Integer getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Integer discountId) {
        this.discountId = discountId;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        if (discountCode == null || discountCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khuyến mãi không được để trống");
        }
        this.discountCode = discountCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        if (!isPercentage && discountAmount == null) {
            throw new IllegalArgumentException("Số tiền giảm giá không được để trống");
        }
        if (!isPercentage && discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số tiền giảm giá không được âm");
        }
        this.discountAmount = discountAmount;
    }

    public BigDecimal getMinPurchaseAmount() {
        return minPurchaseAmount;
    }

    public void setMinPurchaseAmount(BigDecimal minPurchaseAmount) {
        if (minPurchaseAmount != null && minPurchaseAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số tiền mua tối thiểu không được âm");
        }
        this.minPurchaseAmount = minPurchaseAmount;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        if (isPercentage && (discountPercentage <= 0 || discountPercentage > 100)) {
            throw new IllegalArgumentException("Phần trăm giảm giá phải nằm trong khoảng 0-100%");
        }
        this.discountPercentage = discountPercentage;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được để trống");
        }
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không thể sau ngày kết thúc");
        }
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("Ngày kết thúc không được để trống");
        }
        if (startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Ngày kết thúc không thể trước ngày bắt đầu");
        }
        this.endDate = endDate;
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    public void setPercentage(boolean percentage) {
        this.isPercentage = percentage;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(int usageLimit) {
        if (usageLimit < 0) {
            throw new IllegalArgumentException("Giới hạn sử dụng không được âm");
        }
        this.usageLimit = usageLimit;
    }

    public int getUsageCount() {
        return usageCount;
    }

    protected void setUsageCount(int usageCount) {
        if (usageCount < 0) {
            throw new IllegalArgumentException("Số lần sử dụng không được âm");
        }
        this.usageCount = usageCount;
    }

    public List<Category> getApplicableCategories() {
        return applicableCategories;
    }

    protected void setApplicableCategories(List<Category> categories) {
        this.applicableCategories = categories != null ? categories : new ArrayList<>();
    }

    public void addCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Danh mục không được để trống");
        }
        if (!this.applicableCategories.contains(category)) {
            this.applicableCategories.add(category);
        }
    }

    public void removeCategory(Category category) {
        this.applicableCategories.remove(category);
    }

    public List<Product> getApplicableProducts() {
        return applicableProducts;
    }

    protected void setApplicableProducts(List<Product> products) {
        this.applicableProducts = products != null ? products : new ArrayList<>();
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Sản phẩm không được để trống");
        }
        if (!this.applicableProducts.contains(product)) {
            this.applicableProducts.add(product);
        }
    }

    public void removeProduct(Product product) {
        this.applicableProducts.remove(product);
    }

    // Kiểm tra khuyến mãi còn hiệu lực không
    public boolean isValid() {
        if (!isActive) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startDate) || now.isAfter(endDate)) {
            return false;
        }
        
        if (usageLimit > 0 && usageCount >= usageLimit) {
            return false;
        }
        
        return true;
    }

    // Kiểm tra sản phẩm có được áp dụng khuyến mãi không
    public boolean isApplicableToProduct(Product product) {
        if (product == null || !isValid()) {
            return false;
        }
        
        // Nếu không có danh sách sản phẩm và danh mục cụ thể, áp dụng cho tất cả
        if (applicableProducts.isEmpty() && applicableCategories.isEmpty()) {
            return true;
        }
        
        // Kiểm tra sản phẩm có trong danh sách không
        if (applicableProducts.contains(product)) {
            return true;
        }
        
        // Kiểm tra danh mục của sản phẩm
        Category productCategory = product.getCategory();
        if (productCategory != null) {
            for (Category category : applicableCategories) {
                if (category.equals(productCategory)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    // Tính số tiền giảm giá cho một sản phẩm
    public BigDecimal calculateDiscount(Product product, int quantity) {
        if (!isApplicableToProduct(product)) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        
        // Kiểm tra điều kiện mua tối thiểu
        if (minPurchaseAmount != null && totalPrice.compareTo(minPurchaseAmount) < 0) {
            return BigDecimal.ZERO;
        }
        
        if (isPercentage) {
            return totalPrice.multiply(BigDecimal.valueOf(discountPercentage / 100.0));
        } else {
            return discountAmount;
        }
    }

    // Sử dụng khuyến mãi
    public void use() {
        if (!isValid()) {
            throw new IllegalStateException("Khuyến mãi không hợp lệ hoặc đã hết hiệu lực");
        }
        if (usageLimit > 0 && usageCount >= usageLimit) {
            throw new IllegalStateException("Đã vượt quá giới hạn sử dụng");
        }
        usageCount++;
    }

    // Factory method để tạo khuyến mãi mới theo số tiền
    public static Discount createFixedAmountDiscount(String code, BigDecimal amount,
                                                   LocalDateTime startDate,
                                                   LocalDateTime endDate) {
        Discount discount = new Discount();
        discount.setDiscountCode(code);
        discount.setPercentage(false);
        discount.setDiscountAmount(amount);
        discount.setStartDate(startDate);
        discount.setEndDate(endDate);
        discount.setActive(true);
        return discount;
    }

    // Factory method để tạo khuyến mãi mới theo phần trăm
    public static Discount createPercentageDiscount(String code, double percentage,
                                                  LocalDateTime startDate,
                                                  LocalDateTime endDate) {
        Discount discount = new Discount();
        discount.setDiscountCode(code);
        discount.setPercentage(true);
        discount.setDiscountPercentage(percentage);
        discount.setStartDate(startDate);
        discount.setEndDate(endDate);
        discount.setActive(true);
        return discount;
    }
}