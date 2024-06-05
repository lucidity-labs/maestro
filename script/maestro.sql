DROP TABLE IF EXISTS event;

CREATE TYPE status AS ENUM ('STARTED', 'COMPLETED', 'FAILED');
CREATE TYPE entity AS ENUM ('WORKFLOW', 'ACTIVITY', 'SIGNAL');

CREATE TABLE event
(
    id VARCHAR PRIMARY KEY,
    workflow_id VARCHAR NOT NULL,
    run_id VARCHAR NOT NULL,
    entity entity NOT NULL,
    class_name VARCHAR,
    function_name VARCHAR,
    input_data JSON,
    output_data JSON,
    status status NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);