package lucidity.maestro.engine.api.throwable;

public class UnregisteredWorkflowException extends RuntimeException {
    public UnregisteredWorkflowException(Class<?> unregisteredWorkflowClass) {
        super("Workflow implementation with type " + unregisteredWorkflowClass.getSimpleName() + "hasn't been registered");
    }
}
