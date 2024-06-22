package org.example.engine.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public record ActivityInvocationHandler(Object target) implements InvocationHandler {
    private static final java.util.logging.Logger logger = Logger.getLogger(ActivityInvocationHandler.class.getName());

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        WorkflowContext workflowContext = WorkflowContextManager.get();
        Long correlationNumber = WorkflowContextManager.incrementAndGetCorrelationNumber();

        EventEntity existingCompletedActivity = Repo.get(
                workflowContext.workflowId(), target.getClass().getSimpleName(), method.getName(),
                correlationNumber, Status.COMPLETED
        );
        if (existingCompletedActivity != null) {
            applySignals(workflowContext, existingCompletedActivity.sequenceNumber());
            if (method.getReturnType().equals(Void.TYPE)) return existingCompletedActivity.outputData();
            return Json.deserialize(existingCompletedActivity.outputData(), method.getReturnType());
        }

        try {
            Repo.saveWithRetry(new EventEntity(
                    UUID.randomUUID().toString(), workflowContext.workflowId(),
                    correlationNumber, Repo.getNextSequenceNumber(workflowContext.workflowId()), workflowContext.runId(),
                    Entity.ACTIVITY, target.getClass().getSimpleName(), method.getName(),
                    Json.serializeFirst(args), null, Status.STARTED, null
            ));
        } catch (WorkflowCorrelationStatusConflict e) {
            logger.info(e.getMessage());
        }

        EventEntity existingStartedActivity = Repo.get(
                workflowContext.workflowId(), target.getClass().getSimpleName(), method.getName(),
                correlationNumber, Status.STARTED
        );

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
    ) throws SQLException, WorkflowSequenceConflict, InvocationTargetException, IllegalAccessException {
        try {
            Long nextSequenceNumber = Repo.getNextSequenceNumber(workflowContext.workflowId());
            applySignals(workflowContext, nextSequenceNumber);

            Repo.save(new EventEntity(
                    UUID.randomUUID().toString(), workflowContext.workflowId(),
                    correlationNumber, nextSequenceNumber, workflowContext.runId(),
                    Entity.ACTIVITY, target.getClass().getSimpleName(), method.getName(),
                    existingStartedActivity.inputData(), Json.serialize(output),
                    Status.COMPLETED, null
            ));
        } catch (WorkflowCorrelationStatusConflict e) {
            throw new AbortWorkflowExecutionError("Abandoning workflow execution because of conflict with completed activity " +
                    "with workflowId: " + workflowContext.workflowId() + ", correlationNumber " + correlationNumber);
        }
    }

    private static void applySignals(WorkflowContext workflowContext, Long nextSequenceNumber) throws InvocationTargetException, IllegalAccessException {
        Object workflow = workflowContext.workflow();
        List<EventEntity> signals = Repo.getSignals(workflowContext.workflowId(), nextSequenceNumber);
        for (EventEntity signal : signals) {
            Method signalMethod = Arrays.stream(workflow.getClass().getMethods())
                    .filter(m -> m.getName().equals(signal.functionName()))
                    .findFirst().get();

            Object[] finalArgs = Arrays.stream(signalMethod.getParameterTypes())
                    .findFirst()
                    .map(paramType -> Json.deserialize(signal.inputData(), paramType))
                    .map(deserialized -> new Object[]{deserialized})
                    .orElse(new Object[]{});

            signalMethod.invoke(workflow, finalArgs);
        }
    }
}
