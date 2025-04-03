package com.pcstore.service;

import com.pcstore.model.PurchaseOrder;
import com.pcstore.model.Product;
import com.pcstore.repository.impl.PurchaseOrderRepository;
import com.pcstore.repository.Repository;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.repository.impl.ProductRepository;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý logic nghiệp vụ liên quan đến đơn đặt hàng
 */
public class PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;
    
    /**
     * Khởi tạo service với csdl
     * @param connection Kết nối csdl
     */
    public PurchaseOrderService(Connection connection, RepositoryFactory repositoryFactory) {
        // Khởi tạo repository với kết nối csdl
        this.purchaseOrderRepository = new PurchaseOrderRepository(connection, repositoryFactory);
        this.productRepository = new ProductRepository(connection);
    }

    /**
     * Khởi tạo service với repository
     * @param purchaseOrderRepository Repository đơn đặt hàng
     * @param productRepository Repository sản phẩm
     */
    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository, ProductRepository productRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.productRepository = productRepository;
    }
    
    /**
     * Tạo đơn đặt hàng mới
     * @param purchaseOrder Thông tin đơn đặt hàng
     * @return Đơn đặt hàng đã được tạo
     */
    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        // Mặc định trạng thái là "Đang xử lý" nếu không được thiết lập
        if (purchaseOrder.getStatus() == null || purchaseOrder.getStatus().isEmpty()) {
            purchaseOrder.setStatus("Đang xử lý");
        }
        
        return purchaseOrderRepository.add(purchaseOrder);
    }
    
    /**
     * Cập nhật thông tin đơn đặt hàng
     * @param purchaseOrder Thông tin đơn đặt hàng mới
     * @return Đơn đặt hàng đã được cập nhật
     */
    public PurchaseOrder updatePurchaseOrder(PurchaseOrder purchaseOrder) {
        return purchaseOrderRepository.update(purchaseOrder);
    }
    
    /**
     * Xóa đơn đặt hàng theo ID
     * @param purchaseOrderId ID của đơn đặt hàng
     * @return true nếu xóa thành công, ngược lại là false
     */
    public boolean deletePurchaseOrder(String purchaseOrderId) {
        return purchaseOrderRepository.delete(purchaseOrderId);
    }
    
    /**
     * Tìm đơn đặt hàng theo ID
     * @param purchaseOrderId ID của đơn đặt hàng
     * @return Optional chứa đơn đặt hàng nếu tìm thấy
     */
    public Optional<PurchaseOrder> findPurchaseOrderById(String purchaseOrderId) {
        return purchaseOrderRepository.findById(purchaseOrderId);
    }
    
    /**
     * Lấy danh sách tất cả đơn đặt hàng
     * @return Danh sách đơn đặt hàng
     */
    public List<PurchaseOrder> findAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }
    
    /**
     * Tìm đơn đặt hàng theo nhà cung cấp
     * @param supplierId ID của nhà cung cấp
     * @return Danh sách đơn đặt hàng từ nhà cung cấp
     */
    public List<PurchaseOrder> findPurchaseOrdersBySupplier(String supplierId) {
        return purchaseOrderRepository.findBySupplier(supplierId);
    }
    
    /**
     * Tìm đơn đặt hàng theo trạng thái
     * @param status Trạng thái đơn đặt hàng
     * @return Danh sách đơn đặt hàng có trạng thái tương ứng
     */
    public List<PurchaseOrder> findPurchaseOrdersByStatus(String status) {
        return purchaseOrderRepository.findByStatus(status);
    }
    
    /**
     * Tìm đơn đặt hàng trong khoảng thời gian
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách đơn đặt hàng trong khoảng thời gian
     */
    public List<PurchaseOrder> findPurchaseOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return purchaseOrderRepository.findByDateRange(startDate, endDate);
    }
    
    /**
     * Cập nhật trạng thái đơn đặt hàng
     * @param purchaseOrderId ID của đơn đặt hàng
     * @param status Trạng thái mới
     * @return true nếu cập nhật thành công, ngược lại là false
     */

     // Nếu dùng thì bỏ comment ở đây, nhưng phải sửa lại trong PurchaseOrderRepository bởi vì không có order.getOrderDetails() - Hơi rối đấy nha :() ♥♥♥

    // public boolean updatePurchaseOrderStatus(String purchaseOrderId, String status) {
    //     // Nếu trạng thái là "Completed", cập nhật số lượng sản phẩm trong kho
    //     if ("Completed".equals(status)) {
    //         Optional<PurchaseOrder> orderOpt = purchaseOrderRepository.findById(purchaseOrderId);
    //         if (orderOpt.isPresent()) {
    //             PurchaseOrder order = orderOpt.get();
    //             // Cập nhật kho cho từng chi tiết đơn hàng
    //             order.getOrderDetails().forEach(detail -> {
    //                 productRepository.updateStock(detail.getProduct().getProductId(), detail.getQuantity());
    //             });
    //         }
    //     }
        
    //     return purchaseOrderRepository.updateStatus(purchaseOrderId, status);
    // }
    
    
    /**
     * Kiểm tra đơn đặt hàng có tồn tại không
     * @param purchaseOrderId ID của đơn đặt hàng
     * @return true nếu đơn đặt hàng tồn tại, ngược lại là false
     */
    public boolean purchaseOrderExists(Integer purchaseOrderId) {
        return purchaseOrderRepository.exists(purchaseOrderId);
    }
}