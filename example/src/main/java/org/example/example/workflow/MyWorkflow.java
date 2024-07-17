package org.example.example.workflow;

import org.example.engine.api.SignalFunction;
import org.example.engine.api.WorkflowFunction;
import org.example.engine.api.WorkflowInterface;

import java.util.concurrent.ExecutionException;

@WorkflowInterface
public interface MyWorkflow {

    @WorkflowFunction
    SomeWorkflowOutput execute(SomeWorkflowInput input) throws ExecutionException, InterruptedException;

    @SignalFunction
    void confirm(SomeWorkflowInput input);
}
