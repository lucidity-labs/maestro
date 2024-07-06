package org.example.engine.internal;

import io.github.resilience4j.retry.Retry;
import org.postgresql.util.PSQLException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.engine.internal.Datasource.getDataSource;
import static org.example.engine.internal.SqlQueries.*;

public class EventRepo {

    private static final Logger logger = Logger.getLogger(EventRepo.class.getName());
    private static final DataSource dataSource = getDataSource();

    public static EventEntity get(String workflowId, Long correlationNumber, Status status) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_EVENT_BY_CORRELATION_NO)) {

            preparedStatement.setString(1, workflowId);
            preparedStatement.setLong(2, correlationNumber);
            preparedStatement.setString(3, status.name());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return map(resultSet);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database access error while fetching event with workflowId: " + workflowId
                    + ", correlationNumber: " + correlationNumber + ", status: " + status, e);

            throw new RuntimeException(e);
        }
        return null;
    }

    public static EventEntity get(String workflowId, Category category, Status status) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_EVENT)) {

            preparedStatement.setString(1, workflowId);
            preparedStatement.setString(2, category.name());
            preparedStatement.setString(3, status.name());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return map(resultSet);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database access error while fetching event with workflowId: " + workflowId
                    + ", status: " + status, e);

            throw e;
        }
        return null;
    }

    public static List<EventEntity> getSignals(String workflowId, Long sequenceNumber) {
        List<EventEntity> signals = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_SIGNALS)) {

            preparedStatement.setString(1, workflowId);
            preparedStatement.setString(2, workflowId);
            preparedStatement.setLong(3, sequenceNumber);
            preparedStatement.setLong(4, sequenceNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                EventEntity eventEntity = map(resultSet);
                signals.add(eventEntity);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database access error while fetching signals with workflowId: " + workflowId + " and sequenceNumber: " + sequenceNumber, e);
        }
        return signals;
    }

    public static Long getNextSequenceNumber(String workflowId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(MAX_SEQUENCE_NUMBER)) {

            preparedStatement.setString(1, workflowId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return resultSet.getLong(1) + 1;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database access error while fetching max sequence_number with workflowId: " + workflowId);
            throw new RuntimeException(e);
        }
        return 1L;
    }

    public static void saveWithRetry(Supplier<EventEntity> eventSupplier) {
        try {
            Retry.decorateCheckedRunnable(RetryConfiguration.getRetry(), () -> save(eventSupplier.get())).run();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } catch (Throwable t) {
            throw new Error(t);
        }
    }

    private static void save(EventEntity event) throws SQLException, WorkflowCorrelationStatusConflict, WorkflowSequenceConflict {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EVENT)) {

            preparedStatement.setString(1, event.id());
            preparedStatement.setString(2, event.workflowId());
            preparedStatement.setObject(3, event.correlationNumber(), java.sql.Types.BIGINT);
            preparedStatement.setLong(4, event.sequenceNumber());
            preparedStatement.setString(5, event.runId());
            preparedStatement.setString(6, event.category().name());
            preparedStatement.setString(7, event.className());
            preparedStatement.setString(8, event.functionName());
            preparedStatement.setString(9, event.inputData());
            preparedStatement.setString(10, event.outputData());
            preparedStatement.setString(11, event.status().name());
            preparedStatement.executeUpdate();

        } catch (PSQLException e) {
            if ("23505".equals(e.getSQLState())) {
                String message = e.getMessage(); // TODO: needs null check?
                logger.warning(message);

                if (message.contains("event_unique_workflow_correlation_status")) {
                    logger.info("Violation of unique index: event_unique_workflow_correlation_status");
                    throw new WorkflowCorrelationStatusConflict(message);
                } else if (message.contains("event_unique_workflow_sequence")) {
                    logger.info("Violation of unique index: event_unique_workflow_sequence");
                    throw new WorkflowSequenceConflict(message);
                } else logger.severe("Unknown unique index violation");
            }
            throw e;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database access error while inserting event with id: " + event.id(), e);
            throw e;
        }
    }

    private static EventEntity map(ResultSet resultSet) throws SQLException {
        return new EventEntity(
                resultSet.getString("id"),
                resultSet.getString("workflow_id"),
                resultSet.getLong("correlation_number"),
                resultSet.getLong("sequence_number"),
                resultSet.getString("run_id"),
                Category.valueOf(resultSet.getString("category")),
                resultSet.getString("class_name"),
                resultSet.getString("function_name"),
                resultSet.getString("input_data"),
                resultSet.getString("output_data"),
                Status.valueOf(resultSet.getString("status")),
                resultSet.getString("created_at")
        );
    }
}
