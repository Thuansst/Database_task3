package com.marketplace.main;

import com.marketplace.config.DatabaseConnection;
import com.marketplace.model.Order;
import com.marketplace.model.Product;
import com.marketplace.ui.components.OrderForm;

import javax.swing.*;

/**
 * Main Application Entry Point
 * Chạy chương trình, hiển thị cửa sổ chính
 */
public class MainApp {

    public static void main(String[] args) {
        OrderForm orderForm = new OrderForm();
        orderForm.setVisible(true);
    }

}
    
