package com.pcstore.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import com.pcstore.utils.TableStyleUtil;
import com.pcstore.view.RevenueProductForm;

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
        TableStyleUtil.applyDefaultStyle(revenueProductForm.getTableRevenue());
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
}