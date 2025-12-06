package com.marketplace.model;

import javax.swing.table.AbstractTableModel;
import java.util.List;
// import com.marketplace.model.Order;

public class OrderTableModel extends AbstractTableModel {

    private List<Order> orders;
    private String[] columnNames = 
    {"Order ID",
     "Buyer ID",
     "Order At",
     "Order Price",
     "Status",
     "Payment ID"}; 
    
    public OrderTableModel(List<Order> orders){
        this.orders = orders;
    }
    
    @Override
    public int getRowCount(){
        return orders.size();
    }

    @Override
    public int getColumnCount(){
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column){
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex){
        Order order = orders.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return order.getOrderId();
            case 1:
                return order.getBuyerId();
            case 2:
                return order.getOrderAt();
            case 3:
                return order.getOrderPrice();
            case 4:
                return order.getStatus();
            case 5:
                return order.getPaymentId();
            default:
                return null;
        }
    }
}