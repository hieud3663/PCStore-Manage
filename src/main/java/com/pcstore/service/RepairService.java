package com.pcstore.service;

import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.Repair;
import com.pcstore.model.Warranty;
import com.pcstore.model.enums.RepairEnum;
import com.pcstore.repository.impl.RepairRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý logic nghiệp vụ liên quan đến dịch vụ sửa chữa
 */
public class RepairService {
    private final RepairRepository repairServiceRepository;
    private final CustomerService customerService;
    private final EmployeeService employeeService;

    /**
     * Khởi tạo service với repository và các service khác
     *
     * @param connection      Kết nối đến database
     * @param customerService Service khách hàng
     * @param employeeService Service nhân viên
     */
    public RepairService(Connection connection,
                         CustomerService customerService,
                         EmployeeService employeeService) {
        this.repairServiceRepository = new RepairRepository(connection);
        this.customerService = customerService;
        this.employeeService = employeeService;
    }

    public RepairService(Connection connection) {
        this.repairServiceRepository = new RepairRepository(connection);
        try {
            this.customerService = ServiceFactory.getInstance().getCustomerService();
            this.employeeService = ServiceFactory.getInstance().getEmployeeService();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public RepairService(RepairRepository repairServiceRepository) {
        this.repairServiceRepository = repairServiceRepository;
        try {
            this.customerService = ServiceFactory.getInstance().getCustomerService();
            this.employeeService = ServiceFactory.getInstance().getEmployeeService();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Thêm dịch vụ sửa chữa mới
     *
     * @param repairService Thông tin dịch vụ sửa chữa
     * @return Dịch vụ sửa chữa đã được thêm
     */
    public Repair addRepairService(Repair repairService) {
        // Kiểm tra thông tin bắt buộc
        // validateRequiredFields(repairService);

        // Thiết lập ngày nhận nếu chưa có
        if (repairService.getReceiveDate() == null) {
            repairService.setReceiveDate(LocalDateTime.now());
        }

        // Thiết lập trạng thái mặc định nếu chưa có
        if (repairService.getStatus() == null) {
            repairService.setStatus(RepairEnum.RECEIVED);
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
     *
     * @param repairService Thông tin dịch vụ sửa chữa mới
     * @return Dịch vụ sửa chữa đã được cập nhật
     */
    public Repair updateRepairService(Repair repairService) {
        // Kiểm tra tồn tại
        if (!repairServiceRepository.exists(repairService.getRepairServiceId())) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairService.getRepairServiceId() + " không tồn tại");
        }

        // Kiểm tra thông tin bắt buộc
        validateRequiredFields(repairService);

        // Kiểm tra trạng thái có thể cập nhật không
        Optional<Repair> existingService = repairServiceRepository.findById(repairService.getRepairServiceId());
        if (existingService.isPresent() && !existingService.get().canUpdate()) {
            throw new IllegalStateException("Không thể cập nhật dịch vụ sửa chữa trong trạng thái " + existingService.get().getStatus());
        }

        return repairServiceRepository.update(repairService);
    }

    /**
     * Xóa dịch vụ sửa chữa
     *
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @return true nếu xóa thành công
     */
    public boolean deleteRepairService(Integer repairServiceId) {
        // Kiểm tra tồn tại
        Optional<Repair> service = repairServiceRepository.findById(repairServiceId);
        if (!service.isPresent()) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairServiceId + " không tồn tại");
        }

        // Chỉ có thể xóa dịch vụ ở trạng thái Pending hoặc Cancelled
        if (service.get().getStatus() != RepairEnum.RECEIVED && service.get().getStatus() != RepairEnum.CANCELLED) {
            throw new IllegalStateException("Không thể xóa dịch vụ sửa chữa trong trạng thái " + service.get().getStatus());
        }

        return repairServiceRepository.delete(repairServiceId);
    }

    /**
     * Tìm dịch vụ sửa chữa theo mã
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @return Dịch vụ sửa chữa nếu tìm thấy
     */
    /**
     * Tìm dịch vụ sửa chữa theo mã và lấy thông tin đầy đủ của Customer và Employee
     *
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @return Dịch vụ sửa chữa với thông tin đầy đủ nếu tìm thấy
     */
    public Optional<Repair> findRepairServiceWithFullInfo(Integer repairServiceId) {
        Optional<Repair> repairServiceOpt = repairServiceRepository.findById(repairServiceId);

        if (!repairServiceOpt.isPresent()) {
            return Optional.empty();
        }

        Repair repairService = repairServiceOpt.get();

        // Kiểm tra trường hợp customerService và employeeService chưa được thiết lập
        if (customerService == null || employeeService == null) {
            throw new IllegalStateException("CustomerService và EmployeeService phải được khởi tạo để lấy thông tin đầy đủ");
        }

        // Lấy thông tin đầy đủ của Customer
        if (repairService.getCustomer() != null) {
            String customerId = repairService.getCustomer().getCustomerId();
            customerService.findCustomerById(customerId).ifPresent(customer -> {
                repairService.setCustomer(customer);
            });
        }

        // Lấy thông tin đầy đủ của Employee
        if (repairService.getEmployee() != null) {
            String employeeId = repairService.getEmployee().getEmployeeId();
            employeeService.findEmployeeById(employeeId).ifPresent(employee -> {
                repairService.setEmployee(employee);
            });
        }

        return Optional.of(repairService);
    }

    /**
     * Lấy danh sách tất cả dịch vụ sửa chữa với thông tin đầy đủ
     *
     * @return Danh sách dịch vụ sửa chữa với thông tin đầy đủ
     */
    public List<Repair> getAllRepairServicesWithFullInfo() {
        try {
            System.out.println("RepairService: Đang tải danh sách dịch vụ sửa chữa...");
            List<Repair> repairs = repairServiceRepository.findAll();
            System.out.println("RepairService: Đã tìm thấy " + repairs.size() + " dịch vụ");

            // Load thông tin đầy đủ cho mỗi dịch vụ
            for (Repair repair : repairs) {
                try {
                    // Tải thông tin khách hàng
                    if (repair.getCustomer() != null) {
                        String customerId = repair.getCustomer().getCustomerId();
                        customerService.findCustomerById(customerId).ifPresent(customer -> {
                            repair.setCustomer(customer);
                        });
                    }

                    // Tải thông tin nhân viên
                    if (repair.getEmployee() != null) {
                        String employeeId = repair.getEmployee().getEmployeeId();
                        employeeService.findEmployeeById(employeeId).ifPresent(employee -> {
                            repair.setEmployee(employee);
                        });
                    }
                } catch (Exception e) {
                    System.err.println("RepairService: Lỗi khi tải thông tin chi tiết: " + e.getMessage());
                }
            }

            return repairs;
        } catch (Exception e) {
            System.err.println("RepairService: Lỗi khi tải danh sách dịch vụ: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error getting all repair services with full info", e);
        }
    }

    /**
     * Tìm dịch vụ sửa chữa theo khách hàng
     *
     * @param customerId Mã khách hàng
     * @return Danh sách dịch vụ sửa chữa
     */
    public List<Repair> findRepairServicesByCustomer(String customerId) {
        return repairServiceRepository.findByCustomerId(customerId);
    }

    /**
     * Tìm dịch vụ sửa chữa theo nhân viên
     *
     * @param employeeId Mã nhân viên
     * @return Danh sách dịch vụ sửa chữa
     */
    public List<Repair> findRepairServicesByEmployee(String employeeId) {
        return repairServiceRepository.findByEmployeeId(employeeId);
    }

    /**
     * Tìm dịch vụ sửa chữa theo trạng thái
     *
     * @param status Trạng thái dịch vụ
     * @return Danh sách dịch vụ sửa chữa
     */
    public List<Repair> findRepairServicesByStatus(RepairEnum status) {
        return repairServiceRepository.findByStatus(status.getStatus());
    }

    /**
     * Tìm dịch vụ sửa chữa theo trạng thái (phương thức tương thích ngược)
     *
     * @param statusString Chuỗi trạng thái dịch vụ
     * @return Danh sách dịch vụ sửa chữa
     */
    public List<Repair> findRepairServicesByStatus(String statusString) {
        for (RepairEnum status : RepairEnum.values()) {
            if (status.getStatus().equals(statusString)) {
                return repairServiceRepository.findByStatus(statusString);
            }
        }
        throw new IllegalArgumentException("Trạng thái không hợp lệ: " + statusString);
    }

    /**
     * Tìm dịch vụ sửa chữa đến hạn hôm nay
     *
     * @return Danh sách dịch vụ sửa chữa
     */
    public List<Repair> findDueRepairServices() {
        return repairServiceRepository.findDueToday();
    }

    /**
     * Bắt đầu sửa chữa
     *
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @return true nếu cập nhật thành công
     */
    public boolean startRepairService(Integer repairServiceId) {
        Optional<Repair> service = repairServiceRepository.findById(repairServiceId);
        if (!service.isPresent()) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairServiceId + " không tồn tại");
        }

        // Kiểm tra trạng thái
        Repair repairService = service.get();
        if (repairService.getStatus() != RepairEnum.RECEIVED) {
            throw new IllegalStateException("Chỉ có thể bắt đầu dịch vụ sửa chữa đang ở trạng thái chờ xử lý");
        }

        // Cập nhật trạng thái
        return repairServiceRepository.updateStatus(repairServiceId, RepairEnum.DIAGNOSING.getStatus());
    }

    /**
     * Hoàn thành dịch vụ sửa chữa
     *
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @param completionDate  Ngày hoàn thành
     * @param notes           Ghi chú
     * @param finalCost       Chi phí cuối cùng
     * @return true nếu cập nhật thành công
     */
    public boolean completeRepairService(Integer repairServiceId, LocalDateTime completionDate,
                                         String notes, double finalCost) {
        Optional<Repair> service = repairServiceRepository.findById(repairServiceId);
        if (!service.isPresent()) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairServiceId + " không tồn tại");
        }

        // Kiểm tra trạng thái
        Repair repairService = service.get();
        RepairEnum currentStatus = repairService.getStatus();
        if (currentStatus != RepairEnum.REPAIRING && currentStatus != RepairEnum.WAITING_FOR_PARTS) {
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
     *
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @return true nếu cập nhật thành công
     */
    public boolean cancelRepairService(Integer repairServiceId) {
        Optional<Repair> service = repairServiceRepository.findById(repairServiceId);
        if (!service.isPresent()) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairServiceId + " không tồn tại");
        }

        // Kiểm tra trạng thái
        Repair repairService = service.get();
        if (!repairService.canUpdate()) {
            throw new IllegalStateException("Không thể hủy dịch vụ sửa chữa trong trạng thái hiện tại");
        }

        // Cập nhật trạng thái
        return repairServiceRepository.updateStatus(repairServiceId, RepairEnum.CANCELLED.getStatus());
    }

    /**
     * Kiểm tra các trường bắt buộc
     *
     * @param repairService Dịch vụ sửa chữa cần kiểm tra
     */
    private void validateRequiredFields(Repair repairService) {
        if (repairService.getCustomer() == null) {
            throw new IllegalArgumentException("Khách hàng không được để trống");
        }

        if (repairService.getDeviceName() == null || repairService.getDeviceName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thiết bị không được để trống");
        }

        if (repairService.getProblem() == null || repairService.getProblem().trim().isEmpty()) {
            throw new IllegalArgumentException("Mô tả vấn đề không được để trống");
        }

        // Không bắt buộc phải có nhân viên khi tạo mới vì có thể phân công sau
        // if (repairService.getEmployee() == null) {
        //     throw new IllegalArgumentException("Nhân viên không được để trống");
        // }
    }

    /**
     * Tạo mới dịch vụ sửa chữa
     *
     * @param customer Khách hàng
     */
    public Repair createNewRepairService(Customer customer, Employee employee,
                                         String description, Warranty warranty) {
        // Sử dụng factory method từ model
        return new Repair(customer, employee, description, warranty);
    }

    /**
     * Phân công nhân viên cho dịch vụ sửa chữa
     *
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @param employee        Nhân viên kỹ thuật
     * @return true nếu phân công thành công
     */
    public boolean assignEmployee(Integer repairServiceId, Employee employee) {
        Optional<Repair> service = repairServiceRepository.findById(repairServiceId);
        if (!service.isPresent()) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairServiceId + " không tồn tại");
        }

        Repair repairService = service.get();
        if (!repairService.canUpdate()) {
            throw new IllegalStateException("Không thể phân công nhân viên trong trạng thái hiện tại");
        }

        // Cập nhật nhân viên
        repairService.setEmployee(employee);
        return repairServiceRepository.update(repairService) != null;
    }

    /**
     * Cập nhật chẩn đoán của dịch vụ sửa chữa
     *
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @param diagnosis       Chẩn đoán
     * @return true nếu cập nhật thành công
     */
    public boolean updateDiagnosis(Integer repairServiceId, String diagnosis) {
        Optional<Repair> service = repairServiceRepository.findById(repairServiceId);
        if (!service.isPresent()) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairServiceId + " không tồn tại");
        }

        Repair repairService = service.get();
        if (!repairService.canUpdate()) {
            throw new IllegalStateException("Không thể cập nhật chẩn đoán trong trạng thái hiện tại");
        }

        // Cập nhật chẩn đoán
        repairService.setDiagnosis(diagnosis);
        return repairServiceRepository.update(repairService) != null;
    }

    /**
     * Cập nhật phí dịch vụ
     *
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @param serviceFee      Phí dịch vụ
     * @return true nếu cập nhật thành công
     */
    public boolean updateServiceFee(Integer repairServiceId, BigDecimal serviceFee) {
        Optional<Repair> service = repairServiceRepository.findById(repairServiceId);
        if (!service.isPresent()) {
            throw new IllegalArgumentException("Dịch vụ sửa chữa với mã " + repairServiceId + " không tồn tại");
        }

        Repair repairService = service.get();
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