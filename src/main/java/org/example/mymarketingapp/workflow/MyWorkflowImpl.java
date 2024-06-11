package org.example.mymarketingapp.workflow;

import org.example.engine.api.Activity;
import org.example.mymarketingapp.activity.MyActivity;

public class MyWorkflowImpl implements MyWorkflow<SomeWorkflowInput> {

    @Activity
    private MyActivity myActivity;

    @Override
    public SomeWorkflowOutput start(SomeWorkflowInput input) {
        System.out.println("started workflow");

        myActivity.doSomething();

        return new SomeWorkflowOutput("someString");
    }

    @Override
    public void confirm(SomeWorkflowInput input) {

    }
}
