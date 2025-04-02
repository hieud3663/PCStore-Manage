package com.pcstore.service;

import com.pcstore.model.Discount;
import com.pcstore.repository.iDiscountRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý logic nghiệp vụ liên quan đến khuyến mãi và giảm giá
 */
public class DiscountService {
    private final iDiscountRepository discountRepository;
    
    /**
     * Khởi tạo service với repository
     * @param discountRepository Repository khuyến mãi
     */
    public DiscountService(iDiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }
    
    /**
     * Tạo khuyến mãi mới
     * @param discount Thông tin khuyến mãi
     * @return Khuyến mãi đã được tạo
     */
    public Discount createDiscount(Discount discount) {
        // Thiết lập mặc định nếu cần
        if (discount.getCreatedDate() == null) {
            discount.setCreatedDate(LocalDate.now());
        }
        
        return discountRepository.add(discount);
    }
    
    /**
     * Cập nhật thông tin khuyến mãi
     * @param discount Thông tin khuyến mãi mới
     * @return Khuyến mãi đã được cập nhật
     */
    public Discount updateDiscount(Discount discount) {
        return discountRepository.update(discount);
    }
    
    /**
     * Xóa khuyến mãi theo ID
     * @param discountId ID của khuyến mãi
     * @return true nếu xóa thành công, ngược lại là false
     */
    public boolean deleteDiscount(String discountId) {
        return discountRepository.delete(discountId);
    }
    
    /**
     * Tìm khuyến mãi theo ID
     * @param discountId ID của khuyến mãi
     * @return Optional chứa khuyến mãi nếu tìm thấy
     */
    public Optional<Discount> findDiscountById(String discountId) {
        return discountRepository.findById(discountId);
    }
    
    /**
     * Lấy danh sách tất cả khuyến mãi
     * @return Danh sách khuyến mãi
     */
    public List<Discount> findAllDiscounts() {
        return discountRepository.findAll();
    }
    
    /**
     * Tìm khuyến mãi theo tên
     * @param name Tên khuyến mãi
     * @return Danh sách khuyến mãi có tên tương ứng
     */
    public List<Discount> findDiscountsByName(String name) {
        return discountRepository.findByName(name);
    }
    
    /**
     * Lấy danh sách khuyến mãi đang hoạt động
     * @return Danh sách khuyến mãi đang hoạt động
     */
    public List<Discount> findActiveDiscounts() {
        return discountRepository.findActive();
    }
    
    /**
     * Tìm khuyến mãi trong khoảng thời gian
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách khuyến mãi trong khoảng thời gian
     */
    public List<Discount> findDiscountsByDateRange(LocalDate startDate, LocalDate endDate) {
        return discountRepository.findByDateRange(startDate, endDate);
    }
    
    /**
     * Tìm khuyến mãi theo sản phẩm
     * @param productId ID của sản phẩm
     * @return Danh sách khuyến mãi áp dụng cho sản phẩm
     */
    public List<Discount> findDiscountsByProductId(String productId) {
        return discountRepository.findByProductId(productId);
    }
    
    /**
     * Tìm khuyến mãi theo danh mục
     * @param categoryId ID của danh mục
     * @return Danh sách khuyến mãi áp dụng cho danh mục
     */
    public List<Discount> findDiscountsByCategoryId(int categoryId) {
        return discountRepository.findByCategoryId(categoryId);
    }
    
    /**
     * Tìm các khuyến mãi có thể áp dụng cho sản phẩm và giá
     * @param productId ID của sản phẩm
     * @param price Giá của sản phẩm
     * @return Danh sách khuyến mãi có thể áp dụng
     */
    public List<Discount> findApplicableDiscounts(String productId, BigDecimal price) {
        return discountRepository.findApplicableDiscounts(productId, price);
    }
    
    /**
     * Cập nhật trạng thái khuyến mãi
     * @param discountId ID của khuyến mãi
     * @param isActive Trạng thái hoạt động mới
     * @return true nếu cập nhật thành công, ngược lại là false
     */
    public boolean updateDiscountStatus(int discountId, boolean isActive) {
        return discountRepository.updateStatus(discountId, isActive);
    }
    
    /**
     * Tăng số lần sử dụng của khuyến mãi
     * @param discountId ID của khuyến mãi
     * @return true nếu cập nhật thành công, ngược lại là false
     */
    public boolean incrementDiscountUsageCount(int discountId) {
        return discountRepository.incrementUsageCount(discountId);
    }
    
    /**
     * Tìm các khuyến mãi sắp hết hạn
     * @param daysThreshold Số ngày còn lại trước khi hết hạn
     * @return Danh sách khuyến mãi sắp hết hạn
     */
    public List<Discount> findDiscountsAboutToExpire(int daysThreshold) {
        return discountRepository.findDiscountsAboutToExpire(daysThreshold);
    }
    
    /**
     * Tính giá sau khi áp dụng khuyến mãi
     * @param originalPrice Giá gốc
     * @param discount Khuyến mãi áp dụng
     * @return Giá sau khi áp dụng khuyến mãi
     */
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, Discount discount) {
        if (discount == null) {
            return originalPrice;
        }
        
        BigDecimal discountAmount;
        if (discount.isPercentage()) {
            // Nếu là giảm giá theo phần trăm
            BigDecimal discountPercent = new BigDecimal(discount.getDiscountValue());
            discountAmount = originalPrice.multiply(discountPercent).divide(new BigDecimal("100"));
        } else {
            // Nếu là giảm giá theo số tiền cụ thể
            discountAmount = new BigDecimal(discount.getDiscountValue());
        }
        
        // Đảm bảo giá sau khuyến mãi không âm
        BigDecimal discountedPrice = originalPrice.subtract(discountAmount);
        return discountedPrice.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : discountedPrice;
    }
    
    /**
     * Kiểm tra xem khuyến mãi có còn hiệu lực không
     * @param discount Khuyến mãi cần kiểm tra
     * @return true nếu khuyến mãi còn hiệu lực, ngược lại là false
     */
    public boolean isDiscountValid(Discount discount) {
        LocalDate now = LocalDate.now();
        return discount.isActive() && 
               !now.isBefore(discount.getStartDate()) && 
               !now.isAfter(discount.getEndDate()) &&
               (discount.getMaxUsage() == 0 || discount.getUsageCount() < discount.getMaxUsage());
    }
}