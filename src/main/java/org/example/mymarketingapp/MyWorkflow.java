package org.example.mymarketingapp;

import org.example.engine.activity.Activity;
import org.example.engine.workflow.Workflow;

public class MyWorkflow implements Workflow {

    @Activity
    private MyActivity myActivity;

    public void start() {
        myActivity.doSomething();
    }
}
