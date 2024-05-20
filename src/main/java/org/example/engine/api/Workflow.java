package org.example.engine.api;

public interface Workflow<T> {

    void start(T input);
}
