package com.pcstore.service;

import com.pcstore.model.Product;
import com.pcstore.repository.impl.ProductRepository;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý logic nghiệp vụ liên quan đến sản phẩm
 */
public class ProductService {
    private final ProductRepository productRepository;
    
    /**
     * Khởi tạo service với kết nối cơ sở dữ liệu
     * @param connection Kết nối cơ sở dữ liệu
     */
    public ProductService(Connection connection) {
        this.productRepository = new ProductRepository(connection);
    }

    /**
     * Khởi tạo service với repository
     * @param productRepository Repository sản phẩm
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * Thêm sản phẩm mới
     * @param product Thông tin sản phẩm
     * @return Sản phẩm đã được thêm
     */
    public Product addProduct(Product product) {
        return productRepository.add(product);
    }
    
    /**
     * Cập nhật thông tin sản phẩm
     * @param product Thông tin sản phẩm mới
     * @return Sản phẩm đã được cập nhật
     */
    public Product updateProduct(Product product) {
        return productRepository.update(product);
    }
    
    /**
     * Xóa sản phẩm theo ID
     * @param productId ID của sản phẩm
     * @return true nếu xóa thành công, ngược lại là false
     */
    public boolean deleteProduct(String productId) {
        return productRepository.delete(productId);
    }
    
    /**
     * Tìm sản phẩm theo ID
     * @param productId ID của sản phẩm
     * @return Optional chứa sản phẩm nếu tìm thấy
     */
    public Optional<Product> findProductById(String productId) {
        return productRepository.findById(productId);
    }
    
    /**
     * Lấy danh sách tất cả sản phẩm
     * @return Danh sách sản phẩm
     */
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
    
    /**
     * Kiểm tra sản phẩm có tồn tại không
     * @param productId ID của sản phẩm
     * @return true nếu sản phẩm tồn tại, ngược lại là false
     */
    public boolean productExists(String productId) {
        return productRepository.exists(productId);
    }
    
    /**
     * Tìm sản phẩm theo danh mục
     * @param categoryId ID của danh mục
     * @return Danh sách sản phẩm thuộc danh mục
     */
    public List<Product> findProductsByCategory(Integer categoryId) {
        return productRepository.findByCategory(categoryId);
    }
    
    /**
     * Tìm sản phẩm theo nhà cung cấp
     * @param supplierId ID của nhà cung cấp
     * @return Danh sách sản phẩm từ nhà cung cấp
     */
    public List<Product> findProductsBySupplier(String supplierId) {
        return productRepository.findBySupplier(supplierId);
    }
    
    /**
     * Tìm sản phẩm theo tên
     * @param name Tên sản phẩm cần tìm
     * @return Danh sách sản phẩm có tên trùng khớp
     */
    public List<Product> findProductsByName(String name) {
        return productRepository.findByName(name);
    }
    
    /**
     * Tìm sản phẩm trong khoảng giá
     * @param minPrice Giá tối thiểu
     * @param maxPrice Giá tối đa
     * @return Danh sách sản phẩm trong khoảng giá
     */
    // Cần dùng phương thức này nếu có yêu cầu tìm kiếm theo khoảng giá
    // public List<Product> findProductsByPriceRange(double minPrice, double maxPrice) {
    //     return productRepository.findByPriceRange(minPrice, maxPrice);
    // }
    
    /**
     * Cập nhật số lượng tồn kho của sản phẩm
     * @param productId ID của sản phẩm
     * @param quantity Số lượng cần cập nhật (có thể là số âm để giảm)
     * @return true nếu cập nhật thành công, ngược lại là false
     */
    public boolean updateProductStock(String productId, int quantity) {
        return productRepository.updateStockQuantity(productId, quantity);
    }
    
    /**
     * Kiểm tra số lượng tồn kho của sản phẩm có đủ không
     * @param productId ID của sản phẩm
     * @param quantity Số lượng cần kiểm tra
     * @return true nếu số lượng tồn kho đủ, ngược lại là false
     */
    public boolean checkStockAvailability(String productId, int quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            return product.getStockQuantity() >= quantity;
        }
        return false;
    }
}