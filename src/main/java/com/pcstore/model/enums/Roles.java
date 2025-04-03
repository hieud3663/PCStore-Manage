package com.pcstore.model.enums;

public enum Roles {
    ADMIN("Admin"),
    MANAGER("Manager"),
    SALES("Sales"),
    STOCK("Stock"),
    REPAIR("Repair");

    private final String roleName;

    Roles(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}