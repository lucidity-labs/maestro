package org.example.mymarketingapp;

import org.example.engine.api.Maestro;
import org.example.engine.api.Workflow;
import org.example.mymarketingapp.activity.Foo;
import org.example.mymarketingapp.activity.MyActivity;
import org.example.mymarketingapp.activity.MyActivityImpl;
import org.example.mymarketingapp.workflow.MyWorkflow;
import org.example.mymarketingapp.workflow.SomeInput;

public class Main {
    public static void main(String[] args) throws Exception {
        MyActivity myActivity = new MyActivityImpl(new Foo());

        Maestro.registerActivity(myActivity);

        Workflow<SomeInput> workflow = Maestro.newWorkflow(MyWorkflow.class);

        workflow.start(new SomeInput());
    }
}
