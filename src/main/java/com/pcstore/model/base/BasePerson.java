package com.pcstore.model.base;

import java.util.regex.Pattern;

import com.pcstore.utils.ErrorMessage;

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

    public BasePerson(String fullName, String phoneNumber, String email, String address) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    public BasePerson(String fullName, String phoneNumber, String email) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public BasePerson() {
        //TODO Auto-generated constructor stub
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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
    
   public boolean isValidEmail() {
        if (email == null || email.trim().isEmpty()) {
            return true; // Email không bắt buộc với khách hàng
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Số điện thoại"));
        }
        String phoneRegex = "^\\d{10,11}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        return pattern.matcher(phoneNumber).matches();
    }
    
    // Method kiểm tra tính hợp lệ của số điện thoại
    // public abstract boolean isValidPhoneNumber(String phoneNumber);
}