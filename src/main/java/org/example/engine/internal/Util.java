package org.example.engine.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

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

    public static String getActivityEventId(String className, String methodName, Long sequenceNumber) {
        return className + "_" + methodName + "_" + sequenceNumber;
    }
}
