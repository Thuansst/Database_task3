package com.marketplace.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class cho bảng Product
 * Map với bảng Product trong database
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private int productId;           // ProductID - Primary Key
    private int userId;              // UserID - Foreign Key (Seller)
    private String productName;      // ProductName
    private String description;      // Description
    private String category;         // Category
    private String status;           // Status: 'Active', 'Inactive'
    private Timestamp addedAt;       // AddedAt
    private int totalReviews;        // TotalReviews
    private BigDecimal averageRating; // AverageRating (0.0 - 5.0)
}
