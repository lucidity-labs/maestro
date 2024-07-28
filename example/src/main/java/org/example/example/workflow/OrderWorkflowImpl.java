package org.example.example.workflow;

import org.example.engine.api.Activity;
import org.example.engine.internal.handler.Async;
import org.example.engine.internal.handler.Await;
import org.example.engine.internal.handler.Sleep;
import org.example.example.activity.interfaces.InventoryActivity;
import org.example.example.activity.interfaces.NotificationActivity;
import org.example.example.activity.interfaces.PaymentActivity;

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
    public OrderFinalized submitOrder(OrderInput input) throws ExecutionException, InterruptedException {
        System.out.println("submitted order");

        inventoryActivity.reserveInventory();
        paymentActivity.processPayment();
        notificationActivity.sendOrderConfirmedEmail();

        Await.await(() -> orderShipped);

        CompletableFuture<String> orderShippedEmailFuture = Async.function(() -> notificationActivity.sendOrderShippedEmail());
        CompletableFuture<Integer> newInventoryFuture = Async.function(() -> inventoryActivity.decreaseInventory());

        System.out.println("response to sending order shipped email is: " + orderShippedEmailFuture.get());
        System.out.println("new inventory level is: " + newInventoryFuture.get());

        Sleep.sleep(Duration.ofSeconds(10)); // this can be much, much longer if you wish

        notificationActivity.sendSpecialOfferEmail();

        return new OrderFinalized(orderShippedEmailFuture.get());
    }

    @Override
    public void confirmShipped(OrderInput input) {
        orderShipped = true;
    }
}
