package com.pcstore.payment;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;
import vn.payos.PayOS;
import vn.payos.type.PaymentData;
import vn.payos.type.ItemData;

public class BankPayment {

    private String bankCode;
    private String orderId;
    private String amount;
    private String orderInfo;
    private String returnUrl;
    private String requestId;
    private String signature;

    PayOS payOS;


    public BankPayment(){
        payOS = new PayOS("","","");
    }


    public static void main(String[] args) throws Exception {
        String webhookUrl = "https://example.com/webhook"; // Replace with your actual webhook URL
        Long orderCode = 99L; // Replace with your actual order code

        PayOS payOS = new PayOS("your-client-id", "your-client-secret", "your-merchant-code");
        ItemData itemData = ItemData.builder().name("Mỳ tôm Hảo Hảo ly").quantity(1).price(2000).build();
        PaymentData paymentData = PaymentData.builder().orderCode(orderCode).amount(2000)
                .description("Thanh toán đơn hàng").returnUrl(webhookUrl + "/success").cancelUrl(webhookUrl + "/cancel")
                .item(itemData).build();
                
        CheckoutResponseData result = payOS.createPaymentLink(paymentData);
        result.getCheckoutUrl();
    }
    
    
}