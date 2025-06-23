package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import com.pcstore.utils.ErrorMessage;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Class biểu diễn danh mục sản phẩm
 */
public class Category extends BaseTimeEntity {
    private String categoryId;
    private String categoryName;
    private String description;
    private String status; // Thêm dòng này

    // Thêm các constructor có status
    public Category(String categoryId, String categoryName, String description, String status) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.status = status;
    }

    public Category(String id, String name, String desc, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.categoryId = id;
        this.categoryName = name;
        this.description = desc;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Category(String categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Category(String id, String name, String desc, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.categoryId = id;
        this.categoryName = name;
        this.description = desc;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Category(String categoryId, String categoryName, String description) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
    }

    public Category() {
    }

    @Override
    public Object getId() {
        return categoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String generatedId) {
        this.categoryId = generatedId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.CATEGORY_NAME_EMPTY.toString());
        }
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}