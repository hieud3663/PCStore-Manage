package com.pcstore.model;

import com.pcstore.model.base.BasePerson;
import com.pcstore.utils.ErrorMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class biểu diễn khách hàng
 */
public class Customer extends BasePerson {
    private String customerId; // Định dạng: KH01, KH02...
    private List<Invoice> invoices = new ArrayList<>(); // Quan hệ 1-n với hóa đơn
    private List<RepairService> repairServices = new ArrayList<>(); // Quan hệ 1-n với dịch vụ sửa chữa

    @Override
    public Object getId() {
        return customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Mã khách hàng"));
        }
        if (!customerId.matches("KH\\d+")) {
            throw new IllegalArgumentException(ErrorMessage.CUSTOMER_ID_FORMAT);
        }
        this.customerId = customerId;
    }
    
    public List<Invoice> getInvoices() {
        return invoices;
    }
    
    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }
    
    public void addInvoice(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Hóa đơn"));
        }
        this.invoices.add(invoice);
        invoice.setCustomer(this);
    }

    public void removeInvoice(Invoice invoice) {
        if (this.invoices.remove(invoice)) {
            invoice.setCustomer(null);
        }
    }
    
    public List<RepairService> getRepairServices() {
        return repairServices;
    }
    
    public void setRepairServices(List<RepairService> repairServices) {
        this.repairServices = repairServices;
    }
    
    public void addRepairService(RepairService repairService) {
        if (repairService == null) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Dịch vụ sửa chữa"));
        }
        this.repairServices.add(repairService);
        repairService.setCustomer(this);
    }

    public void removeRepairService(RepairService repairService) {
        if (this.repairServices.remove(repairService)) {
            repairService.setCustomer(null);
        }
    }

    @Override
    public boolean isValidEmail() {
        if (email == null || email.trim().isEmpty()) {
            return true; // Email không bắt buộc với khách hàng
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    @Override
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Số điện thoại"));
        }
        String phoneRegex = "^\\d{10,11}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        return pattern.matcher(phoneNumber).matches();
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        if (!isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_PHONE_NUMBER);
        }
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void setEmail(String email) {
        if (email != null && !email.trim().isEmpty() && !isValidEmail()) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_EMAIL);
        }
        this.email = email;
    }

    @Override
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Họ tên khách hàng"));
        }
        if (fullName.length() < 2) {
            throw new IllegalArgumentException(ErrorMessage.CUSTOMER_NAME_TOO_SHORT);
        }
        this.fullName = fullName;
    }

    @Override
    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Địa chỉ"));
        }
        this.address = address;
    }

    // Factory method để tạo khách hàng mới
    public static Customer createNew(String customerId, String fullName, 
                                   String phoneNumber, String address) {
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setFullName(fullName);
        customer.setPhoneNumber(phoneNumber);
        customer.setAddress(address);
        return customer;
    }

}