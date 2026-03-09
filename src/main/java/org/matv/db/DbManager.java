package org.matv.db;

import org.matv.model.Category;
import org.matv.model.Product;
import org.matv.model.TrackedEntity;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DbManager {
    private static Map<String, Object> config;

    static {
        Yaml yaml = new Yaml();
        try (InputStream in = DbManager.class
                .getClassLoader()
                .getResourceAsStream("properties.yml")) {
            config = yaml.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final static String SELECT_PRODUCTS = "SELECT id, category_id, name, price, stock_quantity FROM products";
    private final static String INSERT_PRODUCT = "INSERT INTO products (category_id, name, price, stock_quantity) VALUES (?, ?, ?, ?)";
    private final static String UPDATE_PRODUCT = "UPDATE products SET category_id=?, name=?, price=?, stock_quantity=? WHERE id=?";
    private final static String DELETE_PRODUCT = "DELETE FROM products WHERE id=?";

    private final static String SELECT_CATEGORIES = "SELECT id, name, description FROM categories";
    private final static String INSERT_CATEGORY = "INSERT INTO categories (name, description) VALUES (?, ?)";
    private final static String UPDATE_CATEGORY = "UPDATE categories SET name=?, description=? WHERE id=?";
    private final static String DELETE_CATEGORY = "DELETE FROM categories WHERE id=?";

    @SuppressWarnings("unchecked")
    private Connection getNewConnection() throws SQLException {
        Map<String, String> dbSettings = (Map<String, String>) config.get("db");
        return DriverManager.getConnection(dbSettings.get("url"), dbSettings.get("username"), dbSettings.get("password"));
    }

    public List<Product> readTableProducts() throws SQLException {
        List<Product> products = new LinkedList<>();
        try (Connection conn = getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_PRODUCTS)) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getInt("category_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity")));
            }
        }
        return products;
    }

    public List<Category> readTableCategories() throws SQLException {
        List<Category> categories = new LinkedList<>();
        try (Connection conn = getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_CATEGORIES)) {

            while (rs.next()) {
                categories.add(new Category(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description")));
            }
        }
        return categories;
    }

    public void syncAll(List<TrackedEntity<Product>> productStates,
                        List<TrackedEntity<Category>> categoryStates) throws SQLException {
        try (Connection conn = getNewConnection()) {
            conn.setAutoCommit(false);
            try {
                syncCategoriesInternal(conn, categoryStates);
                syncProductsInternal(conn, productStates);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private void syncProductsInternal(Connection conn, List<TrackedEntity<Product>> states) throws SQLException {
        try (PreparedStatement ins = conn.prepareStatement(INSERT_PRODUCT);
             PreparedStatement upd = conn.prepareStatement(UPDATE_PRODUCT);
             PreparedStatement del = conn.prepareStatement(DELETE_PRODUCT)) {

            for (TrackedEntity<Product> state : states) {
                Product p = state.getEntity();
                switch (state.getState()) {
                    case ADDED -> {
                        ins.setInt(1, p.getCategoryId());
                        ins.setString(2, p.getName());
                        ins.setDouble(3, p.getPrice());
                        ins.setInt(4, p.getStockQuantity());
                        ins.addBatch();
                    }
                    case MODIFIED -> {
                        upd.setInt(1, p.getCategoryId());
                        upd.setString(2, p.getName());
                        upd.setDouble(3, p.getPrice());
                        upd.setInt(4, p.getStockQuantity());
                        upd.setInt(5, p.getId());
                        upd.addBatch();
                    }
                    case DELETED -> {
                        del.setInt(1, p.getId());
                        del.addBatch();
                    }
                    case UNCHANGED -> {}
                }
            }
            ins.executeBatch();
            upd.executeBatch();
            del.executeBatch();
        }
    }

    private void syncCategoriesInternal(Connection conn, List<TrackedEntity<Category>> states) throws SQLException {
        try (PreparedStatement ins = conn.prepareStatement(INSERT_CATEGORY);
             PreparedStatement upd = conn.prepareStatement(UPDATE_CATEGORY);
             PreparedStatement del = conn.prepareStatement(DELETE_CATEGORY)) {

            for (TrackedEntity<Category> state : states) {
                Category c = state.getEntity();
                switch (state.getState()) {
                    case ADDED -> {
                        ins.setString(1, c.getName());
                        ins.setString(2, c.getDescription());
                        ins.addBatch();
                    }
                    case MODIFIED -> {
                        upd.setString(1, c.getName());
                        upd.setString(2, c.getDescription());
                        upd.setInt(3, c.getId());
                        upd.addBatch();
                    }
                    case DELETED -> {
                        del.setInt(1, c.getId());
                        del.addBatch();
                    }
                    case UNCHANGED -> {}
                }
            }
            ins.executeBatch();
            upd.executeBatch();
            del.executeBatch();
        }
    }
}