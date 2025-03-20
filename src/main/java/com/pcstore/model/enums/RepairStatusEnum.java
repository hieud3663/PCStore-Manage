package com.pcstore.model.enums;

public enum RepairStatusEnum {
    RECEIVED("Received"),
    DIAGNOSING("Diagnosing"),
    WAITING_FOR_PARTS("Waiting for Parts"),
    REPAIRING("Repairing"),
    COMPLETED("Completed"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");
    
    private final String displayName;
    
    RepairStatusEnum(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}