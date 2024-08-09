package org.example.example.api;

import org.example.engine.api.Maestro;
import org.example.engine.api.workflow.WorkflowOptions;
import org.example.example.workflow.OrderWorkflow;
import org.example.example.workflow.OrderWorkflowImpl;
import org.example.example.workflow.model.Order;
import org.example.example.workflow.model.OrderFinalized;
import org.example.example.workflow.model.ShippingConfirmation;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class Controller {

    @PostMapping("/order/{orderId}")
    public OrderFinalized order(@PathVariable String orderId, @RequestBody Order order) throws ExecutionException, InterruptedException {
        OrderWorkflow workflow = Maestro.newWorkflow(OrderWorkflowImpl.class, new WorkflowOptions(orderId));
        return workflow.submitOrder(order);
    }

    @PostMapping("/confirmation/{trackingNumber}")
    public void signalWorkflow(@PathVariable String trackingNumber, @RequestBody ShippingConfirmation shippingConfirmation) {
        OrderWorkflow workflow = Maestro.newWorkflow(OrderWorkflowImpl.class, new WorkflowOptions(trackingNumber));
        workflow.confirmShipped(shippingConfirmation);
    }
}