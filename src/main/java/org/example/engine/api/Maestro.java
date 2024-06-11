package org.example.engine.api;

import org.example.engine.internal.*;
import org.example.mymarketingapp.workflow.MyWorkflow;
import org.postgresql.util.PSQLException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class Maestro {
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
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
        Class<?> interfaceClazz = Arrays.stream(clazz.getInterfaces()).findFirst().get();

        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{interfaceClazz},
                new WorkflowInvocationHandler(instance, options)
        );
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

    private static void populateAnnotatedFields(Object instance) throws Exception {
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Activity.class)) {
                field.setAccessible(true);
                field.set(instance, getActivity(field.getType()));
            }
        }
    }

    private record WorkflowInvocationHandler(Object target, WorkflowOptions options) implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Util.isAnnotatedWith(method, target, WorkflowFunction.class)) {
                String runId = UUID.randomUUID().toString();
                String input = Json.serializeFirst(args);

                WorkflowContextManager.set(new WorkflowContext(options.workflowId(), runId, 0L, target));

                // TODO: this doesn't support new workflow executions with new run id?
                Repo.saveIgnoringConflict(new EventEntity(
                        UUID.randomUUID().toString(), options.workflowId(),
                        WorkflowContextManager.incrementAndGetSequenceNumber(), runId,
                        Entity.WORKFLOW, target.getClass().getSimpleName(), method.getName(),
                        input, null, Status.STARTED, null
                ));

                Object output = method.invoke(target, args);

                Repo.saveIgnoringConflict(new EventEntity(
                        UUID.randomUUID().toString(), options.workflowId(),
                        WorkflowContextManager.incrementAndGetSequenceNumber(), runId,
                        Entity.WORKFLOW, target.getClass().getSimpleName(), method.getName(),
                        input, Json.serialize(output), Status.COMPLETED, null
                ));

                WorkflowContextManager.clear();
                return output;
            }
            if (Util.isAnnotatedWith(method, target, SignalFunction.class)) {
                // TODO: what should the sequence number be for signals?
                Repo.saveIgnoringConflict(new EventEntity(
                        UUID.randomUUID().toString(), options.workflowId(),
                        1L, null,
                        Entity.SIGNAL, target.getClass().getSimpleName(), method.getName(),
                        Json.serializeFirst(args), null, Status.RECEIVED, null
                ));

                EventEntity existingStartedWorkflow = Repo.get(
                        options.workflowId(), target.getClass().getSimpleName(), method.getName(),
                        1L, Status.STARTED
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
            }

            return method.invoke(target, args);
        }
    }

    private record ActivityInvocationHandler(Object target) implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            WorkflowContext workflowContext = WorkflowContextManager.get();
            Long sequenceNumber = WorkflowContextManager.incrementAndGetSequenceNumber();

            EventEntity existingCompletedActivity = Repo.get(
                    workflowContext.workflowId(), target.getClass().getSimpleName(), method.getName(),
                    sequenceNumber, Status.COMPLETED
            );
            if (existingCompletedActivity != null) {
                if (method.getReturnType().equals(Void.TYPE)) return existingCompletedActivity.outputData();
                return Json.deserialize(existingCompletedActivity.outputData(), method.getReturnType());
            }

            Repo.saveIgnoringConflict(new EventEntity(
                    UUID.randomUUID().toString(), workflowContext.workflowId(),
                    sequenceNumber, workflowContext.runId(),
                    Entity.ACTIVITY, target.getClass().getSimpleName(), method.getName(),
                    Json.serializeFirst(args), null, Status.STARTED, null
            ));

            EventEntity existingStartedActivity = Repo.get(
                    workflowContext.workflowId(), target.getClass().getSimpleName(), method.getName(),
                    sequenceNumber, Status.STARTED
            );

            Object[] finalArgs = Arrays.stream(method.getParameterTypes())
                    .findFirst()
                    .map(paramType -> Json.deserialize(existingStartedActivity.inputData(), paramType))
                    .map(deserialized -> new Object[]{deserialized})
                    .orElse(new Object[]{});

            Object output = method.invoke(target, finalArgs);

            try {
                Repo.save(new EventEntity(
                        UUID.randomUUID().toString(), workflowContext.workflowId(),
                        sequenceNumber, workflowContext.runId(),
                        Entity.ACTIVITY, target.getClass().getSimpleName(), method.getName(),
                        existingStartedActivity.inputData(), Json.serialize(output),
                        Status.COMPLETED, null
                ));
            } catch (PSQLException e) {
                if ("23505".equals(e.getSQLState())) {
                    throw new ConflictException("Abandoning workflow execution because of conflict with completed activity " +
                            "with workflowId: " + workflowContext.workflowId() + ", sequenceNumber " + sequenceNumber
                            + ", className: " + target.getClass().getSimpleName() + ", functionName: " + method.getName());
                }
            }

            return output;
        }
    }
}
