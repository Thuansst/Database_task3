package com.marketplace.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class cho bảng User
 * Map với bảng User trong database
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int userId;           // UserID - Primary Key
    private String userName;      // UserName - Unique
    private String password;      // Password
    private String firstName;     // FirstName
    private String lastName;      // LastName
    private String email;         // Email - Unique
    private String phoneNumber;   // PhoneNumber
    private String address;       // Address
    private Timestamp registerAt; // RegisterAt
    private String userType;      // UserType: 'Admin', 'Buyer', 'Seller'
    private String status;        // Status: 'Active', 'Suspended', 'Deleted'    
}
