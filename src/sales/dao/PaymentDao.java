package sales.dao;

import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import sales.model.Payment;

public class PaymentDao {

    private final HikariDataSource dataSource;

    public PaymentDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Payment> findAll() {
        String sql = "SELECT p.ID, p.ReceiptNo, p.InvoiceID, inv.InvoiceNo, c.CustomerName, "
                + "p.EmployeeID, e.EmployeeName, p.PaymentDate, p.AmountPaid, p.PaymentMethod, "
                + "p.ReferenceNo, p.Remarks "
                + "FROM tblpayment p "
                + "INNER JOIN tblinvoice inv ON p.InvoiceID = inv.ID "
                + "INNER JOIN tblcustomer c ON inv.CustomerID = c.ID "
                + "INNER JOIN tblemployee e ON p.EmployeeID = e.ID "
                + "ORDER BY p.ID DESC";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list payments: " + ex.getMessage(), ex);
        }
    }

    public List<Payment> findByInvoiceId(int invoiceId) {
        String sql = "SELECT p.ID, p.ReceiptNo, p.InvoiceID, inv.InvoiceNo, c.CustomerName, "
                + "p.EmployeeID, e.EmployeeName, p.PaymentDate, p.AmountPaid, p.PaymentMethod, "
                + "p.ReferenceNo, p.Remarks "
                + "FROM tblpayment p "
                + "INNER JOIN tblinvoice inv ON p.InvoiceID = inv.ID "
                + "INNER JOIN tblcustomer c ON inv.CustomerID = c.ID "
                + "INNER JOIN tblemployee e ON p.EmployeeID = e.ID "
                + "WHERE p.InvoiceID = ? "
                + "ORDER BY p.ID DESC";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to list invoice payments: " + ex.getMessage(), ex);
        }
    }

    public Payment recordPayment(Payment payment) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String lockSql = "SELECT ID, TotalAmount, AmountPaid, Balance, Status FROM tblinvoice WHERE ID = ? FOR UPDATE";
                BigDecimal totalAmount;
                BigDecimal currentPaid;
                BigDecimal currentBalance;
                String currentStatus;
                try (PreparedStatement stmt = conn.prepareStatement(lockSql)) {
                    stmt.setInt(1, payment.getInvoiceId());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            throw new IllegalArgumentException("Selected invoice was not found.");
                        }
                        totalAmount = rs.getBigDecimal("TotalAmount");
                        currentPaid = rs.getBigDecimal("AmountPaid");
                        currentBalance = rs.getBigDecimal("Balance");
                        currentStatus = rs.getString("Status");
                    }
                }

                if ("PAID".equals(currentStatus) || currentBalance.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalStateException("This invoice is already fully paid.");
                }

                BigDecimal payAmount = payment.getAmountPaid().setScale(2, RoundingMode.HALF_UP);
                if (payAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Payment amount must be greater than zero.");
                }
                if (payAmount.compareTo(currentBalance) > 0) {
                    throw new IllegalArgumentException("Payment amount (" + payAmount.toPlainString()
                            + ") exceeds remaining balance (" + currentBalance.toPlainString() + ").");
                }

                String receiptNo = generateNextReceiptNo(conn);
                LocalDateTime paymentDate = payment.getPaymentDate() != null ? payment.getPaymentDate() : LocalDateTime.now();

                String insertSql = "INSERT INTO tblpayment (ReceiptNo, InvoiceID, EmployeeID, PaymentDate, AmountPaid, PaymentMethod, ReferenceNo, Remarks) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                int paymentId;
                try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, receiptNo);
                    stmt.setInt(2, payment.getInvoiceId());
                    stmt.setInt(3, payment.getEmployeeId());
                    stmt.setTimestamp(4, Timestamp.valueOf(paymentDate));
                    stmt.setBigDecimal(5, payAmount);
                    stmt.setString(6, payment.getPaymentMethod());
                    stmt.setString(7, payment.getReferenceNo());
                    stmt.setString(8, payment.getRemarks());
                    stmt.executeUpdate();
                    try (ResultSet keys = stmt.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("Failed to retrieve generated payment ID.");
                        }
                        paymentId = keys.getInt(1);
                    }
                }

                BigDecimal newAmountPaid = currentPaid.add(payAmount).setScale(2, RoundingMode.HALF_UP);
                BigDecimal newBalance = totalAmount.subtract(newAmountPaid).setScale(2, RoundingMode.HALF_UP);
                String newStatus = newBalance.compareTo(BigDecimal.ZERO) == 0 ? "PAID" : "PARTIALLY_PAID";

                String updateInvoiceSql = "UPDATE tblinvoice SET AmountPaid = ?, Balance = ?, Status = ? WHERE ID = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateInvoiceSql)) {
                    stmt.setBigDecimal(1, newAmountPaid);
                    stmt.setBigDecimal(2, newBalance);
                    stmt.setString(3, newStatus);
                    stmt.setInt(4, payment.getInvoiceId());
                    stmt.executeUpdate();
                }

                conn.commit();
                payment.setId(paymentId);
                payment.setReceiptNo(receiptNo);
                payment.setPaymentDate(paymentDate);
                payment.setAmountPaid(payAmount);
                return payment;
            } catch (SQLException | RuntimeException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to record payment: " + ex.getMessage(), ex);
        }
    }

    private String generateNextReceiptNo(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(ID), 0) + 1 AS NextNum FROM tblpayment FOR UPDATE";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            int nextNum = rs.next() ? rs.getInt("NextNum") : 1;
            return String.format("OR-%04d", nextNum);
        }
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getInt("ID"));
        p.setReceiptNo(rs.getString("ReceiptNo"));
        p.setInvoiceId(rs.getInt("InvoiceID"));
        p.setInvoiceNo(rs.getString("InvoiceNo"));
        p.setCustomerName(rs.getString("CustomerName"));
        p.setEmployeeId(rs.getInt("EmployeeID"));
        p.setEmployeeName(rs.getString("EmployeeName"));
        Timestamp ts = rs.getTimestamp("PaymentDate");
        p.setPaymentDate(ts != null ? ts.toLocalDateTime() : null);
        p.setAmountPaid(rs.getBigDecimal("AmountPaid"));
        p.setPaymentMethod(rs.getString("PaymentMethod"));
        p.setReferenceNo(rs.getString("ReferenceNo"));
        p.setRemarks(rs.getString("Remarks"));
        return p;
    }
}
