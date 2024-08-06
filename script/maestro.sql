DROP TABLE IF EXISTS event;

CREATE TYPE status AS ENUM ('STARTED', 'COMPLETED', 'FAILED', 'RECEIVED', 'UNSATISFIED');
CREATE TYPE category AS ENUM ('WORKFLOW', 'ACTIVITY', 'SIGNAL', 'AWAIT', 'SLEEP');

CREATE TABLE event
(
    id                 VARCHAR PRIMARY KEY,
    workflow_id        VARCHAR   NOT NULL,
    category           category  NOT NULL,
    status             status    NOT NULL,
    data               JSON,
    class_name         VARCHAR,
    function_name      VARCHAR,
    timestamp          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    correlation_number BIGINT,
    sequence_number    BIGINT    NOT NULL,
    metadata           JSON
);

CREATE UNIQUE INDEX event_unique_workflow_correlation_status ON event (workflow_id, correlation_number, status);
CREATE UNIQUE INDEX event_unique_workflow_sequence ON event (workflow_id, sequence_number);
CREATE INDEX idx_workflow_category_status ON event (workflow_id, category, status);
CREATE INDEX idx_workflow_correlation_status ON event (workflow_id, correlation_number, status);
CREATE INDEX idx_workflow_status_sequence ON event (workflow_id, status, sequence_number);
CREATE INDEX idx_workflow_category_sequence ON event (workflow_id, category, sequence_number);
CREATE INDEX idx_workflow_category_status_timestamp ON event (workflow_id, category, status, timestamp);


-- table required by db-scheduler
create table scheduled_tasks
(
    task_name            TEXT                     NOT NULL,
    task_instance        TEXT                     NOT NULL,
    task_data            bytea,
    execution_time       TIMESTAMP WITH TIME ZONE NOT NULL,
    picked               BOOLEAN                  NOT NULL,
    picked_by            TEXT,
    last_success         TIMESTAMP WITH TIME ZONE,
    last_failure         TIMESTAMP WITH TIME ZONE,
    consecutive_failures INT,
    last_heartbeat       TIMESTAMP WITH TIME ZONE,
    version              BIGINT                   NOT NULL,
    PRIMARY KEY (task_name, task_instance)
);

CREATE INDEX execution_time_idx ON scheduled_tasks (execution_time);
CREATE INDEX last_heartbeat_idx ON scheduled_tasks (last_heartbeat);