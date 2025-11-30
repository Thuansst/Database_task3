package com.marketplace.model;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private int orderId;          // Primary Key
    private int buyerId;          // <-- THÊM CÁI NÀY (Foreign Key bắt buộc)
    private Timestamp orderAt;    // Thời gian đặt
    private int orderPrice;// Tổng tiền
    private String status;        // Trạng thái
    private Integer paymentId;    // Payment (có thể Null nên dùng Integer)
}