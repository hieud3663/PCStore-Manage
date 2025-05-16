package com.pcstore.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import com.pcstore.model.base.BaseTimeEntity;
import com.pcstore.utils.ErrorMessage;

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
            throw new IllegalArgumentException(ErrorMessage.RETURN_INVOICE_NULL);
        }
        this.invoiceDetail = invoiceDetail;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        if (returnDate == null) {
            throw new IllegalArgumentException(ErrorMessage.FIELD_EMPTY.formatted("Ngày trả hàng"));
        }
        this.returnDate = returnDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(ErrorMessage.INVOICE_DETAIL_QUANTITY_NEGATIVE);
        }
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.RETURN_REASON_EMPTY);
        }
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null) {
            throw new IllegalArgumentException(ErrorMessage.FIELD_EMPTY.formatted("Trạng thái"));
        }
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException(ErrorMessage.REPAIR_STATUS_INVALID);
        }
        if (this.status != null && !canTransitionTo(status)) {
            throw new IllegalStateException(String.format(ErrorMessage.RETURN_CANNOT_TRANSITION, status));
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
    private boolean canTransitionTo(String newStatus) {
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
            throw new IllegalStateException(ErrorMessage.RETURN_APPROVE_PENDING_ONLY);
        }
        setStatus("Approved");
    }

    // Xử lý khi từ chối đơn trả hàng
    public void reject() {
        if (!"Pending".equals(status)) {
            throw new IllegalStateException(ErrorMessage.RETURN_REJECT_PENDING_ONLY);
        }
        setStatus("Rejected");
    }

    // Xử lý khi hoàn thành trả hàng
    public void complete() {
        if (!"Approved".equals(status)) {
            throw new IllegalStateException(ErrorMessage.RETURN_COMPLETE_APPROVED_ONLY);
        }
        setStatus("Completed");
    }

    // Factory method để tạo đơn trả hàng mới
    public static Return createNew(InvoiceDetail invoiceDetail, String reason) {
        if (invoiceDetail == null) {
            throw new IllegalArgumentException(ErrorMessage.RETURN_INVOICE_NULL);
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.RETURN_REASON_EMPTY);
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
            throw new IllegalArgumentException(ErrorMessage.FIELD_EMPTY.formatted("Số tiền hoàn trả"));
        }
        if (returnAmount.compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(ErrorMessage.FIELD_NEGATIVE.formatted("Số tiền hoàn trả"));
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

    public void setRefundAmount(BigDecimal refundAmount) {
        if (refundAmount == null) {
            throw new IllegalArgumentException(ErrorMessage.FIELD_EMPTY.formatted("Số tiền hoàn trả"));
        }
        if (refundAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(ErrorMessage.FIELD_NEGATIVE.formatted("Số tiền hoàn trả"));
        }
        this.returnAmount = refundAmount;
    }
}