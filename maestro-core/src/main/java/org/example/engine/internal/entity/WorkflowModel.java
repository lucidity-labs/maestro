package org.example.engine.internal.entity;

import java.time.Instant;

public record WorkflowModel(String workflowId, String className, String functionName,
                            Instant startTimestamp, Instant endTimestamp, String input, String output) {
}
