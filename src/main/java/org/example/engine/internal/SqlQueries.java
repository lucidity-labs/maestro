package org.example.engine.internal;

public class SqlQueries {
    //language=SQL
    public static final String INSERT_QUERY = "INSERT INTO event " +
            "(id, workflow_id, correlation_number, sequence_number, run_id, entity, class_name, function_name, input_data, output_data, status) " +
            "VALUES (?, ?, ?::bigint, ?::bigint, ?, ?::entity, ?, ?, ?::json, ?::json, ?::status)";
}
