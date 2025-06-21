package com.pcstore.model;

import java.time.LocalDateTime;
import java.util.List;

import com.pcstore.model.base.BaseTimeEntity;

public class InventoryCheck extends BaseTimeEntity {
    private Integer checkId; 
    private String checkCode;        // Mã phiếu kiểm kê (KK001, KK002...)
    private String checkName;       // Tên phiếu kiểm kê (Kiểm kê kho, Kiểm kê theo danh mục...)
    private Employee employee;       // Nhân viên thực hiện
    private LocalDateTime checkDate; // Ngày chốt kiểm kê
    private String checkType;        // Loại kiểm kê: "FULL", "PARTIAL", "CATEGORY"
    private String status;           // "DRAFT", "IN_PROGRESS", "COMPLETED", "CANCELLED"
    private String notes;            // Ghi chú
    private List<InventoryCheckDetail> details;
    // CreateAt : Thời gian tạo phiếu

    @Override
    public Integer getId() {
        return checkId;
    }
    public void setCheckId(Integer checkId) {
        this.checkId = checkId;
    }
    public String getCheckCode() {
        return checkCode;
    }
    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }
    
    public String getCheckName() {
        return checkName;
    }
    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }
    public Employee getEmployee() {
        return employee;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    public LocalDateTime getCheckDate() {
        return checkDate;
    }
    public void setCheckDate(LocalDateTime checkDate) {
        this.checkDate = checkDate;
    }
    public String getCheckType() {
        return checkType;
    }
    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public List<InventoryCheckDetail> getDetails() {
        return details;
    }
    public void setDetails(List<InventoryCheckDetail> details) {
        this.details = details;
    }
    
    
    
}
    