package lucidity.maestro.engine.api;

import lucidity.maestro.engine.api.activity.ActivityOptions;
import lucidity.maestro.engine.internal.MaestroImpl;
import lucidity.maestro.engine.api.workflow.WorkflowOptions;

import java.time.Duration;

public interface Maestro {

    static void registerWorkflowImplementationTypes(Class<?>... workflows) {
        MaestroImpl.registerWorkflowImplementationTypes(workflows);
    }

    static void registerActivity(Object activity) {
        MaestroImpl.registerActivity(activity, new ActivityOptions(Duration.ofMinutes(5)));
    }

    static void registerActivity(Object activity, ActivityOptions activityOptions) {
        MaestroImpl.registerActivity(activity, activityOptions);
    }

    static <T> T newWorkflow(Class<T> clazz, WorkflowOptions options) {
        return MaestroImpl.newWorkflow(clazz, options);
    }
}
