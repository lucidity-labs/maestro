DROP TABLE IF EXISTS event;

CREATE TYPE status AS ENUM ('STARTED', 'COMPLETED', 'FAILED', 'RECEIVED', 'UNSATISFIED');
CREATE TYPE category AS ENUM ('WORKFLOW', 'ACTIVITY', 'SIGNAL', 'AWAIT', 'SLEEP');

CREATE TABLE event
(
    id                 VARCHAR PRIMARY KEY,
    workflow_id        VARCHAR   NOT NULL,
    correlation_number BIGINT,
    sequence_number    BIGINT    NOT NULL,
    category           category    NOT NULL,
    class_name         VARCHAR,
    function_name      VARCHAR,
    data               JSON,
    status             status    NOT NULL,
    timestamp         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
--TODO: add metadata column allowing us to store necessary data for each event such as serialized WorkflowOptions for WORKFLOW STARTED?

CREATE UNIQUE INDEX event_unique_workflow_correlation_status
    ON event (workflow_id, correlation_number, status);

CREATE UNIQUE INDEX event_unique_workflow_sequence
    ON event (workflow_id, sequence_number);

-- TODO: add indexes


-- table required by db-scheduler
create table scheduled_tasks (
                                 task_name text not null,
                                 task_instance text not null,
                                 task_data bytea,
                                 execution_time timestamp with time zone not null,
                                 picked BOOLEAN not null,
                                 picked_by text,
                                 last_success timestamp with time zone,
                                 last_failure timestamp with time zone,
                                 consecutive_failures INT,
                                 last_heartbeat timestamp with time zone,
                                 version BIGINT not null,
                                 PRIMARY KEY (task_name, task_instance)
);

CREATE INDEX execution_time_idx ON scheduled_tasks (execution_time);
CREATE INDEX last_heartbeat_idx ON scheduled_tasks (last_heartbeat);