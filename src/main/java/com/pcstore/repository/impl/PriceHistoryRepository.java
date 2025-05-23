package com.pcstore.repository.impl;

import com.pcstore.model.PriceHistory;
import com.pcstore.utils.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PriceHistoryRepository {
    private Connection conn;
    
    public PriceHistoryRepository() {
        conn = DatabaseConnection.getInstance().getConnection();
    }
    
    public boolean save(PriceHistory priceHistory) {
        String sql = "INSERT INTO PriceHistory (ProductID, OldPrice, NewPrice, OldCostPrice, NewCostPrice, ChangedDate, EmployeeID, Note) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, priceHistory.getProductId());
            stmt.setBigDecimal(2, priceHistory.getOldPrice());
            stmt.setBigDecimal(3, priceHistory.getNewPrice());
            stmt.setBigDecimal(4, priceHistory.getOldCostPrice());
            stmt.setBigDecimal(5, priceHistory.getNewCostPrice());
            stmt.setTimestamp(6, Timestamp.valueOf(priceHistory.getChangedDate()));
            stmt.setString(7, priceHistory.getEmployeeId());
            stmt.setString(8, priceHistory.getNote());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<PriceHistory> findByProductId(String productId) {
        List<PriceHistory> priceHistories = new ArrayList<>();
        String sql = "SELECT * FROM PriceHistory WHERE ProductID = ? ORDER BY ChangedDate DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                PriceHistory history = mapResultSetToPriceHistory(rs);
                priceHistories.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return priceHistories;
    }
    
    public BigDecimal getAverageCostPriceInPeriod(String productId, LocalDateTime fromDate, LocalDateTime toDate) {
        String sql = "SELECT AVG(NewCostPrice) AS AvgCostPrice FROM PriceHistory " +
                     "WHERE ProductID = ? AND ChangedDate BETWEEN ? AND ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productId);
            stmt.setTimestamp(2, Timestamp.valueOf(fromDate));
            stmt.setTimestamp(3, Timestamp.valueOf(toDate));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("AvgCostPrice");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private PriceHistory mapResultSetToPriceHistory(ResultSet rs) throws SQLException {
        PriceHistory history = new PriceHistory();
        history.setPriceHistoryId(rs.getInt("PriceHistoryID"));
        history.setProductId(rs.getString("ProductID"));
        history.setOldPrice(rs.getBigDecimal("OldPrice"));
        history.setNewPrice(rs.getBigDecimal("NewPrice"));
        history.setOldCostPrice(rs.getBigDecimal("OldCostPrice"));
        history.setNewCostPrice(rs.getBigDecimal("NewCostPrice"));
        history.setChangedDate(rs.getTimestamp("ChangedDate").toLocalDateTime());
        history.setEmployeeId(rs.getString("EmployeeID"));
        history.setNote(rs.getString("Note"));
        return history;
    }
}