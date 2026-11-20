package sales.ui;

import java.awt.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import sales.AppContext;
import sales.model.Employee;
import sales.model.Invoice;
import sales.model.InvoiceLine;
import sales.model.SalesOrder;
import sales.model.SalesOrderLine;

public class frmInvoice extends javax.swing.JFrame {

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AppContext appContext;

    public frmInvoice() {
        this(null);
    }

    public frmInvoice(AppContext appContext) {
        this.appContext = appContext;
        initComponents();
        if (this.appContext != null) {
            initCustomLogic();
        }
    }

    private void initCustomLogic() {
        getContentPane().setBackground(new Color(244, 246, 248));
        setLocationRelativeTo(null);
        txtDueDate.setText(LocalDate.now().plusDays(30).toString());
        refreshAll();
    }

    private void refreshAll() {
        loadConfirmedOrders();
        refreshInvoicesTable();
    }

    private void loadConfirmedOrders() {
        if (appContext == null) {
            return;
        }
        try {
            DefaultComboBoxModel<SalesOrder> model = new DefaultComboBoxModel<>();
            for (SalesOrder order : appContext.getSalesOrderService().getConfirmedOrders()) {
                model.addElement(order);
            }
            cboConfirmedOrder.setModel(model);
            previewSelectedOrder();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load confirmed orders: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void previewSelectedOrder() {
        SalesOrder selected = (SalesOrder) cboConfirmedOrder.getSelectedItem();
        if (selected == null) {
            txtCustomer.setText("");
            txtSalesRep.setText("");
            txtOrderTotal.setText("");
            lblLinesTitle.setText("Invoice / Order Line Items");
            ((DefaultTableModel) tblInvoiceLines.getModel()).setRowCount(0);
            return;
        }
        txtCustomer.setText(selected.getCustomerName());
        txtSalesRep.setText(selected.getEmployeeName());
        txtOrderTotal.setText(selected.getTotalAmount().toPlainString());
        lblLinesTitle.setText("Previewing Confirmed Order Lines: " + selected.getOrderNo());
        try {
            SalesOrder full = appContext.getSalesOrderService().getByIdWithLines(selected.getId());
            DefaultTableModel model = (DefaultTableModel) tblInvoiceLines.getModel();
            model.setRowCount(0);
            if (full != null) {
                for (SalesOrderLine line : full.getLines()) {
                    model.addRow(new Object[]{
                        line.getProductCode(),
                        line.getProductName(),
                        line.getQuantity(),
                        line.getUnitPrice().toPlainString(),
                        line.getLineTotal().toPlainString()
                    });
                }
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to preview order items: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshInvoicesTable() {
        if (appContext == null) {
            return;
        }
        try {
            List<Invoice> invoices = appContext.getInvoiceService().getAll();
            DefaultTableModel model = (DefaultTableModel) tblInvoices.getModel();
            model.setRowCount(0);
            for (Invoice inv : invoices) {
                model.addRow(new Object[]{
                    inv.getId(),
                    inv.getInvoiceNo(),
                    inv.getOrderNo(),
                    inv.getCustomerName(),
                    inv.getEmployeeName(),
                    inv.getInvoiceDate() != null ? inv.getInvoiceDate().format(DATE_TIME_FMT) : "",
                    inv.getDueDate() != null ? inv.getDueDate().toString() : "",
                    inv.getTotalAmount().toPlainString(),
                    inv.getAmountPaid().toPlainString(),
                    inv.getBalance().toPlainString(),
                    inv.getStatus()
                });
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load invoices: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadInvoiceLines(int invoiceId) {
        if (appContext == null) {
            return;
        }
        try {
            Invoice inv = appContext.getInvoiceService().getByIdWithLines(invoiceId);
            if (inv == null) {
                return;
            }
            lblLinesTitle.setText("Invoice Line Items: " + inv.getInvoiceNo() + " (" + inv.getCustomerName() + ")");
            DefaultTableModel model = (DefaultTableModel) tblInvoiceLines.getModel();
            model.setRowCount(0);
            for (InvoiceLine line : inv.getLines()) {
                model.addRow(new Object[]{
                    line.getProductCode(),
                    line.getProductName(),
                    line.getQuantity(),
                    line.getUnitPrice().toPlainString(),
                    line.getLineTotal().toPlainString()
                });
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load invoice line items: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        pnlGenerate = new javax.swing.JPanel();
        lblConfirmedOrder = new javax.swing.JLabel();
        cboConfirmedOrder = new javax.swing.JComboBox<>();
        lblDueDate = new javax.swing.JLabel();
        txtDueDate = new javax.swing.JTextField();
        btnGenerateInvoice = new javax.swing.JButton();
        lblCustomer = new javax.swing.JLabel();
        txtCustomer = new javax.swing.JTextField();
        lblSalesRep = new javax.swing.JLabel();
        txtSalesRep = new javax.swing.JTextField();
        lblOrderTotal = new javax.swing.JLabel();
        txtOrderTotal = new javax.swing.JTextField();
        pnlInvoices = new javax.swing.JPanel();
        lblInvoicesTitle = new javax.swing.JLabel();
        jScrollPaneInvoices = new javax.swing.JScrollPane();
        tblInvoices = new javax.swing.JTable();
        pnlInvoiceLines = new javax.swing.JPanel();
        lblLinesTitle = new javax.swing.JLabel();
        jScrollPaneLines = new javax.swing.JScrollPane();
        tblInvoiceLines = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sales Invoicing");
        setPreferredSize(new java.awt.Dimension(1040, 670));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlHeader.setBackground(new java.awt.Color(10, 114, 162));
        pnlHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Sales Invoicing");
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

        pnlGenerate.setBackground(new java.awt.Color(255, 255, 255));
        pnlGenerate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlGenerate.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblConfirmedOrder.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblConfirmedOrder.setText("Confirmed Sales Order *");
        pnlGenerate.add(lblConfirmedOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 10, 250, 18));

        cboConfirmedOrder.setFont(new java.awt.Font("Segoe UI", 0, 12));
        cboConfirmedOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboConfirmedOrderActionPerformed(evt);
            }
        });
        pnlGenerate.add(cboConfirmedOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 30, 470, 28));

        lblDueDate.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblDueDate.setText("Due Date (yyyy-MM-dd) *");
        pnlGenerate.add(lblDueDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(498, 10, 180, 18));

        txtDueDate.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlGenerate.add(txtDueDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(498, 30, 170, 28));

        btnGenerateInvoice.setBackground(new java.awt.Color(46, 125, 50));
        btnGenerateInvoice.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnGenerateInvoice.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerateInvoice.setText("Generate Invoice (Deduct Stock)");
        btnGenerateInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateInvoiceActionPerformed(evt);
            }
        });
        pnlGenerate.add(btnGenerateInvoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(688, 26, 294, 34));

        lblCustomer.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblCustomer.setText("Customer");
        pnlGenerate.add(lblCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 68, 160, 18));

        txtCustomer.setEditable(false);
        txtCustomer.setBackground(new java.awt.Color(235, 239, 242));
        txtCustomer.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlGenerate.add(txtCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 88, 360, 28));

        lblSalesRep.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblSalesRep.setText("Order Sales Rep");
        pnlGenerate.add(lblSalesRep, new org.netbeans.lib.awtextra.AbsoluteConstraints(386, 68, 160, 18));

        txtSalesRep.setEditable(false);
        txtSalesRep.setBackground(new java.awt.Color(235, 239, 242));
        txtSalesRep.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlGenerate.add(txtSalesRep, new org.netbeans.lib.awtextra.AbsoluteConstraints(386, 88, 282, 28));

        lblOrderTotal.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblOrderTotal.setText("Order Total Amount");
        pnlGenerate.add(lblOrderTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(688, 68, 160, 18));

        txtOrderTotal.setEditable(false);
        txtOrderTotal.setBackground(new java.awt.Color(235, 239, 242));
        txtOrderTotal.setFont(new java.awt.Font("Segoe UI", 1, 12));
        pnlGenerate.add(txtOrderTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(688, 88, 294, 28));

        getContentPane().add(pnlGenerate, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 58, 994, 132));

        pnlInvoices.setBackground(new java.awt.Color(255, 255, 255));
        pnlInvoices.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlInvoices.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblInvoicesTitle.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblInvoicesTitle.setText("Generated Invoices (Click a row to inspect line items)");
        pnlInvoices.add(lblInvoicesTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 8, 460, 20));

        tblInvoices.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Invoice No", "Order No", "Customer", "Issued By", "Invoice Date", "Due Date", "Total", "Paid", "Balance", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInvoices.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblInvoicesMouseClicked(evt);
            }
        });
        jScrollPaneInvoices.setViewportView(tblInvoices);

        pnlInvoices.add(jScrollPaneInvoices, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 32, 970, 170));

        getContentPane().add(pnlInvoices, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 198, 994, 214));

        pnlInvoiceLines.setBackground(new java.awt.Color(255, 255, 255));
        pnlInvoiceLines.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlInvoiceLines.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblLinesTitle.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblLinesTitle.setText("Invoice / Order Line Items");
        pnlInvoiceLines.add(lblLinesTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 8, 550, 20));

        tblInvoiceLines.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product Code", "Product Name", "Quantity", "Unit Price", "Line Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneLines.setViewportView(tblInvoiceLines);

        pnlInvoiceLines.add(jScrollPaneLines, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 32, 970, 146));

        getContentPane().add(pnlInvoiceLines, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 420, 994, 190));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        refreshAll();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void cboConfirmedOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboConfirmedOrderActionPerformed
        previewSelectedOrder();
    }//GEN-LAST:event_cboConfirmedOrderActionPerformed

    private void btnGenerateInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateInvoiceActionPerformed
        if (appContext == null) {
            return;
        }
        SalesOrder selectedOrder = (SalesOrder) cboConfirmedOrder.getSelectedItem();
        if (selectedOrder == null) {
            JOptionPane.showMessageDialog(this, "No CONFIRMED sales order is selected.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(txtDueDate.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Due Date must be formatted as yyyy-MM-dd.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Employee current = appContext.getCurrentEmployee();
        if (current == null) {
            JOptionPane.showMessageDialog(this, "No active logged-in employee session.", "Session Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Invoice created = appContext.getInvoiceService().generateInvoice(selectedOrder.getId(), current.getId(), dueDate);
            refreshAll();
            loadInvoiceLines(created.getId());
            JOptionPane.showMessageDialog(this,
                    "Invoice " + created.getInvoiceNo() + " generated and stock deducted.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invoicing Blocked", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "System error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGenerateInvoiceActionPerformed

    private void tblInvoicesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblInvoicesMouseClicked
        int row = tblInvoices.getSelectedRow();
        if (row >= 0) {
            int invoiceId = Integer.parseInt(String.valueOf(tblInvoices.getValueAt(row, 0)));
            loadInvoiceLines(invoiceId);
        }
    }//GEN-LAST:event_tblInvoicesMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerateInvoice;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JComboBox<sales.model.SalesOrder> cboConfirmedOrder;
    private javax.swing.JScrollPane jScrollPaneInvoices;
    private javax.swing.JScrollPane jScrollPaneLines;
    private javax.swing.JLabel lblConfirmedOrder;
    private javax.swing.JLabel lblCustomer;
    private javax.swing.JLabel lblDueDate;
    private javax.swing.JLabel lblInvoicesTitle;
    private javax.swing.JLabel lblLinesTitle;
    private javax.swing.JLabel lblOrderTotal;
    private javax.swing.JLabel lblSalesRep;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlGenerate;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlInvoiceLines;
    private javax.swing.JPanel pnlInvoices;
    private javax.swing.JTable tblInvoiceLines;
    private javax.swing.JTable tblInvoices;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtDueDate;
    private javax.swing.JTextField txtOrderTotal;
    private javax.swing.JTextField txtSalesRep;
    // End of variables declaration//GEN-END:variables
}
