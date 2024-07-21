package org.example.engine.internal;

import org.example.engine.internal.worker.AbandonedWorkflowWorker;

import java.util.concurrent.atomic.AtomicBoolean;

public class Initializer {

    private static final AtomicBoolean configured = new AtomicBoolean(false);

    public static void initialize() {
        if (configured.get()) return;

        AbandonedWorkflowWorker.startPoll();

        configured.set(true);
    }
}
