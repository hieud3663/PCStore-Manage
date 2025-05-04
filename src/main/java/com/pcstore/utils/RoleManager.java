package com.pcstore.utils;

import com.pcstore.model.User;
import com.pcstore.model.enums.Roles;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Lớp quản lý phân quyền người dùng tập trung
 */
public class RoleManager {
    // Singleton instance
    private static RoleManager instance;
    
    // Thông tin quyền của từng chức năng
    private Map<String, Set<String>> featurePermissions;
    
    /**
     * Các tính năng của hệ thống
     */
    public static final String FEATURE_HOME = "home";
    public static final String FEATURE_SELL = "sell";
    public static final String FEATURE_PRODUCT = "product";
    public static final String FEATURE_INVOICE = "invoice";
    public static final String FEATURE_WAREHOUSE = "warehouse";
    public static final String FEATURE_REPORT = "report";
    public static final String FEATURE_CUSTOMER = "customer";
    public static final String FEATURE_SERVICE = "service";
    public static final String FEATURE_EMPLOYEE = "employee";
    
    /**
     * Các hành động trong hệ thống
     */
    public static final String ACTION_VIEW = "view";
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_EDIT = "edit";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_EXPORT = "export";
    
    private RoleManager() {
        initializePermissions();
    }
    
    public static synchronized RoleManager getInstance() {
        if (instance == null) {
            instance = new RoleManager();
        }
        return instance;
    }
    
    /**
     * Khởi tạo quyền cho các chức năng
     */
    private void initializePermissions() {
        featurePermissions = new HashMap<>();
        
        // Trang chủ - Tất cả vai trò đều có thể xem
        Set<String> homePermissions = new HashSet<>();
        homePermissions.add(Roles.ADMIN.getRoleName());
        homePermissions.add(Roles.MANAGER.getRoleName());
        homePermissions.add(Roles.SALES.getRoleName());
        homePermissions.add(Roles.STOCK.getRoleName());
        homePermissions.add(Roles.REPAIR.getRoleName());
        featurePermissions.put(FEATURE_HOME, homePermissions);
        
        // Bán hàng - Admin, Manager và Sales
        Set<String> sellPermissions = new HashSet<>();
        sellPermissions.add(Roles.ADMIN.getRoleName());
        sellPermissions.add(Roles.MANAGER.getRoleName());
        sellPermissions.add(Roles.SALES.getRoleName());
        featurePermissions.put(FEATURE_SELL, sellPermissions);
        
        // Sản phẩm - Xem: Tất cả; Thêm/Sửa/Xóa: Admin, Manager
        Set<String> viewProductPermissions = new HashSet<>();
        viewProductPermissions.add(Roles.ADMIN.getRoleName());
        viewProductPermissions.add(Roles.MANAGER.getRoleName());
        viewProductPermissions.add(Roles.SALES.getRoleName());
        viewProductPermissions.add(Roles.STOCK.getRoleName());
        viewProductPermissions.add(Roles.REPAIR.getRoleName());
        
        Set<String> manageProductPermissions = new HashSet<>();
        manageProductPermissions.add(Roles.ADMIN.getRoleName());
        manageProductPermissions.add(Roles.MANAGER.getRoleName());
        manageProductPermissions.add(Roles.STOCK.getRoleName());
        
        featurePermissions.put(FEATURE_PRODUCT, viewProductPermissions);
        featurePermissions.put(FEATURE_PRODUCT + "_" + ACTION_CREATE, manageProductPermissions);
        featurePermissions.put(FEATURE_PRODUCT + "_" + ACTION_EDIT, manageProductPermissions);
        featurePermissions.put(FEATURE_PRODUCT + "_" + ACTION_DELETE, manageProductPermissions);
        
        // Hóa đơn - Admin, Manager, Sales
        Set<String> invoicePermissions = new HashSet<>();
        invoicePermissions.add(Roles.ADMIN.getRoleName());
        invoicePermissions.add(Roles.MANAGER.getRoleName());
        invoicePermissions.add(Roles.SALES.getRoleName());
        featurePermissions.put(FEATURE_INVOICE, invoicePermissions);
        
        // Kho hàng - Admin, Manager, Stock
        Set<String> warehousePermissions = new HashSet<>();
        warehousePermissions.add(Roles.ADMIN.getRoleName());
        warehousePermissions.add(Roles.MANAGER.getRoleName());
        warehousePermissions.add(Roles.STOCK.getRoleName());
        featurePermissions.put(FEATURE_WAREHOUSE, warehousePermissions);
        
        // Báo cáo - Admin, Manager
        Set<String> reportPermissions = new HashSet<>();
        reportPermissions.add(Roles.ADMIN.getRoleName());
        reportPermissions.add(Roles.MANAGER.getRoleName());
        featurePermissions.put(FEATURE_REPORT, reportPermissions);
        
        // Khách hàng - Xem: Tất cả; Thêm/Sửa/Xóa: Admin, Manager, Sales
        Set<String> viewCustomerPermissions = new HashSet<>();
        viewCustomerPermissions.add(Roles.ADMIN.getRoleName());
        viewCustomerPermissions.add(Roles.MANAGER.getRoleName());
        viewCustomerPermissions.add(Roles.SALES.getRoleName());
        viewCustomerPermissions.add(Roles.REPAIR.getRoleName());
        
        Set<String> manageCustomerPermissions = new HashSet<>();
        manageCustomerPermissions.add(Roles.ADMIN.getRoleName());
        manageCustomerPermissions.add(Roles.MANAGER.getRoleName());
        manageCustomerPermissions.add(Roles.SALES.getRoleName());
        
        featurePermissions.put(FEATURE_CUSTOMER, viewCustomerPermissions);
        featurePermissions.put(FEATURE_CUSTOMER + "_" + ACTION_CREATE, manageCustomerPermissions);
        featurePermissions.put(FEATURE_CUSTOMER + "_" + ACTION_EDIT, manageCustomerPermissions);
        featurePermissions.put(FEATURE_CUSTOMER + "_" + ACTION_DELETE, manageCustomerPermissions);
        
        // Dịch vụ - Admin, Manager, Repair
        Set<String> servicePermissions = new HashSet<>();
        servicePermissions.add(Roles.ADMIN.getRoleName());
        servicePermissions.add(Roles.MANAGER.getRoleName());
        servicePermissions.add(Roles.REPAIR.getRoleName());
        featurePermissions.put(FEATURE_SERVICE, servicePermissions);
        
        // Nhân viên - Ai cũng được xem, chỉ Admin và Manager có quyền thêm/sửa/xóa
        Set<String> employeePermissions = new HashSet<>();
        employeePermissions.add(Roles.ADMIN.getRoleName());
        employeePermissions.add(Roles.MANAGER.getRoleName());
        employeePermissions.add(Roles.STOCK.getRoleName());
        employeePermissions.add(Roles.REPAIR.getRoleName());
        employeePermissions.add(Roles.SALES.getRoleName());
        
        featurePermissions.put(FEATURE_EMPLOYEE, employeePermissions);
    }
    
    /**
     * Kiểm tra quyền truy cập tính năng
     * @param feature Tính năng cần kiểm tra
     * @return true nếu có quyền, false nếu không
     */
    public boolean hasFeaturePermission(String feature) {
        String roleName = getCurrentUserRole();
        if (roleName == null) {
            return false;
        }
        
        Set<String> allowedRoles = featurePermissions.get(feature);
        return allowedRoles != null && allowedRoles.contains(roleName);
    }
    
    /**
     * Kiểm tra quyền thực hiện hành động trên tính năng
     * @param feature Tính năng cần kiểm tra
     * @param action Hành động cần kiểm tra
     * @return true nếu có quyền, false nếu không
     */
    public boolean hasActionPermission(String feature, String action) {
        String roleName = getCurrentUserRole();
        if (roleName == null) {
            return false;
        }
        
        String permissionKey = feature + "_" + action;
        Set<String> allowedRoles = featurePermissions.get(permissionKey);
        
        // Nếu không có cấu hình riêng cho hành động, kiểm tra quyền truy cập tính năng
        if (allowedRoles == null) {
            return hasFeaturePermission(feature);
        }
        
        return allowedRoles.contains(roleName);
    }
    
    /**
     * Lấy vai trò của người dùng hiện tại
     * @return Tên vai trò hoặc null nếu chưa đăng nhập
     */
    private String getCurrentUserRole() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return currentUser.getRoleName();
    }
}