package com.pcstore.repository;

import com.pcstore.model.Return;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface Repository cho Return entity
 */
public interface iReturnRepository extends iRepository<Return, Integer> {
    /**
     * Tìm đơn trả hàng theo hóa đơn
     * @param invoiceId ID của hóa đơn
     * @return Danh sách đơn trả hàng
     */
    List<Return> findByInvoiceId(Integer invoiceId);
    
    /**
     * Tìm đơn trả hàng theo sản phẩm
     * @param productId ID của sản phẩm
     * @return Danh sách đơn trả hàng
     */
    List<Return> findByProductId(String productId);
    
    /**
     * Tìm đơn trả hàng theo khách hàng
     * @param customerId ID của khách hàng
     * @return Danh sách đơn trả hàng
     */
    List<Return> findByCustomerId(String customerId);
    
    /**
     * Tìm đơn trả hàng theo trạng thái
     * @param status Trạng thái cần tìm
     * @return Danh sách đơn trả hàng
     */
    List<Return> findByStatus(String status);
    
    /**
     * Tìm đơn trả hàng trong khoảng thời gian
     * @param startDate Thời gian bắt đầu
     * @param endDate Thời gian kết thúc
     * @return Danh sách đơn trả hàng
     */
    List<Return> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Cập nhật trạng thái đơn trả hàng
     * @param returnId ID của đơn trả hàng
     * @param status Trạng thái mới
     * @param processorId ID của nhân viên xử lý
     * @return true nếu cập nhật thành công
     */
    boolean updateStatus(Integer returnId, String status, String processorId);
    
    /**
     * Phê duyệt đơn trả hàng
     * @param returnId ID của đơn trả hàng
     * @param processorId ID của nhân viên xử lý
     * @param notes Ghi chú
     * @return true nếu phê duyệt thành công
     */
    boolean approveReturn(Integer returnId, String processorId, String notes);
    
    /**
     * Từ chối đơn trả hàng
     * @param returnId ID của đơn trả hàng
     * @param processorId ID của nhân viên xử lý
     * @param notes Ghi chú
     * @return true nếu từ chối thành công
     */
    boolean rejectReturn(Integer returnId, String processorId, String notes);
    
    /**
     * Hoàn thành đơn trả hàng
     * @param returnId ID của đơn trả hàng
     * @param processorId ID của nhân viên xử lý
     * @param notes Ghi chú
     * @return true nếu hoàn thành thành công
     */
    boolean completeReturn(Integer returnId, String processorId, String notes);
}