package org.example.engine.internal;

public class WorkflowContextManager {

    private static final ThreadLocal<WorkflowContext> workflowContextThreadLocal = new ThreadLocal<>();

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
