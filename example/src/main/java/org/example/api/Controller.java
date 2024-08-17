package org.example.api;

import org.example.engine.api.Maestro;
import org.example.engine.api.workflow.WorkflowOptions;
import org.example.workflow.OrderWorkflow;
import org.example.workflow.OrderWorkflowImpl;
import org.example.workflow.model.Order;
import org.example.workflow.model.ShippingConfirmation;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class Controller {

    @PostMapping("/order/{orderId}")
    public void order(@PathVariable String orderId, @RequestBody Order order) throws ExecutionException, InterruptedException {
        OrderWorkflow workflow = Maestro.newWorkflow(OrderWorkflowImpl.class, new WorkflowOptions(orderId));
        workflow.submitOrder(order);
    }

    @PostMapping("/confirmation/{trackingNumber}")
    public void signalWorkflow(@PathVariable String trackingNumber, @RequestBody ShippingConfirmation shippingConfirmation) {
        OrderWorkflow workflow = Maestro.newWorkflow(OrderWorkflowImpl.class, new WorkflowOptions(trackingNumber));
        workflow.confirmShipped(shippingConfirmation);
    }
}