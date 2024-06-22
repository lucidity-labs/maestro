package org.example.engine.internal;

import org.example.engine.api.WorkflowFunction;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class Util {

    public static boolean isAnnotatedWith(Method method, Object target, Class<? extends Annotation> annotationClass) {
        for (Class<?> iface : target.getClass().getInterfaces()) {
            try {
                Method ifaceMethod = iface.getMethod(method.getName(), method.getParameterTypes());
                if (ifaceMethod.isAnnotationPresent(annotationClass)) {
                    return true;
                }
            } catch (NoSuchMethodException e) {
                // Continue checking other interfaces
            }
        }
        return false;
    }

    public static Method findWorkflowMethod(Class<?> clazz) {
        for (Class<?> iface : clazz.getInterfaces()) {
            for (Method method : iface.getMethods()) {
                if (method.isAnnotationPresent(WorkflowFunction.class)) {
                    return method;
                }
            }
        }
        return null;
    }

    public static void applySignals(WorkflowContext workflowContext, Long nextSequenceNumber) throws InvocationTargetException, IllegalAccessException {
        Object workflow = workflowContext.workflow();
        List<EventEntity> signals = Repo.getSignals(workflowContext.workflowId(), nextSequenceNumber);
        for (EventEntity signal : signals) {
            Method signalMethod = Arrays.stream(workflow.getClass().getMethods())
                    .filter(m -> m.getName().equals(signal.functionName()))
                    .findFirst().get();

            Object[] finalArgs = Arrays.stream(signalMethod.getParameterTypes())
                    .findFirst()
                    .map(paramType -> Json.deserialize(signal.inputData(), paramType))
                    .map(deserialized -> new Object[]{deserialized})
                    .orElse(new Object[]{});

            signalMethod.invoke(workflow, finalArgs);
        }
    }
}
