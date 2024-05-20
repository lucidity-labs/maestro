package org.example.engine.api;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class Maestro {

    private static final Map<Class<?>, Object> typeToActivity = new HashMap<>();

    // TODO: maybe expose another method accepting activity options as second param
    public static void registerActivity(Object activity) {
        typeToActivity.put(getInterface(activity.getClass()), proxyActivity(activity));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Workflow<?>> T newWorkflow(Class<T> clazz) throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();
        populateAnnotatedFields(instance);

        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{Workflow.class},
                new WorkflowInvocationHandler(instance)
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

    private record WorkflowInvocationHandler(Object target) implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("Intercepted method call: " + method.getName());
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
