package lucidity.maestro.engine.api.workflow;

import lucidity.maestro.engine.internal.handler.Await;
import lucidity.maestro.engine.internal.handler.Sleep;

import java.time.Duration;
import java.util.function.Supplier;

public interface Workflow {

    static void await(Supplier<Boolean> condition) {
        Await.await(condition);
    }

    static void sleep(Duration duration) {
        Sleep.sleep(duration);
    }
}
