package com.pcstore.payment;

import java.awt.Component;
import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;

import javax.swing.JOptionPane;

import com.pcstore.model.Invoice;
import com.pcstore.model.base.BasePayment;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;
import com.pcstore.utils.EnvironmentConfig;
import com.pcstore.utils.ErrorMessage;

import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;

/**
 * Thanh toán qua Ngân hàng sử dụng PayOS API
 * Lớp này triển khai phương thức thanh toán qua các ngân hàng
 */
public class BankPayment extends BasePayment {
    
    private String checkoutUrl; // Đường dẫn thanh toán
    private Long orderCode; // Mã đơn hàng 
    private PayOS payOS; // Instance của PayOS SDK
    private boolean isVerified; // Trạng thái xác minh thanh toán
    
    // PayOS configuration
    private String clientId;
    private String apiKey;
    private String checksumKey;
    
    private final String fileSecret = "/com/pcstore/resources/secrets.properties";
    private final String fileExample = "/com/pcstore/resources/examples.properties";
    
    private final String returnUrl = "https://avancer-z.online/payment/success";
    private final String cancelUrl = "https://avancer-z.online/payment/cancel";
    /**
     * Constructor mặc định
     */
    public BankPayment() {
        super();
        this.setPaymentMethod(PaymentMethodEnum.BANK_TRANSFER);
        try {
            initPayOS();
        } catch (Exception e) {
            System.err.println(ErrorMessage.BANK_PAYMENT_INIT_ERROR.format(e.getMessage()));
        }
    }
    
    /**
     * Constructor với hóa đơn và số tiền
     * @param invoice Hóa đơn cần thanh toán
     * @param amount Số tiền thanh toán
     */
    public BankPayment(Invoice invoice, BigDecimal amount) {
        super(invoice, amount);
        this.setPaymentMethod(PaymentMethodEnum.BANK_TRANSFER);
        this.isVerified = false;
        try {
            initPayOS();
        } catch (Exception e) {
            System.err.println(ErrorMessage.BANK_PAYMENT_INIT_ERROR.format(e.getMessage()));
        }
    }

    /**
     * Constructor với hóa đơn
     * @param invoice Hóa đơn cần thanh toán
     */
    public BankPayment(Invoice invoice) {
        super(invoice);
        this.setPaymentMethod(PaymentMethodEnum.BANK_TRANSFER);
        this.isVerified = false;
        try {
            initPayOS();
        } catch (Exception e) {
            System.err.println(ErrorMessage.BANK_PAYMENT_INIT_ERROR.format(e.getMessage()));
        }
    }
    
    /**
     * Khởi tạo PayOS SDK
     */
    private void initPayOS() throws Exception {
        try {
            String clientId = EnvironmentConfig.getPayOSClientId();
            String apiKey = EnvironmentConfig.getPayOSApiKey();
            String checksumKey = EnvironmentConfig.getPayOSChecksumKey();
            
            if (clientId == null || apiKey == null || checksumKey == null) {
                throw new Exception(ErrorMessage.BANK_PAYMENT_MISSING_CREDENTIALS.get());
            }
            
            payOS = new PayOS(clientId, apiKey, checksumKey);
        } catch (Exception e) {
            throw new Exception(ErrorMessage.BANK_PAYMENT_INIT_PAYOS_ERROR.format(e.getMessage()));
        }
    }
    
    /**
     * Tạo link thanh toán qua PayOS
     * @return URL thanh toán
     */
    public String createPaymentLink() {
        try {
            if (getInvoice() == null) {
                setDescription(ErrorMessage.BANK_PAYMENT_NO_INVOICE_INFO.get());
                return null;
            }
            
            // Tạo mã đơn hàng duy nhất
            this.orderCode = getInvoice().getInvoiceId().longValue();
            
            // Tạo thông tin sản phẩm
            ItemData itemData = ItemData.builder()
                    .name(ErrorMessage.BANK_PAYMENT_INVOICE_ITEM_NAME.format(getInvoice().getInvoiceId()))
                    .quantity(1)
                    .price(getAmount().intValue())
                    .build();
            
            // Tạo thông tin thanh toán
            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .amount(getAmount().intValue())
                    .description(ErrorMessage.BANK_PAYMENT_PAYMENT_DESCRIPTION.format(getInvoice().getInvoiceId()))
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl)
                    .item(itemData)
                    .build();
            
            // Gọi API tạo link thanh toán
            CheckoutResponseData response = payOS.createPaymentLink(paymentData);
            
            if (response != null) {
                this.checkoutUrl = response.getCheckoutUrl();
                setTransactionReference(orderCode);
                System.out.println(ErrorMessage.BANK_PAYMENT_PAYMENT_LINK_SUCCESS.format(checkoutUrl));
                return checkoutUrl;
            } else {
                System.err.println(ErrorMessage.BANK_PAYMENT_PAYMENT_LINK_FAILED.get());
                return null;
            }
            
        } catch (Exception e) {
            System.err.println(ErrorMessage.BANK_PAYMENT_CREATE_LINK_ERROR.format(e.getMessage()));
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Kiểm tra trạng thái thanh toán từ PayOS
     * @return 1 nếu thanh toán thành công, 0 nếu đang chờ, -1 nếu lỗi
     */
    public int checkPaymentStatus() {
        try {
            if (orderCode == null) {
                return -1;
            }
            
            // Truy vấn trạng thái giao dịch
            PaymentLinkData response = payOS.getPaymentLinkInformation(orderCode);
            
            if (response != null) {
                // Mã trạng thái: PAID = đã thanh toán, PENDING = đang chờ, CANCELLED = đã hủy
                String status = response.getStatus();
                
                if ("PAID".equals(status)) {
                    // Thanh toán thành công
                    this.isVerified = true;
                    setStatus(InvoiceStatusEnum.COMPLETED);
                    setPaymentDate(LocalDateTime.now());
                    setDescription(ErrorMessage.BANK_PAYMENT_SUCCESS_DESCRIPTION.get());
                    
                    // Cập nhật trạng thái hóa đơn
                    if (getInvoice() != null) {
                        getInvoice().setStatus(InvoiceStatusEnum.COMPLETED);
                    }
                    
                    System.out.println(ErrorMessage.BANK_PAYMENT_STATUS_SUCCESS.format(orderCode));
                    return 1;
                } else if ("PENDING".equals(status)) {
                    // Đang chờ thanh toán
                    System.out.println(ErrorMessage.BANK_PAYMENT_STATUS_PENDING.get());
                    return 0;
                } else {
                    // Thanh toán thất bại hoặc bị hủy
                    System.out.println(ErrorMessage.BANK_PAYMENT_STATUS_FAILED_OR_CANCELLED.get());
                    return -1;
                }
            } else {
                return -1;
            }
            
        } catch (Exception e) {
            System.err.println(ErrorMessage.BANK_PAYMENT_CHECK_STATUS_ERROR.format(e.getMessage()));
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Mở URL thanh toán trong trình duyệt mặc định
     * @param url URL thanh toán
     * @return true nếu mở thành công, false nếu thất bại
     */
    public boolean openWebBrowser(String url) {
        try {
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(url));
                return true;
            }
            return false;
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean processPayment() {
        if (getAmount() == null || getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            setDescription(ErrorMessage.BANK_PAYMENT_INVALID_AMOUNT.get());
            return false;
        }
        
        try {
            // Tạo link thanh toán
            String paymentUrl = createPaymentLink();
            if (paymentUrl == null) {
                setStatus(InvoiceStatusEnum.FAILED);
                setDescription("Không thể tạo link thanh toán");
                return false;
            }
            
            // Mở trình duyệt để thanh toán
            if (!openWebBrowser(paymentUrl)) {
                setDescription("Không thể mở trình duyệt để thanh toán");
                return false;
            }
            
            // Trong thực tế, cần webhook hoặc polling để kiểm tra trạng thái
            // Đây là xử lý đơn giản để demo
            
            // Thực hiện kiểm tra trạng thái thanh toán
            int paymentStatus = checkPaymentStatus();
            
            if (paymentStatus == 1) {
                // Thanh toán thành công
                return true;
            } else {
                setStatus(InvoiceStatusEnum.FAILED);
                setDescription(ErrorMessage.BANK_PAYMENT_FAILED_DESCRIPTION.get());
                return false;
            }
        } catch (Exception e) {
            setDescription(ErrorMessage.BANK_PAYMENT_ERROR_DESCRIPTION.format(e.getMessage()));
            return false;
        }
    }

    @Override
    public boolean processPayment(Component parent) {
        if (getAmount() == null || getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            setDescription(ErrorMessage.BANK_PAYMENT_INVALID_AMOUNT.get());
            return false;
        }
        
        try {
            // Tạo link thanh toán
            String paymentUrl = createPaymentLink();
            if (paymentUrl == null) {
                setStatus(InvoiceStatusEnum.FAILED);
                setDescription("Không thể tạo link thanh toán");
                JOptionPane.showMessageDialog(parent, 
                    ErrorMessage.PAYMENT_LINK_CREATE_ERROR.get(), 
                    ErrorMessage.ERROR_TITLE.get(), 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Mở trình duyệt để thanh toán
            if (!openWebBrowser(paymentUrl)) {
                setDescription("Không thể mở trình duyệt để thanh toán");
                JOptionPane.showMessageDialog(parent, 
                    ErrorMessage.BROWSER_OPEN_ERROR.get(), 
                    ErrorMessage.ERROR_TITLE.get(), 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Hiển thị dialog chờ thanh toán
            int option = JOptionPane.showConfirmDialog(
                parent,
                ErrorMessage.PAYMENT_WAITING_MESSAGE.get(),
                ErrorMessage.PAYMENT_CONFIRM_TITLE.get(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            
            if (option == JOptionPane.OK_OPTION) {
                // Kiểm tra trạng thái thanh toán
                int status = checkPaymentStatus();
                if (status == 1) {
                    JOptionPane.showMessageDialog(parent, 
                        ErrorMessage.PAYMENT_SUCCESS.get(), 
                        ErrorMessage.INFO_TITLE.get(), 
                        JOptionPane.INFORMATION_MESSAGE);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(parent, 
                        ErrorMessage.PAYMENT_VERIFICATION_FAILED.get(), 
                        ErrorMessage.WARNING_TITLE.get(), 
                        JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            } else {
                setStatus(InvoiceStatusEnum.CANCELLED);
                setDescription(ErrorMessage.BANK_PAYMENT_USER_CANCELLED.get());
                return false;
            }
        } catch (Exception e) {
            setDescription(ErrorMessage.BANK_PAYMENT_ERROR_DESCRIPTION.format(e.getMessage()));
            JOptionPane.showMessageDialog(parent, 
                ErrorMessage.PAYMENT_PROCESSING_ERROR.format(e.getMessage()), 
                ErrorMessage.ERROR_TITLE.get(), 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Getters and setters
    
    public String getCheckoutUrl() {
        return checkoutUrl;
    }
    
    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }
    
    public Long getOrderCode() {
        return orderCode;
    }
    
    public void setOrderCode(Long orderCode) {
        this.orderCode = orderCode;
    }
    
    public boolean isVerified() {
        return isVerified;
    }
    
    public void setVerified(boolean verified) {
        isVerified = verified;
    }
    
    @Override
    public String toString() {
        return "BankPayment{" +
                "paymentId='" + getPaymentId() + '\'' +
                ", amount=" + getAmount() +
                ", orderCode='" + orderCode + '\'' +
                ", checkoutUrl='" + checkoutUrl + '\'' +
                ", isVerified=" + isVerified +
                ", status=" + getStatus() +
                ", paymentDate=" + getPaymentDate() +
                '}';
    }
}