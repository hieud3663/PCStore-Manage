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
    
    @Override
    public PurchaseOrder add(PurchaseOrder purchaseOrder) {
        String sql = "INSERT INTO PurchaseOrders (SupplierID, EmployeeID, OrderDate, TotalAmount, Status) " +
                    "VALUES (?, ?, ?, ?, ?)";
                    
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, purchaseOrder.getSupplier() != null ? 
                    purchaseOrder.getSupplier().getSupplierId() : null);
            statement.setString(2, purchaseOrder.getEmployee() != null ? 
                    purchaseOrder.getEmployee().getEmployeeId() : null);
            
            // Nếu ngày đặt hàng chưa được thiết lập, sử dụng thời gian hiện tại
            if (purchaseOrder.getOrderDate() == null) {
                purchaseOrder.setOrderDate(LocalDateTime.now());
            }
            statement.setObject(3, purchaseOrder.getOrderDate());
            
            statement.setBigDecimal(4, purchaseOrder.getTotalAmount() != null ? 
                    purchaseOrder.getTotalAmount() : BigDecimal.ZERO);
            statement.setString(5, purchaseOrder.getStatus());
            
            statement.executeUpdate();
            
            // // Lấy ID được tự động tạo
            // ResultSet generatedKeys = statement.getGeneratedKeys();
            // if (generatedKeys.next()) {
            //     int generatedId = generatedKeys.getInt(1);
            //     purchaseOrder.setPurchaseOrderId(generatedId);
            // }
            
            LocalDateTime now = LocalDateTime.now();
            purchaseOrder.setCreatedAt(now);
            purchaseOrder.setUpdatedAt(now);
            
            // Lưu các chi tiết đơn nhập hàng nếu có
            if (purchaseOrder.getPurchaseOrderDetails() != null && 
                    !purchaseOrder.getPurchaseOrderDetails().isEmpty()) {
                for (PurchaseOrderDetail detail : purchaseOrder.getPurchaseOrderDetails()) {
                    detail.setPurchaseOrder(purchaseOrder);
                    RepositoryFactory.getPurchaseOrderDetailRepository().add(detail);
                }
            }
            
            return purchaseOrder;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding purchase order", e);
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

    public String generatePurchaseOrderId() {
        String prefix = "PO-";
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sql = "SELECT COUNT(*) AS count FROM purchase_orders WHERE order_date = CURDATE()";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int count = rs.getInt("count") + 1; // Tăng số thứ tự lên 1
                String formattedCount = String.format("%04d", count); // Định dạng số thứ tự thành 4 chữ số
                return prefix + datePart + "-" + formattedCount;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Trả về mã mặc định nếu có lỗi
        return prefix + datePart + "-0001";
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
    
    // Cập nhật tổng tiền đơn nhập hàng
    public void updateTotalAmount(String purchaseOrderId) {
        String sql = "UPDATE PurchaseOrders SET TotalAmount = (" +
                    "SELECT SUM(Quantity * UnitPrice) " +
                    "FROM PurchaseOrderDetails " +
                    "WHERE PurchaseOrderID = ?) " +
                    "WHERE PurchaseOrderID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, purchaseOrderId);
            statement.setString(2, purchaseOrderId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating purchase order total", e);
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