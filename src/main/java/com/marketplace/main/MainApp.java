package com.marketplace.main;

// import com.marketplace.config.DatabaseConnection;
// import com.marketplace.model.Order;
import com.marketplace.ui.components.Dashboard;
// import com.marketplace.ui.components.OrderForm;
// import com.marketplace.ui.components.OrderForm.Mode;

// import javax.swing.*;

/**
 * Main Application Entry Point
 * Chạy chương trình, hiển thị cửa sổ chính
 */
public class MainApp {

    public static void main(String[] args) {
        Dashboard dashboard = new Dashboard();
        dashboard.setVisible(true);
    }

}
    
