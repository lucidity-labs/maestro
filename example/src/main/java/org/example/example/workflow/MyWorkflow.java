package org.example.example.workflow;

import org.example.engine.api.SignalFunction;
import org.example.engine.api.WorkflowFunction;

import java.util.concurrent.ExecutionException;

public interface MyWorkflow {

    @WorkflowFunction
    SomeWorkflowOutput execute(SomeWorkflowInput input) throws ExecutionException, InterruptedException;

    @SignalFunction
    void confirm(SomeWorkflowInput input);
}
