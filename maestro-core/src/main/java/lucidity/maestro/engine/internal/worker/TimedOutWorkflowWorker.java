package lucidity.maestro.engine.internal.worker;

import lucidity.maestro.engine.internal.entity.EventEntity;
import lucidity.maestro.engine.internal.repo.EventRepo;
import lucidity.maestro.engine.internal.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimedOutWorkflowWorker {
    private static final Logger logger = LoggerFactory.getLogger(TimedOutWorkflowWorker.class);
    private static final AtomicBoolean started = new AtomicBoolean(false);
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void startPoll() {
        if (started.get()) return;

        executor.submit(TimedOutWorkflowWorker::poll);

        started.set(true);
    }

    public static void poll() {
        while (true) {
            List<EventEntity> timedOutEvents = EventRepo.getTimedOutEvents();
            timedOutEvents.forEach(TimedOutWorkflowWorker::logAndReplay);

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
