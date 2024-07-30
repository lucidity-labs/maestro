package org.example.example.activity.interfaces;

import org.example.engine.api.annotation.ActivityInterface;

@ActivityInterface
public interface InventoryActivity {

    Integer reserveInventory();

    Integer decreaseInventory();
}
