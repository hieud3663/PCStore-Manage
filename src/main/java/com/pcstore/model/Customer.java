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

    public Customer(String fullName, String phoneNumber, String email) {
        super(fullName, phoneNumber, email);
        //TODO Auto-generated constructor stub
    }

    public Customer() {
        super();
    }

    private String customerId; // Định dạng: KH01, KH02...

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
                                   String phoneNumber) {
        Customer customer = new Customer(fullName, phoneNumber, null);
        customer.setCustomerId(customerId);
        return customer;
    }

}