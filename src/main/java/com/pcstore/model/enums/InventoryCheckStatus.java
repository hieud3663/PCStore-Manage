package com.pcstore.model.enums;

import java.util.ResourceBundle;

/**
 * Enum định nghĩa các trạng thái của phiếu kiểm kê
 */
public enum InventoryCheckStatus {
    ALL("ALL", "status.all"),
    DRAFT("DRAFT", "inventory.check.status.draft"),
    IN_PROGRESS("IN_PROGRESS", "inventory.check.status.in_progress"),
    COMPLETED("COMPLETED", "inventory.check.status.completed"),
    CANCELLED("CANCELLED", "inventory.check.status.cancelled");

    private final String dbValue;
    private final String resourceKey;

    InventoryCheckStatus(String dbValue, String resourceKey) {
        this.dbValue = dbValue;
        this.resourceKey = resourceKey;
    }

    public String getDbValue() {
        return dbValue;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public String getDisplayText(ResourceBundle bundle) {
        return bundle.getString(resourceKey);
    }

    public static InventoryCheckStatus fromDbValue(String dbValue) {
        if (dbValue == null) return null;

        for (InventoryCheckStatus status : values()) {
            if (status.dbValue.equalsIgnoreCase(dbValue)) {
                return status;
            }
        }
        return null;
    }

    public static InventoryCheckStatus fromDisplayText(String displayText, ResourceBundle bundle) {
        if (displayText == null) return null;

        for (InventoryCheckStatus status : values()) {
            if (status.getDisplayText(bundle).equals(displayText)) {
                return status;
            }
        }
        return null;
    }


    public boolean canDelete() {
        return this == DRAFT || this == CANCELLED;
    }


    public boolean canEdit() {
        return this == DRAFT || this == IN_PROGRESS;
    }

    public boolean canInputActualQuantity() {
        return this == DRAFT || this == IN_PROGRESS;
    }
}