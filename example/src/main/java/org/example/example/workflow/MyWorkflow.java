package org.example.example.workflow;

import org.example.engine.api.SignalFunction;
import org.example.engine.api.WorkflowFunction;

public interface MyWorkflow {

    @WorkflowFunction
    SomeWorkflowOutput execute(SomeWorkflowInput input) throws Throwable;

    @SignalFunction
    void confirm(SomeWorkflowInput input);
}
