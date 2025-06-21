package com.pcstore.model;

import java.math.BigDecimal;

import com.pcstore.model.base.BaseTimeEntity;

public class InventoryCheckDetail extends BaseTimeEntity {
    private Integer detailId;
    private InventoryCheck inventoryCheck;
    private Product product;
    private Integer systemQuantity;  // Số lượng theo hệ thống
    private Integer actualQuantity;  // Số lượng thực đếm
    private Integer discrepancy;     // Chênh lệch (actual - system)
    private String reason;           // Lý do chênh lệch
    private BigDecimal lossValue;    // Giá trị thất thoát (nếu có)

    
    @Override
    public Integer getId() {
        return detailId;
    }
    public void setDetailId(Integer detailId) {
        this.detailId = detailId;
    }
    public InventoryCheck getInventoryCheck() {
        return inventoryCheck;
    }
    public void setInventoryCheck(InventoryCheck inventoryCheck) {
        this.inventoryCheck = inventoryCheck;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public Integer getSystemQuantity() {
        return systemQuantity;
    }
    public void setSystemQuantity(Integer systemQuantity) {
        this.systemQuantity = systemQuantity;
    }
    public Integer getActualQuantity() {
        return actualQuantity;
    }
    public void setActualQuantity(Integer actualQuantity) {
        this.actualQuantity = actualQuantity;
    }
    public Integer getDiscrepancy() {
        return discrepancy;
    }
    public void setDiscrepancy(Integer discrepancy) {
        this.discrepancy = discrepancy;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public BigDecimal getLossValue() {
        return lossValue;
    }
    public void setLossValue(BigDecimal lossValue) {
        this.lossValue = lossValue;
    }
    
}