package sales.ui;

import java.awt.Color;
import javax.swing.JOptionPane;
import sales.AppContext;
import sales.model.DashboardSummary;
import sales.model.Employee;

public class frmMain extends javax.swing.JFrame {

    private final AppContext appContext;

    public frmMain() {
        this(null);
    }

    public frmMain(AppContext appContext) {
        this.appContext = appContext;
        initComponents();
        if (this.appContext != null) {
            initCustomLogic();
        }
    }

    private void initCustomLogic() {
        getContentPane().setBackground(new Color(244, 246, 248));
        setLocationRelativeTo(null);
        Employee current = appContext.getCurrentEmployee();
        if (current != null) {
            lblUserInfo.setText("Signed in: " + current.getEmployeeName() + " [" + current.getRole() + "]");
        }
        refreshKpis();
    }

    private void refreshKpis() {
        if (appContext == null) {
            return;
        }
        try {
            DashboardSummary summary = appContext.getReportService().getDashboardSummary();
            lblKpiCustomersValue.setText(String.valueOf(summary.getTotalCustomers()));
            lblKpiProductsValue.setText(String.valueOf(summary.getTotalProducts()));
            lblKpiOrdersValue.setText(summary.getOpenOrders() + " / " + summary.getConfirmedOrders());
            lblKpiInvoicesValue.setText(String.valueOf(summary.getUnpaidInvoices()));
            lblKpiCollectionsValue.setText(String.format("%,.2f", summary.getTotalCollections()));
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load dashboard KPIs: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblAppTitle = new javax.swing.JLabel();
        lblAppSubtitle = new javax.swing.JLabel();
        lblUserInfo = new javax.swing.JLabel();
        btnRefreshKpis = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        pnlKpiCustomers = new javax.swing.JPanel();
        lblKpiCustomersTitle = new javax.swing.JLabel();
        lblKpiCustomersValue = new javax.swing.JLabel();
        pnlKpiProducts = new javax.swing.JPanel();
        lblKpiProductsTitle = new javax.swing.JLabel();
        lblKpiProductsValue = new javax.swing.JLabel();
        pnlKpiOrders = new javax.swing.JPanel();
        lblKpiOrdersTitle = new javax.swing.JLabel();
        lblKpiOrdersValue = new javax.swing.JLabel();
        pnlKpiInvoices = new javax.swing.JPanel();
        lblKpiInvoicesTitle = new javax.swing.JLabel();
        lblKpiInvoicesValue = new javax.swing.JLabel();
        pnlKpiCollections = new javax.swing.JPanel();
        lblKpiCollectionsTitle = new javax.swing.JLabel();
        lblKpiCollectionsValue = new javax.swing.JLabel();
        pnlNavRegistries = new javax.swing.JPanel();
        lblNavRegistriesTitle = new javax.swing.JLabel();
        lblNavRegistriesDesc = new javax.swing.JLabel();
        btnOpenCustomer = new javax.swing.JButton();
        btnOpenProduct = new javax.swing.JButton();
        btnOpenEmployee = new javax.swing.JButton();
        pnlNavPipeline = new javax.swing.JPanel();
        lblNavPipelineTitle = new javax.swing.JLabel();
        lblNavPipelineDesc = new javax.swing.JLabel();
        btnOpenSalesOrder = new javax.swing.JButton();
        btnOpenInvoice = new javax.swing.JButton();
        btnOpenPayment = new javax.swing.JButton();
        pnlNavReports = new javax.swing.JPanel();
        lblNavReportsTitle = new javax.swing.JLabel();
        lblNavReportsDesc = new javax.swing.JLabel();
        btnOpenReports = new javax.swing.JButton();
        lblPipelineHint = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Integrated Sales Module System - Main Dashboard");
        setPreferredSize(new java.awt.Dimension(1020, 640));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlHeader.setBackground(new java.awt.Color(10, 114, 162));
        pnlHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblAppTitle.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblAppTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblAppTitle.setText("Integrated Sales Module System");
        pnlHeader.add(lblAppTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 440, 28));

        lblAppSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblAppSubtitle.setForeground(new java.awt.Color(220, 238, 248));
        lblAppSubtitle.setText("Executive Dashboard & Module Navigation");
        pnlHeader.add(lblAppSubtitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 400, 18));

        lblUserInfo.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblUserInfo.setForeground(new java.awt.Color(255, 255, 255));
        lblUserInfo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUserInfo.setText("User: -");
        pnlHeader.add(lblUserInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 22, 290, 24));

        btnRefreshKpis.setBackground(new java.awt.Color(255, 255, 255));
        btnRefreshKpis.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnRefreshKpis.setForeground(new java.awt.Color(10, 114, 162));
        btnRefreshKpis.setText("Refresh KPIs");
        btnRefreshKpis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshKpisActionPerformed(evt);
            }
        });
        pnlHeader.add(btnRefreshKpis, new org.netbeans.lib.awtextra.AbsoluteConstraints(774, 18, 116, 34));

        btnLogout.setBackground(new java.awt.Color(192, 57, 43));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        pnlHeader.add(btnLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 18, 90, 34));

        getContentPane().add(pnlHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1020, 70));

        pnlKpiCustomers.setBackground(new java.awt.Color(255, 255, 255));
        pnlKpiCustomers.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlKpiCustomers.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblKpiCustomersTitle.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblKpiCustomersTitle.setForeground(new java.awt.Color(108, 117, 125));
        lblKpiCustomersTitle.setText("Total Customers");
        pnlKpiCustomers.add(lblKpiCustomersTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 157, 20));

        lblKpiCustomersValue.setFont(new java.awt.Font("Segoe UI", 1, 24));
        lblKpiCustomersValue.setForeground(new java.awt.Color(10, 114, 162));
        lblKpiCustomersValue.setText("0");
        pnlKpiCustomers.add(lblKpiCustomersValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 42, 157, 36));

        getContentPane().add(pnlKpiCustomers, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 86, 185, 92));

        pnlKpiProducts.setBackground(new java.awt.Color(255, 255, 255));
        pnlKpiProducts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlKpiProducts.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblKpiProductsTitle.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblKpiProductsTitle.setForeground(new java.awt.Color(108, 117, 125));
        lblKpiProductsTitle.setText("Total Products");
        pnlKpiProducts.add(lblKpiProductsTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 157, 20));

        lblKpiProductsValue.setFont(new java.awt.Font("Segoe UI", 1, 24));
        lblKpiProductsValue.setForeground(new java.awt.Color(10, 114, 162));
        lblKpiProductsValue.setText("0");
        pnlKpiProducts.add(lblKpiProductsValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 42, 157, 36));

        getContentPane().add(pnlKpiProducts, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 86, 185, 92));

        pnlKpiOrders.setBackground(new java.awt.Color(255, 255, 255));
        pnlKpiOrders.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlKpiOrders.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblKpiOrdersTitle.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblKpiOrdersTitle.setForeground(new java.awt.Color(108, 117, 125));
        lblKpiOrdersTitle.setText("Open / Confirmed Orders");
        pnlKpiOrders.add(lblKpiOrdersTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 157, 20));

        lblKpiOrdersValue.setFont(new java.awt.Font("Segoe UI", 1, 22));
        lblKpiOrdersValue.setForeground(new java.awt.Color(10, 114, 162));
        lblKpiOrdersValue.setText("0 / 0");
        pnlKpiOrders.add(lblKpiOrdersValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 42, 157, 36));

        getContentPane().add(pnlKpiOrders, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 86, 185, 92));

        pnlKpiInvoices.setBackground(new java.awt.Color(255, 255, 255));
        pnlKpiInvoices.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlKpiInvoices.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblKpiInvoicesTitle.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblKpiInvoicesTitle.setForeground(new java.awt.Color(108, 117, 125));
        lblKpiInvoicesTitle.setText("Unpaid Invoices");
        pnlKpiInvoices.add(lblKpiInvoicesTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 157, 20));

        lblKpiInvoicesValue.setFont(new java.awt.Font("Segoe UI", 1, 24));
        lblKpiInvoicesValue.setForeground(new java.awt.Color(211, 108, 0));
        lblKpiInvoicesValue.setText("0");
        pnlKpiInvoices.add(lblKpiInvoicesValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 42, 157, 36));

        getContentPane().add(pnlKpiInvoices, new org.netbeans.lib.awtextra.AbsoluteConstraints(605, 86, 185, 92));

        pnlKpiCollections.setBackground(new java.awt.Color(255, 255, 255));
        pnlKpiCollections.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlKpiCollections.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblKpiCollectionsTitle.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblKpiCollectionsTitle.setForeground(new java.awt.Color(108, 117, 125));
        lblKpiCollectionsTitle.setText("Total Collections");
        pnlKpiCollections.add(lblKpiCollectionsTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 157, 20));

        lblKpiCollectionsValue.setFont(new java.awt.Font("Segoe UI", 1, 20));
        lblKpiCollectionsValue.setForeground(new java.awt.Color(46, 125, 50));
        lblKpiCollectionsValue.setText("0.00");
        pnlKpiCollections.add(lblKpiCollectionsValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 42, 157, 36));

        getContentPane().add(pnlKpiCollections, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 86, 185, 92));

        pnlNavRegistries.setBackground(new java.awt.Color(255, 255, 255));
        pnlNavRegistries.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlNavRegistries.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblNavRegistriesTitle.setFont(new java.awt.Font("Segoe UI", 1, 15));
        lblNavRegistriesTitle.setForeground(new java.awt.Color(10, 114, 162));
        lblNavRegistriesTitle.setText("Master Registries");
        pnlNavRegistries.add(lblNavRegistriesTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 16, 270, 24));

        lblNavRegistriesDesc.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblNavRegistriesDesc.setForeground(new java.awt.Color(108, 117, 125));
        lblNavRegistriesDesc.setText("Manage customers, products & employees");
        pnlNavRegistries.add(lblNavRegistriesDesc, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 42, 270, 18));

        btnOpenCustomer.setBackground(new java.awt.Color(10, 114, 162));
        btnOpenCustomer.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnOpenCustomer.setForeground(new java.awt.Color(255, 255, 255));
        btnOpenCustomer.setText("Customer Registry");
        btnOpenCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenCustomerActionPerformed(evt);
            }
        });
        pnlNavRegistries.add(btnOpenCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 82, 270, 48));

        btnOpenProduct.setBackground(new java.awt.Color(10, 114, 162));
        btnOpenProduct.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnOpenProduct.setForeground(new java.awt.Color(255, 255, 255));
        btnOpenProduct.setText("Product Registry");
        btnOpenProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenProductActionPerformed(evt);
            }
        });
        pnlNavRegistries.add(btnOpenProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 148, 270, 48));

        btnOpenEmployee.setBackground(new java.awt.Color(10, 114, 162));
        btnOpenEmployee.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnOpenEmployee.setForeground(new java.awt.Color(255, 255, 255));
        btnOpenEmployee.setText("Employee Registry");
        btnOpenEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenEmployeeActionPerformed(evt);
            }
        });
        pnlNavRegistries.add(btnOpenEmployee, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 214, 270, 48));

        getContentPane().add(pnlNavRegistries, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 196, 310, 370));

        pnlNavPipeline.setBackground(new java.awt.Color(255, 255, 255));
        pnlNavPipeline.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlNavPipeline.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblNavPipelineTitle.setFont(new java.awt.Font("Segoe UI", 1, 15));
        lblNavPipelineTitle.setForeground(new java.awt.Color(10, 114, 162));
        lblNavPipelineTitle.setText("Sales Pipeline");
        pnlNavPipeline.add(lblNavPipelineTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 16, 270, 24));

        lblNavPipelineDesc.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblNavPipelineDesc.setForeground(new java.awt.Color(108, 117, 125));
        lblNavPipelineDesc.setText("Order -> Invoicing -> Collection");
        pnlNavPipeline.add(lblNavPipelineDesc, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 42, 270, 18));

        btnOpenSalesOrder.setBackground(new java.awt.Color(10, 114, 162));
        btnOpenSalesOrder.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnOpenSalesOrder.setForeground(new java.awt.Color(255, 255, 255));
        btnOpenSalesOrder.setText("1. Sales Orders");
        btnOpenSalesOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenSalesOrderActionPerformed(evt);
            }
        });
        pnlNavPipeline.add(btnOpenSalesOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 82, 270, 48));

        btnOpenInvoice.setBackground(new java.awt.Color(10, 114, 162));
        btnOpenInvoice.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnOpenInvoice.setForeground(new java.awt.Color(255, 255, 255));
        btnOpenInvoice.setText("2. Invoicing");
        btnOpenInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenInvoiceActionPerformed(evt);
            }
        });
        pnlNavPipeline.add(btnOpenInvoice, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 148, 270, 48));

        btnOpenPayment.setBackground(new java.awt.Color(10, 114, 162));
        btnOpenPayment.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnOpenPayment.setForeground(new java.awt.Color(255, 255, 255));
        btnOpenPayment.setText("3. Payment / Collection");
        btnOpenPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenPaymentActionPerformed(evt);
            }
        });
        pnlNavPipeline.add(btnOpenPayment, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 214, 270, 48));

        getContentPane().add(pnlNavPipeline, new org.netbeans.lib.awtextra.AbsoluteConstraints(348, 196, 310, 370));

        pnlNavReports.setBackground(new java.awt.Color(255, 255, 255));
        pnlNavReports.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlNavReports.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblNavReportsTitle.setFont(new java.awt.Font("Segoe UI", 1, 15));
        lblNavReportsTitle.setForeground(new java.awt.Color(10, 114, 162));
        lblNavReportsTitle.setText("Reports & Analytics");
        pnlNavReports.add(lblNavReportsTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 16, 270, 24));

        lblNavReportsDesc.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblNavReportsDesc.setForeground(new java.awt.Color(108, 117, 125));
        lblNavReportsDesc.setText("Print & CSV export across modules");
        pnlNavReports.add(lblNavReportsDesc, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 42, 270, 18));

        btnOpenReports.setBackground(new java.awt.Color(46, 125, 50));
        btnOpenReports.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnOpenReports.setForeground(new java.awt.Color(255, 255, 255));
        btnOpenReports.setText("Open Sales Reports");
        btnOpenReports.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenReportsActionPerformed(evt);
            }
        });
        pnlNavReports.add(btnOpenReports, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 82, 270, 54));

        lblPipelineHint.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblPipelineHint.setForeground(new java.awt.Color(64, 72, 80));
        lblPipelineHint.setText("<html><b>End-to-End Flow:</b><br><br>1. Create & Confirm Sales Order<br>2. Generate Invoice (atomic stock deduction)<br>3. Record Payment Collections<br>4. Generate, Print & Export Reports</html>");
        lblPipelineHint.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        pnlNavReports.add(lblPipelineHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 156, 270, 170));

        getContentPane().add(pnlNavReports, new org.netbeans.lib.awtextra.AbsoluteConstraints(675, 196, 310, 370));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        refreshKpis();
    }//GEN-LAST:event_formWindowActivated

    private void btnRefreshKpisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshKpisActionPerformed
        refreshKpis();
    }//GEN-LAST:event_btnRefreshKpisActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        if (appContext != null) {
            appContext.clearSession();
            new frmLogin(appContext).setVisible(true);
        }
        dispose();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnOpenCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenCustomerActionPerformed
        new frmCustomer(appContext).setVisible(true);
    }//GEN-LAST:event_btnOpenCustomerActionPerformed

    private void btnOpenProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenProductActionPerformed
        new frmProduct(appContext).setVisible(true);
    }//GEN-LAST:event_btnOpenProductActionPerformed

    private void btnOpenEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenEmployeeActionPerformed
        new frmEmployee(appContext).setVisible(true);
    }//GEN-LAST:event_btnOpenEmployeeActionPerformed

    private void btnOpenSalesOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenSalesOrderActionPerformed
        new frmSalesOrder(appContext).setVisible(true);
    }//GEN-LAST:event_btnOpenSalesOrderActionPerformed

    private void btnOpenInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenInvoiceActionPerformed
        new frmInvoice(appContext).setVisible(true);
    }//GEN-LAST:event_btnOpenInvoiceActionPerformed

    private void btnOpenPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenPaymentActionPerformed
        new frmPayment(appContext).setVisible(true);
    }//GEN-LAST:event_btnOpenPaymentActionPerformed

    private void btnOpenReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenReportsActionPerformed
        new frmReports(appContext).setVisible(true);
    }//GEN-LAST:event_btnOpenReportsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnOpenCustomer;
    private javax.swing.JButton btnOpenEmployee;
    private javax.swing.JButton btnOpenInvoice;
    private javax.swing.JButton btnOpenPayment;
    private javax.swing.JButton btnOpenProduct;
    private javax.swing.JButton btnOpenReports;
    private javax.swing.JButton btnOpenSalesOrder;
    private javax.swing.JButton btnRefreshKpis;
    private javax.swing.JLabel lblAppSubtitle;
    private javax.swing.JLabel lblAppTitle;
    private javax.swing.JLabel lblKpiCollectionsTitle;
    private javax.swing.JLabel lblKpiCollectionsValue;
    private javax.swing.JLabel lblKpiCustomersTitle;
    private javax.swing.JLabel lblKpiCustomersValue;
    private javax.swing.JLabel lblKpiInvoicesTitle;
    private javax.swing.JLabel lblKpiInvoicesValue;
    private javax.swing.JLabel lblKpiOrdersTitle;
    private javax.swing.JLabel lblKpiOrdersValue;
    private javax.swing.JLabel lblKpiProductsTitle;
    private javax.swing.JLabel lblKpiProductsValue;
    private javax.swing.JLabel lblNavPipelineDesc;
    private javax.swing.JLabel lblNavPipelineTitle;
    private javax.swing.JLabel lblNavRegistriesDesc;
    private javax.swing.JLabel lblNavRegistriesTitle;
    private javax.swing.JLabel lblNavReportsDesc;
    private javax.swing.JLabel lblNavReportsTitle;
    private javax.swing.JLabel lblPipelineHint;
    private javax.swing.JLabel lblUserInfo;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlKpiCollections;
    private javax.swing.JPanel pnlKpiCustomers;
    private javax.swing.JPanel pnlKpiInvoices;
    private javax.swing.JPanel pnlKpiOrders;
    private javax.swing.JPanel pnlKpiProducts;
    private javax.swing.JPanel pnlNavPipeline;
    private javax.swing.JPanel pnlNavRegistries;
    private javax.swing.JPanel pnlNavReports;
    // End of variables declaration//GEN-END:variables
}
