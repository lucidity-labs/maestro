package org.example.example.workflow.model;

public record OrderFinalized(String orderShippedResponse, Integer newInventoryLevel) {
}
