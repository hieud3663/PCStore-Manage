import java.math.BigDecimal;

import com.pcstore.payment.ZalopayPayment;

public class testUnirest {

        /**
     * Main method for testing ZaloPay payment functionality
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("=== ZaloPay Payment Test ===");
        
        try {
            // Create a test payment with amount
            BigDecimal amount = new BigDecimal("100000"); // 100,000 VND
            ZalopayPayment payment = new ZalopayPayment(null, amount);
            
            System.out.println("Created payment: " + payment);
            System.out.println("Amount: " + amount + " VND");
            
            // Process payment (create order and open browser)
            System.out.println("\nCreating ZaloPay order...");
            String paymentUrl = payment.createZaloPayOrder();
            
            if (paymentUrl != null) {
                System.out.println("Payment URL: " + paymentUrl);
                System.out.println("Opening payment URL in browser...");
                payment.openWebBrowser(paymentUrl);
                
                // Wait for user to complete payment
                System.out.println("\nPlease complete the payment in your browser.");
                System.out.println("Press Enter when you have completed the payment or to check status...");
                new java.util.Scanner(System.in).nextLine();
                
                // Check payment status
                System.out.println("\nChecking payment status...");
                int status = payment.checkPaymentStatus();
                
                switch (status) {
                    case 1:
                        System.out.println("Payment successful!");
                        System.out.println("Transaction ID: " + payment.getAppTransactionId());
                        break;
                    case 0:
                        System.out.println("Payment is pending. Please try again later.");
                        break;
                    case -1:
                        System.out.println("Error checking payment status.");
                        break;
                    case -2:
                        System.out.println("Payment was cancelled.");
                        break;
                    default:
                        System.out.println("Unknown status: " + status);
                }
            } else {
                System.out.println("Failed to create ZaloPay order.");
            }
            
        } catch (Exception e) {
            System.err.println("Error in ZaloPay payment test: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== Test completed ===");
    }
}