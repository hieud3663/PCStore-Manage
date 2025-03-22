package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Class biểu diễn dịch vụ sửa chữa
 */
public class RepairService extends BaseTimeEntity {
    private Integer repairServiceId;
    private Customer customer;
    private Employee employee;
    private Warranty warranty;
    private String description;
    private String diagnosis;
    private LocalDateTime receiveDate;
    private LocalDateTime completionDate;
    private BigDecimal serviceFee;
    private String status; // Pending, In Progress, Completed, Cancelled
    private String notes;

    @Override
    public Object getId() {
        return repairServiceId;
    }

    public Integer getRepairServiceId() {
        return repairServiceId;
    }

    public void setRepairServiceId(Integer repairServiceId) {
        this.repairServiceId = repairServiceId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Khách hàng không được để trống");
        }
        this.customer = customer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Nhân viên không được để trống");
        }
        this.employee = employee;
    }

    public Warranty getWarranty() {
        return warranty;
    }

    public void setWarranty(Warranty warranty) {
        this.warranty = warranty;
        if (warranty != null) {
            warranty.setRepairServiceId(repairServiceId);
            // Nếu còn bảo hành, phí dịch vụ = 0
            if (warranty.isValid()) {
                setServiceFee(BigDecimal.ZERO);
            }
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Mô tả vấn đề không được để trống");
        }
        this.description = description;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public LocalDateTime getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(LocalDateTime receiveDate) {
        if (receiveDate == null) {
            throw new IllegalArgumentException("Ngày nhận không được để trống");
        }
        if (completionDate != null && receiveDate.isAfter(completionDate)) {
            throw new IllegalArgumentException("Ngày nhận không thể sau ngày hoàn thành");
        }
        this.receiveDate = receiveDate;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        if (completionDate != null && receiveDate != null && 
            completionDate.isBefore(receiveDate)) {
            throw new IllegalArgumentException("Ngày hoàn thành không thể trước ngày nhận");
        }
        this.completionDate = completionDate;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        if (serviceFee == null) {
            throw new IllegalArgumentException("Phí dịch vụ không được để trống");
        }
        if (serviceFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Phí dịch vụ không được âm");
        }
        // Nếu còn bảo hành, không tính phí
        if (warranty != null && warranty.isValid() && 
            serviceFee.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Không tính phí cho sửa chữa trong bảo hành");
        }
        this.serviceFee = serviceFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái không được để trống");
        }
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ");
        }
        if (!canChangeStatus(status)) {
            throw new IllegalStateException("Không thể chuyển sang trạng thái " + status);
        }
        
        // Cập nhật ngày hoàn thành khi trạng thái là Completed
        if ("Completed".equals(status) && this.completionDate == null) {
            this.completionDate = LocalDateTime.now();
        }
        
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Kiểm tra trạng thái có hợp lệ không
    private boolean isValidStatus(String status) {
        return "Pending".equals(status) ||
               "In Progress".equals(status) ||
               "Completed".equals(status) ||
               "Cancelled".equals(status);
    }

    // Kiểm tra có thể chuyển sang trạng thái mới không
    private boolean canChangeStatus(String newStatus) {
        if (status == null) {
            return true;
        }

        switch (status) {
            case "Pending":
                return "In Progress".equals(newStatus) || 
                       "Cancelled".equals(newStatus);
            case "In Progress":
                return "Completed".equals(newStatus) || 
                       "Cancelled".equals(newStatus);
            case "Completed":
            case "Cancelled":
                return false;
            default:
                return false;
        }
    }

    // Kiểm tra dịch vụ sửa chữa có thể cập nhật không
    public boolean canUpdate() {
        return "Pending".equals(status) || "In Progress".equals(status);
    }

    // Xử lý khi bắt đầu sửa chữa
    public void startRepair() {
        if (!"Pending".equals(status)) {
            throw new IllegalStateException("Chỉ có thể bắt đầu sửa chữa khi đang ở trạng thái chờ xử lý");
        }
        setStatus("In Progress");
    }

    // Xử lý khi hoàn thành sửa chữa
    public void complete() {
        if (!"In Progress".equals(status)) {
            throw new IllegalStateException("Chỉ có thể hoàn thành sửa chữa khi đang trong tiến trình");
        }
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            throw new IllegalStateException("Phải có chẩn đoán trước khi hoàn thành");
        }
        setStatus("Completed");
    }

    // Xử lý khi hủy sửa chữa
    public void cancel() {
        if (!canUpdate()) {
            throw new IllegalStateException("Không thể hủy dịch vụ sửa chữa trong trạng thái hiện tại");
        }
        setStatus("Cancelled");
    }

    // Factory method để tạo dịch vụ sửa chữa mới
    public static RepairService createNew(Customer customer, Employee employee,
                                        String description, Warranty warranty) {
        RepairService service = new RepairService();
        service.setCustomer(customer);
        service.setEmployee(employee);
        service.setDescription(description);
        service.setReceiveDate(LocalDateTime.now());
        service.setStatus("Pending");
        service.setServiceFee(BigDecimal.ZERO);
        
        if (warranty != null) {
            service.setWarranty(warranty);
        }
        
        return service;
    }
}