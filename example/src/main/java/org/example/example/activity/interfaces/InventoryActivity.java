package org.example.example.activity.interfaces;

import org.example.engine.api.activity.ActivityInterface;
import org.example.example.activity.model.ProductInventory;
import org.example.example.workflow.model.OrderedProduct;

import java.util.List;

@ActivityInterface
public interface InventoryActivity {

    List<OrderedProduct> reserveInventory(List<OrderedProduct> orderedProducts);

    List<ProductInventory> decreaseInventory(List<OrderedProduct> orderedProducts);
}
