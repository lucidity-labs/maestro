package org.example;

import org.example.engine.Maestro;
import org.example.mymarketingapp.MyActivity;
import org.example.mymarketingapp.MyWorkflow;
import org.example.mymarketingapp.SomeClass;

public class Main {
    public static void main(String[] args) throws Exception {
        Maestro.registerActivity(new MyActivity(new SomeClass()));
        Maestro.newWorkflow(MyWorkflow.class).start();
    }
}
