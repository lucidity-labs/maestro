package org.example.engine.internal;

public class WorkflowCorrelationStatusConflict extends RuntimeException {
    public WorkflowCorrelationStatusConflict(String message) {
        super(message);
    }
}
