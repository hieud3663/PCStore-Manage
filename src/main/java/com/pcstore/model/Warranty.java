package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Class biểu diễn thông tin bảo hành
 */
public class Warranty extends BaseTimeEntity {
    private Integer warrantyId;
    private InvoiceDetail invoiceDetail;
    private Product product;
    private RepairService repairService;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String warrantyTerms;
    private boolean isUsed;

    @Override
    public Object getId() {
        return warrantyId;
    }

    public Integer getWarrantyId() {
        return warrantyId;
    }

    public void setWarrantyId(Integer warrantyId) {
        this.warrantyId = warrantyId;
    }

    public InvoiceDetail getInvoiceDetail() {
        return invoiceDetail;
    }

    public void setInvoiceDetail(InvoiceDetail invoiceDetail) {
        if (invoiceDetail == null) {
            throw new IllegalArgumentException("Chi tiết hóa đơn không được để trống");
        }
        this.invoiceDetail = invoiceDetail;
    }

    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Sản phẩm không được để trống");
        }
        this.product = product;
    }

    public RepairService getRepairService() {
        return repairService;
    }

    public void setRepairService(RepairService repairService) {
        if (isUsed && repairService == null) {
            throw new IllegalStateException("Không thể hủy dịch vụ sửa chữa của bảo hành đã sử dụng");
        }
        this.repairService = repairService;
        if (repairService != null) {
            this.isUsed = true;
        }
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu bảo hành không được để trống");
        }
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không thể sau ngày kết thúc");
        }
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("Ngày kết thúc bảo hành không được để trống");
        }
        if (startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Ngày kết thúc không thể trước ngày bắt đầu");
        }
        this.endDate = endDate;
    }

    public String getWarrantyTerms() {
        return warrantyTerms;
    }

    public void setWarrantyTerms(String warrantyTerms) {
        if (warrantyTerms == null || warrantyTerms.trim().isEmpty()) {
            throw new IllegalArgumentException("Điều khoản bảo hành không được để trống");
        }
        this.warrantyTerms = warrantyTerms;
    }

    public boolean isUsed() {
        return isUsed;
    }

    protected void setUsed(boolean used) {
        this.isUsed = used;
    }

    // Phương thức kiểm tra bảo hành còn hiệu lực không
    public boolean isValid() {
        if (isUsed) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    // Phương thức kiểm tra có thể sửa chữa bảo hành không
    public boolean canRepair() {
        return isValid() && !isUsed;
    }

    // Phương thức lấy thời gian bảo hành còn lại (theo ngày)
    public long getRemainingDays() {
        if (!isValid()) {
            return 0;
        }
        
        LocalDateTime now = LocalDateTime.now();
        return ChronoUnit.DAYS.between(now, endDate);
    }

    // Factory method để tạo bảo hành mới
    public static Warranty createNew(InvoiceDetail invoiceDetail, 
                                   LocalDateTime startDate,
                                   int warrantyMonths,
                                   String warrantyTerms) {
        if (invoiceDetail == null) {
            throw new IllegalArgumentException("Chi tiết hóa đơn không được để trống");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu bảo hành không được để trống");
        }
        if (warrantyMonths <= 0) {
            throw new IllegalArgumentException("Thời hạn bảo hành phải lớn hơn 0 tháng");
        }
        if (warrantyTerms == null || warrantyTerms.trim().isEmpty()) {
            throw new IllegalArgumentException("Điều khoản bảo hành không được để trống");
        }

        Warranty warranty = new Warranty();
        warranty.setInvoiceDetail(invoiceDetail);
        warranty.setStartDate(startDate);
        warranty.setEndDate(startDate.plusMonths(warrantyMonths));
        warranty.setWarrantyTerms(warrantyTerms);
        warranty.setUsed(false);
        return warranty;
    }

    // Phương thức khởi tạo thời hạn bảo hành
    public void initializeWarrantyPeriod() {
        if (this.invoiceDetail == null || this.invoiceDetail.getProduct() == null) {
            throw new IllegalStateException("Chưa có thông tin sản phẩm hoặc hóa đơn");
        }
        
        // Mặc định thời hạn bảo hành là 12 tháng
        int warrantyMonths = 12;
        
        // Ngày bắt đầu bảo hành là ngày tạo hóa đơn
        LocalDateTime startDate = LocalDateTime.now();
        
        this.setStartDate(startDate);
        this.setEndDate(startDate.plusMonths(warrantyMonths));
        this.setWarrantyTerms("Điều khoản bảo hành tiêu chuẩn");
    }
}