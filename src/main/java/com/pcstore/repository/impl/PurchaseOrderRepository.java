package com.pcstore.repository.impl;

import com.pcstore.repository.Repository;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.model.PurchaseOrder;
import com.pcstore.model.PurchaseOrderDetail;
import com.pcstore.model.Supplier;
import com.pcstore.model.Employee;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository implementation cho PurchaseOrder entity
 */
public class PurchaseOrderRepository implements Repository<PurchaseOrder, Integer> {
    private Connection connection;
    private RepositoryFactory RepositoryFactory;
    
    public PurchaseOrderRepository(Connection connection, RepositoryFactory RepositoryFactory) {
        this.connection = connection;
        this.RepositoryFactory = RepositoryFactory;
    }
    /**
 * Thêm phiếu nhập mới
 */
@Override
public PurchaseOrder add(PurchaseOrder purchaseOrder) {
    // Kiểm tra dữ liệu đầu vào
    if (purchaseOrder == null) {
        throw new IllegalArgumentException("PurchaseOrder cannot be null");
    }
    
    // Tạo mã phiếu nhập mới nếu chưa có
    if (purchaseOrder.getPurchaseOrderId() == null || purchaseOrder.getPurchaseOrderId().trim().isEmpty()) {
        String newId = generatePurchaseOrderId();
        purchaseOrder.setPurchaseOrderId(newId);
    }
    
    // Thêm cột Status vào câu lệnh SQL INSERT
    String sql = "INSERT INTO PurchaseOrders (PurchaseOrderID, OrderDate, SupplierID, EmployeeID, Status) " +
                "VALUES (?, ?, ?, ?, ?)";
    
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
        // Debug log để kiểm tra các giá trị
        System.out.println("===== Debug PurchaseOrder Insert =====");
        System.out.println("PurchaseOrderID: " + purchaseOrder.getPurchaseOrderId());
        System.out.println("OrderDate: " + purchaseOrder.getOrderDate());
        System.out.println("SupplierID: " + (purchaseOrder.getSupplier() != null ? 
                           purchaseOrder.getSupplier().getSupplierId() : "null"));
        System.out.println("EmployeeID: " + (purchaseOrder.getEmployee() != null ? 
                           purchaseOrder.getEmployee().getEmployeeId() : "null"));
        System.out.println("Status: " + purchaseOrder.getStatus());
        System.out.println("=====================================");
        
        statement.setString(1, purchaseOrder.getPurchaseOrderId());
        statement.setTimestamp(2, purchaseOrder.getOrderDate() != null ? 
                              java.sql.Timestamp.valueOf(purchaseOrder.getOrderDate()) : null);
        statement.setString(3, purchaseOrder.getSupplier() != null ? 
                           purchaseOrder.getSupplier().getSupplierId() : null);
        statement.setString(4, purchaseOrder.getEmployee() != null ? 
                           purchaseOrder.getEmployee().getEmployeeId() : null);
        statement.setString(5, purchaseOrder.getStatus()); // Thêm trạng thái vào câu lệnh
        
        int rowsAffected = statement.executeUpdate();
        
        if (rowsAffected > 0) {
            System.out.println("Đã thêm phiếu nhập thành công với ID: " + purchaseOrder.getPurchaseOrderId());
            return purchaseOrder;
        } else {
            throw new RuntimeException("Không thể thêm phiếu nhập hàng");
        }
    } catch (SQLException e) {
        System.err.println("SQL Error: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Error adding purchase order: " + e.getMessage(), e);
    }
}
    
    @Override
    public PurchaseOrder update(PurchaseOrder purchaseOrder) {
        String sql = "UPDATE PurchaseOrders SET SupplierID = ?, EmployeeID = ?, OrderDate = ?, " +
                    "TotalAmount = ?, Status = ? WHERE PurchaseOrderID = ?";
                    
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, purchaseOrder.getSupplier() != null ? 
                    purchaseOrder.getSupplier().getSupplierId() : null);
            statement.setString(2, purchaseOrder.getEmployee() != null ? 
                    purchaseOrder.getEmployee().getEmployeeId() : null);
            statement.setObject(3, purchaseOrder.getOrderDate());
            statement.setBigDecimal(4, purchaseOrder.getTotalAmount() != null ? 
                    purchaseOrder.getTotalAmount() : BigDecimal.ZERO);
            statement.setString(5, purchaseOrder.getStatus());
            statement.setString(6, purchaseOrder.getPurchaseOrderId());
            
            statement.executeUpdate();
            
            purchaseOrder.setUpdatedAt(LocalDateTime.now());
            return purchaseOrder;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating purchase order", e);
        }
    }
    
    // @Override
    public boolean delete(String id) {
        // Trước khi xóa đơn nhập hàng, cần xóa tất cả chi tiết đơn hàng liên quan
        String sqlDeleteDetails = "DELETE FROM PurchaseOrderDetails WHERE PurchaseOrderID = ?";
        String sqlDeleteOrder = "DELETE FROM PurchaseOrders WHERE PurchaseOrderID = ?";
        
        try {
            // Thiết lập auto-commit thành false để thực hiện transaction
            connection.setAutoCommit(false);
            
            try (PreparedStatement stmtDeleteDetails = connection.prepareStatement(sqlDeleteDetails);
                 PreparedStatement stmtDeleteOrder = connection.prepareStatement(sqlDeleteOrder)) {
                     
                stmtDeleteDetails.setString(1, id);
                stmtDeleteDetails.executeUpdate();
                
                stmtDeleteOrder.setString(1, id);
                int rowsAffected = stmtDeleteOrder.executeUpdate();
                
                connection.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting purchase order", e);
        }
    }

 /**
 * Tạo mã phiếu nhập hàng mới với định dạng chi tiết hơn để tránh trùng lặp
 */
public String generatePurchaseOrderId() {
    // Sử dụng thời gian hiện tại với độ chính xác đến mili giây
    LocalDateTime now = LocalDateTime.now();
    String year = String.format("%02d", now.getYear() % 100);
    String month = String.format("%02d", now.getMonthValue());
    String day = String.format("%02d", now.getDayOfMonth());
    String hour = String.format("%02d", now.getHour());
    String minute = String.format("%02d", now.getMinute());
    String second = String.format("%02d", now.getSecond());
    int millis = now.getNano() / 1_000_000;
    
    // Tạo một phần ngẫu nhiên với giá trị từ 100-999
    int randomPart = 100 + (int)(Math.random() * 900);
    
    // Kết hợp thành ID: PO-YYMMDD-HHMMSS-MMMRRR
    String newId = String.format("PO-%s%s%s-%s%s%s-%03d%03d", 
                                year, month, day, hour, minute, second, millis, randomPart);
    
    System.out.println("Generated new PurchaseOrderID: " + newId);
    return newId;
}

/**
 * Kiểm tra xem ID phiếu nhập đã tồn tại trong database chưa
 * @param purchaseOrderId ID phiếu nhập cần kiểm tra
 * @return true nếu ID đã tồn tại, false nếu chưa
 */
private boolean isPurchaseOrderIdExists(String purchaseOrderId) {
    String sql = "SELECT COUNT(*) FROM PurchaseOrders WHERE PurchaseOrderID = ?";
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, purchaseOrderId);
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        }
    } catch (SQLException e) {
        System.err.println("Lỗi khi kiểm tra ID phiếu nhập: " + e.getMessage());
    }
    return false; // Nếu có lỗi, giả định ID chưa tồn tại
}
    
    // Dùng id: String
    public Optional<PurchaseOrder> findById(String id) {
        String sql = "SELECT po.*, s.Name as SupplierName, e.FullName as EmployeeName " +
                    "FROM PurchaseOrders po " +
                    "LEFT JOIN Suppliers s ON po.SupplierID = s.SupplierID " +
                    "LEFT JOIN Employees e ON po.EmployeeID = e.EmployeeID " +
                    "WHERE po.PurchaseOrderID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                PurchaseOrder purchaseOrder = mapResultSetToPurchaseOrder(resultSet);
                
                // Load chi tiết đơn nhập hàng
                purchaseOrder.setPurchaseOrderDetails(
                    RepositoryFactory.getPurchaseOrderDetailRepository().findByPurchaseOrderId(id)
                );
                
                return Optional.of(purchaseOrder);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding purchase order by ID", e);
        }
    }
    
    @Override
    public List<PurchaseOrder> findAll() {
        String sql = "SELECT po.*, s.Name as SupplierName, e.FullName as EmployeeName " +
                    "FROM PurchaseOrders po " +
                    "LEFT JOIN Suppliers s ON po.SupplierID = s.SupplierID " +
                    "LEFT JOIN Employees e ON po.EmployeeID = e.EmployeeID " +
                    "ORDER BY po.OrderDate DESC";
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                purchaseOrders.add(mapResultSetToPurchaseOrder(resultSet));
            }
            return purchaseOrders;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all purchase orders", e);
        }
    }
    
    @Override
    public boolean exists(Integer id) {
        String sql = "SELECT COUNT(*) FROM PurchaseOrders WHERE PurchaseOrderID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if purchase order exists", e);
        }
    }
    
    // Tìm đơn nhập hàng theo nhà cung cấp
    public List<PurchaseOrder> findBySupplier(String supplierId) {
        String sql = "SELECT po.*, s.Name as SupplierName, e.FullName as EmployeeName " +
                    "FROM PurchaseOrders po " +
                    "LEFT JOIN Suppliers s ON po.SupplierID = s.SupplierID " +
                    "LEFT JOIN Employees e ON po.EmployeeID = e.EmployeeID " +
                    "WHERE po.SupplierID = ? " +
                    "ORDER BY po.OrderDate DESC";
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplierId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                purchaseOrders.add(mapResultSetToPurchaseOrder(resultSet));
            }
            return purchaseOrders;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding purchase orders by supplier", e);
        }
    }
    
    // Tìm đơn nhập hàng theo nhân viên tạo
    public List<PurchaseOrder> findByEmployee(String employeeId) {
        String sql = "SELECT po.*, s.Name as SupplierName, e.FullName as EmployeeName " +
                    "FROM PurchaseOrders po " +
                    "LEFT JOIN Suppliers s ON po.SupplierID = s.SupplierID " +
                    "LEFT JOIN Employees e ON po.EmployeeID = e.EmployeeID " +
                    "WHERE po.EmployeeID = ? " +
                    "ORDER BY po.OrderDate DESC";
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employeeId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                purchaseOrders.add(mapResultSetToPurchaseOrder(resultSet));
            }
            return purchaseOrders;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding purchase orders by employee", e);
        }
    }
    
    // Tìm đơn nhập hàng theo trạng thái
    public List<PurchaseOrder> findByStatus(String status) {
        String sql = "SELECT po.*, s.Name as SupplierName, e.FullName as EmployeeName " +
                    "FROM PurchaseOrders po " +
                    "LEFT JOIN Suppliers s ON po.SupplierID = s.SupplierID " +
                    "LEFT JOIN Employees e ON po.EmployeeID = e.EmployeeID " +
                    "WHERE po.Status = ? " +
                    "ORDER BY po.OrderDate DESC";
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                purchaseOrders.add(mapResultSetToPurchaseOrder(resultSet));
            }
            return purchaseOrders;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding purchase orders by status", e);
        }
    }
    
    // Tìm đơn nhập hàng trong khoảng thời gian
    public List<PurchaseOrder> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT po.*, s.Name as SupplierName, e.FullName as EmployeeName " +
                    "FROM PurchaseOrders po " +
                    "LEFT JOIN Suppliers s ON po.SupplierID = s.SupplierID " +
                    "LEFT JOIN Employees e ON po.EmployeeID = e.EmployeeID " +
                    "WHERE po.OrderDate BETWEEN ? AND ? " +
                    "ORDER BY po.OrderDate DESC";
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, startDate);
            statement.setObject(2, endDate);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                purchaseOrders.add(mapResultSetToPurchaseOrder(resultSet));
            }
            return purchaseOrders;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding purchase orders by date range", e);
        }
    }
    
    /**
 * Cập nhật tổng tiền cho phiếu nhập
 * @param purchaseOrderId Mã phiếu nhập
 */
public void updateTotalAmount(String purchaseOrderId) {
    System.out.println("Updating total amount for purchase order: " + purchaseOrderId);
    
    // Truy vấn tính tổng tiền từ chi tiết phiếu nhập
    String sql = "UPDATE PurchaseOrders " +
                "SET TotalAmount = (SELECT SUM(Quantity * UnitCost) " +
                "                  FROM PurchaseOrderDetails " +
                "                  WHERE PurchaseOrderID = ?) " +
                "WHERE PurchaseOrderID = ?";
    
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, purchaseOrderId);
        statement.setString(2, purchaseOrderId);
        
        int rowsAffected = statement.executeUpdate();
        System.out.println("Rows affected: " + rowsAffected);
        
        if (rowsAffected == 0) {
            System.err.println("Warning: No rows updated when updating total amount for purchase order: " + purchaseOrderId);
        }
    } catch (SQLException e) {
        System.err.println("SQL Error in updateTotalAmount: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Error updating purchase order total: " + e.getMessage(), e);
    }
}
    
    // Hoàn thành đơn nhập hàng (cập nhật tồn kho sản phẩm)
    public void completePurchaseOrder(String purchaseOrderId) {
        Optional<PurchaseOrder> orderOpt = findById(purchaseOrderId);
        if (!orderOpt.isPresent()) {
            throw new RuntimeException("Purchase order not found");
        }
        
        PurchaseOrder order = orderOpt.get();
        if ("Completed".equals(order.getStatus())) {
            throw new RuntimeException("Purchase order already completed");
        }
        
        try {
            // Thiết lập auto-commit thành false để thực hiện transaction
            connection.setAutoCommit(false);
            
            try {
                // Cập nhật trạng thái đơn nhập hàng thành "Completed"
                String sql = "UPDATE PurchaseOrders SET Status = 'Completed' WHERE PurchaseOrderID = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, purchaseOrderId);
                    statement.executeUpdate();
                }
                
                // Cập nhật tồn kho sản phẩm
                for (PurchaseOrderDetail detail : order.getPurchaseOrderDetails()) {
                    detail.updateStock();
                }
                
                // Commit transaction
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error completing purchase order", e);
        }
    }

    /**
 * Lưu đơn nhập hàng, thực hiện tạo mới nếu chưa có ID hoặc cập nhật nếu đã có ID
 * @param purchaseOrder Đơn nhập hàng cần lưu
 * @return Mã đơn nhập hàng đã lưu
 * @throws SQLException Nếu có lỗi khi thao tác với CSDL
 */
public String save(PurchaseOrder purchaseOrder) {
    try {
        if (purchaseOrder.getPurchaseOrderId() == null || purchaseOrder.getPurchaseOrderId().isEmpty()) {
            // Nếu chưa có ID, tạo ID mới và thêm mới
            purchaseOrder.setPurchaseOrderId(generatePurchaseOrderId());
            add(purchaseOrder);
        } else {
            // Nếu đã có ID, kiểm tra xem đơn hàng đã tồn tại chưa
            Optional<PurchaseOrder> existingOrder = findById(purchaseOrder.getPurchaseOrderId());
            if (existingOrder.isPresent()) {
                // Đã tồn tại, thực hiện cập nhật
                update(purchaseOrder);
            } else {
                // Không tồn tại nhưng có ID, vẫn thêm mới
                add(purchaseOrder);
            }
        }
        
        // Nếu có chi tiết đơn hàng, lưu từng chi tiết
        if (purchaseOrder.getPurchaseOrderDetails() != null && !purchaseOrder.getPurchaseOrderDetails().isEmpty()) {
            for (PurchaseOrderDetail detail : purchaseOrder.getPurchaseOrderDetails()) {
                // Đảm bảo chi tiết có liên kết với đơn hàng
                detail.setPurchaseOrder(purchaseOrder);
                
                // Lưu chi tiết thông qua repository tương ứng
                RepositoryFactory.getPurchaseOrderDetailRepository().save(detail);
            }
        }
        
        // Cập nhật lại tổng tiền sau khi lưu chi tiết
        updateTotalAmount(purchaseOrder.getPurchaseOrderId());
        
        return purchaseOrder.getPurchaseOrderId();
    } catch (Exception e) {
        throw new RuntimeException("Error saving purchase order", e);
    }
}
    
    private PurchaseOrder mapResultSetToPurchaseOrder(ResultSet resultSet) throws SQLException {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPurchaseOrderId(resultSet.getString("PurchaseOrderID"));
        purchaseOrder.setOrderDate(resultSet.getObject("OrderDate", LocalDateTime.class));
        purchaseOrder.setTotalAmount(resultSet.getBigDecimal("TotalAmount"));
        purchaseOrder.setStatus(resultSet.getString("Status"));
        
        // Tạo và thiết lập thông tin nhà cung cấp
        String supplierId = resultSet.getString("SupplierID");
        if (supplierId != null) {
            Supplier supplier = new Supplier();
            supplier.setSupplierId(supplierId);
            supplier.setName(resultSet.getString("SupplierName"));
            purchaseOrder.setSupplier(supplier);
        }
        
        // Tạo và thiết lập thông tin nhân viên
        String employeeId = resultSet.getString("EmployeeID");
        if (employeeId != null) {
            Employee employee = new Employee();
            employee.setEmployeeId(employeeId);
            employee.setFullName(resultSet.getString("EmployeeName"));
            purchaseOrder.setEmployee(employee);
        }
        
        return purchaseOrder;
    }

    @Override
    public Optional<PurchaseOrder> findById(Integer id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public boolean delete(Integer id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}