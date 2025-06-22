package com.pcstore.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pcstore.model.Invoice;
import com.pcstore.model.InvoiceDetail;
import com.pcstore.model.Product;
import com.pcstore.repository.Repository;
import com.pcstore.repository.RepositoryFactory;

/**
 * Repository implementation cho InvoiceDetail entity
 */
public class InvoiceDetailRepository implements Repository<InvoiceDetail, Integer> {
    private Connection connection;
    private RepositoryFactory RepositoryFactory;
    private static final Logger logger = Logger.getLogger(InvoiceDetailRepository.class.getName());
    
    public InvoiceDetailRepository(Connection connection, RepositoryFactory RepositoryFactory) {
        this.connection = connection;
        this.RepositoryFactory = RepositoryFactory;
    }

    public InvoiceDetailRepository(com.sun.jdi.connect.spi.Connection connection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public InvoiceDetail save(InvoiceDetail invoiceDetail) {

        InvoiceDetail existingDetail = findById(invoiceDetail.getInvoiceDetailId()).orElse(null);
        if (existingDetail != null) {
            // Nếu đã tồn tại, cập nhật
            return update(invoiceDetail);
        } else {
            // Nếu chưa tồn tại, thêm mới
            return add(invoiceDetail);
        }
    }
    
    @Override
    public InvoiceDetail add(InvoiceDetail invoiceDetail) {
        String sql = "INSERT INTO InvoiceDetails (InvoiceID, ProductID, Quantity, UnitPrice, CostPrice, ProfitMargin, DiscountAmount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, invoiceDetail.getInvoice().getInvoiceId());
            statement.setString(2, invoiceDetail.getProduct().getProductId());
            statement.setInt(3, invoiceDetail.getQuantity());
            statement.setBigDecimal(4, invoiceDetail.getUnitPrice());
            statement.setBigDecimal(5, invoiceDetail.getCostPrice());
            statement.setBigDecimal(6, invoiceDetail.getProfitMargin());
            statement.setBigDecimal(7, invoiceDetail.getDiscountAmount());

            statement.executeUpdate();
            
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                invoiceDetail.setInvoiceDetailId(generatedId);
            }
            
            LocalDateTime now = LocalDateTime.now();
            invoiceDetail.setCreatedAt(now);
            invoiceDetail.setUpdatedAt(now);
            
            // Cập nhật số lượng tồn kho sản phẩm
            Product product = invoiceDetail.getProduct();
            product.decreaseStock(invoiceDetail.getQuantity());
            RepositoryFactory.getProductRepository().update(product);
            
            // Cập nhật tổng tiền của hóa đơn
            updateInvoiceTotal(invoiceDetail.getInvoice().getInvoiceId());
            
            return invoiceDetail;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding invoice detail", e);
        }
    }
    
    @Override
    public InvoiceDetail update(InvoiceDetail invoiceDetail) {
        // Lấy thông tin chi tiết hóa đơn hiện tại để biết số lượng cũ
        Optional<InvoiceDetail> oldDetailOpt = findById(invoiceDetail.getInvoiceDetailId());
        
        if (!oldDetailOpt.isPresent()) {
            throw new RuntimeException("Cannot update non-existent invoice detail");
        }
        
        InvoiceDetail oldDetail = oldDetailOpt.get();
        int oldQuantity = oldDetail.getQuantity();
        int newQuantity = invoiceDetail.getQuantity();
        int quantityDiff = newQuantity - oldQuantity;
        
        String sql = "UPDATE InvoiceDetails SET Quantity = ?, UnitPrice = ? " +
                    "WHERE InvoiceDetailID = ?";
                    
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, invoiceDetail.getQuantity());
            statement.setBigDecimal(2, invoiceDetail.getUnitPrice());
            statement.setInt(3, invoiceDetail.getInvoiceDetailId());
            
            statement.executeUpdate();
            
            invoiceDetail.setUpdatedAt(LocalDateTime.now());
            
            // Cập nhật số lượng tồn kho sản phẩm theo sự thay đổi số lượng
            if (quantityDiff != 0) {
                Product product = RepositoryFactory.getProductRepository().findById(invoiceDetail.getProduct().getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));
                
                if (quantityDiff > 0) {
                    // Nếu số lượng tăng, giảm tồn kho
                    product.decreaseStock(quantityDiff);
                } else {
                    // Nếu số lượng giảm, tăng tồn kho
                    product.increaseStock(Math.abs(quantityDiff));
                }
                
                RepositoryFactory.getProductRepository().update(product);
            }
            
            // Cập nhật tổng tiền của hóa đơn
            updateInvoiceTotal(invoiceDetail.getInvoice().getInvoiceId());
            
            return invoiceDetail;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating invoice detail", e);
        }
    }
    
    @Override
    public boolean delete(Integer id) {
        // Lấy thông tin chi tiết hóa đơn trước khi xóa
        Optional<InvoiceDetail> detailOpt = findById(id);
        
        if (!detailOpt.isPresent()) {
            return false;
        }
        
        InvoiceDetail detail = detailOpt.get();
        int invoiceId = detail.getInvoice().getInvoiceId();
        
        String sql = "DELETE FROM InvoiceDetails WHERE InvoiceDetailID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Hoàn trả số lượng sản phẩm vào tồn kho
                Product product = detail.getProduct();
                product.increaseStock(detail.getQuantity());
                RepositoryFactory.getProductRepository().update(product);
                
                // Cập nhật tổng tiền của hóa đơn
                updateInvoiceTotal(invoiceId);
                
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting invoice detail", e);
        }
    }
    
    @Override
    public Optional<InvoiceDetail> findById(Integer id) {
        String sql = "SELECT id.*, p.ProductName FROM InvoiceDetails id " +
                    "JOIN Products p ON id.ProductID = p.ProductID " +
                    "WHERE id.InvoiceDetailID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                InvoiceDetail invoiceDetail = mapResultSetToInvoiceDetail(resultSet);
                return Optional.of(invoiceDetail);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding invoice detail by ID", e);
        }
    }
    
    @Override
    public List<InvoiceDetail> findAll() {
        String sql = "SELECT id.*, p.ProductName FROM InvoiceDetails id " +
                    "JOIN Products p ON id.ProductID = p.ProductID";
        List<InvoiceDetail> invoiceDetails = new ArrayList<>();
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                invoiceDetails.add(mapResultSetToInvoiceDetail(resultSet));
            }
            return invoiceDetails;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all invoice details", e);
        }
    }
    
    @Override
    public boolean exists(Integer id) {
        String sql = "SELECT COUNT(*) FROM InvoiceDetails WHERE InvoiceDetailID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if invoice detail exists", e);
        }
    }
    
    /**
     * Tìm chi tiết hóa đơn theo mã hóa đơn
     * @param invoiceId Mã hóa đơn
     * @return Danh sách chi tiết hóa đơn
     */
    public List<InvoiceDetail> findByInvoiceId(int invoiceId) {
        String sql = "SELECT id.*, p.ProductName, p.Specifications " +
                     "FROM InvoiceDetails id " +
                     "LEFT JOIN Products p ON id.ProductID = p.ProductID " +
                     "WHERE id.InvoiceID = ?";
        
        List<InvoiceDetail> details = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    InvoiceDetail detail = mapResultSetToInvoiceDetail(rs);
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding invoice details by invoice ID: " + invoiceId, e);
            throw new RuntimeException("Database error when finding invoice details", e);
        }
        
        return details;
    }
    
    // Tìm chi tiết hóa đơn theo ID sản phẩm
    public List<InvoiceDetail> findByProductId(String productId) {
        String sql = "SELECT id.*, p.ProductName FROM InvoiceDetails id " +
                    "JOIN Products p ON id.ProductID = p.ProductID " +
                    "WHERE id.ProductID = ?";
        List<InvoiceDetail> invoiceDetails = new ArrayList<>();
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, productId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                invoiceDetails.add(mapResultSetToInvoiceDetail(resultSet));
            }
            return invoiceDetails;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding invoice details by product ID", e);
        }
    }
    
    /**
     * Tìm chi tiết hóa đơn theo danh sách ID hóa đơn
     * 
     * @param invoiceIds Danh sách ID hóa đơn
     * @return Danh sách chi tiết hóa đơn
     */
    public List<InvoiceDetail> findByInvoiceIds(List<Integer> invoiceIds) {
        if (invoiceIds == null || invoiceIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<InvoiceDetail> result = new ArrayList<>();
        
        // Tạo chuỗi dấu ? tương ứng với số lượng ID
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < invoiceIds.size(); i++) {
            if (i > 0) {
                placeholders.append(",");
            }
            placeholders.append("?");
        }
        
        String sql = "SELECT * FROM invoice_detail WHERE invoice_id IN (" + placeholders.toString() + ")";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set giá trị cho các tham số
            for (int i = 0; i < invoiceIds.size(); i++) {
                statement.setInt(i + 1, invoiceIds.get(i));
            }
            
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    InvoiceDetail detail = mapResultSetToInvoiceDetail(rs);
                    result.add(detail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }


    
    // Cập nhật tổng tiền hóa đơn
    private void updateInvoiceTotal(int invoiceId) {
        String sql = "UPDATE Invoices SET TotalAmount = (" +
                    "SELECT SUM(Quantity * UnitPrice) FROM InvoiceDetails WHERE InvoiceID = ?) " +
                    "WHERE InvoiceID = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, invoiceId);
            statement.setInt(2, invoiceId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating invoice total", e);
        }
    }
    
    private InvoiceDetail mapResultSetToInvoiceDetail(ResultSet resultSet) throws SQLException {
        InvoiceDetail invoiceDetail = new InvoiceDetail();
        invoiceDetail.setInvoiceDetailId(resultSet.getInt("InvoiceDetailID"));
        invoiceDetail.setQuantity(resultSet.getInt("Quantity"));
        invoiceDetail.setUnitPrice(resultSet.getBigDecimal("UnitPrice"));
        invoiceDetail.setCostPrice(resultSet.getBigDecimal("CostPrice"));
        invoiceDetail.setProfitMargin(resultSet.getBigDecimal("ProfitMargin"));
        invoiceDetail.setDiscountAmount(resultSet.getBigDecimal("DiscountAmount"));

        // Tạo đối tượng Invoice giả lập chỉ với ID
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(resultSet.getInt("InvoiceID"));
        invoiceDetail.setInvoice(invoice);
        
        // Tạo đối tượng Product giả lập với ID và tên
        Product product = new Product();
        product.setProductId(resultSet.getString("ProductID"));
        product.setProductName(resultSet.getString("ProductName"));
        invoiceDetail.setProduct(product);
        
        return invoiceDetail;
    }
}