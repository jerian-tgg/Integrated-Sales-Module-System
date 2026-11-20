package sales.ui;

import java.awt.Color;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import sales.AppContext;
import sales.model.Employee;
import sales.model.Invoice;
import sales.model.Payment;

public class frmPayment extends javax.swing.JFrame {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AppContext appContext;

    public frmPayment() {
        this(null);
    }

    public frmPayment(AppContext appContext) {
        this.appContext = appContext;
        initComponents();
        if (this.appContext != null) {
            initCustomLogic();
        }
    }

    private void initCustomLogic() {
        getContentPane().setBackground(new Color(244, 246, 248));
        setLocationRelativeTo(null);
        refreshAll();
    }

    private void refreshAll() {
        loadCashiers();
        loadUnpaidInvoices();
        refreshPaymentsTable();
    }

    private void loadCashiers() {
        if (appContext == null) {
            return;
        }
        try {
            DefaultComboBoxModel<Employee> model = new DefaultComboBoxModel<>();
            for (Employee e : appContext.getEmployeeService().getAll()) {
                model.addElement(e);
            }
            cboCashier.setModel(model);
            if (appContext.getCurrentEmployee() != null) {
                cboCashier.setSelectedItem(appContext.getCurrentEmployee());
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load employees: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadUnpaidInvoices() {
        if (appContext == null) {
            return;
        }
        try {
            DefaultComboBoxModel<Invoice> model = new DefaultComboBoxModel<>();
            for (Invoice inv : appContext.getInvoiceService().getUnpaidOrPartialInvoices()) {
                model.addElement(inv);
            }
            cboUnpaidInvoice.setModel(model);
            populateSelectedInvoiceSummary();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load unpaid invoices: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateSelectedInvoiceSummary() {
        Invoice selected = (Invoice) cboUnpaidInvoice.getSelectedItem();
        if (selected == null) {
            txtCustomer.setText("");
            txtInvoiceTotal.setText("");
            txtPaidSoFar.setText("");
            txtBalance.setText("");
            txtAmountPaid.setText("");
            return;
        }
        txtCustomer.setText(selected.getCustomerName());
        txtInvoiceTotal.setText(selected.getTotalAmount().toPlainString());
        txtPaidSoFar.setText(selected.getAmountPaid().toPlainString());
        txtBalance.setText(selected.getBalance().toPlainString());
        txtAmountPaid.setText(selected.getBalance().toPlainString());
    }

    private void refreshPaymentsTable() {
        if (appContext == null) {
            return;
        }
        try {
            List<Payment> payments;
            Invoice selected = (Invoice) cboUnpaidInvoice.getSelectedItem();
            if (chkFilterByInvoice.isSelected() && selected != null) {
                payments = appContext.getPaymentService().getByInvoiceId(selected.getId());
            } else {
                payments = appContext.getPaymentService().getAll();
            }

            DefaultTableModel model = (DefaultTableModel) tblPayments.getModel();
            model.setRowCount(0);
            for (Payment p : payments) {
                model.addRow(new Object[]{
                    p.getReceiptNo(),
                    p.getInvoiceNo(),
                    p.getCustomerName(),
                    p.getEmployeeName(),
                    p.getPaymentDate() != null ? p.getPaymentDate().format(DATE_TIME_FMT) : "",
                    p.getPaymentMethod(),
                    p.getReferenceNo(),
                    p.getAmountPaid().toPlainString(),
                    p.getRemarks()
                });
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load payments: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearEntryFields() {
        txtReferenceNo.setText("");
        txtRemarks.setText("");
        cboPaymentMethod.setSelectedIndex(0);
        populateSelectedInvoiceSummary();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        pnlEntry = new javax.swing.JPanel();
        lblUnpaidInvoice = new javax.swing.JLabel();
        cboUnpaidInvoice = new javax.swing.JComboBox<>();
        lblCustomer = new javax.swing.JLabel();
        txtCustomer = new javax.swing.JTextField();
        lblInvoiceTotal = new javax.swing.JLabel();
        txtInvoiceTotal = new javax.swing.JTextField();
        lblPaidSoFar = new javax.swing.JLabel();
        txtPaidSoFar = new javax.swing.JTextField();
        lblBalance = new javax.swing.JLabel();
        txtBalance = new javax.swing.JTextField();
        lblCashier = new javax.swing.JLabel();
        cboCashier = new javax.swing.JComboBox<>();
        lblAmountPaid = new javax.swing.JLabel();
        txtAmountPaid = new javax.swing.JTextField();
        lblPaymentMethod = new javax.swing.JLabel();
        cboPaymentMethod = new javax.swing.JComboBox<>();
        lblReferenceNo = new javax.swing.JLabel();
        txtReferenceNo = new javax.swing.JTextField();
        lblRemarks = new javax.swing.JLabel();
        txtRemarks = new javax.swing.JTextField();
        btnRecordPayment = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        pnlHistory = new javax.swing.JPanel();
        lblHistoryTitle = new javax.swing.JLabel();
        chkFilterByInvoice = new javax.swing.JCheckBox();
        jScrollPanePayments = new javax.swing.JScrollPane();
        tblPayments = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Payment & Collection");
        setPreferredSize(new java.awt.Dimension(1040, 660));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlHeader.setBackground(new java.awt.Color(10, 114, 162));
        pnlHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Payment & Collection");
        pnlHeader.add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 10, 380, 28));

        btnRefresh.setBackground(new java.awt.Color(255, 255, 255));
        btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnRefresh.setForeground(new java.awt.Color(10, 114, 162));
        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        pnlHeader.add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 10, 116, 30));

        getContentPane().add(pnlHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1040, 50));

        pnlEntry.setBackground(new java.awt.Color(255, 255, 255));
        pnlEntry.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlEntry.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblUnpaidInvoice.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblUnpaidInvoice.setText("Unpaid / Partially Paid Invoice *");
        pnlEntry.add(lblUnpaidInvoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 12, 332, 18));

        cboUnpaidInvoice.setFont(new java.awt.Font("Segoe UI", 0, 12));
        cboUnpaidInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboUnpaidInvoiceActionPerformed(evt);
            }
        });
        pnlEntry.add(cboUnpaidInvoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 32, 332, 28));

        lblCustomer.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblCustomer.setText("Customer");
        pnlEntry.add(lblCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 68, 332, 18));

        txtCustomer.setEditable(false);
        txtCustomer.setBackground(new java.awt.Color(235, 239, 242));
        txtCustomer.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlEntry.add(txtCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 88, 332, 28));

        lblInvoiceTotal.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblInvoiceTotal.setText("Invoice Total");
        pnlEntry.add(lblInvoiceTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 124, 104, 18));

        txtInvoiceTotal.setEditable(false);
        txtInvoiceTotal.setBackground(new java.awt.Color(235, 239, 242));
        txtInvoiceTotal.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlEntry.add(txtInvoiceTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 144, 104, 28));

        lblPaidSoFar.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblPaidSoFar.setText("Paid So Far");
        pnlEntry.add(lblPaidSoFar, new org.netbeans.lib.awtextra.AbsoluteConstraints(128, 124, 104, 18));

        txtPaidSoFar.setEditable(false);
        txtPaidSoFar.setBackground(new java.awt.Color(235, 239, 242));
        txtPaidSoFar.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlEntry.add(txtPaidSoFar, new org.netbeans.lib.awtextra.AbsoluteConstraints(128, 144, 104, 28));

        lblBalance.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblBalance.setText("Balance Due");
        pnlEntry.add(lblBalance, new org.netbeans.lib.awtextra.AbsoluteConstraints(242, 124, 104, 18));

        txtBalance.setEditable(false);
        txtBalance.setBackground(new java.awt.Color(235, 239, 242));
        txtBalance.setFont(new java.awt.Font("Segoe UI", 1, 12));
        pnlEntry.add(txtBalance, new org.netbeans.lib.awtextra.AbsoluteConstraints(242, 144, 104, 28));

        lblCashier.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblCashier.setText("Cashier / Collector *");
        pnlEntry.add(lblCashier, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 180, 332, 18));

        cboCashier.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlEntry.add(cboCashier, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 200, 332, 28));

        lblAmountPaid.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblAmountPaid.setText("Amount Paid *");
        pnlEntry.add(lblAmountPaid, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 236, 160, 18));

        txtAmountPaid.setFont(new java.awt.Font("Segoe UI", 1, 12));
        pnlEntry.add(txtAmountPaid, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 256, 160, 28));

        lblPaymentMethod.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblPaymentMethod.setText("Payment Method *");
        pnlEntry.add(lblPaymentMethod, new org.netbeans.lib.awtextra.AbsoluteConstraints(186, 236, 160, 18));

        cboPaymentMethod.setFont(new java.awt.Font("Segoe UI", 0, 12));
        cboPaymentMethod.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CASH", "BANK_TRANSFER", "CHECK", "CARD" }));
        pnlEntry.add(cboPaymentMethod, new org.netbeans.lib.awtextra.AbsoluteConstraints(186, 256, 160, 28));

        lblReferenceNo.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblReferenceNo.setText("Reference No.");
        pnlEntry.add(lblReferenceNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 292, 332, 18));

        txtReferenceNo.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlEntry.add(txtReferenceNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 312, 332, 28));

        lblRemarks.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblRemarks.setText("Remarks");
        pnlEntry.add(lblRemarks, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 348, 332, 18));

        txtRemarks.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlEntry.add(txtRemarks, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 368, 332, 28));

        btnRecordPayment.setBackground(new java.awt.Color(46, 125, 50));
        btnRecordPayment.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnRecordPayment.setForeground(new java.awt.Color(255, 255, 255));
        btnRecordPayment.setText("Record Payment");
        btnRecordPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecordPaymentActionPerformed(evt);
            }
        });
        pnlEntry.add(btnRecordPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 418, 210, 38));

        btnClear.setBackground(new java.awt.Color(108, 117, 125));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        pnlEntry.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(236, 418, 110, 38));

        getContentPane().add(pnlEntry, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 60, 360, 540));

        pnlHistory.setBackground(new java.awt.Color(255, 255, 255));
        pnlHistory.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlHistory.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblHistoryTitle.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblHistoryTitle.setText("Payment History");
        pnlHistory.add(lblHistoryTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 220, 24));

        chkFilterByInvoice.setBackground(new java.awt.Color(255, 255, 255));
        chkFilterByInvoice.setFont(new java.awt.Font("Segoe UI", 0, 12));
        chkFilterByInvoice.setText("Filter by selected invoice");
        chkFilterByInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFilterByInvoiceActionPerformed(evt);
            }
        });
        pnlHistory.add(chkFilterByInvoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 14, 198, 24));

        tblPayments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Receipt No", "Invoice No", "Customer", "Cashier", "Date", "Method", "Ref No", "Amount", "Remarks"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPanePayments.setViewportView(tblPayments);

        pnlHistory.add(jScrollPanePayments, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 46, 594, 480));

        getContentPane().add(pnlHistory, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 60, 622, 540));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        refreshAll();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void cboUnpaidInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboUnpaidInvoiceActionPerformed
        populateSelectedInvoiceSummary();
        if (chkFilterByInvoice.isSelected()) {
            refreshPaymentsTable();
        }
    }//GEN-LAST:event_cboUnpaidInvoiceActionPerformed

    private void chkFilterByInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFilterByInvoiceActionPerformed
        refreshPaymentsTable();
    }//GEN-LAST:event_chkFilterByInvoiceActionPerformed

    private void btnRecordPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecordPaymentActionPerformed
        if (appContext == null) {
            return;
        }
        Invoice selectedInvoice = (Invoice) cboUnpaidInvoice.getSelectedItem();
        if (selectedInvoice == null) {
            JOptionPane.showMessageDialog(this, "No unpaid or partially paid invoice is selected.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Employee cashier = (Employee) cboCashier.getSelectedItem();
        if (cashier == null) {
            JOptionPane.showMessageDialog(this, "Please select a cashier.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BigDecimal amountPaid;
        try {
            amountPaid = new BigDecimal(txtAmountPaid.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount Paid must be a valid decimal number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Payment payment = new Payment();
            payment.setInvoiceId(selectedInvoice.getId());
            payment.setEmployeeId(cashier.getId());
            payment.setAmountPaid(amountPaid);
            Object method = cboPaymentMethod.getSelectedItem();
            payment.setPaymentMethod(method != null ? method.toString() : "CASH");
            payment.setReferenceNo(txtReferenceNo.getText());
            payment.setRemarks(txtRemarks.getText());

            Payment saved = appContext.getPaymentService().recordPayment(payment);
            refreshAll();
            clearEntryFields();
            JOptionPane.showMessageDialog(this,
                    "Payment receipt " + saved.getReceiptNo() + " recorded.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "System error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnRecordPaymentActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearEntryFields();
    }//GEN-LAST:event_btnClearActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnRecordPayment;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JComboBox<sales.model.Employee> cboCashier;
    private javax.swing.JComboBox<String> cboPaymentMethod;
    private javax.swing.JComboBox<sales.model.Invoice> cboUnpaidInvoice;
    private javax.swing.JCheckBox chkFilterByInvoice;
    private javax.swing.JScrollPane jScrollPanePayments;
    private javax.swing.JLabel lblAmountPaid;
    private javax.swing.JLabel lblBalance;
    private javax.swing.JLabel lblCashier;
    private javax.swing.JLabel lblCustomer;
    private javax.swing.JLabel lblHistoryTitle;
    private javax.swing.JLabel lblInvoiceTotal;
    private javax.swing.JLabel lblPaidSoFar;
    private javax.swing.JLabel lblPaymentMethod;
    private javax.swing.JLabel lblReferenceNo;
    private javax.swing.JLabel lblRemarks;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUnpaidInvoice;
    private javax.swing.JPanel pnlEntry;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlHistory;
    private javax.swing.JTable tblPayments;
    private javax.swing.JTextField txtAmountPaid;
    private javax.swing.JTextField txtBalance;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtInvoiceTotal;
    private javax.swing.JTextField txtPaidSoFar;
    private javax.swing.JTextField txtReferenceNo;
    private javax.swing.JTextField txtRemarks;
    // End of variables declaration//GEN-END:variables
}
