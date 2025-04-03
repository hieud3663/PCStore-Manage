package com.pcstore.model.enums;

public enum PaymentMethodEnum {
    CASH("Tiền mặt"),
    CREDIT_CARD("Thẻ tín dụng"),
    BANK_TRANSFER("Chuyển khoản"),
    E_WALLET("Ví điện tử");
    
    private final String displayName;
    
    PaymentMethodEnum(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}