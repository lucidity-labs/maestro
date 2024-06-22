package org.example.engine.api;

import org.example.engine.internal.*;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.example.engine.internal.Util.applySignals;

public class Maestro {
    private static final Map<Class<?>, Object> typeToActivity = new HashMap<>();
    private static final java.util.logging.Logger logger = Logger.getLogger(Maestro.class.getName());

    // TODO: maybe expose another method accepting activity options as second param
    public static void registerActivity(Object activity) {
        typeToActivity.put(getInterface(activity.getClass()), proxyActivity(activity));
    }

    @SuppressWarnings("unchecked")
    public static <T> T newWorkflow(Class<T> clazz, WorkflowOptions options) throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();
        populateAnnotatedFields(instance);
        Class<?> interfaceClass = Arrays.stream(clazz.getInterfaces()).findFirst().get();

        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new WorkflowInvocationHandler(instance, options)
        );
    }

    public static void await(Supplier<Boolean> condition) throws Throwable {
        WorkflowContext workflowContext = WorkflowContextManager.get();
        Long correlationNumber = WorkflowContextManager.incrementAndGetCorrelationNumber();

        EventEntity existingCompletedAwait = Repo.get(
                workflowContext.workflowId(), null, null,
                correlationNumber, Status.COMPLETED
        );
        if (existingCompletedAwait != null) {
            applySignals(workflowContext, existingCompletedAwait.sequenceNumber());
            return;
        }

        try {
            Repo.saveWithRetry(new EventEntity(
                    UUID.randomUUID().toString(), workflowContext.workflowId(),
                    correlationNumber, Repo.getNextSequenceNumber(workflowContext.workflowId()), workflowContext.runId(),
                    Category.AWAIT, null, null,
                    null, null, Status.STARTED, null
            ));
        } catch (WorkflowCorrelationStatusConflict e) {
            logger.info(e.getMessage());
        }

        Long nextSequenceNumber = Repo.getNextSequenceNumber(workflowContext.workflowId());
        applySignals(workflowContext, nextSequenceNumber);

        if (!condition.get()) {
            // TODO: insert event indicating condition wasn't satisfied so that poll doesn't pick it up as abandoned workflow
            throw new AbortWorkflowExecutionError("Abandoning workflow execution because of await condition wasn't satisfied " +
                    "with workflowId: " + workflowContext.workflowId() + ", correlationNumber " + correlationNumber);
        }

        try {
            Repo.save(new EventEntity(
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

    private static <T> T getActivity(Class<T> activityType) {
        return activityType.cast(typeToActivity.get(activityType));
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxyActivity(T instance) {
        return (T) Proxy.newProxyInstance(
                instance.getClass().getClassLoader(),
                new Class<?>[]{getInterface(instance.getClass())},
                new ActivityInvocationHandler(instance)
        );
    }

    private static Class<?> getInterface(Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();

        // TODO: let's instead require the user annotate the activity interface with our custom annotation
        if (interfaces.length != 1)
            throw new IllegalArgumentException("The class must implement exactly one interface");

        return interfaces[0];
    }

    private static void populateAnnotatedFields(Object instance) throws IllegalAccessException {
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Activity.class)) {
                field.setAccessible(true);
                field.set(instance, getActivity(field.getType()));
            }
        }
    }
}
