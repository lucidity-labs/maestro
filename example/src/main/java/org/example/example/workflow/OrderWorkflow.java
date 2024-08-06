package org.example.example.workflow;

import org.example.engine.api.signal.SignalFunction;
import org.example.engine.api.workflow.WorkflowFunction;
import org.example.engine.api.workflow.WorkflowInterface;
import org.example.example.workflow.model.Order;
import org.example.example.workflow.model.OrderFinalized;
import org.example.example.workflow.model.ShippingConfirmation;

import java.util.concurrent.ExecutionException;

@WorkflowInterface
public interface OrderWorkflow {

    @WorkflowFunction
    OrderFinalized submitOrder(Order order) throws ExecutionException, InterruptedException;

    @SignalFunction
    void confirmShipped(ShippingConfirmation confirmation);
}
