package org.example.example.api;

import org.example.engine.api.Maestro;
import org.example.engine.api.WorkflowOptions;
import org.example.example.workflow.OrderWorkflow;
import org.example.example.workflow.OrderWorkflowImpl;
import org.example.example.workflow.OrderInput;
import org.example.example.workflow.OrderFinalized;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class Controller {

    @PostMapping("/execute")
    public OrderFinalized executeWorkflow() throws ExecutionException, InterruptedException {
        OrderWorkflow workflow = Maestro.newWorkflow(OrderWorkflowImpl.class, new WorkflowOptions("ac1ade8e-1b7b-4784-a15c-724403a77b5b"));

        return workflow.submitOrder(new OrderInput("someInput"));
    }

    @PostMapping("/signal")
    public void signalWorkflow() {
        OrderWorkflow workflow = Maestro.newWorkflow(OrderWorkflowImpl.class, new WorkflowOptions("ac1ade8e-1b7b-4784-a15c-724403a77b5b"));

        workflow.confirmShipped(new OrderInput("someSignalInput"));
    }
}
