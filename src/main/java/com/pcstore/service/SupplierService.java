package com.pcstore.service;

import com.pcstore.repository.Repository;
import com.pcstore.repository.impl.SupplierRepository;
import com.pcstore.model.Supplier;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý logic nghiệp vụ liên quan đến nhà cung cấp
 */
public class SupplierService {
    private final SupplierRepository supplierRepository;
    
    /**
     * Khởi tạo service với repository
     * @param connection Kết nối đến database
     */
    public SupplierService(Connection connection) {
        this.supplierRepository = new SupplierRepository(connection);
    }
    
    /**
     * Khởi tạo service với repository đã có
     * @param supplierRepository Repository nhà cung cấp
     */
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }
    
    /**
     * Thêm nhà cung cấp mới
     * @param supplier Thông tin nhà cung cấp
     * @return Nhà cung cấp đã được thêm
     */
    public Supplier addSupplier(Supplier supplier) {
        // Kiểm tra tồn tại
        if (supplier.getSupplierId() != null && supplierRepository.exists(supplier.getSupplierId())) {
            throw new IllegalArgumentException("Nhà cung cấp với mã " + supplier.getSupplierId() + " đã tồn tại");
        }
        
        // Kiểm tra email đã tồn tại chưa
        if (supplier.getEmail() != null) {
            Optional<Supplier> existingByEmail = supplierRepository.findByEmail(supplier.getEmail());
            if (existingByEmail.isPresent() && !existingByEmail.get().getSupplierId().equals(supplier.getSupplierId())) {
                throw new IllegalArgumentException("Email đã được sử dụng bởi nhà cung cấp khác");
            }
        }
        
        // Kiểm tra số điện thoại đã tồn tại chưa
        if (supplier.getPhoneNumber() != null) {
            Optional<Supplier> existingByPhone = supplierRepository.findByPhoneNumber(supplier.getPhoneNumber());
            if (existingByPhone.isPresent() && !existingByPhone.get().getSupplierId().equals(supplier.getSupplierId())) {
                throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi nhà cung cấp khác");
            }
        }
        
        // Tạo mã nhà cung cấp nếu chưa có
        if (supplier.getSupplierId() == null || supplier.getSupplierId().trim().isEmpty()) {
            supplier.setSupplierId(supplierRepository.generateSupplierId());
        }
        
        return supplierRepository.add(supplier);
    }
    
    /**
     * Cập nhật thông tin nhà cung cấp
     * @param supplier Thông tin nhà cung cấp mới
     * @return Nhà cung cấp đã được cập nhật
     */
    public Supplier updateSupplier(Supplier supplier) {
        // Kiểm tra tồn tại
        if (!supplierRepository.exists(supplier.getSupplierId())) {
            throw new IllegalArgumentException("Nhà cung cấp với mã " + supplier.getSupplierId() + " không tồn tại");
        }
        
        // Kiểm tra email đã tồn tại chưa
        if (supplier.getEmail() != null) {
            Optional<Supplier> existingByEmail = supplierRepository.findByEmail(supplier.getEmail());
            if (existingByEmail.isPresent() && !existingByEmail.get().getSupplierId().equals(supplier.getSupplierId())) {
                throw new IllegalArgumentException("Email đã được sử dụng bởi nhà cung cấp khác");
            }
        }
        
        // Kiểm tra số điện thoại đã tồn tại chưa
        if (supplier.getPhoneNumber() != null) {
            Optional<Supplier> existingByPhone = supplierRepository.findByPhoneNumber(supplier.getPhoneNumber());
            if (existingByPhone.isPresent() && !existingByPhone.get().getSupplierId().equals(supplier.getSupplierId())) {
                throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi nhà cung cấp khác");
            }
        }
        
        return supplierRepository.update(supplier);
    }
    
    /**
     * Xóa nhà cung cấp
     * @param supplierId Mã nhà cung cấp
     * @return true nếu xóa thành công
     */
    public boolean deleteSupplier(String supplierId) {
        // Kiểm tra tồn tại
        Optional<Supplier> supplier = supplierRepository.findById(supplierId);
        if (!supplier.isPresent()) {
            throw new IllegalArgumentException("Nhà cung cấp với mã " + supplierId + " không tồn tại");
        }
                
        // Kiểm tra xem có sản phẩm liên quan không
        int productCount = supplierRepository.getProductCountBySupplier(supplierId);
        if (productCount > 0) {
            throw new IllegalStateException("Không thể xóa nhà cung cấp đang có " + productCount + " sản phẩm");
        }
        
        return supplierRepository.delete(supplierId);
    }
    
    /**
     * Tìm nhà cung cấp theo mã
     * @param supplierId Mã nhà cung cấp
     * @return Nhà cung cấp nếu tìm thấy
     */
    public Optional<Supplier> findSupplierById(String supplierId) {
        return supplierRepository.findById(supplierId);
    }
    
    /**
     * Tìm nhà cung cấp theo tên
     * @param name Tên nhà cung cấp
     * @return Danh sách nhà cung cấp phù hợp
     */
    public List<Supplier> findSuppliersByName(String name) {
        return supplierRepository.findByName(name);
    }
    
    /**
     * Tìm nhà cung cấp theo email
     * @param email Email nhà cung cấp
     * @return Nhà cung cấp nếu tìm thấy
     */
    public Optional<Supplier> findSupplierByEmail(String email) {
        return supplierRepository.findByEmail(email);
    }
    
    /**
     * Tìm nhà cung cấp theo số điện thoại
     * @param phoneNumber Số điện thoại nhà cung cấp
     * @return Nhà cung cấp nếu tìm thấy
     */
    public Optional<Supplier> findSupplierByPhoneNumber(String phoneNumber) {
        return supplierRepository.findByPhoneNumber(phoneNumber);
    }
    
    /**
     * Lấy danh sách tất cả nhà cung cấp
     * @return Danh sách nhà cung cấp
     */
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }
    
    /**
     * Tạo mã nhà cung cấp mới
     * @return Mã nhà cung cấp mới
     */
    public String generateSupplierId() {
        return supplierRepository.generateSupplierId();
    }
    
    /**
     * Kiểm tra nhà cung cấp tồn tại
     * @param supplierId Mã nhà cung cấp
     * @return true nếu tồn tại
     */
    public boolean supplierExists(String supplierId) {
        return supplierRepository.exists(supplierId);
    }
    
    /**
     * Lấy số lượng sản phẩm của nhà cung cấp
     * @param supplierId Mã nhà cung cấp
     * @return Số lượng sản phẩm
     */
    public int getProductCountBySupplier(String supplierId) {
        return supplierRepository.getProductCountBySupplier(supplierId);
    }
}