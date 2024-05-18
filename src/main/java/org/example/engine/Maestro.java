package org.example.engine;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.example.engine.activity.Activity;
import org.example.engine.workflow.Workflow;
import org.example.engine.workflow.WorkflowInterceptor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Maestro {

    private static Map<Class<?>, Object> typeToActivity = new HashMap<>();

    public static void registerActivity(Object activity) {
        typeToActivity.put(activity.getClass(), activity);
    }

    public static <T extends Workflow> T newWorkflow(Class<T> clazz) throws Exception {
        return createProxyAndInjectFields(clazz);
    }

    private static <T> T getActivity(Class<T> activityType) {
        return activityType.cast(typeToActivity.get(activityType));
    }

    private static <T extends Workflow> T createProxyAndInjectFields(Class<T> clazz) throws Exception {
        Class<? extends T> proxyClass = new ByteBuddy()
                .subclass(clazz)
                .method(ElementMatchers.isDeclaredBy(clazz))
                .intercept(MethodDelegation.to(WorkflowInterceptor.class))
                .make()
                .load(clazz.getClassLoader())
                .getLoaded();

        T proxyInstance = proxyClass.getDeclaredConstructor().newInstance();

        populateAnnotatedFields(proxyInstance);

        return proxyInstance;
    }

    private static void populateAnnotatedFields(Object instance) throws Exception {
        Field[] fields = instance.getClass().getSuperclass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Activity.class)) {
                field.setAccessible(true);
                field.set(instance, getActivity(field.getType())); // TODO: implement support for getting the bean of that type from different DI libs/frameworks (spring, CDI)
            }
        }
    }
}
