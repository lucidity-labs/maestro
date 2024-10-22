package org.example.activity.interfaces;

import org.example.activity.model.ProductInventory;
import lucidity.maestro.engine.api.activity.ActivityInterface;
import org.example.workflow.model.OrderedProduct;

import java.util.List;

@ActivityInterface
public interface InventoryActivity {

    List<OrderedProduct> reserveInventory(List<OrderedProduct> orderedProducts);

    List<ProductInventory> decreaseInventory(List<OrderedProduct> orderedProducts);
}
