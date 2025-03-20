package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import com.pcstore.model.enums.InvoiceStatusEnum;
import com.pcstore.model.enums.PaymentMethodEnum;
import com.pcstore.utils.ErrorMessage;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class biểu diễn hóa đơn
 */
public class Invoice extends BaseTimeEntity {
    private Integer invoiceId;
    private Customer customer;
    private Employee employee;
    private BigDecimal totalAmount;
    private LocalDateTime invoiceDate;
    private InvoiceStatusEnum status;
    private PaymentMethodEnum paymentMethod;
    private List<InvoiceDetail> invoiceDetails = new ArrayList<>();

    @Override
    public Object getId() {
        return invoiceId;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException(ErrorMessage.INVOICE_CUSTOMER_NULL);
        }
        this.customer = customer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException(ErrorMessage.INVOICE_EMPLOYEE_NULL);
        }
        this.employee = employee;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        if (totalAmount == null) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Tổng tiền"));
        }
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(ErrorMessage.INVOICE_TOTAL_NEGATIVE);
        }
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        if (invoiceDate == null) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Ngày lập hóa đơn"));
        }
        this.invoiceDate = invoiceDate;
    }

    public InvoiceStatusEnum getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatusEnum status) {
        if (status == null) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Trạng thái hóa đơn"));
        }
        if (!canChangeStatus(status)) {
            throw new IllegalStateException("Không thể chuyển sang trạng thái " + status);
        }
        this.status = status;
    }

    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Phương thức thanh toán"));
        }
        this.paymentMethod = paymentMethod;
    }

    public List<InvoiceDetail> getInvoiceDetails() {
        return invoiceDetails;
    }

    public void setInvoiceDetails(List<InvoiceDetail> invoiceDetails) {
        this.invoiceDetails = invoiceDetails;
    }
    
    public void addInvoiceDetail(InvoiceDetail invoiceDetail) {
        if (invoiceDetail == null) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Chi tiết hóa đơn"));
        }
        if (!canUpdate()) {
            throw new IllegalStateException("Không thể thêm chi tiết cho hóa đơn đã hoàn thành hoặc đã hủy");
        }
        if (isValidDetail(invoiceDetail)) {
            invoiceDetails.add(invoiceDetail);
            invoiceDetail.setInvoice(this);
            updateTotalAmount();
        }
    }
    
    public void removeInvoiceDetail(InvoiceDetail detail) {
        if (!canUpdate()) {
            throw new IllegalStateException("Không thể xóa chi tiết của hóa đơn đã hoàn thành hoặc đã hủy");
        }
        if (invoiceDetails.remove(detail)) {
            detail.setInvoice(null);
            updateTotalAmount();
        }
    }

    // Phương thức tính toán tổng tiền hóa đơn từ các chi tiết
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceDetail detail : invoiceDetails) {
            total = total.add(detail.getSubtotal());
        }
        return total;
    }
    
    // Phương thức cập nhật tổng tiền hóa đơn
    public void updateTotalAmount() {
        setTotalAmount(calculateTotal());
    }

    // Kiểm tra chi tiết hóa đơn hợp lệ
    private boolean isValidDetail(InvoiceDetail detail) {
        if (detail.getProduct() == null) {
            throw new IllegalArgumentException(ErrorMessage.PRODUCT_NULL);
        }
        if (detail.getQuantity() <= 0) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_NEGATIVE, "Số lượng"));
        }
        if (!detail.getProduct().hasEnoughStock(detail.getQuantity())) {
            throw new IllegalArgumentException("Số lượng tồn kho không đủ");
        }
        if (detail.getUnitPrice() == null || detail.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_NEGATIVE, "Đơn giá"));
        }
        return true;
    }

    // Kiểm tra có thể cập nhật hóa đơn
    public boolean canUpdate() {
        return status == InvoiceStatusEnum.PENDING || status == InvoiceStatusEnum.PROCESSING;
    }

    // Kiểm tra có thể chuyển sang trạng thái mới
    private boolean canChangeStatus(InvoiceStatusEnum newStatus) {
        if (status == null) {
            return true;
        }
        
        switch (status) {
            case PENDING:
                return newStatus == InvoiceStatusEnum.PROCESSING || 
                       newStatus == InvoiceStatusEnum.CANCELLED;
            case PROCESSING:
                return newStatus == InvoiceStatusEnum.COMPLETED || 
                       newStatus == InvoiceStatusEnum.CANCELLED;
            case COMPLETED:
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }

    // Phương thức xử lý hoàn thành hóa đơn
    public void complete() {
        if (invoiceDetails.isEmpty()) {
            throw new IllegalStateException(ErrorMessage.INVOICE_DETAILS_EMPTY);
        }
        if (status != InvoiceStatusEnum.PROCESSING) {
            throw new IllegalStateException("Chỉ có thể hoàn thành hóa đơn đang xử lý");
        }
        
        // Cập nhật tồn kho
        for (InvoiceDetail detail : invoiceDetails) {
            Product product = detail.getProduct();
            if (!product.hasEnoughStock(detail.getQuantity())) {
                throw new IllegalStateException("Sản phẩm " + product.getProductName() + 
                    " không đủ số lượng tồn kho");
            }
            product.decreaseStock(detail.getQuantity());
        }
        
        setStatus(InvoiceStatusEnum.COMPLETED);
    }

    // Phương thức hủy hóa đơn
    public void cancel() {
        if (!canUpdate()) {
            throw new IllegalStateException("Không thể hủy hóa đơn trong trạng thái hiện tại");
        }
        setStatus(InvoiceStatusEnum.CANCELLED);
    }

    // Factory method để tạo hóa đơn mới
    public static Invoice createNew(Customer customer, Employee employee) {
        if (customer == null) {
            throw new IllegalArgumentException(ErrorMessage.INVOICE_CUSTOMER_NULL);
        }
        if (employee == null) {
            throw new IllegalArgumentException(ErrorMessage.INVOICE_EMPLOYEE_NULL);
        }
        
        Invoice invoice = new Invoice();
        invoice.setCustomer(customer);
        invoice.setEmployee(employee);
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setStatus(InvoiceStatusEnum.PENDING);
        invoice.setTotalAmount(BigDecimal.ZERO);
        return invoice;
    }
}