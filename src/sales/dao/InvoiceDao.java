package sales.dao;

import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import sales.model.Invoice;
import sales.model.InvoiceLine;
import sales.model.SalesOrderLine;

public class InvoiceDao {

    private final HikariDataSource dataSource;
    private final ProductDao productDao;
    private final SalesOrderDao salesOrderDao;

    public InvoiceDao(HikariDataSource dataSource, ProductDao productDao, SalesOrderDao salesOrderDao) {
        this.dataSource = dataSource;
        this.productDao = productDao;
        this.salesOrderDao = salesOrderDao;
    }

    public List<Invoice> findAll() {
        String sql = "SELECT inv.ID, inv.InvoiceNo, inv.OrderID, o.OrderNo, inv.CustomerID, c.CustomerName, "
                + "inv.EmployeeID, e.EmployeeName, inv.InvoiceDate, inv.DueDate, inv.TotalAmount, "
                + "inv.AmountPaid, inv.Balance, inv.Status "
                + "FROM tblinvoice inv "
                + "INNER JOIN tblsalesorder o ON inv.OrderID = o.ID "
                + "INNER JOIN tblcustomer c ON inv.CustomerID = c.ID "
                + "INNER JOIN tblemployee e ON inv.EmployeeID = e.ID "
                + "ORDER BY inv.ID DESC";
        List<Invoice> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapHeader(rs));
            }
            return list;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list invoices: " + ex.getMessage(), ex);
        }
    }

    public List<Invoice> findUnpaidAndPartial() {
        String sql = "SELECT inv.ID, inv.InvoiceNo, inv.OrderID, o.OrderNo, inv.CustomerID, c.CustomerName, "
                + "inv.EmployeeID, e.EmployeeName, inv.InvoiceDate, inv.DueDate, inv.TotalAmount, "
                + "inv.AmountPaid, inv.Balance, inv.Status "
                + "FROM tblinvoice inv "
                + "INNER JOIN tblsalesorder o ON inv.OrderID = o.ID "
                + "INNER JOIN tblcustomer c ON inv.CustomerID = c.ID "
                + "INNER JOIN tblemployee e ON inv.EmployeeID = e.ID "
                + "WHERE inv.Status IN ('UNPAID', 'PARTIALLY_PAID') "
                + "ORDER BY inv.ID DESC";
        List<Invoice> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapHeader(rs));
            }
            return list;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list unpaid invoices: " + ex.getMessage(), ex);
        }
    }

    public Invoice findByIdWithLines(int invoiceId) {
        String sql = "SELECT inv.ID, inv.InvoiceNo, inv.OrderID, o.OrderNo, inv.CustomerID, c.CustomerName, "
                + "inv.EmployeeID, e.EmployeeName, inv.InvoiceDate, inv.DueDate, inv.TotalAmount, "
                + "inv.AmountPaid, inv.Balance, inv.Status "
                + "FROM tblinvoice inv "
                + "INNER JOIN tblsalesorder o ON inv.OrderID = o.ID "
                + "INNER JOIN tblcustomer c ON inv.CustomerID = c.ID "
                + "INNER JOIN tblemployee e ON inv.EmployeeID = e.ID "
                + "WHERE inv.ID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Invoice invoice = mapHeader(rs);
                invoice.setLines(findLinesByInvoiceId(conn, invoiceId));
                return invoice;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load invoice: " + ex.getMessage(), ex);
        }
    }

    public List<InvoiceLine> findLinesByInvoiceId(int invoiceId) {
        try (Connection conn = dataSource.getConnection()) {
            return findLinesByInvoiceId(conn, invoiceId);
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load invoice lines: " + ex.getMessage(), ex);
        }
    }

    private List<InvoiceLine> findLinesByInvoiceId(Connection conn, int invoiceId) throws SQLException {
        String sql = "SELECT ii.ID, ii.InvoiceID, ii.ProductID, p.ProductCode, p.ProductName, "
                + "ii.Quantity, ii.UnitPrice, ii.LineTotal "
                + "FROM tblinvoiceitem ii "
                + "INNER JOIN tblproduct p ON ii.ProductID = p.ID "
                + "WHERE ii.InvoiceID = ? "
                + "ORDER BY ii.ID ASC";
        List<InvoiceLine> lines = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    InvoiceLine line = new InvoiceLine();
                    line.setId(rs.getInt("ID"));
                    line.setInvoiceId(rs.getInt("InvoiceID"));
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

    public Invoice generateInvoiceFromOrder(int orderId, int employeeId, LocalDate dueDate) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String lockSql = "SELECT ID, OrderNo, CustomerID, EmployeeID, Status, TotalAmount "
                        + "FROM tblsalesorder WHERE ID = ? FOR UPDATE";
                int customerId;
                String orderNo;
                BigDecimal totalAmount;
                try (PreparedStatement stmt = conn.prepareStatement(lockSql)) {
                    stmt.setInt(1, orderId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException("Selected sales order does not exist.");
                        }
                        String status = rs.getString("Status");
                        if (!"CONFIRMED".equals(status)) {
                            throw new IllegalStateException("Only CONFIRMED sales orders can be invoiced (current status: " + status + ").");
                        }
                        orderNo = rs.getString("OrderNo");
                        customerId = rs.getInt("CustomerID");
                        totalAmount = rs.getBigDecimal("TotalAmount");
                    }
                }

                List<SalesOrderLine> orderLines = salesOrderDao.findLinesByOrderId(conn, orderId);
                if (orderLines.isEmpty()) {
                    throw new IllegalStateException("Cannot invoice sales order " + orderNo + " because it has no line items.");
                }

                for (SalesOrderLine line : orderLines) {
                    try {
                        productDao.deductStock(conn, line.getProductId(), line.getQuantity());
                    } catch (SQLException stockEx) {
                        throw new IllegalStateException("Insufficient stock for product " + line.getProductCode()
                                + " - " + line.getProductName() + " (requested qty: " + line.getQuantity() + ").", stockEx);
                    }
                }

                String invoiceNo = generateNextInvoiceNo(conn);
                LocalDateTime invoiceDate = LocalDateTime.now();
                BigDecimal amountPaid = BigDecimal.ZERO.setScale(2);

                String insertInvoice = "INSERT INTO tblinvoice "
                        + "(InvoiceNo, OrderID, CustomerID, EmployeeID, InvoiceDate, DueDate, TotalAmount, AmountPaid, Balance, Status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                int invoiceId;
                try (PreparedStatement stmt = conn.prepareStatement(insertInvoice, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, invoiceNo);
                    stmt.setInt(2, orderId);
                    stmt.setInt(3, customerId);
                    stmt.setInt(4, employeeId);
                    stmt.setTimestamp(5, Timestamp.valueOf(invoiceDate));
                    stmt.setDate(6, Date.valueOf(dueDate));
                    stmt.setBigDecimal(7, totalAmount);
                    stmt.setBigDecimal(8, amountPaid);
                    stmt.setBigDecimal(9, totalAmount);
                    stmt.setString(10, "UNPAID");
                    stmt.executeUpdate();
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Failed to retrieve generated invoice ID.");
                        }
                        invoiceId = keys.getInt(1);
                    }
                }

                String insertLine = "INSERT INTO tblinvoiceitem (InvoiceID, ProductID, Quantity, UnitPrice, LineTotal) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertLine)) {
                    for (SalesOrderLine line : orderLines) {
                        stmt.setInt(1, invoiceId);
                        stmt.setInt(2, line.getProductId());
                        stmt.setInt(3, line.getQuantity());
                        stmt.setBigDecimal(4, line.getUnitPrice());
                        stmt.setBigDecimal(5, line.getLineTotal());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }

                try (PreparedStatement stmt = conn.prepareStatement("UPDATE tblsalesorder SET Status = 'INVOICED' WHERE ID = ?")) {
                    stmt.setInt(1, orderId);
                    stmt.executeUpdate();
                }

                conn.commit();
                return findByIdWithLines(invoiceId);
            } catch (SQLException | RuntimeException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to generate invoice: " + ex.getMessage(), ex);
        }
    }

    private String generateNextInvoiceNo(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(ID), 0) + 1 AS NextNum FROM tblinvoice FOR UPDATE";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            int nextNum = rs.next() ? rs.getInt("NextNum") : 1;
            return String.format("INV-%04d", nextNum);
        }
    }

    private Invoice mapHeader(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setId(rs.getInt("ID"));
        invoice.setInvoiceNo(rs.getString("InvoiceNo"));
        invoice.setOrderId(rs.getInt("OrderID"));
        invoice.setOrderNo(rs.getString("OrderNo"));
        invoice.setCustomerId(rs.getInt("CustomerID"));
        invoice.setCustomerName(rs.getString("CustomerName"));
        invoice.setEmployeeId(rs.getInt("EmployeeID"));
        invoice.setEmployeeName(rs.getString("EmployeeName"));
        Timestamp invTs = rs.getTimestamp("InvoiceDate");
        invoice.setInvoiceDate(invTs != null ? invTs.toLocalDateTime() : null);
        Date due = rs.getDate("DueDate");
        invoice.setDueDate(due != null ? due.toLocalDate() : null);
        invoice.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        invoice.setAmountPaid(rs.getBigDecimal("AmountPaid"));
        invoice.setBalance(rs.getBigDecimal("Balance"));
        invoice.setStatus(rs.getString("Status"));
        return invoice;
    }
}
