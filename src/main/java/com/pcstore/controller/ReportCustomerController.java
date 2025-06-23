package com.pcstore.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.pcstore.chart.Chart;
import com.pcstore.chart.ModelChart;
import com.pcstore.service.CustomerService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.JExcel;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.TableUtils;
import com.pcstore.view.ReportCustomerForm;

import raven.toast.Notifications;

/**
 * Controller để quản lý báo cáo doanh thu khách hàng
 */
public class ReportCustomerController {
    // Singleton instance
    private static ReportCustomerController instance;

    private ReportCustomerForm reportCustomerForm;
    private CustomerService customerService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final NumberFormat currencyFormatter = LocaleManager.getInstance().getNumberFormatter();
    private LocalDate fromDate;
    private LocalDate toDate;
    private boolean isUpdatingToDate = false;

    /**
     * Lấy instance duy nhất của controller (Singleton pattern)
     */
    public static synchronized ReportCustomerController getInstance(ReportCustomerForm reportCustomerForm) {
        if (instance == null) {
            instance = new ReportCustomerController(reportCustomerForm);
        } else {
            instance.reportCustomerForm = reportCustomerForm;
            instance.loadCustomerRevenueData();
            LocalDate today = LocalDate.now();
            instance.fromDate = today.withDayOfMonth(1);
            instance.toDate = today;
            instance.setupEventListeners();
            instance.setupTableStyle();
            instance.loadCustomerRevenueData();
        }
        return instance;
    }

    /**
     * Constructor private cho Singleton pattern
     */
    private ReportCustomerController(ReportCustomerForm reportCustomerForm) {
        try {
            this.reportCustomerForm = reportCustomerForm;
            this.customerService = ServiceFactory.getCustomerService();

            LocalDate today = LocalDate.now();
            this.fromDate = today.withDayOfMonth(1);
            this.toDate = today;

            setupEventListeners();
            setupTableStyle();
            loadCustomerRevenueData();

            updateDateRange(fromDate, toDate);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(reportCustomerForm,
                    "Lỗi khởi tạo controller: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Constructor for testing with connection
     */
    public ReportCustomerController(Connection connection) {
        this.customerService = new CustomerService(connection);
        LocalDate today = LocalDate.now();
        this.fromDate = today.withDayOfMonth(1);
        this.toDate = today;
    }

    /**
     * Thiết lập các event listeners cho form
     */
    private void setupEventListeners() {
        // Event cho nút áp dụng
        reportCustomerForm.getBtnApply().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleFromDateChange();
                handleToDateChange();
            }
        });

        // Event cho nút xuất Excel
        reportCustomerForm.getBtnExportReport().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                exportCustomerRevenueReport();
            }
        });
    }

    /**
     * Xử lý khi ngày bắt đầu thay đổi
     */
    private void handleFromDateChange() {
        if (isUpdatingToDate) return;

        String dateStr = reportCustomerForm.getTxtFromDate().getText();
        if (dateStr.length() != 10) {
            return;
        }

        try {
            LocalDate newFromDate = LocalDate.parse(dateStr, dateFormatter);
            LocalDate tempFromDate = fromDate;
            fromDate = newFromDate;

            // Kiểm tra nếu toDate nhỏ hơn fromDate
            if (toDate.isBefore(newFromDate)) {
                isUpdatingToDate = true;
                toDate = newFromDate;
                reportCustomerForm.setChooserFromDate(tempFromDate);
                isUpdatingToDate = false;

                Notifications.getInstance().show(Notifications.Type.ERROR, 
                    "Ngày kết thúc đã được cập nhật theo ngày bắt đầu");
                return;
            }

            loadCustomerRevenueData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Xử lý khi ngày kết thúc thay đổi
     */
    private void handleToDateChange() {
        if (isUpdatingToDate) return;

        String dateStr = reportCustomerForm.getTxtToDate().getText();
        if (dateStr.length() != 10) {
            return;
        }

        try {
            LocalDate newToDate = LocalDate.parse(dateStr, dateFormatter);

            // Kiểm tra nếu ngày kết thúc < ngày bắt đầu
            if (newToDate.isBefore(fromDate)) {
                Notifications.getInstance().show(Notifications.Type.ERROR,
                        "Ngày kết thúc không thể nhỏ hơn ngày bắt đầu: " + fromDate.format(dateFormatter));
                isUpdatingToDate = true;
                reportCustomerForm.setChooserToDate(toDate);
                isUpdatingToDate = false;
                return;
            }

            toDate = newToDate;
            loadCustomerRevenueData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Thiết lập style cho bảng
     */
    private void setupTableStyle() {
        TableRowSorter<TableModel> sorter = TableUtils.applyDefaultStyle(reportCustomerForm.getTableCustomerRevenue());
        TableUtils.setNumberColumns(sorter, 2, 3, 4, 5, 6, 7, 8); // Số cột, tiền
    }

    /**
     * Tải dữ liệu doanh thu khách hàng
     */
    public void loadCustomerRevenueData() {
        try {
            LocalDateTime fromDateTime = fromDate.atTime(LocalTime.MIN);
            LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);
            
            List<Map<String, Object>> customerRevenueData = customerService.getCustomerRevenueData(fromDateTime, toDateTime);

            DefaultTableModel model = (DefaultTableModel) reportCustomerForm.getTableCustomerRevenue().getModel();
            model.setRowCount(0);

            BigDecimal totalRevenue = BigDecimal.ZERO;
            int totalCustomers = 0;
            int totalOrders = 0;
            BigDecimal totalReturnValue = BigDecimal.ZERO;

            for (Map<String, Object> data : customerRevenueData) {
                totalCustomers++;
                
                String customerId = (String) data.get("customerId");
                String customerName = (String) data.get("customerName");
                Integer orderCount = (Integer) data.get("orderCount");
                BigDecimal totalAmount = (BigDecimal) data.get("totalAmount");
                BigDecimal discountAmount = (BigDecimal) data.get("discountAmount");
                BigDecimal revenue = (BigDecimal) data.get("revenue");
                Integer returnCount = (Integer) data.get("returnCount");
                BigDecimal returnValue = (BigDecimal) data.get("returnValue");
                BigDecimal netRevenue = (BigDecimal) data.get("netRevenue");

                if (orderCount == null) orderCount = 0;
                if (totalAmount == null) totalAmount = BigDecimal.ZERO;
                if (discountAmount == null) discountAmount = BigDecimal.ZERO;
                if (revenue == null) revenue = BigDecimal.ZERO;
                if (returnCount == null) returnCount = 0;
                if (returnValue == null) returnValue = BigDecimal.ZERO;
                if (netRevenue == null) netRevenue = BigDecimal.ZERO;

                totalRevenue = totalRevenue.add(netRevenue);
                totalOrders += orderCount;
                totalReturnValue = totalReturnValue.add(returnValue);

                Object[] row = {
                    customerId,
                    customerName != null ? customerName : "Khách lẻ",
                    orderCount,
                    currencyFormatter.format(totalAmount),
                    currencyFormatter.format(discountAmount),
                    currencyFormatter.format(revenue),
                    returnCount,
                    currencyFormatter.format(returnValue),
                    currencyFormatter.format(netRevenue)
                };
                model.addRow(row);
            }

            // Cập nhật thống kê
            updateStatistics(totalCustomers, totalOrders, totalRevenue, totalReturnValue, customerRevenueData);
            
            // Cập nhật tổng doanh thu ở footer
            reportCustomerForm.getRevenue().setText(currencyFormatter.format(totalRevenue));

            // Cập nhật biểu đồ
            updateRevenueChart(customerRevenueData);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Lỗi khi tải dữ liệu doanh thu khách hàng: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cập nhật các thống kê
     */
    private void updateStatistics(int totalCustomers, int totalOrders, BigDecimal totalRevenue, 
                                 BigDecimal totalReturnValue, List<Map<String, Object>> customerData) {
        // Tổng số khách hàng
        reportCustomerForm.getTxtTotalCustomers().setText(String.valueOf(totalCustomers));
        
        // Giá trị trung bình/đơn
        BigDecimal averageOrderValue = totalOrders > 0 ? 
            totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP) : 
            BigDecimal.ZERO;
        reportCustomerForm.getTxtAverageOrderValue().setText(currencyFormatter.format(averageOrderValue));
        
        // Khách hàng hàng đầu
        String topCustomer = customerData.stream()
            .max((c1, c2) -> {
                BigDecimal revenue1 = (BigDecimal) c1.get("netRevenue");
                BigDecimal revenue2 = (BigDecimal) c2.get("netRevenue");
                return revenue1.compareTo(revenue2);
            })
            .map(c -> (String) c.get("customerName"))
            .orElse("Không có dữ liệu");
        reportCustomerForm.getTxtTopCustomer().setText(topCustomer);
        
        // Tỷ lệ trả hàng
        BigDecimal returnRate = totalRevenue.compareTo(BigDecimal.ZERO) > 0 ?
            totalReturnValue.divide(totalRevenue, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")) :
            BigDecimal.ZERO;
        reportCustomerForm.getTxtReturnRate().setText(returnRate.setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
    }

    /**
     * Cập nhật biểu đồ doanh thu khách hàng (Top 10)
     */
    private void updateRevenueChart(List<Map<String, Object>> customerData) {
        try {
            reportCustomerForm.getPanelChart().removeAll();

            Chart chart = new Chart();
            chart.addLegend("Doanh thu", new Color(12, 84, 175));
            chart.addLegend("Số đơn hàng", new Color(54, 4, 143));

            // Sắp xếp theo doanh thu giảm dần và lấy top 10
            customerData.sort((c1, c2) -> {
                BigDecimal revenue1 = (BigDecimal) c1.get("netRevenue");
                BigDecimal revenue2 = (BigDecimal) c2.get("netRevenue");
                return revenue2.compareTo(revenue1);
            });

            int maxEntries = Math.min(10, customerData.size());
            
            for (int i = 0; i < maxEntries; i++) {
                Map<String, Object> data = customerData.get(i);
                String customerName = (String) data.get("customerName");
                BigDecimal revenue = (BigDecimal) data.get("netRevenue");
                Integer orderCount = (Integer) data.get("orderCount");
                
                if (customerName == null) customerName = "Khách lẻ";
                if (revenue == null) revenue = BigDecimal.ZERO;
                if (orderCount == null) orderCount = 0;
                
                // Cắt tên khách hàng nếu quá dài
                String displayName = customerName.length() > 15 ? 
                    customerName.substring(0, 12) + "..." : customerName;
                
                chart.addData(new ModelChart(displayName, 
                    new double[]{revenue.doubleValue() / 1000000, orderCount.doubleValue()}));
            }

            chart.start();
            
            reportCustomerForm.getPanelChart().setLayout(new BorderLayout());
            reportCustomerForm.getPanelChart().add(chart, BorderLayout.CENTER);

            reportCustomerForm.getPanelChart().revalidate();
            reportCustomerForm.getPanelChart().repaint();

        } catch (Exception e) {
            System.err.println("Lỗi cập nhật biểu đồ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Xuất báo cáo ra Excel
     */
    private void exportCustomerRevenueReport() {
        try {
            // Chuẩn bị headers
            List<String> headers = new ArrayList<>();
            DefaultTableModel model = (DefaultTableModel) reportCustomerForm.getTableCustomerRevenue().getModel();
            for (int i = 0; i < model.getColumnCount(); i++) {
                headers.add(model.getColumnName(i));
            }

            // Chuẩn bị dữ liệu
            List<List<Object>> data = new ArrayList<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                List<Object> row = new ArrayList<>();
                for (int j = 0; j < model.getColumnCount(); j++) {
                    row.add(model.getValueAt(i, j));
                }
                data.add(row);
            }

            // Tên file
            String fileName = "CUSTOMER_REVENUE_" +
                    fromDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_" +
                    toDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // Metadata
            Map<String, Object> metadata = new LinkedHashMap<>();
            metadata.put("Từ ngày:", fromDate.format(dateFormatter));
            metadata.put("Đến ngày:", toDate.format(dateFormatter));
            metadata.put("Tổng doanh thu:", reportCustomerForm.getRevenue().getText());
            metadata.put("Tổng khách hàng:", reportCustomerForm.getTxtTotalCustomers().getText());
            metadata.put("Giá trị TB/đơn:", reportCustomerForm.getTxtAverageOrderValue().getText());

            JExcel exporter = new JExcel();
            String filePath = exporter.toExcel(headers, data, 
                "Báo Cáo Doanh Thu Khách Hàng", metadata, fileName);

            if (filePath != null) {
                JOptionPane.showMessageDialog(null, 
                    "Xuất báo cáo thành công: " + filePath,
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Lỗi khi xuất báo cáo: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cập nhật khoảng thời gian
     */
    public void updateDateRange(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;

        reportCustomerForm.setChooserFromDate(fromDate);
        reportCustomerForm.setChooserToDate(toDate);

        loadCustomerRevenueData();
    }

    /**
     * Getter methods
     */
    public LocalDate getFromDate() {
        return fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }
}
