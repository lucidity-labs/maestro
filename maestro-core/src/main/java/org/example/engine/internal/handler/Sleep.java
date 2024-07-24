package org.example.engine.internal.handler;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import org.example.engine.internal.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.example.engine.internal.Util.applySignals;

public class Sleep {
    private static final Logger logger = LoggerFactory.getLogger(Sleep.class);
    private static final OneTimeTask<SleepData> task = initializeTask();
    private static final Scheduler scheduler = initializeScheduler();

    public static void sleep(Duration duration) {
        WorkflowContext workflowContext = WorkflowContextManager.get();
        Long correlationNumber = WorkflowContextManager.getCorrelationNumber();

        EventEntity existingCompletedSleep = EventRepo.get(workflowContext.workflowId(), correlationNumber, Status.COMPLETED);
        if (existingCompletedSleep != null) {
            applySignals(workflowContext, existingCompletedSleep.sequenceNumber());
            return;
        }

        try {
            EventRepo.saveWithRetry(() -> new EventEntity(
                    UUID.randomUUID().toString(), workflowContext.workflowId(),
                    correlationNumber, EventRepo.getNextSequenceNumber(workflowContext.workflowId()), workflowContext.runId(),
                    Category.SLEEP, null, null,
                    null, null, Status.STARTED, null
            ));
        } catch (WorkflowCorrelationStatusConflict e) {
            logger.info(e.getMessage());
        }

        String id = workflowContext.workflowId() + "-" + correlationNumber;
        scheduler.schedule(task.instance(id, new SleepData(workflowContext.workflowId(), workflowContext.runId(), correlationNumber)), Instant.now().plus(duration));

        WorkflowContextManager.clear();
        throw new AbortWorkflowExecutionError("Scheduled Sleep");
    }

    private static void completeSleep(String workflowId, String runId, Long correlationNumber) {
        try {
            Long nextSequenceNumber = EventRepo.getNextSequenceNumber(workflowId);

            try {
                EventRepo.saveWithRetry(() -> new EventEntity(
                        UUID.randomUUID().toString(), workflowId,
                        correlationNumber, nextSequenceNumber, runId,
                        Category.SLEEP, null, null, null,
                        null, Status.COMPLETED, null
                ));
            } catch (WorkflowCorrelationStatusConflict e) {
                logger.info(e.getMessage());
            }

            EventEntity existingStartedWorkflow = EventRepo.get(
                    workflowId, Category.WORKFLOW, Status.STARTED
            );

            Util.replayWorkflow(existingStartedWorkflow);
        } catch (Throwable t) {
            logger.error(t.getMessage());
            // converting all to unchecked should be fine here because we control all the stack frames
            throw new RuntimeException(t);
        }
    }

    private static OneTimeTask<SleepData> initializeTask() {
        return Tasks.oneTime("generic-task", SleepData.class)
                .execute((inst, ctx) -> {
                    logger.info("completing sleep");
                    SleepData sleepData = inst.getData();
                    completeSleep(sleepData.workflowId(), sleepData.runId(), sleepData.correlationNumber());
                });
    }

    private static Scheduler initializeScheduler() {
        Scheduler scheduler = Scheduler
                .create(Datasource.getDataSource(), task)
                .registerShutdownHook()
                .build();

        scheduler.start();

        return scheduler;
    }

    private record SleepData(String workflowId, String runId, Long correlationNumber) implements Serializable {
    }
}
