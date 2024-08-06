package org.example.example.workflow.model;

import org.example.example.activity.model.ProductInventory;

import java.util.List;

public record OrderFinalized(String trackingNumber, List<ProductInventory> newInventory) {
}
