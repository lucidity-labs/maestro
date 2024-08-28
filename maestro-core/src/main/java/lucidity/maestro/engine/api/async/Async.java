package lucidity.maestro.engine.api.async;

import lucidity.maestro.engine.internal.handler.AsyncImpl;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface Async {

    static <T> CompletableFuture<T> function(Supplier<T> supplier) {
        return AsyncImpl.function(supplier);
    }
}
