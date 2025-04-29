package com.pcstore.model;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.pcstore.model.base.BaseTimeEntity;

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
    private transient String customerPhone;
    private String customerId;
    private String productId;

    @Override
    public Object getId() {
        return warrantyId;
    }

    public String getWarrantyId() {
        return warrantyId;
    }

    // Phương thức setter mới
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

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    /**
     * Lấy mã khách hàng
     * @return Mã khách hàng
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Đặt mã khách hàng
     * @param customerId Mã khách hàng
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Lấy mã sản phẩm
     * @return Mã sản phẩm
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Đặt mã sản phẩm
     * @param productId Mã sản phẩm
     */
    public void setProductId(String productId) {
        this.productId = productId;
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

    // Phương thức helper để chuyển đổi giữa String và Integer ID nếu cần
    public Integer getWarrantyIdAsInteger() {
        try {
            return Integer.parseInt(warrantyId);
        } catch (NumberFormatException e) {
            // Nếu không phải số nguyên hợp lệ
            return null;
        }
    }

    /**
     * Lấy ngày bảo hành (tương thích với cột warranty_date trong DB)
     * @return Ngày bảo hành dưới dạng LocalDate
     */
    public LocalDate getWarrantyDate() {
        return startDate != null ? startDate.toLocalDate() : null;
    }

    /**
     * Thiết lập ngày bảo hành (tương thích với cột warranty_date trong DB)
     * @param warrantyDate Ngày bảo hành
     */
    public void setWarrantyDate(LocalDate warrantyDate) {
        if (warrantyDate == null) {
            this.startDate = null;
        } else {
            // Giữ nguyên giờ, phút, giây nếu có, chỉ thay đổi ngày tháng năm
            LocalDateTime newDateTime;
            if (this.startDate != null) {
                newDateTime = LocalDateTime.of(
                    warrantyDate, 
                    this.startDate.toLocalTime()
                );
            } else {
                newDateTime = LocalDateTime.of(warrantyDate, java.time.LocalTime.MIDNIGHT);
            }
            this.setStartDate(newDateTime);
        }
    }

    /**
     * Lấy thời hạn bảo hành tính theo tháng (tương thích với cột warranty_period trong DB)
     * @return Thời hạn bảo hành theo tháng
     */
    public Integer getWarrantyPeriod() {
        if (startDate == null || endDate == null) {
            return null;
        }
        return (int) ChronoUnit.MONTHS.between(startDate, endDate);
    }

    /**
     * Thiết lập thời hạn bảo hành theo tháng (tương thích với cột warranty_period trong DB)
     * @param warrantyPeriod Thời hạn bảo hành theo tháng
     */
    public void setWarrantyPeriod(Integer warrantyPeriod) {
        if (warrantyPeriod == null || warrantyPeriod <= 0) {
            throw new IllegalArgumentException("Thời hạn bảo hành phải lớn hơn 0 tháng");
        }
        
        if (this.startDate != null) {
            this.setEndDate(this.startDate.plusMonths(warrantyPeriod));
        }
    }

    /**
     * Lấy mô tả bảo hành (tương thích với cột description trong DB)
     * @return Mô tả bảo hành
     */
    public String getDescription() {
        return warrantyTerms;
    }

    /**
     * Thiết lập mô tả bảo hành (tương thích với cột description trong DB)
     * @param description Mô tả bảo hành
     */
    public void setDescription(String description) {
        this.setWarrantyTerms(description);
    }

    /**
     * Kiểm tra trạng thái bảo hành (tương thích với cột status trong DB)
     * @return true nếu bảo hành còn hiệu lực, false nếu không
     */
    public boolean isStatus() {
        return !isUsed && isValid();
    }

    /**
     * Thiết lập trạng thái bảo hành (tương thích với cột status trong DB)
     * @param status Trạng thái bảo hành
     */
    public void setStatus(boolean status) {
        this.isUsed = !status;
    }

    /**
     * Lấy ID chi tiết hóa đơn (phương thức tiện ích)
     * @return ID chi tiết hóa đơn hoặc null nếu không có
     */
    public Integer getInvoiceDetailId() {
        return invoiceDetail != null ? invoiceDetail.getInvoiceDetailId() : null;
    }

    /**
     * Thiết lập ID chi tiết hóa đơn (phương thức tiện ích)
     * @param invoiceDetailId ID chi tiết hóa đơn
     */
    public void setInvoiceDetailId(Integer invoiceDetailId) {
        if (this.invoiceDetail == null) {
            this.invoiceDetail = new InvoiceDetail();
        }
        this.invoiceDetail.setInvoiceDetailId(invoiceDetailId);
    }
}