package org.example.mymarketingapp;

import org.example.engine.api.Activity;
import org.example.engine.api.Workflow;

public class MyWorkflow implements Workflow {

    @Activity
    private MyActivity myActivity;

    @Override
    public <T> void start(T input) {
        myActivity.doSomething();
    }
}
