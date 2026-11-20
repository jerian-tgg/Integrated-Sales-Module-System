package sales.dao;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import sales.model.SalesOrder;
import sales.model.SalesOrderLine;

public class SalesOrderDao {

    private final HikariDataSource dataSource;

    public SalesOrderDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<SalesOrder> findAll() {
        String sql = "SELECT o.ID, o.OrderNo, o.CustomerID, c.CustomerName, o.EmployeeID, e.EmployeeName, "
                + "o.OrderDate, o.Status, o.TotalAmount "
                + "FROM tblsalesorder o "
                + "INNER JOIN tblcustomer c ON o.CustomerID = c.ID "
                + "INNER JOIN tblemployee e ON o.EmployeeID = e.ID "
                + "ORDER BY o.ID DESC";
        List<SalesOrder> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapHeader(rs));
            }
            return list;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list sales orders: " + ex.getMessage(), ex);
        }
    }

    public List<SalesOrder> findByStatus(String status) {
        String sql = "SELECT o.ID, o.OrderNo, o.CustomerID, c.CustomerName, o.EmployeeID, e.EmployeeName, "
                + "o.OrderDate, o.Status, o.TotalAmount "
                + "FROM tblsalesorder o "
                + "INNER JOIN tblcustomer c ON o.CustomerID = c.ID "
                + "INNER JOIN tblemployee e ON o.EmployeeID = e.ID "
                + "WHERE o.Status = ? "
                + "ORDER BY o.ID DESC";
        List<SalesOrder> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapHeader(rs));
                }
            }
            return list;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list sales orders by status: " + ex.getMessage(), ex);
        }
    }

    public SalesOrder findByIdWithLines(int orderId) {
        String sql = "SELECT o.ID, o.OrderNo, o.CustomerID, c.CustomerName, o.EmployeeID, e.EmployeeName, "
                + "o.OrderDate, o.Status, o.TotalAmount "
                + "FROM tblsalesorder o "
                + "INNER JOIN tblcustomer c ON o.CustomerID = c.ID "
                + "INNER JOIN tblemployee e ON o.EmployeeID = e.ID "
                + "WHERE o.ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                SalesOrder order = mapHeader(rs);
                order.setLines(findLinesByOrderId(conn, orderId));
                return order;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load sales order: " + ex.getMessage(), ex);
        }
    }

    public List<SalesOrderLine> findLinesByOrderId(int orderId) {
        try (Connection conn = dataSource.getConnection()) {
            return findLinesByOrderId(conn, orderId);
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load sales order lines: " + ex.getMessage(), ex);
        }
    }

    public List<SalesOrderLine> findLinesByOrderId(Connection conn, int orderId) throws SQLException {
        String sql = "SELECT i.ID, i.OrderID, i.ProductID, p.ProductCode, p.ProductName, "
                + "i.Quantity, i.UnitPrice, i.LineTotal "
                + "FROM tblsalesorderitem i "
                + "INNER JOIN tblproduct p ON i.ProductID = p.ID "
                + "WHERE i.OrderID = ? "
                + "ORDER BY i.ID ASC";
        List<SalesOrderLine> lines = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SalesOrderLine line = new SalesOrderLine();
                    line.setId(rs.getInt("ID"));
                    line.setOrderId(rs.getInt("OrderID"));
                    line.setProductId(rs.getInt("ProductID"));
                    line.setProductCode(rs.getString("ProductCode"));
                    line.setProductName(rs.getString("ProductName"));
                    line.setQuantity(rs.getInt("Quantity"));
                    line.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                    line.setLineTotal(rs.getBigDecimal("LineTotal"));
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    public SalesOrder createOrder(SalesOrder order) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String orderNo = generateNextOrderNo(conn);
                LocalDateTime orderDate = order.getOrderDate() != null ? order.getOrderDate() : LocalDateTime.now();
                String insertHeader = "INSERT INTO tblsalesorder (OrderNo, CustomerID, EmployeeID, OrderDate, Status, TotalAmount) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";
                int orderId;
                try (PreparedStatement stmt = conn.prepareStatement(insertHeader, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, orderNo);
                    stmt.setInt(2, order.getCustomerId());
                    stmt.setInt(3, order.getEmployeeId());
                    stmt.setTimestamp(4, Timestamp.valueOf(orderDate));
                    stmt.setString(5, "OPEN");
                    stmt.setBigDecimal(6, order.getTotalAmount());
                    stmt.executeUpdate();
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Failed to retrieve generated sales order ID.");
                        }
                        orderId = keys.getInt(1);
                    }
                }
                insertLines(conn, orderId, order.getLines());
                conn.commit();

                order.setId(orderId);
                order.setOrderNo(orderNo);
                order.setOrderDate(orderDate);
                order.setStatus("OPEN");
                return order;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to create sales order: " + ex.getMessage(), ex);
        }
    }

    public void updateOrder(SalesOrder order) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String status = lockAndGetStatus(conn, order.getId());
                if (!"OPEN".equals(status)) {
                    throw new IllegalStateException("Only OPEN sales orders can be updated (current status: " + status + ").");
                }
                String updateHeader = "UPDATE tblsalesorder SET CustomerID = ?, EmployeeID = ?, TotalAmount = ? WHERE ID = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateHeader)) {
                    stmt.setInt(1, order.getCustomerId());
                    stmt.setInt(2, order.getEmployeeId());
                    stmt.setBigDecimal(3, order.getTotalAmount());
                    stmt.setInt(4, order.getId());
                    stmt.executeUpdate();
                }
                try (PreparedStatement del = conn.prepareStatement("DELETE FROM tblsalesorderitem WHERE OrderID = ?")) {
                    del.setInt(1, order.getId());
                    del.executeUpdate();
                }
                insertLines(conn, order.getId(), order.getLines());
                conn.commit();
            } catch (SQLException | RuntimeException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to update sales order: " + ex.getMessage(), ex);
        }
    }

    public void confirmOrder(int orderId) {
        updateOpenOrderStatus(orderId, "CONFIRMED", true);
    }

    public void cancelOrder(int orderId) {
        updateOpenOrderStatus(orderId, "CANCELLED", false);
    }

    public void deleteOrder(int orderId) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String status = lockAndGetStatus(conn, orderId);
                if (!"OPEN".equals(status)) {
                    throw new IllegalStateException("Only OPEN sales orders can be deleted (current status: " + status + ").");
                }
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM tblsalesorder WHERE ID = ?")) {
                    stmt.setInt(1, orderId);
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException | RuntimeException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                throw new IllegalStateException("Cannot delete sales order because it is already referenced.", ex);
            }
            throw new RuntimeException("Failed to delete sales order: " + ex.getMessage(), ex);
        }
    }

    private void updateOpenOrderStatus(int orderId, String targetStatus, boolean requireLines) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String status = lockAndGetStatus(conn, orderId);
                if (!"OPEN".equals(status)) {
                    throw new IllegalStateException("Only OPEN sales orders can transition to " + targetStatus + " (current status: " + status + ").");
                }
                if (requireLines && findLinesByOrderId(conn, orderId).isEmpty()) {
                    throw new IllegalStateException("Cannot confirm a sales order with no line items.");
                }
                try (PreparedStatement stmt = conn.prepareStatement("UPDATE tblsalesorder SET Status = ? WHERE ID = ?")) {
                    stmt.setString(1, targetStatus);
                    stmt.setInt(2, orderId);
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException | RuntimeException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to update sales order status: " + ex.getMessage(), ex);
        }
    }

    private String lockAndGetStatus(Connection conn, int orderId) throws SQLException {
        String sql = "SELECT Status FROM tblsalesorder WHERE ID = ? FOR UPDATE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Sales order ID " + orderId + " was not found.");
                }
                return rs.getString("Status");
            }
        }
    }

    private void insertLines(Connection conn, int orderId, List<SalesOrderLine> lines) throws SQLException {
        String sql = "INSERT INTO tblsalesorderitem (OrderID, ProductID, Quantity, UnitPrice, LineTotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (SalesOrderLine line : lines) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, line.getProductId());
                stmt.setInt(3, line.getQuantity());
                stmt.setBigDecimal(4, line.getUnitPrice());
                stmt.setBigDecimal(5, line.getLineTotal());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private String generateNextOrderNo(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(ID), 0) + 1 AS NextNum FROM tblsalesorder FOR UPDATE";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            int nextNum = rs.next() ? rs.getInt("NextNum") : 1;
            return String.format("SO-%04d", nextNum);
        }
    }

    private SalesOrder mapHeader(ResultSet rs) throws SQLException {
        SalesOrder order = new SalesOrder();
        order.setId(rs.getInt("ID"));
        order.setOrderNo(rs.getString("OrderNo"));
        order.setCustomerId(rs.getInt("CustomerID"));
        order.setCustomerName(rs.getString("CustomerName"));
        order.setEmployeeId(rs.getInt("EmployeeID"));
        order.setEmployeeName(rs.getString("EmployeeName"));
        Timestamp ts = rs.getTimestamp("OrderDate");
        order.setOrderDate(ts != null ? ts.toLocalDateTime() : null);
        order.setStatus(rs.getString("Status"));
        order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        return order;
    }
}
