package lucidity.maestro.engine.internal.config;

import lucidity.maestro.engine.internal.http.Server;
import lucidity.maestro.engine.internal.worker.TimedOutWorkflowWorker;

import java.util.concurrent.atomic.AtomicBoolean;

public class Initializer {

    private static final AtomicBoolean configured = new AtomicBoolean(false);

    public static void initialize() {
        if (configured.get()) return;

        TimedOutWorkflowWorker.startPoll();
        Server.serve();

        configured.set(true);
    }
}
