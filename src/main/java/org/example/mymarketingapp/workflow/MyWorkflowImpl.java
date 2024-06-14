package org.example.mymarketingapp.workflow;

import org.example.engine.api.Activity;
import org.example.mymarketingapp.activity.MyActivity;

public class MyWorkflowImpl implements MyWorkflow<SomeWorkflowInput> {

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

    }
}
