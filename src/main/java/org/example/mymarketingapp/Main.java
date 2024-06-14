package org.example.mymarketingapp;

import org.example.engine.api.Maestro;
import org.example.engine.api.WorkflowOptions;
import org.example.mymarketingapp.workflow.SomeWorkflowOutput;
import org.example.mymarketingapp.workflow.MyWorkflow;
import org.example.mymarketingapp.activity.Foo;
import org.example.mymarketingapp.activity.MyActivity;
import org.example.mymarketingapp.activity.MyActivityImpl;
import org.example.mymarketingapp.workflow.MyWorkflowImpl;
import org.example.mymarketingapp.workflow.SomeWorkflowInput;

public class Main {
    public static void main(String[] args) throws Exception {
        MyActivity myActivity = new MyActivityImpl(new Foo());

        Maestro.registerActivity(myActivity);

        MyWorkflow<SomeWorkflowInput> workflow = Maestro.newWorkflow(MyWorkflowImpl.class, new WorkflowOptions("ac1ade8e-1b7b-4784-a15c-724403a77b5b"));

        SomeWorkflowOutput output = workflow.execute(new SomeWorkflowInput("someInput"));
        System.out.println(output);
    }
}
