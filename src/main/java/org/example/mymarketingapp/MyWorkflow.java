package org.example.mymarketingapp;

import org.example.engine.activity.Activity;
import org.example.engine.workflow.Workflow;

public class MyWorkflow implements Workflow {

    @Activity
    private MyActivity myActivity;

    @Override
    public <T> void start(T input) {
        myActivity.doSomething();
    }
}
