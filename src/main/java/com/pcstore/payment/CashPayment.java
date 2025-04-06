package com.pcstore.payment;

import com.pcstore.model.Invoice;
import com.pcstore.model.base.BasePayment;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Thanh toán bằng tiền mặt
 * Lớp này triển khai phương thức thanh toán bằng tiền mặt
 */
public class CashPayment extends BasePayment {
    
    private BigDecimal amountReceived; // Số tiền khách đưa
    private BigDecimal change; // Tiền thối lại
    
    /**
     * Constructor mặc định
     */
    public CashPayment() {
        super();
        this.setPaymentMethod(PaymentMethodEnum.CASH);
    }
    
    /**
     * Constructor với hóa đơn và số tiền
     * @param invoice Hóa đơn cần thanh toán
     * @param amount Số tiền thanh toán
     */
    public CashPayment(Invoice invoice, BigDecimal amount) {
        super(invoice, amount);
        this.setPaymentMethod(PaymentMethodEnum.CASH);
    }
    
    /**
     * Constructor đầy đủ với số tiền khách đưa
     * @param invoice Hóa đơn cần thanh toán
     * @param amount Số tiền thanh toán
     * @param amountReceived Số tiền khách đưa
     */
    public CashPayment(Invoice invoice, BigDecimal amount, BigDecimal amountReceived) {
        this(invoice, amount);
        this.amountReceived = amountReceived;
        if (amountReceived.compareTo(amount) >= 0) {
            this.change = amountReceived.subtract(amount);
        } else {
            this.change = BigDecimal.ZERO;
        }
    }
    
    /**
     * Xử lý thanh toán tiền mặt
     * @return true nếu thanh toán thành công, false nếu thất bại
     */
    @Override
    public boolean processPayment() {
        // Kiểm tra số tiền khách đưa phải >= số tiền cần thanh toán
        if (amountReceived == null || amountReceived.compareTo(getAmount()) < 0) {
            setDescription("Số tiền khách đưa không đủ");
            return false;
        }
        
        try {
            // Tính tiền thừa
            change = amountReceived.subtract(getAmount());
            
            // Cập nhật trạng thái
            setStatus(InvoiceStatusEnum.COMPLETED);
            setPaymentDate(LocalDateTime.now());
            setTransactionReference("CASH_" + getPaymentId());
            setDescription("Thanh toán tiền mặt thành công. Tiền thừa: " + change);
            
            // Cập nhật trạng thái hóa đơn
            if (getInvoice() != null) {
                getInvoice().setStatus(InvoiceStatusEnum.COMPLETED);
            }
            
            return true;
        } catch (Exception e) {
            setDescription("Lỗi xử lý thanh toán: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy tiền thừa
     * @return Số tiền thừa
     */
    public BigDecimal getChange() {
        return change;
    }
    
    /**
     * Thiết lập tiền thừa
     * @param change Số tiền thừa
     */
    public void setChange(BigDecimal change) {
        this.change = change;
    }
    
    /**
     * Lấy số tiền khách đưa
     * @return Số tiền khách đưa
     */
    public BigDecimal getAmountReceived() {
        return amountReceived;
    }
    
    /**
     * Thiết lập số tiền khách đưa và tính lại tiền thừa
     * @param amountReceived Số tiền khách đưa
     */
    public void setAmountReceived(BigDecimal amountReceived) {
        this.amountReceived = amountReceived;
        if (amountReceived != null && getAmount() != null) {
            if (amountReceived.compareTo(getAmount()) >= 0) {
                this.change = amountReceived.subtract(getAmount());
            } else {
                this.change = BigDecimal.ZERO;
            }
        }
    }
    
    @Override
    public String toString() {
        return "CashPayment{" +
                "paymentId='" + getPaymentId() + '\'' +
                ", amount=" + getAmount() +
                ", amountReceived=" + amountReceived +
                ", change=" + change +
                ", status=" + getStatus() +
                ", paymentDate=" + getPaymentDate() +
                '}';
    }
}