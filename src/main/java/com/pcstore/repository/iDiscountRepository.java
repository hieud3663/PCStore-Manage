package com.pcstore.repository;

import com.pcstore.model.Discount;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface iDiscountRepository extends iRepository<Discount, String> {
    List<Discount> findByName(String name);
    List<Discount> findActive();
    List<Discount> findByDateRange(LocalDate startDate, LocalDate endDate);
    List<Discount> findByProductId(String productId);
    List<Discount> findByCategoryId(int categoryId);
    List<Discount> findApplicableDiscounts(String productId, BigDecimal price);
    boolean updateStatus(int discountId, boolean isActive);
    boolean incrementUsageCount(int discountId);
    List<Discount> findDiscountsAboutToExpire(int daysThreshold);
}