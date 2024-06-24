package org.example.engine.internal.handler;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import org.example.engine.api.Maestro;
import org.example.engine.api.WorkflowOptions;
import org.example.engine.internal.*;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static org.example.engine.internal.Util.applySignals;

public class Sleep {
    private static final java.util.logging.Logger logger = Logger.getLogger(Sleep.class.getName());
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private static final OneTimeTask<SleepData> task = initializeTask();
    private static final Scheduler scheduler = initializeScheduler();

    public static void sleep(Duration duration) throws InvocationTargetException, IllegalAccessException, SQLException {
        WorkflowContext workflowContext = WorkflowContextManager.get();
        Long correlationNumber = WorkflowContextManager.incrementAndGetCorrelationNumber();

        EventEntity existingCompletedSleep = EventRepo.get(workflowContext.workflowId(), correlationNumber, Status.COMPLETED);
        if (existingCompletedSleep != null) {
            applySignals(workflowContext, existingCompletedSleep.sequenceNumber());
            return;
        }

        try {
            EventRepo.saveWithRetry(new EventEntity(
                    UUID.randomUUID().toString(), workflowContext.workflowId(),
                    correlationNumber, EventRepo.getNextSequenceNumber(workflowContext.workflowId()), workflowContext.runId(),
                    Category.SLEEP, null, null,
                    null, null, Status.STARTED, null
            ));
        } catch (Throwable e) {
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
                EventRepo.save(new EventEntity(
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

            Class<?> workflowClass = Class.forName(existingStartedWorkflow.className());
            Method workflowMethod = Util.findWorkflowMethod(workflowClass);

            Object[] finalArgs = Arrays.stream(workflowMethod.getParameterTypes())
                    .findFirst()
                    .map(paramType -> Json.deserialize(existingStartedWorkflow.inputData(), paramType))
                    .map(deserialized -> new Object[]{deserialized})
                    .orElse(new Object[]{});

            //maybe WorkflowOptions should be serialized and stored durably so we can pass the full options here?
            Object proxy = Maestro.newWorkflow(workflowClass, new WorkflowOptions(workflowId));

            executor.submit(() -> workflowMethod.invoke(proxy, finalArgs));
        } catch (Exception e) {
            logger.severe(e.getMessage());
            throw new RuntimeException(e.getMessage());
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
