package org.example.engine.internal;

import org.example.engine.api.WorkflowFunction;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Util {

    private static final Set<String> METHODS_TO_BYPASS = Set.of(
            "toString",
            "hashCode",
            "equals"
    );

    public static boolean shouldBypass(Method method) {
        return METHODS_TO_BYPASS.contains(method.getName());
    }

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

    public static void applySignals(WorkflowContext workflowContext, Long nextSequenceNumber) {
        Object workflow = workflowContext.workflow();
        List<EventEntity> signals = EventRepo.getSignals(workflowContext.workflowId(), nextSequenceNumber);
        for (EventEntity signal : signals) {
            Method signalMethod = Arrays.stream(workflow.getClass().getMethods())
                    .filter(m -> m.getName().equals(signal.functionName()))
                    .findFirst().get();

            Object[] finalArgs = Arrays.stream(signalMethod.getParameterTypes())
                    .findFirst()
                    .map(paramType -> Json.deserialize(signal.inputData(), paramType))
                    .map(deserialized -> new Object[]{deserialized})
                    .orElse(new Object[]{});

            try {
                signalMethod.invoke(workflow, finalArgs);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
