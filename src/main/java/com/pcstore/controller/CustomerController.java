package com.pcstore.controller;

import com.pcstore.model.Customer;
import com.pcstore.service.CustomerService;
import com.pcstore.service.ServiceFactory;

import java.util.List;
import java.util.Optional;
import javax.swing.JOptionPane;

/**
 * Controller cho quản lý khách hàng
 */
public class CustomerController {
    private CustomerService customerService;
    
    public CustomerController() {
        try {
            this.customerService = ServiceFactory.getCustomerService();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi kết nối đến cơ sở dữ liệu", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Thêm khách hàng mới
     * @param customer Thông tin khách hàng
     * @return Khách hàng đã được thêm, null nếu thất bại
     */
    public Customer addCustomer(Customer customer) {
        try {
            return customerService.createCustomer(customer);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi thêm khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Cập nhật thông tin khách hàng
     * @param customer Thông tin khách hàng cần cập nhật
     * @return Khách hàng sau khi cập nhật, null nếu thất bại
     */
    public Customer updateCustomer(Customer customer) {
        try {
            return customerService.updateCustomer(customer);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi cập nhật khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Xóa khách hàng
     * @param customerId Mã khách hàng cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteCustomer(String customerId) {
        try {
            return customerService.deleteCustomer(customerId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi xóa khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Tìm khách hàng theo mã
     * @param customerId Mã khách hàng
     * @return Khách hàng nếu tìm thấy, null nếu không tìm thấy
     */
    public Customer findCustomerById(String customerId) {
        try {
            Optional<Customer> customer = customerService.getCustomerById(customerId);
            return customer.orElse(null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi tìm khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Tìm khách hàng theo số điện thoại
     * @param phoneNumber Số điện thoại cần tìm
     * @return Khách hàng nếu tìm thấy, null nếu không tìm thấy
     */
    public Customer findCustomerByPhone(String phoneNumber) {
        try {
            Optional<Customer> customer = customerService.getCustomerByPhone(phoneNumber);
            return customer.orElse(null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi tìm khách hàng theo số điện thoại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * Tìm kiếm khách hàng theo tên
     * @param name Tên khách hàng cần tìm
     * @return Danh sách khách hàng phù hợp
     */
    public List<Customer> searchCustomersByName(String name) {
        try {
            return customerService.searchCustomersByName(name);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi tìm kiếm khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return List.of();
        }
    }
    
    /**
     * Lấy danh sách tất cả khách hàng
     * @return Danh sách khách hàng
     */
    public List<Customer> getAllCustomers() {
        try {
            return customerService.getAllCustomers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi lấy danh sách khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return List.of();
        }
    }
    
    /**
     * Tạo mã khách hàng mới
     * @return Mã khách hàng mới
     */
    public String generateNewCustomerId() {
        try {
            return ((com.pcstore.service.impl.CustomerServiceImpl) customerService).generateNewCustomerId();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi khi tạo mã khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return "KH00";
        }
    }
}