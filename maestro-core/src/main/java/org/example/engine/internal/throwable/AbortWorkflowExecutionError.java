package org.example.engine.internal.throwable;

public class AbortWorkflowExecutionError extends Error {
    public AbortWorkflowExecutionError(String message) {
        super(message);
    }
}
