package com.pcstore.controller;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.pcstore.service.RevenueService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.JExcel;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.TableUtils;
import com.pcstore.view.RevenueProductForm;

// Thêm các import sau
import java.awt.BorderLayout;
import java.awt.Color;
import com.pcstore.chart.PieChart;
import com.pcstore.chart.ModelPieChart;

/**
 * Controller để quản lý thống kê doanh thu sản phẩm
 */
public class RevenueProductController {
    // Singleton instance
    private static RevenueProductController instance;
  
    
    private RevenueProductForm revenueProductForm;
    private RevenueService revenueService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final NumberFormat currencyFormatter = LocaleManager.getInstance().getNumberFormatter();
    private LocalDate fromDate;
    private LocalDate toDate;
    

    /**
     * Lấy instance duy nhất của controller (Singleton pattern)
     * @param revenueProductForm Form hiển thị thống kê doanh thu sản phẩm
     * @return RevenueProductController instance
     */
    public static synchronized RevenueProductController getInstance(RevenueProductForm revenueProductForm) {
        if (instance == null) {
            instance = new RevenueProductController(revenueProductForm);
        } else {
            instance.revenueProductForm = revenueProductForm;
            instance.setupEventListeners();
            instance.setupTableStyle();
            instance.loadRevenueData();
        }
        return instance;
    }
    
    /**
     * Khởi tạo controller với form (Giao diện người dùng)
     * @param revenueProductForm Form hiển thị thống kê doanh thu sản phẩm
     */
    private RevenueProductController(RevenueProductForm revenueProductForm) {
        try {
            this.revenueProductForm = revenueProductForm;
            this.revenueService = ServiceFactory.getRevenueService();
            
            LocalDate today = LocalDate.now();
            this.fromDate = today.withDayOfMonth(1);
            this.toDate = today;
            
            revenueProductForm.getTxtFromDate().setText(fromDate.format(dateFormatter));
            revenueProductForm.getTxtToDate().setText(toDate.format(dateFormatter));
            
            setupEventListeners();
            setupTableStyle();
            loadRevenueData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(revenueProductForm, "Lỗi khi khởi tạo controller: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Constructor with connection for testing purposes
     * @param connection Database connection
     */
    public RevenueProductController(Connection connection) {
        this.revenueService = new RevenueService(connection);
        LocalDate today = LocalDate.now();
        this.fromDate = today.withDayOfMonth(1); 
        this.toDate = today;
    }
    
    /**
     * Thiết lập các sự kiện cho form
     */
    private void setupEventListeners() {
    
        
        // Thêm document listener cho text field ngày bắt đầu
        revenueProductForm.getTxtFromDate().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
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
        
        // Thêm document listener cho text field ngày kết thúc
        revenueProductForm.getTxtToDate().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
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
        
        // Sử dụng MouseListener thay vì ActionListener cho nút xuất báo cáo
        revenueProductForm.getBtnExportReport().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exportRevenueReport();
            }
        });

        revenueProductForm.getBtnApply().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loadRevenueData();
            }
        });
                     
        
    }
    
   
    
    private boolean isUpdatingToDate = false; // Flag để tránh vòng lặp vô hạn

    /**
     * Xử lý khi ngày bắt đầu thay đổi
     */
    private void handleFromDateChange() {
        if (isUpdatingToDate) return;
        
        String dateStr = revenueProductForm.getTxtFromDate().getText();
        if (dateStr.length() != 10) {
            // Chưa đủ định dạng ngày (dd-MM-yyyy) 
            return;
        }
        
        try {
            LocalDate newFromDate = LocalDate.parse(dateStr, dateFormatter);
            
            // Cập nhật ngày bắt đầu
            fromDate = newFromDate;
            
            // Kiểm tra nếu toDate nhỏ hơn fromDate thì cập nhật toDate = fromDate
            if (toDate.isBefore(newFromDate)) {
                isUpdatingToDate = true;
                toDate = newFromDate;
                revenueProductForm.getTxtToDate().setText(toDate.format(dateFormatter));
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
        
        String dateStr = revenueProductForm.getTxtToDate().getText();
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
                revenueProductForm.getTxtToDate().setText(toDate.format(dateFormatter));
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

    //setupTable
    private void setupTableStyle(){
        TableUtils.applyDefaultStyle(revenueProductForm.getTableRevenue());
    }

    /**
     * Tải dữ liệu doanh thu sản phẩm theo khoảng thời gian
     */
    public void loadRevenueData() {
        try {
            List<Map<String, Object>> revenueData = revenueService.getRevenueData(fromDate, toDate);
            
            DefaultTableModel model = (DefaultTableModel) revenueProductForm.getTableRevenue().getModel();
            model.setRowCount(0);
            
            BigDecimal totalRevenue = BigDecimal.ZERO;
            int stt = 0;
            
            for (Map<String, Object> data : revenueData) {
                stt++;
                String employeeName = (String) data.get("employeeName");
                String productId = (String) data.get("productId");
                String productName = (String) data.get("productName");
                int quantity = (int) data.get("quantity");
                BigDecimal unitPrice = (BigDecimal) data.get("unitPrice");
                BigDecimal revenue = (BigDecimal) data.get("revenue");
                
                totalRevenue = totalRevenue.add(revenue);
                
                Object[] row = {
                    stt,
                    employeeName,
                    productId,
                    productName,
                    quantity,
                    currencyFormatter.format(unitPrice),
                    currencyFormatter.format(revenue)
                };
                model.addRow(row);
            }
            
            revenueProductForm.getLbTotal().setText(currencyFormatter.format(totalRevenue));
            
            // Cập nhật biểu đồ tròn với dữ liệu
            updatePieChart(revenueData, totalRevenue);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu doanh thu: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Xuất báo cáo doanh thu sản phẩm ra file Excel
     */
    private void exportRevenueReport() {
        try {
            // Chuẩn bị dữ liệu cho file Excel
            List<String> headers = new ArrayList<>();
            headers.add("STT");
            headers.add("Nhân viên");
            headers.add("Mã sản phẩm");
            headers.add("Tên sản phẩm");
            headers.add("Số lượng");
            headers.add("Đơn giá");
            headers.add("Thành tiền");
            
            // Lấy dữ liệu từ bảng
            DefaultTableModel model = (DefaultTableModel) revenueProductForm.getTableRevenue().getModel();
            List<List<Object>> data = new ArrayList<>();
            
            for (int i = 0; i < model.getRowCount(); i++) {
                List<Object> row = new ArrayList<>();
                for (int j = 0; j < model.getColumnCount(); j++) {
                    row.add(model.getValueAt(i, j));
                }
                data.add(row);
            }
            
            // Tên file là "PRODUCT_REVENUE_" + fromDate + "_" + toDate + ".xlsx"
            String fileName = "PRODUCT_REVENUE_" + 
                              fromDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_" + 
                              toDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            Map<String, Object> metadata = new LinkedHashMap<>();
            metadata.put("Từ ngày", fromDate.format(dateFormatter));
            metadata.put("Đến ngày", toDate.format(dateFormatter));
            metadata.put("Tổng doanh thu", revenueProductForm.getLbTotal().getText());
            
            JExcel exporter = new JExcel();
            String filePath = exporter.toExcel(headers, data, "DOANH THU SẢN PHẨM", metadata, fileName);
            
            if (filePath != null) {
                JOptionPane.showMessageDialog(null, "Xuất báo cáo thành công: " + filePath, 
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất báo cáo: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Lấy dữ liệu doanh thu sản phẩm theo ID
     * @param productId ID sản phẩm cần lấy doanh thu
     * @return Tổng doanh thu của sản phẩm trong khoảng thời gian đã chọn
     */
    public BigDecimal getRevenueByProduct(String productId) {
        try {
            return revenueService.getRevenueByProduct(productId, fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy doanh thu sản phẩm: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Lấy số lượng đã bán của sản phẩm theo ID
     * @param productId ID sản phẩm cần lấy thông tin
     * @return Số lượng đã bán của sản phẩm trong khoảng thời gian đã chọn
     */
    public int getQuantitySoldByProduct(String productId) {
        try {
            return revenueService.getQuantitySoldByProduct(productId, fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy số lượng sản phẩm đã bán: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return 0;
        }
    }
    
    /**
     * Lấy top sản phẩm bán chạy nhất trong khoảng thời gian
     * @param limit Số lượng sản phẩm muốn lấy
     * @return Danh sách sản phẩm bán chạy nhất
     */
    public List<Map<String, Object>> getTopSellingProducts(int limit) {
        try {
            return revenueService.getTopSellingProducts(limit, fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy danh sách sản phẩm bán chạy: " + e.getMessage(), 
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
        
        revenueProductForm.getTxtFromDate().setText(fromDate.format(dateFormatter));
        revenueProductForm.getTxtToDate().setText(toDate.format(dateFormatter));
        
        loadRevenueData();
    }
    
    /**
     * Cập nhật biểu đồ tròn thể hiện phần trăm doanh thu sản phẩm
     * @param revenueData Dữ liệu doanh thu sản phẩm
     * @param totalRevenue Tổng doanh thu
     */
    private void updatePieChart(List<Map<String, Object>> revenueData, BigDecimal totalRevenue) {
        try {
            revenueProductForm.getPanelChart().removeAll();
            
            PieChart pieChart = new PieChart();
            pieChart.setChartType(PieChart.PeiChartType.DONUT_CHART); 
            
            List<Map<String, Object>> sortedData = new ArrayList<>(revenueData);
            sortedData.sort((data1, data2) -> {
                BigDecimal revenue1 = (BigDecimal) data1.get("revenue");
                BigDecimal revenue2 = (BigDecimal) data2.get("revenue");
                return revenue2.compareTo(revenue1);
            });
            
            int maxToShow = Math.min(sortedData.size(), 5);
            
            Color[] colorArray = {
                new Color(26, 162, 106),   // Xanh lá
                new Color(30, 113, 195),   // Xanh dương
                new Color(255, 153, 0),    // Cam
                new Color(255, 51, 51),    // Đỏ
                new Color(153, 51, 255),   // Tím
                new Color(153, 153, 153)   // Xám (cho mục "Khác")
            };
            
            BigDecimal othersRevenue = BigDecimal.ZERO;
            
            for (int i = 0; i < sortedData.size(); i++) {
                Map<String, Object> data = sortedData.get(i);
                String productName = (String) data.get("productName");
                BigDecimal revenue = (BigDecimal) data.get("revenue");
                
                if (i < maxToShow) {
                    // Lấy tên sản phẩm, cắt bớt nếu quá dài
                    String displayName = productName;
                    if (displayName.length() > 20) {
                        displayName = displayName.substring(0, 17) + "...";
                    }
                    
                    pieChart.addData(new ModelPieChart(
                        displayName,
                        revenue.doubleValue(),
                        colorArray[i]
                    ));
                } else {
                    othersRevenue = othersRevenue.add(revenue);
                }
            }
            
            if (othersRevenue.compareTo(BigDecimal.ZERO) > 0) {
                pieChart.addData(new ModelPieChart(
                    "Khác",
                    othersRevenue.doubleValue(),
                    colorArray[5]
                ));
            }
            
            revenueProductForm.getPanelChart().setLayout(new BorderLayout());
            revenueProductForm.getPanelChart().add(pieChart, BorderLayout.CENTER);
            
            revenueProductForm.getPanelChart().revalidate();
            revenueProductForm.getPanelChart().repaint();
            
            setupPieChartClickEvent(pieChart, sortedData, maxToShow);
            
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật biểu đồ tròn: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Hiển thị chi tiết sản phẩm khi người dùng nhấp vào biểu đồ
     * @param pieChart Biểu đồ tròn
     * @param sortedData Dữ liệu đã sắp xếp
     * @param maxToShow Số lượng tối đa hiển thị
     */
    private void setupPieChartClickEvent(PieChart pieChart, List<Map<String, Object>> sortedData, int maxToShow) {
        pieChart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedIndex = pieChart.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < maxToShow) {
                    Map<String, Object> data = sortedData.get(selectedIndex);
                    String productId = (String) data.get("productId");
                    String productName = (String) data.get("productName");
                    BigDecimal revenue = (BigDecimal) data.get("revenue");
                    int quantity = (int) data.get("quantity");
                    
                    // Hiển thị thông tin chi tiết sản phẩm
                    showProductDetail(productId, productName, revenue, quantity);
                } else if (selectedIndex == maxToShow && sortedData.size() > maxToShow) {
                    showOtherProductsList(sortedData, maxToShow);
                }
            }
        });
    }

    /**
     * Hiển thị thông tin chi tiết về sản phẩm
     */
    private void showProductDetail(String productId, String productName, BigDecimal revenue, int quantity) {
        String message = "Sản phẩm: " + productName + "\n"
                       + "Mã sản phẩm: " + productId + "\n"
                       + "Doanh thu: " + currencyFormatter.format(revenue) + "\n"
                       + "Số lượng đã bán: " + quantity;
        
        JOptionPane.showMessageDialog(revenueProductForm, message, "Chi tiết sản phẩm", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Hiển thị danh sách các sản phẩm khác
     */
    private void showOtherProductsList(List<Map<String, Object>> sortedData, int maxToShow) {
        StringBuilder message = new StringBuilder("Các sản phẩm khác:\n\n");
        
        for (int i = maxToShow; i < sortedData.size(); i++) {
            Map<String, Object> data = sortedData.get(i);
            String productName = (String) data.get("productName");
            BigDecimal revenue = (BigDecimal) data.get("revenue");
            
            message.append(productName)
                   .append(" - ")
                   .append(currencyFormatter.format(revenue))
                   .append("\n");
        }
        
        JOptionPane.showMessageDialog(revenueProductForm, message.toString(), 
                "Danh sách sản phẩm khác", JOptionPane.INFORMATION_MESSAGE);
    }
}