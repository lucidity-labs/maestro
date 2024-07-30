package org.example.engine.internal.throwable;

public class WorkflowSequenceConflict extends RuntimeException {
    public WorkflowSequenceConflict(String message) {
        super(message);
    }
}
