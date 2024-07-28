package org.example.example.activity.impl;

import org.example.example.activity.interfaces.InventoryActivity;

public class InventoryActivityImpl implements InventoryActivity {
    @Override
    public Integer reserveInventory() {
        System.out.println("reserving inventory");
        return 5;
    }

    @Override
    public Integer decreaseInventory() {
        System.out.println("decreasing inventory");
        return 8;
    }
}
