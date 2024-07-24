package org.example.engine.internal.handler;

import org.example.engine.internal.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.function.Supplier;

import static org.example.engine.internal.Util.applySignals;

public class Await {
    private static final Logger logger = LoggerFactory.getLogger(Await.class);

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
            EventRepo.saveWithRetry(() -> new EventEntity(
                    UUID.randomUUID().toString(), workflowContext.workflowId(),
                    correlationNumber, EventRepo.getNextSequenceNumber(workflowContext.workflowId()), workflowContext.runId(),
                    Category.AWAIT, null, null,
                    null, null, Status.UNSATISFIED, null
            ));

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
