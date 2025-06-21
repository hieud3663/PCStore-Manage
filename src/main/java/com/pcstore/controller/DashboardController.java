package com.pcstore.controller;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.k33ptoo.components.KButton;
import com.k33ptoo.components.KGradientPanel;
import com.pcstore.components.menu.MenuAction;
import com.pcstore.components.menu.MenuEvent;
import com.pcstore.components.menu.MenuItem;
import com.pcstore.model.User;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.RoleManager;
import com.pcstore.utils.SessionManager;
import com.pcstore.view.DashboardForm;
import com.pcstore.view.LoginForm;

import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

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
    private boolean isMenuExpanded = true; // Thêm biến để theo dõi trạng thái
    private List<String[]> menuItemsStr; // menu
    private List<String[]> menuItemsKey; // menuItemKey
    private Properties bundle = LocaleManager.getInstance().getProperties();
    private boolean isUpdatingUI = false; // Biến để tránh vòng lặp vô hạn khi cập nhật UI


    /**
     * Map chứa mapping giữa menu key và icon path
     */
    private static final Map<String, String> MENU_ICON_MAP = new HashMap<>();

    static {
        MENU_ICON_MAP.put("menu.home", "com/pcstore/resources/icon/home.svg");
        MENU_ICON_MAP.put("menu.sell", "com/pcstore/resources/icon/sell.svg");
        MENU_ICON_MAP.put("menu.product", "com/pcstore/resources/icon/product.svg");
        MENU_ICON_MAP.put("menu.warehouse", "com/pcstore/resources/icon/warehouse.svg");
        MENU_ICON_MAP.put("menu.employee", "com/pcstore/resources/icon/employee.svg");
        MENU_ICON_MAP.put("menu.service", "com/pcstore/resources/icon/service.svg");
        MENU_ICON_MAP.put("menu.customer", "com/pcstore/resources/icon/customer.svg");
        MENU_ICON_MAP.put("menu.report.overview", "com/pcstore/resources/icon/report.svg");
    }

    /**
     * Khởi tạo controller cho Dashboard
     *
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
                    ErrorMessage.LOGIN_REQUIRED.toString(),
                    ErrorMessage.LOGIN_REQUIRED_TITLE.toString(),
                    JOptionPane.WARNING_MESSAGE);
            redirectToLogin();
            return;
        }

        configureUIBasedOnPermissions();
        handleLogout();

        // Khởi tạo đồng hồ
        initializeClockTimer();

        // Khởi tạo language selector
        initializeLanguageSelector();

        setSelectedMenu(0, 0);


    }

    private void setSelectedMenu(int index, int subIndex) {
        dashboardForm.getMenu().setSelectedMenu(index, subIndex);
    }


    /**
     * Thiết lập hiển thị/ẩn các menu dựa trên quyền của người dùng
     */
    private void configureMenuPermissions() {
        com.pcstore.components.menu.Menu menu = dashboardForm.getMenu();

        menuItemsStr = new ArrayList<>();
        menuItemsKey = new ArrayList<>();
        if (roleManager.hasFeaturePermission(RoleManager.FEATURE_HOME)) {
            menuItemsStr.add(new String[]{bundle.getProperty("menu.home")});
            menuItemsKey.add(new String[]{"menu.home"});
        }

        if (roleManager.hasFeaturePermission(RoleManager.FEATURE_SELL) ||
                roleManager.hasFeaturePermission(RoleManager.FEATURE_PRODUCT) ||
                roleManager.hasFeaturePermission(RoleManager.FEATURE_INVOICE)) {

            if (roleManager.hasFeaturePermission(RoleManager.FEATURE_SELL)) {
                menuItemsStr.add(new String[]{
                        bundle.getProperty("menu.sell"),
                        bundle.getProperty("menu.sell.pos"),
                        bundle.getProperty("menu.sell.invoice")
                });
                menuItemsKey.add(new String[]{"menu.sell", "menu.sell.pos", "menu.sell.invoice"});

            }

            if (roleManager.hasFeaturePermission(RoleManager.FEATURE_PRODUCT)) {
                menuItemsStr.add(new String[]{
                        bundle.getProperty("menu.product"),
                        bundle.getProperty("menu.product.list"),
                        bundle.getProperty("menu.product.category")
                });
                menuItemsKey.add(new String[]{"menu.product", "menu.product.list", "menu.product.category"});
            }
        }

        if (roleManager.hasFeaturePermission(RoleManager.FEATURE_WAREHOUSE) ||
                roleManager.hasFeaturePermission(RoleManager.FEATURE_EMPLOYEE) ||
                roleManager.hasFeaturePermission(RoleManager.FEATURE_CUSTOMER) ||
                roleManager.hasFeaturePermission(RoleManager.FEATURE_SERVICE)) {

            menuItemsStr.add(new String[]{bundle.getProperty("menu.management")});

            if (roleManager.hasFeaturePermission(RoleManager.FEATURE_WAREHOUSE)) {
                menuItemsStr.add(new String[]{
                        bundle.getProperty("menu.warehouse"),
                        bundle.getProperty("menu.warehouse.stock"),
                        // bundle.getProperty("menu.warehouse.import"),
                        bundle.getProperty("menu.warehouse.inventory")
                });
                menuItemsKey.add(new String[]{"menu.warehouse", "menu.warehouse.stock", "menu.warehouse.inventory"});
            }

            if (roleManager.hasFeaturePermission(RoleManager.FEATURE_EMPLOYEE)) {
                menuItemsStr.add(new String[]{
                        bundle.getProperty("menu.employee"),
                        bundle.getProperty("menu.employee.list"),
                        bundle.getProperty("menu.employee.users")
                });
                menuItemsKey.add(new String[]{"menu.employee", "menu.employee.list", "menu.employee.users"});
            }

            if (roleManager.hasFeaturePermission(RoleManager.FEATURE_CUSTOMER)) {
                menuItemsStr.add(new String[]{bundle.getProperty("menu.customer")});
                menuItemsKey.add(new String[]{"menu.customer"});
            }

            if (roleManager.hasFeaturePermission(RoleManager.FEATURE_SERVICE)) {
                menuItemsStr.add(new String[]{
                        bundle.getProperty("menu.service"),
                        bundle.getProperty("menu.service.warranty"),
                        bundle.getProperty("menu.service.repair"),
                        bundle.getProperty("menu.service.return")
                });
                menuItemsKey.add(new String[]{"menu.service", "menu.service.warranty", "menu.service.repair", "menu.service.return"});
            }
        }

        if (roleManager.hasFeaturePermission(RoleManager.FEATURE_REPORT)) {
            menuItemsStr.add(new String[]{bundle.getProperty("menu.report")});
            menuItemsStr.add(new String[]{
                    bundle.getProperty("menu.report.overview"),
                    bundle.getProperty("menu.report.revenue"),
                    bundle.getProperty("menu.report.product"),
                    bundle.getProperty("menu.report.customer")
            });
            menuItemsKey.add(new String[]{"menu.report", "menu.report.overview", "menu.report.revenue", "menu.report.product", "menu.report.customer"});
        }

        String[][] menuArray = menuItemsStr.toArray(new String[menuItemsStr.size()][]);

        menu.setMenuItemsStr(menuArray);

        menu.addMenuEvent(new MenuEvent() {
            @Override
            public void menuSelected(int index, int subIndex, MenuAction action) {
//                handleMenuSelection(index, subIndex);
            }
        });

        // Initial state - menu is expanded
        menu.setMenuFull(isMenuExpanded);

        configureMenuIcons();
    }


    private FlatSVGIcon createMenuIcon(String iconPath) {
        return new FlatSVGIcon(iconPath, 24, 24);
    }

    /**
     * Thiết lập icon cho các menu item - Cách tiếp cận mới
     */
    private void configureMenuIcons() {
        com.pcstore.components.menu.Menu menu = dashboardForm.getMenu();
        ResourceBundle rb = LocaleManager.getInstance().getResourceBundle();

        for (Map.Entry<String, String> entry : MENU_ICON_MAP.entrySet()) {
            String menuKey = entry.getKey();
            String iconPath = entry.getValue();

            try {
                String expectedText = rb.getString(menuKey);
                KButton targetButton = findButtonByText(menu, expectedText);

                if (targetButton != null) {
                    targetButton.setIcon(createMenuIcon(iconPath));
                    // System.out.println("✅ Set icon for: " + expectedText + " -> " + iconPath);
                } else {
//                    System.out.println("❌ Button not found for: " + expectedText);
                }
            } catch (Exception e) {
                System.out.println("⚠️ Error setting icon for key: " + menuKey + " - " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Tìm KButton theo text trong Menu
     */
    private KButton findButtonByText(com.pcstore.components.menu.Menu menu, String text) {
        return findButtonInContainer(menu.getPanelMenu(), text);
    }

    /**
     * Tìm KButton trong container theo text (recursive)
     */
    private KButton findButtonInContainer(Container container, String text) {
        for (Component component : container.getComponents()) {
            if (component instanceof com.pcstore.components.menu.MenuItem) {
                com.pcstore.components.menu.MenuItem menuItem = (MenuItem) component;
                KButton mainButton = menuItem.getMainMenu();

                if (text.equals(mainButton.getText())) {
                    return mainButton;
                }
            }

            if (component instanceof Container) {
                KButton found = findButtonInContainer((Container) component, text);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }


    /**
     * Thu gọn menu sidebar
     */
    public void collapseMenu() {
        dashboardForm.getPanelMenu().setPreferredSize(new java.awt.Dimension(80, 680));
        dashboardForm.getMenu().setMenuFull(false);
        dashboardForm.getLbMenu().setIcon(new ImageIcon(getClass().getResource("/com/pcstore/resources/icon/icons8_menu_48px_1.png")));
    }

    /**
     * Mở rộng menu sidebar
     */
    public void expandMenu() {
        dashboardForm.getPanelMenu().setPreferredSize(new java.awt.Dimension(220, 680));
        dashboardForm.getMenu().setMenuFull(true);
        dashboardForm.getLbMenu().setIcon(new ImageIcon(getClass().getResource("/com/pcstore/resources/icon/x-button.png")));
    }


    /**
     * Khởi tạo đồng hồ thời gian thực với định dạng dựa trên ngôn ngữ hiện tại
     */
    private void initializeClockTimer() {
        localeManager = LocaleManager.getInstance();

        timeFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", localeManager.getCurrentLocale());

        updateClock();

        clockTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClock();
            }
        });

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
     * Xử lý sự kiện logout khi nhấn btnSignOut
     */
    public void handleLogout() {
        for (ActionListener al : dashboardForm.getBtnSignOut().getActionListeners()) {
            dashboardForm.getBtnSignOut().removeActionListener(al);
        }

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
                ErrorMessage.LOGOUT_CONFIRM.toString(),
                ErrorMessage.LOGOUT_TITLE.toString(),
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Dừng timer đồng hồ
            cleanup();

            sessionManager.logout();

            dashboardForm.dispose();
            dashboardForm.resetInstance();

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
                    String.format(ErrorMessage.LOGIN_REDIRECT_ERROR.toString(), e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(),
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
        updateLanguageComboBox();

        dashboardForm.getCbLanguage().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = dashboardForm.getCbLanguage().getSelectedIndex();

                if (isUpdatingUI) return;

                switch (selectedIndex) {
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
     *
     * @param locale Locale mới
     */
    public void changeLanguage(Locale locale) {
        // Kiểm tra xem ngôn ngữ có thực sự thay đổi không
        String currentLanguage = localeManager.getCurrentLanguage();
        String newLanguage = locale.getLanguage();

        if (!currentLanguage.equals(newLanguage)) {
            // Hiển thị hộp thoại xác nhận khởi động lại
            String message = currentLanguage.equals("vi")
                    ? ErrorMessage.LANGUAGE_CHANGE_CONFIRM_VI.toString()
                    : ErrorMessage.LANGUAGE_CHANGE_CONFIRM_EN.toString();

            String title = currentLanguage.equals("vi")
                    ? ErrorMessage.LANGUAGE_CHANGE_TITLE_VI.toString()
                    : ErrorMessage.LANGUAGE_CHANGE_TITLE_EN.toString();

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
        dashboardForm.setTitle(bundle.getProperty("titleApp"));

        dashboardForm.getBtnSignOut().setText(bundle.getProperty("btnSignOut"));

    }

    public List<String[]> getMenuItemsStr() {
        return menuItemsStr == null ? new ArrayList<>() : menuItemsStr;
    }

    public List<String[]> getMenuItemsKey() {
        return menuItemsKey == null ? new ArrayList<>() : menuItemsKey;
    }

}