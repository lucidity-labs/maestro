package org.example.example.activity.impl;

import org.example.example.activity.interfaces.InventoryActivity;
import org.example.example.workflow.model.OrderedProduct;

import java.util.List;

public class InventoryActivityImpl implements InventoryActivity {
    @Override
    public Integer reserveInventory(List<OrderedProduct> orderedProducts) {
        System.out.println("reserving inventory");
        return 5;
    }

    @Override
    public Integer decreaseInventory(List<OrderedProduct> orderedProducts) {
        System.out.println("decreasing inventory");
        return 8;
    }
}
