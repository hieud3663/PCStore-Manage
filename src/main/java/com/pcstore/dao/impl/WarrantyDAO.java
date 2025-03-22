package com.pcstore.dao.impl;

import com.pcstore.dao.DAO;
import com.pcstore.dao.DAOFactory;
import com.pcstore.model.Warranty;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Customer;
import com.pcstore.model.Product;
import com.pcstore.model.RepairService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO implementation cho Warranty entity
 */
public class WarrantyDAO implements DAO<Warranty, Integer> {
    private Connection connection;
    
    public WarrantyDAO(Connection connection) {
        this.connection = connection;
    }
    
    // public WarrantyDAO(Connection connection2, DAOFactory daoFactory) {
    //     //TODO Auto-generated constructor stub
    // }

    @Override
    public Warranty add(Warranty warranty) {
        String sql = "INSERT INTO Warranties (InvoiceDetailID, StartDate, EndDate, WarrantyTerms) " +
                     "VALUES (?, ?, ?, ?)";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Thiết lập thời gian tạo và cập nhật
            LocalDateTime now = LocalDateTime.now();
            warranty.setCreatedAt(now);
            warranty.setUpdatedAt(now);
            
            statement.setInt(1, warranty.getInvoiceDetail().getInvoiceDetailId());
            statement.setTimestamp(2, Timestamp.valueOf(warranty.getStartDate()));
            statement.setTimestamp(3, Timestamp.valueOf(warranty.getEndDate()));
            statement.setString(4, warranty.getWarrantyTerms());
            
            statement.executeUpdate();
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    warranty.setWarrantyId(generatedKeys.getString(1));
                }
            }
            
            return warranty;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm bảo hành", e);
        }
    }
    
    @Override
    public Warranty update(Warranty warranty) {
        String sql = "UPDATE Warranties SET StartDate = ?, EndDate = ?, WarrantyTerms = ? " +
                     "WHERE WarrantyID = ?";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Cập nhật thời gian cập nhật
            warranty.setUpdatedAt(LocalDateTime.now());
            
            statement.setTimestamp(1, Timestamp.valueOf(warranty.getStartDate()));
            statement.setTimestamp(2, Timestamp.valueOf(warranty.getEndDate()));
            statement.setString(3, warranty.getWarrantyTerms());
            statement.setString(4, warranty.getWarrantyId());
            
            statement.executeUpdate();
            
            return warranty;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật bảo hành", e);
        }
    }
    
    @Override
    public boolean delete(Integer warrantyId) {
        String sql = "DELETE FROM Warranties WHERE WarrantyID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, warrantyId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa bảo hành", e);
        }
    }
    
    @Override
    public Optional<Warranty> findById(Integer warrantyId) {
        String sql = "SELECT w.*, i.InvoiceID, i.ProductID, i.UnitPrice, " +
                     "p.ProductName, " +
                     "inv.CustomerID, c.FullName as CustomerName, " +
                     "rs.RepairID, rs.Status as RepairStatus " +
                     "FROM Warranties w " +
                     "JOIN InvoiceDetails i ON w.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "LEFT JOIN RepairServices rs ON rs.WarrantyID = w.WarrantyID " +
                     "WHERE w.WarrantyID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, warrantyId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToWarranty(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm bảo hành theo ID", e);
        }
    }
    
    @Override
    public List<Warranty> findAll() {
        String sql = "SELECT w.*, i.InvoiceID, i.ProductID, i.UnitPrice, " +
                     "p.ProductName, " +
                     "inv.CustomerID, c.FullName as CustomerName, " +
                     "rs.RepairID, rs.Status as RepairStatus " +
                     "FROM Warranties w " +
                     "JOIN InvoiceDetails i ON w.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "LEFT JOIN RepairServices rs ON rs.WarrantyID = w.WarrantyID";
                     
        List<Warranty> warranties = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                warranties.add(mapResultSetToWarranty(resultSet));
            }
            return warranties;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy tất cả bảo hành", e);
        }
    }
    
    @Override
    public boolean exists(Integer warrantyId) {
        String sql = "SELECT COUNT(*) FROM Warranties WHERE WarrantyID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, warrantyId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra tồn tại bảo hành", e);
        }
    }
    
    // Tìm bảo hành theo chi tiết hóa đơn
    public Optional<Warranty> findByInvoiceDetailId(Integer invoiceDetailId) {
        String sql = "SELECT w.*, i.InvoiceID, i.ProductID, i.UnitPrice, " +
                     "p.ProductName, " +
                     "inv.CustomerID, c.FullName as CustomerName, " +
                     "rs.RepairID, rs.Status as RepairStatus " +
                     "FROM Warranties w " +
                     "JOIN InvoiceDetails i ON w.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "LEFT JOIN RepairServices rs ON rs.WarrantyID = w.WarrantyID " +
                     "WHERE w.InvoiceDetailID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, invoiceDetailId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToWarranty(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm bảo hành theo chi tiết hóa đơn", e);
        }
    }
    
    // Tìm bảo hành theo khách hàng
    public List<Warranty> findByCustomerId(String customerId) {
        String sql = "SELECT w.*, i.InvoiceID, i.ProductID, i.UnitPrice, " +
                     "p.ProductName, " +
                     "inv.CustomerID, c.FullName as CustomerName, " +
                     "rs.RepairID, rs.Status as RepairStatus " +
                     "FROM Warranties w " +
                     "JOIN InvoiceDetails i ON w.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "LEFT JOIN RepairServices rs ON rs.WarrantyID = w.WarrantyID " +
                     "WHERE inv.CustomerID = ?";
        
        List<Warranty> warranties = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                warranties.add(mapResultSetToWarranty(resultSet));
            }
            return warranties;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm bảo hành theo khách hàng", e);
        }
    }
    
    // Tìm bảo hành theo sản phẩm
    public List<Warranty> findByProductId(String productId) {
        String sql = "SELECT w.*, i.InvoiceID, i.ProductID, i.UnitPrice, " +
                     "p.ProductName, " +
                     "inv.CustomerID, c.FullName as CustomerName, " +
                     "rs.RepairID, rs.Status as RepairStatus " +
                     "FROM Warranties w " +
                     "JOIN InvoiceDetails i ON w.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "LEFT JOIN RepairServices rs ON rs.WarrantyID = w.WarrantyID " +
                     "WHERE i.ProductID = ?";
        
        List<Warranty> warranties = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                warranties.add(mapResultSetToWarranty(resultSet));
            }
            return warranties;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm bảo hành theo sản phẩm", e);
        }
    }
    
    // Tìm bảo hành còn hiệu lực
    public List<Warranty> findActiveWarranties() {
        String sql = "SELECT w.*, i.InvoiceID, i.ProductID, i.UnitPrice, " +
                     "p.ProductName, " +
                     "inv.CustomerID, c.FullName as CustomerName, " +
                     "rs.RepairID, rs.Status as RepairStatus " +
                     "FROM Warranties w " +
                     "JOIN InvoiceDetails i ON w.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "LEFT JOIN RepairServices rs ON rs.WarrantyID = w.WarrantyID " +
                     "WHERE w.EndDate >= CURRENT_TIMESTAMP " +
                     "AND rs.RepairID IS NULL";
        
        List<Warranty> warranties = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                warranties.add(mapResultSetToWarranty(resultSet));
            }
            return warranties;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy bảo hành còn hiệu lực", e);
        }
    }
    
    // Tìm bảo hành hết hạn
    public List<Warranty> findExpiredWarranties() {
        String sql = "SELECT w.*, i.InvoiceID, i.ProductID, i.UnitPrice, " +
                     "p.ProductName, " +
                     "inv.CustomerID, c.FullName as CustomerName, " +
                     "rs.RepairID, rs.Status as RepairStatus " +
                     "FROM Warranties w " +
                     "JOIN InvoiceDetails i ON w.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "LEFT JOIN RepairServices rs ON rs.WarrantyID = w.WarrantyID " +
                     "WHERE w.EndDate < CURRENT_TIMESTAMP";
        
        List<Warranty> warranties = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                warranties.add(mapResultSetToWarranty(resultSet));
            }
            return warranties;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy bảo hành hết hạn", e);
        }
    }
    
    // Tìm bảo hành sắp hết hạn
    public List<Warranty> findWarrantiesAboutToExpire(int daysThreshold) {
        String sql = "SELECT w.*, i.InvoiceID, i.ProductID, i.UnitPrice, " +
                     "p.ProductName, " +
                     "inv.CustomerID, c.FullName as CustomerName, " +
                     "rs.RepairID, rs.Status as RepairStatus " +
                     "FROM Warranties w " +
                     "JOIN InvoiceDetails i ON w.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "LEFT JOIN RepairServices rs ON rs.WarrantyID = w.WarrantyID " +
                     "WHERE w.EndDate BETWEEN CURRENT_TIMESTAMP AND DATEADD(DAY, ?, CURRENT_TIMESTAMP) " +
                     "AND rs.RepairID IS NULL";
        
        List<Warranty> warranties = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, daysThreshold);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                warranties.add(mapResultSetToWarranty(resultSet));
            }
            return warranties;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy bảo hành sắp hết hạn", e);
        }
    }
    
    // Tìm bảo hành đã sử dụng
    public List<Warranty> findUsedWarranties() {
        String sql = "SELECT w.*, i.InvoiceID, i.ProductID, i.UnitPrice, " +
                     "p.ProductName, " +
                     "inv.CustomerID, c.FullName as CustomerName, " +
                     "rs.RepairID, rs.Status as RepairStatus " +
                     "FROM Warranties w " +
                     "JOIN InvoiceDetails i ON w.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "JOIN RepairServices rs ON rs.WarrantyID = w.WarrantyID";
        
        List<Warranty> warranties = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                warranties.add(mapResultSetToWarranty(resultSet));
            }
            return warranties;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy bảo hành đã sử dụng", e);
        }
    }
    
    // Phương thức liên kết bảo hành với dịch vụ sửa chữa
    public boolean linkToRepairService(Integer warrantyId, Integer repairServiceId) {
        String sql = "UPDATE RepairServices SET WarrantyID = ? WHERE RepairID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, warrantyId);
            statement.setInt(2, repairServiceId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi liên kết bảo hành với dịch vụ sửa chữa", e);
        }
    }
    
    // Phương thức hủy liên kết bảo hành với dịch vụ sửa chữa
    public boolean unlinkFromRepairService(Integer repairServiceId) {
        String sql = "UPDATE RepairServices SET WarrantyID = NULL WHERE RepairID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, repairServiceId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi hủy liên kết bảo hành với dịch vụ sửa chữa", e);
        }
    }
    
    // Phương thức kiểm tra bảo hành có đang được sử dụng không
    public boolean isUsed(Integer warrantyId) {
        String sql = "SELECT COUNT(*) FROM RepairServices WHERE WarrantyID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, warrantyId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra bảo hành có đang được sử dụng", e);
        }
    }
    
    // Phương thức lấy tất cả bảo hành liên quan đến dịch vụ sửa chữa
    public List<Warranty> findByRepairServiceId(Integer repairServiceId) {
        String sql = "SELECT w.*, i.InvoiceID, i.ProductID, i.UnitPrice, " +
                     "p.ProductName, " +
                     "inv.CustomerID, c.FullName as CustomerName, " +
                     "rs.RepairID, rs.Status as RepairStatus " +
                     "FROM Warranties w " +
                     "JOIN InvoiceDetails i ON w.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "JOIN RepairServices rs ON rs.WarrantyID = w.WarrantyID " +
                     "WHERE rs.RepairID = ?";
        
        List<Warranty> warranties = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, repairServiceId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                warranties.add(mapResultSetToWarranty(resultSet));
            }
            return warranties;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy bảo hành theo dịch vụ sửa chữa", e);
        }
    }
    
    // Phương thức chuyển đổi từ ResultSet sang đối tượng Warranty
    private Warranty mapResultSetToWarranty(ResultSet resultSet) throws SQLException {
        Warranty warranty = new Warranty();
        warranty.setWarrantyId(resultSet.getString("WarrantyID"));
        
        // Thiết lập thời gian bảo hành
        Timestamp startDate = resultSet.getTimestamp("StartDate");
        if (startDate != null) {
            warranty.setStartDate(startDate.toLocalDateTime());
        }
        
        Timestamp endDate = resultSet.getTimestamp("EndDate");
        if (endDate != null) {
            warranty.setEndDate(endDate.toLocalDateTime());
        }
        
        warranty.setWarrantyTerms(resultSet.getString("WarrantyTerms"));
        
        // Thiết lập chi tiết hóa đơn với thông tin cơ bản
        InvoiceDetail invoiceDetail = new InvoiceDetail();
        invoiceDetail.setInvoiceDetailId(resultSet.getInt("InvoiceDetailID"));
        
        // Thiết lập sản phẩm cơ bản cho chi tiết hóa đơn
        Product product = new Product();
        product.setProductId(resultSet.getString("ProductID"));
        product.setProductName(resultSet.getString("ProductName"));
        
        invoiceDetail.setProduct(product);
        warranty.setInvoiceDetail(invoiceDetail);
        
        // Lưu trữ thông tin hiển thị cho UI
        try {
            warranty.setProductName(resultSet.getString("ProductName"));
            warranty.setCustomerName(resultSet.getString("CustomerName"));
        } catch (SQLException e) {
            // Bỏ qua nếu không có cột này
        }
        
        // Thiết lập trạng thái đã sử dụng bảo hành và thông tin dịch vụ sửa chữa
        try {
            int repairId = resultSet.getInt("RepairID");
            if (repairId > 0) {
                warranty.setUsed(true);
                warranty.setRepairServiceId(repairId);
                warranty.setRepairStatus(resultSet.getString("RepairStatus"));
            } else {
                warranty.setUsed(false);
            }
        } catch (SQLException e) {
            // Bỏ qua nếu không có cột này
            warranty.setUsed(false);
        }
        
        // Lấy thời gian tạo và cập nhật nếu có
        try {
            Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
            if (createdAt != null) {
                warranty.setCreatedAt(createdAt.toLocalDateTime());
            }
            
            Timestamp updatedAt = resultSet.getTimestamp("UpdatedAt");
            if (updatedAt != null) {
                warranty.setUpdatedAt(updatedAt.toLocalDateTime());
            }
        } catch (SQLException e) {
            // Bỏ qua nếu không có các cột này
        }
        
        return warranty;
    }
}