package com.pcstore.repository;

import com.pcstore.repository.impl.*;
import com.pcstore.repository.impl.UserRepository;
import java.sql.Connection;

/**
 * Factory class để tạo và quản lý các Repository trong hệ thống
 */
public class RepositoryFactory {
    private Connection connection;
    
    // Các Repository instance
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private SupplierRepository supplierRepository;
    private EmployeeRepository employeeRepository;
    private InvoiceRepository invoiceRepository;
    private InvoiceDetailRepository invoiceDetailRepository;
    private PurchaseOrderRepository purchaseOrderRepository;
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    private UserRepository userRepository;
    private WarrantyRepository warrantyRepository;
    private RepairRepository repairServiceRepository;
    private ReturnRepository returnRepository;
    private DiscountRepository discountRepository;
    
    public RepositoryFactory(Connection connection) {
        this.connection = connection;
    }
    
    // Lazy initialization pattern cho các Repository
    
    public CustomerRepository getCustomerRepository() {
        if (customerRepository == null) {
            customerRepository = new CustomerRepository(connection);
        }
        return customerRepository;
    }
    
    public ProductRepository getProductRepository() {
        if (productRepository == null) {
            productRepository = new ProductRepository(connection);
        }
        return productRepository;
    }
    
    public CategoryRepository getCategoryRepository() {
        if (categoryRepository == null) {
            categoryRepository = new CategoryRepository(connection);
        }
        return categoryRepository;
    }
    
    public SupplierRepository getSupplierRepository() {
        if (supplierRepository == null) {
            supplierRepository = new SupplierRepository(connection);
        }
        return supplierRepository;
    }
    
    public EmployeeRepository getEmployeeRepository() {
        if (employeeRepository == null) {
            employeeRepository = new EmployeeRepository(connection) {};
        }
        return employeeRepository;
    }
    
    public InvoiceRepository getInvoiceRepository() {
        if (invoiceRepository == null) {
            invoiceRepository = new InvoiceRepository(connection, this);
        }
        return invoiceRepository;
    }
    
    public InvoiceDetailRepository getInvoiceDetailRepository() {
        if (invoiceDetailRepository == null) {
            invoiceDetailRepository = new InvoiceDetailRepository(connection, this);
        }
        return invoiceDetailRepository;
    }
    
    public PurchaseOrderRepository getPurchaseOrderRepository() {
        if (purchaseOrderRepository == null) {
            purchaseOrderRepository = new PurchaseOrderRepository(connection, this);
        }
        return purchaseOrderRepository;
    }
    
    public PurchaseOrderDetailRepository getPurchaseOrderDetailRepository() {
        if (purchaseOrderDetailRepository == null) {
            purchaseOrderDetailRepository = new PurchaseOrderDetailRepository(connection, this);
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
    
    public RepairRepository getRepairServiceRepository() {
        if (repairServiceRepository == null) {
            // repairServiceRepository = new RepairServiceRepository(connection, this);
            repairServiceRepository = new RepairRepository(connection);
        }
        return repairServiceRepository;
    }
    
    public ReturnRepository getReturnRepository() {
        if (returnRepository == null) {
            // returnRepository = new ReturnRepository(connection, this);
            returnRepository = new ReturnRepository(connection);
        }
        return returnRepository;
    }
    
    public DiscountRepository getDiscountRepository() {
        if (discountRepository == null) {
            discountRepository = new DiscountRepository(connection);
        }
        return discountRepository;
    }
    
    // Phương thức để lấy connection
    public Connection getConnection() {
        return connection;
    }
}