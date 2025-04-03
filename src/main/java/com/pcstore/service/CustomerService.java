package com.pcstore.service;

import com.pcstore.model.Customer;
import com.pcstore.repository.iCustomerRepository;
import com.pcstore.repository.impl.CustomerRepository;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý logic nghiệp vụ liên quan đến khách hàng
 */
public class CustomerService {
    private final CustomerRepository customerRepository;
    
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
}