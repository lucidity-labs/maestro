package org.example.engine.internal;

public class WorkflowSequenceConflict extends RuntimeException {
    public WorkflowSequenceConflict(String message) {
        super(message);
    }
}
