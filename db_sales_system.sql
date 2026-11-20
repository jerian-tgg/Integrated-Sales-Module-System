-- Integrated Sales Module System Database Schema & Seed Data
-- Compatible with MySQL 5.7+ / 8.0+ / MariaDB (XAMPP / WAMP)

CREATE DATABASE IF NOT EXISTS db_sales_system
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE db_sales_system;

CREATE TABLE IF NOT EXISTS tblemployee (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    EmployeeName VARCHAR(120) NOT NULL UNIQUE,
    Username VARCHAR(60) NOT NULL UNIQUE,
    Password VARCHAR(60) NOT NULL,
    Role VARCHAR(30) NOT NULL DEFAULT 'SALES'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblcustomer (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    CustomerName VARCHAR(120) NOT NULL,
    Contact VARCHAR(60),
    Address VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblproduct (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ProductCode VARCHAR(40) NOT NULL UNIQUE,
    ProductName VARCHAR(120) NOT NULL,
    UnitPrice DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    StockQty INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblsalesorder (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    OrderNo VARCHAR(30) NOT NULL UNIQUE,
    CustomerID INT NOT NULL,
    EmployeeID INT NOT NULL,
    OrderDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    TotalAmount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_salesorder_customer FOREIGN KEY (CustomerID)
        REFERENCES tblcustomer(ID) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_salesorder_employee FOREIGN KEY (EmployeeID)
        REFERENCES tblemployee(ID) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblsalesorderitem (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(12,2) NOT NULL,
    LineTotal DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_orderitem_order FOREIGN KEY (OrderID)
        REFERENCES tblsalesorder(ID) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_orderitem_product FOREIGN KEY (ProductID)
        REFERENCES tblproduct(ID) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblinvoice (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    InvoiceNo VARCHAR(30) NOT NULL UNIQUE,
    OrderID INT NOT NULL UNIQUE,
    CustomerID INT NOT NULL,
    EmployeeID INT NOT NULL,
    InvoiceDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    DueDate DATE NOT NULL,
    TotalAmount DECIMAL(12,2) NOT NULL,
    AmountPaid DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    Balance DECIMAL(12,2) NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    CONSTRAINT fk_invoice_order FOREIGN KEY (OrderID)
        REFERENCES tblsalesorder(ID) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_invoice_customer FOREIGN KEY (CustomerID)
        REFERENCES tblcustomer(ID) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_invoice_employee FOREIGN KEY (EmployeeID)
        REFERENCES tblemployee(ID) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblinvoiceitem (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    InvoiceID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(12,2) NOT NULL,
    LineTotal DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_invoiceitem_invoice FOREIGN KEY (InvoiceID)
        REFERENCES tblinvoice(ID) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_invoiceitem_product FOREIGN KEY (ProductID)
        REFERENCES tblproduct(ID) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tblpayment (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    ReceiptNo VARCHAR(30) NOT NULL UNIQUE,
    InvoiceID INT NOT NULL,
    EmployeeID INT NOT NULL,
    PaymentDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    AmountPaid DECIMAL(12,2) NOT NULL,
    PaymentMethod VARCHAR(30) NOT NULL,
    ReferenceNo VARCHAR(60),
    Remarks VARCHAR(200),
    CONSTRAINT fk_payment_invoice FOREIGN KEY (InvoiceID)
        REFERENCES tblinvoice(ID) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_payment_employee FOREIGN KEY (EmployeeID)
        REFERENCES tblemployee(ID) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed starter employees (idempotent)
INSERT IGNORE INTO tblemployee (EmployeeName, Username, Password, Role) VALUES
    ('System Admin', 'admin', 'admin', 'ADMIN'),
    ('Maria Santos', 'msantos', 'sales123', 'SALES'),
    ('Juan Dela Cruz', 'jdelacruz', 'cashier123', 'CASHIER');

-- Seed starter customers (idempotent when empty)
INSERT INTO tblcustomer (CustomerName, Contact, Address)
SELECT * FROM (
    SELECT 'Apex Retail Trading Corp.' AS CustomerName, '0917-555-0101' AS Contact, '124 Ayala Ave., Makati City' AS Address
    UNION ALL SELECT 'Horizon Office Supplies Inc.', '0918-555-0202', '88 Ortigas Center, Pasig City'
    UNION ALL SELECT 'Metro Builders & Hardware', '0920-555-0303', '45 Quezon Ave., Quezon City'
    UNION ALL SELECT 'Summit Logistics Solutions', '0917-555-0404', '301 Bonifacio High St., Taguig City'
) AS seed_customers
WHERE NOT EXISTS (SELECT 1 FROM tblcustomer);

-- Seed starter products (idempotent)
INSERT IGNORE INTO tblproduct (ProductCode, ProductName, UnitPrice, StockQty) VALUES
    ('PRD-001', 'Wireless Barcode Scanner', 2450.00, 40),
    ('PRD-002', 'Thermal Receipt Printer 80mm', 4890.00, 25),
    ('PRD-003', 'POS Cash Drawer Steel 5-Bill', 3200.00, 20),
    ('PRD-004', 'A4 Bond Paper 80gsm (Ream)', 265.00, 150),
    ('PRD-005', 'Ergonomic Office Chair', 6750.00, 15),
    ('PRD-006', '24-Inch IPS Business Monitor', 8490.00, 18);
