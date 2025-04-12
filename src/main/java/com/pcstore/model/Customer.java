package com.pcstore.model;

import com.pcstore.model.base.BasePerson;
import com.pcstore.utils.ErrorMessage;

/**
 * Class biểu diễn khách hàng
 */
public class Customer extends BasePerson {

    private Integer points;
    private String customerId; // Định dạng: KH01, KH02...


    public Customer(String fullName, String phoneNumber, String email) {
        super(fullName, phoneNumber, email);
        points = 0;
        //TODO Auto-generated constructor stub
    }
    
    public Customer() {
        super();
        points = 0;
    }
    

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

        if (customerId.equalsIgnoreCase("GUEST")) {
            this.customerId = customerId;
            return;
        }

        this.customerId = customerId;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        if(!isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_PHONE_NUMBER);
        }
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
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
        this.address = address;
    }

    // Factory method để tạo khách hàng mới
    public static Customer createNew(String customerId, String fullName, 
                                   String phoneNumber) {
        Customer customer = new Customer(fullName, phoneNumber, null);
        customer.setCustomerId(customerId);
        return customer;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

}