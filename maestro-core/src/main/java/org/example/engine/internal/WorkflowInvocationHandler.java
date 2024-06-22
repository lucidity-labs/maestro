package org.example.engine.internal;

import org.example.engine.api.SignalFunction;
import org.example.engine.api.WorkflowFunction;
import org.example.engine.api.WorkflowOptions;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public record WorkflowInvocationHandler(Object target, WorkflowOptions options) implements InvocationHandler {
    private static final java.util.logging.Logger logger = Logger.getLogger(WorkflowInvocationHandler.class.getName());
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Util.isAnnotatedWith(method, target, WorkflowFunction.class)) {
            String runId = UUID.randomUUID().toString();
            String input = Json.serializeFirst(args);

            WorkflowContextManager.set(new WorkflowContext(options.workflowId(), runId, 0L, target));
            Long correlationNumber = WorkflowContextManager.incrementAndGetCorrelationNumber();

            try {
                Repo.saveWithRetry(new EventEntity(
                        UUID.randomUUID().toString(), options.workflowId(),
                        correlationNumber, Repo.getNextSequenceNumber(options.workflowId()), runId,
                        Entity.WORKFLOW, target.getClass().getSimpleName(), method.getName(),
                        input, null, Status.STARTED, null
                ));
            } catch (WorkflowCorrelationStatusConflict e) {
                logger.info(e.getMessage());
            }

            Object output = method.invoke(target, args);

            try {
                Repo.saveWithRetry(new EventEntity(
                        UUID.randomUUID().toString(), options.workflowId(),
                        correlationNumber, Repo.getNextSequenceNumber(options.workflowId()), runId,
                        Entity.WORKFLOW, target.getClass().getSimpleName(), method.getName(),
                        input, Json.serialize(output), Status.COMPLETED, null
                ));
            } catch (WorkflowCorrelationStatusConflict e) {
                logger.info(e.getMessage());
            } finally {
                WorkflowContextManager.clear();
            }

            return output;
        } else if (Util.isAnnotatedWith(method, target, SignalFunction.class)) {
            Repo.saveWithRetry(new EventEntity(
                    UUID.randomUUID().toString(), options.workflowId(),
                    null, Repo.getNextSequenceNumber(options.workflowId()), null,
                    Entity.SIGNAL, target.getClass().getSimpleName(), method.getName(),
                    Json.serializeFirst(args), null, Status.RECEIVED, null
            ));

            EventEntity existingStartedWorkflow = Repo.get(
                    options.workflowId(), target.getClass().getSimpleName(), Status.STARTED
            );

            if (existingStartedWorkflow != null) {
                Method workflowMethod = Util.findWorkflowMethod(proxy.getClass());

                Object[] finalArgs = Arrays.stream(workflowMethod.getParameterTypes())
                        .findFirst()
                        .map(paramType -> Json.deserialize(existingStartedWorkflow.inputData(), paramType))
                        .map(deserialized -> new Object[]{deserialized})
                        .orElse(new Object[]{});

                executor.submit(() -> workflowMethod.invoke(proxy, finalArgs));
            }

            return null;
        } else {
            return method.invoke(target, args);
        }
    }
}
