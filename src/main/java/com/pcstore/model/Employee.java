package com.pcstore.model;

import com.pcstore.model.base.BasePerson;
import com.pcstore.model.enums.EmployeePositionEnum;
import com.pcstore.utils.ErrorMessage;
import java.util.regex.Pattern;

/**
 * Class biểu diễn nhân viên
 */
public class Employee extends BasePerson {
    private String employeeId;
    private EmployeePositionEnum position;
    
    private static String currentID;

    public Employee(String employeeId, String fullName, String phoneNumber, String email,
            EmployeePositionEnum position) {
        super(fullName, phoneNumber, email);
        currentID = employeeId;
        this.employeeId = employeeId;
        this.position = position;
    }


    public Employee(String employeeId, String fullName, String phoneNumber, String email,
            String position) {
        super(fullName, phoneNumber, email);
        currentID = employeeId;
        this.employeeId = employeeId;
        this.setPosition(position);
    }

    public Employee() {
        //TODO Auto-generated constructor stub
    }


    @Override
    public Object getId() {
        return employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Mã nhân viên"));
        }
        if (!employeeId.matches("NV\\d+")) {
            throw new IllegalArgumentException(ErrorMessage.EMPLOYEE_ID_FORMAT);
        }
        this.employeeId = employeeId;
    }
    
    public EmployeePositionEnum getPosition() {
        return position;
    }
    
    public void setPosition(EmployeePositionEnum position) {
        if (position == null) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Chức vụ"));
        }
        this.position = position;
    }


    public void setPosition(String position) {
        // System.out.println("Position: " + position);
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Chức vụ"));
        }
        try {
            for (EmployeePositionEnum pos : EmployeePositionEnum.values()) {
                if (pos.name().equalsIgnoreCase(position)) {
                    this.position = pos;
                    return;
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_EMPLOYEE_POSITION);
        }
    }
    

    @Override
    public boolean isValidEmail() {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Email"));
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    @Override
    public boolean isValidPhoneNumber(String phoneNumber){
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
        if (!isValidEmail()) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_EMAIL);
        }
        this.email = email;
    }

    @Override
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Họ tên nhân viên"));
        }
        if (fullName.length() < 2) {
            throw new IllegalArgumentException(ErrorMessage.EMPLOYEE_NAME_TOO_SHORT);
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

    // Factory method để tạo nhân viên mới
    public static Employee createNew(String employeeId, String fullName, 
                                   String phoneNumber, String email, EmployeePositionEnum position) {
        Employee employee = new Employee(email, email, email, email, position);
        return employee;
    }
}