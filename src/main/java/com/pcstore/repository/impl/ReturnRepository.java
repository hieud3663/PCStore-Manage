package com.pcstore.repository.impl;

import com.pcstore.repository.Repository;
import com.pcstore.model.Return;
import com.pcstore.model.InvoiceDetail;

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
 * Repository implementation for Return entity
 */
public class ReturnRepository implements Repository<Return, Integer> {
    private Connection connection;
    
    public ReturnRepository(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public Return add(Return returnObj) {
        String sql = "INSERT INTO Returns (InvoiceDetailID, ReturnDate, ReturnReason, " +
                     "Quantity, ReturnAmount, ProcessedBy, Status, IsExchange, NewProductID, Notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDateTime now = LocalDateTime.now();
            returnObj.setCreatedAt(now);
            returnObj.setUpdatedAt(now);
            
            statement.setInt(1, returnObj.getInvoiceDetail().getInvoiceDetailId());
            statement.setTimestamp(2, Timestamp.valueOf(returnObj.getReturnDate()));
            statement.setString(3, returnObj.getReason());
            statement.setInt(4, returnObj.getQuantity());
            
            // Lấy tổng tiền hoàn lại từ chi tiết hóa đơn và số lượng trả
            double unitPrice = returnObj.getInvoiceDetail().getUnitPrice().doubleValue();
            double returnAmount = unitPrice * returnObj.getQuantity();
            statement.setBigDecimal(5, java.math.BigDecimal.valueOf(returnAmount));
            
            // Nhân viên xử lý đơn trả hàng (null khi mới tạo)
            statement.setString(6, null);
            statement.setString(7, returnObj.getStatus());
            statement.setBoolean(8, false); // IsExchange mặc định là false
            statement.setString(9, null); // NewProductID (sản phẩm mới khi đổi hàng)
            statement.setString(10, returnObj.getNotes());
            
            statement.executeUpdate();
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    returnObj.setReturnId(generatedKeys.getInt(1));
                }
            }
            
            return returnObj;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm đơn trả hàng", e);
        }
    }
    
    @Override
    public Return update(Return returnObj) {
        String sql = "UPDATE Returns SET ReturnDate = ?, ReturnReason = ?, " +
                     "Quantity = ?, ReturnAmount = ?, ProcessedBy = ?, Status = ?, " +
                     "IsExchange = ?, NewProductID = ?, Notes = ? WHERE ReturnID = ?";
                     
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            returnObj.setUpdatedAt(LocalDateTime.now());
            
            statement.setTimestamp(1, Timestamp.valueOf(returnObj.getReturnDate()));
            statement.setString(2, returnObj.getReason());
            statement.setInt(3, returnObj.getQuantity());
            
            // Tính lại số tiền hoàn trả dựa trên giá sản phẩm và số lượng
            double unitPrice = returnObj.getInvoiceDetail().getUnitPrice().doubleValue();
            double returnAmount = unitPrice * returnObj.getQuantity();
            statement.setBigDecimal(4, java.math.BigDecimal.valueOf(returnAmount));
            
            // Nhân viên xử lý - nếu trạng thái không phải Pending
            String processedBy = null;
            if (!"Pending".equals(returnObj.getStatus()) && returnObj.getInvoiceDetail() != null
                && returnObj.getInvoiceDetail().getInvoice() != null 
                && returnObj.getInvoiceDetail().getInvoice().getEmployee() != null) {
                processedBy = returnObj.getInvoiceDetail().getInvoice().getEmployee().getEmployeeId();
            }
            statement.setString(5, processedBy);
            
            statement.setString(6, returnObj.getStatus());
            statement.setBoolean(7, false); // IsExchange luôn là false trong model hiện tại
            statement.setString(8, null); // NewProductID không được hỗ trợ trong model hiện tại
            statement.setString(9, returnObj.getNotes());
            statement.setInt(10, returnObj.getReturnId());
            
            statement.executeUpdate();
            
            return returnObj;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật đơn trả hàng", e);
        }
    }
    
    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM Returns WHERE ReturnID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa đơn trả hàng", e);
        }
    }
    
    @Override
    public Optional<Return> findById(Integer id) {
        String sql = "SELECT r.*, i.InvoiceID, p.ProductID, p.ProductName, i.UnitPrice, " +
                     "inv.CustomerID, c.FullName as CustomerName, e.FullName as ProcessorName " +
                     "FROM Returns r " +
                     "JOIN InvoiceDetails i ON r.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON r.ProcessedBy = e.EmployeeID " +
                     "WHERE r.ReturnID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Optional.of(mapResultSetToReturn(resultSet));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm đơn trả hàng theo ID", e);
        }
    }
    
    @Override
    public List<Return> findAll() {
        String sql = "SELECT r.*, i.InvoiceID, p.ProductID, p.ProductName, i.UnitPrice, " +
                     "inv.CustomerID, c.FullName as CustomerName, e.FullName as ProcessorName " +
                     "FROM Returns r " +
                     "JOIN InvoiceDetails i ON r.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON r.ProcessedBy = e.EmployeeID";
                     
        List<Return> returns = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                returns.add(mapResultSetToReturn(resultSet));
            }
            return returns;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy tất cả đơn trả hàng", e);
        }
    }
    
    @Override
    public boolean exists(Integer id) {
        String sql = "SELECT COUNT(*) FROM Returns WHERE ReturnID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra tồn tại đơn trả hàng", e);
        }
    }
    
    // Tìm đơn trả hàng theo hóa đơn
    public List<Return> findByInvoiceId(Integer invoiceId) {
        String sql = "SELECT r.*, i.InvoiceID, p.ProductID, p.ProductName, i.UnitPrice, " +
                     "inv.CustomerID, c.FullName as CustomerName, e.FullName as ProcessorName " +
                     "FROM Returns r " +
                     "JOIN InvoiceDetails i ON r.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON r.ProcessedBy = e.EmployeeID " +
                     "WHERE i.InvoiceID = ?";
        
        List<Return> returns = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, invoiceId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                returns.add(mapResultSetToReturn(resultSet));
            }
            return returns;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm đơn trả hàng theo hóa đơn", e);
        }
    }
    
    // Tìm đơn trả hàng theo sản phẩm
    public List<Return> findByProductId(String productId) {
        String sql = "SELECT r.*, i.InvoiceID, p.ProductID, p.ProductName, i.UnitPrice, " +
                     "inv.CustomerID, c.FullName as CustomerName, e.FullName as ProcessorName " +
                     "FROM Returns r " +
                     "JOIN InvoiceDetails i ON r.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON r.ProcessedBy = e.EmployeeID " +
                     "WHERE p.ProductID = ?";
        
        List<Return> returns = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                returns.add(mapResultSetToReturn(resultSet));
            }
            return returns;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm đơn trả hàng theo sản phẩm", e);
        }
    }
    
    // Tìm đơn trả hàng theo khách hàng
    public List<Return> findByCustomerId(String customerId) {
        String sql = "SELECT r.*, i.InvoiceID, p.ProductID, p.ProductName, i.UnitPrice, " +
                     "inv.CustomerID, c.FullName as CustomerName, e.FullName as ProcessorName " +
                     "FROM Returns r " +
                     "JOIN InvoiceDetails i ON r.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON r.ProcessedBy = e.EmployeeID " +
                     "WHERE inv.CustomerID = ?";
        
        List<Return> returns = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                returns.add(mapResultSetToReturn(resultSet));
            }
            return returns;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm đơn trả hàng theo khách hàng", e);
        }
    }
    
    // Tìm đơn trả hàng theo trạng thái
    public List<Return> findByStatus(String status) {
        String sql = "SELECT r.*, i.InvoiceID, p.ProductID, p.ProductName, i.UnitPrice, " +
                     "inv.CustomerID, c.FullName as CustomerName, e.FullName as ProcessorName " +
                     "FROM Returns r " +
                     "JOIN InvoiceDetails i ON r.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON r.ProcessedBy = e.EmployeeID " +
                     "WHERE r.Status = ?";
        
        List<Return> returns = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                returns.add(mapResultSetToReturn(resultSet));
            }
            return returns;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm đơn trả hàng theo trạng thái", e);
        }
    }
    
    // Tìm đơn trả hàng theo khoảng thời gian
    public List<Return> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT r.*, i.InvoiceID, p.ProductID, p.ProductName, i.UnitPrice, " +
                     "inv.CustomerID, c.FullName as CustomerName, e.FullName as ProcessorName " +
                     "FROM Returns r " +
                     "JOIN InvoiceDetails i ON r.InvoiceDetailID = i.InvoiceDetailID " +
                     "JOIN Products p ON i.ProductID = p.ProductID " +
                     "JOIN Invoices inv ON i.InvoiceID = inv.InvoiceID " +
                     "LEFT JOIN Customers c ON inv.CustomerID = c.CustomerID " +
                     "LEFT JOIN Employees e ON r.ProcessedBy = e.EmployeeID " +
                     "WHERE r.ReturnDate BETWEEN ? AND ?";
        
        List<Return> returns = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(startDate));
            statement.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                returns.add(mapResultSetToReturn(resultSet));
            }
            return returns;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm đơn trả hàng theo khoảng thời gian", e);
        }
    }
    
    // Cập nhật trạng thái đơn trả hàng
    public boolean updateStatus(Integer returnId, String status, String processorId) {
        String sql = "UPDATE Returns SET Status = ?, ProcessedBy = ? WHERE ReturnID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setString(2, processorId);
            statement.setInt(3, returnId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật trạng thái đơn trả hàng", e);
        }
    }
    
    // Phê duyệt đơn trả hàng
    public boolean approveReturn(Integer returnId, String processorId, String notes) {
        String sql = "UPDATE Returns SET Status = 'Approved', ProcessedBy = ?, Notes = ? WHERE ReturnID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, processorId);
            statement.setString(2, notes);
            statement.setInt(3, returnId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi phê duyệt đơn trả hàng", e);
        }
    }
    
    // Từ chối đơn trả hàng
    public boolean rejectReturn(Integer returnId, String processorId, String notes) {
        String sql = "UPDATE Returns SET Status = 'Rejected', ProcessedBy = ?, Notes = ? WHERE ReturnID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, processorId);
            statement.setString(2, notes);
            statement.setInt(3, returnId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi từ chối đơn trả hàng", e);
        }
    }
    
    // Hoàn thành đơn trả hàng
    public boolean completeReturn(Integer returnId, String processorId, String notes) {
        String sql = "UPDATE Returns SET Status = 'Completed', ProcessedBy = ?, Notes = ? WHERE ReturnID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, processorId);
            statement.setString(2, notes);
            statement.setInt(3, returnId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi hoàn thành đơn trả hàng", e);
        }
    }
    
    private Return mapResultSetToReturn(ResultSet resultSet) throws SQLException {
        Return returnObj = new Return();
        returnObj.setReturnId(resultSet.getInt("ReturnID"));
        
        // Chỉ lấy ID của chi tiết hóa đơn, sản phẩm và khách hàng
        // Các đối tượng đầy đủ sẽ được thiết lập trong service layer
        int invoiceDetailId = resultSet.getInt("InvoiceDetailID");
        
        // Dữ liệu cơ bản của đơn trả hàng
        Timestamp returnDate = resultSet.getTimestamp("ReturnDate");
        if (returnDate != null) {
            returnObj.setReturnDate(returnDate.toLocalDateTime());
        }
        
        returnObj.setQuantity(resultSet.getInt("Quantity"));
        returnObj.setReason(resultSet.getString("ReturnReason"));
        returnObj.setStatus(resultSet.getString("Status"));
        returnObj.setNotes(resultSet.getString("Notes"));
        
        // Dữ liệu mở rộng để hiển thị UI
        // try {
        //     // Lưu thông tin bổ sung cho tầng service/UI nếu cần
        //     returnObj.setCustomerName(resultSet.getString("CustomerName"));
        //     returnObj.setProductName(resultSet.getString("ProductName"));
        //     returnObj.setProcessorName(resultSet.getString("ProcessorName"));
        // } catch (SQLException e) {
        //     // Bỏ qua nếu không có cột này
        // }
        
        // Lấy thời gian tạo và cập nhật nếu có
        try {
            Timestamp createdAt = resultSet.getTimestamp("CreatedAt");
            if (createdAt != null) {
                returnObj.setCreatedAt(createdAt.toLocalDateTime());
            }
            
            Timestamp updatedAt = resultSet.getTimestamp("UpdatedAt");
            if (updatedAt != null) {
                returnObj.setUpdatedAt(updatedAt.toLocalDateTime());
            }
        } catch (SQLException e) {
            // Bỏ qua nếu không có các cột này
        }
        
        return returnObj;
    }
}