package com.pcstore.model.enums;

public enum DiscountTypeEnum {
    PERCENTAGE("Percentage"),
    FIXED_AMOUNT("Fixed Amount");
    
    private final String displayName;
    
    DiscountTypeEnum(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}