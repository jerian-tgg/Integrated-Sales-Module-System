package sales.ui;

import java.awt.Color;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import sales.AppContext;
import sales.model.Customer;

public class frmCustomer extends javax.swing.JFrame {

    private final AppContext appContext;

    public frmCustomer() {
        this(null);
    }

    public frmCustomer(AppContext appContext) {
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
            List<Customer> customers = appContext.getCustomerService().search(keyword);
            DefaultTableModel model = (DefaultTableModel) tblCustomers.getModel();
            model.setRowCount(0);
            for (Customer c : customers) {
                model.addRow(new Object[]{
                    c.getId(),
                    c.getCustomerName(),
                    c.getContact(),
                    c.getAddress()
                });
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load customers: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Customer readForm(boolean includeId) {
        Customer c = new Customer();
        if (includeId && !txtId.getText().trim().isEmpty()) {
            c.setId(Integer.parseInt(txtId.getText().trim()));
        }
        c.setCustomerName(txtCustomerName.getText());
        c.setContact(txtContact.getText());
        c.setAddress(txtAddress.getText());
        return c;
    }

    private void clearForm() {
        txtId.setText("");
        txtCustomerName.setText("");
        txtContact.setText("");
        txtAddress.setText("");
        tblCustomers.clearSelection();
        txtCustomerName.requestFocusInWindow();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        pnlForm = new javax.swing.JPanel();
        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblCustomerName = new javax.swing.JLabel();
        txtCustomerName = new javax.swing.JTextField();
        lblContact = new javax.swing.JLabel();
        txtContact = new javax.swing.JTextField();
        lblAddress = new javax.swing.JLabel();
        jScrollPaneAddress = new javax.swing.JScrollPane();
        txtAddress = new javax.swing.JTextArea();
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
        tblCustomers = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Customer Registry");
        setPreferredSize(new java.awt.Dimension(980, 600));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlHeader.setBackground(new java.awt.Color(10, 114, 162));
        pnlHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Customer Registry");
        pnlHeader.add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 14, 320, 28));

        getContentPane().add(pnlHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 980, 56));

        pnlForm.setBackground(new java.awt.Color(255, 255, 255));
        pnlForm.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblId.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblId.setText("Customer ID");
        pnlForm.add(lblId, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 18, 298, 20));

        txtId.setEditable(false);
        txtId.setBackground(new java.awt.Color(235, 239, 242));
        txtId.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlForm.add(txtId, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 40, 298, 28));

        lblCustomerName.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblCustomerName.setText("Customer Name *");
        pnlForm.add(lblCustomerName, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 78, 298, 20));

        txtCustomerName.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlForm.add(txtCustomerName, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 100, 298, 28));

        lblContact.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblContact.setText("Contact No. / Email");
        pnlForm.add(lblContact, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 138, 298, 20));

        txtContact.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlForm.add(txtContact, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 160, 298, 28));

        lblAddress.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblAddress.setText("Address");
        pnlForm.add(lblAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 198, 298, 20));

        txtAddress.setColumns(20);
        txtAddress.setFont(new java.awt.Font("Segoe UI", 0, 12));
        txtAddress.setLineWrap(true);
        txtAddress.setRows(3);
        txtAddress.setWrapStyleWord(true);
        jScrollPaneAddress.setViewportView(txtAddress);

        pnlForm.add(jScrollPaneAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 220, 298, 90));

        btnSave.setBackground(new java.awt.Color(10, 114, 162));
        btnSave.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setText("Save New");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        pnlForm.add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 330, 142, 36));

        btnUpdate.setBackground(new java.awt.Color(46, 125, 50));
        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        pnlForm.add(btnUpdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 330, 142, 36));

        btnDelete.setBackground(new java.awt.Color(192, 57, 43));
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        pnlForm.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 378, 142, 36));

        btnClear.setBackground(new java.awt.Color(108, 117, 125));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        pnlForm.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 378, 142, 36));

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
        pnlTable.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(76, 14, 290, 28));

        btnSearch.setBackground(new java.awt.Color(10, 114, 162));
        btnSearch.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        pnlTable.add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(376, 14, 96, 28));

        btnRefresh.setBackground(new java.awt.Color(108, 117, 125));
        btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnRefresh.setForeground(new java.awt.Color(255, 255, 255));
        btnRefresh.setText("Show All");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        pnlTable.add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(482, 14, 98, 28));

        tblCustomers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Customer Name", "Contact", "Address"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCustomers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCustomersMouseClicked(evt);
            }
        });
        jScrollPaneTable.setViewportView(tblCustomers);

        pnlTable.add(jScrollPaneTable, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 54, 566, 395));

        getContentPane().add(pnlTable, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 70, 594, 465));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (appContext == null) {
            return;
        }
        try {
            Customer customer = readForm(false);
            appContext.getCustomerService().saveCustomer(customer);
            refreshTable(txtSearch.getText());
            clearForm();
            JOptionPane.showMessageDialog(this, "Customer saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Please select a customer row to update.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Customer customer = readForm(true);
            appContext.getCustomerService().saveCustomer(customer);
            refreshTable(txtSearch.getText());
            clearForm();
            JOptionPane.showMessageDialog(this, "Customer updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Please select a customer row to delete.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete customer '" + txtCustomerName.getText().trim() + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            appContext.getCustomerService().deleteCustomer(id);
            refreshTable(txtSearch.getText());
            clearForm();
            JOptionPane.showMessageDialog(this, "Customer deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
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

    private void tblCustomersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCustomersMouseClicked
        int row = tblCustomers.getSelectedRow();
        if (row >= 0) {
            txtId.setText(String.valueOf(tblCustomers.getValueAt(row, 0)));
            txtCustomerName.setText(String.valueOf(tblCustomers.getValueAt(row, 1)));
            Object contact = tblCustomers.getValueAt(row, 2);
            Object address = tblCustomers.getValueAt(row, 3);
            txtContact.setText(contact != null ? contact.toString() : "");
            txtAddress.setText(address != null ? address.toString() : "");
        }
    }//GEN-LAST:event_tblCustomersMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JScrollPane jScrollPaneAddress;
    private javax.swing.JScrollPane jScrollPaneTable;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblContact;
    private javax.swing.JLabel lblCustomerName;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel pnlForm;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JTable tblCustomers;
    private javax.swing.JTextArea txtAddress;
    private javax.swing.JTextField txtContact;
    private javax.swing.JTextField txtCustomerName;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
