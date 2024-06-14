package org.example.example.workflow;

import org.example.engine.api.SignalFunction;
import org.example.engine.api.WorkflowFunction;

public interface MyWorkflow<T> {

    @WorkflowFunction
    SomeWorkflowOutput execute(T input);

    @SignalFunction
    void confirm(T input);
}
