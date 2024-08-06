package org.example.example.api;

import org.example.engine.api.Maestro;
import org.example.engine.api.workflow.WorkflowOptions;
import org.example.example.workflow.model.OrderedProduct;
import org.example.example.workflow.model.ShippingConfirmation;
import org.example.example.workflow.OrderWorkflow;
import org.example.example.workflow.OrderWorkflowImpl;
import org.example.example.workflow.model.Order;
import org.example.example.workflow.model.OrderFinalized;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class Controller {

    @PostMapping("/execute/{workflowId}")
    public OrderFinalized executeWorkflow(@PathVariable String workflowId) throws ExecutionException, InterruptedException {
        OrderWorkflow workflow = Maestro.newWorkflow(OrderWorkflowImpl.class, new WorkflowOptions(workflowId));

        // TODO: get this from parameters
        return workflow.submitOrder(new Order(BigDecimal.valueOf(127.95), List.of(new OrderedProduct("model car", 2))));
    }

    @PostMapping("/signal/{workflowId}")
    public void signalWorkflow(@PathVariable String workflowId) {
        OrderWorkflow workflow = Maestro.newWorkflow(OrderWorkflowImpl.class, new WorkflowOptions(workflowId));

        // TODO: get this from parameters
        workflow.confirmShipped(new ShippingConfirmation("bfb272f4d95a1d51"));
    }
}