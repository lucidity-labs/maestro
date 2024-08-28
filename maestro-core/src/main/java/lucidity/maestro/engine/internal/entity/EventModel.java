package lucidity.maestro.engine.internal.entity;

import java.time.Instant;

public record EventModel(String workflowId, Category category, String className, String functionName,
                         Instant startTimestamp, Instant endTimestamp, String input, String output) {
}
