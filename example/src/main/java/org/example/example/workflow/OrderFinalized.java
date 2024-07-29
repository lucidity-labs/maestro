package org.example.example.workflow;

public record OrderFinalized(String orderShippedResponse, Integer newInventoryLevel) {
}
