package org.example.mymarketingapp.workflow;

import org.example.engine.api.SignalFunction;
import org.example.engine.api.WorkflowFunction;

public interface MyWorkflow<T> {

    @WorkflowFunction
    SomeWorkflowOutput start(T input);

    @SignalFunction
    void confirm(T input);
}
