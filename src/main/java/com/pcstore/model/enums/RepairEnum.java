package com.pcstore.model.enums;

public enum RepairEnum {
    CANCELLED("Cancelled"),
    DELIVERED("Delivered"),
    COMPLETED("Completed"),
    REPAIRING("Repairing"),
    WAITING_FOR_PARTS("Waiting for Parts"),
    DIAGNOSING("Diagnosing"),
    RECEIVED("Received");

    private final String status;

    RepairEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}
