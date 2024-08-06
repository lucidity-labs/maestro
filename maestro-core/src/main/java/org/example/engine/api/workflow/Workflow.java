package org.example.engine.api.workflow;

import org.example.engine.internal.handler.Await;
import org.example.engine.internal.handler.Sleep;

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
