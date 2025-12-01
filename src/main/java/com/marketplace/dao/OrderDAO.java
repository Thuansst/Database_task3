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
    public boolean insertOrder(Order order) {
        // Câu lệnh gọi Stored Procedure trong MySQL
        String query = "{CALL sp_CreateOrder(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            // Tham số 1: BuyerID (Lấy từ model)
            stmt.setInt(1, order.getBuyerId());

            // tham số 2: OrderAt (Lấy thời gian hiện tại)
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));

            // Tham số 3: OrderPrice
            stmt.setInt(3, order.getOrderPrice());

            // Tham số 4: Status
            stmt.setString(4, order.getStatus());

            // Tham số 5: PaymentID (Xử lý trường hợp null)
            if (order.getPaymentId() != null && order.getPaymentId() > 0) {
                stmt.setInt(5, order.getPaymentId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            stmt.registerOutParameter(6, java.sql.Types.INTEGER);

            // Thực thi lệnh. Nếu số dòng bị ảnh hưởng > 0 là thành công
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi Insert Order: " + e.getMessage());
            e.printStackTrace();
            return false;
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
            stmt.setInt(2, order.getOrderPrice());

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
        String query = "{call sp_GetOrders(?)}";

        try(Connection conn = DatabaseConnection.getInstance().getConnection();
            CallableStatement stmt = conn.prepareCall(query);
            ResultSet rs = stmt.executeQuery();){

            while(rs.next()){
                Order order = new Order();
                order.setOrderId(rs.getInt("OrderID"));
                order.setBuyerId(rs.getInt("BuyerID"));
                order.setOrderAt(rs.getTimestamp("OrderAt"));
                order.setOrderPrice(rs.getInt("OrderPrice"));
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