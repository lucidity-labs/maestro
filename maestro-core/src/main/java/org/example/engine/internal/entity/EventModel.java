package org.example.engine.internal.entity;

public record EventModel(String workflowId, String className, String functionName,
                         String startTimestamp, String endTimestamp, String input, String output) {
}
