package com.marketplace.dao;

import com.marketplace.config.DatabaseConnection;
import com.marketplace.model.Product;
import com.marketplace.model.ProductViewModel;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class ProductDAO {

    public List<ProductViewModel> getAllProductVariants() {
        List<ProductViewModel> products = new ArrayList<>();
        // Reverting to the simpler query (Product + ProductVariant) as discussed.
        // Note: We are still selecting 'p.ImageURL' assuming the column exists, 
        // BUT we will override it with the hardcoded URL for testing below.
        String query = "SELECT p.ProductName, p.ProductID, p.Category, p.AverageRating, p.TotalReviews, pi.ImageURL,"+
                       " pv.VariantID, pv.VariantName, pv.Price " +
                       "FROM Product p " +
                       "JOIN ProductVariant pv ON p.ProductID = pv.ProductID " +
                       "JOIN product_images pi on pi.ProductID = p.ProductID " +
                       "WHERE pv.Status = 'Available'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ProductViewModel vm = new ProductViewModel();
                vm.setProductId(rs.getInt("ProductID"));
                vm.setVariantId(rs.getInt("VariantID"));
                vm.setDisplayName(rs.getString("VariantName"));
                vm.setCategory(rs.getString("Category"));
                vm.setAverageRating(rs.getDouble("AverageRating"));
                vm.setTotalReviews(rs.getInt("TotalReviews"));
                vm.setPrice(rs.getBigDecimal("Price"));
                // Getting URL from DB (might be null)
                vm.setImageUrl(rs.getString("ImageURL")); 
                // vm.setImageUrl("https://placehold.co/400x600/black/white.png?text=iPhone+15+Pro+Max");

                products.add(vm);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product variants: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Product"; 
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("ProductID"));
                product.setUserId(rs.getInt("UserID"));
                product.setProductName(rs.getString("ProductName"));
                product.setDescription(rs.getString("Description"));
                product.setCategory(rs.getString("Category"));
                product.setStatus(rs.getString("Status"));
                product.setAddedAt(rs.getTimestamp("AddedAt"));
                product.setTotalReviews(rs.getInt("TotalReviews"));
                product.setAverageRating(rs.getDouble("AverageRating"));
                
                products.add(product);
            }
        } catch (SQLException e) {
             System.err.println("Error fetching products: " + e.getMessage());
             e.printStackTrace();
        }
        return products;
    }
}