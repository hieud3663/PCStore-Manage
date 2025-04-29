package com.pcstore.repository;

import com.pcstore.repository.impl.*;
import java.sql.Connection;

/**
 * Factory class để tạo và quản lý các Repository trong hệ thống
 */
public class RepositoryFactory {
    private Connection connection;
    
    // Individual repository instances
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private SupplierRepository supplierRepository;
    private EmployeeRepository employeeRepository;
    private InvoiceRepository invoiceRepository;
    private InvoiceDetailRepository invoiceDetailRepository;

    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    private PurchaseOrderRepository purchaseOrderRepository;
    private RepairRepository repairRepository;
    private WarrantyRepository warrantyRepository;
    // Singleton pattern implementation
    private static RepositoryFactory instance;
    private static Connection currentConnection;
    

    public RepositoryFactory(Connection connection) {
        this.connection = connection;
    }
    
    public static synchronized RepositoryFactory getInstance(Connection connection) {
        if (instance == null || currentConnection != connection) {
            instance = new RepositoryFactory(connection);
            currentConnection = connection;
        }
        return instance;
    }
    
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

    public PurchaseOrderDetailRepository getPurchaseOrderDetailRepository() {
        if (purchaseOrderDetailRepository == null) {
            purchaseOrderDetailRepository = new PurchaseOrderDetailRepository(connection, this);
        }
        return purchaseOrderDetailRepository;
    }

    public PurchaseOrderRepository getPurchaseOrderRepository() {
        if (purchaseOrderRepository == null) {
            purchaseOrderRepository = new PurchaseOrderRepository(connection, this);
        }
        return purchaseOrderRepository;
    }


    public WarrantyRepository getWarrantyRepository() {
        if (warrantyRepository == null) {
            warrantyRepository = new WarrantyRepository(connection);
        }
        return warrantyRepository;
    }

    
    public Connection getConnection() {
        return connection;
    }
}
