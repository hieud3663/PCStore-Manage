package com.pcstore.controller;

import com.k33ptoo.components.KGradientPanel;
import com.pcstore.model.User;
import com.pcstore.utils.SessionManager;
import com.pcstore.view.DashboardForm;
import com.pcstore.view.LoginForm;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;

/**
 * Controller điều khiển màn hình Dashboard và phân quyền người dùng
 */
public class DashboardController {
    private DashboardForm dashboardForm;
    private SessionManager sessionManager;
    
    /**
     * Khởi tạo controller cho Dashboard
     * @param dashboardView View dashboard cần điều khiển
     */
    public DashboardController(DashboardForm dashboardView) {
        this.dashboardForm = dashboardView;
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

        // Xử lý sự kiện đăng xuất
        handleLogout();

    }
    
    /**
     * Cấu hình hiển thị giao diện dựa trên vai trò người dùng
     */
    private void configureUIBasedOnRole() {
        User currentUser = sessionManager.getCurrentUser();
        String role = currentUser.getRoleName();
        
        // Hiển thị thông tin người dùng đăng nhập
        dashboardForm.getLbNameUser().setText(currentUser.getFullName());
        
        // Thiết lập hiển thị các menu dựa trên vai trò
        configureMenuVisibility(role);
    }
    
    /**
     * Thiết lập hiển thị/ẩn các menu dựa trên vai trò người dùng
     * @param role Vai trò của người dùng (ADMIN, MANAGER, SALESPERSON)
     */
    private void configureMenuVisibility(String role) {


        //Nếu admin thì full quyền
        if (role.equals("Admin")) {
            showPanel(dashboardForm.getkPanelHome(), true);
            showPanel(dashboardForm.getkPanelSell(), true);
            showPanel(dashboardForm.getkPanelProduct(), true);
            showPanel(dashboardForm.getkPanelInvoice(), true);
            showPanel(dashboardForm.getkPanelWareHouse(), true);
            showPanel(dashboardForm.getkPanelReport(), true);
            showPanel(dashboardForm.getkPanelCustomer(), true);
            showPanel(dashboardForm.getkPanelService(), true);
            return;
        }else{
            // Nếu không phải admin thì ẩn các menu không cần thiết
            showPanel(dashboardForm.getkPanelHome(), false);
            showPanel(dashboardForm.getkPanelSell(), false);
            showPanel(dashboardForm.getkPanelProduct(), false);
            showPanel(dashboardForm.getkPanelInvoice(), false);
            showPanel(dashboardForm.getkPanelWareHouse(), false);
            showPanel(dashboardForm.getkPanelReport(), false);
            showPanel(dashboardForm.getkPanelCustomer(), false);
            showPanel(dashboardForm.getkPanelService(), false);
        }

        // Tất cả vai trò đều có thể xem trang chủ và bán hàng
        showPanel(dashboardForm.getkPanelHome(), true);
        showPanel(dashboardForm.getkPanelSell(), true);
        
        // Tất cả vai trò đều có thể xem sản phẩm, nhưng chỉ ADMIN và MANAGER có thể thêm/sửa/xóa
        showPanel(dashboardForm.getkPanelProduct(), true);
        
        // Phân quyền cho menu hóa đơn
        showPanel(dashboardForm.getkPanelInvoice(), true); // Tất cả đều có thể xem hóa đơn
        
        // ADMIN và MANAGER có quyền quản lý kho hàng
        boolean canManageWarehouse = role.equals("ADMIN") || role.equals("MANAGER");
        showPanel(dashboardForm.getkPanelWareHouse(), canManageWarehouse);
        
        // Chỉ ADMIN mới được quản lý nhân viên
        // showPanel(dashboardForm.getkPanelEmployee(), role.equals("ADMIN"));
        
        // ADMIN và MANAGER có quyền xem báo cáo
        showPanel(dashboardForm.getkPanelReport(), role.equals("ADMIN") || role.equals("MANAGER"));
        
        // Tất cả vai trò có thể xem khách hàng nhưng chỉ ADMIN và MANAGER có thể thêm/sửa/xóa
        showPanel(dashboardForm.getkPanelCustomer(), true);
        
        // Tùy chỉnh quyền cho dịch vụ - giả sử chỉ ADMIN và MANAGER quản lý dịch vụ
        showPanel(dashboardForm.getkPanelService(), role.equals("ADMIN") || role.equals("MANAGER"));
    }
    
    /**
     * Hiển thị hoặc ẩn một menu panel
     * @param panel Panel cần hiển thị/ẩn
     * @param show true để hiển thị, false để ẩn
     */
    private void showPanel(KGradientPanel panel, boolean show) {
        panel.setEnabled(show);
        panel.setkFillBackground(!show);
        panel.setBackground(Color.DARK_GRAY);
        panel.putClientProperty("menu-disabled", !show); 
        //xóa hành động ở label trong panel
        // Vô hiệu hóa/kích hoạt tất cả component con
        for (Component comp : panel.getComponents()) {
            comp.setEnabled(show);
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                label.setForeground(show ? Color.WHITE : Color.GRAY);
                // label.setOpaque(!show);
            }
        }
        
    }
    

    /**
     * Xử lý sự kiện logout khi nhấn btnSignOut
     */

    public void handleLogout() {
        dashboardForm.getBtnSignOut().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
    }
   
    /**
     * Xử lý đăng xuất
     */
    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            dashboardForm,
            "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Xóa thông tin người dùng khỏi session
            sessionManager.logout();
            
            // Đóng cửa sổ dashboard
            dashboardForm.dispose();
            dashboardForm.resetInstance();

            // Mở form đăng nhập
            redirectToLogin();
        }
    }
    
    /**
     * Chuyển hướng về trang đăng nhập
     * @throws SQLException 
     */
    private void redirectToLogin() {
        try {
            LoginForm.getInstance().setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dashboardForm, 
                "Đã xảy ra lỗi khi mở trang đăng nhập: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
        
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