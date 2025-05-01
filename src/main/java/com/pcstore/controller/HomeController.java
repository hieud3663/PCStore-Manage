package com.pcstore.controller;

import com.pcstore.chart.Chart;
import com.pcstore.chart.ModelChart;
import com.pcstore.model.User;
import com.pcstore.service.EmployeeService;
import com.pcstore.service.InvoiceService;
import com.pcstore.service.RevenueService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.SessionManager;
import com.pcstore.view.HomeForm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Controller để quản lý hiển thị thông tin trên Home Form
 */
public class HomeController {
    private static HomeController instance;
    
    private HomeForm homeForm;
    private RevenueService revenueService;
    private InvoiceService invoiceService;
    private EmployeeService employeeService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private final NumberFormat currencyFormatter = LocaleManager.getInstance().getNumberFormatter();
    private Locale currentLocale = LocaleManager.getInstance().getCurrentLocale();
    private ScheduledExecutorService scheduler;
    private static final int REFRESH_INTERVAL = 30; // 30 giây
    
    /**
     * Lấy instance duy nhất của controller (Singleton pattern)
     * @param homeForm Form dashboard cần điều khiển
     * @return HomeController instance
     */
    public static synchronized HomeController getInstance(HomeForm homeForm) {
        if (instance == null) {
            instance = new HomeController(homeForm);
        } else {
            instance.homeForm = homeForm;
            instance.loadData();
        }
        return instance;
    }
    
    /**
     * Khởi tạo controller với form (Giao diện người dùng)
     * @param homeForm Form hiển thị trang chủ
     */
    private HomeController(HomeForm homeForm) {
        try {
            this.homeForm = homeForm;
            this.revenueService = ServiceFactory.getRevenueService();
            this.invoiceService = ServiceFactory.getInvoiceService();
            this.employeeService = ServiceFactory.getEmployeeService();
            
            loadData();
            
            startDataRefreshScheduler();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(homeForm, "Lỗi khi khởi tạo controller: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Tải tất cả dữ liệu cần thiết cho trang chủ
     */
    public void loadData() {
        loadUserInfo();
        loadTodaySummary();
        loadMonthSummary();
        loadWeeklyRevenueChart();
    }
    
    /**
     * Tải thông tin người dùng đang đăng nhập
     */
    private void loadUserInfo() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String employeeId = currentUser.getEmployeeId();
        
        try {
            homeForm.getTxtName().setText(currentUser.getFullName());
            homeForm.getTxtEmployeeID().setText(employeeId);
            homeForm.getTxtPosition().setText(currentUser.getRoleName());
            
            homeForm.getTxtLoginAt().setText(LocalDateTime.now().format(dateFormatter));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(homeForm, "Lỗi khi tải thông tin người dùng: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Tải thống kê doanh thu và đơn hàng hôm nay
     */
    private void loadTodaySummary() {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(23, 59, 59);
            
            // Lấy số đơn hàng trong ngày
            int totalOrdersToday = invoiceService.countInvoicesByDateRange(startOfDay, endOfDay);
            homeForm.getTxtOrderToday().setText(String.valueOf(totalOrdersToday));
            
            // Lấy doanh thu trong ngày
            BigDecimal totalRevenueToday = revenueService.getTotalRevenueByDateRange(startOfDay, endOfDay);
            homeForm.getTxtProfitToday().setText(currencyFormatter.format(totalRevenueToday));
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(homeForm, "Lỗi khi tải thống kê hôm nay: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Tải thống kê doanh thu và đơn hàng trong tháng
     */
    private void loadMonthSummary() {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59);
            
            // Lấy số đơn hàng trong tháng
            int totalOrdersMonth = invoiceService.countInvoicesByDateRange(startOfMonth, endOfMonth);
            homeForm.getTxtOrderMonth().setText(String.valueOf(totalOrdersMonth));
            
            // Lấy doanh thu trong tháng
            BigDecimal totalRevenueMonth = revenueService.getTotalRevenueByDateRange(startOfMonth, endOfMonth);
            homeForm.getTxtProfitMonth().setText(currencyFormatter.format(totalRevenueMonth));
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(homeForm, "Lỗi khi tải thống kê tháng: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Tải và vẽ biểu đồ doanh thu theo tuần
     */
    private void loadWeeklyRevenueChart() {
        try {
            // Lấy ngày đầu tuần (thứ 2) và cuối tuần (chủ nhật)
            LocalDate today = LocalDate.now();
            LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            
            Chart chart = new Chart();
            homeForm.getPanelChart().removeAll();
            homeForm.getPanelChart().setLayout(new BorderLayout());
            homeForm.getPanelChart().add(chart, BorderLayout.CENTER);
            
            chart.addLegend("Doanh thu (triệu đồng)", new Color(26, 162, 106));
            chart.addLegend("Số đơn hàng", new Color(30, 113, 195));
            
            for (int i = 0; i < 7; i++) {
                LocalDate date = startOfWeek.plusDays(i);
                LocalDateTime startOfDay = date.atStartOfDay();
                LocalDateTime endOfDay = date.atTime(23, 59, 59);
                
                // Lấy doanh thu theo ngày
                BigDecimal dailyRevenue = revenueService.getTotalRevenueByDateRange(startOfDay, endOfDay);
                
                // Lấy số đơn hàng theo ngày
                int dailyOrders = invoiceService.countInvoicesByDateRange(startOfDay, endOfDay);
                
                double revenueInMillions = dailyRevenue.doubleValue() / 1000000.0;
                
                String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, currentLocale);
                
                chart.addData(new ModelChart(dayName, new double[]{revenueInMillions, dailyOrders}));
            }
            
            chart.start();
            
            homeForm.getPanelChart().revalidate();
            homeForm.getPanelChart().repaint();
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(homeForm, "Lỗi khi tải biểu đồ doanh thu theo tuần: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Cập nhật lại giao diện khi ngôn ngữ thay đổi
     */
    public void refreshLanguage() {
        currentLocale = LocaleManager.getInstance().getCurrentLocale();
        
        SwingUtilities.invokeLater(() -> {
            loadData();
        });
    }
    
    /**
     * Thêm getter cho HomeForm
     * @return HomeForm hiện tại
     */
    public HomeForm getHomeForm() {
        return homeForm;
    }
    
    /**
     * Lấy danh sách doanh thu theo ngày trong khoảng thời gian
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     * @return Danh sách dữ liệu doanh thu theo ngày
     */
    public List<Map<String, Object>> getDailyRevenueData(LocalDate fromDate, LocalDate toDate) {
        try {
            LocalDateTime fromDateTime = fromDate.atStartOfDay();
            LocalDateTime toDateTime = toDate.atTime(23, 59, 59);
            
            return revenueService.getDailyRevenueData(fromDateTime, toDateTime);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(homeForm, "Lỗi khi lấy dữ liệu doanh thu theo ngày: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }
    
    /**
     * Lấy tổng doanh thu trong khoảng thời gian
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     * @return Tổng doanh thu
     */
    public BigDecimal getTotalRevenue(LocalDate fromDate, LocalDate toDate) {
        try {
            LocalDateTime fromDateTime = fromDate.atStartOfDay();
            LocalDateTime toDateTime = toDate.atTime(23, 59, 59);
            
            return revenueService.getTotalRevenueByDateRange(fromDateTime, toDateTime);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(homeForm, "Lỗi khi lấy tổng doanh thu: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Khởi tạo scheduler để tự động cập nhật dữ liệu
     */
    private void startDataRefreshScheduler() {
        stopDataRefreshScheduler();
        
        scheduler = Executors.newSingleThreadScheduledExecutor();
        
        scheduler.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(() -> {
                loadData();
            });
        }, REFRESH_INTERVAL, REFRESH_INTERVAL, TimeUnit.SECONDS);
        
    }
    
    /**
     * Dừng scheduler cập nhật dữ liệu
     */
    private void stopDataRefreshScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
    }
    
    /**
     * Dừng các scheduler và giải phóng tài nguyên
     */
    public void cleanup() {
        stopDataRefreshScheduler();
    }
}