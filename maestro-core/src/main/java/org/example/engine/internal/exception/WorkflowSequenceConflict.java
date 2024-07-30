package org.example.engine.internal.exception;

public class WorkflowSequenceConflict extends RuntimeException {
    public WorkflowSequenceConflict(String message) {
        super(message);
    }
}
