package com.pcstore.service;

import com.pcstore.model.Category;
import com.pcstore.repository.iCategoryRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service xử lý logic nghiệp vụ liên quan đến danh mục sản phẩm
 */
public class CategoryService {
    private final iCategoryRepository categoryRepository;
    
    /**
     * Khởi tạo service với repository
     * @param categoryRepository Repository danh mục
     */
    public CategoryService(iCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    /**
     * Thêm danh mục mới
     * @param category Thông tin danh mục
     * @return Danh mục đã được thêm
     */
    public Category addCategory(Category category) {
        return categoryRepository.addCategory(category);
    }
    
    /**
     * Cập nhật thông tin danh mục
     * @param category Thông tin danh mục mới
     * @return Danh mục đã được cập nhật
     */
    public Category updateCategory(Category category) {
        return categoryRepository.updateCategory(category);
    }
    
    /**
     * Xóa danh mục theo ID
     * @param categoryId ID của danh mục
     * @return true nếu xóa thành công, ngược lại là false
     */
    public boolean deleteCategory(String categoryId) {
        return categoryRepository.deleteCategory(categoryId);
    }
    
    /**
     * Tìm danh mục theo ID
     * @param categoryId ID của danh mục
     * @return Optional chứa danh mục nếu tìm thấy
     */
    public Optional<Category> findCategoryById(String categoryId) {
        return categoryRepository.findById(categoryId);
    }
    
    /**
     * Lấy danh sách tất cả danh mục
     * @return Danh sách danh mục
     */
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }
    
    /**
     * Tìm danh mục theo tên
     * @param name Tên danh mục
     * @return Danh sách danh mục có tên tương ứng
     */
    public List<Category> findCategoriesByName(String name) {
        return categoryRepository.findByName(name);
    }
    
    /**
     * Kiểm tra danh mục có tồn tại không
     * @param categoryId ID của danh mục
     * @return true nếu danh mục tồn tại, ngược lại là false
     */
    public boolean categoryExists(String categoryId) {
        return categoryRepository.exists(categoryId);
    }
    
    /**
     * Lấy số lượng sản phẩm trong danh mục
     * @param categoryId ID của danh mục
     * @return Số lượng sản phẩm
     */
    public int getProductCountInCategory(String categoryId) {
        // Đây chỉ là một triển khai giả định, cần triển khai thực tế nếu cần
        return 0; // TODO: Triển khai phương thức đếm số lượng sản phẩm trong danh mục
    }
}