package org.example.example.workflow;

import org.example.engine.api.annotation.SignalFunction;
import org.example.engine.api.annotation.WorkflowFunction;
import org.example.engine.api.annotation.WorkflowInterface;

import java.util.concurrent.ExecutionException;

@WorkflowInterface
public interface OrderWorkflow {

    @WorkflowFunction
    OrderFinalized submitOrder(OrderInput input) throws ExecutionException, InterruptedException;

    @SignalFunction
    void confirmShipped(OrderInput input);
}
