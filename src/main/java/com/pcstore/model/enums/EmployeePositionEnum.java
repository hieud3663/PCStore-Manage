package com.pcstore.model.enums;

public enum EmployeePositionEnum {
    MANAGER("Manager"),
    SALES("Sales"),
    STOCK_KEEPER("Stock Keeper"),
    ADMIN("Admin");
    
    private final String displayName;
    
    EmployeePositionEnum(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}