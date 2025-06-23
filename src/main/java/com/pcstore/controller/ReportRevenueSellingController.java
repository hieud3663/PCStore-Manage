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

import com.pcstore.chart.PieChart;
import com.pcstore.chart.ModelPieChart;
import com.pcstore.model.Invoice;
import com.pcstore.service.InvoiceService;
import com.pcstore.service.RevenueService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.JExcel;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.TableUtils;
import com.pcstore.view.ReportRevenueSellingForm;

import raven.toast.Notifications;

/**
 * Controller để quản lý báo cáo doanh thu bán hàng
 */
public class ReportRevenueSellingController {
    // Singleton instance
    private static ReportRevenueSellingController instance;

    private ReportRevenueSellingForm revenueSellingForm;
    private InvoiceService invoiceService;
    private RevenueService revenueService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final NumberFormat currencyFormatter = LocaleManager.getInstance().getNumberFormatter();
    private LocalDate fromDate;
    private LocalDate toDate;
    private boolean isUpdatingToDate = false;

    /**
     * Lấy instance duy nhất của controller (Singleton pattern)
     */
    public static synchronized ReportRevenueSellingController getInstance(ReportRevenueSellingForm revenueSellingForm) {
        if (instance == null) {
            instance = new ReportRevenueSellingController(revenueSellingForm);
        } else {
            instance.revenueSellingForm = revenueSellingForm;
            instance.loadSellingRevenueData();
            LocalDate today = LocalDate.now();
            instance.fromDate = today.withDayOfMonth(1);
            instance.toDate = today;
            instance.setupEventListeners();
            instance.setupTableStyle();
            instance.loadSellingRevenueData();
        }
        return instance;
    }

    /**
     * Constructor private cho Singleton pattern
     */
    private ReportRevenueSellingController(ReportRevenueSellingForm revenueSellingForm) {
        try {
            this.revenueSellingForm = revenueSellingForm;
            this.invoiceService = ServiceFactory.getInvoiceService();
            this.revenueService = ServiceFactory.getRevenueService();
            LocalDate today = LocalDate.now();
            this.fromDate = today.withDayOfMonth(1);
            this.toDate = today;

            setupEventListeners();
            setupTableStyle();
            loadSellingRevenueData();

            updateDateRange(fromDate, toDate);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(revenueSellingForm,
                    ErrorMessage.REPORT_REVENUE_CONTROLLER_INIT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Constructor for testing with connection
     */
    public ReportRevenueSellingController(Connection connection) {
        this.invoiceService = new InvoiceService(connection);
        LocalDate today = LocalDate.now();
        this.fromDate = today.withDayOfMonth(1);
        this.toDate = today;
    }

    /**
     * Thiết lập các event listeners cho form
     */
    private void setupEventListeners() {
        // Event cho nút áp dụng
        revenueSellingForm.getBtnApply().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleFromDateChange();
                handleToDateChange();
            }
        });

        // Event cho nút xuất Excel
        revenueSellingForm.getBtnExportReport().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                exportSellingRevenueReport();
            }
        });

        // Event cho ComboBox thời gian - đã được xử lý trong form
        // Không cần thêm listener ở đây vì đã được handle trong form
    }

    /**
     * Xử lý khi ngày bắt đầu thay đổi
     */
    private void handleFromDateChange() {
        if (isUpdatingToDate) return;

        String dateStr = revenueSellingForm.getTxtFromDate().getText();
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
                revenueSellingForm.setChooserFromDate(tempFromDate);
                isUpdatingToDate = false;

                Notifications.getInstance().show(Notifications.Type.ERROR, 
                    ErrorMessage.REPORT_REVENUE_DATE_END_UPDATED.get());
                return;
            }

            loadSellingRevenueData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Xử lý khi ngày kết thúc thay đổi
     */
    private void handleToDateChange() {
        if (isUpdatingToDate) return;

        String dateStr = revenueSellingForm.getTxtToDate().getText();
        if (dateStr.length() != 10) {
            return;
        }

        try {
            LocalDate newToDate = LocalDate.parse(dateStr, dateFormatter);

            // Kiểm tra nếu ngày kết thúc < ngày bắt đầu
            if (newToDate.isBefore(fromDate)) {
                Notifications.getInstance().show(Notifications.Type.ERROR,
                        ErrorMessage.REPORT_REVENUE_DATE_END_BEFORE_START.format(fromDate.format(dateFormatter)));
                isUpdatingToDate = true;
                revenueSellingForm.setChooserToDate(toDate);
                isUpdatingToDate = false;
                return;
            }

            toDate = newToDate;
            loadSellingRevenueData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Thiết lập style cho bảng
     */
    private void setupTableStyle() {
        TableRowSorter<TableModel> sorter = TableUtils.applyDefaultStyle(revenueSellingForm.getTableRevenue());
        TableUtils.setNumberColumns(sorter, 3, 4, 5, 6, 7, 8); // Số lượng, Tổng tiền hàng, Giảm giá, Doanh thu, Thu khác, Thực thu
    }

    /**
     * Tải dữ liệu doanh thu bán hàng
     */
    public void loadSellingRevenueData() {
        try {
            LocalDateTime fromDateTime = fromDate.atTime(LocalTime.MIN);
            LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);
            
            List<Invoice> invoices = invoiceService.findInvoicesByDateRange(fromDateTime, toDateTime);

            DefaultTableModel model = (DefaultTableModel) revenueSellingForm.getTableRevenue().getModel();
            model.setRowCount(0);

            BigDecimal totalRevenue = BigDecimal.ZERO;
            int totalOrders = 0;
            int totalQuantity = 0;
            Map<String, Integer> productQuantityMap = new HashMap<>();

            for (Invoice invoice : invoices) {
                // Chỉ tính hóa đơn đã hoàn thành
                if (invoice.getStatus().name().equals("COMPLETED")) {
                    totalOrders++;
                    
                    // Tính tổng số lượng sản phẩm
                    int invoiceQuantity = 0;
                    if (invoice.getInvoiceDetails() != null) {
                        invoiceQuantity = invoice.getInvoiceDetails().stream()
                                .mapToInt(detail -> detail.getQuantity())
                                .sum();
                        
                        // Đếm số lượng từng sản phẩm
                        invoice.getInvoiceDetails().forEach(detail -> {
                            String productName = detail.getProduct().getProductName();
                            productQuantityMap.merge(productName, detail.getQuantity(), Integer::sum);
                        });
                    }
                    totalQuantity += invoiceQuantity;

                    BigDecimal invoiceTotal = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;
                    BigDecimal discountAmount = invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : BigDecimal.ZERO;
                    BigDecimal actualRevenue = invoiceTotal.subtract(discountAmount);
                    
                    totalRevenue = totalRevenue.add(actualRevenue);

                    // Thêm dữ liệu vào bảng
                    Object[] row = {
                        invoice.getInvoiceId(),
                        invoice.getCustomer() != null ? invoice.getCustomer().getFullName() : ErrorMessage.REPORT_REVENUE_DEFAULT_CUSTOMER.get(),
                        invoice.getEmployee() != null ? invoice.getEmployee().getFullName() : "",
                        invoiceQuantity,
                        currencyFormatter.format(invoiceTotal),
                        currencyFormatter.format(discountAmount),
                        currencyFormatter.format(actualRevenue),
                        currencyFormatter.format(BigDecimal.ZERO), // Thu khác
                        currencyFormatter.format(actualRevenue), // Thực thu
                        "" // Ghi chú
                    };
                    model.addRow(row);
                }
            }

            // Cập nhật thống kê
            updateStatistics(totalOrders, totalRevenue, productQuantityMap);
            
            // Cập nhật tổng doanh thu ở footer
            revenueSellingForm.getRevenue().setText(currencyFormatter.format(totalRevenue));

            // Cập nhật biểu đồ
            updateRevenueChart(invoices);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                ErrorMessage.REPORT_REVENUE_LOAD_DATA_ERROR.format(e.getMessage()),
                ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cập nhật các thống kê
     */
    private void updateStatistics(int totalOrders, BigDecimal totalRevenue, Map<String, Integer> productQuantityMap) {
        // Tổng số đơn hàng
        revenueSellingForm.getTxtTotalOrders().setText(String.valueOf(totalOrders));

        // Tổng doanh thu
        revenueSellingForm.getTxtTotalRevenue().setText(currencyFormatter.format(totalRevenue));

        // Giá trị trung bình/đơn
        BigDecimal averageOrderValue = totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
        revenueSellingForm.getTxtAverageOrderValue().setText(currencyFormatter.format(averageOrderValue));

        // Tính lợi nhuận ròng
        try {
            LocalDateTime fromDateTime = fromDate.atTime(LocalTime.MIN);
            LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);
            
            Map<String, Object> profitData = revenueService.calculateTotalProfit(fromDate, toDate);
            BigDecimal netProfit = (BigDecimal) profitData.get("totalProfit");
            if (netProfit == null) {
                netProfit = BigDecimal.ZERO;
            }
            
            revenueSellingForm.getTxtProfit().setText(currencyFormatter.format(netProfit));
        } catch (Exception e) {
            e.printStackTrace();
            revenueSellingForm.getTxtProfit().setText("0");
        }
    }

    /**
     * Cập nhật biểu đồ doanh thu
     */
    private void updateRevenueChart(List<Invoice> invoices) {
        try {
            revenueSellingForm.getPanelChart().removeAll();

            PieChart pieChart = new PieChart();
            pieChart.setChartType(PieChart.PeiChartType.DONUT_CHART);

            // Tính doanh thu theo ngày
            Map<LocalDate, BigDecimal> dailyRevenue = new LinkedHashMap<>();
            
            for (Invoice invoice : invoices) {
                if (invoice.getStatus().name().equals("COMPLETED")) {
                    LocalDate invoiceDate = invoice.getInvoiceDate().toLocalDate();
                    BigDecimal revenue = invoice.getTotalAmount().subtract(
                        invoice.getDiscountAmount() != null ? invoice.getDiscountAmount() : BigDecimal.ZERO);
                    
                    dailyRevenue.merge(invoiceDate, revenue, BigDecimal::add);
                }
            }

            Color[] colorArray = {
                new Color(26, 162, 106), // Xanh lá
                new Color(30, 113, 195), // Xanh dương
                new Color(255, 153, 0),  // Cam
                new Color(255, 51, 51),  // Đỏ
                new Color(153, 51, 255), // Tím
                new Color(153, 153, 153) // Xám
            };

            int colorIndex = 0;
            int maxEntries = 7; // Hiển thị tối đa 7 ngày
            int entryCount = 0;
            BigDecimal othersRevenue = BigDecimal.ZERO;

            for (Map.Entry<LocalDate, BigDecimal> entry : dailyRevenue.entrySet()) {
                if (entryCount < maxEntries) {
                    pieChart.addData(new ModelPieChart(
                        entry.getKey().format(DateTimeFormatter.ofPattern("dd/MM")),
                        entry.getValue().doubleValue(),
                        colorArray[colorIndex % colorArray.length]
                    ));
                    colorIndex++;
                } else {
                    othersRevenue = othersRevenue.add(entry.getValue());
                }
                entryCount++;
            }

            if (othersRevenue.compareTo(BigDecimal.ZERO) > 0) {
                pieChart.addData(new ModelPieChart(
                    ErrorMessage.REPORT_REVENUE_CHART_OTHER_LABEL.get(),
                    othersRevenue.doubleValue(),
                    colorArray[colorArray.length - 1]
                ));
            }

            revenueSellingForm.getPanelChart().setLayout(new BorderLayout());
            revenueSellingForm.getPanelChart().add(pieChart, BorderLayout.CENTER);

            revenueSellingForm.getPanelChart().revalidate();
            revenueSellingForm.getPanelChart().repaint();

        } catch (Exception e) {
            System.err.println(ErrorMessage.REPORT_REVENUE_CHART_UPDATE_ERROR.format(e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * Xuất báo cáo ra Excel
     */
    private void exportSellingRevenueReport() {
        try {
            // Chuẩn bị headers
            List<String> headers = new ArrayList<>();
            DefaultTableModel model = (DefaultTableModel) revenueSellingForm.getTableRevenue().getModel();
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
            String fileName = ErrorMessage.REPORT_REVENUE_EXPORT_FILENAME_PREFIX.get() +
                    fromDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_" +
                    toDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // Metadata
            Map<String, Object> metadata = new LinkedHashMap<>();
            metadata.put(ErrorMessage.REPORT_REVENUE_EXPORT_FROM_DATE.get(), fromDate.format(dateFormatter));
            metadata.put(ErrorMessage.REPORT_REVENUE_EXPORT_TO_DATE.get(), toDate.format(dateFormatter));
            metadata.put(ErrorMessage.REPORT_REVENUE_EXPORT_TOTAL_REVENUE.get(), revenueSellingForm.getRevenue().getText());
            metadata.put(ErrorMessage.REPORT_REVENUE_EXPORT_TOTAL_ORDERS.get(), revenueSellingForm.getTxtTotalOrders().getText());
            metadata.put(ErrorMessage.REPORT_REVENUE_EXPORT_AVERAGE_ORDER.get(), revenueSellingForm.getTxtAverageOrderValue().getText());

            JExcel exporter = new JExcel();
            String filePath = exporter.toExcel(headers, data, 
                ErrorMessage.REPORT_REVENUE_EXPORT_SHEET_TITLE.get(), metadata, fileName);

            if (filePath != null) {
                JOptionPane.showMessageDialog(null, 
                    ErrorMessage.REPORT_REVENUE_EXPORT_SUCCESS.format(filePath),
                    ErrorMessage.INFO_TITLE.get(), JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                ErrorMessage.REPORT_REVENUE_EXPORT_ERROR.format(e.getMessage()),
                ErrorMessage.ERROR_TITLE.get(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cập nhật khoảng thời gian
     */
    public void updateDateRange(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;

        revenueSellingForm.setChooserFromDate(fromDate);
        revenueSellingForm.setChooserToDate(toDate);

        loadSellingRevenueData();
    }

    /**
     * Lấy tổng doanh thu trong khoảng thời gian
     */
    public BigDecimal getTotalRevenue() {
        LocalDateTime fromDateTime = fromDate.atTime(LocalTime.MIN);
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);
        return invoiceService.calculateRevenue(fromDateTime, toDateTime);
    }

    /**
     * Lấy số lượng đơn hàng trong khoảng thời gian
     */
    public int getTotalOrders() {
        LocalDateTime fromDateTime = fromDate.atTime(LocalTime.MIN);
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);
        return invoiceService.countInvoicesByDateRange(fromDateTime, toDateTime);
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
