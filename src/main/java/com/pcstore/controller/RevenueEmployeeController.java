package com.pcstore.controller;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Font;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.pcstore.service.RevenueService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.JExcel;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.TableStyleUtil;
import com.pcstore.view.RevenueEmployeeForm;
import com.pcstore.chart.Chart;
import com.pcstore.chart.ModelChart;

/**
 * Controller để quản lý thống kê doanh thu của nhân viên
 */
public class RevenueEmployeeController {
    // Singleton instance
    private static RevenueEmployeeController instance;
    
    private RevenueEmployeeForm revenueEmployeeForm;
    private RevenueService revenueService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final NumberFormat currencyFormatter = LocaleManager.getInstance().getNumberFormatter();
    private LocalDate fromDate;
    private LocalDate toDate;
    private final ResourceBundle bundle = LocaleManager.getInstance().getResourceBundle();

    
    /**
     * Lấy instance duy nhất của controller (Singleton pattern)
     * @param revenueEmployeeForm Form hiển thị thống kê doanh thu nhân viên
     * @return RevenueEmployeeController instance
     */
    public static synchronized RevenueEmployeeController getInstance(RevenueEmployeeForm revenueEmployeeForm) {
        if (instance == null) {
            instance = new RevenueEmployeeController(revenueEmployeeForm);
        } else {
            instance.revenueEmployeeForm = revenueEmployeeForm;
            instance.setupEventListeners();
            instance.setupTableStyle();
            instance.loadRevenueData();
        }
        return instance;
    }
    
    /**
     * Khởi tạo controller với form (Giao diện người dùng)
     * @param revenueEmployeeForm Form hiển thị thống kê doanh thu nhân viên
     */
    private RevenueEmployeeController(RevenueEmployeeForm revenueEmployeeForm) {
        try {
            this.revenueEmployeeForm = revenueEmployeeForm;
            this.revenueService = ServiceFactory.getRevenueService();
            
            LocalDate today = LocalDate.now();
            this.fromDate = today.withDayOfMonth(1); // Ngày đầu tháng
            this.toDate = today;
            
            revenueEmployeeForm.getTxtFromDate().setText(fromDate.format(dateFormatter));
            revenueEmployeeForm.getTxtToDate().setText(toDate.format(dateFormatter));
            
            setupEventListeners();
            setupTableStyle();
            loadRevenueData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(revenueEmployeeForm, "Lỗi khi khởi tạo controller: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Constructor with connection for testing purposes
     * @param connection Database connection
     */
    public RevenueEmployeeController(Connection connection) {
        this.revenueService = new RevenueService(connection);
        // Thiết lập ngày mặc định là ngày hiện tại
        LocalDate today = LocalDate.now();
        this.fromDate = today.withDayOfMonth(1); // Ngày đầu tháng
        this.toDate = today;
    }
    
    /**
     * Thiết lập các sự kiện cho form
     */
    private void setupEventListeners() {
        // Thêm DocumentListener để theo dõi sự thay đổi của text trong các trường
        revenueEmployeeForm.getTxtFromDate().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                handleFromDateChange();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                handleFromDateChange();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                handleFromDateChange();
            }
        });
        
        revenueEmployeeForm.getTxtToDate().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                handleToDateChange();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                handleToDateChange();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                handleToDateChange();
            }
        });
        
        // Nút xuất báo cáo giữ nguyên
        revenueEmployeeForm.getBtnExportReport().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportRevenueReport();
            }
        });
        
        // Vẫn giữ lại các nút chọn ngày (date picker) nếu cần
        revenueEmployeeForm.getBtnFromDate().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Date picker sẽ cập nhật text field và DocumentListener sẽ xử lý
            }
        });
        
        revenueEmployeeForm.getBtnToDate().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Date picker sẽ cập nhật text field và DocumentListener sẽ xử lý
            }
        });

        revenueEmployeeForm.getBtnApply().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loadRevenueData();
            }
        });
        
        revenueEmployeeForm.getTableRevenue().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double click
                    int row = revenueEmployeeForm.getTableRevenue().getSelectedRow();
                    if (row >= 0) {
                        // Lấy thông tin nhân viên từ dòng được chọn (cột 1 là ID, cột 2 là tên)
                        String employeeId = revenueEmployeeForm.getTableRevenue().getValueAt(row, 1).toString();
                        String employeeName = revenueEmployeeForm.getTableRevenue().getValueAt(row, 2).toString();
                        
                        // Hiển thị biểu đồ chi tiết
                        showEmployeeDailyChart(employeeId, employeeName);
                    }
                }
            }
        });
    }
    
    /**
     * Xử lý khi ngày bắt đầu thay đổi
     */
    private boolean isUpdatingToDate = false; // Flag để tránh vòng lặp vô hạn

    private void handleFromDateChange() {
        if (isUpdatingToDate) return;
        
        String dateStr = revenueEmployeeForm.getTxtFromDate().getText();
        if (dateStr.length() != 10) {
            // Chưa đủ định dạng ngày (dd-MM-yyyy) 
            return;
        }
        
        try {
            LocalDate newFromDate = LocalDate.parse(dateStr, dateFormatter);
            
            // Cập nhật ngày bắt đầu
            fromDate = newFromDate;
            
            // Kiểm tra nếu ngày kết thúc < ngày bắt đầu
            if (toDate.isBefore(newFromDate)) {
                isUpdatingToDate = true;
                toDate = newFromDate;
                revenueEmployeeForm.getTxtToDate().setText(toDate.format(dateFormatter));
                isUpdatingToDate = false;
                
                JOptionPane.showMessageDialog(null, 
                    "Ngày kết thúc đã được cập nhật để không nhỏ hơn ngày bắt đầu.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
            
            // Tự động cập nhật dữ liệu
            loadRevenueData();
        } catch (Exception ex) {
            // Không hiển thị thông báo lỗi liên tục khi đang nhập, 
            // chỉ khi đã nhập đủ 10 ký tự mới kiểm tra
        }
    }

    /**
     * Xử lý khi ngày kết thúc thay đổi
     */
    private void handleToDateChange() {
        if (isUpdatingToDate) return;
        
        String dateStr = revenueEmployeeForm.getTxtToDate().getText();
        if (dateStr.length() != 10) {
            // Chưa đủ định dạng ngày (dd-MM-yyyy)
            return;
        }
        
        try {
            LocalDate newToDate = LocalDate.parse(dateStr, dateFormatter);
            
            // Kiểm tra nếu ngày kết thúc < ngày bắt đầu
            if (newToDate.isBefore(fromDate)) {
                JOptionPane.showMessageDialog(null, 
                    "Ngày kết thúc không được nhỏ hơn ngày bắt đầu (" + fromDate.format(dateFormatter) + ").", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                    
                isUpdatingToDate = true;
                // Đặt lại giá trị cũ
                revenueEmployeeForm.getTxtToDate().setText(toDate.format(dateFormatter));
                isUpdatingToDate = false;
                return;
            }
            
            // Cập nhật ngày kết thúc
            toDate = newToDate;
            
            // Tự động cập nhật dữ liệu
            loadRevenueData();
        } catch (Exception ex) {
            // Không hiển thị thông báo lỗi liên tục khi đang nhập
        }
    }
    
    /**
     * Thiết lập style cho bảng
     */
    private void setupTableStyle() {
        TableStyleUtil.applyDefaultStyle(revenueEmployeeForm.getTableRevenue());
    }
    
    /**
     * Tải dữ liệu doanh thu nhân viên theo khoảng thời gian
     */
    public void loadRevenueData() {
        try {
            java.time.LocalDateTime fromDateTime = fromDate.atStartOfDay();
            java.time.LocalDateTime toDateTime = toDate.atTime(23, 59, 59);
            
            List<Map<String, Object>> revenueData = revenueService.getEmployeeRevenueData(fromDateTime, toDateTime);
            
            // Sắp xếp dữ liệu theo doanh thu giảm dần
            List<Map<String, Object>> sortedData = sortRevenueData(revenueData);
            
            DefaultTableModel model = (DefaultTableModel) revenueEmployeeForm.getTableRevenue().getModel();
            model.setRowCount(0);
            
            BigDecimal totalRevenue = BigDecimal.ZERO;
            int stt = 0;
            
            for (Map<String, Object> data : sortedData) {
                stt++;
                String employeeId = (String) data.get("employeeId");
                String employeeName = (String) data.get("employeeName");
                int productCount = (int) data.get("productCount");
                BigDecimal revenue = (BigDecimal) data.get("revenue");
                
                totalRevenue = totalRevenue.add(revenue);
                
                Object[] row = {
                    stt,
                    employeeId,
                    employeeName,
                    productCount,
                    currencyFormatter.format(revenue)
                };
                model.addRow(row);
            }
            
            revenueEmployeeForm.getLbTotal().setText(currencyFormatter.format(totalRevenue));
            
            // Cập nhật biểu đồ với dữ liệu đã sắp xếp
            updateChart(sortedData);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu doanh thu: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Xuất báo cáo doanh thu nhân viên ra file Excel
     */
    private void exportRevenueReport() {
        try {
            List<String> headers = new ArrayList<>();
            headers.add("STT");
            headers.add("Mã nhân viên");
            headers.add("Tên nhân viên");
            headers.add("Số lượng sản phẩm");
            headers.add("Doanh thu");
            
            DefaultTableModel model = (DefaultTableModel) revenueEmployeeForm.getTableRevenue().getModel();
            List<List<Object>> data = new ArrayList<>();
            
            for (int i = 0; i < model.getRowCount(); i++) {
                List<Object> row = new ArrayList<>();
                for (int j = 0; j < model.getColumnCount(); j++) {
                    row.add(model.getValueAt(i, j));
                }
                data.add(row);
            }
            
            // Tên file là "EMPLOYEE_REVENUE_" + fromDate + "_" + toDate + ".xlsx"
            String fileName = "EMPLOYEE_REVENUE_" + 
                              fromDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_" + 
                              toDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            Map<String, Object> metadata = new LinkedHashMap<>();
            metadata.put("Từ ngày", fromDate.format(dateFormatter));
            metadata.put("Đến ngày", toDate.format(dateFormatter));
            metadata.put("Tổng doanh thu", revenueEmployeeForm.getLbTotal().getText());
            
            JExcel exporter = new JExcel();
            String filePath = exporter.toExcel(headers, data, "DOANH THU NHÂN VIÊN", metadata, fileName);
            
            if (filePath != null) {
                JOptionPane.showMessageDialog(null, "Xuất báo cáo thành công: " + filePath, 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất báo cáo: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Lấy doanh thu theo nhân viên
     * @param employeeId ID nhân viên
     * @return Tổng doanh thu của nhân viên trong khoảng thời gian đã chọn
     */
    public BigDecimal getRevenueByEmployee(String employeeId) {
        try {
            java.time.LocalDateTime fromDateTime = fromDate.atStartOfDay();
            java.time.LocalDateTime toDateTime = toDate.atTime(23, 59, 59);
            return revenueService.getRevenueByEmployee(employeeId, fromDateTime, toDateTime);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy doanh thu nhân viên: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Lấy số lượng hóa đơn của nhân viên theo ID
     * @param employeeId ID nhân viên cần lấy thông tin
     * @return Số lượng hóa đơn của nhân viên trong khoảng thời gian đã chọn
     */
    public int getNumberOfSalesByEmployee(String employeeId) {
        try {
            java.time.LocalDateTime fromDateTime = fromDate.atStartOfDay();
            java.time.LocalDateTime toDateTime = toDate.atTime(23, 59, 59);
            return revenueService.getNumberOfSalesByEmployee(employeeId, fromDateTime, toDateTime);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy số lượng hóa đơn của nhân viên: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return 0;
        }
    }
    
    /**
     * Lấy danh sách nhân viên có doanh thu cao nhất trong khoảng thời gian
     * @param limit Số lượng nhân viên muốn lấy
     * @return Danh sách nhân viên có doanh thu cao nhất
     */
    public List<Map<String, Object>> getTopPerformingEmployees(int limit) {
        try {
            java.time.LocalDateTime fromDateTime = fromDate.atStartOfDay();
            java.time.LocalDateTime toDateTime = toDate.atTime(23, 59, 59);
            return revenueService.getTopPerformingEmployees(limit, fromDateTime, toDateTime);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy danh sách nhân viên doanh thu cao: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }
    
    /**
     * Cập nhật khoảng thời gian thống kê
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     */
    public void updateDateRange(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        
        if (revenueEmployeeForm != null) {
            revenueEmployeeForm.getTxtFromDate().setText(fromDate.format(dateFormatter));
            revenueEmployeeForm.getTxtToDate().setText(toDate.format(dateFormatter));
        }
        
        loadRevenueData();
    }
    
    /**
     * Thêm phương thức mới để cập nhật biểu đồ
     */
    private void updateChart(List<Map<String, Object>> revenueData) {
        try {
            
            Chart chart = (Chart) revenueEmployeeForm.getPanelChartView(); 
            chart.clear();

            revenueEmployeeForm.getPanelChartView().removeAll();
            chart = new Chart();
            revenueEmployeeForm.getPanelChartView().setLayout(new BorderLayout());
            revenueEmployeeForm.getPanelChartView().add(chart, BorderLayout.CENTER);
        
            
            chart.addLegend(bundle.getString("textRevenueMillion"), new Color(26, 162, 106));
            chart.addLegend(bundle.getString("textProductCount"), new Color(30, 113, 195));
            
            int maxToShow = Math.min(revenueData.size(), 8);
            
            // Thêm dữ liệu vào biểu đồ
            for (int i = 0; i < maxToShow; i++) {
                Map<String, Object> data = revenueData.get(i);
                String employeeName = (String) data.get("employeeName");
                BigDecimal revenue = (BigDecimal) data.get("revenue");
                int productCount = (int) data.get("productCount");
                
                double revenueInMillions = revenue.doubleValue() / 1000000.0;
                
                // Thêm dữ liệu vào biểu đồ (tên nhân viên, mảng giá trị [doanh thu, số lượng])
                chart.addData(new ModelChart(
                    employeeName, 
                    new double[]{revenueInMillions, productCount}
                ));
            }
            
            // Bắt đầu hiệu ứng biểu đồ
            chart.start();
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật biểu đồ: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Sắp xếp dữ liệu doanh thu theo thứ tự giảm dần
     * @param revenueData Dữ liệu doanh thu chưa sắp xếp
     * @return Dữ liệu doanh thu đã sắp xếp theo thứ tự giảm dần
     */
    private List<Map<String, Object>> sortRevenueData(List<Map<String, Object>> revenueData) {
        // Tạo bản sao của danh sách để không ảnh hưởng đến danh sách gốc
        List<Map<String, Object>> sortedData = new ArrayList<>(revenueData);
        
        // Sắp xếp theo doanh thu giảm dần
        sortedData.sort((data1, data2) -> {
            BigDecimal revenue1 = (BigDecimal) data1.get("revenue");
            BigDecimal revenue2 = (BigDecimal) data2.get("revenue");
            return revenue2.compareTo(revenue1); // Sắp xếp giảm dần
        });
        
        return sortedData;
    }
    
    /**
     * Lấy dữ liệu doanh thu theo ngày của một nhân viên cụ thể
     * @param employeeId ID của nhân viên cần lấy dữ liệu
     * @param fromDate Ngày bắt đầu
     * @param toDate Ngày kết thúc
     * @return Danh sách dữ liệu doanh thu theo ngày
     */
    private List<Map<String, Object>> getEmployeeDailyRevenueData(String employeeId) {
        try {
            java.time.LocalDateTime fromDateTime = fromDate.atStartOfDay();
            java.time.LocalDateTime toDateTime = toDate.atTime(23, 59, 59);
            
            return revenueService.getEmployeeDailyRevenueData(employeeId, fromDateTime, toDateTime);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy dữ liệu doanh thu theo ngày: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }
    
    /**
     * Hiển thị biểu đồ chi tiết doanh thu theo ngày của nhân viên được chọn
     * @param employeeId ID của nhân viên
     * @param employeeName Tên của nhân viên để hiển thị tiêu đề
     */
    private void showEmployeeDailyChart(String employeeId, String employeeName) {
        try {
            // Lấy dữ liệu doanh thu theo ngày
            List<Map<String, Object>> dailyData = getEmployeeDailyRevenueData(employeeId);
            
            if (dailyData.isEmpty()) {
                JOptionPane.showMessageDialog(revenueEmployeeForm, 
                    "Không có dữ liệu doanh thu theo ngày cho nhân viên này trong khoảng thời gian đã chọn", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Tìm giá trị lớn nhất của số lượng sản phẩm để scale
            int maxProductCount = 0;
            BigDecimal maxRevenue = BigDecimal.ZERO;
            
            for (Map<String, Object> data : dailyData) {
                int productCount = (int) data.get("productCount");
                BigDecimal revenue = (BigDecimal) data.get("revenue");
                
                if (productCount > maxProductCount) {
                    maxProductCount = productCount;
                }
                
                if (revenue.compareTo(maxRevenue) > 0) {
                    maxRevenue = revenue;
                }
            }
            
            // Tính tỷ lệ để hiển thị hài hòa
            double scaleFactor = maxRevenue.doubleValue() > 0 ? 
                Math.min(10.0, maxProductCount / Math.max(1, maxRevenue.doubleValue() / 1000000.0)) : 1.0;
            
            // Tạo dialog và các thành phần UI...
            javax.swing.JDialog dialog = new javax.swing.JDialog();
            dialog.setTitle("Biểu đồ doanh thu theo ngày - " + employeeName);
            dialog.setSize(800, 500);
            dialog.setLocationRelativeTo(revenueEmployeeForm);
            dialog.setModal(true);
            dialog.setLayout(new BorderLayout());
            
            // Tạo panel chứa biểu đồ
            Chart dailyChart = new Chart();
            
            // Thêm legend cho biểu đồ
            dailyChart.addLegend(bundle.getString("textRevenueMillion"), new Color(26, 162, 106));
            dailyChart.addLegend("Số lượng sản phẩm", new Color(30, 113, 195));
            
            // Thêm dữ liệu vào biểu đồ
            for (Map<String, Object> data : dailyData) {
                LocalDate date = (LocalDate) data.get("date");
                BigDecimal dailyRevenue = (BigDecimal) data.get("revenue");
                int productCount = (int) data.get("productCount");
                
                double revenueInMillions = dailyRevenue.doubleValue() / 1000000.0;
                
                // Scale số lượng sản phẩm để hiển thị cân đối với doanh thu
                double scaledProductCount = productCount / scaleFactor;
                
                String dateLabel = date.format(DateTimeFormatter.ofPattern("dd/MM"));
                
                dailyChart.addData(new ModelChart(dateLabel, new double[]{revenueInMillions, scaledProductCount}));
            }
            
            javax.swing.JPanel headerPanel = new javax.swing.JPanel();
            headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            
            javax.swing.JLabel titleLabel = new javax.swing.JLabel("Doanh thu của " + employeeName + 
                    " từ " + fromDate.format(dateFormatter) + " đến " + toDate.format(dateFormatter));
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            headerPanel.add(titleLabel);
            
            javax.swing.JLabel scaleLabel = new javax.swing.JLabel("(* Số lượng sản phẩm đã được điều chỉnh tỷ lệ để hiển thị)");
            scaleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            headerPanel.add(scaleLabel);
            
            dialog.add(headerPanel, BorderLayout.NORTH);
            dialog.add(dailyChart, BorderLayout.CENTER);
            
            javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
            javax.swing.JButton closeButton = new javax.swing.JButton("Đóng");
            closeButton.addActionListener(e -> dialog.dispose());
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dailyChart.start();
            
            dialog.setVisible(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(revenueEmployeeForm, 
                "Lỗi khi hiển thị biểu đồ: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}