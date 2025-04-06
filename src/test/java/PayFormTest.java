// package com.pcstore.view;

// import com.pcstore.model.Invoice;
// import com.pcstore.model.InvoiceDetail;
// import com.pcstore.model.enums.PaymentMethodEnum;
// import java.math.BigDecimal;
// import java.util.List;
// import javax.swing.JDialog;

// /**
//  *
//  * @author MSII
//  */
// public class PayForm extends javax.swing.JDialog {
//     // Thêm các biến này để lưu trạng thái
//     private Invoice currentInvoice;
//     private List<InvoiceDetail> cartItems;
//     private BigDecimal totalAmount;
//     private boolean paymentSuccessful = false;
//     private PaymentMethodEnum selectedPaymentMethod = PaymentMethodEnum.CASH;
    
//     public PayForm(java.awt.Frame parent, boolean modal) {
//         super(parent, modal);
//         initComponents();
//         setLocationRelativeTo(parent);
        
//         // Mặc định chọn thanh toán bằng tiền mặt
//         btnRadioPayCash.setSelected(true);
//         selectedPaymentMethod = PaymentMethodEnum.CASH;
//     }
    
//     // Phương thức để đặt thông tin hóa đơn
//     public void setInvoiceDetails(Invoice invoice, List<InvoiceDetail> items, BigDecimal total) {
//         this.currentInvoice = invoice;
//         this.cartItems = items;
//         this.totalAmount = total;
        
//         // Cập nhật giao diện với thông tin
//         updatePaymentDetails();
//     }
    
//     private void updatePaymentDetails() {
//         // Cập nhật hiển thị số tiền
//         if (txtTotalAmount != null && totalAmount != null) {
//             txtTotalAmount.setText(totalAmount.toString() + " đ");
//         }
//     }
    
//     // Getter để kiểm tra trạng thái thanh toán
//     public boolean isPaymentSuccessful() {
//         return paymentSuccessful;
//     }
    
//     // Getter để lấy phương thức thanh toán
//     public PaymentMethodEnum getSelectedPaymentMethod() {
//         return selectedPaymentMethod;
//     }
    
//     // Sửa phương thức xử lý khi nút thanh toán được nhấn
//     private void btnPayActionPerformed(java.awt.event.ActionEvent evt) {
//         // Xác định phương thức thanh toán dựa trên nút radio
//         if (btnRadioPayCash.isSelected()) {
//             selectedPaymentMethod = PaymentMethodEnum.CASH;
//         } else if (btnRadioPayBank.isSelected()) {
//             selectedPaymentMethod = PaymentMethodEnum.CARD;
//         } else if (btnRadioZaloPay.isSelected()) {
//             selectedPaymentMethod = PaymentMethodEnum.TRANSFER;
//         }
        
//         // Đánh dấu thanh toán thành công
//         paymentSuccessful = true;
        
//         // Đóng dialog
//         dispose();
//     }
    
//     // Sửa xử lý cho nút hủy
//     private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
//         paymentSuccessful = false;
//         dispose();
//     }
    
//     // ... các phương thức khác trong lớp PayForm
// }