package lucidity.maestro.engine.internal.handler;

import lucidity.maestro.engine.api.activity.ActivityOptions;
import lucidity.maestro.engine.api.throwable.AbortWorkflowExecutionError;
import lucidity.maestro.engine.internal.dto.WorkflowContext;
import lucidity.maestro.engine.internal.dto.WorkflowContextManager;
import lucidity.maestro.engine.internal.entity.Category;
import lucidity.maestro.engine.internal.entity.EventEntity;
import lucidity.maestro.engine.internal.entity.Status;
import lucidity.maestro.engine.internal.repo.EventRepo;
import lucidity.maestro.engine.internal.exception.WorkflowCorrelationStatusConflict;
import lucidity.maestro.engine.internal.util.Json;
import lucidity.maestro.engine.internal.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.UUID;

import static lucidity.maestro.engine.internal.util.Util.applySignals;

public record ActivityInvocationHandler(Object target, ActivityOptions options) implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(ActivityInvocationHandler.class);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Util.shouldSkip(method)) return method.invoke(target, args);

        WorkflowContext workflowContext = WorkflowContextManager.get();
        Long correlationNumber = WorkflowContextManager.getCorrelationNumber();

        EventEntity existingCompletedActivity = EventRepo.get(workflowContext.workflowId(), correlationNumber, Status.COMPLETED);

        if (existingCompletedActivity != null) {
            applySignals(workflowContext, existingCompletedActivity.sequenceNumber());
            if (method.getReturnType().equals(Void.TYPE)) return existingCompletedActivity.data();
            return Json.deserialize(existingCompletedActivity.data(), method.getGenericReturnType());
        }

        try {
            EventRepo.saveWithRetry(() -> new EventEntity(
                    UUID.randomUUID().toString(), workflowContext.workflowId(),
                    correlationNumber, EventRepo.getNextSequenceNumber(workflowContext.workflowId()),
                    Category.ACTIVITY, target.getClass().getSimpleName(), method.getName(),
                    Json.serializeFirst(args), Status.STARTED, null, Json.serialize(options)
            ));
        } catch (WorkflowCorrelationStatusConflict e) {
            logger.debug(e.getMessage());
        }

        EventEntity existingStartedActivity = EventRepo.get(workflowContext.workflowId(), correlationNumber, Status.STARTED);

        Type[] paramTypes = method.getGenericParameterTypes();
        Object[] finalArgs = Arrays.stream(paramTypes)
                .findFirst()
                .map(paramType -> Json.deserialize(existingStartedActivity.data(), paramType))
                .map(deserialized -> new Object[]{deserialized})
                .orElse(Util.getDefaultArgs(paramTypes.length));

        Object output = method.invoke(target, finalArgs);

        applySignalsAndCompleteActivity(workflowContext, correlationNumber, target, method, output);

        return output;
    }

    private static void applySignalsAndCompleteActivity(
            WorkflowContext workflowContext, Long correlationNumber,
            Object target, Method method, Object output
    ) {
        try {
            EventRepo.saveWithRetry(() -> {
                Long nextSequenceNumber = EventRepo.getNextSequenceNumber(workflowContext.workflowId());

                EventEntity eventEntity = new EventEntity(
                        UUID.randomUUID().toString(), workflowContext.workflowId(),
                        correlationNumber, nextSequenceNumber, Category.ACTIVITY,
                        target.getClass().getSimpleName(), method.getName(),
                        Json.serialize(output), Status.COMPLETED, null, null
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
