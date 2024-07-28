package org.example.engine.internal.handler;

import org.example.engine.internal.WorkflowContext;
import org.example.engine.internal.WorkflowContextManager;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class Async {

    public static <T> CompletableFuture<T> function(Supplier<T> supplier) {
        WorkflowContext existingWorkflowContext = WorkflowContextManager.get();

        WorkflowContext newWorkflowContext = new WorkflowContext(
                existingWorkflowContext.workflowId(), null,
                WorkflowContextManager.getCorrelationNumber(), existingWorkflowContext.workflow()
        );


        return CompletableFuture.supplyAsync(() -> {
            WorkflowContextManager.set(newWorkflowContext);
            T result = supplier.get();
            WorkflowContextManager.clear();
            return result;
        });
    }
}
