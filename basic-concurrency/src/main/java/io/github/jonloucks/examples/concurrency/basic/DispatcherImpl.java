package io.github.jonloucks.examples.concurrency.basic;

import io.github.jonloucks.concurrency.api.OnCompletion;
import io.github.jonloucks.concurrency.api.Waitable;
import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.AutoOpen;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static io.github.jonloucks.concurrency.api.GlobalConcurrency.*;
import static io.github.jonloucks.contracts.api.GlobalContracts.claimContract;
import static io.github.jonloucks.examples.concurrency.basic.Constants.*;
import static java.util.Optional.ofNullable;

final class DispatcherImpl implements Dispatcher, AutoOpen {
    @Override
    public void dispatch(Command command) {
        incrementRunning();
        final OnCompletion<String> onCompletion = c -> {
            c.getValue().ifPresent(output);
            c.getThrown().ifPresent(e ->error.accept(e.getMessage()));
            decrementRunning();
        };
        if (!command.foreground() && ofNullable(delegate).isPresent()) {
            completeLater(onCompletion, on -> delegate.execute(() -> completeNow(on, command::execute)));
        } else {
            completeNow(onCompletion, command::execute);
        }
    }
    
    @Override
    public AutoClose open() {
        delegate = Executors.newFixedThreadPool(claimContract(RUNNER_THREAD_COUNT));
        return this::privateClose; // only open caller can close
    }
    
    private void privateClose() {
        ofNullable(delegate).ifPresent(executor -> {
            delegate = null;
            executor.shutdown();
            try {
                if (executor.awaitTermination(5, TimeUnit.MINUTES)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    private void incrementRunning() {
        runningCommandCount.incrementAndGet();
        health.accept("busy");
    }
    
    private void decrementRunning() {
        if (runningCommandCount.decrementAndGet() == 0) {
            health.accept("idle");
        }
    }

    private ExecutorService delegate;
    private final AtomicInteger runningCommandCount = new AtomicInteger();
    private final Consumer<String> output = claimContract(OUTPUT);
    private final Consumer<String> error = claimContract(ERROR);
    private final Waitable<String> health = claimContract(HEALTH);
}
