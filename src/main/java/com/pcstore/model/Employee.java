package com.pcstore.model;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;

import com.pcstore.model.base.BasePerson;
import com.pcstore.model.enums.EmployeePositionEnum;
import com.pcstore.utils.ErrorMessage;

/**
 * Class biểu diễn nhân viên
 */
public class Employee extends BasePerson {
    private String employeeId;
    private EmployeePositionEnum position;
    private String avatar; //lưu dạng base64
    
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
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY.toString(), "Mã nhân viên"));
        }
        if (!employeeId.matches("NV\\d+")) {
            throw new IllegalArgumentException(ErrorMessage.EMPLOYEE_ID_FORMAT.toString());
        }
        this.employeeId = employeeId;
    }
    
    public EmployeePositionEnum getPosition() {
        return position;
    }
    
    public void setPosition(EmployeePositionEnum position) {
        if (position == null) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY.toString(), "Chức vụ"));
        }
        this.position = position;
    }


    public void setPosition(String position) {
        // System.out.println("Position: " + position);
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY.toString(), "Chức vụ"));
        }
        try {
            for (EmployeePositionEnum pos : EmployeePositionEnum.values()) {
                if (pos.name().equalsIgnoreCase(position)) {
                    this.position = pos;
                    return;
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_EMPLOYEE_POSITION.toString());
        }
    }
    


    @Override
    public void setPhoneNumber(String phoneNumber) {
        if (!isValidPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_PHONE_NUMBER.toString());
        }
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void setEmail(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_EMAIL.toString());
        }
        this.email = email;
    }

    @Override
    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY.toString(), "Họ tên nhân viên"));
        }
        if (fullName.length() < 2) {
            throw new IllegalArgumentException(ErrorMessage.EMPLOYEE_NAME_TOO_SHORT.toString());
        }
        this.fullName = fullName;
    }

    @Override
    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY.toString(), "Địa chỉ"));
        }
        this.address = address;
    }
   
    @Override
    public void setDateOfBirth(Date dateOfBirth) {
        if (dateOfBirth != null) {
            LocalDate today = LocalDate.now();
            LocalDate birthDate = dateOfBirth.toLocalDate();
            if (Period.between(birthDate, today).getYears() < 18) {
                throw new IllegalArgumentException(String.format(ErrorMessage.EMPLOYEE_AGE_18.toString()));
            }else if(Period.between(birthDate, today).getYears() > 70) {
                throw new IllegalArgumentException(String.format(ErrorMessage.EMPLOYEE_AGE_70.toString()));
            }
        }
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getAvatar() {
        return avatar;
    }


    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    // Factory method để tạo nhân viên mới
    public static Employee createNew(String employeeId, String fullName, 
                                   String phoneNumber, String email, EmployeePositionEnum position) {
        Employee employee = new Employee(email, email, email, email, position);
        return employee;
    }
}