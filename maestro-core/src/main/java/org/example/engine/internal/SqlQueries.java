package org.example.engine.internal;

public class SqlQueries {
    //language=SQL
    public static final String INSERT_EVENT = "INSERT INTO event " +
            "(id, workflow_id, correlation_number, sequence_number, category, class_name, function_name, data, status) " +
            "VALUES (?, ?, ?::bigint, ?::bigint, ?::category, ?, ?, ?::json, ?::status)";

    //language=SQL
    public static final String SELECT_EVENT = "SELECT id, workflow_id, correlation_number, sequence_number, category, class_name, function_name, data, status, timestamp " +
            "FROM event WHERE workflow_id = ? AND category = ?::category AND status = ?::status";

    //language=SQL
    public static final String SELECT_EVENT_BY_CORRELATION_NO = "SELECT id, workflow_id, correlation_number, sequence_number, category, class_name, function_name, data, status, timestamp " +
            "FROM event WHERE workflow_id = ? AND correlation_number= ?::bigint AND status = ?::status";

    //language=SQL
    public static final String MAX_SEQUENCE_NUMBER = "SELECT max(sequence_number) FROM event WHERE workflow_id = ?";

    //language=SQL
    public static final String SELECT_SIGNALS = "SELECT * FROM event " +
            "WHERE workflow_id = ? " +
            "AND category = 'SIGNAL' " +
            "AND sequence_number > (" +
            "SELECT COALESCE(max(sequence_number), 0) " +
            "FROM event " +
            "WHERE status = 'COMPLETED' " +
            "AND workflow_id = ? " +
            "AND sequence_number < ?) " +
            "AND sequence_number < ?";

    //language=SQL
    public static final String SELECT_ABANDONED_WORKFLOWS =
            "SELECT DISTINCT ON (e1.workflow_id) e1.* " +
                    "FROM event e1 " +
                    "WHERE e1.category = 'WORKFLOW' AND e1.status = 'STARTED' " +
                    "  AND NOT EXISTS ( " +
                    "    SELECT 1 " +
                    "    FROM event e2 " +
                    "    WHERE e1.workflow_id = e2.workflow_id " +
                    "      AND ((e2.category = 'WORKFLOW' AND e2.status = 'COMPLETED') OR e2.timestamp > CURRENT_TIMESTAMP - INTERVAL '1 hour') " + // TODO: make this interval customizable
                    ");";
}
