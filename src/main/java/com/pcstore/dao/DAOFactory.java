package com.pcstore.dao;

import com.pcstore.dao.impl.*;
import java.sql.Connection;

/**
 * Factory class để tạo và quản lý các DAO trong hệ thống
 */
public class DAOFactory {
    private Connection connection;
    
    // Các DAO instance
    private CustomerDAO customerDAO;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private SupplierDAO supplierDAO;
    private EmployeeDAO employeeDAO;
    private InvoiceDAO invoiceDAO;
    private InvoiceDetailDAO invoiceDetailDAO;
    private PurchaseOrderDAO purchaseOrderDAO;
    private PurchaseOrderDetailDAO purchaseOrderDetailDAO;
    private UserDAO userDAO;
    private WarrantyDAO warrantyDAO;
    private RepairServiceDAO repairServiceDAO;
    private ReturnDAO returnDAO;
    private DiscountDAO discountDAO;
    
    public DAOFactory(Connection connection) {
        this.connection = connection;
    }
    
    // Lazy initialization pattern cho các DAO
    
    public CustomerDAO getCustomerDAO() {
        if (customerDAO == null) {
            customerDAO = new CustomerDAO(connection);
        }
        return customerDAO;
    }
    
    public ProductDAO getProductDAO() {
        if (productDAO == null) {
            productDAO = new ProductDAO(connection);
        }
        return productDAO;
    }
    
    public CategoryDAO getCategoryDAO() {
        if (categoryDAO == null) {
            categoryDAO = new CategoryDAO(connection);
        }
        return categoryDAO;
    }
    
    public SupplierDAO getSupplierDAO() {
        if (supplierDAO == null) {
            supplierDAO = new SupplierDAO(connection);
        }
        return supplierDAO;
    }
    
    public EmployeeDAO getEmployeeDAO() {
        if (employeeDAO == null) {
            employeeDAO = new EmployeeDAO(connection);
        }
        return employeeDAO;
    }
    
    public InvoiceDAO getInvoiceDAO() {
        if (invoiceDAO == null) {
            invoiceDAO = new InvoiceDAO(connection, this);
        }
        return invoiceDAO;
    }
    
    public InvoiceDetailDAO getInvoiceDetailDAO() {
        if (invoiceDetailDAO == null) {
            invoiceDetailDAO = new InvoiceDetailDAO(connection, this);
        }
        return invoiceDetailDAO;
    }
    
    public PurchaseOrderDAO getPurchaseOrderDAO() {
        if (purchaseOrderDAO == null) {
            purchaseOrderDAO = new PurchaseOrderDAO(connection, this);
        }
        return purchaseOrderDAO;
    }
    
    public PurchaseOrderDetailDAO getPurchaseOrderDetailDAO() {
        if (purchaseOrderDetailDAO == null) {
            purchaseOrderDetailDAO = new PurchaseOrderDetailDAO(connection, this);
        }
        return purchaseOrderDetailDAO;
    }
    
    public UserDAO getUserDAO() {
        if (userDAO == null) {
            // userDAO = new UserDAO(connection, this);
            userDAO = new UserDAO(connection);
        }
        return userDAO;
    }
    
    public WarrantyDAO getWarrantyDAO() {
        if (warrantyDAO == null) {
            // warrantyDAO = new WarrantyDAO(connection, this);
            warrantyDAO = new WarrantyDAO(connection);
        }
        return warrantyDAO;
    }
    
    public RepairServiceDAO getRepairServiceDAO() {
        if (repairServiceDAO == null) {
            // repairServiceDAO = new RepairServiceDAO(connection, this);
            repairServiceDAO = new RepairServiceDAO(connection);
        }
        return repairServiceDAO;
    }
    
    public ReturnDAO getReturnDAO() {
        if (returnDAO == null) {
            // returnDAO = new ReturnDAO(connection, this);
            returnDAO = new ReturnDAO(connection);
        }
        return returnDAO;
    }
    
    public DiscountDAO getDiscountDAO() {
        if (discountDAO == null) {
            discountDAO = new DiscountDAO(connection);
        }
        return discountDAO;
    }
    
    // Phương thức để lấy connection
    public Connection getConnection() {
        return connection;
    }
}