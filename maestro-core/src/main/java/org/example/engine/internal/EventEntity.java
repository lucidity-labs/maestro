package org.example.engine.internal;

public record EventEntity(String id, String workflowId, Long correlationNumber, Long sequenceNumber,
                          String runId, Entity entity, String className, String functionName,
                          String inputData, String outputData, Status status, String createdAt) {
}
