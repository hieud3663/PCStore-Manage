package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import java.math.BigDecimal;

/**
 * Class biểu diễn chi tiết đơn nhập hàng
 */
public class PurchaseOrderDetail extends BaseTimeEntity {
    private Integer purchaseOrderDetailId;
    private PurchaseOrder purchaseOrder;
    private Product product;
    private int quantity;
    private BigDecimal unitCost;
    private Supplier supplier;

    @Override
    public Object getId() {
        return purchaseOrderDetailId;
    }

    public Integer getPurchaseOrderDetailId() {
        return purchaseOrderDetailId;
    }

    public void setPurchaseOrderDetailId(Integer purchaseOrderDetailId) {
        this.purchaseOrderDetailId = purchaseOrderDetailId;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Sản phẩm không được để trống");
        }
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    // @Override
    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        if (!canUpdateQuantity()) {
            throw new IllegalStateException("Không thể cập nhật số lượng của đơn hàng đã hoàn thành hoặc đã hủy");
        }
        this.quantity = quantity;
        if (purchaseOrder != null) {
            purchaseOrder.updateTotalAmount();
        }
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitPrice) {
        if (unitPrice == null) {
            throw new IllegalArgumentException("Đơn giá không được để trống");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Đơn giá phải lớn hơn 0");
        }
        this.unitCost = unitPrice;
        if (purchaseOrder != null) {
            purchaseOrder.updateTotalAmount();
        }
    }
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    public Supplier getSupplier() {
        return supplier;
    }
   
    
    // Phương thức tính thành tiền của chi tiết đơn nhập
    public BigDecimal getSubtotal() {
        if (unitCost == null) {
            return BigDecimal.ZERO;
        }
        return unitCost.multiply(BigDecimal.valueOf(quantity));
    }
    
    // Phương thức cập nhật tồn kho khi nhập hàng
    public void updateStock() {
        if (product == null) {
            throw new IllegalStateException("Không thể cập nhật tồn kho khi chưa có thông tin sản phẩm");
        }
        
        product.increaseStock(quantity);
    }
    
    // Phương thức hoàn tác cập nhật tồn kho
    public void revertStock() {
        if (product == null) {
            throw new IllegalStateException("Không thể hoàn tác tồn kho khi chưa có thông tin sản phẩm");
        }
        if (purchaseOrder == null || !"Completed".equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("Chỉ hoàn tác tồn kho khi đơn nhập hàng đã hoàn thành");
        }
        product.decreaseStock(quantity);
    }
    
    // Kiểm tra xem có thể cập nhật số lượng không
    public boolean canUpdateQuantity() {
        return purchaseOrder == null || 
               !("Completed".equals(purchaseOrder.getStatus()) || 
                 "Cancelled".equals(purchaseOrder.getStatus()));
    }
    
    // Factory method
    public static PurchaseOrderDetail createNew(PurchaseOrder order, Product product, 
                                             int quantity, BigDecimal unitPrice) {
        if (order == null) {
            throw new IllegalArgumentException("Đơn nhập hàng không được để trống");
        }
        if (product == null) {
            throw new IllegalArgumentException("Sản phẩm không được để trống");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Đơn giá phải lớn hơn 0");
        }
        
        PurchaseOrderDetail detail = new PurchaseOrderDetail();
        detail.setPurchaseOrder(order);
        detail.setProduct(product);
        detail.setQuantity(quantity);
        detail.setUnitCost(unitPrice);
        return detail;
    }
}