package org.example.engine.internal;

public record EventEntity(String id, String workflowId, String runId, String entity, String className, String functionName,
                   String inputData, String outputData, String status, String createdAt) {
}
