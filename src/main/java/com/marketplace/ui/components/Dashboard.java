package com.marketplace.ui.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import com.marketplace.dao.OrderDAO;
import com.marketplace.model.Order;
import com.marketplace.model.OrderTableModel;
import com.marketplace.ui.components.OrderForm;

import java.awt.*;
import java.util.List;

public class Dashboard extends JFrame {
    
    // DAO để lấy dữ liệu từ database
    private OrderDAO orderDAO;
    
    // Components chính
    private JTable orderTable;
    private OrderTableModel tableModel;
    private JTextField searchField;
    private JButton btnSearch;
    private JButton btnRefresh;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JScrollPane scrollPane;
    
    // Constructor
    public Dashboard() {
        orderDAO = new OrderDAO();
        initComponents();
        setupLayout();
        loadOrders(""); // Load tất cả orders khi khởi động
        
        setTitle("Order Management Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Hiển thị giữa màn hình
    }
    
    /**
     * Khởi tạo các components
     */
    private void initComponents() {
        // Khởi tạo table với model rỗng
        tableModel = new OrderTableModel(new java.util.ArrayList<>());
        orderTable = new JTable(tableModel);
        
        // Tùy chỉnh table
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setRowHeight(25);
        orderTable.getTableHeader().setReorderingAllowed(false);
        
        // Căn giữa các cột số
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        orderTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Order ID
        orderTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Buyer ID
        orderTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Order Price
        orderTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Status
        
        // ScrollPane cho table
        scrollPane = new JScrollPane(orderTable);
        
        // Search components
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(250, 30));
        btnSearch = new JButton("🔍 Search");
        btnRefresh = new JButton("🔄 Refresh");
        
        // Action buttons
        btnAdd = new JButton("➕ Add Order");
        btnEdit = new JButton("✏️ Edit");
        btnDelete = new JButton("🗑️ Delete");
        
        // Tùy chỉnh button màu sắc
        btnAdd.setBackground(new Color(76, 175, 80));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        
        btnEdit.setBackground(new Color(33, 150, 243));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFocusPainted(false);
        
        btnDelete.setBackground(new Color(244, 67, 54));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        
        btnRefresh.setFocusPainted(false);
        btnSearch.setFocusPainted(false);
        
        // Add event listeners
        btnSearch.addActionListener(e -> searchOrders());
        btnRefresh.addActionListener(e -> loadOrders(""));
        btnAdd.addActionListener(e -> addOrder());
        btnEdit.addActionListener(e -> editOrder());
        btnDelete.addActionListener(e -> deleteOrder());
        
        // Enter key trong search field
        searchField.addActionListener(e -> searchOrders());
    }
    
    /**
     * Thiết lập layout cho Dashboard
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // ===== NORTH PANEL: Title và Search =====
        JPanel northPanel = new JPanel(new BorderLayout(10, 10));
        northPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Order Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        
        northPanel.add(titleLabel, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.CENTER);
        
        // ===== CENTER PANEL: Table =====
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // ===== SOUTH PANEL: Action buttons =====
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        southPanel.add(btnAdd);
        southPanel.add(btnEdit);
        southPanel.add(btnDelete);
        
        // Add panels to frame
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Load orders từ database
     */
    private void loadOrders(String keyword) {
        try {
            List<Order> orders = orderDAO.getOrders(keyword);
            tableModel = new OrderTableModel(orders);
            orderTable.setModel(tableModel);
            
            // Cập nhật lại renderer sau khi đổi model
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            orderTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            orderTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            orderTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            orderTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
            
            // Hiển thị thông báo
            JOptionPane.showMessageDialog(this, 
                "Loaded " + orders.size() + " orders successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading orders: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Search orders
     */
    private void searchOrders() {
        String keyword = searchField.getText().trim();
        loadOrders(keyword);
    }
    
    /**
     * Thêm order mới 
     */
    private void addOrder() {
        new OrderForm().setVisible(true);
    }
    
    /**
     * Sửa order đã chọn 
     */
    private void editOrder() {
        new OrderForm().setVisible(true);
    }
    
    /**
     * Xóa order đã chọn 
     */
    private void deleteOrder() {
        new OrderForm().setVisible(true);
    }
    
    /**
     * Main method để chạy Dashboard
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
           new Dashboard().setVisible(true);
        });
    }
}
