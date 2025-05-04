package com.pcstore.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.Repair;
import com.pcstore.model.Warranty;
import com.pcstore.model.enums.RepairEnum;
import com.pcstore.service.CustomerService;
import com.pcstore.service.EmployeeService;
import com.pcstore.service.RepairService;
import com.pcstore.service.WarrantyService;

/**
 * Controller for managing repair services
 */
public class RepairController {
    private final RepairService repairService;
    private final CustomerService customerService;
    private final EmployeeService employeeService;
    private final WarrantyService warrantyService;

    /**
     * Constructor with all required services
     * 
     * @param connection Database connection
     * @param customerService Customer service
     * @param employeeService Employee service
     * @param warrantyService Warranty service
     */
    public RepairController(Connection connection, 
                          CustomerService customerService,
                          EmployeeService employeeService,
                          WarrantyService warrantyService) {
        this.repairService = new RepairService(connection, customerService, employeeService);
        this.customerService = customerService;
        this.employeeService = employeeService;
        this.warrantyService = warrantyService;
    }

    /**
     * Create a new repair service
     * 
     * @param customerId Customer ID hoặc số điện thoại
     * @param employeeId Employee ID
     * @param deviceName Name of the device
     * @param problem Problem description
     * @param warrantyId Warranty ID (optional)
     * @return Created repair service
     */
    public Repair createRepairService(String customerId, String employeeId, 
                                   String deviceName, String problem, 
                                   String diagnosis, String warrantyId) {
        try {
            // Tìm khách hàng theo ID hoặc số điện thoại
            Customer customer = null;
            Optional<Customer> customerOpt = customerService.findCustomerById(customerId);
            
            if (!customerOpt.isPresent()) {
                // Nếu không tìm thấy theo ID, thử tìm theo số điện thoại
                try {
                    Optional<Customer> customerByPhoneOpt = customerService.findCustomerByPhone(customerId);
                    
                    if (customerByPhoneOpt.isPresent()) {
                        // Nếu tìm thấy theo SĐT
                        customer = customerByPhoneOpt.get();
                    } else {
                        // Nếu không tìm thấy, tạo khách hàng mới
                        customer = new Customer();
                        customer.setCustomerId("KH" + System.currentTimeMillis() % 100000); // Tạo ID tạm thời
                        customer.setFullName("Khách hàng mới");
                        customer.setPhoneNumber(customerId); // Dùng số điện thoại vừa nhập
                        // customer.setPoint(0);
                        
                        // Thêm khách hàng mới vào cơ sở dữ liệu
                        customer = customerService.addCustomer(customer);
                        System.out.println("Đã tạo khách hàng mới với ID: " + customer.getCustomerId());
                    }
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Lỗi khi kiểm tra hoặc tạo khách hàng: " + ex.getMessage(), ex);
                }
            } else {
                customer = customerOpt.get();
            }
            
            // Get employee
            Optional<Employee> employeeOpt = employeeService.findEmployeeById(employeeId);
            if (!employeeOpt.isPresent()) {
                throw new IllegalArgumentException("Nhân viên không tồn tại");
            }
            Employee employee = employeeOpt.get();
            
            // Get warranty if provided
            Warranty warranty = null;
            if (warrantyId != null && !warrantyId.isEmpty()) {
                Optional<Warranty> warrantyOpt = warrantyService.findWarrantyById(warrantyId);
                if (warrantyOpt.isPresent()) {
                    warranty = warrantyOpt.get();
                }
            }
            
            // Create repair service
            Repair repair = new Repair();
            repair.setCustomer(customer);
            repair.setEmployee(employee);
            repair.setDeviceName(deviceName);
            repair.setProblem(problem);
            repair.setDiagnosis(diagnosis);
            repair.setReceiveDate(LocalDateTime.now());
            repair.setServiceFee(BigDecimal.ZERO);
            repair.setStatus(RepairEnum.RECEIVED);
            
            if (warranty != null) {
                repair.setWarranty(warranty);
            }
            
            return repairService.addRepairService(repair);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo dịch vụ sửa chữa: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get all repair services
     * 
     * @return List of all repair services
     */
    public List<Repair> getAllRepairServices() {
        try {
            return repairService.getAllRepairServicesWithFullInfo();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách dịch vụ sửa chữa: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get repair service by ID
     * 
     * @param repairId Repair service ID
     * @return Repair service if found
     */
    public Optional<Repair> getRepairServiceById(Integer repairId) {
        try {
            return repairService.findRepairServiceWithFullInfo(repairId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm dịch vụ sửa chữa: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update repair service
     * 
     * @param repairId Repair service ID
     * @param deviceName Device name
     * @param problem Problem description
     * @param diagnosis Diagnosis
     * @param serviceFee Service fee
     * @param status Repair status
     * @param notes Notes
     * @return Updated repair service
     */
    public Repair updateRepairService(Integer repairId, String deviceName, 
                                    String problem, String diagnosis, 
                                    BigDecimal serviceFee, RepairEnum status, 
                                    String notes) {
        try {
            Optional<Repair> repairOpt = repairService.findRepairServiceWithFullInfo(repairId);
            if (!repairOpt.isPresent()) {
                throw new IllegalArgumentException("Dịch vụ sửa chữa không tồn tại");
            }
            
            Repair repair = repairOpt.get();
            
            // Update fields
            if (deviceName != null && !deviceName.isEmpty()) {
                repair.setDeviceName(deviceName);
            }
            
            if (problem != null && !problem.isEmpty()) {
                repair.setProblem(problem);
            }
            
            if (diagnosis != null) {
                repair.setDiagnosis(diagnosis);
            }
            
            if (serviceFee != null) {
                repair.setServiceFee(serviceFee);
            }
            
            if (status != null) {
                repair.setStatus(status);
            }
            
            if (notes != null) {
                repair.setNotes(notes);
            }
            
            return repairService.updateRepairService(repair);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật dịch vụ sửa chữa: " + e.getMessage(), e);
        }
    }
    
    /**
     * Assign employee to repair service
     * 
     * @param repairId Repair service ID
     * @param employeeId Employee ID
     * @return true if successful
     */
    public boolean assignEmployee(Integer repairId, String employeeId) {
        try {
            Optional<Repair> repairOpt = repairService.findRepairServiceWithFullInfo(repairId);
            if (!repairOpt.isPresent()) {
                throw new IllegalArgumentException("Dịch vụ sửa chữa không tồn tại");
            }
            
            Optional<Employee> employeeOpt = employeeService.findEmployeeById(employeeId);
            if (!employeeOpt.isPresent()) {
                throw new IllegalArgumentException("Nhân viên không tồn tại");
            }
            
            return repairService.assignEmployee(repairId, employeeOpt.get());
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi phân công nhân viên: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update diagnosis
     * 
     * @param repairId Repair service ID
     * @param diagnosis Diagnosis
     * @return true if successful
     */
    public boolean updateDiagnosis(Integer repairId, String diagnosis) {
        try {
            return repairService.updateDiagnosis(repairId, diagnosis);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật chẩn đoán: " + e.getMessage(), e);
        }
    }
    
    /**
     * Start repair process
     * 
     * @param repairId Repair service ID
     * @return true if successful
     */
    public boolean startRepair(Integer repairId) {
        try {
            return repairService.startRepairService(repairId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi bắt đầu sửa chữa: " + e.getMessage(), e);
        }
    }
    
    /**
     * Complete repair service
     * 
     * @param repairId Repair service ID
     * @param notes Completion notes
     * @param finalCost Final cost
     * @return true if successful
     */
    public boolean completeRepair(Integer repairId, String notes, double finalCost) {
        try {
            return repairService.completeRepairService(repairId, LocalDateTime.now(), notes, finalCost);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi hoàn thành sửa chữa: " + e.getMessage(), e);
        }
    }
    
    /**
     * Cancel repair service
     * 
     * @param repairId Repair service ID
     * @return true if successful
     */
    public boolean cancelRepair(Integer repairId) {
        try {
            return repairService.cancelRepairService(repairId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi hủy sửa chữa: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete repair service
     * 
     * @param repairId Repair service ID
     * @return true if successful
     */
    public boolean deleteRepair(Integer repairId) {
        try {
            return repairService.deleteRepairService(repairId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa dịch vụ sửa chữa: " + e.getMessage(), e);
        }
    }
    
    /**
     * Find repair services by customer
     * 
     * @param customerId Customer ID
     * @return List of repair services
     */
    public List<Repair> getRepairServicesByCustomer(String customerId) {
        try {
            return repairService.findRepairServicesByCustomer(customerId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm dịch vụ sửa chữa theo khách hàng: " + e.getMessage(), e);
        }
    }
    
    /**
     * Find repair services by employee
     * 
     * @param employeeId Employee ID
     * @return List of repair services
     */
    public List<Repair> getRepairServicesByEmployee(String employeeId) {
        try {
            return repairService.findRepairServicesByEmployee(employeeId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm dịch vụ sửa chữa theo nhân viên: " + e.getMessage(), e);
        }
    }
    
    /**
     * Find repair services by status
     * 
     * @param status Repair status
     * @return List of repair services
     */
    public List<Repair> getRepairServicesByStatus(RepairEnum status) {
        try {
            return repairService.findRepairServicesByStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm dịch vụ sửa chữa theo trạng thái: " + e.getMessage(), e);
        }
    }
    
    /**
     * Find due repair services
     * 
     * @return List of due repair services
     */
    public List<Repair> getDueRepairServices() {
        try {
            return repairService.findDueRepairServices();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm dịch vụ sửa chữa đến hạn: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update service fee
     * 
     * @param repairId Repair service ID
     * @param serviceFee Service fee
     * @return true if successful
     */
    public boolean updateServiceFee(Integer repairId, BigDecimal serviceFee) {
        try {
            return repairService.updateServiceFee(repairId, serviceFee);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật phí dịch vụ: " + e.getMessage(), e);
        }
    }
    
    /**
     * Create a new repair service with warranty information
     * 
     * @param customerId Customer ID
     * @param employeeId Employee ID
     * @param description Repair description
     * @param warrantyId Warranty ID
     * @return Created repair service
     */
    public Repair createWarrantyRepair(String customerId, String employeeId, String description, String warrantyId) {
        try {
            // Get customer
            Optional<Customer> customerOpt = customerService.findCustomerById(customerId);
            if (!customerOpt.isPresent()) {
                throw new IllegalArgumentException("Khách hàng không tồn tại");
            }
            Customer customer = customerOpt.get();
            
            // Get employee
            Optional<Employee> employeeOpt = employeeService.findEmployeeById(employeeId);
            if (!employeeOpt.isPresent()) {
                throw new IllegalArgumentException("Nhân viên không tồn tại");
            }
            Employee employee = employeeOpt.get();
            
            // Get warranty
            Optional<Warranty> warrantyOpt = warrantyService.findWarrantyById(warrantyId);
            if (!warrantyOpt.isPresent()) {
                throw new IllegalArgumentException("Bảo hành không tồn tại");
            }
            Warranty warranty = warrantyOpt.get();
            
            // Create repair service
            Repair repair = repairService.createNewRepairService(customer, employee, description, warranty);
            return repairService.addRepairService(repair);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo dịch vụ sửa chữa bảo hành: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lấy tất cả nhân viên
     * 
     * @return Danh sách tất cả nhân viên
     */
    public List<Employee> getAllEmployees() {
        try {
            return employeeService.findAllEmployees();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách nhân viên: " + e.getMessage(), e);
        }
    }
    
    /**
     * Tìm khách hàng theo số điện thoại
     * 
     * @param phoneNumber Số điện thoại khách hàng
     * @return Optional<Customer> Đối tượng khách hàng (nếu tìm thấy)
     */
    public Optional<Customer> findCustomerByPhone(String phoneNumber) {
        try {
            return customerService.findCustomerByPhone(phoneNumber);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm khách hàng theo số điện thoại: " + e.getMessage(), e);
        }
    }

    /**
     * Tạo mới khách hàng
     * 
     * @param fullName Tên khách hàng
     * @param phoneNumber Số điện thoại
     * @return Customer Đối tượng khách hàng đã tạo
     */
    public Customer createCustomer(String fullName, String phoneNumber) {
        try {
            Customer customer = new Customer();
            // Tạo ID khách hàng theo định dạng KH + số ngẫu nhiên
            String customerId = "KH" + String.format("%05d", (int)(Math.random() * 100000));
            
            customer.setCustomerId(customerId);
            customer.setFullName(fullName);
            customer.setPhoneNumber(phoneNumber);
            // Đặt các giá trị mặc định khác
            
            return customerService.addCustomer(customer);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo khách hàng mới: " + e.getMessage(), e);
        }
    }
}