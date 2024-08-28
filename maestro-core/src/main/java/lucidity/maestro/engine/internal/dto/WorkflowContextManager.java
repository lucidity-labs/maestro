package lucidity.maestro.engine.internal.dto;

public class WorkflowContextManager {

    private static final ThreadLocal<WorkflowContext> workflowContextThreadLocal = new ThreadLocal<>();

    public static Long getCorrelationNumber() {
        WorkflowContext workflowContext = workflowContextThreadLocal.get();
        if (workflowContext.staticCorrelationNumber() != null) return workflowContext.staticCorrelationNumber();
        return incrementAndGetCorrelationNumber();
    }

    public static WorkflowContext get() {
        return workflowContextThreadLocal.get();
    }

    public static void set(WorkflowContext context) {
        workflowContextThreadLocal.set(context);
    }

    public static void clear() {
        workflowContextThreadLocal.remove();
    }

    private static Long incrementAndGetCorrelationNumber() {
        WorkflowContext currentContext = workflowContextThreadLocal.get();
        WorkflowContext newContext = currentContext.incrementCorrelationNumber();
        workflowContextThreadLocal.set(newContext);
        return newContext.mutableCorrelationNumber();
    }
}
