package org.example.engine.internal;

public class WorkflowContextManager {

    private static final ThreadLocal<WorkflowContext> workflowContextThreadLocal = new ThreadLocal<>();

    public static Long incrementAndGetSequenceNumber() {
        WorkflowContext currentContext = workflowContextThreadLocal.get();
        WorkflowContext newContext = currentContext.incrementSequenceNumber();
        workflowContextThreadLocal.set(newContext);
        return newContext.sequenceNumber();
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
}
