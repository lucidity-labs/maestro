package org.example.workflow.model;

import org.example.activity.model.ProductInventory;

import java.util.List;

public record OrderFinalized(String trackingNumber, List<ProductInventory> newInventory) {
}
