package com.pcstore.service;

import com.pcstore.repository.impl.RepairServiceRepository;
import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.RepairService;
import com.pcstore.model.Warranty;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý logic nghiệp vụ liên quan đến dịch vụ sửa chữa
 */
public class RepairServiceService {
    private final RepairServiceRepository repairServiceRepository;
    
    /**
     * Khởi tạo service với repository
     * @param connection Kết nối đến database
     */
    public RepairServiceService(Connection connection) {
        this.repairServiceRepository = new RepairServiceRepository(connection);
    }
    
    /**
     * Khởi tạo service với repository đã có
     * @param repairServiceRepository Repository dịch vụ sửa chữa
     */
    public RepairServiceService(RepairServiceRepository repairServiceRepository) {
        this.repairServiceRepository = repairServiceRepository;
    }
    
    /**
     * Thêm dịch vụ sửa chữa mới
     * @param repairService Thông tin dịch vụ sửa chữa
     * @return Dịch vụ sửa chữa đã được thêm
     */
    public RepairService addRepairService(RepairService repairService) {
        // Kiểm tra thông tin bắt buộc
        validateRequiredFields(repairService);
        
        // Thiết lập ngày nhận nếu chưa có
        if (repairService.getReceiveDate() == null) {
            repairService.setReceiveDate(LocalDateTime.now());
        }
        
        // Thiết lập trạng thái mặc định nếu chưa có
        if (repairService.getStatus() == null || repairService.getStatus().trim().isEmpty()) {
            repairService.setStatus("Pending");
        }
        
        // Kiểm tra liên kết bảo hành nếu có
        if (repairService.getWarranty() != null) {
            if (!repairService.getWarranty().isValid()) {
                throw new IllegalArgumentException("Bảo hành không còn hiệu lực");
            }
        }
        
        return repairServiceRepository.add(repairService);
    }
    
    /**
     * Cập nhật thông tin dịch vụ sửa chữa
     * @param repairService Thông tin dịch vụ sửa chữa mới
     * @return Dịch vụ sửa chữa đã được cập nhật
     */
    public RepairService updateRepairService(RepairService repairService) {
        // Kiểm tra tồn tại
        if (!repairServiceRepository.exists(repairService.getRepairServiceId())) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairService.getRepairServiceId() + " không tồn tại");
        }
        
        // Kiểm tra thông tin bắt buộc
        validateRequiredFields(repairService);
        
        // Kiểm tra trạng thái có thể cập nhật không
        Optional<RepairService> existingService = repairServiceRepository.findById(repairService.getRepairServiceId());
        if (existingService.isPresent() && !existingService.get().canUpdate()) {
            throw new IllegalStateException("Không thể cập nhật dịch vụ sửa chữa trong trạng thái " + existingService.get().getStatus());
        }
        
        return repairServiceRepository.update(repairService);
    }
    
    /**
     * Xóa dịch vụ sửa chữa
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @return true nếu xóa thành công
     */
    public boolean deleteRepairService(Integer repairServiceId) {
        // Kiểm tra tồn tại
        Optional<RepairService> service = repairServiceRepository.findById(repairServiceId);
        if (!service.isPresent()) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairServiceId + " không tồn tại");
        }
        
        // Chỉ có thể xóa dịch vụ ở trạng thái Pending hoặc Cancelled
        if (!"Pending".equals(service.get().getStatus()) && !"Cancelled".equals(service.get().getStatus())) {
            throw new IllegalStateException("Không thể xóa dịch vụ sửa chữa trong trạng thái " + service.get().getStatus());
        }
        
        return repairServiceRepository.delete(repairServiceId);
    }
    
    /**
     * Tìm dịch vụ sửa chữa theo mã
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @return Dịch vụ sửa chữa nếu tìm thấy
     */
    public Optional<RepairService> findRepairServiceById(Integer repairServiceId) {
        return repairServiceRepository.findById(repairServiceId);
    }
    
    /**
     * Lấy danh sách tất cả dịch vụ sửa chữa
     * @return Danh sách dịch vụ sửa chữa
     */
    public List<RepairService> getAllRepairServices() {
        return repairServiceRepository.findAll();
    }
    
    /**
     * Tìm dịch vụ sửa chữa theo khách hàng
     * @param customerId Mã khách hàng
     * @return Danh sách dịch vụ sửa chữa
     */
    public List<RepairService> findRepairServicesByCustomer(String customerId) {
        return repairServiceRepository.findByCustomerId(customerId);
    }
    
    /**
     * Tìm dịch vụ sửa chữa theo nhân viên
     * @param employeeId Mã nhân viên
     * @return Danh sách dịch vụ sửa chữa
     */
    public List<RepairService> findRepairServicesByEmployee(String employeeId) {
        return repairServiceRepository.findByEmployeeId(employeeId);
    }
    
    /**
     * Tìm dịch vụ sửa chữa theo trạng thái
     * @param status Trạng thái dịch vụ
     * @return Danh sách dịch vụ sửa chữa
     */
    public List<RepairService> findRepairServicesByStatus(String status) {
        return repairServiceRepository.findByStatus(status);
    }
    
    /**
     * Tìm dịch vụ sửa chữa đến hạn hôm nay
     * @return Danh sách dịch vụ sửa chữa
     */
    public List<RepairService> findDueRepairServices() {
        return repairServiceRepository.findDueToday();
    }
    
    /**
     * Bắt đầu sửa chữa
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @return true nếu cập nhật thành công
     */
    public boolean startRepairService(Integer repairServiceId) {
        Optional<RepairService> service = repairServiceRepository.findById(repairServiceId);
        if (!service.isPresent()) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairServiceId + " không tồn tại");
        }
        
        // Kiểm tra trạng thái
        RepairService repairService = service.get();
        if (!"Pending".equals(repairService.getStatus())) {
            throw new IllegalStateException("Chỉ có thể bắt đầu dịch vụ sửa chữa đang ở trạng thái chờ xử lý");
        }
        
        // Cập nhật trạng thái
        return repairServiceRepository.updateStatus(repairServiceId, "In Progress");
    }
    
    /**
     * Hoàn thành dịch vụ sửa chữa
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @param completionDate Ngày hoàn thành
     * @param notes Ghi chú
     * @param finalCost Chi phí cuối cùng
     * @return true nếu cập nhật thành công
     */
    public boolean completeRepairService(Integer repairServiceId, LocalDateTime completionDate, 
            String notes, double finalCost) {
        Optional<RepairService> service = repairServiceRepository.findById(repairServiceId);
        if (!service.isPresent()) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairServiceId + " không tồn tại");
        }
        
        // Kiểm tra trạng thái
        RepairService repairService = service.get();
        if (!"In Progress".equals(repairService.getStatus())) {
            throw new IllegalStateException("Chỉ có thể hoàn thành dịch vụ sửa chữa đang trong tiến trình");
        }
        
        // Kiểm tra chẩn đoán
        if (repairService.getDiagnosis() == null || repairService.getDiagnosis().trim().isEmpty()) {
            throw new IllegalStateException("Phải có chẩn đoán trước khi hoàn thành");
        }
        
        // Kiểm tra chi phí bảo hành
        if (repairService.getWarranty() != null && repairService.getWarranty().isValid() && finalCost > 0) {
            throw new IllegalArgumentException("Không tính phí cho sửa chữa trong bảo hành");
        }
        
        // Cập nhật trạng thái và thông tin hoàn thành
        return repairServiceRepository.completeService(repairServiceId, completionDate, notes, finalCost);
    }
    
    /**
     * Hủy dịch vụ sửa chữa
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @return true nếu cập nhật thành công
     */
    public boolean cancelRepairService(Integer repairServiceId) {
        Optional<RepairService> service = repairServiceRepository.findById(repairServiceId);
        if (!service.isPresent()) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairServiceId + " không tồn tại");
        }
        
        // Kiểm tra trạng thái
        RepairService repairService = service.get();
        if (!repairService.canUpdate()) {
            throw new IllegalStateException("Không thể hủy dịch vụ sửa chữa trong trạng thái hiện tại");
        }
        
        // Cập nhật trạng thái
        return repairServiceRepository.updateStatus(repairServiceId, "Cancelled");
    }
    
    /**
     * Kiểm tra các trường bắt buộc
     * @param repairService Dịch vụ sửa chữa cần kiểm tra
     */
    private void validateRequiredFields(RepairService repairService) {
        if (repairService.getCustomer() == null) {
            throw new IllegalArgumentException("Khách hàng không được để trống");
        }
        
        if (repairService.getDescription() == null || repairService.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Mô tả vấn đề không được để trống");
        }
        
        // Không bắt buộc phải có nhân viên khi tạo mới vì có thể phân công sau
        // if (repairService.getEmployee() == null) {
        //     throw new IllegalArgumentException("Nhân viên không được để trống");
        // }
    }
    
    /**
     * Tạo mới dịch vụ sửa chữa
     * @param customer Khách hàng
     * @param employee Nhân viên kỹ thuật
     * @param description Mô tả vấn đề
     * @param warranty Bảo hành (nếu có)
     * @return Dịch vụ sửa chữa mới
     */
    public RepairService createNewRepairService(Customer customer, Employee employee, 
            String description, Warranty warranty) {
        // Sử dụng factory method từ model
        return RepairService.createNew(customer, employee, description, warranty);
    }
    
    /**
     * Phân công nhân viên cho dịch vụ sửa chữa
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @param employee Nhân viên kỹ thuật
     * @return true nếu phân công thành công
     */
    public boolean assignEmployee(Integer repairServiceId, Employee employee) {
        Optional<RepairService> service = repairServiceRepository.findById(repairServiceId);
        if (!service.isPresent()) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairServiceId + " không tồn tại");
        }
        
        RepairService repairService = service.get();
        if (!repairService.canUpdate()) {
            throw new IllegalStateException("Không thể phân công nhân viên trong trạng thái hiện tại");
        }
        
        // Cập nhật nhân viên
        repairService.setEmployee(employee);
        return repairServiceRepository.update(repairService) != null;
    }
    
    /**
     * Cập nhật chẩn đoán của dịch vụ sửa chữa
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @param diagnosis Chẩn đoán
     * @return true nếu cập nhật thành công
     */
    public boolean updateDiagnosis(Integer repairServiceId, String diagnosis) {
        Optional<RepairService> service = repairServiceRepository.findById(repairServiceId);
        if (!service.isPresent()) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairServiceId + " không tồn tại");
        }
        
        RepairService repairService = service.get();
        if (!repairService.canUpdate()) {
            throw new IllegalStateException("Không thể cập nhật chẩn đoán trong trạng thái hiện tại");
        }
        
        // Cập nhật chẩn đoán
        repairService.setDiagnosis(diagnosis);
        return repairServiceRepository.update(repairService) != null;
    }
    
    /**
     * Cập nhật phí dịch vụ
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @param serviceFee Phí dịch vụ
     * @return true nếu cập nhật thành công
     */
    public boolean updateServiceFee(Integer repairServiceId, BigDecimal serviceFee) {
        Optional<RepairService> service = repairServiceRepository.findById(repairServiceId);
        if (!service.isPresent()) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairServiceId + " không tồn tại");
        }
        
        RepairService repairService = service.get();
        if (!repairService.canUpdate()) {
            throw new IllegalStateException("Không thể cập nhật phí dịch vụ trong trạng thái hiện tại");
        }
        
        // Kiểm tra bảo hành
        if (repairService.getWarranty() != null && repairService.getWarranty().isValid() && 
                serviceFee.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Không tính phí cho sửa chữa trong bảo hành");
        }
        
        // Cập nhật phí dịch vụ
        repairService.setServiceFee(serviceFee);
        return repairServiceRepository.update(repairService) != null;
    }
}