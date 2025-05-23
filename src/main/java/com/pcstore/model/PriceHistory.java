package com.pcstore.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pcstore.model.base.BaseTimeEntity;

public class PriceHistory extends BaseTimeEntity {
    private Integer priceHistoryId;
    private String productId;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private BigDecimal oldCostPrice;
    private BigDecimal newCostPrice;
    private LocalDateTime changedDate;
    private String employeeId;
    private String note;
    
    // Constructor
    public PriceHistory() {
        this.changedDate = LocalDateTime.now();
    }
    
    public PriceHistory(String productId, BigDecimal oldPrice, BigDecimal newPrice, 
                       BigDecimal oldCostPrice, BigDecimal newCostPrice,
                       String employeeId, String note) {
        this.productId = productId;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.oldCostPrice = oldCostPrice;
        this.newCostPrice = newCostPrice;
        this.employeeId = employeeId;
        this.note = note;
        this.changedDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getPriceHistoryId() {
        return priceHistoryId;
    }
    
    public void setPriceHistoryId(Integer priceHistoryId) {
        this.priceHistoryId = priceHistoryId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public BigDecimal getOldPrice() {
        return oldPrice;
    }
    
    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }
    
    public BigDecimal getNewPrice() {
        return newPrice;
    }
    
    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }
    
    public BigDecimal getOldCostPrice() {
        return oldCostPrice;
    }
    
    public void setOldCostPrice(BigDecimal oldCostPrice) {
        this.oldCostPrice = oldCostPrice;
    }
    
    public BigDecimal getNewCostPrice() {
        return newCostPrice;
    }
    
    public void setNewCostPrice(BigDecimal newCostPrice) {
        this.newCostPrice = newCostPrice;
    }
    
    public LocalDateTime getChangedDate() {
        return changedDate;
    }
    
    public void setChangedDate(LocalDateTime changedDate) {
        this.changedDate = changedDate;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public Object getId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getId'");
    }
}