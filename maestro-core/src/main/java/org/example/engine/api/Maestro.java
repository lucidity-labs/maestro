package org.example.engine.api;

import org.example.engine.internal.config.Initializer;
import org.example.engine.internal.util.Util;
import org.example.engine.internal.handler.ActivityInvocationHandler;
import org.example.engine.internal.handler.WorkflowInvocationHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Maestro {
    private static final Map<Class<?>, Object> typeToActivity = new HashMap<>();

    public static void registerActivities(Object... activity) {
        Arrays.stream(activity).forEach(Maestro::registerActivity);
    }

    // TODO: maybe expose another method accepting activity options as second param
    public static void registerActivity(Object activity) {
        Initializer.initialize();

        typeToActivity.put(Util.getActivityInterface(activity.getClass()), proxyActivity(activity));
    }

    @SuppressWarnings("unchecked")
    public static <T> T newWorkflow(Class<T> clazz, WorkflowOptions options) {
        T instance = Util.createInstance(clazz);
        populateAnnotatedFields(instance);
        Class<?> interfaceClass = Arrays.stream(clazz.getInterfaces()).findFirst().get();

        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new WorkflowInvocationHandler(instance, options)
        );
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxyActivity(T instance) {
        return (T) Proxy.newProxyInstance(
                instance.getClass().getClassLoader(),
                new Class<?>[]{Util.getActivityInterface(instance.getClass())},
                new ActivityInvocationHandler(instance)
        );
    }

    private static void populateAnnotatedFields(Object instance) {
        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Activity.class)) {
                Object activity = typeToActivity.get(field.getType());
                Util.setField(field, instance, activity);
            }
        }
    }
}
