package com.pcstore.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.pcstore.service.RevenueService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.ErrorMessage;
import com.pcstore.utils.JExcel;
import com.pcstore.utils.LocaleManager;
import com.pcstore.utils.TableUtils;
import com.pcstore.view.RevenueProductForm;

import raven.toast.Notifications;

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
    private boolean isUpdatingToDate = false; // Flag để tránh vòng lặp vô hạn

    /**
     * Lấy instance duy nhất của controller (Singleton pattern)
     * 
     * @param revenueProductForm Form hiển thị thống kê doanh thu sản phẩm
     * @return RevenueProductController instance
     */
    public static synchronized RevenueProductController getInstance(RevenueProductForm revenueProductForm) {
        if (instance == null) {
            instance = new RevenueProductController(revenueProductForm);
        } else {
            instance.revenueProductForm = revenueProductForm;
            instance.loadRevenueData();
            LocalDate today = LocalDate.now();
            instance.fromDate = today.withDayOfMonth(1);
            instance.toDate = today;

            instance.setupEventListeners();
            instance.setupTableStyle();
            instance.loadRevenueData();

        }
        return instance;
    }

    /**
     * Khởi tạo controller với form (Giao diện người dùng)
     * 
     * @param revenueProductForm Form hiển thị thống kê doanh thu sản phẩm
     */
    private RevenueProductController(RevenueProductForm revenueProductForm) {
        try {
            this.revenueProductForm = revenueProductForm;
            this.revenueService = ServiceFactory.getRevenueService();

            LocalDate today = LocalDate.now();
            this.fromDate = today.withDayOfMonth(1);
            this.toDate = today;

            setupEventListeners();
            setupTableStyle();
            loadRevenueData();

            updateDateRange(fromDate, toDate);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(revenueProductForm,
                    ErrorMessage.REVENUE_CONTROLLER_INIT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Constructor with connection for testing purposes
     * 
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
        for (MouseListener listener : revenueProductForm.getTxtProductBestSelling().getMouseListeners()) {
            revenueProductForm.getTxtProductBestSelling().removeMouseListener(listener);
        }
        for (MouseListener listener : revenueProductForm.getTxtProductSlowSelling().getMouseListeners()) {
            revenueProductForm.getTxtProductSlowSelling().removeMouseListener(listener);
        }

        // Thêm document listener cho text field ngày bắt đầu
        // revenueProductForm.getTxtFromDate().getDocument().addDocumentListener(new
        // javax.swing.event.DocumentListener() {
        // @Override
        // public void insertUpdate(javax.swing.event.DocumentEvent e) {
        // handleFromDateChange();
        // }

        // @Override
        // public void removeUpdate(javax.swing.event.DocumentEvent e) {
        // handleFromDateChange();
        // }

        // @Override
        // public void changedUpdate(javax.swing.event.DocumentEvent e) {
        // handleFromDateChange();
        // }
        // });

        // // Thêm document listener cho text field ngày kết thúc
        // revenueProductForm.getTxtToDate().getDocument().addDocumentListener(new
        // javax.swing.event.DocumentListener() {
        // @Override
        // public void insertUpdate(javax.swing.event.DocumentEvent e) {
        // handleToDateChange();
        // }

        // @Override
        // public void removeUpdate(javax.swing.event.DocumentEvent e) {
        // handleToDateChange();
        // }

        // @Override
        // public void changedUpdate(javax.swing.event.DocumentEvent e) {
        // handleToDateChange();
        // }
        // });

        revenueProductForm.getBtnExportReport().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exportRevenueReport();
            }
        });

        revenueProductForm.getBtnApply().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleFromDateChange();
                handleToDateChange();
            }
        });

        revenueProductForm.getTxtProductBestSelling().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showBestSellingProductDialog();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                revenueProductForm.getTxtProductBestSelling()
                        .setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                revenueProductForm.getTxtProductBestSelling()
                        .setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        });

        revenueProductForm.getTxtProductSlowSelling().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showSlowSellingProductDialog();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                revenueProductForm.getTxtProductSlowSelling()
                        .setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                revenueProductForm.getTxtProductSlowSelling()
                        .setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        });

        // Thay đổi tooltip cho các label thống kê
        revenueProductForm.getTxtProductBestSelling()
                .setToolTipText(ErrorMessage.REVENUE_TOOLTIP_BEST_SELLING.toString());
        revenueProductForm.getTxtProductSlowSelling()
                .setToolTipText(ErrorMessage.REVENUE_TOOLTIP_SLOW_SELLING.toString());
        revenueProductForm.getTxtProfit().setToolTipText(ErrorMessage.REVENUE_TOOLTIP_PROFIT_MARGIN.toString());

    }

    /**
     * Xử lý khi ngày bắt đầu thay đổi
     */
    private void handleFromDateChange() {
        if (isUpdatingToDate)
            return;

        String dateStr = revenueProductForm.getTxtFromDate().getText();
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
                revenueProductForm.setChooserFromDate(tempFromDate);
                isUpdatingToDate = false;

                Notifications.getInstance().show(Notifications.Type.ERROR, ErrorMessage.DATE_END_UPDATED.toString());
                return;
            }

            loadRevenueData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Xử lý khi ngày kết thúc thay đổi
     */
    private void handleToDateChange() {
        if (isUpdatingToDate)
            return;

        String dateStr = revenueProductForm.getTxtToDate().getText();
        if (dateStr.length() != 10) {
            return;
        }

        try {
            LocalDate newToDate = LocalDate.parse(dateStr, dateFormatter);

            // Kiểm tra nếu ngày kết thúc < ngày bắt đầu
            if (newToDate.isBefore(fromDate)) {
                Notifications.getInstance().show(Notifications.Type.ERROR,
                        ErrorMessage.DATE_END_BEFORE_START.format(fromDate.format(dateFormatter)));
                isUpdatingToDate = true;
                revenueProductForm.setChooserToDate(toDate);
                isUpdatingToDate = false;
                return;
            }

            toDate = newToDate;
            loadRevenueData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // setupTable
    private void setupTableStyle() {
        TableRowSorter<TableModel> sorter = TableUtils.applyDefaultStyle(revenueProductForm.getTableRevenue());
        TableUtils.setNumberColumns(sorter, 0, 3, 4, 5, 6, 7);

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
                String productId = (String) data.get("productId");
                String productName = (String) data.get("productName");
                int quantity = (int) data.get("quantity");
                BigDecimal costPrice = (BigDecimal) data.get("costPrice");
                if (costPrice == null) {
                    costPrice = BigDecimal.ZERO;
                }
                BigDecimal discountAmount = (BigDecimal) data.get("discountAmount");
                if (discountAmount == null) {
                    discountAmount = BigDecimal.ZERO;
                }
                BigDecimal unitPrice = (BigDecimal) data.get("unitPrice");
                BigDecimal revenue = (BigDecimal) data.get("revenue");

                totalRevenue = totalRevenue.add(revenue);

                Object[] row = {
                        stt,
                        productId,
                        productName,
                        quantity,
                        currencyFormatter.format(costPrice),
                        currencyFormatter.format(unitPrice),
                        currencyFormatter.format(discountAmount),
                        currencyFormatter.format(revenue)
                };
                model.addRow(row);
            }
            revenueProductForm.getLbTotal().setText(currencyFormatter.format(totalRevenue));

            updatePieChart(revenueData, totalRevenue);

            updateProductStatistics();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, ErrorMessage.REVENUE_DATA_LOAD_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Xuất báo cáo doanh thu sản phẩm ra file Excel
     */
    private void exportRevenueReport() {
        try {
            // Chuẩn bị dữ liệu cho file Excel
            List<String> headers = new ArrayList<>();
            // Lấy tên cột từ bảng
            DefaultTableModel model = (DefaultTableModel) revenueProductForm.getTableRevenue().getModel();
            for (int i = 0; i < model.getColumnCount(); i++) {
                headers.add(model.getColumnName(i));
            }

            // Lấy dữ liệu từ bảng
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
            metadata.put(ErrorMessage.REVENUE_EXPORT_FROM_DATE_LABEL.toString(), fromDate.format(dateFormatter));
            metadata.put(ErrorMessage.REVENUE_EXPORT_TO_DATE_LABEL.toString(), toDate.format(dateFormatter));
            metadata.put(ErrorMessage.REVENUE_EXPORT_TOTAL_REVENUE_LABEL.toString(),
                    revenueProductForm.getLbTotal().getText());

            JExcel exporter = new JExcel();
            String filePath = exporter.toExcel(headers, data, ErrorMessage.REVENUE_EXPORT_SHEET_TITLE.toString(),
                    metadata, fileName);

            if (filePath != null) {
                JOptionPane.showMessageDialog(null, ErrorMessage.EXPORT_REPORT_SUCCESS.format(filePath),
                        ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, ErrorMessage.EXPORT_REPORT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lấy dữ liệu doanh thu sản phẩm theo ID
     * 
     * @param productId ID sản phẩm cần lấy doanh thu
     * @return Tổng doanh thu của sản phẩm trong khoảng thời gian đã chọn
     */
    public BigDecimal getRevenueByProduct(String productId) {
        try {
            return revenueService.getRevenueByProduct(productId, fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, ErrorMessage.REVENUE_BY_PRODUCT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Lấy số lượng đã bán của sản phẩm theo ID
     * 
     * @param productId ID sản phẩm cần lấy thông tin
     * @return Số lượng đã bán của sản phẩm trong khoảng thời gian đã chọn
     */
    public int getQuantitySoldByProduct(String productId) {
        try {
            return revenueService.getQuantitySoldByProduct(productId, fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, ErrorMessage.QUANTITY_SOLD_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return 0;
        }
    }

    /**
     * Lấy top sản phẩm bán chạy nhất trong khoảng thời gian
     * 
     * @param limit Số lượng sản phẩm muốn lấy
     * @return Danh sách sản phẩm bán chạy nhất
     */
    public List<Map<String, Object>> getTopSellingProducts(int limit) {
        try {
            return revenueService.getTopSellingProducts(limit, fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, ErrorMessage.TOP_SELLING_PRODUCTS_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }

    /**
     * Cập nhật khoảng thời gian thống kê
     * 
     * @param fromDate Ngày bắt đầu
     * @param toDate   Ngày kết thúc
     */
    public void updateDateRange(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;

        revenueProductForm.setChooserFromDate(fromDate);
        revenueProductForm.setChooserToDate(toDate);

        loadRevenueData();
    }

    /**
     * Cập nhật biểu đồ tròn thể hiện phần trăm doanh thu sản phẩm
     * 
     * @param revenueData  Dữ liệu doanh thu sản phẩm
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
                    new Color(26, 162, 106), // Xanh lá
                    new Color(30, 113, 195), // Xanh dương
                    new Color(255, 153, 0), // Cam
                    new Color(255, 51, 51), // Đỏ
                    new Color(153, 51, 255), // Tím
                    new Color(153, 153, 153) // Xám (cho mục "Khác")
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
                            colorArray[i]));
                } else {
                    othersRevenue = othersRevenue.add(revenue);
                }
            }

            if (othersRevenue.compareTo(BigDecimal.ZERO) > 0) {
                pieChart.addData(new ModelPieChart(
                        ErrorMessage.REVENUE_CHART_OTHER_LABEL.toString(),
                        othersRevenue.doubleValue(),
                        colorArray[5]));
            }

            revenueProductForm.getPanelChart().setLayout(new BorderLayout());
            revenueProductForm.getPanelChart().add(pieChart, BorderLayout.CENTER);

            revenueProductForm.getPanelChart().revalidate();
            revenueProductForm.getPanelChart().repaint();

            setupPieChartClickEvent(pieChart, sortedData, maxToShow);

        } catch (Exception e) {
            // Đổi thông báo lỗi
            System.err.println(ErrorMessage.REVENUE_PIE_CHART_UPDATE_ERROR.format(e.getMessage()));
            e.printStackTrace();
        }
    }

    /**
     * Hiển thị chi tiết sản phẩm khi người dùng nhấp vào biểu đồ
     * 
     * @param pieChart   Biểu đồ tròn
     * @param sortedData Dữ liệu đã sắp xếp
     * @param maxToShow  Số lượng tối đa hiển thị
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
        String message = String.format(ErrorMessage.PRODUCT_DETAIL_MESSAGE.toString(),
                productName, productId, currencyFormatter.format(revenue), quantity);

        JOptionPane.showMessageDialog(revenueProductForm, message,
                ErrorMessage.PRODUCT_DETAIL_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Hiển thị danh sách các sản phẩm khác
     */
    private void showOtherProductsList(List<Map<String, Object>> sortedData, int maxToShow) {
        StringBuilder message = new StringBuilder(ErrorMessage.OTHER_PRODUCTS_HEADER.toString());

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
                ErrorMessage.OTHER_PRODUCTS_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Cập nhật thống kê sản phẩm bán chạy, bán chậm và tỷ suất lợi nhuận
     */
    private void updateProductStatistics() {
        try {
            // Cập nhật sản phẩm bán chạy nhất
            Map<String, Object> bestSelling = revenueService.getBestSellingProduct(fromDate, toDate);
            if (!bestSelling.isEmpty()) {
                String bestSellingText = String.format(revenueProductForm.getTxtProductBestSelling().getText(),
                        bestSelling.get("productName"),
                        bestSelling.get("totalQuantity"));
                revenueProductForm.getTxtProductBestSelling().setText(bestSellingText);

            } else {
                revenueProductForm.getTxtProductBestSelling().setText(ErrorMessage.NO_DATA_AVAILABLE.toString());
            }

            // Cập nhật sản phẩm bán chậm nhất
            Map<String, Object> slowSelling = revenueService.getSlowSellingProduct(fromDate, toDate);
            if (!slowSelling.isEmpty()) {
                String slowSellingText = String.format(revenueProductForm.getTxtProductSlowSelling().getText(),
                        slowSelling.get("productName"),
                        slowSelling.get("totalQuantity"));
                revenueProductForm.getTxtProductSlowSelling().setText(slowSellingText);
            } else {
                revenueProductForm.getTxtProductSlowSelling().setText(ErrorMessage.NO_DATA_AVAILABLE.toString());
            }

            // Cập nhật tỷ suất lợi nhuận
            Map<String, Object> profit = revenueService.calculateTotalProfit(fromDate, toDate);
            BigDecimal profitMargin = (BigDecimal) profit.get("profitMarginPercent");
            BigDecimal totalProfit = (BigDecimal) profit.get("totalProfit");
            revenueProductForm.getTxtProfit().setText(currencyFormatter.format(totalProfit));
            revenueProductForm.getTxtProfitPercent().setText(currencyFormatter.format(profitMargin) + "%");

        } catch (Exception e) {
            e.printStackTrace();
            revenueProductForm.getTxtProductBestSelling().setText(ErrorMessage.DATA_LOAD_ERROR.toString());
            revenueProductForm.getTxtProductSlowSelling().setText(ErrorMessage.DATA_LOAD_ERROR.toString());
            revenueProductForm.getTxtProfit().setText(ErrorMessage.DATA_LOAD_ERROR.toString());
            revenueProductForm.getTxtProfitPercent().setText(ErrorMessage.DATA_LOAD_ERROR.toString());
        }
    }

    /**
     * Lấy thông tin chi tiết sản phẩm bán chạy nhất
     * 
     * @return Map chứa thông tin sản phẩm bán chạy nhất
     */
    public Map<String, Object> getBestSellingProductDetail() {
        try {
            return revenueService.getBestSellingProduct(fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(revenueProductForm,
                    "Lỗi khi lấy thông tin sản phẩm bán chạy: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return new HashMap<>();
        }
    }

    /**
     * Lấy thông tin chi tiết sản phẩm bán chậm nhất
     * 
     * @return Map chứa thông tin sản phẩm bán chậm nhất
     */
    public Map<String, Object> getSlowSellingProductDetail() {
        try {
            return revenueService.getSlowSellingProduct(fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(revenueProductForm,
                    "Lỗi khi lấy thông tin sản phẩm bán chậm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return new HashMap<>();
        }
    }

    /**
     * Lấy tỷ suất lợi nhuận hiện tại
     * 
     * @return Tỷ suất lợi nhuận (%)
     */
    public BigDecimal getCurrentProfitMargin() {
        try {
            return revenueService.calculateProfitMargin(fromDate, toDate);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(revenueProductForm,
                    "Lỗi khi tính tỷ suất lợi nhuận: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Hiển thị dialog chi tiết sản phẩm bán chạy
     */
    public void showBestSellingProductDialog() {
        try {
            List<Map<String, Object>> topProducts = revenueService.getTopBestSellingProducts(10, fromDate, toDate);

            if (topProducts.isEmpty()) {
                // Đổi thông báo không có dữ liệu
                JOptionPane.showMessageDialog(revenueProductForm,
                        ErrorMessage.NO_BEST_SELLING_DATA.toString(),
                        ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Đổi tiêu đề dialog
            javax.swing.JDialog dialog = new javax.swing.JDialog(
                    (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(revenueProductForm),
                    ErrorMessage.TOP_10_BEST_SELLING_TITLE.toString(), true);
            dialog.setSize(700, 400);
            dialog.setLocationRelativeTo(revenueProductForm);

            // Đổi tên cột
            String[] columns = {
                    ErrorMessage.REVENUE_PRODUCT_TABLE_STT.toString(),
                    ErrorMessage.REVENUE_PRODUCT_TABLE_PRODUCT_ID.toString(),
                    ErrorMessage.REVENUE_PRODUCT_TABLE_PRODUCT_NAME.toString(),
                    ErrorMessage.REVENUE_PRODUCT_TABLE_CATEGORY.toString(),
                    ErrorMessage.REVENUE_PRODUCT_TABLE_QUANTITY.toString(),
                    ErrorMessage.REVENUE_PRODUCT_TABLE_REVENUE.toString(),
                    ErrorMessage.REVENUE_PRODUCT_TABLE_AVG_PRICE.toString()
            };

            javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            int index = 1;
            for (Map<String, Object> product : topProducts) {
                Object[] row = {
                        index++,
                        product.get("productId"),
                        product.get("productName"),
                        product.get("categoryName"),
                        product.get("totalQuantity"),
                        currencyFormatter.format(product.get("totalRevenue")),
                        currencyFormatter.format(product.get("avgPrice"))
                };
                tableModel.addRow(row);
            }

            javax.swing.JTable table = new javax.swing.JTable(tableModel);
            TableRowSorter<TableModel> sorter = TableUtils.applyDefaultStyle(table);
            TableUtils.setNumberColumns(sorter, 0, 4, 5, 6);
            javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(table);
            dialog.add(scrollPane, java.awt.BorderLayout.CENTER);

            javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout());
            // Đổi text nút đóng
            javax.swing.JButton closeButton = new javax.swing.JButton(ErrorMessage.CLOSE_BUTTON.toString());
            closeButton.addActionListener(e -> dialog.dispose());
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel, java.awt.BorderLayout.SOUTH);

            dialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(revenueProductForm,
                    ErrorMessage.BEST_SELLING_PRODUCT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Hiển thị dialog chi tiết sản phẩm bán chậm
     */
    public void showSlowSellingProductDialog() {
        try {
            List<Map<String, Object>> slowProducts = revenueService.getSlowSellingProducts(10, fromDate, toDate);

            if (slowProducts.isEmpty()) {
                JOptionPane.showMessageDialog(revenueProductForm,
                        ErrorMessage.NO_SLOW_SELLING_DATA.toString(),
                        ErrorMessage.INFO_TITLE.toString(), JOptionPane.INFORMATION_MESSAGE);

                return;
            }

            // Tạo dialog hiển thị
            javax.swing.JDialog dialog = new javax.swing.JDialog(
                    (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(revenueProductForm),
                    ErrorMessage.TOP_10_SLOW_SELLING_TITLE.toString(), true);

            dialog.setSize(700, 400);
            dialog.setLocationRelativeTo(revenueProductForm);

            // Tạo table model
            String[] columns = {
                    ErrorMessage.REVENUE_PRODUCT_TABLE_STT.toString(),
                    ErrorMessage.REVENUE_PRODUCT_TABLE_PRODUCT_ID.toString(),
                    ErrorMessage.REVENUE_PRODUCT_TABLE_PRODUCT_NAME.toString(),
                    ErrorMessage.REVENUE_PRODUCT_TABLE_CATEGORY.toString(),
                    ErrorMessage.REVENUE_PRODUCT_TABLE_QUANTITY.toString(),
                    ErrorMessage.REVENUE_PRODUCT_TABLE_REVENUE.toString(),
                    ErrorMessage.REVENUE_PRODUCT_TABLE_AVG_PRICE.toString()
            };

            javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            // Điền dữ liệu
            int index = 1;
            for (Map<String, Object> product : slowProducts) {
                Object[] row = {
                        index++,
                        product.get("productId"),
                        product.get("productName"),
                        product.get("categoryName"),
                        product.get("totalQuantity"),
                        currencyFormatter.format(product.get("totalRevenue")),
                        currencyFormatter.format(product.get("avgPrice"))
                };
                tableModel.addRow(row);
            }

            javax.swing.JTable table = new javax.swing.JTable(tableModel);
            TableRowSorter<TableModel> sorter = TableUtils.applyDefaultStyle(table);
            TableUtils.setNumberColumns(sorter, 0, 4, 5, 6);

            javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(table);
            dialog.add(scrollPane, java.awt.BorderLayout.CENTER);

            // Nút đóng
            javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout());
            javax.swing.JButton closeButton = new javax.swing.JButton(ErrorMessage.CLOSE_BUTTON.toString());
            closeButton.addActionListener(e -> dialog.dispose());
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel, java.awt.BorderLayout.SOUTH);

            dialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(revenueProductForm,
                    ErrorMessage.SLOW_SELLING_PRODUCT_ERROR.format(e.getMessage()),
                    ErrorMessage.ERROR_TITLE.toString(), JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}