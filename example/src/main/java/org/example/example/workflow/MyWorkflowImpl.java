package org.example.example.workflow;

import org.example.engine.api.Activity;
import org.example.engine.internal.handler.Async;
import org.example.engine.internal.handler.Await;
import org.example.engine.internal.handler.Sleep;
import org.example.example.activity.MyActivity;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MyWorkflowImpl implements MyWorkflow {

    @Activity
    private MyActivity myActivity;

    @Override
    public SomeWorkflowOutput execute(SomeWorkflowInput input) throws ExecutionException, InterruptedException {
        System.out.println("started workflow");

        Await.await(() -> true);

        Sleep.sleep(Duration.ofSeconds(2));

        CompletableFuture<String> async1 = Async.function(() -> myActivity.doSomething());
        CompletableFuture<String> async2 = Async.function(() -> myActivity.doSomething());
        CompletableFuture<Void> asyncBoth = CompletableFuture.allOf(async1, async2);
        asyncBoth.get();

        System.out.println("first activity returned: " + async1.get());
        System.out.println("second activity returned: " + async2.get());

        return new SomeWorkflowOutput(async1.get());
    }

    @Override
    public void confirm(SomeWorkflowInput input) {
        System.out.println("signalling workflow");
    }
}
