package com.pcstore.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import com.fasterxml.jackson.databind.JsonSerializable.Base;
import com.pcstore.model.Invoice;
import com.pcstore.model.base.BasePayment;
import com.pcstore.model.enums.PaymentMethodEnum;
import com.pcstore.payment.BankPayment;
import com.pcstore.payment.CashPayment;
import com.pcstore.payment.ZalopayPayment;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.view.PayForm;

/**
 * Controller xử lý các hoạt động liên quan đến thanh toán
 * @author YourName
 */
public class PaymentController {
    private PayForm paymentForm;
    private Invoice invoice;
    private BasePayment currentPayment;
    private boolean paymentSuccessful = false;
    
    /**
     * Khởi tạo controller thanh toán
     * @param paymentForm Form thanh toán
     * @param invoice Hóa đơn cần thanh toán
     */
    public PaymentController(PayForm paymentForm, Invoice invoice) {
        this.paymentForm = paymentForm;
        this.invoice = invoice;
        this.paymentForm.setCurrentInvoice(invoice);
        setUpEvent();
    }
    
    /**
     * Xử lý yêu cầu thanh toán từ form
     * @param paymentMethod Phương thức thanh toán được chọn
     * @return Đối tượng thanh toán nếu thành công, null nếu thất bại
     */
    public BasePayment processPayment(PaymentMethodEnum paymentMethod) {
        switch(paymentMethod) {
            case CASH:
                return processCashPayment();
            case ZALOPAY:
                return processZaloPayPayment();
            case BANK_TRANSFER:
                return processBankTransferPayment();
            default:
                return null;
        }
    }
    
    //Set sự kiện 
    private void setUpEvent(){
        paymentForm.getBtnPay().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                btnPayMouseClicked(evt);
            }
        });
    }


    private void btnPayMouseClicked(MouseEvent evt) {
        if (!paymentForm.isCheckSelectedPaymentMethod()) {
            paymentForm.setSelectedPaymentMethod(PaymentMethodEnum.CASH);
        }
        
       processPayment(paymentForm.getSelectedPaymentMethod());
    }

     /**
     * Xử lý thanh toán bằng tiền mặt
     * @return Đối tượng thanh toán tiền mặt
     */
    private BasePayment processCashPayment() {
        currentPayment = new CashPayment(invoice);

        if (!currentPayment.processPayment(paymentForm)) {
            JOptionPane.showMessageDialog(paymentForm, 
                currentPayment.getDescription(), 
                ErrorMessage.ERROR_TITLE, 
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        // Hiển thị thông tin tiền thừa
        CashPayment cashPayment = (CashPayment) currentPayment;
        String message = String.format(ErrorMessage.PAYMENT_SUCCESS_CHANGE, cashPayment.getChange());
        JOptionPane.showMessageDialog(paymentForm, message, ErrorMessage.INFO_TITLE, JOptionPane.INFORMATION_MESSAGE);
        
        confirmPayment("Cash");
        return cashPayment;
    }

    /**
     * Xử lý thanh toán ZaloPay
     * @return Đối tượng thanh toán ZaloPay
     */
    private BasePayment processZaloPayPayment() {
        currentPayment = new ZalopayPayment(invoice);

        if (!currentPayment.processPayment(paymentForm)) {
            JOptionPane.showMessageDialog(paymentForm, 
                currentPayment.getDescription(), 
                ErrorMessage.ERROR_TITLE, 
                JOptionPane.ERROR_MESSAGE);

            paymentSuccessful = false;
            return null;
        }

        paymentSuccessful = true;
        confirmPayment("ZaloPay");
        return currentPayment;
    }
      /**
     * Xử lý thanh toán chuyển khoản
     */
    private BasePayment processBankTransferPayment() {
        currentPayment = new BankPayment(invoice);
        if (!currentPayment.processPayment(paymentForm)) {
            JOptionPane.showMessageDialog(paymentForm, 
                currentPayment.getDescription(), 
                ErrorMessage.ERROR_TITLE, 
                JOptionPane.ERROR_MESSAGE);
            paymentSuccessful = false;
            return null;
        }

        paymentSuccessful = true;
        confirmPayment("Bank Transfer");
        JOptionPane.showMessageDialog(paymentForm, 
            currentPayment.getDescription(), 
            ErrorMessage.INFO_TITLE, 
            JOptionPane.INFORMATION_MESSAGE);
        return currentPayment;
    }
      /**
     * Xác nhận thanh toán
     * @param paymentMethodName Tên phương thức thanh toán
     * @return true nếu xác nhận thành công, false nếu ngược lại
     */
    private boolean confirmPayment(String paymentMethodName) {
        int confirm = JOptionPane.NO_OPTION;
        if (paymentMethodName.equalsIgnoreCase("Cash")) {
            // Xác nhận hoàn thành giao dịch
            confirm = JOptionPane.showConfirmDialog(
                paymentForm,
                ErrorMessage.PAYMENT_CONFIRM,
                ErrorMessage.PAYMENT_CONFIRM_TITLE,
                JOptionPane.YES_NO_OPTION
            );
        } else {
            confirm = JOptionPane.YES_OPTION;
        }
        
        if (confirm == JOptionPane.YES_OPTION) {
            paymentSuccessful = true;
            paymentForm.dispose();
            return true;
        } else {
            paymentSuccessful = false;
            return false;
        }
    }
    
    /**
     * Hiển thị form thanh toán
     */
    public void showPaymentForm() {
        paymentForm.setVisible(true);
    }
    
    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }
    
    public BasePayment getCurrentPayment() {
        return currentPayment;
    }
    
    public void setCurrentPayment(BasePayment currentPayment) {
        this.currentPayment = currentPayment;
    }
    
    public Invoice getInvoice() {
        return invoice;
    }
}