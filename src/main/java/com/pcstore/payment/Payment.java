package com.pcstore.payment;

import java.math.BigDecimal;

import com.pcstore.model.Invoice;
import com.pcstore.model.base.BasePayment;
import com.pcstore.model.enums.PaymentMethodEnum;


/**
 * Factory tạo đối tượng thanh toán dựa trên phương thức thanh toán
 * Sử dụng mẫu thiết kế Factory Method
 */
public class Payment {
    
    /**
     * Tạo đối tượng thanh toán phù hợp với phương thức được chọn
     * @param paymentMethod Phương thức thanh toán
     * @param invoice Hóa đơn cần thanh toán
     * @param amount Số tiền cần thanh toán
     * @return Đối tượng thanh toán tương ứng
     */
    public static BasePayment createPayment(PaymentMethodEnum paymentMethod, Invoice invoice, BigDecimal amount) {
        switch (paymentMethod) {
            case CASH:
                return new CashPayment(invoice, amount);
            case ZALOPAY:
                return new ZalopayPayment(invoice, amount);
            // case BANK_TRANSFER:
            //     return new BankTransferPayment(invoice, amount);
            // case MOMO:
            //     return new MomoPayment(invoice, amount);
            // case CREDIT_CARD:
            //     return new CreditCardPayment(invoice, amount);
            default:
                return new CashPayment(invoice, amount); // Mặc định là thanh toán tiền mặt
        }
    }
    
    /**
     * Tạo đối tượng thanh toán với số tiền khách hàng đưa (cho thanh toán tiền mặt)
     * @param invoice Hóa đơn cần thanh toán
     * @param amount Số tiền cần thanh toán
     * @param amountReceived Số tiền khách hàng đưa
     * @return Đối tượng CashPayment
     */
    public static CashPayment createCashPayment(Invoice invoice, BigDecimal amount, BigDecimal amountReceived) {
        return new CashPayment(invoice, amount, amountReceived);
    }
    
    /**
     * Tạo đối tượng thanh toán tiền mặt mặc định
     * @param invoice Hóa đơn cần thanh toán
     * @return Đối tượng CashPayment với số tiền từ hóa đơn
     */
    public static CashPayment createCashPayment(Invoice invoice) {
        BigDecimal amount = invoice.getTotalAmount();
        return new CashPayment(invoice, amount);
    }
    
    /**
     * Tạo đối tượng thanh toán ZaloPay
     * @param invoice Hóa đơn cần thanh toán
     * @return Đối tượng ZalopayPayment với số tiền từ hóa đơn
     */
    public static ZalopayPayment createZalopayPayment(Invoice invoice) {
        BigDecimal amount = invoice.getTotalAmount();
        return new ZalopayPayment(invoice, amount);
    }
}