package com.pcstore.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pcstore.model.Employee;
import com.pcstore.repository.impl.EmployeeRepository;

/**
 * Service xử lý logic nghiệp vụ liên quan đến nhân viên
 */
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final Connection connection;
    private final UserService userService;
    /**
     * Khởi tạo service với csdl
     * @param connection Kết nối csdl
     */
    public EmployeeService(Connection connection) {
        try{
            this.employeeRepository = new EmployeeRepository(connection);
            this.connection = connection;
            this.userService = ServiceFactory.getUserService();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi khởi tạo EmployeeService: " + e.getMessage(), e);
        }
        
    }

    /**
     * Khởi tạo service với repository
     * @param employeeRepository Repository nhân viên
     */
    public EmployeeService(EmployeeRepository employeeRepository) {
        try{
            this.employeeRepository = employeeRepository;
            this.userService = ServiceFactory.getUserService();
            this.connection = null;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi khởi tạo EmployeeService: " + e.getMessage(), e);
        }
    }
    
    /**
     * Thêm hoặc cập nhật nhân viên
     * @param employee Thông tin nhân viên
     * @return Nhân viên đã được lưu
     */
    public Employee saveEmployee(Employee employee) {
        // Kiểm tra trùng lặp email và số điện thoại
        validateUniqueFields(employee);
        
        if (employee.getEmployeeId() == null || employee.getEmployeeId().isEmpty()) {
            employee.setEmployeeId(employeeRepository.generateEmployeeId());
        }
        
        return employeeRepository.save(employee);
    }
    
    /**
     * Tìm nhân viên theo ID
     * @param employeeId ID của nhân viên
     * @return Optional chứa nhân viên nếu tìm thấy
     */
    public Optional<Employee> findEmployeeById(String employeeId) {
        return employeeRepository.findById(employeeId);
    }
    
    /**
     * Lấy danh sách tất cả nhân viên
     * @return Danh sách nhân viên
     */
    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }
    
    /**
     * Kiểm tra nhân viên có tồn tại không
     * @param employeeId ID của nhân viên
     * @return true nếu nhân viên tồn tại, ngược lại là false
     */
    // public boolean employeeExists(String employeeId) {
    //     return employeeRepository.existsById(employeeId);
    // }
    
    /**
     * Xóa nhân viên theo ID
     * @param employeeId ID của nhân viên
     */
    public void deleteEmployee(String employeeId) {
        employeeRepository.delete(employeeId);
    }
    
    /**
     * Tìm nhân viên theo chức vụ
     * @param position Chức vụ cần tìm
     * @return Danh sách nhân viên có chức vụ trùng khớp
     */
    public List<Employee> findEmployeesByPosition(String position) {
        return employeeRepository.findByPosition(position);
    }
    
    /**
     * Tìm nhân viên theo email
     * @param email Email cần tìm
     * @return Optional chứa nhân viên nếu tìm thấy
     */
    public Optional<Employee> findEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }
    
    /**
     * Tìm nhân viên theo số điện thoại
     * @param phoneNumber Số điện thoại cần tìm
     * @return Optional chứa nhân viên nếu tìm thấy
     */
    public Optional<Employee> findEmployeeByPhoneNumber(String phoneNumber) {
        return employeeRepository.findByPhoneNumber(phoneNumber);
    }
    
    /**
     * Tìm kiếm nhân viên theo từ khóa
     * @param searchTerm Từ khóa tìm kiếm
     * @return Danh sách nhân viên phù hợp
     */
    public List<Employee> searchEmployees(String searchTerm) {
        return employeeRepository.search(searchTerm);
    }
    
    /**
     * Tạo mã nhân viên mới
     * @return Mã nhân viên mới
     */
    public String generateEmployeeId() {
        return employeeRepository.generateEmployeeId();
    }
    

    public boolean hasUserAccount(String employeeId) {
        return userService.findUserByEmployeeId(employeeId).isPresent();
    }

    /**
     * Kiểm tra các trường dữ liệu độc nhất
     * @param employee Nhân viên cần kiểm tra
     */
    private void validateUniqueFields(Employee employee) {
        // Kiểm tra email đã tồn tại chưa
        if (employee.getEmail() != null && !employee.getEmail().isEmpty()) {
            Optional<Employee> existingByEmail = employeeRepository.findByEmail(employee.getEmail());
            if (existingByEmail.isPresent() && !existingByEmail.get().getEmployeeId().equals(employee.getEmployeeId())) {
                throw new IllegalArgumentException("Email đã được sử dụng bởi nhân viên khác");
            }
        }
        
        // Kiểm tra số điện thoại đã tồn tại chưa
        if (employee.getPhoneNumber() != null && !employee.getPhoneNumber().isEmpty()) {
            Optional<Employee> existingByPhone = employeeRepository.findByPhoneNumber(employee.getPhoneNumber());
            if (existingByPhone.isPresent() && !existingByPhone.get().getEmployeeId().equals(employee.getEmployeeId())) {
                throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi nhân viên khác");
            }
        }
    }

  
}