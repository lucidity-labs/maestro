package org.example.example;

import org.example.engine.api.Maestro;
import org.example.engine.api.WorkflowOptions;
import org.example.example.workflow.MyWorkflow;
import org.example.example.workflow.MyWorkflowImpl;
import org.example.example.workflow.SomeWorkflowInput;
import org.example.example.workflow.SomeWorkflowOutput;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class Controller {

    @PostMapping("/execute")
    public SomeWorkflowOutput executeWorkflow() throws ExecutionException, InterruptedException {
        MyWorkflow workflow = Maestro.newWorkflow(MyWorkflowImpl.class, new WorkflowOptions("ac1ade8e-1b7b-4784-a15c-724403a77b5b"));

        return workflow.execute(new SomeWorkflowInput("someInput"));
    }

    @PostMapping("/signal")
    public void signalWorkflow() {
        MyWorkflow workflow = Maestro.newWorkflow(MyWorkflowImpl.class, new WorkflowOptions("ac1ade8e-1b7b-4784-a15c-724403a77b5b"));

        workflow.confirm(new SomeWorkflowInput("someSignalInput"));
    }
}
