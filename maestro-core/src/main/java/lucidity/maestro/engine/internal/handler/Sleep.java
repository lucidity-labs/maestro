package lucidity.maestro.engine.internal.handler;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import lucidity.maestro.engine.api.throwable.AbortWorkflowExecutionError;
import lucidity.maestro.engine.internal.dto.WorkflowContext;
import lucidity.maestro.engine.internal.dto.WorkflowContextManager;
import lucidity.maestro.engine.internal.entity.EventEntity;
import lucidity.maestro.engine.internal.exception.WorkflowCorrelationStatusConflict;
import lucidity.maestro.engine.internal.repo.EventRepo;
import lucidity.maestro.engine.internal.util.Json;
import lucidity.maestro.engine.internal.util.Util;
import lucidity.maestro.engine.internal.config.Datasource;
import lucidity.maestro.engine.internal.entity.Category;
import lucidity.maestro.engine.internal.entity.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class Sleep {
    private static final Logger logger = LoggerFactory.getLogger(Sleep.class);
    private static final OneTimeTask<SleepData> task = initializeTask();
    private static final Scheduler scheduler = initializeScheduler();

    public static void sleep(Duration duration) {
        WorkflowContext workflowContext = WorkflowContextManager.get();
        Long correlationNumber = WorkflowContextManager.getCorrelationNumber();

        EventEntity existingCompletedSleep = EventRepo.get(workflowContext.workflowId(), correlationNumber, Status.COMPLETED);
        if (existingCompletedSleep != null) {
            Util.applySignals(workflowContext, existingCompletedSleep.sequenceNumber());
            return;
        }

        try {
            EventRepo.saveWithRetry(() -> new EventEntity(
                    UUID.randomUUID().toString(), workflowContext.workflowId(),
                    correlationNumber, EventRepo.getNextSequenceNumber(workflowContext.workflowId()),
                    Category.SLEEP, null, null,
                    Json.serialize(duration), Status.STARTED, null, null
            ));
        } catch (WorkflowCorrelationStatusConflict e) {
            logger.debug(e.getMessage());
        }

        String id = workflowContext.workflowId() + "-" + correlationNumber;
        scheduler.schedule(task.instance(id, new SleepData(workflowContext.workflowId(), correlationNumber)), Instant.now().plus(duration));

        WorkflowContextManager.clear();
        throw new AbortWorkflowExecutionError("Scheduled Sleep");
    }

    private static void completeSleep(String workflowId, Long correlationNumber) {
        Long nextSequenceNumber = EventRepo.getNextSequenceNumber(workflowId);

        try {
            EventRepo.saveWithRetry(() -> new EventEntity(
                    UUID.randomUUID().toString(), workflowId,
                    correlationNumber, nextSequenceNumber, Category.SLEEP,
                    null, null, null,
                    Status.COMPLETED, null, null
            ));
        } catch (WorkflowCorrelationStatusConflict e) {
            logger.debug(e.getMessage());
        }

        EventEntity existingStartedWorkflow = EventRepo.get(
                workflowId, Category.WORKFLOW, Status.STARTED
        );

        Util.replayWorkflow(existingStartedWorkflow);
    }

    private static OneTimeTask<SleepData> initializeTask() {
        return Tasks.oneTime("generic-task", SleepData.class)
                .execute((inst, ctx) -> {
                    logger.info("completing sleep");
                    SleepData sleepData = inst.getData();
                    completeSleep(sleepData.workflowId(), sleepData.correlationNumber());
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

    private record SleepData(String workflowId, Long correlationNumber) implements Serializable {
    }
}
