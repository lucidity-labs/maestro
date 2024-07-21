package org.example.engine.internal.handler;

import org.example.engine.internal.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Logger;

import static org.example.engine.internal.Util.applySignals;

public record ActivityInvocationHandler(Object target) implements InvocationHandler {
    private static final Logger logger = Logger.getLogger(ActivityInvocationHandler.class.getName());

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Util.shouldSkip(method)) return method.invoke(target, args);

        WorkflowContext workflowContext = WorkflowContextManager.get();
        Long correlationNumber = WorkflowContextManager.getCorrelationNumber();

        EventEntity existingCompletedActivity = EventRepo.get(workflowContext.workflowId(), correlationNumber, Status.COMPLETED);

        if (existingCompletedActivity != null) {
            applySignals(workflowContext, existingCompletedActivity.sequenceNumber());
            if (method.getReturnType().equals(Void.TYPE)) return existingCompletedActivity.outputData();
            return Json.deserialize(existingCompletedActivity.outputData(), method.getReturnType());
        }

        try {
            EventRepo.saveWithRetry(() -> new EventEntity(
                    UUID.randomUUID().toString(), workflowContext.workflowId(),
                    correlationNumber, EventRepo.getNextSequenceNumber(workflowContext.workflowId()), workflowContext.runId(),
                    Category.ACTIVITY, target.getClass().getCanonicalName(), method.getName(),
                    Json.serializeFirst(args), null, Status.STARTED, null
            ));
        } catch (WorkflowCorrelationStatusConflict e) {
            logger.info(e.getMessage());
        }

        EventEntity existingStartedActivity = EventRepo.get(workflowContext.workflowId(), correlationNumber, Status.STARTED);

        Object[] finalArgs = Arrays.stream(method.getParameterTypes())
                .findFirst()
                .map(paramType -> Json.deserialize(existingStartedActivity.inputData(), paramType))
                .map(deserialized -> new Object[]{deserialized})
                .orElse(new Object[]{});

        Object output = method.invoke(target, finalArgs);

        applySignalsAndCompleteActivity(workflowContext, correlationNumber, target, method, output, existingStartedActivity);

        return output;
    }

    private static void applySignalsAndCompleteActivity(
            WorkflowContext workflowContext, Long correlationNumber, Object target,
            Method method, Object output, EventEntity existingStartedActivity
    ) throws Throwable {

        try {
            EventRepo.saveWithRetry(() -> {
                Long nextSequenceNumber = EventRepo.getNextSequenceNumber(workflowContext.workflowId());

                EventEntity eventEntity = new EventEntity(
                        UUID.randomUUID().toString(), workflowContext.workflowId(),
                        correlationNumber, nextSequenceNumber, workflowContext.runId(),
                        Category.ACTIVITY, target.getClass().getCanonicalName(), method.getName(),
                        existingStartedActivity.inputData(), Json.serialize(output),
                        Status.COMPLETED, null
                );

                applySignals(workflowContext, nextSequenceNumber);

                return eventEntity;
            });
        } catch (WorkflowCorrelationStatusConflict e) {
            throw new AbortWorkflowExecutionError("Abandoning workflow execution because of conflict with completed activity " +
                    "with workflowId: " + workflowContext.workflowId() + ", correlationNumber " + correlationNumber);
        }
    }
}
