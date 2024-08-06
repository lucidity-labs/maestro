package org.example.example.activity.interfaces;

import org.example.engine.api.activity.ActivityInterface;
import org.example.example.workflow.model.OrderedProduct;

import java.util.List;

@ActivityInterface
public interface InventoryActivity {

    Integer reserveInventory(List<OrderedProduct> orderedProducts);

    Integer decreaseInventory(List<OrderedProduct> orderedProducts);
}
