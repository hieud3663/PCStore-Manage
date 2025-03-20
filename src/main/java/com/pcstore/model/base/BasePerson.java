package com.pcstore.model.base;

import java.util.regex.Pattern;

/**
 * Abstract class cơ sở cho các entity có thông tin người dùng (khách hàng, nhân viên)
 */
public abstract class BasePerson extends BaseTimeEntity {
    protected String fullName;
    protected String phoneNumber;
    protected String email;
    protected String address;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        String phoneRegex = "^\\d{10,11}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        return pattern.matcher(phoneNumber).matches();
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    // Method kiểm tra tính hợp lệ của email
    public abstract boolean isValidEmail();
    
    // Method kiểm tra tính hợp lệ của số điện thoại
    // public abstract boolean isValidPhoneNumber(String phoneNumber);
}