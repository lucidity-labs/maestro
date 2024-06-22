package org.example.engine.internal.handler;

import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;
import org.example.engine.internal.Datasource;

import java.time.Duration;
import java.time.Instant;

public class Sleep {

    private static final OneTimeTask<Void> task = Tasks.oneTime("generic-task")
            .execute((inst, ctx) -> System.out.println("Executed!"));

    private static final Scheduler scheduler = initializeScheduler();

    public static void sleep(String id, Duration duration) {
        // TODO: make real implementation
        scheduler.schedule(task.instance(id), Instant.now().plus(duration));
    }

    private static Scheduler initializeScheduler() {
        Scheduler scheduler = Scheduler
                .create(Datasource.getDataSource(), task)
                .registerShutdownHook()
                .build();

        scheduler.start();

        return scheduler;
    }
}
