package sales.dao;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import sales.model.Employee;

public class EmployeeDao {

    private final HikariDataSource dataSource;

    public EmployeeDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Employee findByCredentials(String username, String password) {
        String sql = "SELECT ID, EmployeeName, Username, Password, Role FROM tblemployee WHERE Username = ? AND Password = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to authenticate employee: " + ex.getMessage(), ex);
        }
    }

    public Employee findById(int id) {
        String sql = "SELECT ID, EmployeeName, Username, Password, Role FROM tblemployee WHERE ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load employee: " + ex.getMessage(), ex);
        }
    }

    public List<Employee> findAll() {
        String sql = "SELECT ID, EmployeeName, Username, Password, Role FROM tblemployee ORDER BY EmployeeName ASC";
        List<Employee> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list employees: " + ex.getMessage(), ex);
        }
    }

    public List<Employee> search(String keyword) {
        String sql = "SELECT ID, EmployeeName, Username, Password, Role FROM tblemployee "
                + "WHERE EmployeeName LIKE ? OR Username LIKE ? OR Role LIKE ? ORDER BY EmployeeName ASC";
        String pattern = "%" + keyword + "%";
        List<Employee> list = new ArrayList<>();
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
            throw new RuntimeException("Failed to search employees: " + ex.getMessage(), ex);
        }
    }

    public boolean existsByUsername(String username, int excludeId) {
        String sql = "SELECT 1 FROM tblemployee WHERE Username = ? AND ID <> ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, excludeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to check employee username: " + ex.getMessage(), ex);
        }
    }

    public boolean existsByEmployeeName(String employeeName, int excludeId) {
        String sql = "SELECT 1 FROM tblemployee WHERE EmployeeName = ? AND ID <> ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeName);
            stmt.setInt(2, excludeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to check employee name: " + ex.getMessage(), ex);
        }
    }

    public int insert(Employee employee) {
        String sql = "INSERT INTO tblemployee (EmployeeName, Username, Password, Role) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, employee.getEmployeeName());
            stmt.setString(2, employee.getUsername());
            stmt.setString(3, employee.getPassword());
            stmt.setString(4, employee.getRole());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    employee.setId(id);
                    return id;
                }
                throw new SQLException("No generated key returned for employee insert.");
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                throw new IllegalArgumentException("Employee name or username already exists.", ex);
            }
            throw new RuntimeException("Failed to insert employee: " + ex.getMessage(), ex);
        }
    }

    public void update(Employee employee) {
        String sql = "UPDATE tblemployee SET EmployeeName = ?, Username = ?, Password = ?, Role = ? WHERE ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employee.getEmployeeName());
            stmt.setString(2, employee.getUsername());
            stmt.setString(3, employee.getPassword());
            stmt.setString(4, employee.getRole());
            stmt.setInt(5, employee.getId());
            if (stmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Employee ID " + employee.getId() + " was not found.");
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                throw new IllegalArgumentException("Employee name or username already exists.", ex);
            }
            throw new RuntimeException("Failed to update employee: " + ex.getMessage(), ex);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM tblemployee WHERE ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new IllegalArgumentException("Employee ID " + id + " was not found.");
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                throw new IllegalStateException("Cannot delete employee because they are referenced by orders, invoices, or payments.", ex);
            }
            throw new RuntimeException("Failed to delete employee: " + ex.getMessage(), ex);
        }
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("ID"),
                rs.getString("EmployeeName"),
                rs.getString("Username"),
                rs.getString("Password"),
                rs.getString("Role")
        );
    }
}
