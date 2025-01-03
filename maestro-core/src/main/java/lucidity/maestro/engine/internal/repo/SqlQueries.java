package lucidity.maestro.engine.internal.repo;

public class SqlQueries {
    //language=SQL
    public static final String INSERT_EVENT = """
            INSERT INTO event
                        (id, workflow_id, correlation_number, sequence_number, category, class_name, function_name, data, status, metadata)
                        VALUES (?, ?, ?::bigint, ?::bigint, ?::category, ?, ?, ?::json, ?::status, ?::json)
            """;

    //language=SQL
    public static final String SELECT_EVENT = """
            SELECT * FROM event WHERE workflow_id = ? AND category = ?::category AND status = ?::status
            """;

    //language=SQL
    public static final String SELECT_EVENT_BY_CORRELATION_NO = """
            SELECT * FROM event WHERE workflow_id = ? AND correlation_number= ?::bigint AND status = ?::status
            """;

    //language=SQL
    public static final String SELECT_WORKFLOWS = """
            SELECT started.workflow_id,
                   started.class_name,
                   started.function_name,
                   started.timestamp   AS start_timestamp,
                   completed.timestamp AS end_timestamp,
                   started.data AS input,
                   completed.data AS output
            FROM event AS started LEFT JOIN event AS completed
                 ON started.workflow_id = completed.workflow_id
                     AND completed.category = 'WORKFLOW'
                     AND completed.status = 'COMPLETED'
            WHERE started.category = 'WORKFLOW'
              AND started.status = 'STARTED'
            ORDER BY started.timestamp;
            """;

    //language=SQL
    public static final String SELECT_EVENTS_BY_WORKFLOW_ID = """
            SELECT started.workflow_id,
                   started.category,
                   started.class_name,
                   started.function_name,
                   started.timestamp   AS start_timestamp,
                   completed.timestamp AS end_timestamp,
                   started.data        AS input,
                   completed.data      AS output
            FROM event AS started
                     LEFT JOIN
                 event AS completed
                 ON started.correlation_number = completed.correlation_number
                     AND completed.workflow_id = ?
                     AND completed.status = 'COMPLETED'
            WHERE started.workflow_id = ?
              AND started.status in ('STARTED', 'RECEIVED')
            ORDER BY started.timestamp;
            """;

    //language=SQL
    public static final String MAX_SEQUENCE_NUMBER = "SELECT max(sequence_number) FROM event WHERE workflow_id = ?";

    //language=SQL
    public static final String SELECT_SIGNALS = """
            SELECT *
            FROM event
            WHERE workflow_id = ?
              AND category = 'SIGNAL'
              AND sequence_number > (SELECT COALESCE(max(sequence_number), 0)
                                     FROM event
                                     WHERE status = 'COMPLETED'
                                       AND workflow_id = ?
                                       AND sequence_number < ?)
              AND sequence_number < ?
            """;

    //language=SQL
    public static final String SELECT_TIMED_OUT_EVENTS = """
                    SELECT DISTINCT ON (e1.workflow_id) e1.*
                    FROM event e1
                    WHERE e1.status = 'STARTED' AND e1.category in ('WORKFLOW', 'ACTIVITY') AND (e1.timestamp < CURRENT_TIMESTAMP - (e1.metadata->>'startedToCompletedTimeout')::interval)
                      AND NOT EXISTS (
                        SELECT 1
                        FROM event e2
                        WHERE e1.workflow_id = e2.workflow_id AND e1.correlation_number = e2.correlation_number
                          AND e2.status = 'COMPLETED');
            """;
}
