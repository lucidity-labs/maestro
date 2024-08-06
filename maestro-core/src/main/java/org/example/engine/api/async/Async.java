package org.example.engine.api.async;

import org.example.engine.internal.handler.AsyncImpl;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface Async {

    static <T> CompletableFuture<T> function(Supplier<T> supplier) {
        return AsyncImpl.function(supplier);
    }
}
