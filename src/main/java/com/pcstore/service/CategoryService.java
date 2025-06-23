package com.pcstore.service;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import com.pcstore.model.Category;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.repository.impl.CategoryRepository;

/**
 * Service xử lý logic nghiệp vụ liên quan đến danh mục sản phẩm
 */
public class CategoryService {
    private final CategoryRepository categoryRepository;

    /**
     * Khởi tạo service với connection
     * @param connection Kết nối đến database
     */
    public CategoryService(Connection connection) {
        try {
            this.categoryRepository = RepositoryFactory.getInstance(connection)
                .getCategoryRepository();
        } catch (Exception e) {
            System.err.println("Lỗi khởi tạo CategoryService: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khởi tạo CategoryService", e);
        }
    }

    /**
     * Lấy tất cả danh mục
     * @return Danh sách danh mục
     */
    public List<Category> getAllCategories() {
        try {
            return categoryRepository.findAll();
        } catch (Exception e) {
            System.err.println("Lỗi lấy danh sách danh mục: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Tìm danh mục theo ID
     * @param categoryId ID danh mục
     * @return Optional chứa danh mục
     */
    public Optional<Category> findCategoryById(String categoryId) {
        try {
            return categoryRepository.findById(categoryId);
        } catch (Exception e) {
            System.err.println("Lỗi tìm danh mục theo ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Tìm danh mục theo tên
     * @param categoryName Tên danh mục
     * @return Optional chứa danh mục
     */
    public Optional<Category> findCategoryByName(String categoryName) {
        try {
            return categoryRepository.findByName(categoryName);
        } catch (Exception e) {
            System.err.println("Lỗi tìm danh mục theo tên: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Lấy danh mục đang hoạt động
     * @return Danh sách danh mục đang hoạt động
     */
    public List<Category> getActiveCategories() {
        try {
            return categoryRepository.findAll(); // Tạm thời trả về tất cả
        } catch (Exception e) {
            System.err.println("Lỗi lấy danh mục đang hoạt động: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Thêm danh mục mới
     * @param category Danh mục cần thêm
     * @return Danh mục đã được thêm
     */
    public Category addCategory(Category category) {
        try {
            return categoryRepository.add(category);
        } catch (Exception e) {
            System.err.println("Lỗi thêm danh mục: " + e.getMessage());
            throw new RuntimeException("Không thể thêm danh mục", e);
        }
    }

    /**
     * Cập nhật thông tin danh mục
     * @param category Danh mục cần cập nhật
     * @return Danh mục đã được cập nhật
     */
    public Category updateCategory(Category category) {
        try {
            return categoryRepository.update(category);
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật danh mục: " + e.getMessage());
            throw new RuntimeException("Không thể cập nhật danh mục", e);
        }
    }

    /**
     * Xóa danh mục
     * @param categoryId ID danh mục cần xóa
     * @return true nếu xóa thành công
     */
    public boolean deleteCategory(String categoryId) {
        try {
            return categoryRepository.delete(categoryId);
        } catch (Exception e) {
            System.err.println("Lỗi xóa danh mục: " + e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra danh mục có tồn tại không
     * @param categoryId ID danh mục
     * @return true nếu tồn tại
     */
    public boolean existsCategory(String categoryId) {
        try {
            return categoryRepository.exists(categoryId);
        } catch (Exception e) {
            System.err.println("Lỗi kiểm tra tồn tại danh mục: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tìm kiếm danh mục theo từ khóa
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách danh mục
     */
    public List<Category> searchCategories(String keyword) {
        try {
            return categoryRepository.searchByKeyword(keyword);
        } catch (Exception e) {
            System.err.println("Lỗi tìm kiếm danh mục: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Sinh mã danh mục mới tự động (ví dụ: DM001, DM002, ...)
     * @return mã danh mục mới
     */
    public String generateCategoryId() {
        List<Category> categories = getAllCategories();
        int max = 0;
        for (Category c : categories) {
            String id = c.getCategoryId();
            if (id != null && id.startsWith("DM")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("DM%03d", max + 1);
    }

    /**
     * Kiểm tra danh mục có sản phẩm liên kết không
     * @param categoryId ID danh mục
     * @return true nếu có sản phẩm liên kết
     */
    public boolean hasProducts(String categoryId) {
        try {
            return categoryRepository.hasProducts(categoryId);
        } catch (Exception e) {
            System.err.println("Lỗi kiểm tra sản phẩm liên kết: " + e.getMessage());
            return false;
        }
    }

    // /**
    //  * Đếm số lượng danh mục
    //  * @return Số lượng danh mục
    //  */
    // public long countCategories() {
    //     try {
    //         return categoryRepository.count();
    //     } catch (Exception e) {
    //         System.err.println("Lỗi đếm danh mục: " + e.getMessage());
    //         return 0;
    //     }
    // }
}