package org.example.engine.internal.config;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.example.engine.internal.exception.WorkflowSequenceConflict;

import java.time.Duration;

public class RetryConfiguration {

    private static final Retry retry = initializeRetry();

    public static Retry getRetry() {
        return retry;
    }

    private static Retry initializeRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(5)
                .intervalFunction(IntervalFunction.ofExponentialRandomBackoff(Duration.ofMillis(700), 1.1, Duration.ofMillis(2000)))
                .retryExceptions(WorkflowSequenceConflict.class)
                .build();

        RetryRegistry registry = RetryRegistry.of(config);

        return registry.retry("WorkflowSequenceConflictRetry");
    }
}
