package com.marketplace.dao;

import com.marketplace.config.DatabaseConnection;
import com.marketplace.model.Order;
import java.sql.*;

import java.util.*;
public class OrderDAO {

    /**
     * INSERT: Thêm mới đơn hàng
     * Gọi SP: sp_InsertOrder(BuyerID, OrderPrice, Status, PaymentID)
     */
    public void insertOrder(Order order) throws SQLException {
        // Cập nhật query khớp với SP mới: IN BuyerID, IN OrderAt, IN OrderPrice, OUT NewOrderID
        String query = "{CALL sp_CreateOrder(?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            // Tham số 1: BuyerID
            stmt.setInt(1, order.getBuyerId());

            // Tham số 2: OrderAt (Thời gian hiện tại)
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));

            // Tham số 3: OrderPrice
            stmt.setBigDecimal(3, order.getOrderPrice());

            // Tham số 4: NewOrderID (OUT)
            stmt.registerOutParameter(4, java.sql.Types.INTEGER);

            // Thực thi lệnh
            stmt.execute();

            // Lấy ID đơn hàng vừa tạo
            int newOrderId = stmt.getInt(4);
            order.setOrderId(newOrderId);
            
            // Cập nhật lại status mặc định để hiển thị trên UI nếu cần
            order.setStatus("Draft"); 
        }
    }

    /**
     * UPDATE: Cập nhật đơn hàng
     * Gọi SP: sp_UpdateOrder(OrderID, OrderPrice, Status, PaymentID)
     */
    public boolean updateOrder(Order order) {
        String query = "{CALL sp_UpdateOrder(?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            // Tham số 1: OrderID (Để biết sửa đơn nào)
            stmt.setInt(1, order.getOrderId());

            // Tham số 2: OrderPrice (Giá mới)
            stmt.setBigDecimal(2, order.getOrderPrice());

            // Tham số 3: Status (Trạng thái mới)
            stmt.setString(3, order.getStatus());

            // Tham số 4: PaymentID (Thanh toán mới)
            if (order.getPaymentId() != null && order.getPaymentId() > 0) {
                stmt.setInt(4, order.getPaymentId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi Update Order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Order> getOrders(String keyword){
        
        List<Order> orders = new ArrayList<Order>();
        String query = "SELECT * from `Order`";

        try(Connection conn = DatabaseConnection.getInstance().getConnection();
            CallableStatement stmt = conn.prepareCall(query);
            ResultSet rs = stmt.executeQuery();){

            while(rs.next()){
                Order order = new Order();
                order.setOrderId(rs.getInt("OrderID"));
                order.setBuyerId(rs.getInt("BuyerID"));
                order.setOrderAt(rs.getTimestamp("OrderAt"));
                order.setOrderPrice(rs.getBigDecimal("OrderPrice"));
                order.setStatus(rs.getString("Status"));
                int paymentId = rs.getInt("PaymentID");
                if(rs.wasNull()){
                    order.setPaymentId(null);
                } else {
                    order.setPaymentId(paymentId);
                }
                orders.add(order);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách đơn hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }
    public boolean isBuyerValid(int buyerId) {
        String query = "SELECT COUNT(*) FROM Buyer WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, buyerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking buyer validity: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * DELETE: Xóa đơn hàng
     * Gọi SP: sp_DeleteOrder(OrderID)
     */    public boolean deleteOrder(int orderId) {
        String query = "{CALL sp_DeleteOrder(?)}";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            // Tham số 1: OrderID (Để biết xóa đơn nào)
            stmt.setInt(1, orderId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi Delete Order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}