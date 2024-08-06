package org.example.example.workflow;

import org.example.engine.api.workflow.Workflow;
import org.example.engine.api.activity.Activity;
import org.example.engine.internal.handler.Async;
import org.example.example.activity.interfaces.InventoryActivity;
import org.example.example.activity.interfaces.NotificationActivity;
import org.example.example.activity.interfaces.PaymentActivity;
import org.example.example.workflow.model.Order;
import org.example.example.workflow.model.OrderFinalized;
import org.example.example.workflow.model.ShippingConfirmation;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OrderWorkflowImpl implements OrderWorkflow {

    @Activity
    private InventoryActivity inventoryActivity;

    @Activity
    private PaymentActivity paymentActivity;

    @Activity
    private NotificationActivity notificationActivity;

    private Boolean orderShipped = false;

    @Override
    public OrderFinalized submitOrder(Order order) throws ExecutionException, InterruptedException {
        inventoryActivity.reserveInventory(order.orderedProducts());
        paymentActivity.processPayment(order.total());
        notificationActivity.sendOrderConfirmedEmail();

        Workflow.await(() -> orderShipped);

        CompletableFuture<String> orderShippedEmailFuture = Async.function(() -> notificationActivity.sendOrderShippedEmail());
        CompletableFuture<Integer> newInventoryFuture = Async.function(() -> inventoryActivity.decreaseInventory(order.orderedProducts()));

        String orderShippedEmailResponse = orderShippedEmailFuture.get();
        Integer newInventoryLevel = newInventoryFuture.get();

        Workflow.sleep(Duration.ofSeconds(10)); // this can be much, much longer if you wish

        notificationActivity.sendSpecialOfferEmail();

        return new OrderFinalized(orderShippedEmailResponse, newInventoryLevel);
    }

    @Override
    public void confirmShipped(ShippingConfirmation input) {
        orderShipped = true;
    }
}
