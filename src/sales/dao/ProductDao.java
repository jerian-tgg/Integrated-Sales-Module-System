package sales.dao;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import sales.model.Product;

public class ProductDao {

    private final HikariDataSource dataSource;

    public ProductDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Product findById(int id) {
        String sql = "SELECT ID, ProductCode, ProductName, UnitPrice, StockQty FROM tblproduct WHERE ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load product: " + ex.getMessage(), ex);
        }
    }

    public List<Product> findAll() {
        String sql = "SELECT ID, ProductCode, ProductName, UnitPrice, StockQty FROM tblproduct ORDER BY ProductName ASC";
        List<Product> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list products: " + ex.getMessage(), ex);
        }
    }

    public List<Product> search(String keyword) {
        String sql = "SELECT ID, ProductCode, ProductName, UnitPrice, StockQty FROM tblproduct "
                + "WHERE ProductCode LIKE ? OR ProductName LIKE ? ORDER BY ProductName ASC";
        String pattern = "%" + keyword + "%";
        List<Product> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to search products: " + ex.getMessage(), ex);
        }
    }

    public boolean existsByProductCode(String productCode, int excludeId) {
        String sql = "SELECT 1 FROM tblproduct WHERE ProductCode = ? AND ID <> ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productCode);
            stmt.setInt(2, excludeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to check product code: " + ex.getMessage(), ex);
        }
    }

    public int insert(Product product) {
        String sql = "INSERT INTO tblproduct (ProductCode, ProductName, UnitPrice, StockQty) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getProductCode());
            stmt.setString(2, product.getProductName());
            stmt.setBigDecimal(3, product.getUnitPrice());
            stmt.setInt(4, product.getStockQty());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    product.setId(id);
                    return id;
                }
                throw new SQLException("No generated key returned for product insert.");
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                throw new IllegalArgumentException("Product code already exists.", ex);
            }
            throw new RuntimeException("Failed to insert product: " + ex.getMessage(), ex);
        }
    }

    public void update(Product product) {
        String sql = "UPDATE tblproduct SET ProductCode = ?, ProductName = ?, UnitPrice = ?, StockQty = ? WHERE ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getProductCode());
            stmt.setString(2, product.getProductName());
            stmt.setBigDecimal(3, product.getUnitPrice());
            stmt.setInt(4, product.getStockQty());
            stmt.setInt(5, product.getId());
            if (stmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Product ID " + product.getId() + " was not found.");
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                throw new IllegalArgumentException("Product code already exists.", ex);
            }
            throw new RuntimeException("Failed to update product: " + ex.getMessage(), ex);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM tblproduct WHERE ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Product ID " + id + " was not found.");
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                throw new IllegalStateException("Cannot delete product because it is referenced by sales orders or invoices.", ex);
            }
            throw new RuntimeException("Failed to delete product: " + ex.getMessage(), ex);
        }
    }

    public void deductStock(Connection conn, int productId, int quantity) throws SQLException {
        String sql = "UPDATE tblproduct SET StockQty = StockQty - ? WHERE ID = ? AND StockQty >= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Insufficient stock for product ID " + productId);
            }
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("ID"),
                rs.getString("ProductCode"),
                rs.getString("ProductName"),
                rs.getBigDecimal("UnitPrice"),
                rs.getInt("StockQty")
        );
    }
}
