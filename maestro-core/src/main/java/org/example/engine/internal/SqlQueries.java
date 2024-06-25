package org.example.engine.internal;

public class SqlQueries {
    //language=SQL
    public static final String INSERT_EVENT = "INSERT INTO event " +
            "(id, workflow_id, correlation_number, sequence_number, run_id, category, class_name, function_name, input_data, output_data, status) " +
            "VALUES (?, ?, ?::bigint, ?::bigint, ?, ?::category, ?, ?, ?::json, ?::json, ?::status)";

    //language=SQL
    public static final String SELECT_EVENT = "SELECT id, workflow_id, correlation_number, sequence_number, run_id, category, class_name, function_name, input_data, output_data, status, created_at " +
            "FROM event WHERE workflow_id = ? AND category = ?::category AND status = ?::status";

    //language=SQL
    public static final String SELECT_EVENT_BY_CORRELATION_NO = "SELECT id, workflow_id, correlation_number, sequence_number, run_id, category, class_name, function_name, input_data, output_data, status, created_at " +
            "FROM event WHERE workflow_id = ? AND correlation_number= ?::bigint AND status = ?::status";

    //language=SQL
    public static final String MAX_SEQUENCE_NUMBER = "SELECT max(sequence_number) FROM event WHERE workflow_id = ?";

    //language=SQL
    public static final String SELECT_SIGNALS = "SELECT * from event " +
            "WHERE workflow_id = ? " +
            "AND category = 'SIGNAL' " +
            "AND sequence_number > (" +
            "SELECT COALESCE(max(sequence_number), 0) " +
            "FROM event " +
            "WHERE status = 'COMPLETED' " +
            "AND workflow_id = ? " +
            "AND sequence_number < ?) " +
            "AND sequence_number < ?";
}