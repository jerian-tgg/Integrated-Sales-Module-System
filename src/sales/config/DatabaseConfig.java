package sales.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConfig {

    public static HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/db_sales_system?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true");
        config.setUsername("root");
        config.setPassword("");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setInitializationFailTimeout(3000);
        config.setConnectionTimeout(3000);
        return new HikariDataSource(config);
    }
}
