package org.example.engine.internal;

public record WorkflowContext(
        String workflowId,
        String runId,
        Long correlationNumber,
        Object workflow
) {

    public WorkflowContext incrementCorrelationNumber() {
        return new WorkflowContext(workflowId, runId, correlationNumber + 1, workflow);
    }
}
