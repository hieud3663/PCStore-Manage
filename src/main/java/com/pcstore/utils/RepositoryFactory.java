package com.pcstore.utils;

import com.pcstore.Repository.impl.*;
import java.sql.Connection;

/**
 * Factory class để tạo và quản lý các Repository trong hệ thống
 */
public class RepositoryFactory {
    private Connection connection;
    
    // Các Repository instance
    private iCustomerRepository customerRepository;
    private iProductRepository productRepository;
    private iCategoryRepository categoryRepository;
    private iSupplierRepository supplierRepository;
    private iEmployeeRepository employeeRepository;
    private iInvoiceRepository invoiceRepository;
    private iInvoiceDetailRepository invoiceDetailRepository;
    private iPurchaseOrderRepository purchaseOrderRepository;
    private iPurchaseOrderDetailRepository purchaseOrderDetailRepository;
    private UserRepository userRepository;
    private WarrantyRepository warrantyRepository;
    private iRepairServiceRepository repairServiceRepository;
    private iReturnRepository returnRepository;
    private iDiscountRepository discountRepository;
    
    public RepositoryFactory(Connection connection) {
        this.connection = connection;
    }
    
    // Lazy initialization pattern cho các Repository
    
    public iCustomerRepository getCustomerRepository() {
        if (customerRepository == null) {
            customerRepository = new iCustomerRepository(connection);
        }
        return customerRepository;
    }
    
    public iProductRepository getProductRepository() {
        if (productRepository == null) {
            productRepository = new iProductRepository(connection);
        }
        return productRepository;
    }
    
    public iCategoryRepository getCategoryRepository() {
        if (categoryRepository == null) {
            categoryRepository = new iCategoryRepository(connection);
        }
        return categoryRepository;
    }
    
    public iSupplierRepository getSupplierRepository() {
        if (supplierRepository == null) {
            supplierRepository = new iSupplierRepository(connection);
        }
        return supplierRepository;
    }
    
    public iEmployeeRepository getEmployeeRepository() {
        if (employeeRepository == null) {
            employeeRepository = new iEmployeeRepository(connection);
        }
        return employeeRepository;
    }
    
    public iInvoiceRepository getInvoiceRepository() {
        if (invoiceRepository == null) {
            invoiceRepository = new iInvoiceRepository(connection, this);
        }
        return invoiceRepository;
    }
    
    public iInvoiceDetailRepository getInvoiceDetailRepository() {
        if (invoiceDetailRepository == null) {
            invoiceDetailRepository = new iInvoiceDetailRepository(connection, this);
        }
        return invoiceDetailRepository;
    }
    
    public iPurchaseOrderRepository getPurchaseOrderRepository() {
        if (purchaseOrderRepository == null) {
            purchaseOrderRepository = new iPurchaseOrderRepository(connection, this);
        }
        return purchaseOrderRepository;
    }
    
    public iPurchaseOrderDetailRepository getPurchaseOrderDetailRepository() {
        if (purchaseOrderDetailRepository == null) {
            purchaseOrderDetailRepository = new iPurchaseOrderDetailRepository(connection, this);
        }
        return purchaseOrderDetailRepository;
    }
    
    public UserRepository getUserRepository() {
        if (userRepository == null) {
            // userRepository = new UserRepository(connection, this);
            userRepository = new UserRepository(connection);
        }
        return userRepository;
    }
    
    public WarrantyRepository getWarrantyRepository() {
        if (warrantyRepository == null) {
            // warrantyRepository = new WarrantyRepository(connection, this);
            warrantyRepository = new WarrantyRepository(connection);
        }
        return warrantyRepository;
    }
    
    public iRepairServiceRepository getRepairServiceRepository() {
        if (repairServiceRepository == null) {
            // repairServiceRepository = new RepairServiceRepository(connection, this);
            repairServiceRepository = new iRepairServiceRepository(connection);
        }
        return repairServiceRepository;
    }
    
    public iReturnRepository getReturnRepository() {
        if (returnRepository == null) {
            // returnRepository = new ReturnRepository(connection, this);
            returnRepository = new iReturnRepository(connection);
        }
        return returnRepository;
    }
    
    public iDiscountRepository getDiscountRepository() {
        if (discountRepository == null) {
            discountRepository = new iDiscountRepository(connection);
        }
        return discountRepository;
    }
    
    // Phương thức để lấy connection
    public Connection getConnection() {
        return connection;
    }
}