package com.pcstore.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pcstore.model.Customer;
import com.pcstore.repository.impl.CustomerRepository;

/**
 * Service xử lý logic nghiệp vụ liên quan đến khách hàng
 */
public class CustomerService {
    private final CustomerRepository customerRepository;
    private static final Logger logger = Logger.getLogger(CustomerService.class.getName());
    
    /**
     * Khởi tạo service với csd
     * @param Connection CSDL
     * 
     */

    public CustomerService(Connection connection) {
        this.customerRepository = new CustomerRepository(connection);
    }


    /**
     * Khởi tạo service với repository
     * @param customerRepository Repository khách hàng
     */
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }   

    public String generateCustomerId() {
        return customerRepository.generateCustomerId();
    }
    

    //Lưu 
    public Customer saveCustomer(Customer customer) {
        // Kiểm tra xem customer có tồn tại chưa đã
        Customer existingCustomer = findCustomerById(customer.getCustomerId()).orElse(null);
        if (existingCustomer != null) {
            return updateCustomer(customer);
        } else {
            return addCustomer(customer);
        }
    }


    /**
     * Thêm khách hàng mới
     * @param customer Thông tin khách hàng
     * @return Khách hàng đã được thêm
     */
    public Customer addCustomer(Customer customer) {
        // Kiểm tra trùng lặp email và số điện thoại
        if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
            Optional<Customer> existingByEmail = customerRepository.findByEmail(customer.getEmail());
            if (existingByEmail.isPresent()) {
                throw new IllegalArgumentException("Email đã được sử dụng bởi khách hàng khác");
            }
        }
        
        if (customer.getPhoneNumber() != null && !customer.getPhoneNumber().isEmpty()) {
            Optional<Customer> existingByPhone = customerRepository.findByPhoneNumber(customer.getPhoneNumber());
            if (existingByPhone.isPresent()) {
                throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi khách hàng khác");
            }
        }
        
        return customerRepository.add(customer);
    }
    
    /**
     * Cập nhật thông tin khách hàng
     * @param customer Thông tin khách hàng mới
     * @return Khách hàng đã được cập nhật
     */
    public Customer updateCustomer(Customer customer) {
        // Kiểm tra tồn tại
        if (!customerRepository.exists(customer.getCustomerId())) {
            throw new IllegalArgumentException("Khách hàng không tồn tại");
        }
        
        // Kiểm tra trùng lặp email và số điện thoại (trừ chính khách hàng này)
        if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
            Optional<Customer> existingByEmail = customerRepository.findByEmail(customer.getEmail());
            if (existingByEmail.isPresent() && !existingByEmail.get().getCustomerId().equals(customer.getCustomerId())) {
                throw new IllegalArgumentException("Email đã được sử dụng bởi khách hàng khác");
            }
        }
        
        if (customer.getPhoneNumber() != null && !customer.getPhoneNumber().isEmpty()) {
            Optional<Customer> existingByPhone = customerRepository.findByPhoneNumber(customer.getPhoneNumber());
            if (existingByPhone.isPresent() && !existingByPhone.get().getCustomerId().equals(customer.getCustomerId())) {
                throw new IllegalArgumentException("Số điện thoại đã được sử dụng bởi khách hàng khác");
            }
        }
        
        return customerRepository.update(customer);
    }
    
    /**
     * Xóa khách hàng theo ID
     * @param customerId ID của khách hàng
     * @return true nếu xóa thành công, ngược lại là false
     */
    public boolean deleteCustomer(String customerId) {
        return customerRepository.delete(customerId);
    }
    
    /**
     * Tìm khách hàng theo ID
     * @param customerId ID của khách hàng
     * @return Optional chứa khách hàng nếu tìm thấy
     */
    public Optional<Customer> findCustomerById(String customerId) {
        return customerRepository.findById(customerId);
    }
    
    /**
     * Tìm khách hàng theo ID cho hiển thị thẻ bảo hành
     * @param customerId ID của khách hàng
     * @return Optional<Customer> đối tượng khách hàng với đầy đủ thông tin
     */
    public Optional<Customer> findCustomerByIdForWarranty(String customerId) {
        try {
            return customerRepository.findByIdWarranty(customerId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding customer by ID for warranty", e);
            return Optional.empty();
        }
    }
    
    /**
     * Lấy danh sách tất cả khách hàng
     * @return Danh sách khách hàng
     */
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }
    
    /**
     * Tìm khách hàng theo tên
     * @param name Tên khách hàng cần tìm
     * @return Danh sách khách hàng có tên trùng khớp
     */
    public List<Customer> findCustomersByName(String name) {
        return customerRepository.searchByName(name);
    }
    
    /**
     * Tìm khách hàng theo số điện thoại
     * @param phone Số điện thoại cần tìm
     * @return Optional chứa khách hàng nếu tìm thấy
     */
    public Optional<Customer> findCustomerByPhone(String phone) {
        return customerRepository.findByPhoneNumber(phone);
    }
    
    /**
     * Tìm khách hàng theo email
     * @param email Email cần tìm
     * @return Optional chứa khách hàng nếu tìm thấy
     */
    public Optional<Customer> findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
    
    /**
     * Kiểm tra khách hàng có tồn tại không
     * @param customerId ID của khách hàng
     * @return true nếu khách hàng tồn tại, ngược lại là false
     */
    public boolean customerExists(String customerId) {
        return customerRepository.exists(customerId);
    }

    /**
     * Lấy dữ liệu doanh thu khách hàng trong khoảng thời gian
     * 
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Danh sách dữ liệu doanh thu khách hàng
     */
    public List<Map<String, Object>> getCustomerRevenueData(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return customerRepository.getCustomerRevenueData(startDate, endDate);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi lấy dữ liệu doanh thu khách hàng", e);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    
}