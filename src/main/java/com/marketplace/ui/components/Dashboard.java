package com.marketplace.ui.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import com.marketplace.dao.*;
import com.marketplace.model.Order;
import com.marketplace.model.OrderTableModel;
import com.marketplace.ui.components.OrderForm;
import com.marketplace.ui.components.OrderForm.Mode;
import com.marketplace.ui.components.OrderSummaryDashboard;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.marketplace.model.OrderList;

public class Dashboard extends JFrame {
    
    // DAO để lấy dữ liệu từ database
    private OrderListDAO orderListDAO;
    
    // Components chính
    private JTable orderTable;
    private OrderTableModel tableModel;
    
    // Filter components - theo stored procedure parameters
    private JComboBox<String> statusComboBox;      // p_Status
    private JTextField buyerIDField;                // p_BuyerID
    private JTextField fromDateField;               // p_StartDate
    private JTextField toDateField;                 // p_EndDate
    private JComboBox<String> sortColumnComboBox;   // p_SortColumn
    private JComboBox<String> sortDirectionComboBox; // p_SortDirection
    
    // Buttons
    private JButton btnSearch;
    private JButton btnClearFilters;
    private JButton btnNewOrder;
    private JButton btnViewSummary;
    private JButton btnEdit;
    private JButton btnDelete;
    
    private JScrollPane scrollPane;
    private JLabel statusLabel;
    
    // Constructor
    public Dashboard() {
        orderListDAO = new OrderListDAO();
        initComponents();
        setupLayout();
        loadOrders(); // Load tất cả orders khi khởi động
        
        setTitle("Orders Management");
        setSize(1400, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
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
        orderTable.setRowHeight(30);
        orderTable.getTableHeader().setReorderingAllowed(false);
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        orderTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Căn giữa các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Set column widths
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // OrderID
        orderTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Order At
        orderTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Order Price
        orderTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Tax Amount
        orderTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
        orderTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Buyer ID
        orderTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        
        orderTable.getColumnModel().getColumn(6).setPreferredWidth(150); // Buyer Name
        
        orderTable.getColumnModel().getColumn(7).setPreferredWidth(200); // Buyer Email
        
        orderTable.getColumnModel().getColumn(8).setPreferredWidth(120); // Payment Status
        orderTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);
        
        // ScrollPane cho table
        scrollPane = new JScrollPane(orderTable);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // ===== Filter components theo stored procedure parameters =====
        
        // p_Status - Status filter
        String[] statuses = {"All", "Draft", "Pending", "Placed", "Cancelled", "Delivered"
                            , "Preparing to Ship", "In Transit", "Out for Delivery", "Completed"
                            , "Disputed", "Return Processing", "Return Completed", "Refunded"};
        statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setPreferredSize(new Dimension(130, 30));
        statusComboBox.setToolTipText("Filter by order status (p_Status parameter)");
        
        // p_BuyerID - Buyer ID filter
        buyerIDField = new JTextField(8);
        buyerIDField.setPreferredSize(new Dimension(100, 30));
        buyerIDField.setToolTipText("Enter Buyer ID to filter (p_BuyerID parameter)");
        
        // p_StartDate - From date
        fromDateField = new JTextField(10);
        fromDateField.setPreferredSize(new Dimension(120, 30));
        fromDateField.setToolTipText("Format: yyyy-MM-dd (p_StartDate parameter)");
        
        // p_EndDate - To date
        toDateField = new JTextField(10);
        toDateField.setPreferredSize(new Dimension(120, 30));
        toDateField.setToolTipText("Format: yyyy-MM-dd (p_EndDate parameter)");
        
        // p_SortColumn - Sort column selector
        String[] sortColumns = {"OrderID", "OrderAt", "OrderPrice", "Status", "BuyerName"};
        sortColumnComboBox = new JComboBox<>(sortColumns);
        sortColumnComboBox.setSelectedItem("OrderAt"); // Default
        sortColumnComboBox.setPreferredSize(new Dimension(130, 30));
        sortColumnComboBox.setToolTipText("Select column to sort by (p_SortColumn parameter)");
        
        // p_SortDirection - Sort direction
        String[] sortDirections = {"ASC", "DESC"};
        sortDirectionComboBox = new JComboBox<>(sortDirections);
        sortDirectionComboBox.setSelectedItem("DESC"); // Default
        sortDirectionComboBox.setPreferredSize(new Dimension(80, 30));
        sortDirectionComboBox.setToolTipText("Sort direction (p_SortDirection parameter)");
        
        // Buttons
        btnSearch = new JButton("🔍 Search");
        btnClearFilters = new JButton("Clear Filters");
        btnNewOrder = new JButton("+ New Order");
        btnViewSummary = new JButton("👁 View Summary Report");
        btnEdit = new JButton("✏ Edit");
        btnDelete = new JButton("🗑 Delete");
        
        // Style buttons
        styleButton(btnSearch, new Color(66, 133, 244), Color.WHITE);
        styleButton(btnClearFilters, new Color(158, 158, 158), Color.WHITE);
        styleButton(btnNewOrder, new Color(76, 175, 80), Color.WHITE);
        styleButton(btnViewSummary, new Color(156, 39, 176), Color.WHITE);
        styleButton(btnEdit, new Color(255, 152, 0), Color.WHITE);
        styleButton(btnDelete, new Color(244, 67, 54), Color.WHITE);
        
        // Status label
        statusLabel = new JLabel("Showing 0 orders");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Add event listeners
        btnSearch.addActionListener(e -> applyFilters());
        btnClearFilters.addActionListener(e -> clearFilters());
        btnNewOrder.addActionListener(e -> addOrder());
         btnViewSummary.addActionListener(e -> openSummaryReport());
        btnEdit.addActionListener(e -> editOrder());
        btnDelete.addActionListener(e -> deleteOrder());
        
        // Enter key handlers for quick search
        buyerIDField.addActionListener(e -> applyFilters());
        fromDateField.addActionListener(e -> applyFilters());
        toDateField.addActionListener(e -> applyFilters());
        
        // Auto-apply when changing sort options
        sortColumnComboBox.addActionListener(e -> applyFilters());
        sortDirectionComboBox.addActionListener(e -> applyFilters());
    }
    
    /**
     * Style button helper
     */
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    /**
     * Thiết lập layout cho Dashboard
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // ===== NORTH PANEL: Title và Filters =====
        JPanel northPanel = new JPanel(new BorderLayout(10, 10));
        northPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        northPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("ORDERS MANAGEMENT", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 33, 33));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Filters panel
        JPanel filtersPanel = new JPanel();
        filtersPanel.setLayout(new BoxLayout(filtersPanel, BoxLayout.Y_AXIS));
        filtersPanel.setBackground(Color.WHITE);
        
        // Row 1: Status, BuyerID, Date Range
        JPanel filterRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterRow1.setBackground(Color.WHITE);
        
        // Status filter
        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        filterRow1.add(lblStatus);
        filterRow1.add(statusComboBox);
        
        filterRow1.add(Box.createHorizontalStrut(15));
        
        // Buyer ID filter
        JLabel lblBuyerID = new JLabel("Buyer ID:");
        lblBuyerID.setFont(new Font("Arial", Font.BOLD, 12));
        filterRow1.add(lblBuyerID);
        filterRow1.add(buyerIDField);
        
        filterRow1.add(Box.createHorizontalStrut(15));
        
        // Date range filter
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.setBackground(Color.WHITE);
        
        JLabel lblFrom = new JLabel("From:");
        lblFrom.setFont(new Font("Arial", Font.BOLD, 12));
        datePanel.add(lblFrom);
        datePanel.add(fromDateField);
        
        JLabel lblTo = new JLabel("To:");
        lblTo.setFont(new Font("Arial", Font.BOLD, 12));
        datePanel.add(lblTo);
        datePanel.add(toDateField);
        
        filterRow1.add(datePanel);
        
        // Row 2: Sort options and action buttons
        JPanel filterRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterRow2.setBackground(Color.WHITE);
        
        // Sort controls
        JLabel lblSort = new JLabel("Sort By:");
        lblSort.setFont(new Font("Arial", Font.BOLD, 12));
        filterRow2.add(lblSort);
        filterRow2.add(sortColumnComboBox);
        filterRow2.add(sortDirectionComboBox);
        
        filterRow2.add(Box.createHorizontalStrut(15));
        
        // Action buttons
        filterRow2.add(btnSearch);
        filterRow2.add(btnClearFilters);
        
        filtersPanel.add(filterRow1);
        filtersPanel.add(filterRow2);
        
        // Add separator line
        JSeparator separator = new JSeparator();
        separator.setPreferredSize(new Dimension(1, 1));
        
        northPanel.add(titleLabel, BorderLayout.NORTH);
        northPanel.add(filtersPanel, BorderLayout.CENTER);
        northPanel.add(separator, BorderLayout.SOUTH);
        
        // ===== CENTER PANEL: Table =====
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.add(statusLabel);
        centerPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // ===== SOUTH PANEL: CRUD Action buttons =====
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        southPanel.setBackground(Color.WHITE);
        southPanel.add(btnNewOrder);
        southPanel.add(btnViewSummary);
        southPanel.add(btnEdit);
        southPanel.add(btnDelete);
        
        // Add panels to frame
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        
        // Set white background for frame
        getContentPane().setBackground(Color.WHITE);
    }
    
    /**
     * Load orders từ database với filters theo stored procedure parameters
     */
    private void loadOrders() {
        try {
            // Get filter values
            String status = statusComboBox.getSelectedItem().toString();
            if (status.equals("All")) {
                status = null; // NULL = all statuses
            }
            
            // Parse Buyer ID
            Integer buyerID = null;
            String buyerIDText = buyerIDField.getText().trim();
            if (!buyerIDText.isEmpty()) {
                try {
                    buyerID = Integer.parseInt(buyerIDText);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "Invalid Buyer ID. Please enter a valid number.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Parse dates - convert to java.sql.Timestamp
            java.sql.Timestamp startDate = null;
            String startDateText = fromDateField.getText().trim();
            if (!startDateText.isEmpty()) {
                try {
                    // Parse date in format yyyy-MM-dd and set time to 00:00:00
                    LocalDate localDate = LocalDate.parse(startDateText, DateTimeFormatter.ISO_LOCAL_DATE);
                    startDate = java.sql.Timestamp.valueOf(localDate.atStartOfDay());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Invalid Start Date format. Please use yyyy-MM-dd (e.g., 2024-12-01)",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            java.sql.Timestamp endDate = null;
            String endDateText = toDateField.getText().trim();
            if (!endDateText.isEmpty()) {
                try {
                    // Parse date in format yyyy-MM-dd and set time to 23:59:59
                    LocalDate localDate = LocalDate.parse(endDateText, DateTimeFormatter.ISO_LOCAL_DATE);
                    endDate = java.sql.Timestamp.valueOf(localDate.atTime(23, 59, 59));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Invalid End Date format. Please use yyyy-MM-dd (e.g., 2024-12-31)",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Get sort options
            String sortColumn = sortColumnComboBox.getSelectedItem().toString();
            String sortDirection = sortDirectionComboBox.getSelectedItem().toString();
            
            // Call stored procedure via DAO
            List<OrderList> orderList = orderListDAO.getOrderList(
                status,         // p_Status
                buyerID,        // p_BuyerID
                startDate,      // p_StartDate
                endDate,        // p_EndDate
                sortColumn,     // p_SortColumn
                sortDirection   // p_SortDirection
            );
            
            // Update table model
            tableModel = new OrderTableModel(orderList);
            orderTable.setModel(tableModel);
            
            // Reapply column settings
            applyColumnSettings();
            
            // Update status label
            statusLabel.setText("Showing 1-" + orderList.size() + " of " + orderList.size() + " orders");
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading orders: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Reapply column settings after model change
     */
    private void applyColumnSettings() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        orderTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        orderTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        orderTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        
        orderTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        
        orderTable.getColumnModel().getColumn(7).setPreferredWidth(200);
        
        orderTable.getColumnModel().getColumn(8).setPreferredWidth(120);
        orderTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);
    }
    
    /**
     * Apply filters - reload orders with current filter settings
     */
    private void applyFilters() {
        loadOrders();
    }
    
    /**
     * Clear all filters and reload with defaults
     */
    private void clearFilters() {
        statusComboBox.setSelectedIndex(0); // "All"
        buyerIDField.setText("");
        fromDateField.setText("");
        toDateField.setText("");
        sortColumnComboBox.setSelectedItem("OrderAt");
        sortDirectionComboBox.setSelectedItem("DESC");
        loadOrders();
    }
    
    /**
     * View order detail
     */
    private void viewOrderDetail() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an order to view details.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // TODO: Implement order detail view using sp_GetOrderDetail
        JOptionPane.showMessageDialog(this,
            "Order detail view will be implemented.",
            "Info",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Thêm order mới 
     */
    private void addOrder() {
        new OrderForm(Mode.ADD).setVisible(true);
    }
    
    /**
     * Sửa order đã chọn 
     */
    private void editOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an order to edit.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        new OrderForm(Mode.UPDATE).setVisible(true);
    }
    
    /**
     * Xóa order đã chọn 
     */
    private void deleteOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an order to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this order?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            new OrderForm(Mode.DELETE).setVisible(true);
        }
    }
    /**
     * Mở báo cáo tóm tắt đơn hàng
     */
    private void openSummaryReport() {
        new OrderSummaryDashboard().setVisible(true);
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