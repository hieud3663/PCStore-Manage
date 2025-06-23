package com.pcstore.repository.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Warranty;
import com.pcstore.repository.Repository;

/**
 * Repository implementation cho Warranty entity
 */
public class WarrantyRepository implements Repository<Warranty, Integer> {
    private Connection connection;
    private static final Logger logger = Logger.getLogger(WarrantyRepository.class.getName());
    
    public WarrantyRepository(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public Warranty add(Warranty warranty) {
        if (warranty.getWarrantyId() == null || warranty.getWarrantyId().isEmpty()) {
            throw new IllegalArgumentException("Mã bảo hành không được để trống");
        }

        String sql = "INSERT INTO Warranties (WarrantyID, InvoiceDetailID, StartDate, EndDate) " +
                     "VALUES (?, ?, ?, ?)";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, warranty.getWarrantyId());
            statement.setInt(2, warranty.getInvoiceDetail().getInvoiceDetailId());
            statement.setTimestamp(3, java.sql.Timestamp.valueOf(warranty.getStartDate()));
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(warranty.getEndDate()));
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new SQLException("Thêm bảo hành thất bại, không có dòng nào được thêm vào.");
            }
            
            return warranty;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi SQL khi thêm bảo hành", e);
            throw new RuntimeException("Lỗi khi thêm bảo hành: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Warranty update(Warranty warranty) {
        String sql = "UPDATE Warranties SET StartDate = ?, EndDate = ? " +
                     "WHERE WarrantyID = ?";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, java.sql.Timestamp.valueOf(warranty.getStartDate()));
            statement.setTimestamp(2, java.sql.Timestamp.valueOf(warranty.getEndDate()));
            statement.setString(3, warranty.getWarrantyId());
            
            statement.executeUpdate();
            
            return warranty;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật bảo hành", e);
        }
    }
    
    @Override
    public boolean delete(Integer warrantyId) {
        return delete(warrantyId.toString());
    }

    // Thêm phương thức delete mới nhận vào String
    public boolean delete(String warrantyId) {
        String sql = "DELETE FROM Warranties WHERE WarrantyID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, warrantyId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa bảo hành", e);
        }
    }
    
    /**
     * Xóa bảo hành an toàn (không kiểm tra sử dụng trước)
     * @param warrantyId ID bảo hành
     * @return true nếu xóa thành công
     */
    public boolean safeForcedDelete(String warrantyId) {
        String sql = "DELETE FROM Warranties WHERE WarrantyID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, warrantyId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting warranty: " + e.getMessage(), e);
            throw new RuntimeException("Lỗi khi xóa bảo hành", e);
        }
    }
    
    @Override
    public Optional<Warranty> findById(Integer warrantyId) {
        // Sửa câu truy vấn để sử dụng CAST hoặc CONVERT nếu cần thiết
        String sql = "SELECT w.*, id.invoice_detail_id, c.customer_id, c.full_name as CustomerName, c.phone_number as CustomerPhone, " +
                     "p.product_id, p.product_name " +
                     "FROM warranty w " +
                     "LEFT JOIN invoice_detail id ON w.invoice_detail_id = id.invoice_detail_id " +
                     "LEFT JOIN invoice i ON id.invoice_id = i.invoice_id " +
                     "LEFT JOIN customer c ON i.customer_id = c.customer_id " +
                     "LEFT JOIN product p ON id.product_id = p.product_id " +
                     "WHERE w.warranty_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Có thể warrantyId là String hoặc Integer, xử lý cả hai trường hợp
            statement.setString(1, warrantyId.toString());
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToWarranty(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm bảo hành theo ID", e);
        }
    }

    // Thêm phương thức findById mới nhận vào String
    public Optional<Warranty> findById(String warrantyId) {
        String sql = "SELECT w.*, id.InvoiceDetailID, c.CustomerID, c.FullName as CustomerName, c.PhoneNumber as CustomerPhone, " +
                     "p.ProductID, p.ProductName " +
                     "FROM Warranties w " +
                     "LEFT JOIN InvoiceDetails id ON w.InvoiceDetailID = id.InvoiceDetailID " +
                     "LEFT JOIN Invoices i ON id.InvoiceID = i.InvoiceID " +
                     "LEFT JOIN Customers c ON i.CustomerID = c.CustomerID " +
                     "LEFT JOIN Products p ON id.ProductID = p.ProductID " +
                     "WHERE w.WarrantyID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, warrantyId);
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
        // Sửa tên bảng từ warranty thành Warranties
        String sql = "SELECT w.*, id.InvoiceDetailID, c.CustomerID, c.FullName as CustomerName, c.PhoneNumber as CustomerPhone, " +
                     "p.ProductID, p.ProductName " +
                     "FROM Warranties w " +
                     "LEFT JOIN InvoiceDetails id ON w.InvoiceDetailID = id.InvoiceDetailID " +
                     "LEFT JOIN Invoices i ON id.InvoiceID = i.InvoiceID " +
                     "LEFT JOIN Customers c ON i.CustomerID = c.CustomerID " +
                     "LEFT JOIN Products p ON id.ProductID = p.ProductID " +
                     "ORDER BY w.StartDate DESC";
                     
        List<Warranty> warranties = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                warranties.add(mapResultSetToWarranty(resultSet));
            }
            return warranties;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching all warranties", e);
            throw new RuntimeException("Lỗi khi lấy tất cả bảo hành", e);
        }
    }
    
    @Override
    public boolean exists(Integer warrantyId) {
        return exists(warrantyId.toString());
    }

    // Thêm phương thức exists mới nhận vào String
    public boolean exists(String warrantyId) {
        String sql = "SELECT COUNT(*) FROM Warranties WHERE WarrantyID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, warrantyId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra tồn tại bảo hành", e);
        }
    }
    
    /**
     * Tìm bảo hành theo ID chi tiết hóa đơn
     * @param invoiceDetailId ID chi tiết hóa đơn
     * @return Đối tượng Optional chứa bảo hành nếu tìm thấy
     * @throws RuntimeException nếu có lỗi xảy ra
     */
    public Optional<Warranty> findByInvoiceDetailId(Integer invoiceDetailId) {
        String sql = "SELECT w.WarrantyID, w.InvoiceDetailID, w.StartDate, " +
                "w.EndDate " +
                "FROM Warranties w WHERE w.InvoiceDetailID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, invoiceDetailId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Warranty warranty = new Warranty();
                    // Sử dụng tên cột đúng với schema database của bạn
                    warranty.setWarrantyId(rs.getString("WarrantyID"));
                    
                    InvoiceDetail invoiceDetail = new InvoiceDetail();
                    invoiceDetail.setInvoiceDetailId(rs.getInt("InvoiceDetailID"));
                    warranty.setInvoiceDetail(invoiceDetail);
                    
                    // Chuyển đổi từ Date sang LocalDateTime
                    if (rs.getDate("StartDate") != null) {
                        warranty.setStartDate(rs.getTimestamp("StartDate").toLocalDateTime());
                    }
                    
                    // Đọc ngày kết thúc
                    if (rs.getDate("EndDate") != null) {
                        warranty.setEndDate(rs.getTimestamp("EndDate").toLocalDateTime());
                    }
                    
                    return Optional.of(warranty);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm bảo hành theo chi tiết hóa đơn", e);
        }
    }
    
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
    
    /**
     * Kiểm tra xem bảo hành có đang được sử dụng không
     * @param warrantyId ID bảo hành
     * @return true nếu đang được sử dụng
     */
    public boolean isUsed(String warrantyId) {
        // Kiểm tra nếu cột không tồn tại trong CSDL
        try {
            // Kiểm tra cấu trúc bảng RepairServices
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "RepairServices", "WarrantyID");
            
            // Nếu cột không tồn tại, luôn trả về false (không được sử dụng)
            if (!columns.next()) {
                logger.warning("Cột WarrantyID không tồn tại trong bảng RepairServices");
                return false;
            }
            
            // Nếu cột tồn tại, thực hiện truy vấn
            String sql = "SELECT COUNT(*) FROM RepairServices WHERE WarrantyID = ?";
            
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, warrantyId);
                
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error checking if warranty is used: " + e.getMessage(), e);
            // Trong trường hợp lỗi, trả về false để cho phép xóa bảo hành
            return false;
        }
    }
    
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
    
    /**
     * Tìm kiếm bảo hành theo từ khóa
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách bảo hành phù hợp với từ khóa
     */
    public List<Warranty> search(String keyword) {
        List<Warranty> warranties = new ArrayList<>();

        String sql = "SELECT w.*, id.InvoiceDetailID, c.CustomerID, c.FullName as CustomerName, c.PhoneNumber as CustomerPhone, " +
                     "p.ProductID, p.ProductName " +
                     "FROM Warranties w " +
                     "LEFT JOIN InvoiceDetails id ON w.InvoiceDetailID = id.InvoiceDetailID " +
                     "LEFT JOIN Invoices i ON id.InvoiceID = i.InvoiceID " +
                     "LEFT JOIN Customers c ON i.CustomerID = c.CustomerID " +
                     "LEFT JOIN Products p ON id.ProductID = p.ProductID " +
                     "WHERE w.WarrantyID LIKE ? " +
                     "OR c.FullName LIKE ? " +
                     "OR c.PhoneNumber LIKE ? " +
                     "OR p.ProductID LIKE ? " +
                     "OR p.ProductName LIKE ? " +
                     "ORDER BY w.StartDate DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, searchPattern);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    warranties.add(mapResultSetToWarranty(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching warranties with keyword: " + keyword, e);
            throw new RuntimeException("Database error when searching warranties", e);
        }

        return warranties;
    }
    
    private Warranty mapResultSetToWarranty(ResultSet rs) throws SQLException {
        Warranty warranty = new Warranty();
        
        try {
            // Các trường cơ bản của Warranty
            warranty.setWarrantyId(rs.getString("WarrantyID"));
            
            // Xử lý ngày tháng
            if (hasColumn(rs, "StartDate")) {
                java.sql.Timestamp startDate = rs.getTimestamp("StartDate");
                if (startDate != null) {
                    warranty.setStartDate(startDate.toLocalDateTime());
                }
            }
            
            // Xử lý ngày kết thúc
            if (hasColumn(rs, "EndDate")) {
                java.sql.Timestamp endDate = rs.getTimestamp("EndDate");
                if (endDate != null) {
                    warranty.setEndDate(endDate.toLocalDateTime());
                }
            }
            
            // Thông tin invoice detail
            InvoiceDetail invoiceDetail = new InvoiceDetail();
            try {
                invoiceDetail.setInvoiceDetailId(rs.getInt("InvoiceDetailID"));
                warranty.setInvoiceDetail(invoiceDetail);
            } catch (SQLException e) {
                logger.warning("Error getting invoice_detail_id: " + e.getMessage());
            }
            
            // Thông tin bổ sung từ join (nếu có)
            if (hasColumn(rs, "CustomerName")) {
                warranty.setCustomerName(rs.getString("CustomerName"));
            }
            
            if (hasColumn(rs, "CustomerPhone")) {
                warranty.setCustomerPhone(rs.getString("CustomerPhone"));
            } else if (hasColumn(rs, "PhoneNumber")) {
                warranty.setCustomerPhone(rs.getString("PhoneNumber"));
            }
            
            if (hasColumn(rs, "ProductName")) {
                warranty.setProductName(rs.getString("ProductName"));
            }
            
            if (hasColumn(rs, "CustomerID")) {
                warranty.setCustomerId(rs.getString("CustomerID"));
            }
            
            if (hasColumn(rs, "ProductID")) {
                warranty.setProductId(rs.getString("ProductID"));
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error mapping ResultSet to Warranty: " + e.getMessage(), e);
            throw e;
        }
        
        return warranty;
    }

    /**
     * Kiểm tra xem ResultSet có chứa cột với tên cụ thể không
     * @param rs ResultSet cần kiểm tra
     * @param columnName Tên cột cần tìm
     * @return true nếu có cột, false nếu không có
     */
    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
