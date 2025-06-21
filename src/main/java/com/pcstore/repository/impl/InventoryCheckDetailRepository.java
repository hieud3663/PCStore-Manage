package com.pcstore.repository.impl;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pcstore.model.InventoryCheck;
import com.pcstore.model.InventoryCheckDetail;
import com.pcstore.model.Product;
import com.pcstore.repository.Repository;

/**
 * Repository implementation cho InventoryCheckDetail entity
 */
public class InventoryCheckDetailRepository implements Repository<InventoryCheckDetail, Integer> {
    private Connection connection;

    public InventoryCheckDetailRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public InventoryCheckDetail add(InventoryCheckDetail detail) {
        String sql = "INSERT INTO InventoryCheckDetails (InventoryCheckID, ProductID, SystemQuantity, " +
                     "ActualQuantity, Reason, LossValue) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, detail.getInventoryCheck().getId());
            statement.setString(2, detail.getProduct().getProductId());
            statement.setInt(3, detail.getSystemQuantity());
            statement.setInt(4, detail.getActualQuantity());
            statement.setString(5, detail.getReason());
            statement.setBigDecimal(6, detail.getLossValue());

            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                detail.setDetailId(generatedKeys.getInt(1));
            }

            return detail;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding inventory check detail", e);
        }
    }

    @Override
    public InventoryCheckDetail update(InventoryCheckDetail detail) {
        String sql = "UPDATE InventoryCheckDetails SET InventoryCheckID = ?, ProductID = ?, " +
                     "SystemQuantity = ?, ActualQuantity = ?, Reason = ?, LossValue = ? " +
                     "WHERE InventoryCheckDetailID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, detail.getInventoryCheck().getId());
            statement.setString(2, detail.getProduct().getProductId());
            statement.setInt(3, detail.getSystemQuantity());
            statement.setInt(4, detail.getActualQuantity());
            statement.setString(5, detail.getReason());
            statement.setBigDecimal(6, detail.getLossValue());
            statement.setInt(7, detail.getId());

            statement.executeUpdate();
            return detail;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating inventory check detail", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM InventoryCheckDetails WHERE InventoryCheckDetailID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting inventory check detail", e);
        }
    }

    @Override
    public Optional<InventoryCheckDetail> findById(Integer id) {
        String sql = "SELECT icd.*, p.ProductName, p.Price, ic.CheckCode " +
                     "FROM InventoryCheckDetails icd " +
                     "LEFT JOIN Products p ON icd.ProductID = p.ProductID " +
                     "LEFT JOIN InventoryChecks ic ON icd.InventoryCheckID = ic.InventoryCheckID " +
                     "WHERE icd.InventoryCheckDetailID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToInventoryCheckDetail(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inventory check detail by ID", e);
        }
    }

    @Override
    public List<InventoryCheckDetail> findAll() {
        String sql = "SELECT icd.*, p.ProductName, p.Price, ic.CheckCode " +
                     "FROM InventoryCheckDetails icd " +
                     "LEFT JOIN Products p ON icd.ProductID = p.ProductID " +
                     "LEFT JOIN InventoryChecks ic ON icd.InventoryCheckID = ic.InventoryCheckID " +
                     "ORDER BY icd.CreatedAt DESC";

        List<InventoryCheckDetail> details = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                details.add(mapResultSetToInventoryCheckDetail(resultSet));
            }
            return details;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all inventory check details", e);
        }
    }

    @Override
    public boolean exists(Integer id) {
        String sql = "SELECT COUNT(*) FROM InventoryCheckDetails WHERE InventoryCheckDetailID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking inventory check detail existence", e);
        }
    }

    /**
     * Tìm chi tiết kiểm kê theo ID phiếu kiểm kê
     * @param inventoryCheckId ID phiếu kiểm kê
     * @return Danh sách chi tiết kiểm kê
     */
    public List<InventoryCheckDetail> findByInventoryCheckId(Integer inventoryCheckId) {
        String sql = "SELECT icd.*, p.ProductName, p.Price, ic.CheckCode " +
                     "FROM InventoryCheckDetails icd " +
                     "LEFT JOIN Products p ON icd.ProductID = p.ProductID " +
                     "LEFT JOIN InventoryChecks ic ON icd.InventoryCheckID = ic.InventoryCheckID " +
                     "WHERE icd.InventoryCheckID = ? " +
                     "ORDER BY p.ProductName";

        List<InventoryCheckDetail> details = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, inventoryCheckId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                details.add(mapResultSetToInventoryCheckDetail(resultSet));
            }
            return details;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inventory check details by check ID", e);
        }
    }

    /**
     * Tìm chi tiết kiểm kê theo sản phẩm
     * @param productId ID sản phẩm
     * @return Danh sách chi tiết kiểm kê của sản phẩm
     */
    public List<InventoryCheckDetail> findByProductId(String productId) {
        String sql = "SELECT icd.*, p.ProductName, p.Price, ic.CheckCode " +
                     "FROM InventoryCheckDetails icd " +
                     "LEFT JOIN Products p ON icd.ProductID = p.ProductID " +
                     "LEFT JOIN InventoryChecks ic ON icd.InventoryCheckID = ic.InventoryCheckID " +
                     "WHERE icd.ProductID = ? " +
                     "ORDER BY ic.CheckDate DESC";

        List<InventoryCheckDetail> details = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                details.add(mapResultSetToInventoryCheckDetail(resultSet));
            }
            return details;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inventory check details by product ID", e);
        }
    }

    /**
     * Tìm chi tiết kiểm kê có chênh lệch
     * @param inventoryCheckId ID phiếu kiểm kê
     * @return Danh sách chi tiết có chênh lệch
     */
    public List<InventoryCheckDetail> findDiscrepanciesByCheckId(Integer inventoryCheckId) {
        String sql = "SELECT icd.*, p.ProductName, p.Price, ic.CheckCode " +
                     "FROM InventoryCheckDetails icd " +
                     "LEFT JOIN Products p ON icd.ProductID = p.ProductID " +
                     "LEFT JOIN InventoryChecks ic ON icd.InventoryCheckID = ic.InventoryCheckID " +
                     "WHERE icd.InventoryCheckID = ? AND icd.ActualQuantity != icd.SystemQuantity " +
                     "ORDER BY ABS(icd.ActualQuantity - icd.SystemQuantity) DESC";

        List<InventoryCheckDetail> details = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, inventoryCheckId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                details.add(mapResultSetToInventoryCheckDetail(resultSet));
            }
            return details;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding discrepancies by check ID", e);
        }
    }

    /**
     * Xóa tất cả chi tiết kiểm kê của một phiếu kiểm kê
     * @param inventoryCheckId ID phiếu kiểm kê
     * @return true nếu xóa thành công
     */
    public boolean deleteByInventoryCheckId(Integer inventoryCheckId) {
        String sql = "DELETE FROM InventoryCheckDetails WHERE InventoryCheckID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, inventoryCheckId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting inventory check details by check ID", e);
        }
    }

    /**
     * Tính tổng giá trị thất thoát của một phiếu kiểm kê
     * @param inventoryCheckId ID phiếu kiểm kê
     * @return Tổng giá trị thất thoát
     */
    public BigDecimal calculateTotalLossValue(Integer inventoryCheckId) {
        String sql = "SELECT ISNULL(SUM(LossValue), 0) as TotalLoss " +
                     "FROM InventoryCheckDetails WHERE InventoryCheckID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, inventoryCheckId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getBigDecimal("TotalLoss");
            }
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating total loss value", e);
        }
    }

    /**
     * Map ResultSet sang InventoryCheckDetail object
     */
    private InventoryCheckDetail mapResultSetToInventoryCheckDetail(ResultSet resultSet) throws SQLException {
        InventoryCheckDetail detail = new InventoryCheckDetail();
        detail.setDetailId(resultSet.getInt("InventoryCheckDetailID"));
        detail.setSystemQuantity(resultSet.getInt("SystemQuantity"));
        detail.setActualQuantity(resultSet.getInt("ActualQuantity"));
        detail.setReason(resultSet.getString("Reason"));
        detail.setLossValue(resultSet.getBigDecimal("LossValue"));

        // Tính chênh lệch
        detail.setDiscrepancy(detail.getActualQuantity() - detail.getSystemQuantity());

        // Xử lý ngày tháng
        Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
        if (createdAt != null) {
            detail.setCreatedAt(createdAt.toLocalDateTime());
        }

        // Xử lý Product
        String productId = resultSet.getString("ProductID");
        if (productId != null) {
            Product product = new Product();
            product.setProductId(productId);
            product.setProductName(resultSet.getString("ProductName"));
            product.setPrice(resultSet.getBigDecimal("Price"));
            detail.setProduct(product);
        }

        // Xử lý InventoryCheck
        Integer inventoryCheckId = resultSet.getInt("InventoryCheckID");
        if (inventoryCheckId != null) {
            InventoryCheck inventoryCheck = new InventoryCheck();
            inventoryCheck.setCheckId(inventoryCheckId);
            inventoryCheck.setCheckCode(resultSet.getString("CheckCode"));
            detail.setInventoryCheck(inventoryCheck);
        }

        return detail;
    }
}
