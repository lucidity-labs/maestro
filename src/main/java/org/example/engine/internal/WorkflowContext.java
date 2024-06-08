package org.example.engine.internal;

public record WorkflowContext(
        String workflowId,
        String runId,
        Long sequenceNumber,
        Object workflow
) {

    public WorkflowContext incrementSequenceNumber() {
        return new WorkflowContext(workflowId, runId, sequenceNumber + 1, workflow);
    }
}
