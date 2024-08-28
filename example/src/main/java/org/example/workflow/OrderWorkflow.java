package org.example.workflow;

import lucidity.maestro.engine.api.signal.SignalFunction;
import lucidity.maestro.engine.api.workflow.WorkflowFunction;
import lucidity.maestro.engine.api.workflow.WorkflowInterface;
import org.example.workflow.model.Order;
import org.example.workflow.model.OrderFinalized;
import org.example.workflow.model.ShippingConfirmation;

import java.util.concurrent.ExecutionException;

@WorkflowInterface
public interface OrderWorkflow {

    @WorkflowFunction
    OrderFinalized submitOrder(Order order) throws ExecutionException, InterruptedException;

    @SignalFunction
    void confirmShipped(ShippingConfirmation confirmation);
}
