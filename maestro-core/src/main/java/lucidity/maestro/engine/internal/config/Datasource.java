package lucidity.maestro.engine.internal.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class Datasource {

    private static final HikariDataSource dataSource = initializeDataSource();

    public static DataSource getDataSource() {
        return dataSource;
    }

    private static HikariDataSource initializeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getenv("MAESTRO_DB_URL"));
        config.setUsername(System.getenv("MAESTRO_DB_USERNAME"));
        config.setPassword(System.getenv("MAESTRO_DB_PASSWORD"));
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        return new HikariDataSource(config);
    }
}
