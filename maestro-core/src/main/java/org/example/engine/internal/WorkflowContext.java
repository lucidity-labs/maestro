package org.example.engine.internal;

public record WorkflowContext(
        String workflowId,
        String runId,
        Long mutableCorrelationNumber,
        Long staticCorrelationNumber,
        Object workflow
) {

    public WorkflowContext incrementCorrelationNumber() {
        return new WorkflowContext(workflowId, runId, mutableCorrelationNumber + 1, staticCorrelationNumber, workflow);
    }
}
