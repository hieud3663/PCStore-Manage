package test.model;

// import model.enums.EmployeePositionEnum;

public class Employee {
    private String employeeId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private EmployeePositionEnum position;

    // Getters and setters
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
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

    public EmployeePositionEnum getPosition() {
        return position;
    }

    public void setPosition(EmployeePositionEnum position) {
        this.position = position;
    }
}