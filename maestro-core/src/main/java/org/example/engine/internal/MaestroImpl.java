package org.example.engine.internal;

import org.example.engine.api.Maestro;
import org.example.engine.api.WorkflowOptions;
import org.example.engine.api.annotation.Activity;
import org.example.engine.api.throwable.UnregisteredWorkflowException;
import org.example.engine.internal.config.Initializer;
import org.example.engine.internal.handler.ActivityInvocationHandler;
import org.example.engine.internal.handler.WorkflowInvocationHandler;
import org.example.engine.internal.util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MaestroImpl {
    private static final Map<Class<?>, Object> typeToActivity = new HashMap<>();
    private static final Map<String, Class<?>> simpleNameToWorkflowImplType = new HashMap<>();

    public static void registerWorkflowImplementationTypes(Class<?>... workflows) {
        Initializer.initialize();

        Arrays.stream(workflows)
                .forEach(workflow -> simpleNameToWorkflowImplType.put(workflow.getSimpleName(), workflow));
    }

    public static void registerActivities(Object... activities) {
        Arrays.stream(activities).forEach(Maestro::registerActivity);
    }

    // TODO: maybe expose another method accepting activity options as second param
    public static void registerActivity(Object activity) {
        typeToActivity.put(Util.getActivityInterface(activity.getClass()), proxyActivity(activity));
    }

    @SuppressWarnings("unchecked")
    public static <T> T newWorkflow(Class<T> clazz, WorkflowOptions options) {
        if (simpleNameToWorkflowImplType.get(clazz.getSimpleName()) == null) {
            throw new UnregisteredWorkflowException(clazz);
        }

        T instance = Util.createInstance(clazz);
        populateAnnotatedFields(instance);
        Class<?> interfaceClass = Util.getWorkflowInterface(clazz);

        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new WorkflowInvocationHandler(instance, options)
        );
    }

    public static Class<?> getWorkflowImplType(String simpleName) {
        return simpleNameToWorkflowImplType.get(simpleName);
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
