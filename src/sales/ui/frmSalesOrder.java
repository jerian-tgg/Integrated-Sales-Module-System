package sales.ui;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import sales.AppContext;
import sales.model.Customer;
import sales.model.Employee;
import sales.model.Product;
import sales.model.SalesOrder;
import sales.model.SalesOrderLine;

public class frmSalesOrder extends javax.swing.JFrame {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AppContext appContext;
    private int currentOrderId = 0;
    private final List<SalesOrderLine> stagedLines = new ArrayList<>();

    public frmSalesOrder() {
        this(null);
    }

    public frmSalesOrder(AppContext appContext) {
        this.appContext = appContext;
        initComponents();
        if (this.appContext != null) {
            initCustomLogic();
        }
    }

    private void initCustomLogic() {
        getContentPane().setBackground(new Color(244, 246, 248));
        setLocationRelativeTo(null);
        loadComboLookups();
        refreshOrdersTable();
        resetNewOrder();
    }

    private void loadComboLookups() {
        if (appContext == null) {
            return;
        }
        try {
            DefaultComboBoxModel<Customer> custModel = new DefaultComboBoxModel<>();
            for (Customer c : appContext.getCustomerService().getAll()) {
                custModel.addElement(c);
            }
            cboCustomer.setModel(custModel);

            DefaultComboBoxModel<Employee> empModel = new DefaultComboBoxModel<>();
            for (Employee e : appContext.getEmployeeService().getAll()) {
                empModel.addElement(e);
            }
            cboEmployee.setModel(empModel);
            if (appContext.getCurrentEmployee() != null) {
                cboEmployee.setSelectedItem(appContext.getCurrentEmployee());
            }

            DefaultComboBoxModel<Product> prodModel = new DefaultComboBoxModel<>();
            for (Product p : appContext.getProductService().getAll()) {
                prodModel.addElement(p);
            }
            cboProduct.setModel(prodModel);
            updateSelectedProductInfo();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load registry lookups: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelectedProductInfo() {
        Product selected = (Product) cboProduct.getSelectedItem();
        if (selected == null) {
            txtUnitPrice.setText("0.00");
            lblAvailableStock.setText("Stock: 0");
            return;
        }
        txtUnitPrice.setText(selected.getUnitPrice().toPlainString());
        lblAvailableStock.setText("Stock: " + selected.getStockQty());
    }

    private void refreshOrdersTable() {
        if (appContext == null) {
            return;
        }
        try {
            List<SalesOrder> orders = appContext.getSalesOrderService().getAll();
            DefaultTableModel model = (DefaultTableModel) tblOrders.getModel();
            model.setRowCount(0);
            for (SalesOrder o : orders) {
                model.addRow(new Object[]{
                    o.getId(),
                    o.getOrderNo(),
                    o.getOrderDate() != null ? o.getOrderDate().format(DATE_FMT) : "",
                    o.getCustomerName(),
                    o.getEmployeeName(),
                    o.getStatus(),
                    o.getTotalAmount().toPlainString()
                });
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load sales orders: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshLinesTable() {
        DefaultTableModel model = (DefaultTableModel) tblOrderLines.getModel();
        model.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (SalesOrderLine line : stagedLines) {
            BigDecimal lineTotal = line.getUnitPrice()
                    .multiply(BigDecimal.valueOf(line.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            line.setLineTotal(lineTotal);
            total = total.add(lineTotal);
            model.addRow(new Object[]{
                line.getProductId(),
                line.getProductCode(),
                line.getProductName(),
                line.getQuantity(),
                line.getUnitPrice().toPlainString(),
                lineTotal.toPlainString()
            });
        }
        lblTotalAmount.setText(String.format("%,.2f", total));
    }

    private void resetNewOrder() {
        currentOrderId = 0;
        txtOrderNo.setText("(NEW)");
        txtStatus.setText("OPEN");
        stagedLines.clear();
        refreshLinesTable();
        txtQuantity.setText("1");
        if (cboCustomer.getItemCount() > 0) {
            cboCustomer.setSelectedIndex(0);
        }
        if (appContext != null && appContext.getCurrentEmployee() != null) {
            cboEmployee.setSelectedItem(appContext.getCurrentEmployee());
        }
        tblOrders.clearSelection();
        updateEditableState("OPEN", false);
    }

    private void updateEditableState(String status, boolean existingOrder) {
        boolean isOpen = "OPEN".equals(status);
        cboCustomer.setEnabled(isOpen);
        cboEmployee.setEnabled(isOpen);
        cboProduct.setEnabled(isOpen);
        txtQuantity.setEnabled(isOpen);
        btnAddItem.setEnabled(isOpen);
        btnRemoveItem.setEnabled(isOpen);
        btnSaveOrder.setEnabled(isOpen);
        btnConfirmOrder.setEnabled(existingOrder && isOpen);
        btnCancelOrder.setEnabled(existingOrder && isOpen);
        btnDeleteOrder.setEnabled(existingOrder && isOpen);
    }

    private void loadOrderIntoEditor(int orderId) {
        if (appContext == null) {
            return;
        }
        try {
            SalesOrder order = appContext.getSalesOrderService().getByIdWithLines(orderId);
            if (order == null) {
                return;
            }
            currentOrderId = order.getId();
            txtOrderNo.setText(order.getOrderNo());
            txtStatus.setText(order.getStatus());

            Customer cMatch = new Customer();
            cMatch.setId(order.getCustomerId());
            cboCustomer.setSelectedItem(cMatch);

            Employee eMatch = new Employee();
            eMatch.setId(order.getEmployeeId());
            cboEmployee.setSelectedItem(eMatch);

            stagedLines.clear();
            stagedLines.addAll(order.getLines());
            refreshLinesTable();
            updateEditableState(order.getStatus(), true);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load sales order: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        btnRefreshData = new javax.swing.JButton();
        pnlOrderHeader = new javax.swing.JPanel();
        lblOrderNo = new javax.swing.JLabel();
        txtOrderNo = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        lblCustomer = new javax.swing.JLabel();
        cboCustomer = new javax.swing.JComboBox<>();
        lblEmployee = new javax.swing.JLabel();
        cboEmployee = new javax.swing.JComboBox<>();
        pnlLineEntry = new javax.swing.JPanel();
        lblProduct = new javax.swing.JLabel();
        cboProduct = new javax.swing.JComboBox<>();
        lblUnitPrice = new javax.swing.JLabel();
        txtUnitPrice = new javax.swing.JTextField();
        lblAvailableStock = new javax.swing.JLabel();
        lblQuantity = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        btnAddItem = new javax.swing.JButton();
        btnRemoveItem = new javax.swing.JButton();
        jScrollPaneLines = new javax.swing.JScrollPane();
        tblOrderLines = new javax.swing.JTable();
        lblTotalLabel = new javax.swing.JLabel();
        lblTotalAmount = new javax.swing.JLabel();
        btnNewOrder = new javax.swing.JButton();
        btnSaveOrder = new javax.swing.JButton();
        btnConfirmOrder = new javax.swing.JButton();
        btnCancelOrder = new javax.swing.JButton();
        btnDeleteOrder = new javax.swing.JButton();
        pnlExistingOrders = new javax.swing.JPanel();
        lblExistingOrders = new javax.swing.JLabel();
        jScrollPaneOrders = new javax.swing.JScrollPane();
        tblOrders = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sales Order Management");
        setPreferredSize(new java.awt.Dimension(1040, 670));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlHeader.setBackground(new java.awt.Color(10, 114, 162));
        pnlHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Sales Order Management");
        pnlHeader.add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 10, 380, 28));

        btnRefreshData.setBackground(new java.awt.Color(255, 255, 255));
        btnRefreshData.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnRefreshData.setForeground(new java.awt.Color(10, 114, 162));
        btnRefreshData.setText("Refresh Lists");
        btnRefreshData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshDataActionPerformed(evt);
            }
        });
        pnlHeader.add(btnRefreshData, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 10, 116, 30));

        getContentPane().add(pnlHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1040, 50));

        pnlOrderHeader.setBackground(new java.awt.Color(255, 255, 255));
        pnlOrderHeader.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlOrderHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblOrderNo.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblOrderNo.setText("Order No");
        pnlOrderHeader.add(lblOrderNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 8, 120, 18));

        txtOrderNo.setEditable(false);
        txtOrderNo.setBackground(new java.awt.Color(235, 239, 242));
        txtOrderNo.setFont(new java.awt.Font("Segoe UI", 1, 12));
        txtOrderNo.setText("(NEW)");
        pnlOrderHeader.add(txtOrderNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 28, 120, 28));

        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblStatus.setText("Status");
        pnlOrderHeader.add(lblStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(144, 8, 110, 18));

        txtStatus.setEditable(false);
        txtStatus.setBackground(new java.awt.Color(235, 239, 242));
        txtStatus.setFont(new java.awt.Font("Segoe UI", 1, 12));
        txtStatus.setText("OPEN");
        pnlOrderHeader.add(txtStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(144, 28, 110, 28));

        lblCustomer.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblCustomer.setText("Customer (from Registry) *");
        pnlOrderHeader.add(lblCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(266, 8, 240, 18));

        cboCustomer.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlOrderHeader.add(cboCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(266, 28, 360, 28));

        lblEmployee.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblEmployee.setText("Sales Representative *");
        pnlOrderHeader.add(lblEmployee, new org.netbeans.lib.awtextra.AbsoluteConstraints(638, 8, 240, 18));

        cboEmployee.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlOrderHeader.add(cboEmployee, new org.netbeans.lib.awtextra.AbsoluteConstraints(638, 28, 342, 28));

        getContentPane().add(pnlOrderHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 58, 994, 76));

        pnlLineEntry.setBackground(new java.awt.Color(255, 255, 255));
        pnlLineEntry.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlLineEntry.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblProduct.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblProduct.setText("Product (from Registry)");
        pnlLineEntry.add(lblProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 8, 220, 18));

        cboProduct.setFont(new java.awt.Font("Segoe UI", 0, 12));
        cboProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboProductActionPerformed(evt);
            }
        });
        pnlLineEntry.add(cboProduct, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 28, 410, 28));

        lblUnitPrice.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblUnitPrice.setText("Unit Price");
        pnlLineEntry.add(lblUnitPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(434, 8, 105, 18));

        txtUnitPrice.setEditable(false);
        txtUnitPrice.setBackground(new java.awt.Color(235, 239, 242));
        txtUnitPrice.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlLineEntry.add(txtUnitPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(434, 28, 105, 28));

        lblAvailableStock.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblAvailableStock.setForeground(new java.awt.Color(10, 114, 162));
        lblAvailableStock.setText("Stock: 0");
        pnlLineEntry.add(lblAvailableStock, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 28, 110, 28));

        lblQuantity.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblQuantity.setText("Qty *");
        pnlLineEntry.add(lblQuantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 8, 80, 18));

        txtQuantity.setFont(new java.awt.Font("Segoe UI", 0, 12));
        txtQuantity.setText("1");
        pnlLineEntry.add(txtQuantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 28, 80, 28));

        btnAddItem.setBackground(new java.awt.Color(10, 114, 162));
        btnAddItem.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnAddItem.setForeground(new java.awt.Color(255, 255, 255));
        btnAddItem.setText("Add Item");
        btnAddItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemActionPerformed(evt);
            }
        });
        pnlLineEntry.add(btnAddItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(762, 26, 102, 30));

        btnRemoveItem.setBackground(new java.awt.Color(192, 57, 43));
        btnRemoveItem.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnRemoveItem.setForeground(new java.awt.Color(255, 255, 255));
        btnRemoveItem.setText("Remove Item");
        btnRemoveItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveItemActionPerformed(evt);
            }
        });
        pnlLineEntry.add(btnRemoveItem, new org.netbeans.lib.awtextra.AbsoluteConstraints(874, 26, 108, 30));

        tblOrderLines.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Product Code", "Product Name", "Quantity", "Unit Price", "Line Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneLines.setViewportView(tblOrderLines);

        pnlLineEntry.add(jScrollPaneLines, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 64, 970, 104));

        lblTotalLabel.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblTotalLabel.setText("Grand Total:");
        pnlLineEntry.add(lblTotalLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 176, 90, 30));

        lblTotalAmount.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblTotalAmount.setForeground(new java.awt.Color(10, 114, 162));
        lblTotalAmount.setText("0.00");
        pnlLineEntry.add(lblTotalAmount, new org.netbeans.lib.awtextra.AbsoluteConstraints(104, 176, 160, 30));

        btnNewOrder.setBackground(new java.awt.Color(108, 117, 125));
        btnNewOrder.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnNewOrder.setForeground(new java.awt.Color(255, 255, 255));
        btnNewOrder.setText("New Order");
        btnNewOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewOrderActionPerformed(evt);
            }
        });
        pnlLineEntry.add(btnNewOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 176, 112, 30));

        btnSaveOrder.setBackground(new java.awt.Color(10, 114, 162));
        btnSaveOrder.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnSaveOrder.setForeground(new java.awt.Color(255, 255, 255));
        btnSaveOrder.setText("Save Order");
        btnSaveOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveOrderActionPerformed(evt);
            }
        });
        pnlLineEntry.add(btnSaveOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(462, 176, 124, 30));

        btnConfirmOrder.setBackground(new java.awt.Color(46, 125, 50));
        btnConfirmOrder.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnConfirmOrder.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirmOrder.setText("Confirm Order");
        btnConfirmOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmOrderActionPerformed(evt);
            }
        });
        pnlLineEntry.add(btnConfirmOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(596, 176, 134, 30));

        btnCancelOrder.setBackground(new java.awt.Color(211, 108, 0));
        btnCancelOrder.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnCancelOrder.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelOrder.setText("Cancel Order");
        btnCancelOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelOrderActionPerformed(evt);
            }
        });
        pnlLineEntry.add(btnCancelOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 176, 120, 30));

        btnDeleteOrder.setBackground(new java.awt.Color(192, 57, 43));
        btnDeleteOrder.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnDeleteOrder.setForeground(new java.awt.Color(255, 255, 255));
        btnDeleteOrder.setText("Delete");
        btnDeleteOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteOrderActionPerformed(evt);
            }
        });
        pnlLineEntry.add(btnDeleteOrder, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 176, 112, 30));

        getContentPane().add(pnlLineEntry, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 142, 994, 218));

        pnlExistingOrders.setBackground(new java.awt.Color(255, 255, 255));
        pnlExistingOrders.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlExistingOrders.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblExistingOrders.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblExistingOrders.setText("Existing Sales Orders (Click a row to load into editor)");
        pnlExistingOrders.add(lblExistingOrders, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 8, 460, 22));

        tblOrders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Order No", "Order Date", "Customer", "Sales Rep", "Status", "Total Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblOrders.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblOrdersMouseClicked(evt);
            }
        });
        jScrollPaneOrders.setViewportView(tblOrders);

        pnlExistingOrders.add(jScrollPaneOrders, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 34, 970, 196));

        getContentPane().add(pnlExistingOrders, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 368, 994, 242));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshDataActionPerformed
        loadComboLookups();
        refreshOrdersTable();
    }//GEN-LAST:event_btnRefreshDataActionPerformed

    private void cboProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboProductActionPerformed
        updateSelectedProductInfo();
    }//GEN-LAST:event_cboProductActionPerformed

    private void btnAddItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemActionPerformed
        Product product = (Product) cboProduct.getSelectedItem();
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Please select a product from the registry.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int qty;
        try {
            qty = Integer.parseInt(txtQuantity.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a whole number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (qty <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SalesOrderLine existingLine = null;
        for (SalesOrderLine line : stagedLines) {
            if (line.getProductId() == product.getId()) {
                existingLine = line;
                break;
            }
        }

        int newQty = existingLine != null ? existingLine.getQuantity() + qty : qty;
        if (newQty > product.getStockQty()) {
            JOptionPane.showMessageDialog(this,
                    "Requested quantity (" + newQty + ") exceeds available stock (" + product.getStockQty() + ") for " + product.getProductName() + ".",
                    "Stock Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (existingLine != null) {
            existingLine.setQuantity(newQty);
            existingLine.setUnitPrice(product.getUnitPrice());
        } else {
            SalesOrderLine line = new SalesOrderLine();
            line.setProductId(product.getId());
            line.setProductCode(product.getProductCode());
            line.setProductName(product.getProductName());
            line.setQuantity(qty);
            line.setUnitPrice(product.getUnitPrice());
            stagedLines.add(line);
        }
        refreshLinesTable();
        txtQuantity.setText("1");
    }//GEN-LAST:event_btnAddItemActionPerformed

    private void btnRemoveItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveItemActionPerformed
        int row = tblOrderLines.getSelectedRow();
        if (row < 0 || row >= stagedLines.size()) {
            JOptionPane.showMessageDialog(this, "Select a line item row to remove.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        stagedLines.remove(row);
        refreshLinesTable();
    }//GEN-LAST:event_btnRemoveItemActionPerformed

    private void btnNewOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewOrderActionPerformed
        loadComboLookups();
        resetNewOrder();
    }//GEN-LAST:event_btnNewOrderActionPerformed

    private void btnSaveOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveOrderActionPerformed
        if (appContext == null) {
            return;
        }
        Customer customer = (Customer) cboCustomer.getSelectedItem();
        Employee employee = (Employee) cboEmployee.getSelectedItem();
        if (customer == null || employee == null) {
            JOptionPane.showMessageDialog(this, "Please select both Customer and Sales Representative.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            SalesOrder order = new SalesOrder();
            order.setId(currentOrderId);
            order.setCustomerId(customer.getId());
            order.setEmployeeId(employee.getId());
            order.setLines(new ArrayList<>(stagedLines));

            if (currentOrderId <= 0) {
                SalesOrder created = appContext.getSalesOrderService().createOrder(order);
                refreshOrdersTable();
                loadOrderIntoEditor(created.getId());
                JOptionPane.showMessageDialog(this, "Sales Order " + created.getOrderNo() + " created.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                appContext.getSalesOrderService().updateOrder(order);
                refreshOrdersTable();
                loadOrderIntoEditor(currentOrderId);
                JOptionPane.showMessageDialog(this, "Sales Order updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "System error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveOrderActionPerformed

    private void btnConfirmOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmOrderActionPerformed
        if (appContext == null || currentOrderId <= 0) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Confirm Sales Order " + txtOrderNo.getText() + " for invoicing?",
                "Confirm Sales Order", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            appContext.getSalesOrderService().confirmOrder(currentOrderId);
            refreshOrdersTable();
            loadOrderIntoEditor(currentOrderId);
            JOptionPane.showMessageDialog(this, "Sales Order confirmed and ready for invoicing.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Business Rule Violation", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "System error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnConfirmOrderActionPerformed

    private void btnCancelOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelOrderActionPerformed
        if (appContext == null || currentOrderId <= 0) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel Sales Order " + txtOrderNo.getText() + "?",
                "Cancel Sales Order", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            appContext.getSalesOrderService().cancelOrder(currentOrderId);
            refreshOrdersTable();
            loadOrderIntoEditor(currentOrderId);
            JOptionPane.showMessageDialog(this, "Sales Order cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Business Rule Violation", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "System error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCancelOrderActionPerformed

    private void btnDeleteOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteOrderActionPerformed
        if (appContext == null || currentOrderId <= 0) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete Sales Order " + txtOrderNo.getText() + "?",
                "Delete Sales Order", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            appContext.getSalesOrderService().deleteOrder(currentOrderId);
            refreshOrdersTable();
            resetNewOrder();
            JOptionPane.showMessageDialog(this, "Sales Order deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot Delete", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "System error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteOrderActionPerformed

    private void tblOrdersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblOrdersMouseClicked
        int row = tblOrders.getSelectedRow();
        if (row >= 0) {
            int orderId = Integer.parseInt(String.valueOf(tblOrders.getValueAt(row, 0)));
            loadOrderIntoEditor(orderId);
        }
    }//GEN-LAST:event_tblOrdersMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddItem;
    private javax.swing.JButton btnCancelOrder;
    private javax.swing.JButton btnConfirmOrder;
    private javax.swing.JButton btnDeleteOrder;
    private javax.swing.JButton btnNewOrder;
    private javax.swing.JButton btnRefreshData;
    private javax.swing.JButton btnRemoveItem;
    private javax.swing.JButton btnSaveOrder;
    private javax.swing.JComboBox<sales.model.Customer> cboCustomer;
    private javax.swing.JComboBox<sales.model.Employee> cboEmployee;
    private javax.swing.JComboBox<sales.model.Product> cboProduct;
    private javax.swing.JScrollPane jScrollPaneLines;
    private javax.swing.JScrollPane jScrollPaneOrders;
    private javax.swing.JLabel lblAvailableStock;
    private javax.swing.JLabel lblCustomer;
    private javax.swing.JLabel lblEmployee;
    private javax.swing.JLabel lblExistingOrders;
    private javax.swing.JLabel lblOrderNo;
    private javax.swing.JLabel lblProduct;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalAmount;
    private javax.swing.JLabel lblTotalLabel;
    private javax.swing.JLabel lblUnitPrice;
    private javax.swing.JPanel pnlExistingOrders;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlLineEntry;
    private javax.swing.JPanel pnlOrderHeader;
    private javax.swing.JTable tblOrderLines;
    private javax.swing.JTable tblOrders;
    private javax.swing.JTextField txtOrderNo;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtStatus;
    private javax.swing.JTextField txtUnitPrice;
    // End of variables declaration//GEN-END:variables
}
