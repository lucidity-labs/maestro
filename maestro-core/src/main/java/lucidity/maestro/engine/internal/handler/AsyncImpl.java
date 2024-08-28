package lucidity.maestro.engine.internal.handler;

import lucidity.maestro.engine.internal.dto.WorkflowContextManager;
import lucidity.maestro.engine.internal.dto.WorkflowContext;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AsyncImpl {

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
