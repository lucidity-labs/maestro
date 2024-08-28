package lucidity.maestro.engine.internal.exception;

public class WorkflowCorrelationStatusConflict extends RuntimeException {
    public WorkflowCorrelationStatusConflict(String message) {
        super(message);
    }
}
