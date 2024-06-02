package org.example.mymarketingapp.workflow;

import org.example.engine.api.Activity;
import org.example.engine.api.Workflow;
import org.example.mymarketingapp.activity.MyActivity;

public class MyWorkflow implements Workflow<SomeWorkflowInput> {

    @Activity
    private MyActivity myActivity;

    @Override
    public void start(SomeWorkflowInput input) {
        System.out.println("started workflow");
        myActivity.doSomething();
    }
}
