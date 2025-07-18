package com.pcstore.service;

import java.sql.Connection;
import java.sql.SQLException;

import com.pcstore.repository.RepositoryFactory;
import com.pcstore.service.RevenueService;
import com.pcstore.utils.DatabaseConnection;

/**
 * Factory class để tạo và quản lý các Service trong hệ thống
 */
public class ServiceFactory {
    private static Connection connection;
    private static ServiceFactory instance;
    private static RepositoryFactory repositoryFactory;
    // Services
    private static UserService userService;
    private static EmployeeService employeeService;
    private static CustomerService customerService;
    private static ProductService productService;
    private static SupplierService supplierService;
    private static InvoiceService invoiceService; 
    private static InvoiceDetailService invoiceDetailService;
    private static PurchaseOrderService purchaseOrderService;
    private static RepairService repairServiceService;
    private static WarrantyService warrantyService;
    private static ReturnService returnService;
    private static RevenueService revenueService;
    // private static UserService userService;
    private static PurchaseOrderDetailService purchaseOrderDetailService;
    private static CategoryService categoryService;
    private static InventoryCheckService inventoryCheckService;

    /**
     * Khởi tạo factory và kết nối đến cơ sở dữ liệu
     * @throws SQLException Nếu không thể kết nối đến database
     */
    private ServiceFactory() throws SQLException {
        connection = DatabaseConnection.getInstance().getConnection();
        repositoryFactory = RepositoryFactory.getInstance(connection);
    }
    
    /**
     * Lấy instance của factory
     * @return ServiceFactory instance
     * @throws SQLException Nếu không thể kết nối đến database
     */
    public static synchronized ServiceFactory getInstance() throws SQLException {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }
    
    /**
     * Lấy UserService
     * @return UserService instance
     * @throws SQLException Nếu có lỗi với kết nối database
     */
    public static UserService getUserService() throws SQLException {
        if (userService == null) {
            userService = new UserService(getInstance().getConnection());
        }
        return userService;
    }
    
    /**
     * Lấy EmployeeService
     * @return EmployeeService instance
     * @throws SQLException Nếu có lỗi với kết nối database
     */
    public static EmployeeService getEmployeeService() throws SQLException {
        if (employeeService == null) {
            employeeService = new EmployeeService(getInstance().getConnection());
        }
        return employeeService;
    }
    
    /**
     * Lấy CustomerService
     * @return CustomerService instance
     * @throws SQLException Nếu có lỗi với kết nối database
     */
    public static CustomerService getCustomerService() throws SQLException {
        if (customerService == null) {
            customerService = new CustomerService(getInstance().getConnection());
        }
        return customerService;
    }
    
    /**
     * Lấy InvoiceDetailService
     * @return InvoiceDetailService instance
     * @throws SQLException Nếu có lỗi với kết nối database
     */
    public static InvoiceDetailService getInvoiceDetailService() throws SQLException {
        if (invoiceDetailService == null) {
            RepositoryFactory repoFactory = RepositoryFactory.getInstance(getInstance().getConnection());
            invoiceDetailService = new InvoiceDetailService(
                    repoFactory.getInvoiceDetailRepository(), 
                    repoFactory.getProductRepository()
            );
        }
        return invoiceDetailService;
    }
    
    /*
     * Lấy CategoryService
     * @return CategoryService instance
     * @throws SQLException Nếu có lỗi với kết nối database
     */
    public static CategoryService getCategoryService() throws SQLException {
        if (categoryService == null) {
            categoryService = new CategoryService(getInstance().getConnection());
        }
        return categoryService;
    }

    /**
     * Lấy ProductService
     * @return ProductService instance
     * @throws SQLException Nếu có lỗi với kết nối database
     */
    public static ProductService getProductService() throws SQLException {
        if (productService == null) {
            productService = new ProductService(getInstance().getConnection());
        }
        return productService;
    }
    
    /**
     * Lấy SupplierService
     * @return SupplierService instance
     * @throws SQLException Nếu có lỗi với kết nối database
     */
    public static SupplierService getSupplierService() throws SQLException {
        if (supplierService == null) {
            supplierService = new SupplierService(getInstance().getConnection());
        }
        return supplierService;
    }
    
    /**
     * Lấy InvoiceService
     * @return InvoiceService instance
     * @throws SQLException Nếu có lỗi với kết nối database
     */
    public static InvoiceService getInvoiceService() throws SQLException {
        if (invoiceService == null) {
            invoiceService = new InvoiceService(getInstance().getConnection());
        }
        return invoiceService;
    }
    

    /**
     * Lấy PurchaseOrderService
     * @return PurchaseOrderService instance
     * @throws SQLException Nếu có lỗi với kết nối database
     */
    public static PurchaseOrderService getPurchaseOrderService() throws SQLException {
        if (purchaseOrderService == null) {
            purchaseOrderService = new PurchaseOrderService(getInstance().getConnection(), repositoryFactory);
        }
        return purchaseOrderService;
    }

    /*
     * Lấy PurchaseOrderDetailService
     * @return PurchaseOrderDetailService instance
     */
    public static PurchaseOrderDetailService getPurchaseOrderDetailService() throws SQLException {
        if (purchaseOrderDetailService == null) {
            purchaseOrderDetailService = new PurchaseOrderDetailService(getInstance().getConnection());
        }
        return purchaseOrderDetailService; 
    }
    
    /**
     * Lấy RepairServiceService
     * @return RepairServiceService instance
     * @throws SQLException Nếu có lỗi với kết nối database
     */
    public static RepairService getRepairServiceService() throws SQLException {
        if (repairServiceService == null) {
            repairServiceService = new RepairService(getInstance().getConnection());
        }
        return repairServiceService;
    }
    
    /**
     * Lấy WarrantyService
     * @return WarrantyService instance
     * @throws SQLException Nếu có lỗi với kết nối database
     */
    public static WarrantyService getWarrantyService() throws SQLException {
        if (warrantyService == null) {
            warrantyService = new WarrantyService(getInstance().getConnection());
        }
        return warrantyService;
    }
    
    /**
     * Lấy ReturnService
     * @return ReturnService instance
     * @throws SQLException Nếu có lỗi với kết nối database
     */
    public static ReturnService getReturnService() throws SQLException {
        if (returnService == null) {
            returnService = new ReturnService(getInstance().getConnection());
        }
        return returnService;
    }

    /**
     * Lấy RevenueService
     * @return RevenueService instance
     * @throws SQLException Nếu có lỗi với kết nối database
     */
    public static RevenueService getRevenueService() throws SQLException {
        if (revenueService == null) {
            revenueService = new RevenueService(getInstance().getConnection());
        }
        return revenueService;
    }
    
    /*
     * Lấy InventoryCheckService
     * @return InventoryCheckService instance
     * @throws SQLException Nếu có lỗi với kết nối database
     */
    public static InventoryCheckService getInventoryCheckService() throws SQLException {
        if (inventoryCheckService == null) {
            inventoryCheckService = new InventoryCheckService(getInstance().getConnection());
        }
        return inventoryCheckService;
    }

    /**
     * Đóng kết nối đến cơ sở dữ liệu
     * @throws SQLException Nếu có lỗi khi đóng kết nối
     */
    public static void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            
            // Reset các services
            userService = null;
            employeeService = null;
            customerService = null;
            productService = null;
            supplierService = null;
            invoiceService = null;
            purchaseOrderService = null;
            repairServiceService = null;
            warrantyService = null;
            returnService = null;
            revenueService = null;
            invoiceDetailService = null;
            purchaseOrderDetailService = null;
            categoryService = null;
            repositoryFactory = null;
            instance = null;
        }
    }
    
    /**
     * Lấy kết nối đến cơ sở dữ liệu
     * @return Connection đến cơ sở dữ liệu
     */
    public Connection getConnection() {
        // Kiểm tra xem kết nối đã được khởi tạo chưa
        if (connection == null) {
            try {
                connection = DatabaseConnection.getInstance().getConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}