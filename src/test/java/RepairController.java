import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.math.BigDecimal;

// import com.pcstore.controller.RepairController;
import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.Repair;
import com.pcstore.model.enums.RepairEnum;
import com.pcstore.service.CustomerService;
import com.pcstore.service.EmployeeService;
import com.pcstore.service.RepairService;
import com.pcstore.utils.DatabaseConnection;

/**
 * Controller xử lý các thao tác với dịch vụ sửa chữa
 */
public class RepairController {
    private final RepairService repairServiceService;
    private final CustomerService customerService;
    private final EmployeeService employeeService;
    
    /**
     * Khởi tạo controller với các service cần thiết
     */
    public RepairController() {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        this.customerService = new CustomerService(connection);
        this.employeeService = new EmployeeService(connection);
        this.repairServiceService = new RepairService(connection, customerService, employeeService);
    }
    
    /**
     * Khởi tạo controller với các service được tiêm vào (dependency injection)
     * @param repairServiceService Service xử lý dịch vụ sửa chữa
     * @param customerService Service xử lý khách hàng
     * @param employeeService Service xử lý nhân viên
     */
    public RepairController(
            RepairService repairServiceService,
            CustomerService customerService,
            EmployeeService employeeService) {
        this.repairServiceService = repairServiceService;
        this.customerService = customerService;
        this.employeeService = employeeService;
    }
    
    /**
     * Tìm kiếm khách hàng theo số điện thoại
     * @param phoneNumber Số điện thoại của khách hàng cần tìm
     * @return Khách hàng nếu tìm thấy, null nếu không tìm thấy
     */
    public Customer searchCustomerByPhone(String phoneNumber) {
        try {
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return null;
            }
            
            Optional<Customer> customerOpt = customerService.findCustomerByPhone(phoneNumber);
            return customerOpt.orElse(null);
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm kiếm khách hàng: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Tạo mới hoặc lấy thông tin khách hàng đã tồn tại
     * @param fullName Tên khách hàng
     * @param phoneNumber Số điện thoại khách hàng
     * @return Khách hàng đã tạo/tìm thấy hoặc null nếu có lỗi
     */
    public Customer getOrCreateCustomer(String fullName, String phoneNumber) {
        try {
            // Kiểm tra xem khách hàng đã tồn tại trong cơ sở dữ liệu chưa
            Customer existingCustomer = searchCustomerByPhone(phoneNumber);
            if (existingCustomer != null) {
                return existingCustomer;
            }
            
            // Nếu chưa có khách hàng, tạo khách hàng mới
            Customer newCustomer = new Customer();
            newCustomer.setFullName(fullName);
            newCustomer.setPhoneNumber(phoneNumber);
            newCustomer.setPoints(0);
            
            // Thêm khách hàng vào database
            return customerService.addCustomer(newCustomer);
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo khách hàng mới: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thêm mới dịch vụ sửa chữa
     * @param customerId Mã khách hàng
     * @param employeeId Mã nhân viên (có thể null)
     * @param deviceName Tên thiết bị
     * @param problem Mô tả vấn đề
     * @param diagnosis Chẩn đoán ban đầu (có thể null)
     * @param notes Ghi chú thêm (có thể null)
     * @return Dịch vụ sửa chữa đã được thêm hoặc null nếu có lỗi
     */
    public Repair addRepairService(
            String customerId, 
            String employeeId, 
            String deviceName, 
            String problem, 
            String diagnosis, 
            String notes) {
        try {
            // Tìm khách hàng
            Customer customer = customerService.findCustomerById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng với mã " + customerId));
            
            // Tìm nhân viên (nếu có)
            Employee employee = null;
            if (employeeId != null && !employeeId.trim().isEmpty()) {
                employee = employeeService.findEmployeeById(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên với mã " + employeeId));
            }
            
            // Tạo đối tượng RepairService
            Repair repairService = new Repair();
            repairService.setCustomer(customer);
            if (employee != null) {
                repairService.setEmployee(employee);
            }
            repairService.setDeviceName(deviceName);
            repairService.setProblem(problem);
            if (diagnosis != null && !diagnosis.trim().isEmpty()) {
                repairService.setDiagnosis(diagnosis);
            }
            repairService.setReceiveDate(LocalDateTime.now());
            repairService.setStatus(RepairEnum.RECEIVED);
            repairService.setServiceFee(BigDecimal.ZERO);
            if (notes != null && !notes.trim().isEmpty()) {
                repairService.setNotes(notes);
            }
            
            // Lưu vào database
            return repairServiceService.addRepairService(repairService);
        } catch (Exception e) {
            System.err.println("Lỗi khi thêm dịch vụ sửa chữa: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Thêm mới dịch vụ sửa chữa với thông tin khách hàng (tự động tìm/tạo khách hàng)
     * @param customerName Tên khách hàng
     * @param customerPhone Số điện thoại khách hàng
     * @param employeeId Mã nhân viên
     * @param deviceName Tên thiết bị
     * @param problem Mô tả vấn đề
     * @param diagnosis Chẩn đoán ban đầu
     * @param notes Ghi chú
     * @return Dịch vụ sửa chữa đã được thêm hoặc null nếu có lỗi
     */
    public Repair addRepairServiceWithCustomerInfo(
            String customerName,
            String customerPhone,
            String employeeId,
            String deviceName,
            String problem,
            String diagnosis,
            String notes) {
        try {
            // Kiểm tra và tạo khách hàng nếu cần
            Customer customer = getOrCreateCustomer(customerName, customerPhone);
            if (customer == null) {
                throw new IllegalArgumentException("Không thể tạo/tìm khách hàng với số điện thoại " + customerPhone);
            }
            
            // Gọi phương thức thêm dịch vụ sửa chữa với mã khách hàng
            return addRepairService(
                customer.getCustomerId(),
                employeeId,
                deviceName,
                problem,
                diagnosis,
                notes
            );
        } catch (Exception e) {
            System.err.println("Lỗi khi thêm dịch vụ sửa chữa với thông tin khách hàng: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Cập nhật thông tin dịch vụ sửa chữa
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @param employeeId Mã nhân viên mới (có thể null)
     * @param diagnosis Chẩn đoán mới
     * @param status Trạng thái mới
     * @param serviceFee Phí dịch vụ mới
     * @param notes Ghi chú mới
     * @return Dịch vụ sửa chữa đã được cập nhật hoặc null nếu có lỗi
     */
    public Repair updateRepairService(
            Integer repairServiceId, 
            String employeeId, 
            String diagnosis, 
            String status, 
            BigDecimal serviceFee, 
            String notes) {
        try {
            // Tìm dịch vụ sửa chữa
            Optional<Repair> repairServiceOpt = repairServiceService.findRepairServiceWithFullInfo(repairServiceId);
            if (!repairServiceOpt.isPresent()) {
                throw new IllegalArgumentException("Không tìm thấy dịch vụ sửa chữa với mã " + repairServiceId);
            }
            
            Repair repairService = repairServiceOpt.get();
            
            // Cập nhật nhân viên nếu có thay đổi
            if (employeeId != null && (repairService.getEmployee() == null || 
                    !employeeId.equals(repairService.getEmployee().getEmployeeId()))) {
                Employee employee = employeeService.findEmployeeById(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên với mã " + employeeId));
                repairService.setEmployee(employee);
            }
            
            // Cập nhật các thông tin khác
            if (diagnosis != null && !diagnosis.equals(repairService.getDiagnosis())) {
                repairService.setDiagnosis(diagnosis);
            }
            
            if (serviceFee != null && !serviceFee.equals(repairService.getServiceFee())) {
                repairService.setServiceFee(serviceFee);
            }
            
            if (notes != null && !notes.equals(repairService.getNotes())) {
                repairService.setNotes(notes);
            }
            
            // Cập nhật trạng thái nếu có thay đổi (phải để cuối cùng vì có thể cập nhật completionDate)
            if (status != null) {
                // Tìm kiếm enum tương ứng với chuỗi status
                RepairEnum statusEnum = null;
                for (RepairEnum repairEnum : RepairEnum.values()) {
                    if (repairEnum.getStatus().equals(status)) {
                        statusEnum = repairEnum;
                        break;
                    }
                }
                
                // Nếu tìm thấy enum và khác với trạng thái hiện tại
                if (statusEnum != null && statusEnum != repairService.getStatus()) {
                    repairService.setStatus(statusEnum);
                }
            }
            
            // Lưu vào database
            return repairServiceService.updateRepairService(repairService);
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật dịch vụ sửa chữa: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy danh sách tất cả dịch vụ sửa chữa
     * @return Danh sách dịch vụ sửa chữa
     */
    public List<Repair> getAllRepairServices() {
        return repairServiceService.getAllRepairServicesWithFullInfo();
    }
    
    /**
     * Tìm dịch vụ sửa chữa theo mã
     * @param repairServiceId Mã dịch vụ sửa chữa
     * @return Dịch vụ sửa chữa nếu tìm thấy
     */
    public Optional<Repair> findRepairServiceById(Integer repairServiceId) {
        return repairServiceService.findRepairServiceWithFullInfo(repairServiceId);
    }
    
    /**
     * Tìm dịch vụ sửa chữa theo số điện thoại khách hàng
     * @param phoneNumber Số điện thoại khách hàng
     * @return Danh sách dịch vụ sửa chữa
     */
    public List<Repair> findRepairServicesByCustomerPhone(String phoneNumber) {
        try {
            Customer customer = searchCustomerByPhone(phoneNumber);
            if (customer == null) {
                return List.of(); // Trả về danh sách rỗng nếu không tìm thấy khách hàng
            }
            
            return repairServiceService.findRepairServicesByCustomer(customer.getCustomerId());
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm dịch vụ sửa chữa theo số điện thoại: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }


    //cái main để test thôi nhé
    // không phải là main của chương trình đâu
    public static void main(String[] args) {
        // Ví dụ sử dụng controller
        RepairController controller = new RepairController();

        //Tạo mới dịch vụ sửa chữa có thêm thông tin khách hàng
        // Tìm kiếm khách hàng theo số điện thoại
        //Nếu không tìm thấy, tạo mới khách hàng
        //nếu khách hàng đã tồn tại, lấy thông tin khách hàng đó

        // Ví dụ thêm dịch vụ sửa chữa với thông tin khách hàng
        Repair newService = controller.addRepairServiceWithCustomerInfo(
            "Nguyễn Văn A",        // Tên khách hàng 
            "0916543257",          // Số điện thoại khách hàng
            "NV03",                // Mã nhân viên
            "Laptop Dell XPS 13",  // Tên thiết bị
            "Lỗi màn hình",        // Vấn đề
            "Cần thay màn hình",   // Chẩn đoán
            "Khách hàng cần lấy gấp" // Ghi chú
        );
        System.out.println("Đã thêm dịch vụ sửa chữa thành công: " + newService.getRepairServiceId());

        //cái này để test thôi nhé
        // Đợi người dùng nhấn Enter để tiếp tục
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine(); // Đợi người dùng nhấn Enter để tiếp tục


        // Tìm kiếm dịch vụ sửa chữa theo số điện thoại khách hàng
        // Hiển thị thông tin dịch vụ vừa thêm
        if (newService != null) {
            System.out.println("Đã thêm dịch vụ sửa chữa thành công:");
            System.out.println("Mã dịch vụ: " + newService.getRepairServiceId());
            System.out.println("Khách hàng: " + newService.getCustomer().getFullName());
            System.out.println("Số điện thoại: " + newService.getCustomer().getPhoneNumber());
            System.out.println("Thiết bị: " + newService.getDeviceName());
            System.out.println("Trạng thái: " + newService.getStatus());
            
            // cái này để Cập nhật dịch vụ sửa chữa nè
            Repair updatedService = controller.updateRepairService(
                newService.getRepairServiceId(),
                "NV02",                            // Thay đổi nhân viên phụ trách
                "Màn hình bị nứt, cần thay mới",   // Chẩn đoán mới
                RepairEnum.DIAGNOSING.getStatus(), // Trạng thái mới
                new BigDecimal("1500000"),         // Phí dịch vụ
                "Đã liên hệ với khách hàng"        // Ghi chú mới
            );
            
            // Hiển thị thông tin sau khi cập nhật
            if (updatedService != null) {
                System.out.println("\nCập nhật thành công:");
                System.out.println("Mã dịch vụ: " + updatedService.getRepairServiceId());
                System.out.println("Khách hàng: " + updatedService.getCustomer().getFullName());
                System.out.println("Nhân viên: " + updatedService.getEmployee().getFullName());
                System.out.println("Trạng thái: " + updatedService.getStatus());
            }
        }
    }
}