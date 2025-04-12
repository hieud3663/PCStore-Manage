package com.pcstore.model.enums;

public enum RepairEnum {
    RECEIVED("Received"),
    DIAGNOSING("Diagnosing"),
    WAITING_FOR_PARTS("Waiting for Parts"),
    REPAIRING("Repairing"),
    COMPLETED("Completed"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    private final String status;

    RepairEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Chuyển đổi chuỗi trạng thái thành enum
     * 
     * @param status Chuỗi trạng thái
     * @return RepairEnum tương ứng
     * @throws IllegalArgumentException nếu không tìm thấy trạng thái phù hợp
     */
    public static RepairEnum fromString(String status) {
        for (RepairEnum repairEnum : RepairEnum.values()) {
            if (repairEnum.getStatus().equalsIgnoreCase(status)) {
                return repairEnum;
            }
        }
        
        // Thử so sánh trực tiếp với tên enum
        try {
            return RepairEnum.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ: " + status);
        }
    }

    @Override
    public String toString() {
        return status;
    }
}
