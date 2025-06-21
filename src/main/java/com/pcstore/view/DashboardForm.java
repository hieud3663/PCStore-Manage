/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.pcstore.view;


import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.k33ptoo.components.KButton;
import com.k33ptoo.components.KGradientPanel;
import com.pcstore.components.menu.Menu;
import com.pcstore.components.menu.MenuAction;
import com.pcstore.controller.DashboardController;
import com.pcstore.utils.DatabaseConnection;
import raven.toast.Notifications;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * @author MSII
 */
public class DashboardForm extends JFrame {

    private static DashboardForm instance;
    private DashboardController dashboardController;

    private HomeForm homForm;
    private SellForm sellForm;
    private ProductForm productForm;
    private EmployeeManageForm employeeManageForm;
    private WareHouseForm wareHouseForm;
    private InvoiceForm invoiceForm;
    private CustomerForm customerForm;
    private ServiceForm serviceForm;
    private ReportForm reportForm;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.k33ptoo.components.KGradientPanel PanelMenu;
    private com.k33ptoo.components.KGradientPanel PanelNavigation;
    private com.k33ptoo.components.KButton btnSignOut;
    private javax.swing.JComboBox<String> cbLanguage;
    private com.k33ptoo.components.KGradientPanel kMainPanel;
    private com.k33ptoo.components.KGradientPanel kPanelCustomer;
    private com.k33ptoo.components.KGradientPanel kPanelEmployee;
    private com.k33ptoo.components.KGradientPanel kPanelHome;
    private com.k33ptoo.components.KGradientPanel kPanelInvoice;
    private com.k33ptoo.components.KGradientPanel kPanelProduct;
    private com.k33ptoo.components.KGradientPanel kPanelReport;
    private com.k33ptoo.components.KGradientPanel kPanelSell;
    private com.k33ptoo.components.KGradientPanel kPanelService;
    private com.k33ptoo.components.KGradientPanel kPanelWareHouse;
    private javax.swing.JLabel lbMenu;
    private javax.swing.JLabel lbMenuCustomer;
    private javax.swing.JLabel lbMenuEmployee;
    private javax.swing.JLabel lbMenuHome;
    private javax.swing.JLabel lbMenuInvoice;
    private javax.swing.JLabel lbMenuReport;
    private javax.swing.JLabel lbMenuService;
    private javax.swing.JLabel lbMenuWareHouse;
    private javax.swing.JLabel lbNameUser;
    private javax.swing.JLabel lbProductMenu;
    private javax.swing.JLabel lbSell;
    private javax.swing.JLabel lbTImeNow;
    private javax.swing.JPanel panelEmpty;
    private javax.swing.JPanel panelLanguage;
    // End of variables declaration//GEN-END:variables
    private Menu menu;

    public static DashboardForm getInstance() {
        if (instance == null) {
            instance = new DashboardForm();
            instance.dashboardController = new DashboardController(instance);
        }
        return instance;
    }

    public static void resetInstance() {
        DashboardForm.instance = null;
    }

    public static void restartApp() {
        if (instance != null) {
            instance.dispose();
        }
        instance = new DashboardForm();
        instance.setVisible(true);
        instance.setLocationRelativeTo(null);
        instance.dashboardController = new DashboardController(instance);
    }

    public DashboardForm() {

        initComponents();
        initMenu();

        homForm = new HomeForm();
        sellForm = new SellForm();
        productForm = new ProductForm();
        employeeManageForm = new EmployeeManageForm();
        invoiceForm = new InvoiceForm();
        wareHouseForm = new WareHouseForm();
        customerForm = new CustomerForm();
        serviceForm = new ServiceForm();
        reportForm = new ReportForm();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });


        dashboardController = new DashboardController(this);

        Notifications.getInstance().setJFrame(this);
        
        initMenuEvent();
    }


    public void initMenu() {
        PanelMenu.removeAll();
        PanelMenu.setLayout(null);
        PanelMenu.setLayout(new BorderLayout());
        PanelMenu.setLayout(new BorderLayout());
        menu = new Menu();
        PanelMenu.add(menu, BorderLayout.CENTER);
        menu.setOpaque(false);
        menu.getHeader().setIcon(new FlatSVGIcon("com/pcstore/resources/icon/menu_right.svg", 40, 40));

    }

    private void initMenuEvent() {
        menu.addMenuEvent((int index, int subIndex, MenuAction action) -> {
            handleMenuSelection(index, subIndex);
        });

        menu.getHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEventEnabled(e)) {
                    if (menu.isMenuFull()) {
                        dashboardController.collapseMenu();
                    } else {
                        dashboardController.expandMenu();
                    }
                }
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                DatabaseConnection.getInstance().closeConnection();
            }
        });
    }

    /**
     * Xử lý lựa chọn menu dựa trên cấu trúc menu động và phân quyền
     */
    private void handleMenuSelection(int index, int subIndex) {
        List<String[]> menuItemsKey = dashboardController.getMenuItemsKey();

        if (index < 0 || index >= menuItemsKey.size()) {
            showForm(new HomeForm());
            return;
        }

        String[] menuItem = menuItemsKey.get(index);
        String mainMenuText = menuItem[0];

        // Xử lý theo tên menu chính
        switch (mainMenuText) {
            case "menu.home":
                showForm(new HomeForm());
                break;
            case "menu.sell":
                handleSalesMenu(subIndex);
                break;

            case "menu.product":
                handleProductMenu(subIndex);
                break;

            case "menu.warehouse":
                handleWarehouseMenu(subIndex);
                break;
            case "menu.employee":
                handleEmployeeMenu(subIndex);
                break;

            case "menu.customer":
                showForm(new CustomerForm());
                break;

            case "menu.service":
                handleServiceMenu(subIndex);
                break;

            case "menu.report":
                showForm(new ReportForm());
                break;
            default:
                showForm(new HomeForm());
                break;
        }
    }

    private void handleSalesMenu(int subIndex) {
        switch (subIndex) {
            case 1:
                showForm(new SellForm());
                break;
            case 2:
                showForm(new InvoiceForm());
                break;
            default:
                showForm(new SellForm());
                break;
        }
    }

    private void handleProductMenu(int subIndex) {
        switch (subIndex) {
            case 1:
                showForm(new ProductForm());
                break;
            case 2:
                showForm(new CategoryForm());
                break;
            default:
                showForm(new ProductForm());
                break;
        }
    }

    private void handleWarehouseMenu(int subIndex) {
        switch (subIndex) {
            case 1:
                showForm(new WareHouseForm());
                break;
            
            case 2:
                showForm(new SupplierForm());
                break;
            case 3:
                showForm(new InventoryCheckForm());
                break;
            default:
                showForm(new WareHouseForm());
                break;
        }
    }

    private void handleEmployeeMenu(int subIndex) {
        switch (subIndex) {
            case 1:
                showForm(new EmployeeForm());
                break;
            case 2:
                showForm(new UserForm());
                break;
            default:
                showForm(new EmployeeForm());
                break;
        }
    }

    private void handleServiceMenu(int subIndex) {
        switch (subIndex) {
            case 1:
                showForm(new WarrantyServiceForm());
                break;
            case 2:
                showForm(new RepairServiceForm());
                break;
            case 3:
                showForm(new ReturnServiceForm());
                break;
            default:
                showForm(new WarrantyServiceForm());
                break;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelMenu = new com.k33ptoo.components.KGradientPanel();
        lbMenu = new javax.swing.JLabel();
        kPanelHome = new com.k33ptoo.components.KGradientPanel();
        lbMenuHome = new javax.swing.JLabel();
        kPanelSell = new com.k33ptoo.components.KGradientPanel();
        lbSell = new javax.swing.JLabel();
        kPanelProduct = new com.k33ptoo.components.KGradientPanel();
        lbProductMenu = new javax.swing.JLabel();
        kPanelInvoice = new com.k33ptoo.components.KGradientPanel();
        lbMenuInvoice = new javax.swing.JLabel();
        kPanelWareHouse = new com.k33ptoo.components.KGradientPanel();
        lbMenuWareHouse = new javax.swing.JLabel();
        kPanelEmployee = new com.k33ptoo.components.KGradientPanel();
        lbMenuEmployee = new javax.swing.JLabel();
        kPanelService = new com.k33ptoo.components.KGradientPanel();
        lbMenuService = new javax.swing.JLabel();
        kPanelCustomer = new com.k33ptoo.components.KGradientPanel();
        lbMenuCustomer = new javax.swing.JLabel();
        kPanelReport = new com.k33ptoo.components.KGradientPanel();
        lbMenuReport = new javax.swing.JLabel();
        PanelNavigation = new com.k33ptoo.components.KGradientPanel();
        panelEmpty = new javax.swing.JPanel();
        lbTImeNow = new javax.swing.JLabel();
        panelLanguage = new javax.swing.JPanel();
        cbLanguage = new javax.swing.JComboBox<>();
        lbNameUser = new javax.swing.JLabel();
        btnSignOut = new com.k33ptoo.components.KButton();
        kMainPanel = new com.k33ptoo.components.KGradientPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/pcstore/resources/vi_VN"); // NOI18N
        setTitle(bundle.getString("titleApp")); // NOI18N
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImages(null);

        PanelMenu.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        PanelMenu.setkEndColor(new java.awt.Color(102, 153, 255));
        PanelMenu.setkGradientFocus(40);
        PanelMenu.setkStartColor(new java.awt.Color(153, 255, 153));
        PanelMenu.setMinimumSize(new java.awt.Dimension(100, 713));
        PanelMenu.setPreferredSize(new java.awt.Dimension(220, 680));
        PanelMenu.setLayout(new java.awt.GridLayout(11, 1, 0, 25));

        lbMenu.setForeground(new java.awt.Color(255, 255, 255));
        lbMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/x-button.png"))); // NOI18N
        lbMenu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        PanelMenu.add(lbMenu);

        kPanelHome.setkBorderRadius(30);
        kPanelHome.setkBorderSize(1.5F);
        kPanelHome.setkEndColor(new java.awt.Color(255, 255, 255));
        kPanelHome.setkFillBackground(false);
        kPanelHome.setkStartColor(new java.awt.Color(255, 255, 255));
        kPanelHome.setOpaque(false);
        kPanelHome.setPreferredSize(new java.awt.Dimension(200, 10));
        kPanelHome.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 5));

        lbMenuHome.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbMenuHome.setForeground(new java.awt.Color(255, 255, 255));
        lbMenuHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/home_24px.png"))); // NOI18N
        lbMenuHome.setText(bundle.getString("txtMenuHome")); // NOI18N
        lbMenuHome.setToolTipText(bundle.getString("txtMenuHome")); // NOI18N
        lbMenuHome.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbMenuHome.setMaximumSize(new java.awt.Dimension(104, 30));
        lbMenuHome.setMinimumSize(new java.awt.Dimension(104, 30));
        lbMenuHome.setPreferredSize(new java.awt.Dimension(200, 30));
        kPanelHome.add(lbMenuHome);

        PanelMenu.add(kPanelHome);

        kPanelSell.setkBorderRadius(30);
        kPanelSell.setkBorderSize(1.5F);
        kPanelSell.setkEndColor(new java.awt.Color(255, 255, 255));
        kPanelSell.setkFillBackground(false);
        kPanelSell.setkStartColor(new java.awt.Color(255, 255, 255));
        kPanelSell.setOpaque(false);
        kPanelSell.setPreferredSize(new java.awt.Dimension(200, 10));
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 5);
        flowLayout1.setAlignOnBaseline(true);
        kPanelSell.setLayout(flowLayout1);

        lbSell.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbSell.setForeground(new java.awt.Color(255, 255, 255));
        lbSell.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/shopping_cart_24px.png"))); // NOI18N
        lbSell.setText(bundle.getString("txtMenuSell")); // NOI18N
        lbSell.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbSell.setMaximumSize(new java.awt.Dimension(200, 30));
        lbSell.setMinimumSize(new java.awt.Dimension(200, 30));
        lbSell.setPreferredSize(new java.awt.Dimension(200, 30));
        kPanelSell.add(lbSell);

        PanelMenu.add(kPanelSell);

        kPanelProduct.setkBorderRadius(30);
        kPanelProduct.setkBorderSize(1.5F);
        kPanelProduct.setkEndColor(new java.awt.Color(255, 255, 255));
        kPanelProduct.setkFillBackground(false);
        kPanelProduct.setkStartColor(new java.awt.Color(255, 255, 255));
        kPanelProduct.setOpaque(false);
        kPanelProduct.setPreferredSize(new java.awt.Dimension(200, 10));
        kPanelProduct.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 5));

        lbProductMenu.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbProductMenu.setForeground(new java.awt.Color(255, 255, 255));
        lbProductMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/box.png"))); // NOI18N
        lbProductMenu.setText(bundle.getString("txtMenuProduct")); // NOI18N
        lbProductMenu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbProductMenu.setMaximumSize(new java.awt.Dimension(200, 30));
        lbProductMenu.setMinimumSize(new java.awt.Dimension(200, 30));
        lbProductMenu.setPreferredSize(new java.awt.Dimension(200, 30));
        lbProductMenu.setRequestFocusEnabled(false);
        kPanelProduct.add(lbProductMenu);

        PanelMenu.add(kPanelProduct);

        kPanelInvoice.setkBorderRadius(30);
        kPanelInvoice.setkBorderSize(1.5F);
        kPanelInvoice.setkEndColor(new java.awt.Color(255, 255, 255));
        kPanelInvoice.setkFillBackground(false);
        kPanelInvoice.setkStartColor(new java.awt.Color(255, 255, 255));
        kPanelInvoice.setOpaque(false);
        kPanelInvoice.setPreferredSize(new java.awt.Dimension(200, 10));
        kPanelInvoice.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 5));

        lbMenuInvoice.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbMenuInvoice.setForeground(new java.awt.Color(255, 255, 255));
        lbMenuInvoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/invoice.png"))); // NOI18N
        lbMenuInvoice.setText(bundle.getString("txtMenuInvoice")); // NOI18N
        lbMenuInvoice.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbMenuInvoice.setMaximumSize(new java.awt.Dimension(200, 30));
        lbMenuInvoice.setMinimumSize(new java.awt.Dimension(200, 30));
        lbMenuInvoice.setPreferredSize(new java.awt.Dimension(200, 30));
        kPanelInvoice.add(lbMenuInvoice);

        PanelMenu.add(kPanelInvoice);

        kPanelWareHouse.setkBorderRadius(30);
        kPanelWareHouse.setkBorderSize(1.5F);
        kPanelWareHouse.setkEndColor(new java.awt.Color(255, 255, 255));
        kPanelWareHouse.setkFillBackground(false);
        kPanelWareHouse.setkStartColor(new java.awt.Color(255, 255, 255));
        kPanelWareHouse.setOpaque(false);
        kPanelWareHouse.setPreferredSize(new java.awt.Dimension(200, 10));
        kPanelWareHouse.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 5));

        lbMenuWareHouse.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbMenuWareHouse.setForeground(new java.awt.Color(255, 255, 255));
        lbMenuWareHouse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/warehouse.png"))); // NOI18N
        lbMenuWareHouse.setText(bundle.getString("txtMenuWareHouse")); // NOI18N
        lbMenuWareHouse.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbMenuWareHouse.setMinimumSize(new java.awt.Dimension(200, 30));
        lbMenuWareHouse.setPreferredSize(new java.awt.Dimension(200, 30));
        kPanelWareHouse.add(lbMenuWareHouse);

        PanelMenu.add(kPanelWareHouse);

        kPanelEmployee.setkBorderRadius(30);
        kPanelEmployee.setkBorderSize(1.5F);
        kPanelEmployee.setkEndColor(new java.awt.Color(255, 255, 255));
        kPanelEmployee.setkFillBackground(false);
        kPanelEmployee.setkStartColor(new java.awt.Color(255, 255, 255));
        kPanelEmployee.setOpaque(false);
        kPanelEmployee.setPreferredSize(new java.awt.Dimension(200, 10));
        kPanelEmployee.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 15, 5));

        lbMenuEmployee.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbMenuEmployee.setForeground(new java.awt.Color(255, 255, 255));
        lbMenuEmployee.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/management.png"))); // NOI18N
        lbMenuEmployee.setText(bundle.getString("txtMenuIEmployee")); // NOI18N
        lbMenuEmployee.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbMenuEmployee.setMinimumSize(new java.awt.Dimension(200, 30));
        lbMenuEmployee.setPreferredSize(new java.awt.Dimension(200, 30));
        kPanelEmployee.add(lbMenuEmployee);

        PanelMenu.add(kPanelEmployee);

        kPanelService.setkBorderRadius(30);
        kPanelService.setkBorderSize(1.5F);
        kPanelService.setkEndColor(new java.awt.Color(255, 255, 255));
        kPanelService.setkFillBackground(false);
        kPanelService.setkStartColor(new java.awt.Color(255, 255, 255));
        kPanelService.setOpaque(false);
        kPanelService.setPreferredSize(new java.awt.Dimension(200, 10));
        kPanelService.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 5));

        lbMenuService.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbMenuService.setForeground(new java.awt.Color(255, 255, 255));
        lbMenuService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/service-product.png"))); // NOI18N
        lbMenuService.setText(bundle.getString("txtMenuIService")); // NOI18N
        lbMenuService.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbMenuService.setMinimumSize(new java.awt.Dimension(200, 30));
        lbMenuService.setPreferredSize(new java.awt.Dimension(200, 30));
        kPanelService.add(lbMenuService);

        PanelMenu.add(kPanelService);

        kPanelCustomer.setkBorderRadius(30);
        kPanelCustomer.setkBorderSize(1.5F);
        kPanelCustomer.setkEndColor(new java.awt.Color(255, 255, 255));
        kPanelCustomer.setkFillBackground(false);
        kPanelCustomer.setkStartColor(new java.awt.Color(255, 255, 255));
        kPanelCustomer.setOpaque(false);
        kPanelCustomer.setPreferredSize(new java.awt.Dimension(200, 10));
        java.awt.FlowLayout flowLayout2 = new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 5);
        flowLayout2.setAlignOnBaseline(true);
        kPanelCustomer.setLayout(flowLayout2);

        lbMenuCustomer.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbMenuCustomer.setForeground(new java.awt.Color(255, 255, 255));
        lbMenuCustomer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbMenuCustomer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/user-bag.png"))); // NOI18N
        lbMenuCustomer.setText(bundle.getString("lbMenuCustomer")); // NOI18N
        lbMenuCustomer.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbMenuCustomer.setMinimumSize(new java.awt.Dimension(200, 30));
        lbMenuCustomer.setPreferredSize(new java.awt.Dimension(200, 30));
        kPanelCustomer.add(lbMenuCustomer);

        PanelMenu.add(kPanelCustomer);

        kPanelReport.setkBorderRadius(30);
        kPanelReport.setkBorderSize(1.5F);
        kPanelReport.setkEndColor(new java.awt.Color(255, 255, 255));
        kPanelReport.setkFillBackground(false);
        kPanelReport.setkStartColor(new java.awt.Color(255, 255, 255));
        kPanelReport.setOpaque(false);
        kPanelReport.setPreferredSize(new java.awt.Dimension(200, 10));
        kPanelReport.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 20, 5));

        lbMenuReport.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lbMenuReport.setForeground(new java.awt.Color(255, 255, 255));
        lbMenuReport.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbMenuReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/monitor.png"))); // NOI18N
        lbMenuReport.setText(bundle.getString("txtMenuIReport")); // NOI18N
        lbMenuReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbMenuReport.setMaximumSize(new java.awt.Dimension(200, 30));
        lbMenuReport.setMinimumSize(new java.awt.Dimension(200, 30));
        lbMenuReport.setPreferredSize(new java.awt.Dimension(200, 30));
        kPanelReport.add(lbMenuReport);

        PanelMenu.add(kPanelReport);

        getContentPane().add(PanelMenu, java.awt.BorderLayout.LINE_START);

        PanelNavigation.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        PanelNavigation.setkEndColor(new java.awt.Color(102, 153, 255));
        PanelNavigation.setkStartColor(new java.awt.Color(153, 255, 153));
        PanelNavigation.setLayout(new javax.swing.BoxLayout(PanelNavigation, javax.swing.BoxLayout.LINE_AXIS));

        panelEmpty.setOpaque(false);
        panelEmpty.setPreferredSize(new java.awt.Dimension(850, 10));
        panelEmpty.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lbTImeNow.setBackground(new java.awt.Color(255, 255, 255));
        lbTImeNow.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbTImeNow.setForeground(new java.awt.Color(255, 255, 255));
        lbTImeNow.setText("timeNow");
        lbTImeNow.setToolTipText("");
        panelEmpty.add(lbTImeNow);

        PanelNavigation.add(panelEmpty);

        panelLanguage.setMinimumSize(new java.awt.Dimension(150, 30));
        panelLanguage.setOpaque(false);
        panelLanguage.setPreferredSize(new java.awt.Dimension(150, 30));
        panelLanguage.setLayout(new javax.swing.BoxLayout(panelLanguage, javax.swing.BoxLayout.LINE_AXIS));

        cbLanguage.setBackground(new java.awt.Color(102, 153, 255));
        cbLanguage.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cbLanguage.setForeground(new java.awt.Color(255, 255, 255));
        cbLanguage.setMaximumRowCount(10);
        cbLanguage.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Việt Nam", "English"}));
        cbLanguage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelLanguage.add(cbLanguage);

        PanelNavigation.add(panelLanguage);

        lbNameUser.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbNameUser.setForeground(new java.awt.Color(255, 255, 255));
        lbNameUser.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbNameUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pcstore/resources/icon/circle-user.png"))); // NOI18N
        lbNameUser.setText(bundle.getString("titleLogo")); // NOI18N
        lbNameUser.setPreferredSize(new java.awt.Dimension(200, 32));
        PanelNavigation.add(lbNameUser);

        btnSignOut.setText(bundle.getString("btnSignOut")); // NOI18N
        btnSignOut.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSignOut.setkBorderRadius(35);
        btnSignOut.setkHoverForeGround(new java.awt.Color(255, 255, 255));
        btnSignOut.setPreferredSize(new java.awt.Dimension(155, 35));
        PanelNavigation.add(btnSignOut);

        getContentPane().add(PanelNavigation, java.awt.BorderLayout.PAGE_START);

        kMainPanel.setBackground(new java.awt.Color(255, 255, 255));
        kMainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        kMainPanel.setkBorderColor(new java.awt.Color(204, 204, 204));
        kMainPanel.setkBorderRadius(20);
        kMainPanel.setkFillBackground(false);
        kMainPanel.setOpaque(false);
        kMainPanel.setPreferredSize(new java.awt.Dimension(1153, 0));
        kMainPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(kMainPanel, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public JLabel getLbNameUser() {
        return lbNameUser;
    }

    // Hàm xử lý bật tắt sự kiện
    public boolean isEventEnabled(MouseEvent e) {
        Component source = (Component) e.getSource();
        KGradientPanel panel = (KGradientPanel) SwingUtilities.getAncestorOfClass(KGradientPanel.class, source);

        if (panel != null && Boolean.TRUE.equals(panel.getClientProperty("menu-disabled"))) {
            return false;
        }

        return true;
    }

    public void showForm(Component component) {
        // Hiển thị component trong kMainPanel
        kMainPanel.removeAll();
        kMainPanel.setLayout(new BorderLayout());

        if (component instanceof JPanel) {
            kMainPanel.add((JPanel) component, BorderLayout.CENTER);
        } else {
            kMainPanel.add(homForm, BorderLayout.CENTER);
        }

        kMainPanel.repaint();
        kMainPanel.revalidate();
    }


    public Menu getMenu() {
        return menu;
    }

    public KGradientPanel getkMainPanel() {
        return kMainPanel;
    }

    public KGradientPanel getkPanelCustomer() {
        return kPanelCustomer;
    }

    public KGradientPanel getkPanelEmployee() {
        return kPanelEmployee;
    }

    public KGradientPanel getkPanelHome() {
        return kPanelHome;
    }

    public KGradientPanel getkPanelInvoice() {
        return kPanelInvoice;
    }

    public KGradientPanel getkPanelProduct() {
        return kPanelProduct;
    }

    public KGradientPanel getkPanelReport() {
        return kPanelReport;
    }

    public KGradientPanel getkPanelSell() {
        return kPanelSell;
    }

    public KGradientPanel getkPanelService() {
        return kPanelService;
    }

    public KGradientPanel getkPanelWareHouse() {
        return kPanelWareHouse;
    }

    public KButton getBtnSignOut() {
        return btnSignOut;
    }


    public javax.swing.JLabel getLbTImeNow() {
        return lbTImeNow;
    }

    public KGradientPanel getPanelMenu() {
        return PanelMenu;
    }

    public JLabel getLbMenu() {
        return lbMenu;
    }


    public javax.swing.JComboBox<String> getCbLanguage() {
        return cbLanguage;
    }

    public javax.swing.JLabel getLbMenuCustomer() {
        return lbMenuCustomer;
    }

    public javax.swing.JLabel getLbMenuEmployee() {
        return lbMenuEmployee;
    }

    public javax.swing.JLabel getLbMenuHome() {
        return lbMenuHome;
    }

    public javax.swing.JLabel getLbMenuInvoice() {
        return lbMenuInvoice;
    }

    public javax.swing.JLabel getLbMenuReport() {
        return lbMenuReport;
    }

    public javax.swing.JLabel getLbMenuService() {
        return lbMenuService;
    }

    public javax.swing.JLabel getLbMenuWareHouse() {
        return lbMenuWareHouse;
    }

    public javax.swing.JLabel getLbProductMenu() {
        return lbProductMenu;
    }

    public javax.swing.JLabel getLbSell() {
        return lbSell;
    }


    private KGradientPanel activePanel = null;
    private JLabel activeLabel = null;

    // Thêm phương thức formWindowClosing để dọn dẹp resources khi đóng cửa sổ
    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        if (dashboardController != null) {
            dashboardController.cleanup();
        }
    }

    // Khi ứng dụng đóng
    public void dispose() {
        if (dashboardController != null) {
            dashboardController.cleanup();
        }
        super.dispose();
    }

    public void disposeAllForms() {
        // Giải phóng bộ nhớ cho tất cả form
        homForm = null;
        sellForm = null;
        productForm = null;
        employeeManageForm = null;
        wareHouseForm = null;
        invoiceForm = null;
        customerForm = null;
        serviceForm = null;
        reportForm = null;

        // Xóa các thành phần khỏi kMainPanel
        kMainPanel.removeAll();
        kMainPanel.revalidate();
        kMainPanel.repaint();

        // Gợi ý cho Garbage Collector
        System.gc();
    }

}
