package com.marketplace.ui.components;
import javax.swing.*;

import com.marketplace.dao.OrderDAO;
import com.marketplace.model.Order;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrderForm extends JDialog implements ActionListener {
    // Enum để quản lý chế độ
    public enum Mode {
        ADD, UPDATE, DELETE
    }

    private Mode currentMode = Mode.ADD; // Chế độ mặc định

    // Khai báo các biến components
    private JLabel titleLabel;
    private JLabel orderIdLabel, orderPriceLabel, statusLabel, paymentIdLabel, buyerIDLabel;
    private JTextField orderIdField, orderPriceField, paymentIdField, buyerIDField;
    private JComboBox<String> statusComboBox;
    private JButton insertButton, updateButton, cancelButton, deleteButton;
    private JButton switchToAddButton, switchToUpdateButton, switchToDeleteButton; // Nút chuyển đổi mode

    // Font chuẩn đẹp
    private final Color PRIMARY_COLOR = new Color(70, 130, 180); // Màu xanh Steel Blue

    public OrderForm(Mode mode) {
        this.currentMode = mode;
        // 1. CÀI ĐẶT LOOK AND FEEL (NIMBUS)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        // Setup Form
        setTitle("Order Management");
        setSize(750, 700); // Tăng chiều rộng lên 750
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(null);
        // getContentPane().setBackground(new Color(245, 245, 250)); // Màu nền xám nhẹ hiện đại

        // --- HEADER ---
        JPanel headerPanel = new JPanel();
        headerPanel.setBounds(0, 0, 750, 120); // Tăng chiều cao để chứa nút chuyển đổi
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setLayout(null);
        add(headerPanel);

        titleLabel = new JLabel("ORDER MANAGEMENT", SwingConstants.CENTER);
        titleLabel.setBounds(0, 10, 750, 40);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Nút chuyển đổi mode
        
        switchToAddButton = createSwitchButton("ADD MODE", 100, 65, new Color(46, 204, 113)); // Xanh lá
        headerPanel.add(switchToAddButton);

        switchToUpdateButton = createSwitchButton("UPDATE MODE", 300, 65, new Color(52, 152, 219)); // Xanh dương
        headerPanel.add(switchToUpdateButton);

        switchToDeleteButton = createSwitchButton("DELETE MODE", 500, 65, new Color(231, 76, 60)); // Đỏ
        headerPanel.add(switchToDeleteButton);

        // --- BODY ---
        int labelX = 50;
        int fieldX = 220;
        int width = 450;
        int yStart = 150; // Tăng lên vì header cao hơn
        int yGap = 60;
        int currentY = yStart;

        // Buyer ID (chỉ hiện ở ADD mode)
        buyerIDLabel = createAndAddLabel("Buyer ID:", labelX, currentY);
        add(buyerIDLabel);
        buyerIDField = createStyledTextField(fieldX, currentY, width, true);
        add(buyerIDField);
        currentY += yGap;

        // Order ID (chỉ hiện ở UPDATE mode)\
        orderIdLabel = createAndAddLabel("Order ID:", labelX, yStart);
        orderIdLabel.setVisible(false); // Ẩn mặc định
        add(orderIdLabel);
        orderIdField = createStyledTextField(fieldX, yStart, width, true);
        orderIdField.setVisible(false); // Ẩn mặc định
        add(orderIdField);

        // Order Price
        orderPriceLabel = createAndAddLabel("Order Price:", labelX, currentY);
        orderPriceField = createStyledTextField(fieldX, currentY, width, true);
        add(orderPriceField);
        currentY += yGap;

        // Status (ComboBox)
        statusLabel = createAndAddLabel("Status:", labelX, currentY);
        String[] statusOptions = {"Placed", "Preparing to Ship", "In Transit", "Out for Delivery", 
            "Delivered", "Completed", "Disputed", "Return Processing", 
            "Return Completed", "Refunded", "Cancelled", "Pending"};
        statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setBounds(fieldX, currentY, width, 40); // Tăng chiều cao
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font to hơn
        add(statusComboBox);
        currentY += yGap;

        // Payment ID (nullable)
        paymentIdLabel = createAndAddLabel("Payment ID:", labelX, currentY);
        paymentIdField = createStyledTextField(fieldX, currentY, width, true);
        add(paymentIdField);
        currentY += yGap + 20;

        // --- BUTTONS ---
        insertButton = createStyledButton("Add Order", new Color(46, 204, 113), 250, currentY);
        insertButton.addActionListener(this);
        add(insertButton);

        updateButton = createStyledButton("Update Order", new Color(52, 152, 219), 250, currentY);
        updateButton.addActionListener(this);
        updateButton.setVisible(false); // Ẩn mặc định vì đang ở ADD mode
        add(updateButton);

        deleteButton = createStyledButton("Delete Order", new Color(231, 76, 60), 250, currentY);
        deleteButton.addActionListener(this);
        deleteButton.setVisible(false); // Ẩn mặc định
        add(deleteButton);

        // Gọi switchMode để thiết lập UI ban đầu
        switchMode(currentMode);
    }

    // Phương thức chuyển đổi giữa ADD và UPDATE mode
    private void switchMode(Mode mode) {
        currentMode = mode;

        if (mode == Mode.ADD) {
            // ADD MODE: Hiện BuyerID, ẩn OrderID
            buyerIDLabel.setVisible(true);
            buyerIDField.setVisible(true);
            buyerIDField.setText("");

            orderIdLabel.setVisible(false);
            orderIdField.setVisible(false);

            // Hiện nút Insert, ẩn nút Update
            insertButton.setVisible(true);
            updateButton.setVisible(false);

            deleteButton.setVisible(false);
            paymentIdLabel.setVisible(true);
            paymentIdField.setVisible(true);
            statusLabel.setVisible(true);
            statusComboBox.setVisible(true);
            orderPriceLabel.setVisible(true);
            orderPriceField.setVisible(true);

            // Highlight nút ADD MODE
            switchToAddButton.setBackground(new Color(46, 204, 113)); // Xanh lá đậm
            switchToUpdateButton.setBackground(new Color(100, 180, 220)); // Xanh dương nhạt

            // Clear các field
            orderPriceField.setText("");
            paymentIdField.setText("");
            statusComboBox.setSelectedIndex(0);

        } else if (mode == Mode.UPDATE) {
            // UPDATE MODE: Ẩn BuyerID, hiện OrderID
            buyerIDLabel.setVisible(false);
            buyerIDField.setVisible(false);

            orderIdLabel.setVisible(true);
            orderIdField.setVisible(true);
            orderIdField.setText("");

            deleteButton.setVisible(false);
            paymentIdLabel.setVisible(true);
            paymentIdField.setVisible(true);
            statusLabel.setVisible(true);
            statusComboBox.setVisible(true);
            orderPriceLabel.setVisible(true);
            orderPriceField.setVisible(true);

            // Ẩn nút Insert, hiện nút Update
            insertButton.setVisible(false);
            updateButton.setVisible(true);

            // Highlight nút UPDATE MODE
            switchToAddButton.setBackground(new Color(100, 220, 150)); // Xanh lá nhạt
            switchToUpdateButton.setBackground(new Color(52, 152, 219)); // Xanh dương đậm

            // Clear các field
            orderPriceField.setText("");
            paymentIdField.setText("");
            statusComboBox.setSelectedIndex(0);
        }
        else if (mode == Mode.DELETE) {
            // DELETE MODE: Ẩn BuyerID, hiện OrderID
            buyerIDLabel.setVisible(false);
            buyerIDField.setVisible(false);

            orderIdLabel.setVisible(true);
            orderIdField.setVisible(true);
            orderIdField.setText("");

            orderPriceLabel.setVisible(false);
            orderPriceField.setVisible(false);

            statusLabel.setVisible(false);
            statusComboBox.setVisible(false);

            paymentIdLabel.setVisible(false);
            paymentIdField.setVisible(false);

            deleteButton.setVisible(true);

            // Ẩn nút Insert, ẩn nút Update
            insertButton.setVisible(false);
            updateButton.setVisible(false);

            // Highlight nút DELETE MODE
            switchToAddButton.setBackground(new Color(100, 220, 150)); // Xanh lá nhạt
            switchToUpdateButton.setBackground(new Color(100, 180, 220)); // Xanh dương nhạt
            switchToDeleteButton.setBackground(new Color(231, 76, 60)); // Đỏ đậm

            // Clear các field
            orderPriceField.setText("");
            paymentIdField.setText("");
            statusComboBox.setSelectedIndex(0);
        }
    }

    // --- CÁC HÀM HỖ TRỢ TRANG TRÍ (HELPER METHODS) ---

    // 1. Hàm tạo Label nhanh
    private JLabel createAndAddLabel(String text, int x, int y) { // Đổi tên cho rõ nghĩa
    JLabel lbl = new JLabel(text);
    lbl.setBounds(x, y, 160, 40);
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
    lbl.setForeground(new Color(50, 50, 50));
    add(lbl); // Thêm vào form luôn
    return lbl; // <--- TRẢ VỀ ĐỐI TƯỢNG
}

    // 2. Hàm tạo TextField đẹp
    private JTextField createStyledTextField(int x, int y, int width, boolean editable) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, width, 40); // Tăng chiều cao lên 40
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font to hơn
        tf.setEditable(editable);
        tf.setBorder(BorderFactory.createCompoundBorder(
        tf.getBorder(),
        BorderFactory.createEmptyBorder(2, 10, 2, 10))); // Top 2, Left 10, Bottom 2, Right 10
        return tf;
    }

    // 3. Hàm tạo Button đẹp
    private JButton createStyledButton(String text, Color bgColor, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 140, 40);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // 4. Hàm tạo switch button đẹp
    private JButton createSwitchButton(String text, int x, int y, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 140, 40);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(this);
        return btn;
    }

    @Override
public void actionPerformed(ActionEvent e) {
    // Xử lý chuyển đổi mode
    if (e.getSource() == switchToAddButton) {
        switchMode(Mode.ADD);
        return;
    } else if (e.getSource() == switchToUpdateButton) {
        switchMode(Mode.UPDATE);
        return;
    }
    else if (e.getSource() == switchToDeleteButton) {
        switchMode(Mode.DELETE);
        return;
    }

    // Xử lý Insert Order
    if (e.getSource() == insertButton) {
        handleInsertOrder();
    } 
    // Xử lý Update Order
    else if (e.getSource() == updateButton) {
        handleUpdateOrder();
    }
    // Xử lý Delete Order
    else if (e.getSource() == deleteButton) {
        handleDeleteOrder();
    }
}

    // Main để test thử giao diện
    public static void main(String[] args) {
        new OrderForm(Mode.ADD).setVisible(true);
    }
    private void handleInsertOrder(){
        try {
            // 1. VALIDATION CƠ BẢN
            // Kiểm tra giá tiền có trống không
            if (orderPriceField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Order Price cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. CHUYỂN ĐỔI DỮ LIỆU (PARSING)
            int price;
            try {
                price = Integer.parseInt(orderPriceField.getText().trim());
                if (price < 0) {
                    JOptionPane.showMessageDialog(this, "Price cannot be negative!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Xử lý Payment ID (Có thể null hoặc rỗng)
            Integer paymentId = null;
            String payIdText = paymentIdField.getText().trim();
            if (!payIdText.isEmpty()) {
                try {
                    paymentId = Integer.parseInt(payIdText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Payment ID must be a whole number!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // 3. XỬ LY BUYER ID (QUAN TRỌNG)
            int buyerId;
            String buyerIdText = buyerIDField.getText().trim();
            if (buyerIdText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Buyer ID cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                buyerId = Integer.parseInt(buyerIdText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Buyer ID must be a whole number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4. TẠO ĐỐI TƯỢNG ORDER
            // Thứ tự tham số phải KHỚP 100% với file Order.java:
            // (orderId, buyerId, orderAt, orderPrice, status, paymentId)
            Order order = new Order(
                0,                  // orderId: Để 0 vì Insert tự tăng (Auto Increment)
                buyerId,            // buyerId: Bắt buộc phải có
                null,               // orderAt: Để null vì trong SQL đã dùng hàm NOW()
                price,              // orderPrice: BigDecimal
                (String) statusComboBox.getSelectedItem(), // status
                paymentId           // paymentId: Integer (có thể null)
            );

            // 5. GỌI DAO
            OrderDAO orderDAO = new OrderDAO();
            if (orderDAO.insertOrder(order)) {
                JOptionPane.showMessageDialog(this, "Order inserted successfully!");
                // dispose(); // Đóng form sau khi thêm thành công
            } else {
                JOptionPane.showMessageDialog(this, "Failed to insert order. Check Database!", "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage());
        }
    }
    private void handleUpdateOrder(){
        try {
            // 1. VALIDATION OrderID
            if (orderIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Order ID cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int orderId;
            try {
                orderId = Integer.parseInt(orderIdField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Order ID must be a whole number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. VALIDATION OrderPrice
            if (orderPriceField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Order Price cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int price;
            try {
                price = Integer.parseInt(orderPriceField.getText().trim());
                if (price < 0) {
                    JOptionPane.showMessageDialog(this, "Price cannot be negative!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Xử lý Payment ID (nullable)
            Integer paymentId = null;
            String payIdText = paymentIdField.getText().trim();
            if (!payIdText.isEmpty()) {
                try {
                    paymentId = Integer.parseInt(payIdText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Payment ID must be a whole number!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // 4. Tạo đối tượng Order để update
            Order order = new Order(
                orderId,                // orderId: Lấy từ field
                0,                      // buyerId: Không cần update (để 0)
                null,                   // orderAt: Không update timestamp
                price,                  // orderPrice: Giá mới
                (String) statusComboBox.getSelectedItem(), // status: Trạng thái mới
                paymentId               // paymentId: Có thể null
            );

            // 5. Gọi DAO để update
            OrderDAO orderDAO = new OrderDAO();
            if (orderDAO.updateOrder(order)) {
                JOptionPane.showMessageDialog(this, "Order updated successfully!");
                // dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update order. Check Order ID or Database!", "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage());
        }
    }
    private void handleDeleteOrder(){
        try {
            // 1. VALIDATION OrderID
            if (orderIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Order ID cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int orderId;
            try {
                orderId = Integer.parseInt(orderIdField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Order ID must be a whole number!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Gọi DAO để xóa
            OrderDAO orderDAO = new OrderDAO();
            if (orderDAO.deleteOrder(orderId)) {
                JOptionPane.showMessageDialog(this, "Order deleted successfully!");
                // dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete order. Check Order ID or Database!", "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage());
        }
    }
}
