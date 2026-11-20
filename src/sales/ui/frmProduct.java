package sales.ui;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import sales.AppContext;
import sales.model.Product;

public class frmProduct extends javax.swing.JFrame {

    private final AppContext appContext;

    public frmProduct() {
        this(null);
    }

    public frmProduct(AppContext appContext) {
        this.appContext = appContext;
        initComponents();
        if (this.appContext != null) {
            initCustomLogic();
        }
    }

    private void initCustomLogic() {
        getContentPane().setBackground(new Color(244, 246, 248));
        setLocationRelativeTo(null);
        refreshTable("");
    }

    private void refreshTable(String keyword) {
        if (appContext == null) {
            return;
        }
        try {
            List<Product> products = appContext.getProductService().search(keyword);
            DefaultTableModel model = (DefaultTableModel) tblProducts.getModel();
            model.setRowCount(0);
            for (Product p : products) {
                model.addRow(new Object[]{
                    p.getId(),
                    p.getProductCode(),
                    p.getProductName(),
                    p.getUnitPrice().toPlainString(),
                    p.getStockQty()
                });
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load products: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Product readForm(boolean includeId) {
        Product p = new Product();
        if (includeId && !txtId.getText().trim().isEmpty()) {
            p.setId(Integer.parseInt(txtId.getText().trim()));
        }
        p.setProductCode(txtProductCode.getText());
        p.setProductName(txtProductName.getText());
        try {
            p.setUnitPrice(new BigDecimal(txtUnitPrice.getText().trim()));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Unit price must be a valid decimal number.");
        }
        try {
            p.setStockQty(Integer.parseInt(txtStockQty.getText().trim()));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Stock quantity must be a valid whole number.");
        }
        return p;
    }

    private void clearForm() {
        txtId.setText("");
        txtProductCode.setText("");
        txtProductName.setText("");
        txtUnitPrice.setText("");
        txtStockQty.setText("");
        tblProducts.clearSelection();
        txtProductCode.requestFocusInWindow();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        pnlForm = new javax.swing.JPanel();
        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblProductCode = new javax.swing.JLabel();
        txtProductCode = new javax.swing.JTextField();
        lblProductName = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        lblUnitPrice = new javax.swing.JLabel();
        txtUnitPrice = new javax.swing.JTextField();
        lblStockQty = new javax.swing.JLabel();
        txtStockQty = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        pnlTable = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jScrollPaneTable = new javax.swing.JScrollPane();
        tblProducts = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Product Registry");
        setPreferredSize(new java.awt.Dimension(1000, 600));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlHeader.setBackground(new java.awt.Color(10, 114, 162));
        pnlHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Product Registry");
        pnlHeader.add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 14, 320, 28));

        getContentPane().add(pnlHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 56));

        pnlForm.setBackground(new java.awt.Color(255, 255, 255));
        pnlForm.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblId.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblId.setText("Product ID");
        pnlForm.add(lblId, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 16, 298, 20));

        txtId.setEditable(false);
        txtId.setBackground(new java.awt.Color(235, 239, 242));
        txtId.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlForm.add(txtId, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 38, 298, 28));

        lblProductCode.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblProductCode.setText("Product Code *");
        pnlForm.add(lblProductCode, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 74, 298, 20));

        txtProductCode.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlForm.add(txtProductCode, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 96, 298, 28));

        lblProductName.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblProductName.setText("Product Name *");
        pnlForm.add(lblProductName, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 132, 298, 20));

        txtProductName.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlForm.add(txtProductName, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 154, 298, 28));

        lblUnitPrice.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblUnitPrice.setText("Unit Price *");
        pnlForm.add(lblUnitPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 190, 298, 20));

        txtUnitPrice.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlForm.add(txtUnitPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 212, 298, 28));

        lblStockQty.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblStockQty.setText("Stock Quantity *");
        pnlForm.add(lblStockQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 248, 298, 20));

        txtStockQty.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlForm.add(txtStockQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 270, 298, 28));

        btnSave.setBackground(new java.awt.Color(10, 114, 162));
        btnSave.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setText("Save New");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        pnlForm.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 326, 142, 36));

        btnUpdate.setBackground(new java.awt.Color(46, 125, 50));
        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        pnlForm.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 326, 142, 36));

        btnDelete.setBackground(new java.awt.Color(192, 57, 43));
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        pnlForm.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 374, 142, 36));

        btnClear.setBackground(new java.awt.Color(108, 117, 125));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        pnlForm.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 374, 142, 36));

        getContentPane().add(pnlForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 70, 330, 465));

        pnlTable.setBackground(new java.awt.Color(255, 255, 255));
        pnlTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlTable.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblSearch.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblSearch.setText("Search");
        pnlTable.add(lblSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 14, 60, 28));

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 12));
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        pnlTable.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(76, 14, 310, 28));

        btnSearch.setBackground(new java.awt.Color(10, 114, 162));
        btnSearch.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        pnlTable.add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(396, 14, 96, 28));

        btnRefresh.setBackground(new java.awt.Color(108, 117, 125));
        btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnRefresh.setForeground(new java.awt.Color(255, 255, 255));
        btnRefresh.setText("Show All");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        pnlTable.add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(502, 14, 98, 28));

        tblProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Product Code", "Product Name", "Unit Price", "Stock Qty"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProducts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductsMouseClicked(evt);
            }
        });
        jScrollPaneTable.setViewportView(tblProducts);

        pnlTable.add(jScrollPaneTable, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 54, 586, 395));

        getContentPane().add(pnlTable, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 70, 614, 465));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (appContext == null) {
            return;
        }
        try {
            Product product = readForm(false);
            appContext.getProductService().saveProduct(product);
            refreshTable(txtSearch.getText());
            clearForm();
            JOptionPane.showMessageDialog(this, "Product saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "System error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        if (appContext == null) {
            return;
        }
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a product row to update.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Product product = readForm(true);
            appContext.getProductService().saveProduct(product);
            refreshTable(txtSearch.getText());
            clearForm();
            JOptionPane.showMessageDialog(this, "Product updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "System error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (appContext == null) {
            return;
        }
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a product row to delete.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete product '" + txtProductName.getText().trim() + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            appContext.getProductService().deleteProduct(id);
            refreshTable(txtSearch.getText());
            clearForm();
            JOptionPane.showMessageDialog(this, "Product deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot Delete", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "System error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearForm();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        refreshTable(txtSearch.getText());
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        txtSearch.setText("");
        refreshTable("");
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        refreshTable(txtSearch.getText());
    }//GEN-LAST:event_txtSearchActionPerformed

    private void tblProductsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductsMouseClicked
        int row = tblProducts.getSelectedRow();
        if (row >= 0) {
            txtId.setText(String.valueOf(tblProducts.getValueAt(row, 0)));
            txtProductCode.setText(String.valueOf(tblProducts.getValueAt(row, 1)));
            txtProductName.setText(String.valueOf(tblProducts.getValueAt(row, 2)));
            txtUnitPrice.setText(String.valueOf(tblProducts.getValueAt(row, 3)));
            txtStockQty.setText(String.valueOf(tblProducts.getValueAt(row, 4)));
        }
    }//GEN-LAST:event_tblProductsMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JScrollPane jScrollPaneTable;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblProductCode;
    private javax.swing.JLabel lblProductName;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblStockQty;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUnitPrice;
    private javax.swing.JPanel pnlForm;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JTable tblProducts;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtProductCode;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtStockQty;
    private javax.swing.JTextField txtUnitPrice;
    // End of variables declaration//GEN-END:variables
}
