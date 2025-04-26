package com.pcstore.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pcstore.model.Product;
import com.pcstore.model.Return;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.repository.impl.ReturnRepository;

/**
 * Service xử lý logic nghiệp vụ liên quan đến đơn trả hàng
 */
public class ReturnService {
    private final ReturnRepository returnRepository;
    private final ProductRepository productRepository;
    
    /**
     * Khởi tạo service với csdl
     * @param connection Kết nối csdl
     */
    public ReturnService(Connection connection) {
        this.returnRepository = new ReturnRepository(connection);
        this.productRepository = new ProductRepository(connection);
    }

    /**
     * Khởi tạo service với repository
     * @param returnRepository Repository đơn trả hàng
     * @param productRepository Repository sản phẩm để cập nhật tồn kho
     */
    public ReturnService(ReturnRepository returnRepository, ProductRepository productRepository) {
        this.returnRepository = returnRepository;
        this.productRepository = productRepository;
    }
    
    /**
     * Tạo đơn trả hàng mới
     * @param returnRequest Thông tin đơn trả hàng
     * @return Đơn trả hàng đã được tạo
     */
    public Return createReturn(Return returnRequest) {
        // Mặc định trạng thái là "Đang xử lý" nếu không được thiết lập
        if (returnRequest.getStatus() == null || returnRequest.getStatus().isEmpty()) {
            returnRequest.setStatus("Đang xử lý");
        }
        
        // Thiết lập thời gian tạo đơn
        if (returnRequest.getReturnDate() == null) {
            returnRequest.setReturnDate(LocalDateTime.now());
        }
        
        return returnRepository.add(returnRequest);
    }
    
    /**
     * Cập nhật thông tin đơn trả hàng
     * @param returnRequest Thông tin đơn trả hàng mới
     * @return Đơn trả hàng đã được cập nhật
     */
    public Return updateReturn(Return returnRequest) {
        // Kiểm tra tồn tại
        Optional<Return> existingReturn = returnRepository.findById(returnRequest.getReturnId());
        if (!existingReturn.isPresent()) {
            throw new IllegalArgumentException("Đơn trả hàng không tồn tại");
        }
        
        // Kiểm tra trạng thái có cho phép cập nhật không
        String currentStatus = existingReturn.get().getStatus();
        if ("Đã hoàn thành".equals(currentStatus) || "Đã từ chối".equals(currentStatus)) {
            throw new IllegalStateException("Không thể cập nhật đơn trả hàng ở trạng thái " + currentStatus);
        }
        
        return returnRepository.update(returnRequest);
    }
    
    /**
     * Xóa đơn trả hàng theo ID
     * @param returnId ID của đơn trả hàng
     * @return true nếu xóa thành công, ngược lại là false
     */
    public boolean deleteReturn(Integer returnId) {
        // Kiểm tra tồn tại
        Optional<Return> existingReturn = returnRepository.findById(returnId);
        if (!existingReturn.isPresent()) {
            throw new IllegalArgumentException("Đơn trả hàng không tồn tại");
        }
        
        // Kiểm tra trạng thái có cho phép xóa không
        String currentStatus = existingReturn.get().getStatus();
        if (!"Đang xử lý".equals(currentStatus)) {
            throw new IllegalStateException("Chỉ có thể xóa đơn trả hàng ở trạng thái đang xử lý");
        }
        
        return returnRepository.delete(returnId);
    }
    
    /**
     * Tìm đơn trả hàng theo ID
     * @param returnId ID của đơn trả hàng
     * @return Optional chứa đơn trả hàng nếu tìm thấy
     */
    public Optional<Return> findReturnById(Integer returnId) {
        return returnRepository.findById(returnId);
    }
    
    /**
     * Lấy danh sách tất cả đơn trả hàng
     * @return Danh sách đơn trả hàng
     */
    public List<Return> findAllReturns() {
        return returnRepository.findAll();
    }
    
    /**
     * Tìm đơn trả hàng theo hóa đơn
     * @param invoiceId ID của hóa đơn
     * @return Danh sách đơn trả hàng
     */
    public List<Return> findReturnsByInvoice(Integer invoiceId) {
        return returnRepository.findByInvoiceId(invoiceId);
    }
    
    /**
     * Tìm đơn trả hàng theo sản phẩm
     * @param productId ID của sản phẩm
     * @return Danh sách đơn trả hàng
     */
    public List<Return> findReturnsByProduct(String productId) {
        return returnRepository.findByProductId(productId);
    }
    
    /**
     * Tìm đơn trả hàng theo khách hàng
     * @param customerId ID của khách hàng
     * @return Danh sách đơn trả hàng
     */
    public List<Return> findReturnsByCustomer(String customerId) {
        return returnRepository.findByCustomerId(customerId);
    }
    
    /**
     * Tìm đơn trả hàng theo trạng thái
     * @param status Trạng thái cần tìm
     * @return Danh sách đơn trả hàng
     */
    public List<Return> findReturnsByStatus(String status) {
        return returnRepository.findByStatus(status);
    }
    
    /**
     * Tìm đơn trả hàng trong khoảng thời gian
     * @param startDate Thời gian bắt đầu
     * @param endDate Thời gian kết thúc
     * @return Danh sách đơn trả hàng
     */
    public List<Return> findReturnsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return returnRepository.findByDateRange(startDate, endDate);
    }
    
    /**
     * Cập nhật trạng thái đơn trả hàng
     * @param returnId ID của đơn trả hàng
     * @param status Trạng thái mới
     * @param processorId ID của nhân viên xử lý
     * @return true nếu cập nhật thành công
     */
    public boolean updateReturnStatus(Integer returnId, String status, String processorId) {
        return returnRepository.updateStatus(returnId, status, processorId);
    }
    
    /**
     * Phê duyệt đơn trả hàng
     * @param returnId ID của đơn trả hàng
     * @param processorId ID của nhân viên xử lý
     * @param notes Ghi chú
     * @return true nếu phê duyệt thành công
     */
    public boolean approveReturn(Integer returnId, String processorId, String notes) {
        // Kiểm tra tồn tại
        Optional<Return> existingReturn = returnRepository.findById(returnId);
        if (!existingReturn.isPresent()) {
            throw new IllegalArgumentException("Đơn trả hàng không tồn tại");
        }
        
        // Kiểm tra trạng thái có cho phép phê duyệt không
        String currentStatus = existingReturn.get().getStatus();
        if (!"Đang xử lý".equals(currentStatus)) {
            throw new IllegalStateException("Chỉ có thể phê duyệt đơn trả hàng ở trạng thái đang xử lý");
        }

        // Cái này là để xử lý cập nhật tồn kho khi đơn trả hàng được phê duyệt 
        // Nhưng đang bị lỗi Return trả về Hóa đơn chứ không phải sản phẩm
        
        // Xử lý cập nhật tồn kho khi đơn trả hàng được phê duyệt
        // Return returnRequest = existingReturn.get();
        // String productId = returnRequest.getProduct().getProductId();
        // int quantity = returnRequest.getQuantity();
        
        // // Tăng số lượng tồn kho khi phê duyệt trả hàng
        // productRepository.updateStock(productId, quantity);
        
        return returnRepository.approveReturn(returnId, processorId, notes);
    }
    
    /**
     * Từ chối đơn trả hàng
     * @param returnId ID của đơn trả hàng
     * @param processorId ID của nhân viên xử lý
     * @param notes Ghi chú
     * @return true nếu từ chối thành công
     */
    public boolean rejectReturn(Integer returnId, String processorId, String notes) {
        // Kiểm tra tồn tại
        Optional<Return> existingReturn = returnRepository.findById(returnId);
        if (!existingReturn.isPresent()) {
            throw new IllegalArgumentException("Đơn trả hàng không tồn tại");
        }
        
        // Kiểm tra trạng thái có cho phép từ chối không
        String currentStatus = existingReturn.get().getStatus();
        if (!"Đang xử lý".equals(currentStatus)) {
            throw new IllegalStateException("Chỉ có thể từ chối đơn trả hàng ở trạng thái đang xử lý");
        }
        
        return returnRepository.rejectReturn(returnId, processorId, notes);
    }
    
    /**
     * Hoàn thành đơn trả hàng
     * @param returnId ID của đơn trả hàng
     * @param processorId ID của nhân viên xử lý
     * @param notes Ghi chú
     * @return true nếu hoàn thành thành công
     */
    public boolean completeReturn(Integer returnId, String processorId, String notes) {
        // Kiểm tra tồn tại
        Optional<Return> existingReturn = returnRepository.findById(returnId);
        if (!existingReturn.isPresent()) {
            throw new IllegalArgumentException("Đơn trả hàng không tồn tại");
        }
        
        // Kiểm tra trạng thái có cho phép hoàn thành không
        String currentStatus = existingReturn.get().getStatus();
        if (!"Đã phê duyệt".equals(currentStatus)) {
            throw new IllegalStateException("Chỉ có thể hoàn thành đơn trả hàng ở trạng thái đã phê duyệt");
        }
        
        return returnRepository.completeReturn(returnId, processorId, notes);
    }
    
    /**
     * Kiểm tra đơn trả hàng có tồn tại không
     * @param returnId ID của đơn trả hàng
     * @return true nếu đơn trả hàng tồn tại, ngược lại là false
     */
    public boolean returnExists(Integer returnId) {
        return returnRepository.exists(returnId);
    }
    
    /**
     * Tìm kiếm đơn trả hàng theo từ khóa
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách đơn trả hàng phù hợp
     */
    public List<Return> searchReturns(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllReturns();
        }
        
        keyword = keyword.trim().toLowerCase();
        List<Return> results = new ArrayList<>();
        List<Return> allReturns = findAllReturns();
        
        for (Return returnObj : allReturns) {
            // Tìm theo ID
            if (returnObj.getReturnId().toString().contains(keyword)) {
                results.add(returnObj);
                continue;
            }
            
            // Tìm theo lý do
            if (returnObj.getReason() != null && 
                returnObj.getReason().toLowerCase().contains(keyword)) {
                results.add(returnObj);
                continue;
            }
            
            // Tìm theo ghi chú
            if (returnObj.getNotes() != null && 
                returnObj.getNotes().toLowerCase().contains(keyword)) {
                results.add(returnObj);
                continue;
            }
            
            // Tìm theo trạng thái
            if (returnObj.getStatus() != null && 
                returnObj.getStatus().toLowerCase().contains(keyword)) {
                results.add(returnObj);
                continue;
            }
            
            // Tìm theo thông tin sản phẩm
            if (returnObj.getInvoiceDetail() != null && 
                returnObj.getInvoiceDetail().getProduct() != null) {
                
                Product product = returnObj.getInvoiceDetail().getProduct();
                
                if (product.getProductId() != null && 
                    product.getProductId().toLowerCase().contains(keyword)) {
                    results.add(returnObj);
                    continue;
                }
                
                if (product.getProductName() != null && 
                    product.getProductName().toLowerCase().contains(keyword)) {
                    results.add(returnObj);
                }
            }
        }
        
        return results;
    }
    
    /**
     * Tìm các đơn trả hàng theo mã chi tiết hóa đơn
     * 
     * @param invoiceDetailId Mã chi tiết hóa đơn
     * @return Danh sách các đơn trả hàng của chi tiết hóa đơn đó
     * @throws SQLException Nếu có lỗi truy cập CSDL
     */
    public List<Return> findReturnsByInvoiceDetail(Integer invoiceDetailId) throws SQLException {
        if (invoiceDetailId == null) {
            throw new IllegalArgumentException("ID chi tiết hóa đơn không được null");
        }
        
        return returnRepository.findByInvoiceDetail(invoiceDetailId);
    }
}