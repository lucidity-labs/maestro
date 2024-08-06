package org.example.engine.internal.entity;

public record EventEntity(String id, String workflowId, Long correlationNumber,
                          Long sequenceNumber, Category category, String className,
                          String functionName, String data, Status status,
                          String timestamp, String metadata) {
}
