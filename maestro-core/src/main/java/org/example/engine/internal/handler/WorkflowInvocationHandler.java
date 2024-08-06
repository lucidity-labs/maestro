package org.example.engine.internal.handler;

import org.example.engine.api.signal.SignalFunction;
import org.example.engine.api.workflow.WorkflowFunction;
import org.example.engine.api.workflow.WorkflowOptions;
import org.example.engine.internal.dto.WorkflowContext;
import org.example.engine.internal.dto.WorkflowContextManager;
import org.example.engine.internal.entity.Category;
import org.example.engine.internal.entity.EventEntity;
import org.example.engine.internal.entity.Status;
import org.example.engine.internal.repo.EventRepo;
import org.example.engine.api.throwable.AbortWorkflowExecutionError;
import org.example.engine.internal.exception.WorkflowCorrelationStatusConflict;
import org.example.engine.internal.util.Json;
import org.example.engine.internal.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public record WorkflowInvocationHandler(Object target, WorkflowOptions options) implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(WorkflowInvocationHandler.class);
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (Util.shouldSkip(method)) return method.invoke(target, args);

            if (Util.isAnnotatedWith(method, target, WorkflowFunction.class)) {
                String input = Json.serializeFirst(args);

                WorkflowContextManager.set(new WorkflowContext(options.workflowId(), 0L, null, target));
                Long correlationNumber = WorkflowContextManager.getCorrelationNumber();

                try {
                    EventRepo.saveWithRetry(() -> new EventEntity(
                            UUID.randomUUID().toString(), options.workflowId(),
                            correlationNumber, EventRepo.getNextSequenceNumber(options.workflowId()),
                            Category.WORKFLOW, target.getClass().getSimpleName(), method.getName(),
                            input, Status.STARTED, null, Json.serialize(options)
                    ));
                } catch (WorkflowCorrelationStatusConflict e) {
                    logger.debug(e.getMessage());
                }

                Object output = method.invoke(target, args);

                try {
                    EventRepo.saveWithRetry(() -> new EventEntity(
                            UUID.randomUUID().toString(), options.workflowId(),
                            correlationNumber, EventRepo.getNextSequenceNumber(options.workflowId()),
                            Category.WORKFLOW, target.getClass().getSimpleName(), method.getName(),
                            Json.serialize(output), Status.COMPLETED, null, null
                    ));
                } catch (WorkflowCorrelationStatusConflict e) {
                    logger.debug(e.getMessage());
                } finally {
                    WorkflowContextManager.clear();
                }

                return output;
            } else if (Util.isAnnotatedWith(method, target, SignalFunction.class)) {
                EventRepo.saveWithRetry(() -> new EventEntity(
                        UUID.randomUUID().toString(), options.workflowId(),
                        null, EventRepo.getNextSequenceNumber(options.workflowId()),
                        Category.SIGNAL, target.getClass().getSimpleName(), method.getName(),
                        Json.serializeFirst(args), Status.RECEIVED, null, null
                ));

                EventEntity existingStartedWorkflow = EventRepo.get(
                        options.workflowId(), Category.WORKFLOW, Status.STARTED
                );

                if (existingStartedWorkflow != null) {
                    Method workflowMethod = Util.findWorkflowMethod(proxy.getClass());

                    Type[] paramTypes = workflowMethod.getGenericParameterTypes();
                    Object[] finalArgs = Arrays.stream(paramTypes)
                            .findFirst()
                            .map(paramType -> Json.deserialize(existingStartedWorkflow.data(), paramType))
                            .map(deserialized -> new Object[]{deserialized})
                            .orElse(Util.getDefaultArgs(paramTypes.length));

                    executor.submit(() -> workflowMethod.invoke(proxy, finalArgs));
                }

                return null;
            } else {
                return method.invoke(target, args);
            }
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof AbortWorkflowExecutionError) return null;
            else throw e;
        }
    }
}
