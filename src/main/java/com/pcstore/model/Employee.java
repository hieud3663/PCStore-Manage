package com.pcstore.model;

import com.pcstore.model.base.BasePerson;
import com.pcstore.model.enums.EmployeePositionEnum;
import com.pcstore.utils.ErrorMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class biểu diễn nhân viên
 */
public class Employee extends BasePerson {
    private String employeeId;
    private EmployeePositionEnum position;
    private List<Invoice> invoices = new ArrayList<>(); // Nhân viên lập nhiều hóa đơn
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>(); // Nhân viên lập nhiều phiếu nhập hàng
    private List<RepairService> repairServices = new ArrayList<>(); // Nhân viên phụ trách nhiều dịch vụ sửa chữa
    private User user; // Một nhân viên có thể có một tài khoản

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
        invoice.setEmployee(this);
    }
    
    public void removeInvoice(Invoice invoice) {
        if (this.invoices.remove(invoice)) {
            invoice.setEmployee(null);
        }
    }
    
    public List<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }
    
    public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }
    
    public void addPurchaseOrder(PurchaseOrder purchaseOrder) {
        if (purchaseOrder == null) {
            throw new IllegalArgumentException(String.format(ErrorMessage.FIELD_EMPTY, "Đơn nhập hàng"));
        }
        this.purchaseOrders.add(purchaseOrder);
        purchaseOrder.setEmployee(this);
    }
    
    public void removePurchaseOrder(PurchaseOrder purchaseOrder) {
        if (this.purchaseOrders.remove(purchaseOrder)) {
            purchaseOrder.setEmployee(null);
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
        repairService.setEmployee(this);
    }
    
    public void removeRepairService(RepairService repairService) {
        if (this.repairServices.remove(repairService)) {
            repairService.setEmployee(null);
        }
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        // Xử lý mối quan hệ hai chiều
        if (this.user != user) {
            User oldUser = this.user;
            this.user = user;
            
            if (oldUser != null) {
                oldUser.setEmployee(null);
            }
            
            if (user != null && user.getEmployee() != this) {
                user.setEmployee(this);
            }
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
                                   String phoneNumber, String email, 
                                   String address, EmployeePositionEnum position) {
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setFullName(fullName);
        employee.setPhoneNumber(phoneNumber);
        employee.setEmail(email);
        employee.setAddress(address);
        employee.setPosition(position);
        return employee;
    }
}