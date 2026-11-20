package sales.config;

import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Ensures database schema tables and starter seed records exist on startup.
 */
public final class SalesSchema {

    private SalesSchema() {
    }

    public static void ensureInstalled(HikariDataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                createTables(conn);
                seedEmployeesIfEmpty(conn);
                seedCustomersIfEmpty(conn);
                seedProductsIfEmpty(conn);
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to initialize database schema: " + ex.getMessage(), ex);
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        String[] ddlStatements = new String[] {
            "CREATE TABLE IF NOT EXISTS tblemployee ("
                + "ID INT AUTO_INCREMENT PRIMARY KEY, "
                + "EmployeeName VARCHAR(120) NOT NULL UNIQUE, "
                + "Username VARCHAR(60) NOT NULL UNIQUE, "
                + "Password VARCHAR(60) NOT NULL, "
                + "Role VARCHAR(30) NOT NULL DEFAULT 'SALES'"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS tblcustomer ("
                + "ID INT AUTO_INCREMENT PRIMARY KEY, "
                + "CustomerName VARCHAR(120) NOT NULL, "
                + "Contact VARCHAR(60), "
                + "Address VARCHAR(200)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS tblproduct ("
                + "ID INT AUTO_INCREMENT PRIMARY KEY, "
                + "ProductCode VARCHAR(40) NOT NULL UNIQUE, "
                + "ProductName VARCHAR(120) NOT NULL, "
                + "UnitPrice DECIMAL(12,2) NOT NULL DEFAULT 0.00, "
                + "StockQty INT NOT NULL DEFAULT 0"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS tblsalesorder ("
                + "ID INT AUTO_INCREMENT PRIMARY KEY, "
                + "OrderNo VARCHAR(30) NOT NULL UNIQUE, "
                + "CustomerID INT NOT NULL, "
                + "EmployeeID INT NOT NULL, "
                + "OrderDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "Status VARCHAR(20) NOT NULL DEFAULT 'OPEN', "
                + "TotalAmount DECIMAL(12,2) NOT NULL DEFAULT 0.00, "
                + "CONSTRAINT fk_salesorder_customer FOREIGN KEY (CustomerID) REFERENCES tblcustomer(ID) ON DELETE RESTRICT ON UPDATE CASCADE, "
                + "CONSTRAINT fk_salesorder_employee FOREIGN KEY (EmployeeID) REFERENCES tblemployee(ID) ON DELETE RESTRICT ON UPDATE CASCADE"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS tblsalesorderitem ("
                + "ID INT AUTO_INCREMENT PRIMARY KEY, "
                + "OrderID INT NOT NULL, "
                + "ProductID INT NOT NULL, "
                + "Quantity INT NOT NULL, "
                + "UnitPrice DECIMAL(12,2) NOT NULL, "
                + "LineTotal DECIMAL(12,2) NOT NULL, "
                + "CONSTRAINT fk_orderitem_order FOREIGN KEY (OrderID) REFERENCES tblsalesorder(ID) ON DELETE CASCADE ON UPDATE CASCADE, "
                + "CONSTRAINT fk_orderitem_product FOREIGN KEY (ProductID) REFERENCES tblproduct(ID) ON DELETE RESTRICT ON UPDATE CASCADE"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS tblinvoice ("
                + "ID INT AUTO_INCREMENT PRIMARY KEY, "
                + "InvoiceNo VARCHAR(30) NOT NULL UNIQUE, "
                + "OrderID INT NOT NULL UNIQUE, "
                + "CustomerID INT NOT NULL, "
                + "EmployeeID INT NOT NULL, "
                + "InvoiceDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "DueDate DATE NOT NULL, "
                + "TotalAmount DECIMAL(12,2) NOT NULL, "
                + "AmountPaid DECIMAL(12,2) NOT NULL DEFAULT 0.00, "
                + "Balance DECIMAL(12,2) NOT NULL, "
                + "Status VARCHAR(20) NOT NULL DEFAULT 'UNPAID', "
                + "CONSTRAINT fk_invoice_order FOREIGN KEY (OrderID) REFERENCES tblsalesorder(ID) ON DELETE RESTRICT ON UPDATE CASCADE, "
                + "CONSTRAINT fk_invoice_customer FOREIGN KEY (CustomerID) REFERENCES tblcustomer(ID) ON DELETE RESTRICT ON UPDATE CASCADE, "
                + "CONSTRAINT fk_invoice_employee FOREIGN KEY (EmployeeID) REFERENCES tblemployee(ID) ON DELETE RESTRICT ON UPDATE CASCADE"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS tblinvoiceitem ("
                + "ID INT AUTO_INCREMENT PRIMARY KEY, "
                + "InvoiceID INT NOT NULL, "
                + "ProductID INT NOT NULL, "
                + "Quantity INT NOT NULL, "
                + "UnitPrice DECIMAL(12,2) NOT NULL, "
                + "LineTotal DECIMAL(12,2) NOT NULL, "
                + "CONSTRAINT fk_invoiceitem_invoice FOREIGN KEY (InvoiceID) REFERENCES tblinvoice(ID) ON DELETE CASCADE ON UPDATE CASCADE, "
                + "CONSTRAINT fk_invoiceitem_product FOREIGN KEY (ProductID) REFERENCES tblproduct(ID) ON DELETE RESTRICT ON UPDATE CASCADE"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            "CREATE TABLE IF NOT EXISTS tblpayment ("
                + "ID INT AUTO_INCREMENT PRIMARY KEY, "
                + "ReceiptNo VARCHAR(30) NOT NULL UNIQUE, "
                + "InvoiceID INT NOT NULL, "
                + "EmployeeID INT NOT NULL, "
                + "PaymentDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "AmountPaid DECIMAL(12,2) NOT NULL, "
                + "PaymentMethod VARCHAR(30) NOT NULL, "
                + "ReferenceNo VARCHAR(60), "
                + "Remarks VARCHAR(200), "
                + "CONSTRAINT fk_payment_invoice FOREIGN KEY (InvoiceID) REFERENCES tblinvoice(ID) ON DELETE RESTRICT ON UPDATE CASCADE, "
                + "CONSTRAINT fk_payment_employee FOREIGN KEY (EmployeeID) REFERENCES tblemployee(ID) ON DELETE RESTRICT ON UPDATE CASCADE"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String sql : ddlStatements) {
                stmt.execute(sql);
            }
        }
    }

    private static boolean isTableEmpty(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    private static void seedEmployeesIfEmpty(Connection conn) throws SQLException {
        if (!isTableEmpty(conn, "tblemployee")) {
            return;
        }
        String sql = "INSERT INTO tblemployee (EmployeeName, Username, Password, Role) VALUES (?, ?, ?, ?)";
        Object[][] seeds = new Object[][] {
            { "System Admin", "admin", "admin", "ADMIN" },
            { "Maria Santos", "msantos", "sales123", "SALES" },
            { "Juan Dela Cruz", "jdelacruz", "cashier123", "CASHIER" }
        };
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Object[] row : seeds) {
                stmt.setString(1, (String) row[0]);
                stmt.setString(2, (String) row[1]);
                stmt.setString(3, (String) row[2]);
                stmt.setString(4, (String) row[3]);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private static void seedCustomersIfEmpty(Connection conn) throws SQLException {
        if (!isTableEmpty(conn, "tblcustomer")) {
            return;
        }
        String sql = "INSERT INTO tblcustomer (CustomerName, Contact, Address) VALUES (?, ?, ?)";
        String[][] seeds = new String[][] {
            { "Apex Retail Trading Corp.", "0917-555-0101", "124 Ayala Ave., Makati City" },
            { "Horizon Office Supplies Inc.", "0918-555-0202", "88 Ortigas Center, Pasig City" },
            { "Metro Builders & Hardware", "0920-555-0303", "45 Quezon Ave., Quezon City" },
            { "Summit Logistics Solutions", "0917-555-0404", "301 Bonifacio High St., Taguig City" }
        };
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String[] row : seeds) {
                stmt.setString(1, row[0]);
                stmt.setString(2, row[1]);
                stmt.setString(3, row[2]);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private static void seedProductsIfEmpty(Connection conn) throws SQLException {
        if (!isTableEmpty(conn, "tblproduct")) {
            return;
        }
        String sql = "INSERT INTO tblproduct (ProductCode, ProductName, UnitPrice, StockQty) VALUES (?, ?, ?, ?)";
        Object[][] seeds = new Object[][] {
            { "PRD-001", "Wireless Barcode Scanner", new BigDecimal("2450.00"), 40 },
            { "PRD-002", "Thermal Receipt Printer 80mm", new BigDecimal("4890.00"), 25 },
            { "PRD-003", "POS Cash Drawer Steel 5-Bill", new BigDecimal("3200.00"), 20 },
            { "PRD-004", "A4 Bond Paper 80gsm (Ream)", new BigDecimal("265.00"), 150 },
            { "PRD-005", "Ergonomic Office Chair", new BigDecimal("6750.00"), 15 },
            { "PRD-006", "24-Inch IPS Business Monitor", new BigDecimal("8490.00"), 18 }
        };
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Object[] row : seeds) {
                stmt.setString(1, (String) row[0]);
                stmt.setString(2, (String) row[1]);
                stmt.setBigDecimal(3, (BigDecimal) row[2]);
                stmt.setInt(4, (Integer) row[3]);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
}
