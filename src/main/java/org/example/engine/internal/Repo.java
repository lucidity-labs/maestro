package org.example.engine.internal;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.engine.internal.Datasource.initializeDataSource;

public class Repo {

    private static final Logger logger = Logger.getLogger(Repo.class.getName());
    private static final HikariDataSource dataSource = initializeDataSource();

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
                        resultSet.getLong("sequence_number"),
                        resultSet.getString("run_id"),
                        Entity.valueOf(resultSet.getString("entity")),
                        resultSet.getString("class_name"),
                        resultSet.getString("function_name"),
                        resultSet.getString("input_data"),
                        resultSet.getString("output_data"),
                        Status.valueOf(resultSet.getString("status")),
                        resultSet.getString("created_at")
                );
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database access error while fetching event with id: " + eventId, e);
        }
        return null;
    }

    public static void insertEvent(EventEntity event) {
        String query = "INSERT INTO event (id, workflow_id, sequence_number, run_id, entity, class_name, function_name, input_data, output_data, status) " +
                "VALUES (?, ?, ?::bigint, ?, ?::entity, ?, ?, ?::json, ?::json, ?::status)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, event.id());
            preparedStatement.setString(2, event.workflowId());
            preparedStatement.setLong(3, event.sequenceNumber());
            preparedStatement.setString(4, event.runId());
            preparedStatement.setString(5, event.entity().name());
            preparedStatement.setString(6, event.className());
            preparedStatement.setString(7, event.functionName());
            preparedStatement.setString(8, event.inputData());
            preparedStatement.setString(9, event.outputData());
            preparedStatement.setString(10, event.status().name());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database access error while inserting event with id: " + event.id(), e);
        }
    }
}
