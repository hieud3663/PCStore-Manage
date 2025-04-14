package com.pcstore.model.enums;

public enum InvoiceStatusEnum {
    PENDING("Chờ xử lý"),
    PAID("Đã thanh toán"),
    CANCELLED("Đã hủy"),
    DELIVERED("Đã giao hàng"),
    PROCESSING("Đang xử lý"),
    COMPLETED("Hoàn thành"),
    RETURNED("Đã trả hàng"),
    REFUNDED("Đã hoàn tiền"),
    FAILED("Thất bại");
    private final String displayName;
    
    InvoiceStatusEnum(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}