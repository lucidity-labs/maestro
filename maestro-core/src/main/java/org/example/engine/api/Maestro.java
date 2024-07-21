package org.example.engine.api;

import org.example.engine.internal.Initializer;
import org.example.engine.internal.handler.ActivityInvocationHandler;
import org.example.engine.internal.handler.WorkflowInvocationHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Maestro {
    private static final Map<Class<?>, Object> typeToActivity = new HashMap<>();
    private static final Logger logger = Logger.getLogger(Maestro.class.getName());

    // TODO: maybe expose another method accepting activity options as second param
    public static void registerActivity(Object activity) {
        Initializer.initialize();

        typeToActivity.put(getInterface(activity.getClass()), proxyActivity(activity));
    }

    @SuppressWarnings("unchecked")
    public static <T> T newWorkflow(Class<T> clazz, WorkflowOptions options) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            populateAnnotatedFields(instance);
            Class<?> interfaceClass = Arrays.stream(clazz.getInterfaces()).findFirst().get();

            return (T) Proxy.newProxyInstance(
                    clazz.getClassLoader(),
                    new Class<?>[]{interfaceClass},
                    new WorkflowInvocationHandler(instance, options)
            );
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        for (Class<?> iface : clazz.getInterfaces()) {
            if (iface.isAnnotationPresent(ActivityInterface.class)) return iface;
        }

        throw new IllegalArgumentException("The class must implement an interface annotated with @" + ActivityInterface.class.getSimpleName());
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
