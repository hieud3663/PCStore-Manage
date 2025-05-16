package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import com.pcstore.utils.ErrorMessage;

/**
 * Class biểu diễn nhà cung cấp
 */
public class Supplier extends BaseTimeEntity {
    private String supplierId;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    


    
    public Supplier(String supplierId, String name, String phoneNumber, String email, String address) {
        this.supplierId = supplierId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    public Supplier() {
    }

    @Override
    public Object getId() {
        return supplierId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        if (supplierId == null || supplierId.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.FIELD_EMPTY.formatted("Mã nhà cung cấp"));
        }
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }
    public String getSupplierName() {
        return getName();
    }
    @Override
    public String toString() {
    return getName();
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.SUPPLIER_NAME_EMPTY);
        }
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException(ErrorMessage.SUPPLIER_PHONE_INVALID);
        }
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !email.trim().isEmpty() && !isValidEmail(email)) {
            throw new IllegalArgumentException(ErrorMessage.SUPPLIER_EMAIL_INVALID);
        }
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.FIELD_EMPTY.formatted("Địa chỉ"));
        }
        this.address = address;
    }


    // Phương thức kiểm tra số điện thoại hợp lệ
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{10,11}");
    }

    // Phương thức kiểm tra email hợp lệ
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

}