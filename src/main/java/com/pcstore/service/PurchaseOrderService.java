package com.pcstore.service;

import com.pcstore.model.PurchaseOrder;
import com.pcstore.model.Product;
import com.pcstore.model.PriceHistory;
import com.pcstore.model.PurchaseOrderDetail;
import com.pcstore.repository.impl.PurchaseOrderRepository;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.repository.impl.PriceHistoryRepository;
import com.pcstore.repository.impl.PurchaseOrderDetailRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý logic nghiệp vụ liên quan đến đơn đặt hàng
 */
public class PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    
    /**
     * Khởi tạo service với csdl
     * @param connection Kết nối csdl
     */
    public PurchaseOrderService(Connection connection, RepositoryFactory repositoryFactory) {
        // Khởi tạo repository với kết nối csdl
        this.purchaseOrderRepository = new PurchaseOrderRepository(connection, repositoryFactory);
        this.productRepository = new ProductRepository(connection);
        this.priceHistoryRepository = new PriceHistoryRepository();
        this.purchaseOrderDetailRepository = new PurchaseOrderDetailRepository(connection, repositoryFactory);
    }

    /**
     * Khởi tạo service với repository
     * @param purchaseOrderRepository Repository đơn đặt hàng
     * @param productRepository Repository sản phẩm
     */
    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository, ProductRepository productRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.productRepository = productRepository;
        this.priceHistoryRepository = new PriceHistoryRepository();
        this.purchaseOrderDetailRepository = new PurchaseOrderDetailRepository();
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
    public boolean completePurchaseOrder(String purchaseOrderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(purchaseOrderId).orElse(null);
        if (order == null) {
            return false;
        }
        
        // Lấy chi tiết đơn hàng
        List<PurchaseOrderDetail> details = purchaseOrderDetailRepository.findByPurchaseOrderId(purchaseOrderId);
        
        // Cập nhật trạng thái đơn hàng
        order.setStatus("Completed");
        PurchaseOrder updateResult = purchaseOrderRepository.update(order);
        
        if (updateResult != null) {
            // Cập nhật số lượng sản phẩm và giá vốn
            for (PurchaseOrderDetail detail : details) {
                Product product = productRepository.findById(detail.getProduct().getProductId()).orElse(null);
                if (product != null) {
                    // Lưu giá cũ trước khi cập nhật
                    BigDecimal oldPrice = product.getPrice();
                    BigDecimal oldCostPrice = product.getCostPrice();
                    
                    // Cập nhật số lượng
                    int newQuantity = product.getStockQuantity() + detail.getQuantity();
                    product.setStockQuantity(newQuantity);
                    
                    // Cập nhật giá vốn trung bình
                    updateAverageCostPrice(product, detail.getUnitCost(), detail.getQuantity());
                    
                    // Tự động cập nhật giá bán nếu có thiết lập tỷ suất lợi nhuận
                    if (product.getProfitMargin() != null && product.getProfitMargin().compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal newPrice = product.calculateSellingPrice();
                        product.setPrice(newPrice);
                        
                        // Lưu lịch sử giá
                        PriceHistory priceHistory = new PriceHistory(
                            product.getProductId(),
                            oldPrice,
                            newPrice,
                            oldCostPrice,
                            detail.getUnitCost(),
                            order.getEmployee().getEmployeeId(),
                            "Cập nhật giá từ phiếu nhập " + purchaseOrderId
                        );
                        priceHistoryRepository.save(priceHistory);
                    }
                    
                    productRepository.update(product);
                }
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Thêm phương thức tính giá vốn trung bình
     */
    private void updateAverageCostPrice(Product product, BigDecimal newCostPrice, int newQuantity) {
        if (product == null || newCostPrice == null || newQuantity <= 0) {
            return;
        }
        
        int currentStock = product.getStockQuantity();
        BigDecimal currentAvgCost = product.getAverageCostPrice() != null ? 
                product.getAverageCostPrice() : BigDecimal.ZERO;
        
        // Tính tổng giá trị hiện tại
        BigDecimal currentTotalValue = currentAvgCost.multiply(BigDecimal.valueOf(currentStock));
        
        // Tính giá trị lô hàng mới
        BigDecimal newTotalValue = newCostPrice.multiply(BigDecimal.valueOf(newQuantity));
        
        // Tính tổng số lượng
        int totalQuantity = currentStock + newQuantity;
        
        // Tính giá vốn trung bình mới
        BigDecimal newAverageCost = totalQuantity > 0 ?
                (currentTotalValue.add(newTotalValue))
                    .divide(BigDecimal.valueOf(totalQuantity), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;
        
        // Cập nhật vào sản phẩm
        product.setAverageCostPrice(newAverageCost);
        product.setCostPrice(newCostPrice);
    }
    
    /**
     * Kiểm tra đơn đặt hàng có tồn tại không
     * @param purchaseOrderId ID của đơn đặt hàng
     * @return true nếu đơn đặt hàng tồn tại, ngược lại là false
     */
    public boolean purchaseOrderExists(Integer purchaseOrderId) {
        return purchaseOrderRepository.exists(purchaseOrderId);
    }
}