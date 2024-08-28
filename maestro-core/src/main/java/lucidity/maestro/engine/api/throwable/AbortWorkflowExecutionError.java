package lucidity.maestro.engine.api.throwable;

public class AbortWorkflowExecutionError extends Error {
    public AbortWorkflowExecutionError(String message) {
        super(message);
    }
}
