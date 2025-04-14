package com.pcstore.model.base;

import com.pcstore.model.Invoice;
import com.pcstore.model.enums.PaymentMethodEnum;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.InvoiceStatusEnum;

import java.awt.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lớp cơ sở cho tất cả các phương thức thanh toán trong hệ thống
 * Định nghĩa các thuộc tính và phương thức chung cho mọi phương thức thanh toán
 */
public abstract class BasePayment {
    
    protected String paymentId;
    protected Invoice invoice;
    protected BigDecimal amount;
    protected PaymentMethodEnum paymentMethod;
    protected InvoiceStatusEnum status;
    protected LocalDateTime paymentDate;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected String transactionReference;
    protected String description;
    
    /**
     * Constructor mặc định
     */
    public BasePayment() {
        this.paymentId = UUID.randomUUID().toString();
        this.status = InvoiceStatusEnum.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor với invoice và số tiền
     * @param invoice Hóa đơn cần thanh toán
     * @param amount Số tiền thanh toán
     */
    public BasePayment(Invoice invoice, BigDecimal amount) {
        this();
        this.invoice = invoice;
        this.amount = amount;
    }
    
    /**
     * Constructor với invoice
     * @param invoice Hóa đơn cần thanh toán
     */
    public BasePayment(Invoice invoice) {
        this.invoice = invoice;
        this.paymentId = UUID.randomUUID().toString();
        this.status = InvoiceStatusEnum.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.amount = invoice.getTotalAmount(); // Số tiền thanh toán mặc định là tổng hóa đơn
    }

    /**
     * Xử lý thanh toán - hàm trừu tượng bắt buộc các lớp con phải triển khai
     * @return true nếu thanh toán thành công, false nếu thất bại
     */
    public abstract boolean processPayment();

    /**
     * Xử lý thanh toán - hàm trừu tượng bắt buộc các lớp con phải triển khai
     * @return true nếu thanh toán thành công, false nếu thất bại
     */
    public abstract boolean processPayment(Component parent);
    
    

    /**
     * Kiểm tra trạng thái thanh toán
     * @return true nếu đã thanh toán thành công, false nếu chưa
     */
    public boolean isPaymentSuccessful() {
        return this.status == InvoiceStatusEnum.COMPLETED;
    }
    
    /**
     * Hủy thanh toán
     * @param reason Lý do hủy thanh toán
     * @return true nếu hủy thành công, false nếu thất bại
     */
    public boolean cancelPayment(String reason) {
        if (this.status == InvoiceStatusEnum.COMPLETED) {
            return false; // Không thể hủy thanh toán đã hoàn thành
        }
        
        this.status = InvoiceStatusEnum.CANCELLED;
        this.description = reason;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    /**
     * Cập nhật trạng thái thanh toán
     * @param status Trạng thái mới
     */
    public void updateStatus(InvoiceStatusEnum status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        
        if (status == InvoiceStatusEnum.COMPLETED) {
            this.paymentDate = LocalDateTime.now();
        }
    }
    
    // Getters và Setters
    
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public InvoiceStatusEnum getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatusEnum status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "BasePayment{" +
                "paymentId='" + paymentId + '\'' +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                ", paymentDate=" + paymentDate +
                ", transactionReference='" + transactionReference + '\'' +
                '}';
    }
}