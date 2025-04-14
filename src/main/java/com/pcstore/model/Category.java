package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import com.pcstore.utils.ErrorMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * Class biểu diễn danh mục sản phẩm
 */
public class Category extends BaseTimeEntity {
    private String categoryId;
    private String categoryName;
    private String description;

    public Category(String categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
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
            throw new IllegalArgumentException(ErrorMessage.CATEGORY_NAME_EMPTY);
        }
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}