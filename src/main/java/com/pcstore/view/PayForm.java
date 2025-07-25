/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.pcstore.view;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Year;
import java.util.List;

import javax.swing.JOptionPane;

import com.k33ptoo.components.KButton;
import com.pcstore.controller.PaymentController;
import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.base.BasePayment;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;
import com.pcstore.payment.CashPayment;
import com.pcstore.payment.ZalopayPayment;
//import com.pcstore.payment.ZalopayPayment;
import com.pcstore.utils.LocaleManager;

/**
 *
 * @author MSII
 */
public class PayForm extends javax.swing.JDialog {

    private Invoice currentInvoice;
    private boolean paymentSuccessful = false;
    private PaymentMethodEnum selectedPaymentMethod = PaymentMethodEnum.CASH;
    private PaymentController paymentController;
    private boolean  checkSelectedPaymentMethod = false;


    private BasePayment currentPayment;
  
    private com.k33ptoo.components.KButton btnCancel;
    private javax.swing.ButtonGroup btnGroupPay;
    private com.k33ptoo.components.KButton btnPay;
    private javax.swing.JRadioButton btnRadioPayBank;
    private javax.swing.JRadioButton btnRadioPayCash;
    private javax.swing.JRadioButton btnRadioZaloPay;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private com.k33ptoo.components.KGradientPanel panelBody;
    private javax.swing.JPanel panelFooter;
    private com.k33ptoo.components.KGradientPanel panelForm;
    private javax.swing.JPanel panelHeader;
    private com.k33ptoo.components.KGradientPanel panelTitle;
    private javax.swing.JLabel txtTotalAmount;
    
    public PayForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(parent);
    }
    
    // Giữ constructor cũ cho backward compatibility (tương thích ngược)
    public PayForm() {
        super();
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        btnGroupPay = new javax.swing.ButtonGroup();
        panelForm = new com.k33ptoo.components.KGradientPanel();
        panelHeader = new javax.swing.JPanel();
        panelTitle = new com.k33ptoo.components.KGradientPanel();
        jLabel1 = new javax.swing.JLabel();
        panelBody = new com.k33ptoo.components.KGradientPanel();
        btnRadioPayCash = new javax.swing.JRadioButton();
        btnRadioPayBank = new javax.swing.JRadioButton();
        btnRadioZaloPay = new javax.swing.JRadioButton();
        panelFooter = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtTotalAmount = new javax.swing.JLabel();
        btnPay = new com.k33ptoo.components.KButton();
        btnCancel = new com.k33ptoo.components.KButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = com.pcstore.utils.LocaleManager.getInstance().getResourceBundle(); // NOI18N
        setTitle(bundle.getString("titleConfirmPay")); // NOI18N
        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(450, 485));
        setModalExclusionType(null);
        setType(java.awt.Window.Type.POPUP);

        panelForm.setBackground(new java.awt.Color(255, 255, 255));
        panelForm.setkEndColor(new java.awt.Color(153, 255, 153));
        panelForm.setkStartColor(new java.awt.Color(102, 153, 255));
        panelForm.setMinimumSize(new java.awt.Dimension(611, 485));
        panelForm.setLayout(new javax.swing.BoxLayout(panelForm, javax.swing.BoxLayout.Y_AXIS));

        panelHeader.setBackground(new java.awt.Color(255, 255, 255));
        panelHeader.setMinimumSize(new java.awt.Dimension(300, 55));
        panelHeader.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 30, 5));

        panelTitle.setBackground(new java.awt.Color(255, 255, 255));
        panelTitle.setkBorderRadius(70);
        panelTitle.setkFillBackground(false);
        panelTitle.setName(""); // NOI18N
        panelTitle.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 102, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(bundle.getString("titlePayForm")); // NOI18N
        jLabel1.setMaximumSize(new java.awt.Dimension(250, 40));
        jLabel1.setMinimumSize(new java.awt.Dimension(250, 45));
        jLabel1.setPreferredSize(new java.awt.Dimension(250, 45));
        panelTitle.add(jLabel1, java.awt.BorderLayout.CENTER);

        panelHeader.add(panelTitle);
        panelTitle.getAccessibleContext().setAccessibleName("");

        panelForm.add(panelHeader);

        panelBody.setBackground(new java.awt.Color(255, 255, 255));
        panelBody.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("titleChoosePayment"))); // NOI18N
        panelBody.setkBorderRadius(30);
        panelBody.setkEndColor(new java.awt.Color(255, 255, 255));
        panelBody.setkGradientFocus(900);
        panelBody.setkStartColor(new java.awt.Color(255, 255, 255));
        panelBody.setMinimumSize(new java.awt.Dimension(166, 231));
        panelBody.setOpaque(false);
        panelBody.setPreferredSize(new java.awt.Dimension(166, 231));
        panelBody.setLayout(new java.awt.GridLayout(4, 1, 30, 40));

        btnGroupPay.add(btnRadioPayCash);
        btnRadioPayCash.setText(bundle.getString("btnRadioPayCash")); // NOI18N
        btnRadioPayCash.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRadioPayCash.setIconTextGap(10);
        btnRadioPayCash.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/cash.png"))); // NOI18N
        btnRadioPayCash.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/cash.png"))); // NOI18N
        btnRadioPayCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRadioPayCashActionPerformed(evt);
            }
        });
        panelBody.add(btnRadioPayCash);

        btnGroupPay.add(btnRadioPayBank);
        btnRadioPayBank.setText(bundle.getString("btnRadioPayBank")); // NOI18N
        btnRadioPayBank.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRadioPayBank.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/mobile-banking.png"))); // NOI18N
        btnRadioPayBank.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/mobile-banking.png"))); // NOI18N
        btnRadioPayBank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRadioPayBankActionPerformed(evt);
            }
        });
        panelBody.add(btnRadioPayBank);

        btnGroupPay.add(btnRadioZaloPay);
        btnRadioZaloPay.setText(bundle.getString("btnRadioZaloPay")); // NOI18N
        btnRadioZaloPay.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRadioZaloPay.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/zalopay.png"))); // NOI18N
        btnRadioZaloPay.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/zalopay.png"))); // NOI18N
        btnRadioZaloPay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRadioZaloPayActionPerformed(evt);
            }
        });
        panelBody.add(btnRadioZaloPay);

        panelForm.add(panelBody);

        panelFooter.setBackground(new java.awt.Color(255, 255, 255));
        panelFooter.setPreferredSize(new java.awt.Dimension(367, 100));
        panelFooter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 5));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText(bundle.getString("lbTotalAmount")); // NOI18N
        panelFooter.add(jLabel2);

        txtTotalAmount.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        txtTotalAmount.setForeground(new java.awt.Color(0, 204, 51));
        txtTotalAmount.setText("0 đ");
        panelFooter.add(txtTotalAmount);

        btnPay.setText(bundle.getString("btnPayConfirm")); // NOI18N
        btnPay.setToolTipText("");
        btnPay.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnPay.setIconTextGap(3);
        btnPay.setkBorderRadius(20);
        btnPay.setkEndColor(new java.awt.Color(153, 255, 153));
        btnPay.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnPay.setkHoverStartColor(new java.awt.Color(0, 153, 153));
        btnPay.setkStartColor(new java.awt.Color(102, 153, 255));
        btnPay.setPreferredSize(new java.awt.Dimension(100, 45));
        panelFooter.add(btnPay);

        btnCancel.setText(bundle.getString("btnCancle")); // NOI18N
        btnCancel.setToolTipText("");
        btnCancel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnCancel.setIconTextGap(3);
        btnCancel.setkBorderRadius(20);
        btnCancel.setkEndColor(new java.awt.Color(255, 102, 0));
        btnCancel.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnCancel.setkHoverStartColor(new java.awt.Color(0, 153, 153));
        btnCancel.setkStartColor(new java.awt.Color(255, 51, 51));
        btnCancel.setMinimumSize(new java.awt.Dimension(65, 27));
        btnCancel.setPreferredSize(new java.awt.Dimension(65, 45));
        btnCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCancelMouseClicked(evt);
            }
        });
        panelFooter.add(btnCancel);

        panelForm.add(panelFooter);

        getContentPane().add(panelForm, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void btnCancelMouseClicked(java.awt.event.MouseEvent evt) {
        this.dispose();
        paymentSuccessful = false;
    }

    private void btnRadioPayCashActionPerformed(java.awt.event.ActionEvent evt) {
        checkSelectedPaymentMethod = true;
        selectedPaymentMethod = PaymentMethodEnum.CASH;
    }

    private void btnRadioPayBankActionPerformed(java.awt.event.ActionEvent evt) {
        checkSelectedPaymentMethod = true;
        selectedPaymentMethod = PaymentMethodEnum.BANK_TRANSFER;
    }

    private void btnRadioZaloPayActionPerformed(java.awt.event.ActionEvent evt) {
        checkSelectedPaymentMethod = true;
        selectedPaymentMethod = PaymentMethodEnum.ZALOPAY;
    }
    
    public KButton getBtnPay() {
        return btnPay;
    }

    public Invoice getCurrentInvoice() {
        return currentInvoice;
    }

    public void setCurrentInvoice(Invoice currentInvoice) {
        this.currentInvoice = currentInvoice;
        
        NumberFormat formatter = LocaleManager.getInstance().getNumberFormatter();
        this.txtTotalAmount.setText(formatter.format(currentInvoice.getTotalAmount()) + " đ");
    }

    public boolean isPaymentSuccessful() {
        return paymentController != null && paymentController.isPaymentSuccessful();
    }
    
    public PaymentMethodEnum getSelectedPaymentMethod() {
        return selectedPaymentMethod;
    }

    public void setSelectedPaymentMethod(PaymentMethodEnum selectedPaymentMethod) {
        this.selectedPaymentMethod = selectedPaymentMethod;
    }

    public boolean isCheckSelectedPaymentMethod() {
        return checkSelectedPaymentMethod;
    }
    public void setCheckSelectedPaymentMethod(boolean checkSelectedPaymentMethod) {
        this.checkSelectedPaymentMethod = checkSelectedPaymentMethod;
    }
    

    public BasePayment getCurrentPayment() {
        return paymentController != null ? paymentController.getCurrentPayment() : null;
    }

    public void setPaymentController(PaymentController paymentController) {
        this.paymentController = paymentController;
    }

    public void setCurrentPayment(BasePayment currentPayment) {
        this.currentPayment = currentPayment;
    }

}
