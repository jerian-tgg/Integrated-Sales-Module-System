package sales;

import com.zaxxer.hikari.HikariDataSource;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sales.config.DatabaseConfig;
import sales.config.SalesSchema;
import sales.ui.frmLogin;

public class Main {

    public static void main(String[] args) {
        configureLookAndFeel();

        final HikariDataSource dataSource;
        try {
            dataSource = DatabaseConfig.createDataSource();
            SalesSchema.ensureInstalled(dataSource);
        } catch (RuntimeException ex) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                    null,
                    "Unable to connect to MySQL on localhost:3306.\n"
                    + "Please start MySQL (XAMPP / WAMP) and verify database 'db_sales_system'.\n\n"
                    + "Details: " + ex.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE
            ));
            return;
        }

        final AppContext appContext = new AppContext(dataSource);
        Runtime.getRuntime().addShutdownHook(new Thread(appContext::close));

        SwingUtilities.invokeLater(() -> {
            frmLogin loginForm = new frmLogin(appContext);
            loginForm.setVisible(true);
        });
    }

    private static void configureLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback to default Swing Look and Feel
        }
    }
}
