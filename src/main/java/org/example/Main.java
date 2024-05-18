package org.example;

import org.example.engine.api.Maestro;
import org.example.mymarketingapp.MyActivity;
import org.example.mymarketingapp.MyWorkflow;
import org.example.mymarketingapp.SomeClass;
import org.example.mymarketingapp.SomeInput;

public class Main {
    public static void main(String[] args) throws Exception {
        Maestro.registerActivity(new MyActivity(new SomeClass()));

        MyWorkflow myWorkflow = Maestro.newWorkflow(MyWorkflow.class);
        myWorkflow.start(new SomeInput());
    }
}
