package com.pcstore.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.pcstore.model.PurchaseOrder;
import com.pcstore.model.PurchaseOrderDetail;
import com.pcstore.repository.impl.PurchaseOrderDetailRepository;
import com.pcstore.repository.impl.PurchaseOrderRepository;
import com.pcstore.service.PurchaseOrderDetailService;
import com.pcstore.service.PurchaseOrderService;
import com.pcstore.service.ServiceFactory;
import com.pcstore.utils.CurrencyFormatter;
import com.pcstore.utils.DatabaseConnection;
import com.pcstore.utils.TableStyleUtil;
import com.pcstore.view.StockInHistoryForm;

/**
 * Controller cho màn hình lịch sử nhập kho
 */
public class StockInHistoryController {
    private StockInHistoryForm historyForm;
    private Connection connection;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private PurchaseOrderService purchaseOrderService;
    private PurchaseOrderDetailService purchaseOrderDetailService;
    /**
     * Khởi tạo controller với form lịch sử nhập hàng
     * 
     * @param historyForm Form lịch sử nhập hàng
     */
    public StockInHistoryController(StockInHistoryForm historyForm) {
        this.historyForm = historyForm;

        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            this.purchaseOrderService = ServiceFactory.getInstance().getPurchaseOrderService();
            this.purchaseOrderDetailService = ServiceFactory.getInstance().getPurchaseOrderDetailService();

            // Đăng ký sự kiện
            registerEvents();

            // Thiết lập các bảng
            TableStyleUtil.applyDefaultStyle(historyForm.getTablePurchaseOrders());
            TableStyleUtil.applyDefaultStyle(historyForm.getTablePurchaseOrderDetails());

            // Tải dữ liệu lịch sử
            loadPurchaseOrderHistory();

            // Thiết lập renderer màu cho trạng thái
            setupStatusRenderer();

            // System.out.println("StockInHistoryController: Khởi tạo thành công");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(historyForm,
                    "Lỗi kết nối đến cơ sở dữ liệu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e ) {
            JOptionPane.showMessageDialog(historyForm,
                    "Lỗi khởi tạo form lịch sử nhập kho: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Đăng ký các sự kiện
     */
    private void registerEvents() {
        try {
            // Sự kiện click vào bảng lịch sử để xem chi tiết
            historyForm.getTablePurchaseOrders().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int selectedRow = historyForm.getTablePurchaseOrders().getSelectedRow();
                    if (selectedRow >= 0) {
                        // Lấy mã phiếu nhập
                        String purchaseOrderId = historyForm.getTablePurchaseOrders()
                                .getValueAt(selectedRow, 1).toString();
                        // Tải chi tiết phiếu nhập
                        loadPurchaseOrderDetails(purchaseOrderId);

                        // Kích hoạt nút cập nhật trạng thái
                        historyForm.enableUpdateStatusButton(true);
                    } else {
                        // Vô hiệu hóa nút khi không có dòng nào được chọn
                        historyForm.enableUpdateStatusButton(false);
                    }
                }
            });

            // Sự kiện nút đóng
            historyForm.getBtnClose().addActionListener(e -> {
                historyForm.dispose();
            });

            // Sự kiện làm mới
            historyForm.getBtnRefresh().addActionListener(e -> {
                loadPurchaseOrderHistory();
                // Vô hiệu hóa nút cập nhật trạng thái khi làm mới
                historyForm.enableUpdateStatusButton(false);
            });

            System.out.println("StockInHistoryController: Đăng ký sự kiện thành công");
        } catch (Exception e) {
            System.err.println("Lỗi khi đăng ký sự kiện: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Hiển thị hộp thoại chọn trạng thái mới cho phiếu nhập
     * 
     * @param purchaseOrderId Mã phiếu nhập
     * @param currentStatus   Trạng thái hiện tại
     */
    public void showUpdateStatusDialog(String purchaseOrderId, String currentStatus) {
        try {
            // Các trạng thái có thể có
            String[] statuses = { "Pending", "Delivering", "Completed", "Cancelled" };

            // Tìm vị trí của trạng thái hiện tại
            int currentIndex = 0;
            for (int i = 0; i < statuses.length; i++) {
                if (statuses[i].equals(currentStatus)) {
                    currentIndex = i;
                    break;
                }
            }

            // Hiển thị dialog lựa chọn
            String newStatus = (String) JOptionPane.showInputDialog(
                    historyForm,
                    "Chọn trạng thái mới cho phiếu nhập " + purchaseOrderId + ":",
                    "Cập Nhật Trạng Thái",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    statuses,
                    statuses[currentIndex]);

            // Nếu người dùng đã chọn trạng thái mới và khác trạng thái hiện tại
            if (newStatus != null && !newStatus.equals(currentStatus)) {
                // Cập nhật trạng thái trong database
                boolean updated = updatePurchaseOrderStatus(purchaseOrderId, newStatus);

                if (updated) {
                    JOptionPane.showMessageDialog(
                            historyForm,
                            "Đã cập nhật trạng thái phiếu nhập " + purchaseOrderId + " thành " + newStatus,
                            "Cập Nhật Thành Công",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Làm mới dữ liệu
                    loadPurchaseOrderHistory();
                } else {
                    JOptionPane.showMessageDialog(
                            historyForm,
                            "Không thể cập nhật trạng thái phiếu nhập.",
                            "Lỗi Cập Nhật",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi hiển thị dialog cập nhật trạng thái: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cập nhật trạng thái phiếu nhập trong database và xử lý logic khi chuyển trạng thái
     * @param purchaseOrderId Mã phiếu nhập
     * @param newStatus Trạng thái mới
     * @return true nếu thành công, false nếu thất bại
     */
    private boolean updatePurchaseOrderStatus(String purchaseOrderId, String newStatus) {
        try {
            ensureConnection();
            
            // Bắt đầu transaction
            connection.setAutoCommit(false);
            
            // 1. Lấy thông tin phiếu nhập hiện tại 
            Optional<PurchaseOrder> orderOpt = purchaseOrderService.findPurchaseOrderById(purchaseOrderId);
            if (!orderOpt.isPresent()) {
                System.err.println("Không tìm thấy phiếu nhập: " + purchaseOrderId);
                connection.rollback();
                return false;
            }
            
            PurchaseOrder order = orderOpt.get();
            String currentStatus = order.getStatus();
            
            // 2. Kiểm tra tính hợp lệ khi chuyển đổi trạng thái
            if (!isValidStatusTransition(currentStatus, newStatus)) {
                String message = "Không thể chuyển trạng thái từ '" + currentStatus + "' sang '" + newStatus + "'";
                System.err.println(message);
                JOptionPane.showMessageDialog(historyForm, 
                    message, 
                    "Lỗi Cập Nhật Trạng Thái", 
                    JOptionPane.ERROR_MESSAGE);
                connection.rollback();
                return false;
            }
            
            // 3. Cập nhật trạng thái
            order.setStatus(newStatus);
            
            // Cập nhật vào cơ sở dữ liệu cho tất cả các trường hợp
            PurchaseOrder updatedOrder = purchaseOrderService.updatePurchaseOrder(order);
            if (updatedOrder == null) {
                System.err.println("Không thể cập nhật trạng thái phiếu nhập trong cơ sở dữ liệu.");
                connection.rollback();
                return false;
            }
            
            // 4. Xử lý logic đặc biệt khi chuyển sang trạng thái Completed
            if ("Completed".equals(newStatus) && !"Completed".equals(currentStatus)) {
                System.out.println("Đang cập nhật số lượng sản phẩm trong kho cho phiếu nhập: " + purchaseOrderId);
                
                // Lấy chi tiết phiếu nhập
                List<PurchaseOrderDetail> details = purchaseOrderDetailService.findPurchaseOrderDetailsByOrderId(purchaseOrderId);
                
                // Hoàn thành phiếu nhập sẽ cập nhật số lượng sản phẩm
                purchaseOrderService.completePurchaseOrder(purchaseOrderId);

                System.out.println("Đã cập nhật số lượng cho " + details.size() + " sản phẩm");
            }
            
            // Commit transaction nếu mọi thứ thành công
            connection.commit();
            return true;
            
        } catch (Exception e) {
            // Rollback transaction nếu có lỗi
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Lỗi khi rollback: " + ex.getMessage());
            }
            
            System.err.println("Lỗi khi cập nhật trạng thái phiếu nhập: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Đảm bảo autoCommit được thiết lập lại
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Lỗi khi reset autoCommit: " + e.getMessage());
            }
        }
    }

    /**
     * Kiểm tra xem việc chuyển đổi trạng thái có hợp lệ không
     * @param currentStatus Trạng thái hiện tại
     * @param newStatus Trạng thái mới
     * @return true nếu chuyển đổi hợp lệ, false nếu không hợp lệ
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // Nếu không thay đổi trạng thái
        if (currentStatus.equals(newStatus)) {
            return true;
        }
        
        // Định nghĩa quy tắc chuyển đổi trạng thái
        switch (currentStatus) {
            case "Pending":
                // Từ Pending có thể chuyển sang Delivering, Completed hoặc Cancelled
                return "Delivering".equals(newStatus) || 
                       "Completed".equals(newStatus) || 
                       "Cancelled".equals(newStatus);
                
            case "Delivering":
                // Từ Delivering chỉ có thể chuyển sang Completed hoặc Cancelled
                return "Completed".equals(newStatus) || 
                       "Cancelled".equals(newStatus);
                
            case "Completed":
                // Từ Completed không thể chuyển sang trạng thái khác
                return false;
                
            case "Cancelled":
                // Từ Cancelled không thể chuyển sang trạng thái khác
                return false;
                
            default:
                // Trạng thái không xác định - không cho phép chuyển đổi
                return false;
        }
    }

    /**
     * Thiết lập renderer màu cho trạng thái
     */
    private void setupStatusRenderer() {
        try {
            JTable table = historyForm.getTablePurchaseOrders();
            TableColumnModel columnModel = table.getColumnModel();

            // Đảm bảo số cột đủ để tránh IndexOutOfBoundsException
            if (columnModel.getColumnCount() >= 5) {
                // Cột trạng thái thường nằm ở vị trí 4 (index 4)
                columnModel.getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value,
                            boolean isSelected, boolean hasFocus, int row, int column) {
                        Component c = super.getTableCellRendererComponent(
                                table, value, isSelected, hasFocus, row, column);

                        if (value != null) {
                            String status = value.toString().toLowerCase();
                            if (status.contains("hoàn thành") || status.contains("completed")) {
                                setForeground(new Color(0, 128, 0)); // Màu xanh lá
                            } else if (status.contains("chờ") || status.contains("pending")) {
                                setForeground(new Color(255, 165, 0)); // Màu cam
                            } else if (status.contains("hủy") || status.contains("cancelled")) {
                                setForeground(Color.RED); // Màu đỏ
                            } else {
                                setForeground(Color.BLACK); // Màu đen cho các trạng thái khác
                            }
                            setHorizontalAlignment(JLabel.CENTER);
                        }
                        return c;
                    }
                });
            }

            // Căn giữa tất cả các cột trong cả hai bảng
            centerTableColumns(table);
            centerTableColumns(historyForm.getTablePurchaseOrderDetails());

        } catch (Exception e) {
            System.err.println("Lỗi khi thiết lập renderer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Căn giữa tất cả các cột trong bảng
     */
    private void centerTableColumns(JTable table) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }
    }


    /**
     * Load lịch sử phiếu nhập hàng từ database
     */
    public void loadPurchaseOrderHistory() {
        try {
            

            // // SQL đã được sửa để khớp chính xác với tên cột trong database
            // String directSql = "SELECT po.PurchaseOrderID, po.OrderDate, po.Status, po.TotalAmount, " +
            //         "po.EmployeeID, e.FullName, " + // FullName chính xác
            //         "po.SupplierID, s.Name " + // Name chính xác
            //         "FROM PurchaseOrders po " +
            //         "LEFT JOIN Suppliers s ON po.SupplierID = s.SupplierID " +
            //         "LEFT JOIN Employees e ON po.EmployeeID = e.EmployeeID " +
            //         "ORDER BY po.OrderDate DESC";

            // List<PurchaseOrder> purchaseOrders = new ArrayList<>();

            // try (java.sql.Statement stmt = connection.createStatement();
            //         java.sql.ResultSet rs = stmt.executeQuery(directSql)) {

            //     while (rs.next()) {
            //         try {
            //             PurchaseOrder order = new PurchaseOrder();
            //             order.setPurchaseOrderId(rs.getString("PurchaseOrderID"));

            //             // Đọc OrderDate
            //             java.sql.Timestamp timestamp = rs.getTimestamp("OrderDate");
            //             if (timestamp != null) {
            //                 order.setOrderDate(timestamp.toLocalDateTime());
            //             }

            //             // Đọc Status và TotalAmount
            //             order.setStatus(rs.getString("Status"));
            //             order.setTotalAmount(rs.getBigDecimal("TotalAmount"));

            //             // Thiết lập Employee - sử dụng đúng tên cột FullName
            //             String employeeId = rs.getString("EmployeeID");
            //             if (employeeId != null) {
            //                 com.pcstore.model.Employee emp = new com.pcstore.model.Employee();
            //                 emp.setEmployeeId(employeeId);
            //                 emp.setFullName(rs.getString("FullName"));
            //                 order.setEmployee(emp);
            //             }

            //             // Thiết lập Supplier - sử dụng đúng tên cột Name
            //             String supplierId = rs.getString("SupplierID");
            //             if (supplierId != null) {
            //                 com.pcstore.model.Supplier sup = new com.pcstore.model.Supplier();
            //                 sup.setSupplierId(supplierId);
            //                 sup.setName(rs.getString("Name")); // Sử dụng Name thay vì SupplierName
            //                 order.setSupplier(sup);
            //             }

            //             purchaseOrders.add(order);
            //             System.out.println("Found order: " + order.getPurchaseOrderId() +
            //                     " | Date: " + (order.getOrderDate() != null ? order.getOrderDate() : "null") +
            //                     " | Status: " + order.getStatus());
            //         } catch (Exception e) {
            //             System.err.println("Error processing row: " + e.getMessage());
            //             e.printStackTrace();
            //         }
            //     }
            // }

            List<PurchaseOrder> purchaseOrders = purchaseOrderService.findAllPurchaseOrders();
            // Lấy model của bảng
            DefaultTableModel model = (DefaultTableModel) historyForm.getTablePurchaseOrders().getModel();

            // Xóa dữ liệu cũ
            model.setRowCount(0);

            // Thêm dữ liệu mới vào bảng
            int stt = 1;
            for (PurchaseOrder order : purchaseOrders) {
                // Người tạo - lấy từ Employee.FullName
                String employeeName = (order.getEmployee() != null) ? order.getEmployee().getFullName() : "N/A";

                // Định dạng ngày tạo
                String formattedDate = order.getOrderDate() != null ? order.getOrderDate().format(dateFormatter) : "";

                // Định dạng tổng tiền
                String formattedAmount = order.getTotalAmount() != null
                        ? CurrencyFormatter.format(order.getTotalAmount())
                        : "0";

                model.addRow(new Object[] {
                        stt++, // STT - số tự tăng
                        order.getPurchaseOrderId(), // Mã phiếu
                        formattedDate, // Ngày tạo
                        employeeName, // Người tạo
                        order.getStatus(), // Trạng thái
                        formattedAmount // Tổng tiền
                });
            }

            // Xóa dữ liệu bảng chi tiết khi mới load
            DefaultTableModel detailModel = (DefaultTableModel) historyForm.getTablePurchaseOrderDetails().getModel();
            detailModel.setRowCount(0);

            System.out.println("Số lượng phiếu nhập đã tìm thấy: " + purchaseOrders.size());
        } catch (Exception e) {
            System.err.println("Lỗi khi tải lịch sử phiếu nhập: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tải chi tiết phiếu nhập khi click vào một dòng trong bảng
     * 
     * @param purchaseOrderId Mã phiếu nhập
     */
    private void loadPurchaseOrderDetails(String purchaseOrderId) {
        try {
            // Kiểm tra kết nối
            ensureConnection();

            // Lấy chi tiết phiếu nhập
            List<PurchaseOrderDetail> details = purchaseOrderDetailService.findPurchaseOrderDetailsByOrderId(purchaseOrderId);

            // Lấy model của bảng
            DefaultTableModel model = (DefaultTableModel) historyForm.getTablePurchaseOrderDetails().getModel();

            // Xóa dữ liệu cũ
            model.setRowCount(0);

            // Thêm dữ liệu mới
            int stt = 1;
            for (PurchaseOrderDetail detail : details) {
                String productId = detail.getProduct() != null ? detail.getProduct().getProductId() : "";
                String productName = detail.getProduct() != null ? detail.getProduct().getProductName() : "";

                BigDecimal totalPrice = detail.getUnitCost().multiply(new BigDecimal(detail.getQuantity()));

                model.addRow(new Object[] {
                        stt++,
                        productId,
                        productName,
                        detail.getQuantity(),
                        CurrencyFormatter.format(detail.getUnitCost()),
                        CurrencyFormatter.format(totalPrice)
                });
            }

            // System.out.println("StockInHistoryController: Đã tải " + details.size() +
                    // " chi tiết phiếu nhập cho phiếu " + purchaseOrderId);
        } catch (Exception e) {
            System.err.println("Lỗi khi tải chi tiết phiếu nhập: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Đảm bảo kết nối hoạt động
     */
    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.getInstance().getConnection();
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