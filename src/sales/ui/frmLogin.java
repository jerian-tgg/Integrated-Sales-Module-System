package sales.ui;

import java.awt.Color;
import javax.swing.JOptionPane;
import sales.AppContext;
import sales.model.Employee;

public class frmLogin extends javax.swing.JFrame {

    private final AppContext appContext;

    public frmLogin() {
        this(null);
    }

    public frmLogin(AppContext appContext) {
        this.appContext = appContext;
        initComponents();
        if (this.appContext != null) {
            initCustomLogic();
        }
    }

    private void initCustomLogic() {
        getContentPane().setBackground(new Color(244, 246, 248));
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btnLogin);
        txtUsername.requestFocusInWindow();
    }

    private void performLogin() {
        if (appContext == null) {
            return;
        }
        try {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            Employee employee = appContext.getEmployeeService().authenticate(username, password);
            appContext.setCurrentEmployee(employee);

            frmMain mainForm = new frmMain(appContext);
            mainForm.setVisible(true);
            dispose();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Failed", JOptionPane.WARNING_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "System error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblSubtitle = new javax.swing.JLabel();
        pnlCard = new javax.swing.JPanel();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        lblHint = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Integrated Sales Module System - Login");
        setPreferredSize(new java.awt.Dimension(440, 340));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlHeader.setBackground(new java.awt.Color(10, 114, 162));
        pnlHeader.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Integrated Sales Module System");
        pnlHeader.add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 390, 24));

        lblSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 12));
        lblSubtitle.setForeground(new java.awt.Color(220, 238, 248));
        lblSubtitle.setText("Sign in with your employee credentials");
        pnlHeader.add(lblSubtitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 36, 380, 18));

        getContentPane().add(pnlHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 440, 64));

        pnlCard.setBackground(new java.awt.Color(255, 255, 255));
        pnlCard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(208, 215, 222)));
        pnlCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblUsername.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblUsername.setText("Username");
        pnlCard.add(lblUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 90, 28));

        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 12));
        pnlCard.add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(115, 20, 250, 28));

        lblPassword.setFont(new java.awt.Font("Segoe UI", 1, 12));
        lblPassword.setText("Password");
        pnlCard.add(lblPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 90, 28));

        txtPassword.setFont(new java.awt.Font("Segoe UI", 0, 12));
        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });
        pnlCard.add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(115, 60, 250, 28));

        btnLogin.setBackground(new java.awt.Color(10, 114, 162));
        btnLogin.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnLogin.setForeground(new java.awt.Color(255, 255, 255));
        btnLogin.setText("Sign In");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        pnlCard.add(btnLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(115, 106, 120, 34));

        btnClear.setBackground(new java.awt.Color(108, 117, 125));
        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 12));
        btnClear.setForeground(new java.awt.Color(255, 255, 255));
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        pnlCard.add(btnClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(245, 106, 120, 34));

        lblHint.setFont(new java.awt.Font("Segoe UI", 2, 11));
        lblHint.setForeground(new java.awt.Color(108, 117, 125));
        lblHint.setText("Default admin account: admin / admin");
        pnlCard.add(lblHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 152, 345, 22));

        getContentPane().add(pnlCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 385, 192));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        performLogin();
    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.requestFocusInWindow();
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        performLogin();
    }//GEN-LAST:event_txtPasswordActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel lblHint;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblSubtitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPanel pnlCard;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
