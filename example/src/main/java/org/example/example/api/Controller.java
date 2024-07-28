package org.example.example.api;

import org.example.engine.api.Maestro;
import org.example.engine.api.WorkflowOptions;
import org.example.example.workflow.OrderWorkflow;
import org.example.example.workflow.OrderWorkflowImpl;
import org.example.example.workflow.OrderInput;
import org.example.example.workflow.OrderFinalized;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class Controller {

    @PostMapping("/execute/{workflowId}")
    public OrderFinalized executeWorkflow(@PathVariable String workflowId) throws ExecutionException, InterruptedException {
        OrderWorkflow workflow = Maestro.newWorkflow(OrderWorkflowImpl.class, new WorkflowOptions(workflowId));

        return workflow.submitOrder(new OrderInput("someInput"));
    }

    @PostMapping("/signal/{workflowId}")
    public void signalWorkflow(@PathVariable String workflowId) {
        OrderWorkflow workflow = Maestro.newWorkflow(OrderWorkflowImpl.class, new WorkflowOptions(workflowId));

        workflow.confirmShipped(new OrderInput("someSignalInput"));
    }
}