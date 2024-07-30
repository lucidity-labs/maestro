package org.example.engine.internal.throwable;

public class WorkflowCorrelationStatusConflict extends RuntimeException {
    public WorkflowCorrelationStatusConflict(String message) {
        super(message);
    }
}
