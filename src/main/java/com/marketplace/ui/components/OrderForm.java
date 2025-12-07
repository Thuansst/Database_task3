package com.marketplace.ui.components;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.marketplace.dao.OrderDAO;
import com.marketplace.model.Order;

public class OrderForm extends JDialog implements ActionListener {
    // Enum để quản lý chế độ
    public enum Mode {
        UPDATE, DELETE
    }

    private Mode currentMode = Mode.UPDATE; // Chế độ mặc định

    // Khai báo các biến components
    private JLabel titleLabel;
    private JLabel orderIdLabel, statusLabel;
    private JTextField orderIdField;
    private JComboBox<String> statusComboBox;
    private JButton updateButton, deleteButton;
    private JButton switchToUpdateButton, switchToDeleteButton; // Nút chuyển đổi mode

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
        // setModal(true);
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
        
        switchToUpdateButton = createSwitchButton("UPDATE MODE", 200, 65, new Color(52, 152, 219)); // Xanh dương
        headerPanel.add(switchToUpdateButton);

        switchToDeleteButton = createSwitchButton("DELETE MODE", 400, 65, new Color(231, 76, 60)); // Đỏ
        headerPanel.add(switchToDeleteButton);

        // --- BODY ---
        int labelX = 50;
        int fieldX = 220;
        int width = 450;
        int yStart = 150; // Tăng lên vì header cao hơn
        int yGap = 60;

        // Order ID
        orderIdLabel = createAndAddLabel("Order ID:", labelX, yStart);
        add(orderIdLabel);
        orderIdField = createStyledTextField(fieldX, yStart, width, true);
        add(orderIdField);

        // Status (ComboBox) - sẽ hiện ở vị trí dưới Order ID trong UPDATE mode
        statusLabel = createAndAddLabel("Status:", labelX, yStart + yGap);
        String[] statusOptions = {"Placed", "Preparing to Ship", "In Transit", "Out for Delivery", 
            "Delivered", "Completed", "Disputed", "Return Processing", 
            "Return Completed", "Refunded", "Cancelled", "Pending"};
        statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setBounds(fieldX, yStart + yGap, width, 40); // Đặt ngay dưới Order ID
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font to hơn
        add(statusComboBox);

        // --- BUTTONS ---
        int buttonY = yStart + yGap * 2 + 20;
        
        updateButton = createStyledButton("Update Order", new Color(52, 152, 219), 250, buttonY);
        updateButton.addActionListener(this);
        add(updateButton);

        deleteButton = createStyledButton("Delete Order", new Color(231, 76, 60), 250, buttonY);
        deleteButton.addActionListener(this);
        deleteButton.setVisible(false); // Ẩn mặc định
        add(deleteButton);

        // Gọi switchMode để thiết lập UI ban đầu
        switchMode(currentMode);
    }

    // Phương thức chuyển đổi giữa UPDATE và DELETE mode
    private void switchMode(Mode mode) {
        currentMode = mode;

        if (mode == Mode.UPDATE) {
            // UPDATE MODE: Chỉ hiện OrderID và Status
            orderIdLabel.setVisible(true);
            orderIdField.setVisible(true);
            
            // Chỉ hiện Status
            statusLabel.setVisible(true);
            statusComboBox.setVisible(true);

            deleteButton.setVisible(false);
            updateButton.setVisible(true);

            // Highlight nút UPDATE MODE
            switchToUpdateButton.setBackground(new Color(52, 152, 219)); // Xanh dương đậm
            switchToDeleteButton.setBackground(new Color(200, 100, 100)); // Đỏ nhạt

            // Clear các field
            statusComboBox.setSelectedIndex(0);
        }
        else if (mode == Mode.DELETE) {
            // DELETE MODE: Chỉ hiện OrderID
            orderIdLabel.setVisible(true);
            orderIdField.setVisible(true);

            statusLabel.setVisible(false);
            statusComboBox.setVisible(false);

            deleteButton.setVisible(true);
            updateButton.setVisible(false);

            // Highlight nút DELETE MODE
            switchToUpdateButton.setBackground(new Color(100, 180, 220)); // Xanh dương nhạt
            switchToDeleteButton.setBackground(new Color(231, 76, 60)); // Đỏ đậm

            // Clear các field
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
    if (e.getSource() == switchToUpdateButton) {
        switchMode(Mode.UPDATE);
        return;
    }
    else if (e.getSource() == switchToDeleteButton) {
        switchMode(Mode.DELETE);
        return;
    }

    // Xử lý Update Order
    if (e.getSource() == updateButton) {
        handleUpdateOrder();
    }
    // Xử lý Delete Order
    else if (e.getSource() == deleteButton) {
        handleDeleteOrder();
    }
}

    // Main để test thử giao diện
    public static void main(String[] args) {
        new OrderForm(Mode.UPDATE).setVisible(true);
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

            // 2. Lấy status từ combobox
            String newStatus = (String) statusComboBox.getSelectedItem();

            // 3. Tạo đối tượng Order để update (chỉ update status)
            Order order = new Order(
                orderId,                // orderId: Lấy từ field
                0,                      // buyerId: Không cần update (để 0)
                null,                   // orderAt: Không update timestamp
                null,                   // orderPrice: Không update (để null)
                newStatus,              // status: Trạng thái mới từ combobox
                null                    // paymentId: Không update (để null)
            );

            // 4. Gọi DAO để update
            OrderDAO orderDAO = new OrderDAO();
            if (orderDAO.updateOrder(order)) {
                // Lấy lại thông tin Order sau khi update để lấy PaymentID (nếu có)
                try {
                    Order updatedOrder = orderDAO.getOrderById(orderId);
                    
                    String successMessage = "Order status updated successfully!\nOrder ID: " + orderId + "\nNew Status: " + newStatus;
                    
                    // Nếu có PaymentID (khi chuyển từ Draft sang Pending), hiển thị PaymentID
                    if (updatedOrder != null && updatedOrder.getPaymentId() != null) {
                        successMessage += "\nPayment ID: " + updatedOrder.getPaymentId();
                    }
                    
                    JOptionPane.showMessageDialog(this, successMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    // Nếu không lấy được Order, vẫn hiển thị thông báo thành công cơ bản
                    JOptionPane.showMessageDialog(this, "Order status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
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
