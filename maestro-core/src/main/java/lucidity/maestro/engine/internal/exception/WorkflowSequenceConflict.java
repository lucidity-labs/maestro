package lucidity.maestro.engine.internal.exception;

public class WorkflowSequenceConflict extends RuntimeException {
    public WorkflowSequenceConflict(String message) {
        super(message);
    }
}
