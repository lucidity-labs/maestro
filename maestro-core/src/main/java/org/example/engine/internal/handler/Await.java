package org.example.engine.internal.handler;

import org.example.engine.internal.*;

import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.example.engine.internal.Util.applySignals;

public class Await {
    private static final java.util.logging.Logger logger = Logger.getLogger(Await.class.getName());

    public static void await(Supplier<Boolean> condition) {
        WorkflowContext workflowContext = WorkflowContextManager.get();
        Long correlationNumber = WorkflowContextManager.getCorrelationNumber();

        EventEntity existingCompletedAwait = EventRepo.get(workflowContext.workflowId(), correlationNumber, Status.COMPLETED);
        if (existingCompletedAwait != null) {
            applySignals(workflowContext, existingCompletedAwait.sequenceNumber());
            return;
        }

        try {
            EventRepo.saveWithRetry(() -> new EventEntity(
                    UUID.randomUUID().toString(), workflowContext.workflowId(),
                    correlationNumber, EventRepo.getNextSequenceNumber(workflowContext.workflowId()), workflowContext.runId(),
                    Category.AWAIT, null, null,
                    null, null, Status.STARTED, null
            ));
        } catch (WorkflowCorrelationStatusConflict e) {
            logger.info(e.getMessage());
        }

        Long nextSequenceNumber = EventRepo.getNextSequenceNumber(workflowContext.workflowId());
        applySignals(workflowContext, nextSequenceNumber);

        if (!condition.get()) {
            // TODO: insert event indicating condition wasn't satisfied so that poll doesn't pick it up as abandoned workflow
            throw new AbortWorkflowExecutionError("Abandoning workflow execution because of await condition wasn't satisfied " +
                    "with workflowId: " + workflowContext.workflowId() + ", correlationNumber " + correlationNumber);
        }

        try {
            EventRepo.saveWithRetry(() -> new EventEntity(
                    UUID.randomUUID().toString(), workflowContext.workflowId(),
                    correlationNumber, nextSequenceNumber, workflowContext.runId(),
                    Category.AWAIT, null, null, null,
                    null, Status.COMPLETED, null
            ));
        } catch (WorkflowCorrelationStatusConflict e) {
            throw new AbortWorkflowExecutionError("Abandoning workflow execution because of conflict with completed activity " +
                    "with workflowId: " + workflowContext.workflowId() + ", correlationNumber " + correlationNumber);
        }
    }
}
