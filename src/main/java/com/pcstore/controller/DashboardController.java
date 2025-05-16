package com.pcstore.controller;

import com.k33ptoo.components.KGradientPanel;
import com.pcstore.model.User;
import com.pcstore.utils.SessionManager;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.RoleManager;
import com.pcstore.view.DashboardForm;
import com.pcstore.view.LoginForm;

import test.Main;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Controller điều khiển màn hình Dashboard và phân quyền người dùng
 */
public class DashboardController {
    private DashboardForm dashboardForm;
    private SessionManager sessionManager;
    private Timer clockTimer;
    private SimpleDateFormat timeFormat;
    private LocaleManager localeManager;
    private RoleManager roleManager;
    private boolean isMenuExpanded = true; // Thêm biến để theo dõi trạng thái menu
    private Properties bundle = LocaleManager.getInstance().getProperties();
    private boolean isUpdatingUI = false; // Biến để tránh vòng lặp vô hạn khi cập nhật UI

    /**
     * Khởi tạo controller cho Dashboard
     * @param dashboardForm View dashboard cần điều khiển
     */
    public DashboardController(DashboardForm dashboardForm) {
        this.dashboardForm = dashboardForm;
        this.sessionManager = SessionManager.getInstance();
        this.roleManager = RoleManager.getInstance();
        this.localeManager = LocaleManager.getInstance();
        
        // Kiểm tra người dùng đã đăng nhập chưa
        if (!sessionManager.isLoggedIn()) {
            JOptionPane.showMessageDialog(dashboardForm, 
                ErrorMessage.LOGIN_REQUIRED, 
                ErrorMessage.LOGIN_REQUIRED_TITLE, 
                JOptionPane.WARNING_MESSAGE);
            redirectToLogin();
            return;
        }
        
        configureUIBasedOnPermissions();
        handleLogout();
        initializeMenuToggle();
        
        // Khởi tạo đồng hồ
        initializeClockTimer();
        
        // Khởi tạo language selector
        initializeLanguageSelector();
    }
    
    /**
     * Khởi tạo xử lý sự kiện khi nhấn vào nút menu để thu gọn/mở rộng sidebar
     */
    private void initializeMenuToggle() {
        dashboardForm.getLbMenu().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toggleMenuSize();
            }
        });
    }
    
    /**
     * Thay đổi kích thước của menu sidebar
     */
    public void toggleMenuSize() {
        isMenuExpanded = !isMenuExpanded;
        
        if (isMenuExpanded) {
            // Mở rộng menu
            expandMenu();
        } else {
            // Thu gọn menu
            collapseMenu();
        }
        
        dashboardForm.getPanelMenu().revalidate();
        dashboardForm.getPanelMenu().repaint();
    }
    
    /**
     * Thu gọn menu sidebar
     */
    private void collapseMenu() {
        // Đặt kích thước mới cho sidebar
        dashboardForm.getPanelMenu().setPreferredSize(new java.awt.Dimension(70, 680));
        
        hideMenuText(dashboardForm.getkPanelHome(), dashboardForm.getLbMenuHome());
        hideMenuText(dashboardForm.getkPanelSell(), dashboardForm.getLbSell());
        hideMenuText(dashboardForm.getkPanelProduct(), dashboardForm.getLbProductMenu());
        hideMenuText(dashboardForm.getkPanelInvoice(), dashboardForm.getLbMenuInvoice());
        hideMenuText(dashboardForm.getkPanelWareHouse(), dashboardForm.getLbMenuWareHouse());
        hideMenuText(dashboardForm.getkPanelEmployee(), dashboardForm.getLbMenuEmployee());
        hideMenuText(dashboardForm.getkPanelService(), dashboardForm.getLbMenuService());
        hideMenuText(dashboardForm.getkPanelCustomer(), dashboardForm.getLbMenuCustomer());
        hideMenuText(dashboardForm.getkPanelReport(), dashboardForm.getLbMenuReport());
        
        dashboardForm.getLbMenu().setIcon(new ImageIcon(getClass().getResource("/com/pcstore/resources/icon/icons8_menu_48px_1.png")));
    }
    
    /**
     * Mở rộng menu sidebar
     */
    private void expandMenu() {
        // Đặt lại kích thước ban đầu cho sidebar
        dashboardForm.getPanelMenu().setPreferredSize(new java.awt.Dimension(220, 680));
        
        // Hiển thị lại text trên tất cả các menu sử dụng bundle
        showMenuText(dashboardForm.getkPanelHome(), dashboardForm.getLbMenuHome(), bundle.getProperty("txtMenuHome"));
        showMenuText(dashboardForm.getkPanelSell(), dashboardForm.getLbSell(), bundle.getProperty("txtMenuSell"));
        showMenuText(dashboardForm.getkPanelProduct(), dashboardForm.getLbProductMenu(), bundle.getProperty("txtMenuProduct"));
        showMenuText(dashboardForm.getkPanelInvoice(), dashboardForm.getLbMenuInvoice(), bundle.getProperty("txtMenuInvoice"));
        showMenuText(dashboardForm.getkPanelWareHouse(), dashboardForm.getLbMenuWareHouse(), bundle.getProperty("txtMenuWareHouse"));
        showMenuText(dashboardForm.getkPanelEmployee(), dashboardForm.getLbMenuEmployee(), bundle.getProperty("txtMenuIEmployee"));
        showMenuText(dashboardForm.getkPanelService(), dashboardForm.getLbMenuService(), bundle.getProperty("txtMenuIService"));
        showMenuText(dashboardForm.getkPanelCustomer(), dashboardForm.getLbMenuCustomer(), bundle.getProperty("lbMenuCustomer"));
        showMenuText(dashboardForm.getkPanelReport(), dashboardForm.getLbMenuReport(), bundle.getProperty("txtMenuIReport"));
        
        dashboardForm.getLbMenu().setIcon(new ImageIcon(getClass().getResource("/com/pcstore/resources/icon/x-button.png")));
    }

    
    /**
     * Ẩn text của menu, chỉ hiển thị icon
     */
    private void hideMenuText(KGradientPanel panel, JLabel label) {
        if (label != null) {
            // Lưu lại text gốc để khôi phục sau này
            label.putClientProperty("original-text", label.getText());
            
            // Xóa text, chỉ để lại icon
            label.setText("");
            
            // Căn giữa icon
            label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            
            // Thay đổi kích thước label
            label.setPreferredSize(new java.awt.Dimension(50, 30));
            
            // Thay đổi flow layout để căn giữa
            panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));
        }
    }
    
    /**
     * Cập nhật text cho một label menu
     * @param label Label cần cập nhật
     * @param text Text mới từ ResourceBundle
     */
    private void updateMenuText(JLabel label, String text) {
        if (label != null && isMenuExpanded) {
            label.setText(text);
            // Lưu lại text này để sử dụng khi expand menu
            label.putClientProperty("original-text", text);
        }
    }

    /**
     * Hiển thị lại text của menu
     */
    private void showMenuText(KGradientPanel panel, JLabel label, String defaultText) {
        if (label != null) {
            String originalText = (String) label.getClientProperty("original-text");
            if (originalText != null && !originalText.isEmpty()) {
                label.setText(originalText);
            } else {
                label.setText(defaultText);
                label.putClientProperty("original-text", defaultText);
            }
            
            label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            
            label.setPreferredSize(new java.awt.Dimension(200, 30));
            
            panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 5));
        }
    }

    
    /**
     * Khởi tạo đồng hồ thời gian thực với định dạng dựa trên ngôn ngữ hiện tại
     */
    private void initializeClockTimer() {
        // Lấy instance của LocaleManager
        localeManager = LocaleManager.getInstance();
        
        // Khởi tạo định dạng thời gian dựa trên locale hiện tại
        timeFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", localeManager.getCurrentLocale());
        
        // Cập nhật thời gian ngay lập tức
        updateClock();
        
        // Khởi tạo timer để cập nhật thời gian mỗi giây
        clockTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClock();
            }
        });
        
        // Bắt đầu timer
        clockTimer.start();
    }
    
    /**
     * Cập nhật hiển thị thời gian hiện tại theo ngôn ngữ hiện hành
     */
    private void updateClock() {
        // Lấy thời gian hiện tại
        Date now = new Date();
        String timeString;
        
        // Định dạng thời gian dựa trên ngôn ngữ hiện tại
        String language = localeManager.getCurrentLanguage();
        if (language.equals("vi")) {
            // Định dạng cho tiếng Việt (ví dụ: "15:30:45 Ngày 01-05-2025")
            SimpleDateFormat vietnameseFormat = new SimpleDateFormat("HH:mm:ss 'Ngày' dd-MM-yyyy", localeManager.getCurrentLocale());
            timeString = vietnameseFormat.format(now);
        } else {
            // Định dạng cho tiếng Anh (ví dụ: "3:30:45 PM, May 1, 2025")
            SimpleDateFormat englishFormat = new SimpleDateFormat("h:mm:ss a, MMM d, yyyy", Locale.US);
            timeString = englishFormat.format(now);
        }
        
        // Cập nhật label hiển thị thời gian
        final String finalTimeString = timeString;
        SwingUtilities.invokeLater(() -> {
            if (dashboardForm != null && dashboardForm.getLbTImeNow() != null) {
                dashboardForm.getLbTImeNow().setText(finalTimeString);
            }
        });
    }
    
    /**
     * Cập nhật lại text menu khi thay đổi ngôn ngữ
     */
    public void refreshLanguage() {
        // Cập nhật định dạng thời gian theo ngôn ngữ mới
        timeFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", localeManager.getCurrentLocale());
        
        // Cập nhật ngay lập tức
        updateClock();
        
        // Nếu menu đang ở trạng thái mở rộng, cập nhật lại text
        if (isMenuExpanded) {
            bundle = LocaleManager.getInstance().getProperties();
            
            // Cập nhật text cho tất cả menu
            updateMenuText(dashboardForm.getLbMenuHome(), bundle.getProperty("txtMenuHome"));
            updateMenuText(dashboardForm.getLbSell(), bundle.getProperty("txtMenuSell"));
            updateMenuText(dashboardForm.getLbProductMenu(), bundle.getProperty("txtMenuProduct"));
            updateMenuText(dashboardForm.getLbMenuInvoice(), bundle.getProperty("txtMenuInvoice"));
            updateMenuText(dashboardForm.getLbMenuWareHouse(), bundle.getProperty("txtMenuWareHouse"));
            updateMenuText(dashboardForm.getLbMenuEmployee(), bundle.getProperty("txtMenuIEmployee"));
            updateMenuText(dashboardForm.getLbMenuService(), bundle.getProperty("txtMenuIService"));
            updateMenuText(dashboardForm.getLbMenuCustomer(), bundle.getProperty("lbMenuCustomer"));
            updateMenuText(dashboardForm.getLbMenuReport(), bundle.getProperty("txtMenuIReport"));
        }
    }

    
    /**
     * Dừng timer đồng hồ khi đóng ứng dụng
     */
    public void cleanup() {
        if (clockTimer != null && clockTimer.isRunning()) {
            clockTimer.stop();
            clockTimer = null;
        }
    }
    
    // Phương thức finalize để đảm bảo dừng timer khi garbage collection
    @Override
    protected void finalize() throws Throwable {
        cleanup();
        super.finalize();
    }
    
    /**
     * Cấu hình hiển thị giao diện dựa trên quyền của người dùng
     */
    private void configureUIBasedOnPermissions() {
        User currentUser = sessionManager.getCurrentUser();
        dashboardForm.getLbNameUser().setText(currentUser.getFullName());
        
        configureMenuPermissions();
    }
    
    /**
     * Thiết lập hiển thị/ẩn các menu dựa trên quyền của người dùng
     */
    private void configureMenuPermissions() {
        dashboardForm.getPanelMenu().removeAll();
        
        List<KGradientPanel> visiblePanels = new ArrayList<>();
        
        // Kiểm tra và thêm panel có quyền vào danh sách hiển thị
        if (roleManager.hasFeaturePermission(RoleManager.FEATURE_HOME)) {
            KGradientPanel panel = dashboardForm.getkPanelHome();
            configurePanel(panel, true);
            visiblePanels.add(panel);
        }
        
        if (roleManager.hasFeaturePermission(RoleManager.FEATURE_SELL)) {
            KGradientPanel panel = dashboardForm.getkPanelSell();
            configurePanel(panel, true);
            visiblePanels.add(panel);
        }
        
        if (roleManager.hasFeaturePermission(RoleManager.FEATURE_PRODUCT)) {
            KGradientPanel panel = dashboardForm.getkPanelProduct();
            configurePanel(panel, true);
            visiblePanels.add(panel);
        }
        
        if (roleManager.hasFeaturePermission(RoleManager.FEATURE_INVOICE)) {
            KGradientPanel panel = dashboardForm.getkPanelInvoice();
            configurePanel(panel, true);
            visiblePanels.add(panel);
        }
        
        if (roleManager.hasFeaturePermission(RoleManager.FEATURE_WAREHOUSE)) {
            KGradientPanel panel = dashboardForm.getkPanelWareHouse();
            configurePanel(panel, true);
            visiblePanels.add(panel);
        }
        
        if (roleManager.hasFeaturePermission(RoleManager.FEATURE_REPORT)) {
            KGradientPanel panel = dashboardForm.getkPanelReport();
            configurePanel(panel, true);
            visiblePanels.add(panel);
        }
        
        if (roleManager.hasFeaturePermission(RoleManager.FEATURE_CUSTOMER)) {
            KGradientPanel panel = dashboardForm.getkPanelCustomer();
            configurePanel(panel, true);
            visiblePanels.add(panel);
        }
        
        if (roleManager.hasFeaturePermission(RoleManager.FEATURE_SERVICE)) {
            KGradientPanel panel = dashboardForm.getkPanelService();
            configurePanel(panel, true);
            visiblePanels.add(panel);
        }

        if (roleManager.hasFeaturePermission(RoleManager.FEATURE_EMPLOYEE)) {
            KGradientPanel panel = dashboardForm.getkPanelEmployee();
            configurePanel(panel, true);
            visiblePanels.add(panel);
        }
        
        // Lấy layout hiện tại từ container
        GridLayout layout = (GridLayout) dashboardForm.getPanelMenu().getLayout();
        int rows = layout.getRows();
        
        // Đảm bảo rằng menu logo được thêm trước (nếu có)
        if (dashboardForm.getLbMenu() != null) {
            dashboardForm.getPanelMenu().add(dashboardForm.getLbMenu());
        }
        
        // Thêm các panel có quyền vào container
        for (KGradientPanel panel : visiblePanels) {
            dashboardForm.getPanelMenu().add(panel);
        }
        
        // Nếu cần, thêm các panel trống để giữ layout
        int neededComponents = rows - visiblePanels.size() - (dashboardForm.getLbMenu() != null ? 1 : 0);
        for (int i = 0; i < neededComponents; i++) {
            // Tạo một panel trong suốt để điền vào các vị trí còn lại
            javax.swing.JPanel emptyPanel = new javax.swing.JPanel();
            emptyPanel.setOpaque(false);
            dashboardForm.getPanelMenu().add(emptyPanel);
        }
        
        // Cập nhật giao diện
        dashboardForm.getPanelMenu().revalidate();
        dashboardForm.getPanelMenu().repaint();
    }
    
    /**
     * Cấu hình giao diện cho panel
     * @param panel Panel cần cấu hình
     * @param show true để hiển thị, false để ẩn
     */
    private void configurePanel(KGradientPanel panel, boolean show) {
        panel.setVisible(show);
        panel.setEnabled(show);
        panel.setkFillBackground(!show);
        panel.setBackground(Color.DARK_GRAY);
        panel.putClientProperty("menu-disabled", !show);
        
        // Vô hiệu hóa/kích hoạt tất cả component con
        for (Component comp : panel.getComponents()) {
            comp.setEnabled(show);
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                label.setForeground(show ? Color.WHITE : Color.GRAY);
            }
        }
    }
    
    /**
     * Hiển thị hoặc ẩn một menu panel (phương thức giữ lại để tương thích ngược)
     * @param panel Panel cần hiển thị/ẩn
     * @param show true để hiển thị, false để ẩn
     */
    private void showPanel(KGradientPanel panel, boolean show) {
        configurePanel(panel, show);
    }
    
    /**
     * Xử lý sự kiện logout khi nhấn btnSignOut
     */
    public void handleLogout() {
        // Xóa tất cả ActionListener cũ để tránh duplicate
        for (ActionListener al : dashboardForm.getBtnSignOut().getActionListeners()) {
            dashboardForm.getBtnSignOut().removeActionListener(al);
        }
        
        // Thêm listener mới
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
            ErrorMessage.LOGOUT_CONFIRM,
            ErrorMessage.LOGOUT_TITLE,
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Dừng timer đồng hồ
            cleanup();
            
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
     */
    private void redirectToLogin() {
        try {
            LoginForm.getInstance().setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dashboardForm, 
                String.format(ErrorMessage.LOGIN_REDIRECT_ERROR, e.getMessage()), 
                ErrorMessage.ERROR_TITLE, 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Cập nhật lại phân quyền sau khi có thay đổi về vai trò người dùng
     */
    public void refreshPermissions() {
        if (sessionManager.isLoggedIn()) {
            configureUIBasedOnPermissions();
        }
    }
    
    /**
     * Khởi tạo xử lý sự kiện khi thay đổi ngôn ngữ từ ComboBox
     */
    private void initializeLanguageSelector() {
        // Thiết lập trạng thái ban đầu của ComboBox dựa trên ngôn ngữ hiện tại
        updateLanguageComboBox();
        
        // Đăng ký sự kiện thay đổi ngôn ngữ
        dashboardForm.getCbLanguage().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = dashboardForm.getCbLanguage().getSelectedIndex();
                
                // Tránh trigger lại khi đang cập nhật UI
                if (isUpdatingUI) return;
                
                switch(selectedIndex) {
                    case 0: // Tiếng Việt
                        changeLanguage(LocaleManager.LOCALE_VIETNAM);
                        break;
                    case 1: // Tiếng Anh
                        changeLanguage(LocaleManager.LOCALE_US);
                        break;
                }
            }
        });
    }

    /**
     * Cập nhật trạng thái ComboBox theo ngôn ngữ hiện tại
     */
    private void updateLanguageComboBox() {
        isUpdatingUI = true;
        try {
            String currentLanguage = localeManager.getCurrentLanguage();
            if (currentLanguage.equals("vi")) {
                dashboardForm.getCbLanguage().setSelectedIndex(0);
            } else {
                dashboardForm.getCbLanguage().setSelectedIndex(1);
            }
        } finally {
            isUpdatingUI = false;
        }
    }

    /**
     * Thay đổi ngôn ngữ của ứng dụng
     * @param locale Locale mới
     */
    public void changeLanguage(Locale locale) {
        // Kiểm tra xem ngôn ngữ có thực sự thay đổi không
        String currentLanguage = localeManager.getCurrentLanguage();
        String newLanguage = locale.getLanguage();
        
        if (!currentLanguage.equals(newLanguage)) {
            // Hiển thị hộp thoại xác nhận khởi động lại
            String message = currentLanguage.equals("vi") 
                ? ErrorMessage.LANGUAGE_CHANGE_CONFIRM_VI 
                : ErrorMessage.LANGUAGE_CHANGE_CONFIRM_EN;
                
            String title = currentLanguage.equals("vi") 
                ? ErrorMessage.LANGUAGE_CHANGE_TITLE_VI 
                : ErrorMessage.LANGUAGE_CHANGE_TITLE_EN;
                
            int response = JOptionPane.showConfirmDialog(
                dashboardForm,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (response == JOptionPane.YES_OPTION) {
                localeManager.setLocale(locale);
                
                // Dọn dẹp tài nguyên
                cleanup();
                
                // Khởi động lại ứng dụng
                SwingUtilities.invokeLater(() -> {
                    try {
                        dashboardForm.restartApp();
                    } catch (Exception ex) {
                        String errorMsg = currentLanguage.equals("vi") 
                            ? "Lỗi khởi động lại ứng dụng: " 
                            : "Error restarting application: ";
                        
                        JOptionPane.showMessageDialog(null,
                            errorMsg + ex.getMessage(),
                            currentLanguage.equals("vi") ? "Lỗi" : "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
            } else {
                // Người dùng đã hủy, đặt lại combobox về ngôn ngữ hiện tại
                updateLanguageComboBox();
            }
        } else {
            bundle = localeManager.getProperties();

            refreshLanguage();
            
            updateLanguageComboBox();
            
            updateUITexts();
        }
    }

    /**
     * Cập nhật các text cố định trên giao diện
     */
    private void updateUITexts() {
        // Cập nhật title của ứng dụng
        dashboardForm.setTitle(bundle.getProperty("titleApp"));
        
        // Cập nhật nút đăng xuất
        dashboardForm.getBtnSignOut().setText(bundle.getProperty("btnSignOut"));
        
        // Các text cố định khác...
    }
}