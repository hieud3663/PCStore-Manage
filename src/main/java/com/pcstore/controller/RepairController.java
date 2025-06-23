package com.pcstore.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.pcstore.model.Customer;
import com.pcstore.model.Employee;
import com.pcstore.model.Repair;
import com.pcstore.model.Warranty;
import com.pcstore.model.enums.RepairEnum;
import com.pcstore.service.CustomerService;
import com.pcstore.service.EmployeeService;
import com.pcstore.service.RepairService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.service.WarrantyService;
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.view.AddReapairProductForm;
import com.pcstore.view.RepairDetailsForm;
import com.pcstore.view.RepairServiceForm;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for managing repair services
 */
public class RepairController {
    private RepairService repairService;
    private CustomerService customerService;
    private EmployeeService employeeService;
    private WarrantyService warrantyService;
    private Connection connection;
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
        this.connection = connection;
        this.repairService = new RepairService(connection, customerService, employeeService);
        this.customerService = customerService;
        this.employeeService = employeeService;
        this.warrantyService = warrantyService;
    }
    
    public RepairController(){
        try {
            connection = DatabaseConnection.getInstance().getConnection();
            customerService = ServiceFactory.getInstance().getCustomerService();
            employeeService = ServiceFactory.getInstance().getEmployeeService();
            warrantyService = ServiceFactory.getInstance().getWarrantyService();
            repairService = ServiceFactory.getInstance().getRepairServiceService();
        } catch (SQLException ex) {
            Logger.getLogger(RepairController.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (Exception e) {
            Logger.getLogger(RepairController.class.getName()).log(Level.SEVERE, null, e);
            e.printStackTrace();
        }
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
                    ex.printStackTrace();
                    throw new IllegalArgumentException("Lỗi khi kiểm tra hoặc tạo khách hàng: " + ex.getMessage(), ex);
                }
            } else {
                customer = customerOpt.get();
            }
            
            // Get employee
            Optional<Employee> employeeOpt = employeeService.findEmployeeById(employeeId);
            if (!employeeOpt.isPresent()) {
                throw new IllegalArgumentException(ErrorMessage.EMPLOYEE_NOT_FOUND.toString());
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
            throw new RuntimeException(ErrorMessage.REPAIR_CREATE_ERROR + ": " + e.getMessage(), e);
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
            throw new RuntimeException(ErrorMessage.REPAIR_LIST_ERROR + ": " + e.getMessage(), e);
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
            throw new RuntimeException(ErrorMessage.REPAIR_FIND_ERROR + ": " + e.getMessage(), e);
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
                throw new IllegalArgumentException(ErrorMessage.REPAIR_NOT_FOUND.toString());
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
            throw new RuntimeException(ErrorMessage.REPAIR_UPDATE_ERROR + ": " + e.getMessage(), e);
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
                throw new IllegalArgumentException(ErrorMessage.REPAIR_NOT_FOUND.toString());
            }
            
            Optional<Employee> employeeOpt = employeeService.findEmployeeById(employeeId);
            if (!employeeOpt.isPresent()) {
                throw new IllegalArgumentException(ErrorMessage.EMPLOYEE_NOT_FOUND.toString());
            }
            
            return repairService.assignEmployee(repairId, employeeOpt.get());
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.REPAIR_ASSIGN_EMPLOYEE_ERROR + ": " + e.getMessage(), e);
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
            throw new RuntimeException(ErrorMessage.REPAIR_UPDATE_FEE_ERROR + ": " + e.getMessage(), e);
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
            e.printStackTrace();
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

    /**
     * Force update repair service status (bypass validation)
     * 
     * @param repairId Repair service ID
     * @param status New status
     * @return Updated repair service
     */
    public Repair forceUpdateRepairStatus(Integer repairId, RepairEnum status) {
        try {
            // Temporarily enable force update
            System.setProperty("repair.status.force", "true");
            
            Optional<Repair> repairOpt = repairService.findRepairServiceWithFullInfo(repairId);
            if (!repairOpt.isPresent()) {
                throw new IllegalArgumentException("Dịch vụ sửa chữa không tồn tại");
            }
            
            Repair repair = repairOpt.get();
            repair.setStatus(status);
            
            Repair result = repairService.updateRepairService(repair);
            
            // Disable force update
            System.setProperty("repair.status.force", "false");
            
            return result;
        } catch (Exception e) {
            System.setProperty("repair.status.force", "false");
            throw new RuntimeException("Lỗi khi cập nhật trạng thái dịch vụ sửa chữa: " + e.getMessage(), e);
        }
    }

    // Map trạng thái tiếng Anh <-> tiếng Việt (có thể chuyển sang static nếu cần)
    private static final Map<String, String> statusRepairTranslation = Map.of(
        "Received", "Đã tiếp nhận",
        "Diagnosing", "Đang chẩn đoán",
        "Waiting for Parts", "Chờ linh kiện",
        "Repairing", "Đang sửa chữa",
        "Completed", "Đã hoàn thành",
        "Delivered", "Đã giao khách",
        "Cancelled", "Đã hủy"
    );

    public void handleAddRepair(RepairServiceForm form) {
        try {
            AddReapairProductForm addDialog = new AddReapairProductForm();
            addDialog.setTitle("Thêm sản phẩm sửa chữa");
            addDialog.setSize(850, 700);
            addDialog.setLocationRelativeTo(form);
            addDialog.setModal(true);
            addDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            addDialog.setRepairController(this);

            addDialog.setVisible(true);

            if (addDialog.isRepairAdded()) {
                form.loadRepairServices();
                System.out.println("Đã cập nhật bảng sau khi thêm mới dịch vụ sửa chữa");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(form,
                ErrorMessage.REPAIR_FORM_ADD_ERROR + ": " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleRemoveRepair(RepairServiceForm form) {
        JTable tableRepair = form.getTableRepair();
        int selectedRow = tableRepair.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(form,
                ErrorMessage.REPAIR_SELECT_ONE_DELETE,
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Object repairIdObj = tableRepair.getValueAt(selectedRow, 0);
        if (repairIdObj == null || repairIdObj.toString().isEmpty()) {
            JOptionPane.showMessageDialog(form,
                ErrorMessage.REPAIR_ID_INVALID,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        Integer repairId;
        try {
            repairId = Integer.parseInt(repairIdObj.toString());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(form,
                "ID dịch vụ sửa chữa không hợp lệ: " + repairIdObj,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        int choice = JOptionPane.showConfirmDialog(form,
            ErrorMessage.REPAIR_DELETE_CONFIRM,
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        boolean success = deleteRepair(repairId);
        if (success) {
            JOptionPane.showMessageDialog(form,
                ErrorMessage.REPAIR_DELETE_SUCCESS,
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
            form.loadRepairServices();
        } else {
            JOptionPane.showMessageDialog(form,
                ErrorMessage.REPAIR_DELETE_FAIL,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleDetailRepair(RepairServiceForm form) {
        JTable tableRepair = form.getTableRepair();
        int selectedRow = tableRepair.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(form,
                ErrorMessage.REPAIR_SELECT_ONE,
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Object repairIdObj = tableRepair.getValueAt(selectedRow, 0);
        if (repairIdObj == null || repairIdObj.toString().isEmpty()) {
            JOptionPane.showMessageDialog(form,
                ErrorMessage.REPAIR_ID_INVALID,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        Integer repairId;
        try {
            repairId = Integer.parseInt(repairIdObj.toString());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(form,
                "ID dịch vụ sửa chữa không hợp lệ: " + repairIdObj,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        Optional<Repair> repairOpt = getRepairServiceById(repairId);
        if (!repairOpt.isPresent()) {
            JOptionPane.showMessageDialog(form,
                String.format(ErrorMessage.REPAIR_NOT_FOUND_WITH_ID.toString(), repairId),
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JDialog detailDialog = new JDialog();
        detailDialog.setTitle("Chi tiết dịch vụ sửa chữa");
        detailDialog.setSize(900, 800);
        detailDialog.setLocationRelativeTo(form);
        detailDialog.setModal(true);

        RepairDetailsForm detailsForm = new RepairDetailsForm();
        detailsForm.setRepairDetails(repairOpt.get());
        detailDialog.getContentPane().add(detailsForm, java.awt.BorderLayout.CENTER);
        detailsForm.addCloseButton(detailDialog);

        detailDialog.pack();
        detailDialog.setVisible(true);
    }

    public void handleUpdateRepairStatus(RepairServiceForm form) {
        JTable table = form.getTableRepair();
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(form,
                "Vui lòng chọn một dịch vụ sửa chữa để cập nhật trạng thái.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Object repairIdObj = table.getValueAt(selectedRow, 0);
        if (repairIdObj == null || repairIdObj.toString().isEmpty()) {
            JOptionPane.showMessageDialog(form,
                "ID dịch vụ sửa chữa không hợp lệ.",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        Integer repairId;
        try {
            repairId = Integer.parseInt(repairIdObj.toString());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(form,
                "ID dịch vụ sửa chữa không hợp lệ: " + repairIdObj,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Danh sách trạng thái mới
        String[] statuses = {
            "Đã tiếp nhận",
            "Đang chẩn đoán",
            "Chờ linh kiện",
            "Đang sửa chữa",
            "Đã hoàn thành",
            "Đã giao khách",
            "Đã hủy"
        };
        // Lấy trạng thái hiện tại ở cột 6
        String currentStatus = table.getValueAt(selectedRow, 6).toString();
        String newStatus = (String) JOptionPane.showInputDialog(
            form,
            "Chọn trạng thái mới:",
            "Cập nhật trạng thái",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statuses,
            currentStatus
        );
        if (newStatus == null || newStatus.equals(currentStatus)) {
            return; // Không chọn hoặc không thay đổi
        }

        // Chuyển trạng thái tiếng Việt sang Enum tiếng Anh nếu cần
        String statusEnum = null;
        for (Map.Entry<String, String> entry : statusRepairTranslation.entrySet()) {
            if (entry.getValue().equals(newStatus)) {
                statusEnum = entry.getKey();
                break;
            }
        }
        if (statusEnum == null) statusEnum = newStatus; // fallback

        // Gọi service cập nhật trạng thái
        boolean success = repairService.updateServiceStatus(repairId, statusEnum);
        if (success) {
            JOptionPane.showMessageDialog(form,
                "Cập nhật trạng thái thành công!",
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
            form.loadRepairServices();
        } else {
            JOptionPane.showMessageDialog(form,
                "Cập nhật trạng thái thất bại!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Thêm các hàm getTableRepair(), getTableModel(), ... vào RepairServiceForm nếu cần
}