package sales.dao;

import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import sales.model.DashboardSummary;
import sales.model.InvoiceReportRow;
import sales.model.PaymentReportRow;
import sales.model.SalesOrderReportRow;

public class ReportDao {

    private final HikariDataSource dataSource;

    public ReportDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DashboardSummary getDashboardSummary() {
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM tblcustomer) AS TotalCustomers, "
                + "(SELECT COUNT(*) FROM tblproduct) AS TotalProducts, "
                + "(SELECT COUNT(*) FROM tblsalesorder WHERE Status = 'OPEN') AS OpenOrders, "
                + "(SELECT COUNT(*) FROM tblsalesorder WHERE Status = 'CONFIRMED') AS ConfirmedOrders, "
                + "(SELECT COUNT(*) FROM tblinvoice WHERE Status IN ('UNPAID', 'PARTIALLY_PAID')) AS UnpaidInvoices, "
                + "(SELECT COALESCE(SUM(AmountPaid), 0.00) FROM tblpayment) AS TotalCollections";
        DashboardSummary summary = new DashboardSummary();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                summary.setTotalCustomers(rs.getInt("TotalCustomers"));
                summary.setTotalProducts(rs.getInt("TotalProducts"));
                summary.setOpenOrders(rs.getInt("OpenOrders"));
                summary.setConfirmedOrders(rs.getInt("ConfirmedOrders"));
                summary.setUnpaidInvoices(rs.getInt("UnpaidInvoices"));
                BigDecimal collections = rs.getBigDecimal("TotalCollections");
                summary.setTotalCollections(collections != null ? collections : BigDecimal.ZERO);
            }
            return summary;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load dashboard summary: " + ex.getMessage(), ex);
        }
    }

    public List<SalesOrderReportRow> getSalesOrderReport(LocalDate from, LocalDate to, String status) {
        boolean filterStatus = hasFilter(status);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT o.OrderNo, o.OrderDate, c.CustomerName, e.EmployeeName, ")
           .append("COUNT(i.ID) AS ItemCount, o.Status, o.TotalAmount ")
           .append("FROM tblsalesorder o ")
           .append("INNER JOIN tblcustomer c ON o.CustomerID = c.ID ")
           .append("INNER JOIN tblemployee e ON o.EmployeeID = e.ID ")
           .append("LEFT JOIN tblsalesorderitem i ON o.ID = i.OrderID ")
           .append("WHERE o.OrderDate >= ? AND o.OrderDate < ? ");
        if (filterStatus) {
            sql.append("AND o.Status = ? ");
        }
        sql.append("GROUP BY o.ID, o.OrderNo, o.OrderDate, c.CustomerName, e.EmployeeName, o.Status, o.TotalAmount ")
           .append("ORDER BY o.OrderDate DESC, o.ID DESC");

        List<SalesOrderReportRow> rows = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setTimestamp(1, Timestamp.valueOf(from.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(to.plusDays(1).atStartOfDay()));
            if (filterStatus) {
                stmt.setString(3, status.trim());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SalesOrderReportRow row = new SalesOrderReportRow();
                    row.setOrderNo(rs.getString("OrderNo"));
                    Timestamp ts = rs.getTimestamp("OrderDate");
                    row.setOrderDate(ts != null ? ts.toLocalDateTime() : null);
                    row.setCustomerName(rs.getString("CustomerName"));
                    row.setEmployeeName(rs.getString("EmployeeName"));
                    row.setItemCount(rs.getInt("ItemCount"));
                    row.setStatus(rs.getString("Status"));
                    row.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                    rows.add(row);
                }
            }
            return rows;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to generate sales order report: " + ex.getMessage(), ex);
        }
    }

    public List<InvoiceReportRow> getInvoiceReport(LocalDate from, LocalDate to, String status) {
        boolean filterStatus = hasFilter(status);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT inv.InvoiceNo, o.OrderNo, c.CustomerName, inv.InvoiceDate, inv.DueDate, ")
           .append("inv.TotalAmount, inv.AmountPaid, inv.Balance, inv.Status ")
           .append("FROM tblinvoice inv ")
           .append("INNER JOIN tblsalesorder o ON inv.OrderID = o.ID ")
           .append("INNER JOIN tblcustomer c ON inv.CustomerID = c.ID ")
           .append("WHERE inv.InvoiceDate >= ? AND inv.InvoiceDate < ? ");
        if (filterStatus) {
            sql.append("AND inv.Status = ? ");
        }
        sql.append("ORDER BY inv.InvoiceDate DESC, inv.ID DESC");

        List<InvoiceReportRow> rows = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setTimestamp(1, Timestamp.valueOf(from.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(to.plusDays(1).atStartOfDay()));
            if (filterStatus) {
                stmt.setString(3, status.trim());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    InvoiceReportRow row = new InvoiceReportRow();
                    row.setInvoiceNo(rs.getString("InvoiceNo"));
                    row.setOrderNo(rs.getString("OrderNo"));
                    row.setCustomerName(rs.getString("CustomerName"));
                    Timestamp invTs = rs.getTimestamp("InvoiceDate");
                    row.setInvoiceDate(invTs != null ? invTs.toLocalDateTime() : null);
                    Date due = rs.getDate("DueDate");
                    row.setDueDate(due != null ? due.toLocalDate() : null);
                    row.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                    row.setAmountPaid(rs.getBigDecimal("AmountPaid"));
                    row.setBalance(rs.getBigDecimal("Balance"));
                    row.setStatus(rs.getString("Status"));
                    rows.add(row);
                }
            }
            return rows;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to generate invoice report: " + ex.getMessage(), ex);
        }
    }

    public List<PaymentReportRow> getPaymentReport(LocalDate from, LocalDate to, String method) {
        boolean filterMethod = hasFilter(method);
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.ReceiptNo, inv.InvoiceNo, c.CustomerName, e.EmployeeName AS CashierName, ")
           .append("p.PaymentDate, p.PaymentMethod, p.ReferenceNo, p.AmountPaid ")
           .append("FROM tblpayment p ")
           .append("INNER JOIN tblinvoice inv ON p.InvoiceID = inv.ID ")
           .append("INNER JOIN tblcustomer c ON inv.CustomerID = c.ID ")
           .append("INNER JOIN tblemployee e ON p.EmployeeID = e.ID ")
           .append("WHERE p.PaymentDate >= ? AND p.PaymentDate < ? ");
        if (filterMethod) {
            sql.append("AND p.PaymentMethod = ? ");
        }
        sql.append("ORDER BY p.PaymentDate DESC, p.ID DESC");

        List<PaymentReportRow> rows = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setTimestamp(1, Timestamp.valueOf(from.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(to.plusDays(1).atStartOfDay()));
            if (filterMethod) {
                stmt.setString(3, method.trim());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PaymentReportRow row = new PaymentReportRow();
                    row.setReceiptNo(rs.getString("ReceiptNo"));
                    row.setInvoiceNo(rs.getString("InvoiceNo"));
                    row.setCustomerName(rs.getString("CustomerName"));
                    row.setCashierName(rs.getString("CashierName"));
                    Timestamp ts = rs.getTimestamp("PaymentDate");
                    row.setPaymentDate(ts != null ? ts.toLocalDateTime() : null);
                    row.setPaymentMethod(rs.getString("PaymentMethod"));
                    row.setReferenceNo(rs.getString("ReferenceNo"));
                    row.setAmountPaid(rs.getBigDecimal("AmountPaid"));
                    rows.add(row);
                }
            }
            return rows;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to generate payment report: " + ex.getMessage(), ex);
        }
    }

    private boolean hasFilter(String value) {
        return value != null && !value.trim().isEmpty() && !"ALL".equalsIgnoreCase(value.trim());
    }
}
