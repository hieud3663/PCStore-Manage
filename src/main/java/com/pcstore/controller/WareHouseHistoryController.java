package com.pcstore.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.pcstore.model.PurchaseOrder;
import com.pcstore.model.PurchaseOrderDetail;
import com.pcstore.repository.impl.PurchaseOrderDetailRepository;
import com.pcstore.repository.impl.PurchaseOrderRepository;
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.view.StockInHistoryForm;
import com.pcstore.repository.Repository;
import com.pcstore.repository.RepositoryFactory;

/**
 * Controller cho màn hình lịch sử nhập kho
 */
public class WareHouseHistoryController {
    private StockInHistoryForm stockInHistoryForm;
    private PurchaseOrderRepository purchaseOrderRepository;
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    private Connection connection;

    /**
     * Khởi tạo controller với form lịch sử nhập kho
     * @param stockInHistoryForm Form lịch sử nhập kho
     */
    public WareHouseHistoryController(StockInHistoryForm stockInHistoryForm) {
        this.stockInHistoryForm = stockInHistoryForm;
        this.connection = DatabaseConnection.getInstance().getConnection();

        // Lấy repository từ factory
        RepositoryFactory repositoryFactory = RepositoryFactory.getInstance(connection);
        this.purchaseOrderRepository = repositoryFactory.getPurchaseOrderRepository();
        this.purchaseOrderDetailRepository = repositoryFactory.getPurchaseOrderDetailRepository();

        // Đăng ký các sự kiện cho form
        registerEvents();

        // Tải dữ liệu lịch sử nhập kho
        loadPurchaseOrderHistory();
    }

    /**
     * Đăng ký các sự kiện cho form
     */
    private void registerEvents() {
        // Xử lý sự kiện khi chọn đơn nhập hàng để xem chi tiết
        if (stockInHistoryForm.getTablePurchaseOrders() != null) {
            stockInHistoryForm.getTablePurchaseOrders().getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    loadSelectedPurchaseOrderDetails();
                }
            });
        }
    }

    /**
     * Tải dữ liệu lịch sử đơn nhập hàng
     */
    public void loadPurchaseOrderHistory() {
        try {
            List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll();
            DefaultTableModel model = (DefaultTableModel) stockInHistoryForm.getTablePurchaseOrders().getModel();
            model.setRowCount(0); // Xóa tất cả các dòng hiện tại

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            int stt = 1;

            for (PurchaseOrder order : purchaseOrders) {
                String createdAt = order.getCreatedAt() != null ? order.getCreatedAt().format(formatter) : "";
                String employeeName = order.getEmployee() != null ? order.getEmployee().getFullName() : "";
                String supplierName = order.getSupplier() != null ? order.getSupplier().getName() : "";

                model.addRow(new Object[]{
                        stt++,
                        order.getPurchaseOrderId(),
                        createdAt,
                        employeeName,
                        supplierName,
                        order.getStatus(),
                        order.getTotalAmount()
                });
            }

            // Nếu có dữ liệu, chọn dòng đầu tiên để hiển thị chi tiết
            if (model.getRowCount() > 0) {
                stockInHistoryForm.getTablePurchaseOrders().setRowSelectionInterval(0, 0);
                loadSelectedPurchaseOrderDetails();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(stockInHistoryForm, "Lỗi khi tải dữ liệu lịch sử nhập kho: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Tải chi tiết đơn nhập hàng được chọn
     */
    private void loadSelectedPurchaseOrderDetails() {
        try {
            int selectedRow = stockInHistoryForm.getTablePurchaseOrders().getSelectedRow();
            if (selectedRow < 0) return;

            String purchaseOrderId = stockInHistoryForm.getTablePurchaseOrders().getValueAt(selectedRow, 1).toString();

            List<PurchaseOrderDetail> details = purchaseOrderDetailRepository.findByPurchaseOrderId(purchaseOrderId);
            DefaultTableModel model = (DefaultTableModel) stockInHistoryForm.getTablePurchaseOrderDetails().getModel();
            model.setRowCount(0); // Xóa tất cả các dòng hiện tại

            int stt = 1;
            for (PurchaseOrderDetail detail : details) {
                String productId = detail.getProduct() != null ? detail.getProduct().getProductId() : "";
                String productName = detail.getProduct() != null ? detail.getProduct().getProductName() : "";

                // Tính tổng tiền cho mỗi dòng bằng cách nhân số lượng với đơn giá
                BigDecimal totalPrice = detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity()));

                model.addRow(new Object[]{
                        stt++,
                        productId,
                        productName,
                        detail.getQuantity(),
                        detail.getUnitPrice(),
                        totalPrice
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(stockInHistoryForm, "Lỗi khi tải chi tiết đơn nhập hàng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Tìm kiếm đơn nhập hàng theo mã hoặc nhà cung cấp
     * @param searchText Từ khóa tìm kiếm
     */
    public void searchPurchaseOrders(String searchText) {
        try {
            List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll();
            List<PurchaseOrder> filteredOrders = new ArrayList<>();

            // Lọc các đơn hàng theo từ khóa tìm kiếm
            for (PurchaseOrder order : purchaseOrders) {
                if (order.getPurchaseOrderId().toLowerCase().contains(searchText.toLowerCase()) ||
                        (order.getSupplier() != null &&
                                order.getSupplier().getName().toLowerCase().contains(searchText.toLowerCase()))) {
                    filteredOrders.add(order);
                }
            }

            // Hiển thị kết quả tìm kiếm
            DefaultTableModel model = (DefaultTableModel) stockInHistoryForm.getTablePurchaseOrders().getModel();
            model.setRowCount(0);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            int stt = 1;

            for (PurchaseOrder order : filteredOrders) {
                String createdAt = order.getCreatedAt() != null ? order.getCreatedAt().format(formatter) : "";
                String employeeName = order.getEmployee() != null ? order.getEmployee().getFullName() : "";
                String supplierName = order.getSupplier() != null ? order.getSupplier().getName() : "";

                model.addRow(new Object[]{
                        stt++,
                        order.getPurchaseOrderId(),
                        createdAt,
                        employeeName,
                        supplierName,
                        order.getStatus(),
                        order.getTotalAmount()
                });
            }

            // Xóa dữ liệu chi tiết nếu không có kết quả tìm kiếm
            if (model.getRowCount() == 0) {
                ((DefaultTableModel) stockInHistoryForm.getTablePurchaseOrderDetails().getModel()).setRowCount(0);
            } else {
                // Chọn dòng đầu tiên để hiển thị chi tiết
                stockInHistoryForm.getTablePurchaseOrders().setRowSelectionInterval(0, 0);
                loadSelectedPurchaseOrderDetails();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(stockInHistoryForm, "Lỗi khi tìm kiếm đơn nhập hàng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * In đơn nhập hàng được chọn
     */
    public void printSelectedPurchaseOrder() {
        try {
            int selectedRow = stockInHistoryForm.getTablePurchaseOrders().getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(stockInHistoryForm, "Vui lòng chọn một đơn nhập hàng để in",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String purchaseOrderId = stockInHistoryForm.getTablePurchaseOrders().getValueAt(selectedRow, 1).toString();

            // TODO: Thực hiện chức năng in đơn nhập hàng
            JOptionPane.showMessageDialog(stockInHistoryForm, "Đang in đơn nhập hàng: " + purchaseOrderId,
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(stockInHistoryForm, "Lỗi khi in đơn nhập hàng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Đóng kết nối khi không cần thiết nữa
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}