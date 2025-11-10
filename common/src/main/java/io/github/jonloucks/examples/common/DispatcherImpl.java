package io.github.jonloucks.examples.common;

import io.github.jonloucks.concurrency.api.OnCompletion;
import io.github.jonloucks.concurrency.api.Waitable;
import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.AutoOpen;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static io.github.jonloucks.concurrency.api.GlobalConcurrency.completeLater;
import static io.github.jonloucks.concurrency.api.GlobalConcurrency.completeNow;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;
import static io.github.jonloucks.contracts.api.GlobalContracts.claimContract;
import static io.github.jonloucks.examples.common.Constants.*;
import static java.util.Optional.ofNullable;

final class DispatcherImpl implements Dispatcher, AutoOpen {
    @Override
    public <T> void dispatch(Supplier<T> valueSupplier, OnCompletion<T> onCompletion) {
        final OnCompletion<T> trackingOnCompletion = trackingOnCompletion(onCompletion);
        if (ofNullable(delegate).isPresent()) {
            completeLater(trackingOnCompletion, on -> delegate.execute(() -> completeNow(on, valueSupplier)));
        } else {
            completeNow(trackingOnCompletion, valueSupplier);
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
    
    // this is not required if keeping track of how many in-flight activities there are
    private <T> OnCompletion<T> trackingOnCompletion(OnCompletion<T> onCompletion) {
        final OnCompletion<T> validOnCompletion = nullCheck(onCompletion, "OnCompletion must be present.");
        incrementRunning();
        return completion -> {
            try {
                validOnCompletion.onCompletion(completion);
            } finally {
                decrementRunning();
            }
        };
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
    private final Waitable<String> health = claimContract(HEALTH);
}
