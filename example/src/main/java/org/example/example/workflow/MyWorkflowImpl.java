package org.example.example.workflow;

import org.example.engine.api.Activity;
import org.example.engine.api.Maestro;
import org.example.example.activity.MyActivity;

public class MyWorkflowImpl implements MyWorkflow {

    @Activity
    private MyActivity myActivity;

    @Override
    public SomeWorkflowOutput execute(SomeWorkflowInput input) throws Throwable {
        System.out.println("started workflow");

        Maestro.await(() -> true);

        myActivity.doSomething();

        return new SomeWorkflowOutput("someOutput");
    }

    @Override
    public void confirm(SomeWorkflowInput input) {
        System.out.println("signalling workflow");
    }
}
