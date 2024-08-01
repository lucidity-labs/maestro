package org.example.engine.internal.worker;

import org.example.engine.internal.entity.EventEntity;
import org.example.engine.internal.repo.EventRepo;
import org.example.engine.internal.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class AbandonedWorkflowWorker {
    private static final Logger logger = LoggerFactory.getLogger(AbandonedWorkflowWorker.class);
    private static final AtomicBoolean started = new AtomicBoolean(false);
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void startPoll() {
        if (started.get()) return;

        executor.submit(AbandonedWorkflowWorker::poll);

        started.set(true);
    }

    public static void poll() {
        while (true) {
            EventRepo.getAbandonedWorkflows().forEach(AbandonedWorkflowWorker::logAndReplay);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void logAndReplay(EventEntity workflow) {
        logger.info("replaying workflow with id: {}", workflow.workflowId());
        Util.replayWorkflow(workflow);
    }
}
