package sales.dao;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import sales.model.Customer;

public class CustomerDao {

    private final HikariDataSource dataSource;

    public CustomerDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Customer findById(int id) {
        String sql = "SELECT ID, CustomerName, Contact, Address FROM tblcustomer WHERE ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load customer: " + ex.getMessage(), ex);
        }
    }

    public List<Customer> findAll() {
        String sql = "SELECT ID, CustomerName, Contact, Address FROM tblcustomer ORDER BY CustomerName ASC";
        List<Customer> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list customers: " + ex.getMessage(), ex);
        }
    }

    public List<Customer> search(String keyword) {
        String sql = "SELECT ID, CustomerName, Contact, Address FROM tblcustomer "
                + "WHERE CustomerName LIKE ? OR Contact LIKE ? OR Address LIKE ? ORDER BY CustomerName ASC";
        String pattern = "%" + keyword + "%";
        List<Customer> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to search customers: " + ex.getMessage(), ex);
        }
    }

    public int insert(Customer customer) {
        String sql = "INSERT INTO tblcustomer (CustomerName, Contact, Address) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getContact());
            stmt.setString(3, customer.getAddress());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    customer.setId(id);
                    return id;
                }
                throw new SQLException("No generated key returned for customer insert.");
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to insert customer: " + ex.getMessage(), ex);
        }
    }

    public void update(Customer customer) {
        String sql = "UPDATE tblcustomer SET CustomerName = ?, Contact = ?, Address = ? WHERE ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getContact());
            stmt.setString(3, customer.getAddress());
            stmt.setInt(4, customer.getId());
            if (stmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Customer ID " + customer.getId() + " was not found.");
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to update customer: " + ex.getMessage(), ex);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM tblcustomer WHERE ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Customer ID " + id + " was not found.");
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                throw new IllegalStateException("Cannot delete customer because they have existing sales orders or invoices.", ex);
            }
            throw new RuntimeException("Failed to delete customer: " + ex.getMessage(), ex);
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("ID"),
                rs.getString("CustomerName"),
                rs.getString("Contact"),
                rs.getString("Address")
        );
    }
}
