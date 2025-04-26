package com.pcstore.payment;

import com.pcstore.model.Invoice;
import com.pcstore.model.base.BasePayment;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;

import java.awt.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Thanh toán qua ZaloPay
 * Lớp này triển khai phương thức thanh toán qua ZaloPay
 */
public class ZalopayPayment extends BasePayment {
    
    private String qrCodeUrl; // Đường dẫn QR Code
    private String appTransactionId; // Mã giao dịch trong ứng dụng ZaloPay
    private boolean isVerified; // Trạng thái xác minh thanh toán
    
    // ZaloPay configuration
    private final int APP_ID = 2554;
    private final String KEY1 = "sdngKKJmqEMzvh5QQcdD2A9XBSKUNaYn";
    
    private final String CREATE_ORDER_URL = "https://sandbox.zalopay.com.vn/v001/tpe/createorder";
    private final String GET_STATUS_URL = "https://sandbox.zalopay.com.vn/v001/tpe/getstatusbyapptransid";
    
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
     * Constructor với hóa đơn
     * @param invoice Hóa đơn cần thanh toán
     */
    public ZalopayPayment(Invoice invoice) {
        super(invoice);
        this.setPaymentMethod(PaymentMethodEnum.ZALOPAY);
        this.isVerified = false;
    }
    
    /**
     * Tạo giao dịch trên ZaloPay và lấy URL thanh toán
     * @return URL của trang thanh toán ZaloPay
     */
    public String createZaloPayOrder() {
        try {
            // Tạo mã giao dịch theo định dạng yyMMdd_uuid
            String dateFormat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
            this.appTransactionId = dateFormat + "_" + UUID.randomUUID().toString();
            
            // Thời gian giao dịch (milliseconds)
            long appTime = System.currentTimeMillis();
            
            // Tạo thông tin nhúng
            JSONObject embedData = new JSONObject();
            embedData.put("merchantinfo", "embeddata123");
            embedData.put("redirecturl", "https://avancer-z.online");
            
            // Tạo thông tin sản phẩm
            JSONArray items = new JSONArray();
            JSONObject item = new JSONObject();
            String invoiceId = getInvoice() != null ? String.valueOf(getInvoice().getInvoiceId()) : "0";
            String description =  "Hóa đơn số " + invoiceId + " , tại cửa hàng HAL Store";
            
            item.put("itemid", invoiceId);
            item.put("itemname", description);
            item.put("itemprice", getAmount().intValue());
            item.put("itemquantity", 1);
            items.put(item);
            
            // Tạo chuỗi dữ liệu để tính MAC
            String data = String.format("%d|%s|%s|%d|%d|%s|%s",
                    APP_ID,
                    appTransactionId,
                    "demo", // appuser
                    getAmount().intValue(),
                    appTime,
                    embedData.toString(),
                    items.toString());
            
            // Tính MAC bằng HMAC SHA256
            String mac = hmacSha256(KEY1, data);
            
            // Gửi request đến ZaloPay API
            HttpResponse<JsonNode> response = Unirest.post(CREATE_ORDER_URL)
                    .field("appid", APP_ID)
                    .field("apptransid", appTransactionId)
                    .field("appuser", "demo")
                    .field("apptime", String.valueOf(appTime))
                    .field("amount", String.valueOf(getAmount().intValue()))
                    .field("description", "Thanh toán hóa đơn: " + description)
                    .field("bankcode", "zalopayapp")
                    .field("embeddata", embedData.toString())
                    .field("item", items.toString())
                    .field("mac", mac)
                    .asJson();
            
            JSONObject result = response.getBody().getObject();
            
            // Xử lý kết quả
            if (result.getInt("returncode") == 1) {
                this.qrCodeUrl = result.getString("orderurl");
                System.out.println("Tạo đơn hàng thành công. Mã giao dịch: " + appTransactionId);
                System.out.println("URL thanh toán: " + qrCodeUrl);
                return qrCodeUrl;
            } else {
                System.err.println("Tạo đơn hàng thất bại. Mã lỗi: " + result.getInt("returncode"));
                System.err.println("Thông tin lỗi: " + result.getString("returnmessage"));
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo đơn hàng ZaloPay: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Kiểm tra trạng thái thanh toán từ ZaloPay
     * @return 1 nếu thanh toán thành công, 0 nếu đang chờ, -1 nếu lỗi, -2 nếu hủy
     */
    public int checkPaymentStatus() {
        try {
            if (appTransactionId == null) {
                return -1;
            }
            
            // Tạo chuỗi dữ liệu để tính MAC
            String data = String.format("%d|%s|%s", APP_ID, appTransactionId, KEY1);
            String mac = hmacSha256(KEY1, data);
            
            // Gửi request kiểm tra trạng thái
            HttpResponse<JsonNode> response = Unirest.post(GET_STATUS_URL)
                    .field("appid", APP_ID)
                    .field("apptransid", appTransactionId)
                    .field("mac", mac)
                    .asJson();
            
            JSONObject result = response.getBody().getObject();
            
            if (result.getInt("returncode") == 1) {
                // Thanh toán thành công
                this.isVerified = true;
                setStatus(InvoiceStatusEnum.COMPLETED);
                setPaymentDate(LocalDateTime.now());
                setTransactionReference(appTransactionId);
                setDescription("Thanh toán ZaloPay thành công");
                
                // Cập nhật trạng thái hóa đơn
                if (getInvoice() != null) {
                    getInvoice().setStatus(InvoiceStatusEnum.COMPLETED);
                }
                
                System.out.println("Thanh toán thành công. Mã giao dịch: " + appTransactionId);
                return 1;
            } else {
                // Đang chờ thanh toán
                System.out.println("Đang chờ thanh toán...");
                return 0;
            }
            
        } catch (Exception e) {
            System.err.println("Lỗi khi kiểm tra trạng thái thanh toán: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Tạo chuỗi HMAC SHA256
     * @param key Khóa bí mật
     * @param data Dữ liệu cần tính
     * @return Chuỗi HMAC SHA256 dạng hex
     */
    private String hmacSha256(String key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        
        byte[] hash = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        
        return hexString.toString();
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
            setDescription("Số tiền thanh toán không hợp lệ");
            return false;
        }
        
        try {
            // Tạo đơn hàng ZaloPay và lấy URL thanh toán
            String paymentUrl = createZaloPayOrder();
            if (paymentUrl == null) {
                setStatus(InvoiceStatusEnum.FAILED);
                setDescription("Không thể tạo đơn hàng ZaloPay");
                return false;
            }
            
            // Mở trình duyệt để thanh toán
            if (!openWebBrowser(paymentUrl)) {
                setDescription("Không thể mở trình duyệt để thanh toán");
                return false;
            }
            
            // Giả lập kiểm tra trạng thái thanh toán
            // Trong thực tế, cần webhook hoặc polling để kiểm tra
            
            // Mô phỏng xác suất thành công là 90%
           
            int paymentSuccess = checkPaymentStatus();
            
            if (paymentSuccess == 1) {
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

    @Override
    public boolean processPayment(Component parent) {
        if (getAmount() == null || getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            setDescription("Số tiền thanh toán không hợp lệ");
            return false;
        }
        
        try {
            // Tạo đơn hàng ZaloPay và lấy URL thanh toán
            String paymentUrl = createZaloPayOrder();
            if (paymentUrl == null) {
                setStatus(InvoiceStatusEnum.FAILED);
                setDescription("Không thể tạo đơn hàng ZaloPay");
                JOptionPane.showMessageDialog(parent, "Không thể tạo đơn hàng ZaloPay", "Lỗi thanh toán", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Mở trình duyệt để thanh toán
            if (!openWebBrowser(paymentUrl)) {
                setDescription("Không thể mở trình duyệt để thanh toán");
                JOptionPane.showMessageDialog(parent, "Không thể mở trình duyệt để thanh toán", "Lỗi thanh toán", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Hiển thị dialog chờ thanh toán
            int option = JOptionPane.showConfirmDialog(
                parent,
                "Vui lòng hoàn tất thanh toán trong trình duyệt.\nBấm OK khi đã thanh toán xong hoặc Cancel để hủy.",
                "Đang chờ thanh toán",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );
            
            if (option == JOptionPane.OK_OPTION) {
                // Kiểm tra trạng thái thanh toán
                int status = checkPaymentStatus();
                if (status == 1) {
                    JOptionPane.showMessageDialog(parent, "Thanh toán thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    return true;
                } else {
                    JOptionPane.showMessageDialog(parent, "Không thể xác nhận thanh toán. Vui lòng kiểm tra lại sau.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            } else {
                setStatus(InvoiceStatusEnum.CANCELLED);
                setDescription("Người dùng đã hủy thanh toán");
                return false;
            }
        } catch (Exception e) {
            setDescription("Lỗi xử lý thanh toán ZaloPay: " + e.getMessage());
            JOptionPane.showMessageDialog(parent, "Lỗi xử lý thanh toán: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    

    
    // /**
    //  * Kiểm tra trạng thái thanh toán từ ZaloPay (giả lập)
    //  * @return true nếu thanh toán đã được xác minh
    //  */
    // public boolean verifyPayment() {
    //     // Kiểm tra trạng thái thực từ ZaloPay
    //     try {
    //         int status = checkPaymentStatus();
    //         return status == 1;
    //     } catch (Exception e) {
    //         e.printStackTrace();
            
    //         // Fallback: Giả lập việc kiểm tra trạng thái thanh toán
    //         Random random = new Random();
    //         boolean verificationResult = random.nextDouble() <= 0.95; // 95% xác suất xác minh thành công
            
    //         if (verificationResult) {
    //             this.isVerified = true;
    //             setStatus(InvoiceStatusEnum.COMPLETED);
    //             setPaymentDate(LocalDateTime.now());
    //         }
            
    //         return this.isVerified;
    //     }
    // }
    
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