package org.example.example.workflow;

import org.example.engine.api.signal.SignalFunction;
import org.example.engine.api.workflow.WorkflowFunction;
import org.example.engine.api.workflow.WorkflowInterface;

import java.util.concurrent.ExecutionException;

@WorkflowInterface
public interface OrderWorkflow {

    @WorkflowFunction
    OrderFinalized submitOrder(OrderInput input) throws ExecutionException, InterruptedException;

    @SignalFunction
    void confirmShipped(OrderInput input);
}
