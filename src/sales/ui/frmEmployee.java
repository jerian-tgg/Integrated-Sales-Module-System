package sales.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import sales.AppContext;
import sales.model.Employee;

public class frmEmployee extends javax.swing.JFrame {

    private final AppContext appContext;
    private List<Employee> loadedEmployees = new ArrayList<>();

    public frmEmployee() {
        this(null);
    }

    public frmEmployee(AppContext appContext) {
        this.appContext = appContext;
        initComponents();
        if (this.appContext != null) {
            initCustomLogic();
        }
    }

    private void initCustomLogic() {
        getContentPane().setBackground(new Color(244, 246, 248));
        setLocationRelativeTo(null);
        cboRole.setSelectedItem("SALES");
        refreshTable("");
    }

    private void refreshTable(String keyword) {
        if (appContext == null) {
            return;
        }
        try {
            loadedEmployees = appContext.getEmployeeService().search(keyword);
            DefaultTableModel model = (DefaultTableModel) tblEmployees.getModel();
            model.setRowCount(0);
            for (Employee e : loadedEmployees) {
                model.addRow(new Object[]{
                    e.getId(),
                    e.getEmployeeName(),
                    e.getUsername(),
                    e.getRole()
                });
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load employees: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Employee readForm(boolean includeId) {
        Employee emp = new Employee();
        if (includeId && !txtId.getText().trim().isEmpty()) {
            emp.setId(Integer.parseInt(txtId.getText().trim()));
        }
        emp.setEmployeeName(txtEmployeeName.getText());
        emp.setUsername(txtUsername.getText());
        emp.setPassword(new String(txtPassword.getPassword()));
        Object role = cboRole.getSelectedItem();
        emp.setRole(role != null ? role.toString() : "SALES");
        return emp;
    }

    private void clearForm() {
        txtId.setText("");
        txtEmployeeName.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cboRole.setSelectedItem("SALES");
        tblEmployees.clearSelection();
        txtEmployeeName.requestFocusInWindow();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        pnlForm = new javax.swing.JPanel();
        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblEmployeeName = new javax.swing.JLabel();
        txtEmployeeName = new javax.swing.JTextField();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        lblRole = new javax.swing.JLabel();
        cboRole = new javax.swing.JComboBox<>();
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
        tblEmployees = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Employee Registry");
        setPreferredSize(new java.awt.Dimension(1000, 600));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlHeader.setBackground(new java.awt.Color(10, 114, 162));
        pnlHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Employee Registry");
        pnlHeader.add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 14, 320, 28));

        getContentPane().add(pnlHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 56));

        pnlForm.setBackground(new java.awt.Color(255, 255, 255));
        pnlForm.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlForm.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblId.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblId.setText("Employee ID");
        pnlForm.add(lblId, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 16, 298, 20));

        txtId.setEditable(false);
        txtId.setBackground(new java.awt.Color(235, 239, 242));
        txtId.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlForm.add(txtId, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 38, 298, 28));

        lblEmployeeName.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblEmployeeName.setText("Employee Name *");
        pnlForm.add(lblEmployeeName, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 74, 298, 20));

        txtEmployeeName.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlForm.add(txtEmployeeName, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 96, 298, 28));

        lblUsername.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblUsername.setText("Username *");
        pnlForm.add(lblUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 132, 298, 20));

        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlForm.add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 154, 298, 28));

        lblPassword.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblPassword.setText("Password *");
        pnlForm.add(lblPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 190, 298, 20));

        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlForm.add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 212, 298, 28));

        lblRole.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblRole.setText("Role *");
        pnlForm.add(lblRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 248, 298, 20));

        cboRole.setFont(new java.awt.Font("Segoe UI", 0, 12));
        cboRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ADMIN", "SALES", "CASHIER" }));
        pnlForm.add(cboRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 270, 298, 28));

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

        tblEmployees.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Employee Name", "Username", "Role"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblEmployees.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblEmployeesMouseClicked(evt);
            }
        });
        jScrollPaneTable.setViewportView(tblEmployees);

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
            Employee employee = readForm(false);
            appContext.getEmployeeService().saveEmployee(employee);
            refreshTable(txtSearch.getText());
            clearForm();
            JOptionPane.showMessageDialog(this, "Employee saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Please select an employee row to update.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Employee employee = readForm(true);
            appContext.getEmployeeService().saveEmployee(employee);
            refreshTable(txtSearch.getText());
            clearForm();
            JOptionPane.showMessageDialog(this, "Employee updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Please select an employee row to delete.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete employee '" + txtEmployeeName.getText().trim() + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            Employee current = appContext.getCurrentEmployee();
            int currentId = current != null ? current.getId() : -1;
            appContext.getEmployeeService().deleteEmployee(id, currentId);
            refreshTable(txtSearch.getText());
            clearForm();
            JOptionPane.showMessageDialog(this, "Employee deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
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

    private void tblEmployeesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblEmployeesMouseClicked
        int row = tblEmployees.getSelectedRow();
        if (row >= 0 && row < loadedEmployees.size()) {
            Employee selected = loadedEmployees.get(row);
            txtId.setText(String.valueOf(selected.getId()));
            txtEmployeeName.setText(selected.getEmployeeName());
            txtUsername.setText(selected.getUsername());
            txtPassword.setText(selected.getPassword());
            cboRole.setSelectedItem(selected.getRole());
        }
    }//GEN-LAST:event_tblEmployeesMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cboRole;
    private javax.swing.JScrollPane jScrollPaneTable;
    private javax.swing.JLabel lblEmployeeName;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblRole;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel pnlForm;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JTable tblEmployees;
    private javax.swing.JTextField txtEmployeeName;
    private javax.swing.JTextField txtId;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
