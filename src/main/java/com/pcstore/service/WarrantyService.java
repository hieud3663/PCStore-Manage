package com.pcstore.service;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Warranty;
import com.pcstore.repository.impl.WarrantyRepository;

/**
 * Service xử lý logic nghiệp vụ liên quan đến bảo hành
 */
public class WarrantyService {
    private final WarrantyRepository warrantyRepository;
    private static final Logger logger = Logger.getLogger(WarrantyService.class.getName());
    
    /**
     * Khởi tạo service với repository
     * @param connection Kết nối đến database
     */
    public WarrantyService(Connection connection) {
        this.warrantyRepository = new WarrantyRepository(connection);
    }
    
    /**
     * Khởi tạo service với repository đã có
     * @param warrantyRepository Repository bảo hành
     */
    public WarrantyService(WarrantyRepository warrantyRepository) {
        this.warrantyRepository = warrantyRepository;
    }
    
    /**
     * Thêm bảo hành mới
     * @param warranty Thông tin bảo hành
     * @return Bảo hành đã được thêm
     */
    public Warranty addWarranty(Warranty warranty) {
        // Kiểm tra thông tin bắt buộc
        validateBasicInfo(warranty);
        
        // Kiểm tra chi tiết hóa đơn
        if (warranty.getInvoiceDetail() == null || 
            warranty.getInvoiceDetail().getInvoiceDetailId() == null) {
            throw new IllegalArgumentException("Chi tiết hóa đơn không được để trống");
        }
        
        // Kiểm tra xem đã có bảo hành cho chi tiết hóa đơn này chưa
        Optional<Warranty> existingWarranty = warrantyRepository.findByInvoiceDetailId(
                warranty.getInvoiceDetail().getInvoiceDetailId());
        if (existingWarranty.isPresent()) {
            throw new IllegalArgumentException("Đã tồn tại bảo hành cho chi tiết hóa đơn này");
        }
        
        // Nếu chưa có ngày bắt đầu/kết thúc, tự động khởi tạo
        if (warranty.getStartDate() == null) {
            warranty.setStartDate(LocalDateTime.now());
        }
        
        if (warranty.getEndDate() == null) {
            // Mặc định thời hạn bảo hành là 12 tháng
            warranty.setEndDate(warranty.getStartDate().plusMonths(12));
        }
        
        return warrantyRepository.add(warranty);
    }
    
    /**
     * Cập nhật thông tin bảo hành
     * @param warranty Thông tin bảo hành mới
     * @return Bảo hành đã được cập nhật
     */
    public Warranty updateWarranty(Warranty warranty) {
        // Kiểm tra tồn tại
        if (warranty.getWarrantyId() == null || !warrantyRepository.exists(
                Integer.parseInt(warranty.getWarrantyId()))) {
            throw new IllegalArgumentException("Bảo hành không tồn tại");
        }
        
        // Kiểm tra thông tin cơ bản
        validateBasicInfo(warranty);
        
        // Kiểm tra xem bảo hành đã được sử dụng chưa
        if (warrantyRepository.isUsed(Integer.parseInt(warranty.getWarrantyId()))) {
            throw new IllegalStateException("Không thể cập nhật bảo hành đã sử dụng");
        }
        
        return warrantyRepository.update(warranty);
    }
    
    /**
     * Xóa bảo hành
     * @param warrantyId Mã bảo hành
     * @return true nếu xóa thành công
     */
    public boolean deleteWarranty(Integer warrantyId) {
        // Kiểm tra tồn tại
        if (!warrantyRepository.exists(warrantyId)) {
            throw new IllegalArgumentException("Bảo hành không tồn tại");
        }
        
        // Kiểm tra xem bảo hành đã được sử dụng chưa
        if (warrantyRepository.isUsed(warrantyId)) {
            throw new IllegalStateException("Không thể xóa bảo hành đã sử dụng");
        }
        
        return warrantyRepository.delete(warrantyId);
    }
    
    /**
     * Tìm bảo hành theo mã
     * @param warrantyId Mã bảo hành
     * @return Bảo hành nếu tìm thấy
     */
    public Optional<Warranty> findWarrantyById(String warrantyId) {
        try {
            return warrantyRepository.findById(warrantyId);
        } catch (Exception e) {
            logger.warning("Error finding warranty by ID: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Tìm bảo hành theo chi tiết hóa đơn
     * @param invoiceDetailId Mã chi tiết hóa đơn
     * @return Bảo hành nếu tìm thấy
     */
    public Optional<Warranty> findWarrantyByInvoiceDetailId(Integer invoiceDetailId) {
        return warrantyRepository.findByInvoiceDetailId(invoiceDetailId);
    }
    
    /**
     * Tìm bảo hành theo khách hàng
     * @param customerId Mã khách hàng
     * @return Danh sách bảo hành
     */
    public List<Warranty> findWarrantiesByCustomerId(String customerId) {
        return warrantyRepository.findByCustomerId(customerId);
    }
    
    /**
     * Tìm bảo hành theo sản phẩm
     * @param productId Mã sản phẩm
     * @return Danh sách bảo hành
     */
    public List<Warranty> findWarrantiesByProductId(String productId) {
        return warrantyRepository.findByProductId(productId);
    }
    
    /**
     * Lấy danh sách tất cả bảo hành
     * @return Danh sách bảo hành
     */
    public List<Warranty> getAllWarranties() {
        return warrantyRepository.findAll();
    }
    
    /**
     * Tìm kiếm bảo hành theo từ khóa
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách bảo hành phù hợp với từ khóa
     */
    public List<Warranty> searchWarranties(String keyword) {
        return warrantyRepository.search(keyword);
    }
    
    /**
     * Lấy danh sách bảo hành còn hiệu lực
     * @return Danh sách bảo hành
     */
    public List<Warranty> getActiveWarranties() {
        return warrantyRepository.findActiveWarranties();
    }
    
    /**
     * Lấy danh sách bảo hành hết hạn
     * @return Danh sách bảo hành
     */
    public List<Warranty> getExpiredWarranties() {
        return warrantyRepository.findExpiredWarranties();
    }
    
    /**
     * Lấy danh sách bảo hành sắp hết hạn
     * @param daysThreshold Số ngày còn lại
     * @return Danh sách bảo hành
     */
    public List<Warranty> getWarrantiesAboutToExpire(int daysThreshold) {
        return warrantyRepository.findWarrantiesAboutToExpire(daysThreshold);
    }
    
    /**
     * Lấy danh sách bảo hành đã sử dụng
     * @return Danh sách bảo hành
     */
    public List<Warranty> getUsedWarranties() {
        return warrantyRepository.findUsedWarranties();
    }
    
    /**
     * Liên kết bảo hành với dịch vụ sửa chữa
     * @param warrantyId Mã bảo hành
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @return true nếu liên kết thành công
     */
    public boolean linkToRepairService(Integer warrantyId, Integer repairServiceId) {
        // Kiểm tra tồn tại
        Optional<Warranty> warranty = warrantyRepository.findById(warrantyId);
        if (!warranty.isPresent()) {
            throw new IllegalArgumentException("Bảo hành không tồn tại");
        }
        
        // Kiểm tra hiệu lực
        if (!warranty.get().isValid()) {
            throw new IllegalStateException("Bảo hành không còn hiệu lực hoặc đã được sử dụng");
        }
        
        return warrantyRepository.linkToRepairService(warrantyId, repairServiceId);
    }
    
    /**
     * Hủy liên kết bảo hành với dịch vụ sửa chữa
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @return true nếu hủy liên kết thành công
     */
    public boolean unlinkFromRepairService(Integer repairServiceId) {
        return warrantyRepository.unlinkFromRepairService(repairServiceId);
    }
    
    /**
     * Kiểm tra bảo hành có đang sử dụng
     * @param warrantyId Mã bảo hành
     * @return true nếu đang sử dụng
     */
    public boolean isWarrantyUsed(Integer warrantyId) {
        return warrantyRepository.isUsed(warrantyId);
    }
    
    /**
     * Lấy danh sách bảo hành theo dịch vụ sửa chữa
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @return Danh sách bảo hành
     */
    public List<Warranty> findWarrantiesByRepairServiceId(Integer repairServiceId) {
        return warrantyRepository.findByRepairServiceId(repairServiceId);
    }
    
    /**
     * Tạo mới bảo hành
     * @param invoiceDetail Chi tiết hóa đơn
     * @param warrantyMonths Số tháng bảo hành
     * @param warrantyTerms Điều khoản bảo hành
     * @return Bảo hành mới
     */
    public Warranty createNewWarranty(InvoiceDetail invoiceDetail, int warrantyMonths, String warrantyTerms) {
        if (invoiceDetail == null) {
            throw new IllegalArgumentException("Chi tiết hóa đơn không được để trống");
        }
        
        // Sử dụng factory method từ model Warranty
        return Warranty.createNew(invoiceDetail, LocalDateTime.now(), warrantyMonths, warrantyTerms);
    }
    
    /**
     * Kiểm tra thông tin cơ bản của bảo hành
     * @param warranty Bảo hành cần kiểm tra
     */
    private void validateBasicInfo(Warranty warranty) {
        if (warranty == null) {
            throw new IllegalArgumentException("Bảo hành không được để trống");
        }
        
        if (warranty.getStartDate() == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được để trống");
        }
        
        if (warranty.getEndDate() == null) {
            throw new IllegalArgumentException("Ngày kết thúc không được để trống");
        }
        
        if (warranty.getStartDate().isAfter(warranty.getEndDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu không thể sau ngày kết thúc");
        }
        
        if (warranty.getWarrantyTerms() == null || warranty.getWarrantyTerms().trim().isEmpty()) {
            throw new IllegalArgumentException("Điều khoản bảo hành không được để trống");
        }
    }
}