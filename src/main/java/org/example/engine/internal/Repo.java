package org.example.engine.internal;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Repo {

    private static final Logger logger = Logger.getLogger(Repo.class.getName());
    private static final HikariDataSource dataSource = initializeDataSource();

    public static WorkflowEntity getWorkflowById(String workflowId) {
        String query = "SELECT id, heartbeat_ts FROM workflow WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, workflowId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String id = resultSet.getString("id");
                String heartbeatTs = resultSet.getString("heartbeat_ts");
                return new WorkflowEntity(id, heartbeatTs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database access error while fetching workflow with id: " + workflowId, e);
        }
        return null;
    }

    private static HikariDataSource initializeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/application_db");
        config.setUsername("postgres");
        config.setPassword("password");
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setAutoCommit(false);
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
        return new HikariDataSource(config);
    }
}
