package com.pcstore.payment;

import com.pcstore.model.Invoice;
import com.pcstore.model.base.BasePayment;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Thanh toán qua ZaloPay
 * Lớp này triển khai phương thức thanh toán qua ZaloPay
 */
public class ZalopayPayment extends BasePayment {
    
    private String qrCodeUrl; // Đường dẫn QR Code
    private String appTransactionId; // Mã giao dịch trong ứng dụng ZaloPay
    private boolean isVerified; // Trạng thái xác minh thanh toán
    
    /**
     * Constructor mặc định
     */
    public ZalopayPayment() {
        super();
        this.setPaymentMethod(PaymentMethodEnum.ZALOPAY);
    }
    
    /**
     * Constructor với hóa đơn và số tiền
     * @param invoice Hóa đơn cần thanh toán
     * @param amount Số tiền thanh toán
     */
    public ZalopayPayment(Invoice invoice, BigDecimal amount) {
        super(invoice, amount);
        this.setPaymentMethod(PaymentMethodEnum.ZALOPAY);
        this.isVerified = false;
    }
    
    /**
     * Tạo QR Code để thanh toán
     * @return URL của QR Code
     */
    public String generateQRCode() {
        // Mô phỏng việc tạo QR Code từ ZaloPay API
        String baseUrl = "https://zalopay.vn/qr/";
        String invoiceId = getInvoice() != null ? String.valueOf(getInvoice().getInvoiceId()) : "";
        String amount = getAmount() != null ? getAmount().toString() : "0";
        String randomStr = String.valueOf(System.currentTimeMillis());
        
        // Trong triển khai thực tế, đây sẽ là cuộc gọi API đến ZaloPay
        this.qrCodeUrl = baseUrl + "pay=" + invoiceId + "&amount=" + amount + "&ref=" + randomStr;
        this.appTransactionId = "ZLP" + System.currentTimeMillis();
        
        return this.qrCodeUrl;
    }
    
    /**
     * Xử lý thanh toán ZaloPay
     * Trong môi trường thực tế, sẽ gọi API của ZaloPay và xử lý callback
     * @return true nếu thanh toán thành công, false nếu thất bại
     */
    @Override
    public boolean processPayment() {
        if (getAmount() == null || getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            setDescription("Số tiền thanh toán không hợp lệ");
            return false;
        }
        
        try {
            if (qrCodeUrl == null) {
                generateQRCode();
            }
            
            // Mô phỏng việc xác minh thanh toán từ ZaloPay
            // Trong triển khai thực tế, cần có webhook hoặc polling để kiểm tra trạng thái
            
            // Mô phỏng xác suất thành công là 90%
            Random random = new Random();
            boolean paymentSuccess = random.nextDouble() <= 0.9;
            
            if (paymentSuccess) {
                this.isVerified = true;
                setStatus(InvoiceStatusEnum.COMPLETED);
                setPaymentDate(LocalDateTime.now());
                setTransactionReference(this.appTransactionId);
                setDescription("Thanh toán ZaloPay thành công");
                
                // Cập nhật trạng thái hóa đơn
                if (getInvoice() != null) {
                    getInvoice().setStatus(InvoiceStatusEnum.COMPLETED);
                }
                
                return true;
            } else {
                setStatus(InvoiceStatusEnum.FAILED);
                setDescription("Thanh toán ZaloPay thất bại: Giao dịch bị từ chối");
                return false;
            }
        } catch (Exception e) {
            setDescription("Lỗi xử lý thanh toán ZaloPay: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Kiểm tra trạng thái thanh toán từ ZaloPay (giả lập)
     * @return true nếu thanh toán đã được xác minh
     */
    public boolean verifyPayment() {
        // Giả lập việc kiểm tra trạng thái thanh toán
        // Trong thực tế sẽ gọi API kiểm tra của ZaloPay
        Random random = new Random();
        boolean verificationResult = random.nextDouble() <= 0.95; // 95% xác suất xác minh thành công
        
        if (verificationResult) {
            this.isVerified = true;
            setStatus(InvoiceStatusEnum.COMPLETED);
            setPaymentDate(LocalDateTime.now());
        }
        
        return this.isVerified;
    }
    
    public String getQrCodeUrl() {
        return qrCodeUrl;
    }
    
    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
    
    public String getAppTransactionId() {
        return appTransactionId;
    }
    
    public void setAppTransactionId(String appTransactionId) {
        this.appTransactionId = appTransactionId;
    }
    
    public boolean isVerified() {
        return isVerified;
    }
    
    public void setVerified(boolean verified) {
        isVerified = verified;
    }
    
    @Override
    public String toString() {
        return "ZaloPayPayment{" +
                "paymentId='" + getPaymentId() + '\'' +
                ", amount=" + getAmount() +
                ", appTransactionId='" + appTransactionId + '\'' +
                ", qrCodeUrl='" + qrCodeUrl + '\'' +
                ", isVerified=" + isVerified +
                ", status=" + getStatus() +
                ", paymentDate=" + getPaymentDate() +
                '}';
    }
}