package com.pcstore.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.pcstore.model.base.BaseTimeEntity;

/**
 * Class biểu diễn chi tiết hóa đơn
 */
public class InvoiceDetail extends BaseTimeEntity {
    private Integer invoiceDetailId;
    private Invoice invoice;
    private Product product;
    private Warranty warranty;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountAmount;
    private String notes;

    // Thêm các trường bổ sung
    // private String customerName;
    // private String customerPhone;
    // private String customerId;
    // private LocalDateTime purchaseDate;
    // private String productName;
    // private String manufacturer;
    // private LocalDateTime warrantyEndDate; // Ngày kết thúc bảo hành
    // private String productId;

    public InvoiceDetail(Integer invoiceDetailId, Invoice invoice, Product product, Warranty warranty, int quantity,
            BigDecimal unitPrice, BigDecimal discountAmount, String notes) {
        this.invoiceDetailId = invoiceDetailId;
        this.invoice = invoice;
        this.product = product;
        this.warranty = warranty;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountAmount = discountAmount;
        this.notes = notes;
    }

    
    public InvoiceDetail() {
    }


    @Override
    public Object getId() {
        return invoiceDetailId;
    }

    public Integer getInvoiceDetailId() {
        return invoiceDetailId;
    }

    public void setInvoiceDetailId(Integer invoiceDetailId) {
        this.invoiceDetailId = invoiceDetailId;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Hóa đơn không được để trống");
        }
        this.invoice = invoice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Sản phẩm không được để trống");
        }
        this.product = product;
        if (this.unitPrice == null) {
            this.unitPrice = product.getPrice();
        }
    }

    public Warranty getWarranty() {
        return warranty;
    }

    public void setWarranty(Warranty warranty) {
        this.warranty = warranty;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        if (product != null && quantity > product.getQuantityInStock()) {
            throw new IllegalArgumentException("Số lượng vượt quá số lượng tồn kho");
        }
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        if (unitPrice == null) {
            throw new IllegalArgumentException("Đơn giá không được để trống");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Đơn giá phải lớn hơn 0");
        }
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount != null ? discountAmount : BigDecimal.ZERO;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số tiền giảm giá không được âm");
        }
        this.discountAmount = discountAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // public String getCustomerName() {
    //     return customerName;
    // }

    // public void setCustomerName(String customerName) {
    //     this.customerName = customerName;
    // }

    // public String getCustomerPhone() {
    //     return customerPhone;
    // }

    // public void setCustomerPhone(String customerPhone) {
    //     this.customerPhone = customerPhone;
    // }

    // public String getCustomerId() {
    //     return customerId;
    // }

    // public void setCustomerId(String customerId) {
    //     this.customerId = customerId;
    // }

    // public LocalDateTime getPurchaseDate() {
    //     return purchaseDate;
    // }

    // public void setPurchaseDate(LocalDateTime purchaseDate) {
    //     this.purchaseDate = purchaseDate;
    // }

    // public String getProductName() {
    //     return productName;
    // }

    // public void setProductName(String productName) {
    //     this.productName = productName;
    // }

    // public String getManufacturer() {
    //     return manufacturer;
    // }

    // public void setManufacturer(String manufacturer) {
    //     this.manufacturer = manufacturer;
    // }

    // public LocalDateTime getWarrantyEndDate() {
    //     return warrantyEndDate;
    // }

    // public void setWarrantyEndDate(LocalDateTime warrantyEndDate) {
    //     this.warrantyEndDate = warrantyEndDate;
    // }

    /**
     * Lấy mã sản phẩm
     * @return Mã sản phẩm
     */
    // public String getProductId() {
    //     // Nếu product không null thì lấy productId từ product
    //     if (product != null) {
    //         return product.getProductId();
    //     }
    //     // Nếu không thì trả về trường productId đã được set trực tiếp
    //     return productId;
    // }

    // /**
    //  * Đặt mã sản phẩm
    //  * @param productId Mã sản phẩm
    //  */
    // public void setProductId(String productId) {
    //     this.productId = productId;
    //     // Nếu product đã tồn tại, cũng cập nhật productId của nó
    //     if (product != null) {
    //         product.setProductId(productId);
    //     }
    // }

    // Tính tổng tiền trước giảm giá
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Tính tổng tiền sau giảm giá
    public BigDecimal getTotalAmount() {
        BigDecimal subtotal = getSubtotal();
        BigDecimal discount = getDiscountAmount();
        return subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }

    // Áp dụng khuyến mãi
    public void applyDiscount(Discount discount) {
        if (discount == null || !discount.isValid()) {
            return;
        }

        if (!discount.isApplicableToProduct(this.product)) {
            return;
        }

        BigDecimal calculatedDiscount = discount.calculateDiscount(this.getTotalAmount());
        if (calculatedDiscount.compareTo(BigDecimal.ZERO) > 0) {
            setDiscountAmount(calculatedDiscount);
            // discount.use();
        }
    }

    // Kiểm tra và cập nhật tồn kho khi tạo chi tiết hóa đơn
    public void processWareHouse() {
        if (product == null) {
            throw new IllegalStateException("Chưa có thông tin sản phẩm");
        }

        if (quantity > product.getQuantityInStock()) {
            throw new IllegalStateException("Số lượng vượt quá tồn kho");
        }

        product.decreaseStock(quantity);
    }

    // Hoàn trả tồn kho khi hủy chi tiết hóa đơn
    public void reverseWareHouse() {
        if (product != null) {
            product.increaseStock(quantity);
        }
    }

    // Tạo bảo hành cho sản phẩm
    public void createWarranty() {
        if (product == null || !product.hasWarranty()) {
            return;
        }

        if (warranty == null) {
            warranty = new Warranty();
            // warranty.setProduct(product);
            warranty.setInvoiceDetail(this);
            warranty.initializeWarrantyPeriod();
        }
    }

    /**
     * Kiểm tra liệu chi tiết hóa đơn có thể trả lại với số lượng cho trước không
     * @param quantityToReturn Số lượng muốn trả lại
     * @return true nếu có thể trả với số lượng đó
     */
    public boolean canReturn(int quantityToReturn) {
        // Nếu số lượng trả <= 0, không hợp lệ
        if (quantityToReturn <= 0) return false;
        
        // Mặc định, mọi chi tiết đều có thể trả lại
        return quantityToReturn <= quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != getClass()) return false;
        InvoiceDetail that = (InvoiceDetail) o;
        return Objects.equals(invoiceDetailId, that.invoiceDetailId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceDetailId);
    }

    @Override
    public String toString() {
        return String.format("Chi tiết hóa đơn [Mã: %d, Sản phẩm: %s, Số lượng: %d, Đơn giá: %s]",
            invoiceDetailId,
            product != null ? product.getProductName() : "N/A",
            quantity,
            unitPrice);
    }
}