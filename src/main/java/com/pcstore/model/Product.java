package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import com.pcstore.utils.ErrorMessage;
import java.math.BigDecimal;
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
    
    // Một sản phẩm xuất hiện trong nhiều chi tiết hóa đơn
    private List<InvoiceDetail> invoiceDetails = new ArrayList<>();
    
    // Một sản phẩm xuất hiện trong nhiều chi tiết nhập hàng
    private List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<>();

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

    public List<InvoiceDetail> getInvoiceDetails() {
        return invoiceDetails;
    }

    public void setInvoiceDetails(List<InvoiceDetail> invoiceDetails) {
        this.invoiceDetails = invoiceDetails;
    }
    
    public void addInvoiceDetail(InvoiceDetail invoiceDetail) {
        if (invoiceDetail == null) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Chi tiết hóa đơn"));
        }
        this.invoiceDetails.add(invoiceDetail);
        invoiceDetail.setProduct(this);
    }

    public void removeInvoiceDetail(InvoiceDetail invoiceDetail) {
        if (this.invoiceDetails.remove(invoiceDetail)) {
            invoiceDetail.setProduct(null);
        }
    }

    public List<PurchaseOrderDetail> getPurchaseOrderDetails() {
        return purchaseOrderDetails;
    }

    public void setPurchaseOrderDetails(List<PurchaseOrderDetail> purchaseOrderDetails) {
        this.purchaseOrderDetails = purchaseOrderDetails;
    }
    
    public void addPurchaseOrderDetail(PurchaseOrderDetail detail) {
        if (detail == null) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Chi tiết đơn nhập hàng"));
        }
        this.purchaseOrderDetails.add(detail);
        detail.setProduct(this);
    }

    public void removePurchaseOrderDetail(PurchaseOrderDetail detail) {
        if (this.purchaseOrderDetails.remove(detail)) {
            detail.setProduct(null);
        }
    }
    
    // Phương thức kiểm tra xem có đủ số lượng tồn kho không
    public boolean hasEnoughStock(int quantity) {
        return this.stockQuantity >= quantity;
    }
    
    // Phương thức giảm số lượng tồn kho khi bán
    public void decreaseStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new IllegalArgumentException(
                String.format(ErrorMessage.PRODUCT_INSUFFICIENT_STOCK, quantity, this.stockQuantity));
        }
        setStockQuantity(this.stockQuantity - quantity);
    }
    
    // Phương thức tăng số lượng tồn kho khi nhập hàng
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_QUANTITY_NOT_POSITIVE);
        }
        setStockQuantity(this.stockQuantity + quantity);
    }
    
    // Phương thức getter cho số lượng tồn kho (alias cho getStockQuantity để tương thích với InvoiceDetail)
    public int getQuantityInStock() {
        return getStockQuantity();
    }
    
    // Phương thức kiểm tra sản phẩm có bảo hành không
    public boolean hasWarranty() {
        // Mặc định là có bảo hành, có thể thay đổi logic tùy theo yêu cầu kinh doanh
        return true;
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
    
    // Phương thức kiểm tra xem sản phẩm có thể xóa không
    public boolean canDelete() {
        return (invoiceDetails == null || invoiceDetails.isEmpty()) &&
               (purchaseOrderDetails == null || purchaseOrderDetails.isEmpty());
    }
    
    // Phương thức kiểm tra tồn kho tối thiểu
    public boolean isLowStock(int minimumStock) {
        return this.stockQuantity <= minimumStock;
    }
}