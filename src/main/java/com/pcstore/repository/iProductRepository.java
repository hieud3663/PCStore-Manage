package com.pcstore.repository;

import com.pcstore.model.Product;
import java.util.List;
import java.util.Optional;

public interface iProductRepository extends iRepository<Product, String> {
    List<Product> findByCategory(String categoryId);
    List<Product> findBySupplier(String supplierId);
    List<Product> findByName(String name);
    List<Product> findByPriceRange(double minPrice, double maxPrice);
    boolean updateStock(String productId, int quantity);
}