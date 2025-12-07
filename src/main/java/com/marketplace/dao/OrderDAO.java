package com.marketplace.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.marketplace.config.DatabaseConnection;
import com.marketplace.model.Order;
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
     * Gọi SP: sp_UpdateOrder(OrderID, OrderPrice, Status) - 3 parameters
     * Note: PaymentID is internally managed by the stored procedure
     */
    public boolean updateOrder(Order order) {
        String query = "{CALL sp_UpdateOrder(?, ?, ?)}";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            // Tham số 1: OrderID (Để biết sửa đơn nào)
            stmt.setInt(1, order.getOrderId());

            // Tham số 2: OrderPrice (Giá - chỉ update được khi Status = Draft)
            if (order.getOrderPrice() != null) {
                stmt.setBigDecimal(2, order.getOrderPrice());
            } else {
                stmt.setNull(2, java.sql.Types.DECIMAL);
            }

            // Tham số 3: Status (Trạng thái mới)
            if (order.getStatus() != null) {
                stmt.setString(3, order.getStatus());
            } else {
                stmt.setNull(3, java.sql.Types.VARCHAR);
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
     * GET ORDER BY ID: Lấy thông tin order theo OrderID
     */
    public Order getOrderById(int orderId) throws SQLException {
        String query = "SELECT * FROM `Order` WHERE OrderID = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, orderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getInt("OrderID"));
                    order.setBuyerId(rs.getInt("BuyerID"));
                    order.setOrderAt(rs.getTimestamp("OrderAt"));
                    order.setOrderPrice(rs.getBigDecimal("OrderPrice"));
                    order.setStatus(rs.getString("Status"));
                    
                    int paymentId = rs.getInt("PaymentID");
                    if (rs.wasNull()) {
                        order.setPaymentId(null);
                    } else {
                        order.setPaymentId(paymentId);
                    }
                    
                    return order;
                }
            }
        }
        
        return null; // Order not found
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

    /**
     * ADD ORDER ITEM: Thêm sản phẩm vào đơn hàng
     * Gọi SP: sp_AddOrderItem(OrderID, VariantID, ProductID, Quantity, OUT OrderItemID)
     */
    public int addOrderItem(int orderId, int variantId, int productId, int quantity) throws SQLException {
        String query = "{CALL sp_AddOrderItem(?, ?, ?, ?, ?)}";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {
            
            // Tham số 1: OrderID
            stmt.setInt(1, orderId);
            
            // Tham số 2: VariantID
            stmt.setInt(2, variantId);
            
            // Tham số 3: ProductID
            stmt.setInt(3, productId);
            
            // Tham số 4: Quantity
            stmt.setInt(4, quantity);
            
            // Tham số 5: OrderItemID (OUT)
            stmt.registerOutParameter(5, java.sql.Types.INTEGER);
            
            // Thực thi lệnh
            stmt.execute();
            
            // Lấy OrderItemID vừa tạo
            return stmt.getInt(5);
        }
    }
}