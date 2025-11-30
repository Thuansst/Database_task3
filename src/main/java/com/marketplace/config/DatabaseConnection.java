package com.marketplace.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Singleton class để quản lý kết nối MySQL Database
 * Sử dụng pattern Singleton để đảm bảo chỉ có 1 instance duy nhất
 */
public class DatabaseConnection {

    // Singleton instance
    private static DatabaseConnection instance;

    // Thông tin kết nối database - có thể config từ file properties
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/marketplace";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    // Connection object
    private Connection connection;

    // Database configuration
    private String url;
    private String user;
    private String password;

    /**
     * Private constructor để implement Singleton pattern
     */
    private DatabaseConnection() {
        // Load config từ file nếu có, nếu không dùng default
        // loadConfiguration();
        this.url = DEFAULT_URL;
        this.user = DEFAULT_USER;
        this.password = DEFAULT_PASSWORD;
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver đã được load thành công!");
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy MySQL JDBC Driver!");
            e.printStackTrace();
        }
    }

    /**
     * Lấy instance của DatabaseConnection (Singleton)
     * @return DatabaseConnection instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Lấy connection đến database
     * Nếu connection chưa có hoặc đã đóng thì tạo mới
     * @return Connection object
     * @throws SQLException nếu không thể kết nối
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Kết nối database thành công!");

                // // Set auto-commit to false để quản lý transaction thủ công nếu cần
                // connection.setAutoCommit(true);

            } catch (SQLException e) {
                System.err.println("Lỗi khi kết nối database!");
                System.err.println("URL: " + url);
                System.err.println("User: " + user);
                e.printStackTrace();
                throw e;
            }
        }
        return connection;
    }

    /**
     * Kiểm tra kết nối có hoạt động không
     * @return true nếu kết nối OK, false nếu không
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed() && conn.isValid(5);
        } catch (SQLException e) {
            System.err.println("Test connection thất bại!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Đóng connection
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Đã đóng kết nối database");
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng connection!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Lấy thông tin database đang kết nối
     * @return String chứa thông tin database
     */
    public String getDatabaseInfo() {
        try {
            Connection conn = getConnection();
            return "Database: " + conn.getMetaData().getDatabaseProductName() + " " +
                   conn.getMetaData().getDatabaseProductVersion() +
                   "\nURL: " + url;
        } catch (SQLException e) {
            return "Không thể lấy thông tin database";
        }
    }

    /**
     * Method để test kết nối (có thể chạy standalone)
     */
    public static void main(String[] args) {
        System.out.println("=== Test DatabaseConnection ===");

        DatabaseConnection dbConn = DatabaseConnection.getInstance();

        if (dbConn.testConnection()) {
            System.out.println("✓ Kết nối database thành công!");
            System.out.println(dbConn.getDatabaseInfo());
        } else {
            System.out.println("✗ Kết nối database thất bại!");
        }

        dbConn.closeConnection();
    }
}
