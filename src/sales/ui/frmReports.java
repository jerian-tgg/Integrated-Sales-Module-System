package sales.ui;

import java.awt.Color;
import java.awt.print.PrinterException;
import java.io.File;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import sales.AppContext;
import sales.model.InvoiceReportRow;
import sales.model.PaymentReportRow;
import sales.model.SalesOrderReportRow;

public class frmReports extends javax.swing.JFrame {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AppContext appContext;

    public frmReports() {
        this(null);
    }

    public frmReports(AppContext appContext) {
        this.appContext = appContext;
        initComponents();
        if (this.appContext != null) {
            initCustomLogic();
        }
    }

    private void initCustomLogic() {
        getContentPane().setBackground(new Color(244, 246, 248));
        setLocationRelativeTo(null);

        String startOfMonth = LocalDate.now().withDayOfMonth(1).toString();
        String today = LocalDate.now().toString();
        txtOrdFrom.setText(startOfMonth);
        txtOrdTo.setText(today);
        txtInvFrom.setText(startOfMonth);
        txtInvTo.setText(today);
        txtPayFrom.setText(startOfMonth);
        txtPayTo.setText(today);

        generateOrderReport();
        generateInvoiceReport();
        generatePaymentReport();
    }

    private LocalDate parseDate(String text, String label) {
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(label + " must be formatted as yyyy-MM-dd.");
        }
    }

    private void generateOrderReport() {
        if (appContext == null) {
            return;
        }
        try {
            LocalDate from = parseDate(txtOrdFrom.getText(), "Date From");
            LocalDate to = parseDate(txtOrdTo.getText(), "Date To");
            String status = String.valueOf(cboOrdStatus.getSelectedItem());
            List<SalesOrderReportRow> rows = appContext.getReportService().getSalesOrderReport(from, to, status);

            DefaultTableModel model = (DefaultTableModel) tblOrderReport.getModel();
            model.setRowCount(0);
            BigDecimal grandTotal = BigDecimal.ZERO;
            for (SalesOrderReportRow r : rows) {
                grandTotal = grandTotal.add(r.getTotalAmount());
                model.addRow(new Object[]{
                    r.getOrderNo(),
                    r.getOrderDate() != null ? r.getOrderDate().format(DATE_TIME_FMT) : "",
                    r.getCustomerName(),
                    r.getEmployeeName(),
                    r.getItemCount(),
                    r.getStatus(),
                    r.getTotalAmount().toPlainString()
                });
            }
            lblOrdCount.setText("Total Orders: " + rows.size());
            lblOrdTotal.setText(String.format("Grand Total: %,.2f", grandTotal));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to generate sales order report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateInvoiceReport() {
        if (appContext == null) {
            return;
        }
        try {
            LocalDate from = parseDate(txtInvFrom.getText(), "Date From");
            LocalDate to = parseDate(txtInvTo.getText(), "Date To");
            String status = String.valueOf(cboInvStatus.getSelectedItem());
            List<InvoiceReportRow> rows = appContext.getReportService().getInvoiceReport(from, to, status);

            DefaultTableModel model = (DefaultTableModel) tblInvoiceReport.getModel();
            model.setRowCount(0);
            BigDecimal totalBilled = BigDecimal.ZERO;
            BigDecimal totalPaid = BigDecimal.ZERO;
            BigDecimal totalBalance = BigDecimal.ZERO;
            for (InvoiceReportRow r : rows) {
                totalBilled = totalBilled.add(r.getTotalAmount());
                totalPaid = totalPaid.add(r.getAmountPaid());
                totalBalance = totalBalance.add(r.getBalance());
                model.addRow(new Object[]{
                    r.getInvoiceNo(),
                    r.getOrderNo(),
                    r.getCustomerName(),
                    r.getInvoiceDate() != null ? r.getInvoiceDate().format(DATE_TIME_FMT) : "",
                    r.getDueDate() != null ? r.getDueDate().toString() : "",
                    r.getTotalAmount().toPlainString(),
                    r.getAmountPaid().toPlainString(),
                    r.getBalance().toPlainString(),
                    r.getStatus()
                });
            }
            lblInvBilled.setText(String.format("Total Billed: %,.2f", totalBilled));
            lblInvPaid.setText(String.format("Total Paid: %,.2f", totalPaid));
            lblInvReceivable.setText(String.format("Unpaid Receivables: %,.2f", totalBalance));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to generate invoice report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generatePaymentReport() {
        if (appContext == null) {
            return;
        }
        try {
            LocalDate from = parseDate(txtPayFrom.getText(), "Date From");
            LocalDate to = parseDate(txtPayTo.getText(), "Date To");
            String method = String.valueOf(cboPayMethod.getSelectedItem());
            List<PaymentReportRow> rows = appContext.getReportService().getPaymentReport(from, to, method);

            DefaultTableModel model = (DefaultTableModel) tblPaymentReport.getModel();
            model.setRowCount(0);
            BigDecimal totalCollected = BigDecimal.ZERO;
            for (PaymentReportRow r : rows) {
                totalCollected = totalCollected.add(r.getAmountPaid());
                model.addRow(new Object[]{
                    r.getReceiptNo(),
                    r.getInvoiceNo(),
                    r.getCustomerName(),
                    r.getCashierName(),
                    r.getPaymentDate() != null ? r.getPaymentDate().format(DATE_TIME_FMT) : "",
                    r.getPaymentMethod(),
                    r.getReferenceNo(),
                    r.getAmountPaid().toPlainString()
                });
            }
            lblPayCount.setText("Total Receipts: " + rows.size());
            lblPayTotal.setText(String.format("Total Collected: %,.2f", totalCollected));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to generate payment report: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void printReportTable(JTable table, String headerTitle) {
        try {
            boolean complete = table.print(JTable.PrintMode.FIT_WIDTH,
                    new MessageFormat(headerTitle),
                    new MessageFormat("Page {0}"));
            if (!complete) {
                JOptionPane.showMessageDialog(this, "Printing cancelled.", "Print", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Failed to print report: " + ex.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportTableToCsv(JTable table, String defaultFileName) {
        if (appContext == null) {
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(defaultFileName));
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File target = chooser.getSelectedFile();
        if (!target.getName().toLowerCase().endsWith(".csv")) {
            target = new File(target.getParentFile(), target.getName() + ".csv");
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        List<String> headers = new ArrayList<>();
        for (int c = 0; c < model.getColumnCount(); c++) {
            headers.add(model.getColumnName(c));
        }
        List<List<String>> rows = new ArrayList<>();
        for (int r = 0; r < model.getRowCount(); r++) {
            List<String> row = new ArrayList<>();
            for (int c = 0; c < model.getColumnCount(); c++) {
                Object val = model.getValueAt(r, c);
                row.add(val != null ? val.toString() : "");
            }
            rows.add(row);
        }

        try {
            appContext.getReportService().exportToCsv(headers, rows, target);
            JOptionPane.showMessageDialog(this, "Exported CSV to:\n" + target.getAbsolutePath(),
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "CSV export failed: " + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        tabReports = new javax.swing.JTabbedPane();
        pnlTabOrders = new javax.swing.JPanel();
        lblOrdFrom = new javax.swing.JLabel();
        txtOrdFrom = new javax.swing.JTextField();
        lblOrdTo = new javax.swing.JLabel();
        txtOrdTo = new javax.swing.JTextField();
        lblOrdStatus = new javax.swing.JLabel();
        cboOrdStatus = new javax.swing.JComboBox<>();
        btnOrdGenerate = new javax.swing.JButton();
        btnOrdPrint = new javax.swing.JButton();
        btnOrdExport = new javax.swing.JButton();
        jScrollPaneOrd = new javax.swing.JScrollPane();
        tblOrderReport = new javax.swing.JTable();
        lblOrdCount = new javax.swing.JLabel();
        lblOrdTotal = new javax.swing.JLabel();
        pnlTabInvoices = new javax.swing.JPanel();
        lblInvFrom = new javax.swing.JLabel();
        txtInvFrom = new javax.swing.JTextField();
        lblInvTo = new javax.swing.JLabel();
        txtInvTo = new javax.swing.JTextField();
        lblInvStatus = new javax.swing.JLabel();
        cboInvStatus = new javax.swing.JComboBox<>();
        btnInvGenerate = new javax.swing.JButton();
        btnInvPrint = new javax.swing.JButton();
        btnInvExport = new javax.swing.JButton();
        jScrollPaneInv = new javax.swing.JScrollPane();
        tblInvoiceReport = new javax.swing.JTable();
        lblInvBilled = new javax.swing.JLabel();
        lblInvPaid = new javax.swing.JLabel();
        lblInvReceivable = new javax.swing.JLabel();
        pnlTabPayments = new javax.swing.JPanel();
        lblPayFrom = new javax.swing.JLabel();
        txtPayFrom = new javax.swing.JTextField();
        lblPayTo = new javax.swing.JLabel();
        txtPayTo = new javax.swing.JTextField();
        lblPayMethod = new javax.swing.JLabel();
        cboPayMethod = new javax.swing.JComboBox<>();
        btnPayGenerate = new javax.swing.JButton();
        btnPayPrint = new javax.swing.JButton();
        btnPayExport = new javax.swing.JButton();
        jScrollPanePay = new javax.swing.JScrollPane();
        tblPaymentReport = new javax.swing.JTable();
        lblPayCount = new javax.swing.JLabel();
        lblPayTotal = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sales System Reports");
        setPreferredSize(new java.awt.Dimension(1040, 670));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlHeader.setBackground(new java.awt.Color(10, 114, 162));
        pnlHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Sales System Reports");
        pnlHeader.add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 10, 420, 28));

        getContentPane().add(pnlHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1040, 50));

        tabReports.setFont(new java.awt.Font("Segoe UI", 1, 12));

        pnlTabOrders.setBackground(new java.awt.Color(255, 255, 255));
        pnlTabOrders.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblOrdFrom.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblOrdFrom.setText("Date From (yyyy-MM-dd):");
        pnlTabOrders.add(lblOrdFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 14, 150, 28));

        txtOrdFrom.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlTabOrders.add(txtOrdFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(164, 14, 102, 28));

        lblOrdTo.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblOrdTo.setText("To:");
        pnlTabOrders.add(lblOrdTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(276, 14, 24, 28));

        txtOrdTo.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlTabOrders.add(txtOrdTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(302, 14, 102, 28));

        lblOrdStatus.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblOrdStatus.setText("Status:");
        pnlTabOrders.add(lblOrdStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(414, 14, 48, 28));

        cboOrdStatus.setFont(new java.awt.Font("Segoe UI", 0, 12));
        cboOrdStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "OPEN", "CONFIRMED", "INVOICED", "CANCELLED" }));
        pnlTabOrders.add(cboOrdStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(464, 14, 128, 28));

        btnOrdGenerate.setBackground(new java.awt.Color(10, 114, 162));
        btnOrdGenerate.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnOrdGenerate.setForeground(new java.awt.Color(255, 255, 255));
        btnOrdGenerate.setText("Generate Report");
        btnOrdGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrdGenerateActionPerformed(evt);
            }
        });
        pnlTabOrders.add(btnOrdGenerate, new org.netbeans.lib.awtextra.AbsoluteConstraints(606, 13, 134, 30));

        btnOrdPrint.setBackground(new java.awt.Color(108, 117, 125));
        btnOrdPrint.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnOrdPrint.setForeground(new java.awt.Color(255, 255, 255));
        btnOrdPrint.setText("Print");
        btnOrdPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrdPrintActionPerformed(evt);
            }
        });
        pnlTabOrders.add(btnOrdPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 13, 94, 30));

        btnOrdExport.setBackground(new java.awt.Color(46, 125, 50));
        btnOrdExport.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnOrdExport.setForeground(new java.awt.Color(255, 255, 255));
        btnOrdExport.setText("Export CSV");
        btnOrdExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOrdExportActionPerformed(evt);
            }
        });
        pnlTabOrders.add(btnOrdExport, new org.netbeans.lib.awtextra.AbsoluteConstraints(854, 13, 118, 30));

        tblOrderReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order No", "Order Date", "Customer", "Sales Rep", "Item Count", "Status", "Total Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneOrd.setViewportView(tblOrderReport);

        pnlTabOrders.add(jScrollPaneOrd, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 54, 960, 408));

        lblOrdCount.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblOrdCount.setText("Total Orders: 0");
        pnlTabOrders.add(lblOrdCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 474, 260, 28));

        lblOrdTotal.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblOrdTotal.setForeground(new java.awt.Color(10, 114, 162));
        lblOrdTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblOrdTotal.setText("Grand Total: 0.00");
        pnlTabOrders.add(lblOrdTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(672, 474, 300, 28));

        tabReports.addTab("Sales Order Report", pnlTabOrders);

        pnlTabInvoices.setBackground(new java.awt.Color(255, 255, 255));
        pnlTabInvoices.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblInvFrom.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblInvFrom.setText("Date From (yyyy-MM-dd):");
        pnlTabInvoices.add(lblInvFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 14, 150, 28));

        txtInvFrom.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlTabInvoices.add(txtInvFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(164, 14, 102, 28));

        lblInvTo.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblInvTo.setText("To:");
        pnlTabInvoices.add(lblInvTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(276, 14, 24, 28));

        txtInvTo.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlTabInvoices.add(txtInvTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(302, 14, 102, 28));

        lblInvStatus.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblInvStatus.setText("Status:");
        pnlTabInvoices.add(lblInvStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(414, 14, 48, 28));

        cboInvStatus.setFont(new java.awt.Font("Segoe UI", 0, 12));
        cboInvStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "UNPAID", "PARTIALLY_PAID", "PAID" }));
        pnlTabInvoices.add(cboInvStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(464, 14, 128, 28));

        btnInvGenerate.setBackground(new java.awt.Color(10, 114, 162));
        btnInvGenerate.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnInvGenerate.setForeground(new java.awt.Color(255, 255, 255));
        btnInvGenerate.setText("Generate Report");
        btnInvGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInvGenerateActionPerformed(evt);
            }
        });
        pnlTabInvoices.add(btnInvGenerate, new org.netbeans.lib.awtextra.AbsoluteConstraints(606, 13, 134, 30));

        btnInvPrint.setBackground(new java.awt.Color(108, 117, 125));
        btnInvPrint.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnInvPrint.setForeground(new java.awt.Color(255, 255, 255));
        btnInvPrint.setText("Print");
        btnInvPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInvPrintActionPerformed(evt);
            }
        });
        pnlTabInvoices.add(btnInvPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 13, 94, 30));

        btnInvExport.setBackground(new java.awt.Color(46, 125, 50));
        btnInvExport.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnInvExport.setForeground(new java.awt.Color(255, 255, 255));
        btnInvExport.setText("Export CSV");
        btnInvExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInvExportActionPerformed(evt);
            }
        });
        pnlTabInvoices.add(btnInvExport, new org.netbeans.lib.awtextra.AbsoluteConstraints(854, 13, 118, 30));

        tblInvoiceReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Invoice No", "Order No", "Customer", "Invoice Date", "Due Date", "Total", "Paid", "Balance", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneInv.setViewportView(tblInvoiceReport);

        pnlTabInvoices.add(jScrollPaneInv, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 54, 960, 408));

        lblInvBilled.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblInvBilled.setText("Total Billed: 0.00");
        pnlTabInvoices.add(lblInvBilled, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 474, 280, 28));

        lblInvPaid.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblInvPaid.setForeground(new java.awt.Color(46, 125, 50));
        lblInvPaid.setText("Total Paid: 0.00");
        pnlTabInvoices.add(lblInvPaid, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 474, 280, 28));

        lblInvReceivable.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblInvReceivable.setForeground(new java.awt.Color(211, 108, 0));
        lblInvReceivable.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblInvReceivable.setText("Unpaid Receivables: 0.00");
        pnlTabInvoices.add(lblInvReceivable, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 474, 332, 28));

        tabReports.addTab("Invoicing Report", pnlTabInvoices);

        pnlTabPayments.setBackground(new java.awt.Color(255, 255, 255));
        pnlTabPayments.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblPayFrom.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblPayFrom.setText("Date From (yyyy-MM-dd):");
        pnlTabPayments.add(lblPayFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 14, 150, 28));

        txtPayFrom.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlTabPayments.add(txtPayFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(164, 14, 102, 28));

        lblPayTo.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblPayTo.setText("To:");
        pnlTabPayments.add(lblPayTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(276, 14, 24, 28));

        txtPayTo.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlTabPayments.add(txtPayTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(302, 14, 102, 28));

        lblPayMethod.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblPayMethod.setText("Method:");
        pnlTabPayments.add(lblPayMethod, new org.netbeans.lib.awtextra.AbsoluteConstraints(412, 14, 52, 28));

        cboPayMethod.setFont(new java.awt.Font("Segoe UI", 0, 12));
        cboPayMethod.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL", "CASH", "BANK_TRANSFER", "CHECK", "CARD" }));
        pnlTabPayments.add(cboPayMethod, new org.netbeans.lib.awtextra.AbsoluteConstraints(466, 14, 128, 28));

        btnPayGenerate.setBackground(new java.awt.Color(10, 114, 162));
        btnPayGenerate.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnPayGenerate.setForeground(new java.awt.Color(255, 255, 255));
        btnPayGenerate.setText("Generate Report");
        btnPayGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayGenerateActionPerformed(evt);
            }
        });
        pnlTabPayments.add(btnPayGenerate, new org.netbeans.lib.awtextra.AbsoluteConstraints(606, 13, 134, 30));

        btnPayPrint.setBackground(new java.awt.Color(108, 117, 125));
        btnPayPrint.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnPayPrint.setForeground(new java.awt.Color(255, 255, 255));
        btnPayPrint.setText("Print");
        btnPayPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayPrintActionPerformed(evt);
            }
        });
        pnlTabPayments.add(btnPayPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 13, 94, 30));

        btnPayExport.setBackground(new java.awt.Color(46, 125, 50));
        btnPayExport.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnPayExport.setForeground(new java.awt.Color(255, 255, 255));
        btnPayExport.setText("Export CSV");
        btnPayExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayExportActionPerformed(evt);
            }
        });
        pnlTabPayments.add(btnPayExport, new org.netbeans.lib.awtextra.AbsoluteConstraints(854, 13, 118, 30));

        tblPaymentReport.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Receipt No", "Invoice No", "Customer", "Cashier", "Payment Date", "Method", "Reference No", "Amount Paid"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPanePay.setViewportView(tblPaymentReport);

        pnlTabPayments.add(jScrollPanePay, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 54, 960, 408));

        lblPayCount.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblPayCount.setText("Total Receipts: 0");
        pnlTabPayments.add(lblPayCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 474, 260, 28));

        lblPayTotal.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblPayTotal.setForeground(new java.awt.Color(46, 125, 50));
        lblPayTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPayTotal.setText("Total Collected: 0.00");
        pnlTabPayments.add(lblPayTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(672, 474, 300, 28));

        tabReports.addTab("Payment / Collection Report", pnlTabPayments);

        getContentPane().add(tabReports, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 58, 994, 552));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnOrdGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrdGenerateActionPerformed
        generateOrderReport();
    }//GEN-LAST:event_btnOrdGenerateActionPerformed

    private void btnOrdPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrdPrintActionPerformed
        printReportTable(tblOrderReport, "Sales Order Report");
    }//GEN-LAST:event_btnOrdPrintActionPerformed

    private void btnOrdExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrdExportActionPerformed
        exportTableToCsv(tblOrderReport, "sales_order_report.csv");
    }//GEN-LAST:event_btnOrdExportActionPerformed

    private void btnInvGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvGenerateActionPerformed
        generateInvoiceReport();
    }//GEN-LAST:event_btnInvGenerateActionPerformed

    private void btnInvPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvPrintActionPerformed
        printReportTable(tblInvoiceReport, "Invoicing Report");
    }//GEN-LAST:event_btnInvPrintActionPerformed

    private void btnInvExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvExportActionPerformed
        exportTableToCsv(tblInvoiceReport, "invoicing_report.csv");
    }//GEN-LAST:event_btnInvExportActionPerformed

    private void btnPayGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayGenerateActionPerformed
        generatePaymentReport();
    }//GEN-LAST:event_btnPayGenerateActionPerformed

    private void btnPayPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayPrintActionPerformed
        printReportTable(tblPaymentReport, "Payment Collection Report");
    }//GEN-LAST:event_btnPayPrintActionPerformed

    private void btnPayExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayExportActionPerformed
        exportTableToCsv(tblPaymentReport, "payment_collection_report.csv");
    }//GEN-LAST:event_btnPayExportActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInvExport;
    private javax.swing.JButton btnInvGenerate;
    private javax.swing.JButton btnInvPrint;
    private javax.swing.JButton btnOrdExport;
    private javax.swing.JButton btnOrdGenerate;
    private javax.swing.JButton btnOrdPrint;
    private javax.swing.JButton btnPayExport;
    private javax.swing.JButton btnPayGenerate;
    private javax.swing.JButton btnPayPrint;
    private javax.swing.JComboBox<String> cboInvStatus;
    private javax.swing.JComboBox<String> cboOrdStatus;
    private javax.swing.JComboBox<String> cboPayMethod;
    private javax.swing.JScrollPane jScrollPaneInv;
    private javax.swing.JScrollPane jScrollPaneOrd;
    private javax.swing.JScrollPane jScrollPanePay;
    private javax.swing.JLabel lblInvBilled;
    private javax.swing.JLabel lblInvFrom;
    private javax.swing.JLabel lblInvPaid;
    private javax.swing.JLabel lblInvReceivable;
    private javax.swing.JLabel lblInvStatus;
    private javax.swing.JLabel lblInvTo;
    private javax.swing.JLabel lblOrdCount;
    private javax.swing.JLabel lblOrdFrom;
    private javax.swing.JLabel lblOrdStatus;
    private javax.swing.JLabel lblOrdTo;
    private javax.swing.JLabel lblOrdTotal;
    private javax.swing.JLabel lblPayCount;
    private javax.swing.JLabel lblPayFrom;
    private javax.swing.JLabel lblPayMethod;
    private javax.swing.JLabel lblPayTo;
    private javax.swing.JLabel lblPayTotal;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlTabInvoices;
    private javax.swing.JPanel pnlTabOrders;
    private javax.swing.JPanel pnlTabPayments;
    private javax.swing.JTabbedPane tabReports;
    private javax.swing.JTable tblInvoiceReport;
    private javax.swing.JTable tblOrderReport;
    private javax.swing.JTable tblPaymentReport;
    private javax.swing.JTextField txtInvFrom;
    private javax.swing.JTextField txtInvTo;
    private javax.swing.JTextField txtOrdFrom;
    private javax.swing.JTextField txtOrdTo;
    private javax.swing.JTextField txtPayFrom;
    private javax.swing.JTextField txtPayTo;
    // End of variables declaration//GEN-END:variables
}
