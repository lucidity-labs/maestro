package org.example.engine.api;

import org.example.engine.internal.MaestroImpl;

// TODO: move some of these methods elsewhere? e.g. make an API class called Workflow?
public class Maestro {

    public static void registerWorkflowImplementationTypes(Class<?>... workflows) {
        MaestroImpl.registerWorkflowImplementationTypes(workflows);
    }

    public static void registerActivities(Object... activities) {
        MaestroImpl.registerActivities(activities);
    }

    // TODO: maybe expose another method accepting activity options as second param
    public static void registerActivity(Object activity) {
        MaestroImpl.registerActivity(activity);
    }

    public static <T> T newWorkflow(Class<T> clazz, WorkflowOptions options) {
        return MaestroImpl.newWorkflow(clazz, options);
    }
}
