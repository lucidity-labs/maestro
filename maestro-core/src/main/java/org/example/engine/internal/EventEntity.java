package org.example.engine.internal;

public record EventEntity(String id, String workflowId, Long correlationNumber,
                          Long sequenceNumber, Category category, String className,
                          String functionName, String data, Status status, String timestamp) {
}
