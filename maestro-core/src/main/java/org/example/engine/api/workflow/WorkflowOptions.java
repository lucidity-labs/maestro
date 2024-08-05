package org.example.engine.api.workflow;

import java.time.Duration;

public record WorkflowOptions(String workflowId, Duration startedToCompletedTimeout) {

    public WorkflowOptions(String workflowId) {
        this(workflowId, Duration.ofHours(1));
    }
}
