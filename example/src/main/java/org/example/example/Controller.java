package org.example.example;

import org.example.engine.api.Maestro;
import org.example.engine.api.WorkflowOptions;
import org.example.example.workflow.MyWorkflow;
import org.example.example.workflow.MyWorkflowImpl;
import org.example.example.workflow.SomeWorkflowInput;
import org.example.example.workflow.SomeWorkflowOutput;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Controller {

    @GetMapping("/greeting")
    public String getGreeting() throws Exception {
        MyWorkflow<SomeWorkflowInput> workflow = Maestro.newWorkflow(MyWorkflowImpl.class, new WorkflowOptions("ac1ade8e-1b7b-4784-a15c-724403a77b5b"));

        SomeWorkflowOutput output = workflow.execute(new SomeWorkflowInput("someInput"));
        System.out.println(output);

        return "Hello World!";
    }
}
