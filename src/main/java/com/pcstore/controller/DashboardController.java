package com.pcstore.controller;

import com.k33ptoo.components.KGradientPanel;
import com.pcstore.model.User;
import com.pcstore.util.SessionManager;
import com.pcstore.utils.SessionManager;
import com.pcstore.view.DashboardView;
import com.pcstore.view.LoginView;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller điều khiển màn hình Dashboard và phân quyền người dùng
 */
public class DashboardController {
    private DashboardView dashboardView;
    private SessionManager sessionManager;
    
    /**
     * Khởi tạo controller cho Dashboard
     * @param dashboardView View dashboard cần điều khiển
     */
    public DashboardController(DashboardView dashboardView) {
        this.dashboardView = dashboardView;
        this.sessionManager = SessionManager.getInstance();
        
        // Kiểm tra người dùng đã đăng nhập chưa
        if (!sessionManager.isLoggedIn()) {
            JOptionPane.showMessageDialog(dashboardView, 
                "Bạn cần đăng nhập để sử dụng hệ thống", 
                "Yêu cầu đăng nhập", 
                JOptionPane.WARNING_MESSAGE);
            redirectToLogin();
            return;
        }
        
        // Cấu hình giao diện dựa trên quyền của người dùng
        configureUIBasedOnRole();
        
        // Đăng ký xử lý sự kiện cho nút đăng xuất
        registerLogoutHandler();
    }
    
    /**
     * Cấu hình hiển thị giao diện dựa trên vai trò người dùng
     */
    private void configureUIBasedOnRole() {
        User currentUser = sessionManager.getCurrentUser();
        String role = currentUser.getRole();
        
        // Hiển thị thông tin người dùng đăng nhập
        dashboardView.getLbNameUser().setText(currentUser.getFullName());
        
        // Thiết lập hiển thị các menu dựa trên vai trò
        configureMenuVisibility(role);
    }
    
    /**
     * Thiết lập hiển thị/ẩn các menu dựa trên vai trò người dùng
     * @param role Vai trò của người dùng (ADMIN, MANAGER, SALESPERSON)
     */
    private void configureMenuVisibility(String role) {
        // Tất cả vai trò đều có thể xem trang chủ và bán hàng
        showPanel(dashboardView.getkPanelHome(), true);
        showPanel(dashboardView.getkPanelSell(), true);
        
        // Tất cả vai trò đều có thể xem sản phẩm, nhưng chỉ ADMIN và MANAGER có thể thêm/sửa/xóa
        showPanel(dashboardView.getkPanelProduct(), true);
        
        // Phân quyền cho menu hóa đơn
        showPanel(dashboardView.getkPanelInvoice(), true); // Tất cả đều có thể xem hóa đơn
        
        // ADMIN và MANAGER có quyền quản lý kho hàng
        boolean canManageWarehouse = role.equals("ADMIN") || role.equals("MANAGER");
        showPanel(dashboardView.getkPanelWareHouse(), canManageWarehouse);
        
        // Chỉ ADMIN mới được quản lý nhân viên
        showPanel(dashboardView.getkPanelEmployee(), role.equals("ADMIN"));
        
        // ADMIN và MANAGER có quyền xem báo cáo
        showPanel(dashboardView.getkPanelReport(), role.equals("ADMIN") || role.equals("MANAGER"));
        
        // Tất cả vai trò có thể xem khách hàng nhưng chỉ ADMIN và MANAGER có thể thêm/sửa/xóa
        showPanel(dashboardView.getkPanelCustomer(), true);
        
        // Tùy chỉnh quyền cho dịch vụ - giả sử chỉ ADMIN và MANAGER quản lý dịch vụ
        showPanel(dashboardView.getkPanelService(), role.equals("ADMIN") || role.equals("MANAGER"));
    }
    
    /**
     * Hiển thị hoặc ẩn một menu panel
     * @param panel Panel cần hiển thị/ẩn
     * @param show true để hiển thị, false để ẩn
     */
    private void showPanel(KGradientPanel panel, boolean show) {
        if (panel != null) {
            panel.setVisible(show);
        }
    }
    
    /**
     * Đăng ký xử lý sự kiện đăng xuất
     */
    private void registerLogoutHandler() {
        dashboardView.getBtnSignOut().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
    }
    
    /**
     * Xử lý đăng xuất
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            dashboardView,
            "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Xóa thông tin người dùng khỏi session
            sessionManager.logout();
            
            // Đóng cửa sổ dashboard
            dashboardView.dispose();
            
            // Mở form đăng nhập
            redirectToLogin();
        }
    }
    
    /**
     * Chuyển hướng về trang đăng nhập
     */
    private void redirectToLogin() {
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(loginView);
        loginView.setVisible(true);
    }
    
    /**
     * Cập nhật lại phân quyền sau khi có thay đổi về vai trò người dùng
     */
    public void refreshPermissions() {
        if (sessionManager.isLoggedIn()) {
            configureUIBasedOnRole();
        }
    }
}