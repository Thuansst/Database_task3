package com.marketplace.dao;

import com.marketplace.config.DatabaseConnection;
import com.marketplace.model.OrderSummaryByBuyer;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho stored procedure sp_GetOrderSummaryByBuyer
 */
public class OrderSummaryDAO {
    
    /**
     * Gọi stored procedure sp_GetOrderSummaryByBuyer
     * 
     * @param status Filter by order status (NULL = all statuses)
     * @param startDate Filter orders from date (NULL = no lower limit)
     * @param endDate Filter orders to date (NULL = no upper limit)
     * @param minOrderCount Only buyers with at least X orders (NULL = no filter)
     * @param minTotalSpent Only buyers who spent at least X amount (NULL = no filter)
     * @return List of OrderSummaryByBuyer objects
     * @throws SQLException if database error occurs
     */
    public List<OrderSummaryByBuyer> getOrderSummaryByBuyer(
            String status,
            java.sql.Timestamp startDate,
            java.sql.Timestamp endDate,
            Integer minOrderCount,
            BigDecimal minTotalSpent) throws SQLException {
        
        List<OrderSummaryByBuyer> summaries = new ArrayList<>();
        
        // SQL call to stored procedure
        String sql = "{CALL sp_GetOrderSummaryByBuyer(?, ?, ?, ?, ?)}";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            // Set parameters
            // p_Status
            if (status == null) {
                stmt.setNull(1, Types.VARCHAR);
            } else {
                stmt.setString(1, status);
            }
            
            // p_StartDate
            if (startDate == null) {
                stmt.setNull(2, Types.DATE);
            } else {
                stmt.setDate(2, new java.sql.Date(startDate.getTime()));
            }
            
            // p_EndDate
            if (endDate == null) {
                stmt.setNull(3, Types.DATE);
            } else {
                stmt.setDate(3, new java.sql.Date(endDate.getTime()));
            }
            
            // p_MinOrderCount
            if (minOrderCount == null) {
                stmt.setNull(4, Types.INTEGER);
            } else {
                stmt.setInt(4, minOrderCount);
            }
            
            // p_MinTotalSpent
            if (minTotalSpent == null) {
                stmt.setNull(5, Types.DECIMAL);
            } else {
                stmt.setBigDecimal(5, minTotalSpent);
            }
            
            // Execute query
            ResultSet rs = stmt.executeQuery();
            
            // Process results
            while (rs.next()) {
                OrderSummaryByBuyer summary = new OrderSummaryByBuyer();
                
                summary.setBuyerID(rs.getInt("BuyerID"));
                summary.setBuyerName(rs.getString("BuyerName"));
                summary.setBuyerEmail(rs.getString("BuyerEmail"));
                summary.setTotalOrders(rs.getInt("TotalOrders"));
                summary.setTotalItemsPurchased(rs.getInt("TotalItemsPurchased"));
                summary.setTotalSpent(rs.getBigDecimal("TotalSpent"));
                summary.setTotalTax(rs.getBigDecimal("TotalTax"));
                summary.setGrandTotal(rs.getBigDecimal("GrandTotal"));
                summary.setAverageOrderValue(rs.getBigDecimal("AverageOrderValue"));
                
                summaries.add(summary);
            }
            
        } catch (SQLException e) {
            System.err.println("Error calling sp_GetOrderSummaryByBuyer: " + e.getMessage());
            throw e;
        }
        
        return summaries;
    }
}