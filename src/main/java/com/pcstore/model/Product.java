package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.LocaleManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class biểu diễn sản phẩm
 */
public class Product extends BaseTimeEntity {
    private String productId;
    private String productName;
    private BigDecimal price;
    private int stockQuantity;
    private String specifications;
    private String description;
    private Category category;
    private Supplier supplier;
    private String manufacturer;    
    private BigDecimal costPrice;        // Giá vốn gần nhất
    private BigDecimal averageCostPrice; // Giá vốn trung bình
    private BigDecimal profitMargin;     // Tỷ suất lợi nhuận (%)

    public Product(String productId, String productName, BigDecimal price, int stockQuantity, String specifications,
            String description, Category category, Supplier supplier) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.specifications = specifications;
        this.description = description;
        this.category = category;
        this.supplier = supplier;
    }

    public Product() {
    }

    @Override
    public Object getId() {
        return productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Mã sản phẩm"));
        }
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_NAME_EMPTY);
        }
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Giá sản phẩm"));
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_PRICE_NEGATIVE);
        }
        this.price = price;
        setUpdatedAt(java.time.LocalDateTime.now());
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_QUANTITY_NEGATIVE);
        }
        this.stockQuantity = stockQuantity;
        setUpdatedAt(java.time.LocalDateTime.now());
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException(ErrorMessage.CATEGORY_NULL);
        }
        this.category = category;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_SUPPLIER_NULL);
        }
        this.supplier = supplier;
    }
   
    public boolean hasEnoughStock(int quantity) {
        return this.stockQuantity >= quantity;
    }
    
    public void decreaseStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new IllegalArgumentException(
                String.format(ErrorMessage.PRODUCT_INSUFFICIENT_STOCK, quantity, this.stockQuantity));
        }
        setStockQuantity(this.stockQuantity - quantity);
    }
    
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_QUANTITY_NOT_POSITIVE);
        }
        setStockQuantity(this.stockQuantity + quantity);
    }
    
    public int getQuantityInStock() {
        return getStockQuantity();
    }
    
    public boolean hasWarranty() {
        return true;
    }
    
    
    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public BigDecimal getCostPrice() {
        return costPrice == null ? BigDecimal.ZERO : costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getAverageCostPrice() {
        return averageCostPrice == null ? BigDecimal.ZERO : averageCostPrice;
    }

    public void setAverageCostPrice(BigDecimal averageCostPrice) {
        this.averageCostPrice = averageCostPrice;
    }

    public BigDecimal getProfitMargin() {
        return profitMargin == null ? new BigDecimal(LocaleManager.profitMargin) : profitMargin;
    }

    public void setProfitMargin(BigDecimal profitMargin) {
        this.profitMargin = profitMargin;
    }

    public BigDecimal calculateSellingPrice() {
        BigDecimal avgCost = getAverageCostPrice();
        if (avgCost.compareTo(BigDecimal.ZERO) <= 0) {
            return price; // Trả về giá hiện tại nếu không có giá vốn
        }
        
        BigDecimal margin = getProfitMargin().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal factor = BigDecimal.ONE.add(margin);
        return avgCost.multiply(factor).setScale(0, RoundingMode.CEILING); // Làm tròn lên đến đơn vị
    }

    // Factory method để tạo sản phẩm mới
    public static Product createNew(String productId, String productName, 
                                 BigDecimal price, Category category,
                                 Supplier supplier) {
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName(productName);
        product.setPrice(price);
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setStockQuantity(0);
        return product;
    }
    
    
    
    // Phương thức kiểm tra tồn kho tối thiểu
    public boolean isLowStock(int minimumStock) {
        return this.stockQuantity <= minimumStock;
    }
}