package org.example.engine.internal;

import org.example.engine.api.Maestro;
import org.example.engine.api.WorkflowFunction;
import org.example.engine.api.WorkflowOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Util.class);
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private static final Set<String> METHODS_TO_SKIP = Set.of(
            "toString",
            "hashCode",
            "equals"
    );

    public static boolean shouldSkip(Method method) {
        return METHODS_TO_SKIP.contains(method.getName());
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
                    .map(paramType -> Json.deserialize(signal.data(), paramType))
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

    public static void replayWorkflow(EventEntity workflowStartedEvent) {
        try {
            Class<?> workflowClass = Class.forName(workflowStartedEvent.className());
            Method workflowMethod = Util.findWorkflowMethod(workflowClass);

            Object[] finalArgs = Arrays.stream(workflowMethod.getParameterTypes())
                    .findFirst()
                    .map(paramType -> Json.deserialize(workflowStartedEvent.data(), paramType))
                    .map(deserialized -> new Object[]{deserialized})
                    .orElse(new Object[]{});

            //TODO: maybe WorkflowOptions should be serialized and stored durably so we can pass the full options here?
            Object proxy = Maestro.newWorkflow(workflowClass, new WorkflowOptions(workflowStartedEvent.workflowId()));

            executor.submit(() -> workflowMethod.invoke(proxy, finalArgs));
        } catch (Throwable t) {
            logger.error(t.getMessage());
            // converting all to unchecked should be fine here because we control all the stack frames
            throw new RuntimeException(t);
        }
    }
}
