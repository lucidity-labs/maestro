package org.example.example.workflow;

import org.example.engine.api.Activity;
import org.example.example.activity.MyActivity;

public class MyWorkflowImpl implements MyWorkflow {

    @Activity
    private MyActivity myActivity;

    @Override
    public SomeWorkflowOutput execute(SomeWorkflowInput input) {
        System.out.println("started workflow");

        myActivity.doSomething();

        return new SomeWorkflowOutput("someOutput");
    }

    @Override
    public void confirm(SomeWorkflowInput input) {
        System.out.println("signalling workflow");
    }
}
