package org.example.engine.internal;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.engine.internal.Datasource.initializeDataSource;

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
                return new WorkflowEntity(
                        resultSet.getString("id"),
                        resultSet.getString("heartbeat_ts")
                );
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database access error while fetching workflow with id: " + workflowId, e);
        }
        return null;
    }

    public static void insertWorkflow(WorkflowEntity workflow) {
        String query = "INSERT INTO workflow (id, heartbeat_ts) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, workflow.id());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(workflow.heartbeatTs()));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database access error while inserting workflow with id: " + workflow.id(), e);
        }
    }

    public static EventEntity getEventById(String eventId) {
        String query = "SELECT id, workflow_id, run_id, entity, class_name, function_name, input_data, output_data, status, created_at FROM event WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, eventId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new EventEntity(
                        resultSet.getString("id"),
                        resultSet.getString("workflow_id"),
                        resultSet.getString("run_id"),
                        resultSet.getString("entity"),
                        resultSet.getString("class_name"),
                        resultSet.getString("function_name"),
                        resultSet.getString("input_data"),
                        resultSet.getString("output_data"),
                        resultSet.getString("status"),
                        resultSet.getString("created_at")
                );
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database access error while fetching event with id: " + eventId, e);
        }
        return null;
    }

    public static void insertEvent(EventEntity event) {
        String query = "INSERT INTO event (id, workflow_id, run_id, entity, class_name, function_name, input_data, output_data, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, event.id());
            preparedStatement.setString(2, event.workflowId());
            preparedStatement.setString(3, event.runId());
            preparedStatement.setString(4, event.entity());
            preparedStatement.setString(5, event.className());
            preparedStatement.setString(6, event.functionName());
            preparedStatement.setString(7, event.inputData());
            preparedStatement.setString(8, event.outputData());
            preparedStatement.setString(9, event.status());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database access error while inserting event with id: " + event.id(), e);
        }
    }
}
