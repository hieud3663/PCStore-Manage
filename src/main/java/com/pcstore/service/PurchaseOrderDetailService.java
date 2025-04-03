package com.pcstore.service;

import com.pcstore.model.PurchaseOrderDetail;
import com.pcstore.repository.impl.PurchaseOrderDetailRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý logic nghiệp vụ liên quan đến chi tiết đơn đặt hàng
 */
public class PurchaseOrderDetailService {
    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    
    /**
     * Khởi tạo service với repository
     * @param purchaseOrderDetailRepository Repository chi tiết đơn đặt hàng
     */
    public PurchaseOrderDetailService(PurchaseOrderDetailRepository purchaseOrderDetailRepository) {
        this.purchaseOrderDetailRepository = purchaseOrderDetailRepository;
    }
    
    /**
     * Thêm chi tiết đơn đặt hàng mới
     * @param purchaseOrderDetail Thông tin chi tiết đơn đặt hàng
     * @return Chi tiết đơn đặt hàng đã được thêm
     */
    public PurchaseOrderDetail addPurchaseOrderDetail(PurchaseOrderDetail purchaseOrderDetail) {
        return purchaseOrderDetailRepository.add(purchaseOrderDetail);
    }
    
    /**
     * Cập nhật thông tin chi tiết đơn đặt hàng
     * @param purchaseOrderDetail Thông tin chi tiết đơn đặt hàng mới
     * @return Chi tiết đơn đặt hàng đã được cập nhật
     */
    public PurchaseOrderDetail updatePurchaseOrderDetail(PurchaseOrderDetail purchaseOrderDetail) {
        return purchaseOrderDetailRepository.update(purchaseOrderDetail);
    }
    
    /**
     * Xóa chi tiết đơn đặt hàng theo ID
     * @param purchaseOrderDetailId ID của chi tiết đơn đặt hàng
     * @return true nếu xóa thành công, ngược lại là false
     */
    public boolean deletePurchaseOrderDetail(Integer purchaseOrderDetailId) {
        return purchaseOrderDetailRepository.delete(purchaseOrderDetailId);
    }
    
    /**
     * Tìm chi tiết đơn đặt hàng theo ID
     * @param purchaseOrderDetailId ID của chi tiết đơn đặt hàng
     * @return Optional chứa chi tiết đơn đặt hàng nếu tìm thấy
     */
    public Optional<PurchaseOrderDetail> findPurchaseOrderDetailById(Integer purchaseOrderDetailId) {
        return purchaseOrderDetailRepository.findById(purchaseOrderDetailId);
    }
    
    /**
     * Lấy danh sách tất cả chi tiết đơn đặt hàng
     * @return Danh sách chi tiết đơn đặt hàng
     */
    public List<PurchaseOrderDetail> findAllPurchaseOrderDetails() {
        return purchaseOrderDetailRepository.findAll();
    }
    
    /**
     * Tìm chi tiết đơn đặt hàng theo mã đơn đặt hàng
     * @param purchaseOrderId Mã đơn đặt hàng
     * @return Danh sách chi tiết đơn đặt hàng
     */
    public List<PurchaseOrderDetail> findPurchaseOrderDetailsByOrderId(String purchaseOrderId) {
        return purchaseOrderDetailRepository.findByPurchaseOrderId(purchaseOrderId);
    }
    
    /**
     * Tìm chi tiết đơn đặt hàng theo mã sản phẩm
     * @param productId Mã sản phẩm
     * @return Danh sách chi tiết đơn đặt hàng
     */
    public List<PurchaseOrderDetail> findPurchaseOrderDetailsByProductId(String productId) {
        return purchaseOrderDetailRepository.findByProductId(productId);
    }
    
    /**
     * Xóa tất cả chi tiết đơn đặt hàng theo mã đơn đặt hàng
     * @param purchaseOrderId Mã đơn đặt hàng
     */
    public void deletePurchaseOrderDetailsByOrderId(String purchaseOrderId) {
        purchaseOrderDetailRepository.deleteByPurchaseOrderId(purchaseOrderId);
    }
    
    /**
     * Kiểm tra chi tiết đơn đặt hàng có tồn tại không
     * @param purchaseOrderDetailId ID của chi tiết đơn đặt hàng
     * @return true nếu chi tiết đơn đặt hàng tồn tại, ngược lại là false
     */
    public boolean purchaseOrderDetailExists(Integer purchaseOrderDetailId) {
        return purchaseOrderDetailRepository.exists(purchaseOrderDetailId);
    }
    
    /**
     * Tính tổng giá trị của một đơn đặt hàng
     * @param purchaseOrderId Mã đơn đặt hàng
     * @return Tổng giá trị
     */
       public BigDecimal calculatePurchaseOrderTotal(String purchaseOrderId) {
        List<PurchaseOrderDetail> details = purchaseOrderDetailRepository.findByPurchaseOrderId(purchaseOrderId);
        return details.stream()
                .map(detail -> BigDecimal.valueOf(detail.getUnitPrice().doubleValue() * detail.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}