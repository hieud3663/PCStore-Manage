package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import com.pcstore.utils.ErrorMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * Class biểu diễn danh mục sản phẩm
 */
public class Category extends BaseTimeEntity {
    private Integer categoryId;
    private String categoryName;
    private String description;
    private Category parentCategory;
    private List<Category> subCategories = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private boolean isActive;

    @Override
    public Object getId() {
        return categoryId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        // Tránh tự tham chiếu
        if (parentCategory == this) {
            throw new IllegalArgumentException(ErrorMessage.CATEGORY_SELF_REFERENCE);
        }
        // Tránh tạo vòng lặp trong cây phân cấp
        if (parentCategory != null && isAncestor(parentCategory)) {
            throw new IllegalArgumentException(ErrorMessage.CATEGORY_CIRCULAR_REFERENCE);
        }
        
        // Cập nhật quan hệ hai chiều
        if (this.parentCategory != null) {
            this.parentCategory.subCategories.remove(this);
        }
        this.parentCategory = parentCategory;
        if (parentCategory != null) {
            parentCategory.subCategories.add(this);
        }
    }

    public List<Category> getSubCategories() {
        return subCategories;
    }

    protected void setSubCategories(List<Category> subCategories) {
        this.subCategories = subCategories != null ? subCategories : new ArrayList<>();
    }

    public void addSubCategory(Category subCategory) {
        if (subCategory == null) {
            throw new IllegalArgumentException(ErrorMessage.SUBCATEGORY_NULL);
        }
        if (!this.subCategories.contains(subCategory)) {
            this.subCategories.add(subCategory);
            subCategory.setParentCategory(this);
        }
    }

    public void removeSubCategory(Category subCategory) {
        if (this.subCategories.remove(subCategory)) {
            subCategory.setParentCategory(null);
        }
    }

    public List<Product> getProducts() {
        return products;
    }

    protected void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_NULL);
        }
        if (!this.products.contains(product)) {
            this.products.add(product);
            product.setCategory(this);
        }
    }

    public void removeProduct(Product product) {
        if (this.products.remove(product)) {
            product.setCategory(null);
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    // Kiểm tra một danh mục có phải là tổ tiên không
    private boolean isAncestor(Category category) {
        Category current = this.parentCategory;
        while (current != null) {
            if (current == category) {
                return true;
            }
            current = current.getParentCategory();
        }
        return false;
    }

    // Lấy danh sách tất cả sản phẩm (bao gồm từ các danh mục con)
    public List<Product> getAllProducts() {
        List<Product> allProducts = new ArrayList<>(this.products);
        for (Category subCategory : subCategories) {
            allProducts.addAll(subCategory.getAllProducts());
        }
        return allProducts;
    }

    // Lấy đường dẫn đầy đủ của danh mục
    public String getFullPath() {
        if (parentCategory == null) {
            return categoryName;
        }
        return parentCategory.getFullPath() + " > " + categoryName;
    }

    // Kiểm tra có phải danh mục gốc không
    public boolean isRoot() {
        return parentCategory == null;
    }

    // Kiểm tra có phải danh mục lá không
    public boolean isLeaf() {
        return subCategories.isEmpty();
    }

    // Factory method để tạo danh mục mới
    public static Category createNew(String categoryName, String description) {
        Category category = new Category();
        category.setCategoryName(categoryName);
        category.setDescription(description);
        category.setActive(true);
        return category;
    }

    // Factory method để tạo danh mục con
    public static Category createSubCategory(Category parent, String categoryName, 
                                          String description) {
        if (parent == null) {
            throw new IllegalArgumentException(ErrorMessage.PARENT_CATEGORY_NULL);
        }
        Category category = createNew(categoryName, description);
        category.setParentCategory(parent);
        return category;
    }
}