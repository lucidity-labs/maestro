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

    @PostMapping("/execute/{workflowId}")
    public OrderFinalized executeWorkflow(@PathVariable String workflowId, @RequestBody Order order) throws ExecutionException, InterruptedException {
        OrderWorkflow workflow = Maestro.newWorkflow(OrderWorkflowImpl.class, new WorkflowOptions(workflowId));
        return workflow.submitOrder(order);
    }

    @PostMapping("/signal/{workflowId}")
    public void signalWorkflow(@PathVariable String workflowId, @RequestBody ShippingConfirmation shippingConfirmation) {
        OrderWorkflow workflow = Maestro.newWorkflow(OrderWorkflowImpl.class, new WorkflowOptions(workflowId));
        workflow.confirmShipped(shippingConfirmation);
    }
}