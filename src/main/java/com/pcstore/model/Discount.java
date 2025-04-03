package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class biểu diễn thông tin khuyến mãi
 * 
 * Đây là model chứa thông tin về các chương trình khuyến mãi trong hệ thống
 * với khả năng áp dụng cho cả danh mục hoặc sản phẩm cụ thể
 */
public class Discount extends BaseTimeEntity {
    // Thông tin cơ bản của khuyến mãi
    private Integer discountId;        // ID của khuyến mãi (INT trong database)
    private String discountCode;       // Mã code của khuyến mãi (VARCHAR(20) trong database)
    private String description;        // Mô tả về khuyến mãi (VARCHAR(255) trong database)
    private BigDecimal discountAmount; // Số tiền giảm giá (DECIMAL(10,2) trong database)
    private BigDecimal minPurchaseAmount; // Số tiền mua tối thiểu (DECIMAL(10,2) trong database)
    private double discountPercentage; // Phần trăm giảm giá (FLOAT trong database)
    private LocalDateTime startDate;   // Ngày bắt đầu khuyến mãi (DATETIME trong database)
    private LocalDateTime endDate;     // Ngày kết thúc khuyến mãi (DATETIME trong database)
    private boolean isPercentage;      // Khuyến mãi là phần trăm hay không (BIT trong database)
    private boolean isActive;          // Trạng thái kích hoạt (BIT trong database)
    private int usageLimit;            // Giới hạn số lần sử dụng (INT trong database)
    private int usageCount;            // Số lần đã sử dụng (INT trong database)

    // Danh sách các sản phẩm và danh mục áp dụng
    private List<Product> applicableProducts = new ArrayList<>();
    private List<Category> applicableCategories = new ArrayList<>();

    /**
     * Phương thức lấy ID của đối tượng (override từ BaseTimeEntity)
     * 
     * @return ID của khuyến mãi dưới dạng Object
     */
    @Override
    public Object getId() {
        return discountId;
    }

    /**
     * Lấy ID của khuyến mãi
     * 
     * @return ID của khuyến mãi
     */
    public Integer getDiscountId() {
        return discountId;
    }

    /**
     * Thiết lập ID cho khuyến mãi
     * 
     * @param discountId ID của khuyến mãi
     */
    public void setDiscountId(Integer discountId) {
        this.discountId = discountId;
    }

    /**
     * Lấy mã code của khuyến mãi
     * 
     * @return Mã code của khuyến mãi
     */
    public String getDiscountCode() {
        return discountCode;
    }

    /**
     * Thiết lập mã code cho khuyến mãi
     * 
     * @param discountCode Mã code của khuyến mãi (không được để trống)
     */
    public void setDiscountCode(String discountCode) {
        if (discountCode == null || discountCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khuyến mãi không được để trống");
        }
        this.discountCode = discountCode;
    }

    /**
     * Lấy mô tả của khuyến mãi
     * 
     * @return Mô tả của khuyến mãi
     */
    public String getDescription() {
        return description;
    }

    /**
     * Thiết lập mô tả cho khuyến mãi
     * 
     * @param description Mô tả của khuyến mãi
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Lấy số tiền giảm giá cố định
     * 
     * @return Số tiền giảm giá cố định (có thể null nếu khuyến mãi theo phần trăm)
     */
    

    /**
     * Thiết lập số tiền giảm giá cố định
     * 
     * @param discountAmount Số tiền giảm giá cố định
     */
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    /**
     * Lấy số tiền mua tối thiểu để áp dụng khuyến mãi
     * 
     * @return Số tiền mua tối thiểu (có thể null nếu không có giới hạn)
     */
    public BigDecimal getMinPurchaseAmount() {
        return minPurchaseAmount;
    }

    /**
     * Thiết lập số tiền mua tối thiểu để áp dụng khuyến mãi
     * 
     * @param minPurchaseAmount Số tiền mua tối thiểu
     */
    public void setMinPurchaseAmount(BigDecimal minPurchaseAmount) {
        this.minPurchaseAmount = minPurchaseAmount;
    }

    /**
     * Lấy phần trăm giảm giá
     * 
     * @return Phần trăm giảm giá (0-100)
     */
    public double getDiscountPercentage() {
        return discountPercentage;
    }

    /**
     * Thiết lập phần trăm giảm giá
     * 
     * @param discountPercentage Phần trăm giảm giá (0-100)
     */
    public void setDiscountPercentage(double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Phần trăm giảm giá phải nằm trong khoảng 0-100");
        }
        this.discountPercentage = discountPercentage;
    }

    /**
     * Lấy ngày bắt đầu khuyến mãi
     * 
     * @return Ngày bắt đầu khuyến mãi
     */
    public LocalDateTime getStartDate() {
        return startDate;
    }

    /**
     * Thiết lập ngày bắt đầu khuyến mãi
     * 
     * @param startDate Ngày bắt đầu khuyến mãi (không được null)
     */
    public void setStartDate(LocalDateTime startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được để trống");
        }
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không thể sau ngày kết thúc");
        }
        this.startDate = startDate;
    }

    /**
     * Lấy ngày kết thúc khuyến mãi
     * 
     * @return Ngày kết thúc khuyến mãi
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     * Thiết lập ngày kết thúc khuyến mãi
     * 
     * @param endDate Ngày kết thúc khuyến mãi (không được null)
     */
    public void setEndDate(LocalDateTime endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("Ngày kết thúc không được để trống");
        }
        if (startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Ngày kết thúc không thể trước ngày bắt đầu");
        }
        this.endDate = endDate;
    }

    /**
     * Kiểm tra khuyến mãi có áp dụng theo phần trăm không
     * 
     * @return true nếu khuyến mãi theo phần trăm, false nếu theo số tiền cố định
     */
    public boolean isPercentage() {
        return isPercentage;
    }

    /**
     * Thiết lập loại khuyến mãi (phần trăm hoặc số tiền cố định)
     * 
     * @param isPercentage true nếu khuyến mãi theo phần trăm, false nếu theo số tiền cố định
     */
    public void setPercentage(boolean isPercentage) {
        this.isPercentage = isPercentage;
    }

    /**
     * Kiểm tra khuyến mãi có đang hoạt động không
     * 
     * @return true nếu khuyến mãi đang hoạt động, false nếu không
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Thiết lập trạng thái hoạt động cho khuyến mãi
     * 
     * @param isActive true nếu kích hoạt khuyến mãi, false nếu vô hiệu hóa
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Lấy giới hạn số lần sử dụng khuyến mãi
     * 
     * @return Giới hạn số lần sử dụng (0 = không giới hạn)
     */
    public int getUsageLimit() {
        return usageLimit;
    }

    /**
     * Thiết lập giới hạn số lần sử dụng khuyến mãi
     * 
     * @param usageLimit Giới hạn số lần sử dụng (0 = không giới hạn)
     */
    public void setUsageLimit(int usageLimit) {
        if (usageLimit < 0) {
            throw new IllegalArgumentException("Giới hạn sử dụng không thể âm");
        }
        this.usageLimit = usageLimit;
    }

    /**
     * Lấy số lần đã sử dụng khuyến mãi
     * 
     * @return Số lần đã sử dụng
     */
    public int getUsageCount() {
        return usageCount;
    }

    /**
     * Thiết lập số lần đã sử dụng khuyến mãi
     * Thường chỉ được gọi từ Repository hoặc service layer
     * 
     * @param usageCount Số lần đã sử dụng
     */
    public void setUsageCount(int usageCount) {
        if (usageCount < 0) {
            throw new IllegalArgumentException("Số lần sử dụng không thể âm");
        }
        this.usageCount = usageCount;
    }

    /**
     * Tăng số lần sử dụng thêm 1
     */
    public void incrementUsageCount() {
        this.usageCount++;
    }

    /**
     * Lấy danh sách sản phẩm được áp dụng khuyến mãi
     * 
     * @return Danh sách các sản phẩm
     */
    public List<Product> getApplicableProducts() {
        return applicableProducts;
    }

    /**
     * Thiết lập danh sách sản phẩm được áp dụng khuyến mãi
     * 
     * @param applicableProducts Danh sách các sản phẩm
     */
    public void setApplicableProducts(List<Product> applicableProducts) {
        this.applicableProducts = applicableProducts != null ? applicableProducts : new ArrayList<>();
    }

    /**
     * Thêm một sản phẩm vào danh sách áp dụng
     * 
     * @param product Sản phẩm cần thêm
     */
    public void addApplicableProduct(Product product) {
        if (product != null && !this.applicableProducts.contains(product)) {
            this.applicableProducts.add(product);
        }
    }

    /**
     * Xóa một sản phẩm khỏi danh sách áp dụng
     * 
     * @param product Sản phẩm cần xóa
     * @return true nếu xóa thành công, false nếu không tìm thấy
     */
    public boolean removeApplicableProduct(Product product) {
        return this.applicableProducts.remove(product);
    }

    /**
     * Lấy danh sách danh mục được áp dụng khuyến mãi
     * 
     * @return Danh sách các danh mục
     */
    public List<Category> getApplicableCategories() {
        return applicableCategories;
    }

    /**
     * Thiết lập danh sách danh mục được áp dụng khuyến mãi
     * 
     * @param applicableCategories Danh sách các danh mục
     */
    public void setApplicableCategories(List<Category> applicableCategories) {
        this.applicableCategories = applicableCategories != null ? applicableCategories : new ArrayList<>();
    }

    /**
     * Thêm một danh mục vào danh sách áp dụng
     * 
     * @param category Danh mục cần thêm
     */
    public void addApplicableCategory(Category category) {
        if (category != null && !this.applicableCategories.contains(category)) {
            this.applicableCategories.add(category);
        }
    }

    /**
     * Xóa một danh mục khỏi danh sách áp dụng
     * 
     * @param category Danh mục cần xóa
     * @return true nếu xóa thành công, false nếu không tìm thấy
     */
    public boolean removeApplicableCategory(Category category) {
        return this.applicableCategories.remove(category);
    }

    /**
     * Kiểm tra xem khuyến mãi có còn hiệu lực không
     * 
     * @return true nếu khuyến mãi còn hiệu lực, false nếu đã hết hạn
     */
    public boolean isValid() {
        if (!isActive) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        boolean withinTimeframe = !now.isBefore(startDate) && !now.isAfter(endDate);
        boolean withinUsageLimit = usageLimit == 0 || usageCount < usageLimit;
        
        return withinTimeframe && withinUsageLimit;
    }

    /**
     * Kiểm tra xem khuyến mãi có áp dụng được cho sản phẩm cụ thể không
     * 
     * @param product Sản phẩm cần kiểm tra
     * @return true nếu khuyến mãi áp dụng được cho sản phẩm, false nếu không
     */
    public boolean isApplicableToProduct(Product product) {
        if (product == null || !isValid()) {
            return false;
        }
        
        // Nếu không có sản phẩm hoặc danh mục nào được chỉ định, áp dụng cho tất cả
        if (applicableProducts.isEmpty() && applicableCategories.isEmpty()) {
            return true;
        }
        
        // Kiểm tra sản phẩm có trong danh sách không
        if (applicableProducts.contains(product)) {
            return true;
        }
        
        // Kiểm tra danh mục của sản phẩm có trong danh sách không
        if (product.getCategory() != null) {
            for (Category category : applicableCategories) {
                if (category.getCategoryId().equals(product.getCategory().getCategoryId())) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * Tính toán giá trị giảm giá dựa trên số tiền mua
     * 
     * @param amount Số tiền mua
     * @return Số tiền được giảm
     */
    public BigDecimal calculateDiscount(BigDecimal amount) {
        if (amount == null || !isValid()) {
            return BigDecimal.ZERO;
        }
        
        // Kiểm tra số tiền tối thiểu
        if (minPurchaseAmount != null && amount.compareTo(minPurchaseAmount) < 0) {
            return BigDecimal.ZERO;
        }
        
        if (isPercentage) {
            BigDecimal percentage = BigDecimal.valueOf(discountPercentage).divide(BigDecimal.valueOf(100));
            return amount.multiply(percentage);
        } else {
            // Nếu số tiền giảm lớn hơn tổng số tiền mua, chỉ giảm tối đa bằng số tiền mua
            return discountAmount != null && discountAmount.compareTo(amount) <= 0 ? 
                   discountAmount : amount;
        }
    }

    /**
     * Factory method tạo khuyến mãi dạng phần trăm
     * 
     * @param code Mã khuyến mãi
     * @param description Mô tả khuyến mãi
     * @param percentage Phần trăm giảm giá (0-100)
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @param minPurchase Số tiền mua tối thiểu (null nếu không có)
     * @return Đối tượng Discount đã được thiết lập
     */
    public static Discount createPercentageDiscount(String code, String description, 
                                                  double percentage, LocalDateTime startDate, 
                                                  LocalDateTime endDate, BigDecimal minPurchase) {
        Discount discount = new Discount();
        discount.setDiscountCode(code);
        discount.setDescription(description);
        discount.setPercentage(true);
        discount.setDiscountPercentage(percentage);
        discount.setStartDate(startDate);
        discount.setEndDate(endDate);
        discount.setMinPurchaseAmount(minPurchase);
        discount.setActive(true);
        discount.setUsageLimit(0); // Không giới hạn
        discount.setUsageCount(0);
        return discount;
    }

    /**
     * Factory method tạo khuyến mãi dạng số tiền cố định
     * 
     * @param code Mã khuyến mãi
     * @param description Mô tả khuyến mãi
     * @param amount Số tiền giảm cố định
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @param minPurchase Số tiền mua tối thiểu (null nếu không có)
     * @return Đối tượng Discount đã được thiết lập
     */
    public static Discount createFixedAmountDiscount(String code, String description, 
                                                   BigDecimal amount, LocalDateTime startDate, 
                                                   LocalDateTime endDate, BigDecimal minPurchase) {
        Discount discount = new Discount();
        discount.setDiscountCode(code);
        discount.setDescription(description);
        discount.setPercentage(false);
        discount.setDiscountAmount(amount);
        discount.setStartDate(startDate);
        discount.setEndDate(endDate);
        discount.setMinPurchaseAmount(minPurchase);
        discount.setActive(true);
        discount.setUsageLimit(0); // Không giới hạn
        discount.setUsageCount(0);
        return discount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
}