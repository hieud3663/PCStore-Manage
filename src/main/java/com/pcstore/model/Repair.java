package com.pcstore.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pcstore.model.base.BaseTimeEntity;
import com.pcstore.model.enums.RepairEnum;

/**
 * Class biểu diễn dịch vụ sửa chữa
 */
public class Repair extends BaseTimeEntity {
    private Integer repairServiceId;
    private Customer customer;
    private Employee employee;
    private Warranty warranty;
    private String deviceName;
    private String problem;
    private String diagnosis;
    private LocalDateTime receiveDate;
    private LocalDateTime completionDate;
    private BigDecimal serviceFee;
    private RepairEnum status; // Thay đổi từ String sang RepairEnum
    private String notes;

    

    public Repair(Integer repairServiceId, Customer customer, Employee employee, Warranty warranty,
            String deviceName, String problem, String diagnosis, LocalDateTime receiveDate,
            LocalDateTime completionDate, BigDecimal serviceFee, RepairEnum status, String notes) {
        this.repairServiceId = repairServiceId;
        this.customer = customer;
        this.employee = employee;
        this.warranty = warranty;
        this.deviceName = deviceName;
        this.problem = problem;
        this.diagnosis = diagnosis;
        this.receiveDate = receiveDate;
        this.completionDate = completionDate;
        this.serviceFee = serviceFee;
        this.status = status;
        this.notes = notes;
    }

    public Repair(Customer customer, Employee employee, Warranty warranty,
            String deviceName, String problem, String diagnosis, LocalDateTime receiveDate,
            LocalDateTime completionDate, BigDecimal serviceFee, RepairEnum status, String notes) {
        this.customer = customer;
        this.employee = employee;
        this.warranty = warranty;
        this.deviceName = deviceName;
        this.problem = problem;
        this.diagnosis = diagnosis;
        this.receiveDate = receiveDate;
        this.completionDate = completionDate;
        this.serviceFee = serviceFee;
        this.status = status;
        this.notes = notes;
    }


    

    public Repair(Customer customer, Employee employee, Warranty warranty, String deviceName, String problem,
            String diagnosis) {
        this.customer = customer;
        this.employee = employee;
        this.warranty = warranty;
        this.deviceName = deviceName;
        this.problem = problem;
        this.diagnosis = diagnosis;
        this.receiveDate = LocalDateTime.now();
        this.completionDate = null;
        this.serviceFee = BigDecimal.ZERO;
        this.status = RepairEnum.RECEIVED;
        this.notes = null;
    }

    public Repair(Customer customer2, Employee employee2, String description, Warranty warranty2) {
        this.customer = customer2;
        this.employee = employee2;
        this.warranty = warranty2;
        this.deviceName = null;
        this.problem = null;
        this.diagnosis = null;
        this.receiveDate = LocalDateTime.now();
        this.completionDate = null;
        this.serviceFee = BigDecimal.ZERO;
        this.status = RepairEnum.RECEIVED;
        this.notes = description;
    }

    public Repair() {
        //TODO Auto-generated constructor stub
    }

    public Repair(Integer repairServiceId2, Customer customer2, String deviceName2, String problem2, String diagnosis2,
            BigDecimal fee, RepairEnum repairEnum, String notes2, LocalDateTime createdAt, LocalDateTime now) {
        this.repairServiceId = repairServiceId2;
        this.customer = customer2;
        this.deviceName = deviceName2;
        this.problem = problem2;
        this.diagnosis = diagnosis2;
        this.serviceFee = fee;
        this.status = repairEnum;
        this.notes = notes2;
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(now);
        this.receiveDate = createdAt;
        this.completionDate = null;
    }

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

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        if (deviceName == null || deviceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thiết bị không được để trống");
        }
        this.deviceName = deviceName;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        if (problem == null || problem.trim().isEmpty()) {
            throw new IllegalArgumentException("Vấn đề không được để trống");
        }
        this.problem = problem;
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

    /**
     * Đặt phí dịch vụ sửa chữa
     * 
     * @param serviceFee Phí dịch vụ
     */
    public void setServiceFee(BigDecimal serviceFee) {
        // Cho phép phí dịch vụ là null (trường hợp chưa xác định)
        // if (serviceFee == null) {
        //    throw new IllegalArgumentException("Phí dịch vụ không được để trống");
        // }
        this.serviceFee = serviceFee;
    }

    public RepairEnum getStatus() {
        return status;
    }

    public void setStatus(RepairEnum status) {
        if (status == null) {
            throw new IllegalArgumentException("Trạng thái không được để trống");
        }
        
        // Bỏ qua kiểm tra trạng thái khi đang khởi tạo đối tượng mới
        if (this.status != null && !canChangeStatus(status)) {
            throw new IllegalStateException("Không thể chuyển sang trạng thái " + status);
        }
        
        // Cập nhật ngày hoàn thành khi trạng thái là Completed
        if (status == RepairEnum.COMPLETED && this.completionDate == null) {
            this.completionDate = LocalDateTime.now();
        }
        
        this.status = status;
    }
    
    // Phương thức tương thích ngược với code cũ
    public void setStatus(String statusStr) {
        for (RepairEnum repairEnum : RepairEnum.values()) {
            if (repairEnum.getStatus().equals(statusStr)) {
                setStatus(repairEnum);
                return;
            }
        }
        throw new IllegalArgumentException("Trạng thái không hợp lệ: " + statusStr);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Kiểm tra có thể chuyển sang trạng thái mới không
    private boolean canChangeStatus(RepairEnum newStatus) {
        if (status == null) {
            return true;
        }

        switch (status) {
            case RECEIVED:
                return newStatus == RepairEnum.DIAGNOSING || 
                       newStatus == RepairEnum.CANCELLED;
            case DIAGNOSING:
                return newStatus == RepairEnum.REPAIRING || 
                       newStatus == RepairEnum.WAITING_FOR_PARTS ||
                       newStatus == RepairEnum.CANCELLED;
            case REPAIRING:
            case WAITING_FOR_PARTS:
                return newStatus == RepairEnum.COMPLETED || 
                       newStatus == RepairEnum.CANCELLED;
            case COMPLETED:
                return newStatus == RepairEnum.DELIVERED;
            case DELIVERED:
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }

    // Kiểm tra dịch vụ sửa chữa có thể cập nhật không
    public boolean canUpdate() {
        return status == RepairEnum.RECEIVED || 
               status == RepairEnum.DIAGNOSING || 
               status == RepairEnum.REPAIRING || 
               status == RepairEnum.WAITING_FOR_PARTS;
    }

    // Xử lý khi bắt đầu sửa chữa
    public void startRepair() {
        if (status != RepairEnum.DIAGNOSING) {
            throw new IllegalStateException("Chỉ có thể bắt đầu sửa chữa khi đã chẩn đoán xong");
        }
        setStatus(RepairEnum.REPAIRING);
    }

    // Xử lý khi hoàn thành sửa chữa
    public void complete() {
        if (status != RepairEnum.REPAIRING && status != RepairEnum.WAITING_FOR_PARTS) {
            throw new IllegalStateException("Chỉ có thể hoàn thành sửa chữa khi đang trong tiến trình");
        }
        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            throw new IllegalStateException("Phải có chẩn đoán trước khi hoàn thành");
        }
        setStatus(RepairEnum.COMPLETED);
    }

    // Xử lý khi hủy sửa chữa
    public void cancel() {
        if (!canUpdate()) {
            throw new IllegalStateException("Không thể hủy dịch vụ sửa chữa trong trạng thái hiện tại");
        }
        setStatus(RepairEnum.CANCELLED);
    }

}