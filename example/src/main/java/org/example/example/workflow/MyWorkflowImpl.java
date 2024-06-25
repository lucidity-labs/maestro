package org.example.example.workflow;

import org.example.engine.api.Activity;
import org.example.engine.internal.handler.Async;
import org.example.engine.internal.handler.Await;
import org.example.engine.internal.handler.Sleep;
import org.example.example.activity.MyActivity;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class MyWorkflowImpl implements MyWorkflow {

    @Activity
    private MyActivity myActivity;

    @Override
    public SomeWorkflowOutput execute(SomeWorkflowInput input) throws Throwable {
        System.out.println("started workflow");

        Await.await(() -> true);

        Sleep.sleep(Duration.ofSeconds(2));

        CompletableFuture<String> function = Async.function(() -> myActivity.doSomething());
        String activityOutput = function.get();
        System.out.println(activityOutput);

        return new SomeWorkflowOutput(activityOutput);
    }

    @Override
    public void confirm(SomeWorkflowInput input) {
        System.out.println("signalling workflow");
    }
}
