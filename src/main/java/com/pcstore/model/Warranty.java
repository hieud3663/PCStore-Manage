package com.pcstore.model;

import com.pcstore.model.base.BaseTimeEntity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Class biểu diễn thông tin bảo hành
 */
public class Warranty extends BaseTimeEntity {
    private String warrantyId;
    private InvoiceDetail invoiceDetail;
    // Bỏ thuộc tính product vì đã có trong invoiceDetail
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String warrantyTerms;
    private boolean isUsed;
    
    // Thông tin tạm thời để hiển thị UI
    private transient String customerName;
    private transient String productName;
    private transient Integer repairServiceId;
    private transient String repairStatus;

    @Override
    public Object getId() {
        return warrantyId;
    }

    public String getWarrantyId() {
        return warrantyId;
    }

    public void setWarrantyId(String warrantyId) {
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

    // Phương thức để lấy product từ invoiceDetail
    public Product getProduct() {
        return invoiceDetail != null ? invoiceDetail.getProduct() : null;
    }
    
    // Bỏ setter của product
    
    // Thay đổi cách xử lý RepairService để tránh tham chiếu vòng
    public Integer getRepairServiceId() {
        return repairServiceId;
    }
    
    public void setRepairServiceId(Integer repairServiceId) {
        this.repairServiceId = repairServiceId;
        if (repairServiceId != null) {
            this.isUsed = true;
        }
    }
    
    public String getRepairStatus() {
        return repairStatus;
    }
    
    public void setRepairStatus(String repairStatus) {
        this.repairStatus = repairStatus;
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

    public void setUsed(boolean used) {
        this.isUsed = used;
    }

    // Getter/Setter cho các thuộc tính tạm thời UI
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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