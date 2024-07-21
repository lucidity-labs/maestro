package org.example.engine.internal.worker;

import org.example.engine.internal.EventRepo;
import org.example.engine.internal.Util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class AbandonedWorkflowWorker {
    private static final Logger logger = Logger.getLogger(AbandonedWorkflowWorker.class.getName());
    private static final AtomicBoolean started = new AtomicBoolean(false);
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void startPoll() {
        if (started.get()) return;

        executor.submit(AbandonedWorkflowWorker::poll);

        started.set(true);
    }

    public static void poll() {
        while (true) {

            EventRepo.getAbandonedWorkflows()
                    .forEach(w -> {
                        logger.info("replaying workflow with id: " + w.workflowId());
                        Util.replayWorkflow(w);
                    });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
