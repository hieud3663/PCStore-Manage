package com.pcstore.repository;

import com.pcstore.model.Category;
import java.util.List;
import java.util.Optional;

public interface iCategoryRepository extends iRepository<Category, Integer> {
    List<Category> findByName(String name);

    public Category addCategory(Category category);

    public Category updateCategory(Category category);

    public boolean deleteCategory(String categoryId);

    public Optional<Category> findById(String categoryId);

    public boolean exists(String categoryId);
}