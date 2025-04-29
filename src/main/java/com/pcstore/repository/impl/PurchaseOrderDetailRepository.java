package com.pcstore.repository.impl;

import com.pcstore.repository.Repository;
import com.pcstore.repository.RepositoryFactory;
import com.pcstore.model.Product;
import com.pcstore.model.PurchaseOrder;
import com.pcstore.model.PurchaseOrderDetail;

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
 * Repository implementation cho PurchaseOrderDetail entity
 */
public class PurchaseOrderDetailRepository implements Repository<PurchaseOrderDetail, Integer> {
    private Connection connection;
    private RepositoryFactory RepositoryFactory;
    
    public PurchaseOrderDetailRepository(Connection connection, RepositoryFactory RepositoryFactory) {
        this.connection = connection;
        this.RepositoryFactory = RepositoryFactory;
    }
    
    @Override
    public PurchaseOrderDetail add(PurchaseOrderDetail detail) {
        String sql = "INSERT INTO PurchaseOrderDetails (PurchaseOrderID, ProductID, Quantity, UnitPrice) " +
                    "VALUES (?, ?, ?, ?)";
                    
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, detail.getPurchaseOrder().getPurchaseOrderId());
            statement.setString(2, detail.getProduct().getProductId());
            statement.setInt(3, detail.getQuantity());
            statement.setBigDecimal(4, detail.getUnitPrice());
            
            statement.executeUpdate();
            
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                detail.setPurchaseOrderDetailId(generatedKeys.getInt(1));
            }
            
            LocalDateTime now = LocalDateTime.now();
            detail.setCreatedAt(now);
            detail.setUpdatedAt(now);
            
            // Cập nhật tổng tiền đơn nhập hàng
            // RepositoryFactory.getPurchaseOrderRepository().updateTotalAmount(detail.getPurchaseOrder().getPurchaseOrderId());
            
            // Nếu đơn nhập hàng đã hoàn thành, cập nhật số lượng tồn kho
            PurchaseOrder order = detail.getPurchaseOrder();
            if ("Completed".equals(order.getStatus())) {
                detail.updateStock();
            }
            
            return detail;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding purchase order detail", e);
        }
    }
    /**
 * Lưu chi tiết đơn nhập hàng, thực hiện thêm mới nếu chưa có ID hoặc cập nhật nếu đã có ID
 * @param detail Chi tiết đơn nhập hàng cần lưu
 * @return Chi tiết đơn nhập hàng đã được lưu
 */
public PurchaseOrderDetail save(PurchaseOrderDetail detail) {
    if (detail == null) {
        throw new IllegalArgumentException("Purchase order detail cannot be null");
    }
    
    try {
        if (detail.getPurchaseOrderDetailId() == 0) {
            // Nếu chưa có ID (ID = 0 hoặc null), thực hiện thêm mới
            return add(detail);
        } else {
            // Nếu đã có ID, kiểm tra xem chi tiết này có tồn tại trong database không
            if (exists(detail.getPurchaseOrderDetailId())) {
                // Nếu tồn tại, thực hiện cập nhật
                return update(detail);
            } else {
                // Nếu không tồn tại nhưng đã có ID, vẫn thực hiện thêm mới
                return add(detail);
            }
        }
    } catch (Exception e) {
        throw new RuntimeException("Error saving purchase order detail", e);
    }
}

/**
 * Tìm chi tiết đơn nhập hàng dựa trên đơn hàng và sản phẩm
 * @param purchaseOrderId ID của đơn nhập hàng
 * @param productId ID của sản phẩm
 * @return Chi tiết đơn nhập hàng tương ứng (nếu tồn tại)
 */
public Optional<PurchaseOrderDetail> findByPurchaseOrderIdAndProductId(String purchaseOrderId, String productId) {
    String sql = "SELECT pod.*, p.ProductName " +
                "FROM PurchaseOrderDetails pod " +
                "JOIN Products p ON pod.ProductID = p.ProductID " +
                "WHERE pod.PurchaseOrderID = ? AND pod.ProductID = ?";
    
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, purchaseOrderId);
        statement.setString(2, productId);
        ResultSet resultSet = statement.executeQuery();
        
        if (resultSet.next()) {
            return Optional.of(mapResultSetToDetail(resultSet));
        }
        return Optional.empty();
    } catch (SQLException e) {
        throw new RuntimeException("Error finding purchase order detail by purchase order ID and product ID", e);
    }
}

/**
 * Lưu chi tiết đơn nhập hàng dựa trên đơn hàng và sản phẩm (sẽ cập nhật nếu đã tồn tại)
 * @param detail Chi tiết đơn nhập hàng cần lưu
 * @return Chi tiết đơn nhập hàng đã được lưu
 */
public PurchaseOrderDetail saveByOrderAndProduct(PurchaseOrderDetail detail) {
    if (detail == null || detail.getPurchaseOrder() == null || detail.getProduct() == null) {
        throw new IllegalArgumentException("Purchase order detail, order, and product cannot be null");
    }
    
    try {
        // Tìm chi tiết đơn hàng dựa trên ID đơn hàng và ID sản phẩm
        Optional<PurchaseOrderDetail> existingDetail = findByPurchaseOrderIdAndProductId(
            detail.getPurchaseOrder().getPurchaseOrderId(),
            detail.getProduct().getProductId()
        );
        
        if (existingDetail.isPresent()) {
            // Nếu đã tồn tại, cập nhật số lượng và đơn giá
            PurchaseOrderDetail updatedDetail = existingDetail.get();
            updatedDetail.setQuantity(detail.getQuantity());
            updatedDetail.setUnitPrice(detail.getUnitPrice());
            return update(updatedDetail);
        } else {
            // Nếu chưa tồn tại, thực hiện thêm mới
            return add(detail);
        }
    } catch (Exception e) {
        throw new RuntimeException("Error saving purchase order detail by order and product", e);
    }
}
    
    @Override
    public PurchaseOrderDetail update(PurchaseOrderDetail detail) {
        // Lấy thông tin chi tiết đơn nhập cũ
        Optional<PurchaseOrderDetail> oldDetailOpt = findById(detail.getPurchaseOrderDetailId());
        if (!oldDetailOpt.isPresent()) {
            throw new RuntimeException("Cannot update non-existent purchase order detail");
        }
        
        PurchaseOrderDetail oldDetail = oldDetailOpt.get();
        int oldQuantity = oldDetail.getQuantity();
        int newQuantity = detail.getQuantity();
        int quantityDiff = newQuantity - oldQuantity;
        
        String sql = "UPDATE PurchaseOrderDetails SET Quantity = ?, UnitPrice = ? " +
                    "WHERE PurchaseOrderDetailID = ?";
                    
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, detail.getQuantity());
            statement.setBigDecimal(2, detail.getUnitPrice());
            statement.setInt(3, detail.getPurchaseOrderDetailId());
            
            statement.executeUpdate();
            
            detail.setUpdatedAt(LocalDateTime.now());
            
            // Cập nhật tổng tiền đơn nhập hàng
            RepositoryFactory.getPurchaseOrderRepository().updateTotalAmount(detail.getPurchaseOrder().getPurchaseOrderId());
            
            // Nếu đơn nhập hàng đã hoàn thành và số lượng thay đổi, cập nhật tồn kho
            PurchaseOrder order = detail.getPurchaseOrder();
            if ("Completed".equals(order.getStatus()) && quantityDiff != 0) {
                detail.updateStock();
            }
            
            return detail;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating purchase order detail", e);
        }
    }
    
    @Override
    public boolean delete(Integer id) {
        // Lấy thông tin chi tiết đơn nhập hàng trước khi xóa
        Optional<PurchaseOrderDetail> detailOpt = findById(id);
        if (!detailOpt.isPresent()) {
            return false;
        }
        
        PurchaseOrderDetail detail = detailOpt.get();
        String purchaseOrderId = detail.getPurchaseOrder().getPurchaseOrderId();
        
        String sql = "DELETE FROM PurchaseOrderDetails WHERE PurchaseOrderDetailID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Cập nhật tổng tiền đơn nhập hàng
                RepositoryFactory.getPurchaseOrderRepository().updateTotalAmount(purchaseOrderId);
                
                // Nếu đơn nhập hàng đã hoàn thành, cập nhật số lượng tồn kho
                PurchaseOrder order = detail.getPurchaseOrder();
                if ("Completed".equals(order.getStatus())) {
                    detail.updateStock();
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting purchase order detail", e);
        }
    }
    
    @Override
    public Optional<PurchaseOrderDetail> findById(Integer id) {
        String sql = "SELECT pod.*, p.ProductName " +
                    "FROM PurchaseOrderDetails pod " +
                    "JOIN Products p ON pod.ProductID = p.ProductID " +
                    "WHERE pod.PurchaseOrderDetailID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToDetail(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding purchase order detail by ID", e);
        }
    }
    
    @Override
    public List<PurchaseOrderDetail> findAll() {
        String sql = "SELECT pod.*, p.ProductName " +
                    "FROM PurchaseOrderDetails pod " +
                    "JOIN Products p ON pod.ProductID = p.ProductID";
        List<PurchaseOrderDetail> details = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                details.add(mapResultSetToDetail(resultSet));
            }
            return details;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all purchase order details", e);
        }
    }
    
    @Override
    public boolean exists(Integer id) {
        String sql = "SELECT COUNT(*) FROM PurchaseOrderDetails WHERE PurchaseOrderDetailID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if purchase order detail exists", e);
        }
    }
    
    // Tìm chi tiết đơn nhập hàng theo đơn nhập hàng
    public List<PurchaseOrderDetail> findByPurchaseOrderId(String purchaseOrderId) {
        String sql = "SELECT pod.*, p.ProductName " +
                    "FROM PurchaseOrderDetails pod " +
                    "JOIN Products p ON pod.ProductID = p.ProductID " +
                    "WHERE pod.PurchaseOrderID = ?";
        List<PurchaseOrderDetail> details = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, purchaseOrderId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                details.add(mapResultSetToDetail(resultSet));
            }
            return details;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding purchase order details by purchase order ID", e);
        }
    }

    //Xóa chi tiết đơn nhập hàng theo đơn nhập hàng
    public boolean deleteByPurchaseOrderId(String purchaseOrderId) {
        String sql = "DELETE FROM PurchaseOrderDetails WHERE PurchaseOrderID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, purchaseOrderId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting purchase order details by purchase order ID", e);
        }
    }
    
    // Tìm chi tiết đơn nhập hàng theo sản phẩm
    public List<PurchaseOrderDetail> findByProductId(String productId) {
        String sql = "SELECT pod.*, p.ProductName " +
                    "FROM PurchaseOrderDetails pod " +
                    "JOIN Products p ON pod.ProductID = p.ProductID " +
                    "WHERE pod.ProductID = ?";
        List<PurchaseOrderDetail> details = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                details.add(mapResultSetToDetail(resultSet));
            }
            return details;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding purchase order details by product ID", e);
        }
    }
    
    // Tính tổng số lượng sản phẩm đã nhập theo thời gian
    public int calculateTotalQuantityByProductAndDateRange(String productId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT SUM(pod.Quantity) AS TotalQuantity " +
                    "FROM PurchaseOrderDetails pod " +
                    "JOIN PurchaseOrders po ON pod.PurchaseOrderID = po.PurchaseOrderID " +
                    "WHERE pod.ProductID = ? AND po.OrderDate BETWEEN ? AND ? " +
                    "AND po.Status = 'Completed'";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            statement.setObject(2, startDate);
            statement.setObject(3, endDate);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt("TotalQuantity");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating total quantity by product and date range", e);
        }
    }
    
    private PurchaseOrderDetail mapResultSetToDetail(ResultSet resultSet) throws SQLException {
        PurchaseOrderDetail detail = new PurchaseOrderDetail();
        detail.setPurchaseOrderDetailId(resultSet.getInt("PurchaseOrderDetailID"));
        detail.setQuantity(resultSet.getInt("Quantity"));
        detail.setUnitPrice(resultSet.getBigDecimal("UnitPrice"));
        
        // Tạo đối tượng PurchaseOrder giả lập chỉ với ID
        PurchaseOrder order = new PurchaseOrder();
        order.setPurchaseOrderId(resultSet.getString("PurchaseOrderID"));
        detail.setPurchaseOrder(order);
        
        // Tạo đối tượng Product giả lập với ID và tên
        Product product = new Product();
        product.setProductId(resultSet.getString("ProductID"));
        product.setProductName(resultSet.getString("ProductName"));
        detail.setProduct(product);
        
        return detail;
    }
}