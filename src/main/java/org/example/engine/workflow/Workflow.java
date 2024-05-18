package org.example.engine.workflow;

public interface Workflow {

    <T> void start(T input);
}
