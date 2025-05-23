package com.pcstore.model;

import java.time.LocalDateTime;

import com.pcstore.model.base.BaseTimeEntity;

/**
 * Class biểu diễn đơn trả hàng
 */
public class Return extends BaseTimeEntity {
    private Integer returnId;
    private InvoiceDetail invoiceDetail;
    private LocalDateTime returnDate;
    private int quantity;
    private String reason;
    private String status; // Pending, Approved, Rejected, Completed
    private String notes;
    private java.math.BigDecimal returnAmount;
    private Employee processedBy;
    private boolean isExchange;
    private Product newProduct;

    @Override
    public Object getId() {
        return returnId;
    }

    public Integer getReturnId() {
        return returnId;
    }

    public void setReturnId(Integer returnId) {
        this.returnId = returnId;
    }

    public InvoiceDetail getInvoiceDetail() {
        return invoiceDetail;
    }

    public void setInvoiceDetail(InvoiceDetail invoiceDetail) {
        if (invoiceDetail == null) {
            throw new IllegalArgumentException("Chi tiết hóa đơn không được để trống");
        }
        this.invoiceDetail = invoiceDetail;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        if (returnDate == null) {
            throw new IllegalArgumentException("Ngày trả hàng không được để trống");
        }
        this.returnDate = returnDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng trả phải lớn hơn 0");
        }
        
        // Bỏ kiểm tra invoiceDetail.canReturn() vì đã được kiểm tra ở controller
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Lý do trả hàng không được để trống");
        }
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái không được để trống");
        }
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ");
        }
        if (!canChangeStatus(status)) {
            throw new IllegalStateException("Không thể chuyển sang trạng thái " + status);
        }
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Kiểm tra trạng thái có hợp lệ không
    private boolean isValidStatus(String status) {
        return "Pending".equals(status) ||
               "Approved".equals(status) ||
               "Rejected".equals(status) ||
               "Completed".equals(status);
    }

    // Kiểm tra có thể chuyển sang trạng thái mới không
    private boolean canChangeStatus(String newStatus) {
        if (status == null) {
            return true;
        }

        switch (status) {
            case "Pending":
                return "Approved".equals(newStatus) || 
                       "Rejected".equals(newStatus);
            case "Approved":
                return "Completed".equals(newStatus);
            case "Rejected":
            case "Completed":
                return false;
            default:
                return false;
        }
    }

    // Kiểm tra đơn trả hàng có thể cập nhật không
    public boolean canUpdate() {
        return "Pending".equals(status);
    }

    // Xử lý khi chấp nhận đơn trả hàng
    public void approve() {
        if (!"Pending".equals(status)) {
            throw new IllegalStateException("Chỉ có thể chấp nhận đơn trả hàng đang chờ xử lý");
        }
        setStatus("Approved");
    }

    // Xử lý khi từ chối đơn trả hàng
    public void reject() {
        if (!"Pending".equals(status)) {
            throw new IllegalStateException("Chỉ có thể từ chối đơn trả hàng đang chờ xử lý");
        }
        setStatus("Rejected");
    }

    // Xử lý khi hoàn thành trả hàng
    public void complete() {
        if (!"Approved".equals(status)) {
            throw new IllegalStateException("Chỉ có thể hoàn thành đơn trả hàng đã được chấp nhận");
        }
        
        // Cập nhật tồn kho
        Product product = invoiceDetail.getProduct();
        product.increaseStock(quantity);
        
        setStatus("Completed");
    }

    // Factory method để tạo đơn trả hàng mới
    public static Return createNew(InvoiceDetail invoiceDetail, String reason) {
        if (invoiceDetail == null) {
            throw new IllegalArgumentException("Chi tiết hóa đơn không được để trống");
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Lý do trả hàng không được để trống");
        }

        Return returnItem = new Return();
        returnItem.setInvoiceDetail(invoiceDetail);
     
        returnItem.setReason(reason);
        returnItem.setReturnDate(LocalDateTime.now());
        returnItem.setStatus("Pending");
        return returnItem;
    }
    
    /**
     * Kiểm tra xem đơn trả hàng có thể chuyển sang trạng thái tiếp theo không
     * @return Mảng các trạng thái có thể chuyển tới, rỗng nếu không thể chuyển
     */
    public String[] canIterate() {
        switch(status) {
            case "Pending":
                return new String[]{"Approved", "Rejected"};
            case "Approved":
                return new String[]{"Completed"};
            case "Rejected":
            case "Completed":
                return new String[]{};
            default:
                throw new IllegalStateException("Trạng thái không hợp lệ: " + status);
        }
    }

    /**
     * Lấy số tiền hoàn trả
     * @return Số tiền hoàn trả
     */
    public java.math.BigDecimal getReturnAmount() {
        return returnAmount;
    }

    /**
     * Thiết lập số tiền hoàn trả
     * @param returnAmount Số tiền hoàn trả
     */
    public void setReturnAmount(java.math.BigDecimal returnAmount) {
        if (returnAmount == null) {
            throw new IllegalArgumentException("Số tiền hoàn trả không được để trống");
        }
        if (returnAmount.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số tiền hoàn trả không được âm");
        }
        this.returnAmount = returnAmount;
    }

    /**
     * Thiết lập nhân viên xử lý đơn trả hàng
     * 
     * @param processedBy Nhân viên xử lý
     */
    public void setProcessedBy(Employee processedBy) {
        this.processedBy = processedBy;
    }

    /**
     * Thiết lập trạng thái đổi hàng
     * 
     * @param isExchange true nếu là đổi hàng, false nếu là trả hàng
     */
    public void setExchange(boolean isExchange) {
        this.isExchange = isExchange;
    }

    /**
     * Thiết lập sản phẩm mới (trong trường hợp đổi hàng)
     * 
     * @param newProduct Sản phẩm mới
     */
    public void setNewProduct(Product newProduct) {
        this.newProduct = newProduct;
    }
}