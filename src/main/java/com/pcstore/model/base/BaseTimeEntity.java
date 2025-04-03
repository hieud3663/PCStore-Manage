package com.pcstore.model.base;

import java.time.LocalDateTime;

/**
 * Abstract class cơ sở cho các entity có thông tin thời gian
 */
public abstract class BaseTimeEntity implements Entity {
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Method để cập nhật thời gian khi entity được sửa
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}