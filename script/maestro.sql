DROP TABLE IF EXISTS event;

CREATE TYPE status AS ENUM ('STARTED', 'COMPLETED', 'FAILED', 'RECEIVED');
CREATE TYPE entity AS ENUM ('WORKFLOW', 'ACTIVITY', 'SIGNAL');

CREATE TABLE event
(
    id                 VARCHAR PRIMARY KEY,
    workflow_id        VARCHAR   NOT NULL,
    correlation_number BIGINT,
    sequence_number    BIGINT    NOT NULL,
    run_id             VARCHAR,
    entity             entity    NOT NULL,
    class_name         VARCHAR,
    function_name      VARCHAR,
    input_data         JSON,
    output_data        JSON,
    status             status    NOT NULL,
    created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX event_unique_workflow_correlation_status
    ON event (workflow_id, correlation_number, status);

CREATE UNIQUE INDEX event_unique_workflow_sequence
    ON event (workflow_id, sequence_number);

-- TODO: this needs indexes