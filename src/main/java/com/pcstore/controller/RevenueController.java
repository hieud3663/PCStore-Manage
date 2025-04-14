package com.pcstore.controller;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Product;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.repository.impl.InvoiceDetailRepository;
import com.pcstore.repository.impl.InvoiceRepository;
import com.pcstore.repository.impl.ProductRepository;
import com.pcstore.service.InvoiceDetailService;
import com.pcstore.service.InvoiceService;
import com.pcstore.service.ProductService;
import com.pcstore.view.RevenueDailyForm;
import com.pcstore.view.RevenueMonthlyForm;
import com.toedter.calendar.JDateChooser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Controller xử lý logic của các form báo cáo doanh thu
 */
public class RevenueController {
    private InvoiceService invoiceService;
    private InvoiceDetailService invoiceDetailService;
    private ProductService productService;
    
    private RevenueDailyForm revenueDailyForm;
    private RevenueMonthlyForm revenueMonthlyForm;
    
    private LocalDate currentSelectedDate;
    private LocalDate currentSelectedMonth;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");
    
    /**
     * Khởi tạo controller với connection
     * @param connection Kết nối đến database
     */
    public RevenueController(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        
        try {
            // Khởi tạo RepositoryFactory
            RepositoryFactory repositoryFactory = new RepositoryFactory(connection);
            
            // Khởi tạo các repositories
            InvoiceRepository invoiceRepository = repositoryFactory.getInvoiceRepository();
            InvoiceDetailRepository invoiceDetailRepository = repositoryFactory.getInvoiceDetailRepository();
            ProductRepository productRepository = repositoryFactory.getProductRepository();
            
            // Khởi tạo các services
            this.invoiceService = new InvoiceService(invoiceRepository, productRepository);
            this.invoiceDetailService = new InvoiceDetailService(
                invoiceDetailRepository,
                productRepository,
                invoiceRepository
            );
            this.productService = new ProductService(productRepository);
            
            // Mặc định là ngày hiện tại
            this.currentSelectedDate = LocalDate.now();
            this.currentSelectedMonth = LocalDate.now();
            
            System.out.println("RevenueController: Khởi tạo thành công");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize RevenueController: " + e.getMessage(), e);
        }
    }
    
    /**
     * Thiết lập form doanh thu theo ngày để hiển thị
     * @param revenueDailyForm Form doanh thu theo ngày
     */
    public void setRevenueDailyForm(RevenueDailyForm revenueDailyForm) {
        this.revenueDailyForm = revenueDailyForm;
        setupRevenueDailyForm();
    }
    
    /**
     * Thiết lập form doanh thu theo tháng để hiển thị
     * @param revenueMonthlyForm Form doanh thu theo tháng
     */
    public void setRevenueMonthlyForm(RevenueMonthlyForm revenueMonthlyForm) {
        this.revenueMonthlyForm = revenueMonthlyForm;
        setupRevenueMonthlyForm();
    }
    
    /**
     * Thiết lập các thành phần và sự kiện cho form doanh thu theo ngày
     */
    private void setupRevenueDailyForm() {
        if (revenueDailyForm != null) {
            // Thiết lập ngày hiện tại
            currentSelectedDate = LocalDate.now();
            revenueDailyForm.getDateLabel().setText(currentSelectedDate.format(DATE_FORMATTER));
            
            // Tải dữ liệu cho ngày hiện tại
            loadDailyRevenueData(currentSelectedDate);
            
            // Thêm sự kiện khi nhấn nút chọn ngày
            revenueDailyForm.getDateButton().addActionListener(e -> showDatePicker());
            
            // Thêm sự kiện xuất báo cáo
            revenueDailyForm.getExportButton().addActionListener(e -> exportDailyReport());
        }
    }
    
    /**
     * Thiết lập các thành phần và sự kiện cho form doanh thu theo tháng
     */
    private void setupRevenueMonthlyForm() {
        if (revenueMonthlyForm != null) {
            // Thiết lập tháng hiện tại
            currentSelectedMonth = LocalDate.now().withDayOfMonth(1);
            revenueMonthlyForm.getMonthLabel().setText(currentSelectedMonth.format(MONTH_FORMATTER));
            
            // Tải dữ liệu cho tháng hiện tại
            loadMonthlyRevenueData(currentSelectedMonth);
            
            // Thêm sự kiện khi nhấn nút chọn tháng
            revenueMonthlyForm.getMonthButton().addActionListener(e -> showMonthPicker());
            
            // Thêm sự kiện xuất báo cáo
            revenueMonthlyForm.getExportButton().addActionListener(e -> exportMonthlyReport());
        }
    }
    
    /**
     * Hiển thị dialog chọn ngày
     */
    private void showDatePicker() {
        // Sử dụng JDateChooser từ thư viện JCalendar
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDate(java.sql.Date.valueOf(currentSelectedDate));
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Chọn ngày");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            if (dateChooser.getDate() != null) {
                currentSelectedDate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                revenueDailyForm.getDateLabel().setText(currentSelectedDate.format(DATE_FORMATTER));
                loadDailyRevenueData(currentSelectedDate);
                dialog.dispose();
            }
        });
        
        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(dateChooser, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(revenueDailyForm);
        dialog.setVisible(true);
    }
    
    /**
     * Hiển thị dialog chọn tháng
     */
    private void showMonthPicker() {
        // Tạo dialog cho chọn tháng và năm
        JDialog dialog = new JDialog();
        dialog.setTitle("Chọn tháng");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        
        // Tạo panel cho chọn tháng và năm
        JPanel panel = new JPanel(new GridLayout(2, 2));
        
        JLabel monthLabel = new JLabel("Tháng:");
        JSpinner monthSpinner = new JSpinner(new SpinnerNumberModel(
                currentSelectedMonth.getMonthValue(), 1, 12, 1));
        
        JLabel yearLabel = new JLabel("Năm:");
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(
                currentSelectedMonth.getYear(), 2000, 2100, 1));
        
        panel.add(monthLabel);
        panel.add(monthSpinner);
        panel.add(yearLabel);
        panel.add(yearSpinner);
        
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            int month = (int) monthSpinner.getValue();
            int year = (int) yearSpinner.getValue();
            
            currentSelectedMonth = LocalDate.of(year, month, 1);
            revenueMonthlyForm.getMonthLabel().setText(currentSelectedMonth.format(MONTH_FORMATTER));
            loadMonthlyRevenueData(currentSelectedMonth);
            dialog.dispose();
        });
        
        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(revenueMonthlyForm);
        dialog.setVisible(true);
    }
    
    /**
     * Tải dữ liệu doanh thu theo ngày
     * @param date Ngày cần tải dữ liệu
     */
    public void loadDailyRevenueData(LocalDate date) {
        try {
            // Chuyển đổi LocalDate thành LocalDateTime
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);
            
            // Gọi service để lấy dữ liệu
            List<InvoiceDetail> details = invoiceDetailService.findByDateRange(startOfDay, endOfDay);
            
            // Tính tổng doanh thu
            BigDecimal totalRevenue = calculateTotalRevenue(details);
            BigDecimal totalProfit = calculateTotalProfit(details);
            
            // Cập nhật giao diện
            if (revenueDailyForm != null) {
                revenueDailyForm.getRevenueLabel().setText(formatCurrency(totalRevenue));
                updateDailyRevenueTable(details);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(revenueDailyForm, 
                    "Lỗi khi tải dữ liệu doanh thu: " + e.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Tải dữ liệu doanh thu theo tháng
     * @param month Tháng cần tải dữ liệu
     */
    public void loadMonthlyRevenueData(LocalDate month) {
        try {
            // Chuyển đổi LocalDate thành LocalDateTime cho đầu tháng và cuối tháng
            LocalDateTime startOfMonth = month.atStartOfDay();
            LocalDateTime endOfMonth = month.plusMonths(1).atStartOfDay().minusNanos(1);
            
            // Gọi service để lấy dữ liệu
            List<InvoiceDetail> details = invoiceDetailService.findByDateRange(startOfMonth, endOfMonth);
            
            // Tính tổng doanh thu và lợi nhuận
            BigDecimal totalRevenue = calculateTotalRevenue(details);
            BigDecimal totalProfit = calculateTotalProfit(details);
            
            // Cập nhật giao diện
            if (revenueMonthlyForm != null) {
                revenueMonthlyForm.getTotalRevenueLabel().setText(formatCurrency(totalRevenue));
                revenueMonthlyForm.getTotalProfitLabel().setText(formatCurrency(totalProfit));
                updateMonthlyRevenueTable(details);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(revenueMonthlyForm, 
                    "Lỗi khi tải dữ liệu doanh thu: " + e.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Tính tổng doanh thu từ danh sách chi tiết hóa đơn
     * @param details Danh sách chi tiết hóa đơn
     * @return Tổng doanh thu
     */
    private BigDecimal calculateTotalRevenue(List<InvoiceDetail> details) {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceDetail detail : details) {
            total = total.add(detail.getTotalAmount());
        }
        return total;
    }
    
    /**
     * Tính tổng lợi nhuận từ danh sách chi tiết hóa đơn
     * @param details Danh sách chi tiết hóa đơn
     * @return Tổng lợi nhuận
     */
    private BigDecimal calculateTotalProfit(List<InvoiceDetail> details) {
        // Đây chỉ là ví dụ đơn giản về cách tính lợi nhuận
        // Trong thực tế, bạn cần biết giá nhập của sản phẩm để tính lợi nhuận chính xác
        BigDecimal totalRevenue = calculateTotalRevenue(details);
        return totalRevenue.multiply(BigDecimal.valueOf(0.3)); // Giả sử lợi nhuận là 30% doanh thu
    }
    
    /**
     * Cập nhật bảng doanh thu theo ngày
     * @param details Danh sách chi tiết hóa đơn
     */
    private void updateDailyRevenueTable(List<InvoiceDetail> details) {
        if (revenueDailyForm == null) return;
        
        DefaultTableModel model = (DefaultTableModel) revenueDailyForm.getRevenueTable().getModel();
        model.setRowCount(0);
        
        int stt = 1;
        Map<String, ProductSummary> productSummary = new HashMap<>();
        
        // Tổng hợp các sản phẩm giống nhau
        for (InvoiceDetail detail : details) {
            Product product = detail.getProduct();
            String productId = product.getProductId();
            
            if (productSummary.containsKey(productId)) {
                // Nếu sản phẩm đã có trong map, tăng số lượng lên
                productSummary.get(productId).increaseQuantity(detail.getQuantity());
            } else {
                // Nếu sản phẩm chưa có, thêm mới vào map
                ProductSummary summary = new ProductSummary(
                    productId,
                    product.getProductName(),
                    detail.getQuantity(),
                    detail.getUnitPrice(),
                    detail.getTotalAmount()
                );
                productSummary.put(productId, summary);
            }
        }
        
        // Hiển thị lên bảng
        for (ProductSummary summary : productSummary.values()) {
            model.addRow(new Object[]{
                stt++,
                summary.getProductId(),
                summary.getProductName(),
                summary.getQuantity(),
                formatCurrency(summary.getUnitPrice()),
                formatCurrency(summary.getTotalAmount())
            });
        }
    }
    
    /**
     * Cập nhật bảng doanh thu theo tháng
     * @param details Danh sách chi tiết hóa đơn
     */
    private void updateMonthlyRevenueTable(List<InvoiceDetail> details) {
        if (revenueMonthlyForm == null) return;
        
        DefaultTableModel model = (DefaultTableModel) revenueMonthlyForm.getRevenueTable().getModel();
        model.setRowCount(0);
        
        int stt = 1;
        Map<String, ProductSummary> productSummary = new HashMap<>();
        
        // Tổng hợp các sản phẩm giống nhau
        for (InvoiceDetail detail : details) {
            Product product = detail.getProduct();
            String productId = product.getProductId();
            
            if (productSummary.containsKey(productId)) {
                // Nếu sản phẩm đã có trong map, tăng số lượng lên
                productSummary.get(productId).increaseQuantity(detail.getQuantity());
            } else {
                // Nếu sản phẩm chưa có, thêm mới vào map
                ProductSummary summary = new ProductSummary(
                    productId,
                    product.getProductName(),
                    detail.getQuantity(),
                    detail.getUnitPrice(),
                    detail.getTotalAmount()
                );
                productSummary.put(productId, summary);
            }
        }
        
        // Hiển thị lên bảng
        for (ProductSummary summary : productSummary.values()) {
            model.addRow(new Object[]{
                stt++,
                summary.getProductId(),
                summary.getProductName(),
                summary.getQuantity(),
                formatCurrency(summary.getUnitPrice()),
                formatCurrency(summary.getTotalAmount())
            });
        }
    }
    
    /**
     * Xuất báo cáo doanh thu theo ngày ra file Excel
     */
    private void exportDailyReport() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu báo cáo doanh thu");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
            
            String defaultFileName = "BaoCaoDoanhThu_" + currentSelectedDate.format(DateTimeFormatter.ofPattern("dd_MM_yyyy")) + ".xlsx";
            fileChooser.setSelectedFile(new File(defaultFileName));
            
            int userSelection = fileChooser.showSaveDialog(revenueDailyForm);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                
                if (!filePath.endsWith(".xlsx")) {
                    filePath += ".xlsx";
                    fileToSave = new File(filePath);
                }
                
                // Xuất báo cáo ra file Excel
                exportToExcel(
                    revenueDailyForm.getRevenueTable(), 
                    fileToSave, 
                    "Báo cáo doanh thu ngày " + currentSelectedDate.format(DATE_FORMATTER),
                    revenueDailyForm.getRevenueLabel().getText()
                );
                
                JOptionPane.showMessageDialog(revenueDailyForm, 
                        "Xuất báo cáo thành công!\nĐường dẫn: " + filePath, 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(revenueDailyForm, 
                    "Có lỗi xảy ra khi xuất báo cáo: " + e.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Xuất báo cáo doanh thu theo tháng ra file Excel
     */
    private void exportMonthlyReport() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu báo cáo doanh thu");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
            
            String defaultFileName = "BaoCaoDoanhThu_Thang_" + currentSelectedMonth.format(DateTimeFormatter.ofPattern("MM_yyyy")) + ".xlsx";
            fileChooser.setSelectedFile(new File(defaultFileName));
            
            int userSelection = fileChooser.showSaveDialog(revenueMonthlyForm);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                
                if (!filePath.endsWith(".xlsx")) {
                    filePath += ".xlsx";
                    fileToSave = new File(filePath);
                }
                
                // Xuất báo cáo ra file Excel
                exportToExcel(
                    revenueMonthlyForm.getRevenueTable(), 
                    fileToSave, 
                    "Báo cáo doanh thu tháng " + currentSelectedMonth.format(MONTH_FORMATTER),
                    "Doanh thu: " + revenueMonthlyForm.getTotalRevenueLabel().getText() + 
                    ", Lợi nhuận: " + revenueMonthlyForm.getTotalProfitLabel().getText()
                );
                
                JOptionPane.showMessageDialog(revenueMonthlyForm, 
                        "Xuất báo cáo thành công!\nĐường dẫn: " + filePath, 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(revenueMonthlyForm, 
                    "Có lỗi xảy ra khi xuất báo cáo: " + e.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Xuất bảng dữ liệu ra file Excel
     * @param table Bảng dữ liệu cần xuất
     * @param file File đích
     * @param sheetName Tên sheet
     * @param summaryText Thông tin tổng kết
     */
    private void exportToExcel(JTable table, File file, String sheetName, String summaryText) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        
        // Tạo header của bảng
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < table.getColumnCount(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(table.getColumnName(i));
        }
        
        // Đổ dữ liệu từ JTable vào Excel
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < model.getColumnCount(); j++) {
                Cell cell = row.createCell(j);
                Object value = model.getValueAt(i, j);
                if (value != null) {
                    cell.setCellValue(value.toString());
                }
            }
        }
        
        // Thêm thông tin tổng kết
        Row summaryRow = sheet.createRow(model.getRowCount() + 2);
        Cell summaryCell = summaryRow.createCell(0);
        summaryCell.setCellValue(summaryText);
        
        // Auto-size các cột
        for (int i = 0; i < table.getColumnCount(); i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Lưu file
        try (FileOutputStream fileOut = new FileOutputStream(file)) {
            workbook.write(fileOut);
        }
        
        workbook.close();
    }
    
    /**
     * Định dạng số tiền thành chuỗi có đơn vị tiền tệ
     * @param amount Số tiền
     * @return Chuỗi định dạng tiền tệ
     */
    private String formatCurrency(BigDecimal amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormat.format(amount);
    }
    
    /**
     * Lớp nội bộ để lưu trữ tổng hợp thông tin sản phẩm
     */
    private class ProductSummary {
        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalAmount;
        
        public ProductSummary(String productId, String productName, int quantity, BigDecimal unitPrice, BigDecimal totalAmount) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalAmount = totalAmount;
        }
        
        public void increaseQuantity(int additionalQuantity) {
            this.quantity += additionalQuantity;
            this.totalAmount = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
        
        public String getProductId() {
            return productId;
        }
        
        public String getProductName() {
            return productName;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public BigDecimal getUnitPrice() {
            return unitPrice;
        }
        
        public BigDecimal getTotalAmount() {
            return totalAmount;
        }
    }
}