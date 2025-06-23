package com.pcstore.payment;

import java.awt.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.swing.JOptionPane;

import com.pcstore.model.Invoice;
import com.pcstore.model.base.BasePayment;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.JDialogInputUtils;

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
     * Constructor với hóa đơn
     * @param invoice Hóa đơn cần thanh toán
     */
    public CashPayment(Invoice invoice) {
        super(invoice);
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
    public boolean processPayment(Component parent) {

        BigDecimal amountReceived = JDialogInputUtils.showInputDialogBigDecimal(parent, 
                ErrorMessage.CASH_PAYMENT_ENTER_AMOUNT.get(), 
                "0.00");
        
        // Nếu người dùng hủy việc nhập tiền
        if (amountReceived == null) {
            return false;
        }
        
        try {
            // Kiểm tra số tiền khách đưa có đủ không
            if (amountReceived.compareTo(invoice.getTotalAmount()) < 0) {
                JOptionPane.showMessageDialog(parent, 
                    ErrorMessage.CASH_PAYMENT_INSUFFICIENT_AMOUNT.get(), 
                    ErrorMessage.ERROR_TITLE.get(), 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            setAmountReceived(amountReceived);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, 
                ErrorMessage.CASH_PAYMENT_INVALID_AMOUNT.get(), 
                ErrorMessage.ERROR_TITLE.get(), 
                JOptionPane.ERROR_MESSAGE);
        }
        // Kiểm tra số tiền khách đưa phải >= số tiền cần thanh toán
        if (amountReceived == null || amountReceived.compareTo(getAmount()) < 0) {
            setDescription(ErrorMessage.CASH_PAYMENT_INSUFFICIENT_DESCRIPTION.get());
            return false;
        }
        
        try {
            // Tính tiền thừa
            change = amountReceived.subtract(getAmount());
            
            // Cập nhật trạng thái
            setStatus(InvoiceStatusEnum.PAID);
            setPaymentDate(LocalDateTime.now());
            setTransactionReference(ErrorMessage.CASH_PAYMENT_TRANSACTION_REFERENCE.format(getPaymentId()));
            setDescription(ErrorMessage.CASH_PAYMENT_SUCCESS_DESCRIPTION.format(change));
            
            // Cập nhật trạng thái hóa đơn
            if (getInvoice() != null) {
                getInvoice().setStatus(InvoiceStatusEnum.COMPLETED);
            }
            
            return true;
        } catch (Exception e) {
            setDescription(ErrorMessage.CASH_PAYMENT_ERROR_DESCRIPTION.format(e.getMessage()));
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

    @Override
    public boolean processPayment() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processPayment'");
    }
}