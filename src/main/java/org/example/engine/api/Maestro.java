package org.example.engine.api;

import org.example.engine.internal.*;
import org.example.mymarketingapp.workflow.Workflow;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Maestro {
    private static final Map<Class<?>, Object> typeToActivity = new HashMap<>();

    // TODO: maybe expose another method accepting activity options as second param
    public static void registerActivity(Object activity) {
        typeToActivity.put(getInterface(activity.getClass()), proxyActivity(activity));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Workflow<?>> T newWorkflow(Class<T> clazz, WorkflowOptions options) throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();
        populateAnnotatedFields(instance);

        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{Workflow.class},
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

        private static final ThreadLocal<WorkflowContext> workflowContextThreadLocal = new ThreadLocal<>();

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Util.isAnnotatedWith(method, target, WorkflowFunction.class)) {
                String runId = UUID.randomUUID().toString();
                String input = Json.serialize(args[0]);

                workflowContextThreadLocal.set(new WorkflowContext(options.workflowId(), runId));

                Repo.insertEvent(new EventEntity(
                        UUID.randomUUID().toString(), options.workflowId(), runId,
                        Entity.WORKFLOW, target.getClass().getName(), null,
                        input, null, Status.STARTED, null
                ));

                Object output = method.invoke(target, args);

                Repo.insertEvent(new EventEntity(
                        UUID.randomUUID().toString(), options.workflowId(), runId,
                        Entity.WORKFLOW, target.getClass().getName(), null,
                        input, Json.serialize(output), Status.COMPLETED, null
                ));

                workflowContextThreadLocal.remove();
                return output;
            }

            return method.invoke(target, args);
        }
    }

    private record ActivityInvocationHandler(Object target) implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("Intercepted method call: " + method.getName());
            return method.invoke(target, args);
        }
    }
}
