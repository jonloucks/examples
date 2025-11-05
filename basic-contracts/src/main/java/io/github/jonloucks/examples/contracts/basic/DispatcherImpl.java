package io.github.jonloucks.examples.contracts.basic;

import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.AutoOpen;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static io.github.jonloucks.contracts.api.GlobalContracts.claimContract;
import static io.github.jonloucks.examples.contracts.basic.Constants.*;
import static java.util.Optional.ofNullable;

final class DispatcherImpl implements Dispatcher, AutoOpen {
   
    @Override
    public void dispatch(Command command) {
        incrementRunning();
        
        final Runnable job = () -> {
            try {
                output.accept(command.execute());
            } catch (Throwable thrown) {
                error.accept(thrown.getMessage());
            } finally {
                decrementRunning();
            }
        };
        
        if (!command.foreground() && ofNullable(delegate).isPresent()) {
            delegate.execute(job);
        } else {
            job.run();
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
                if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    private void incrementRunning() {
        runningCommandCount.incrementAndGet();
        health.set("busy");
    }
    
    private void decrementRunning() {
        if (runningCommandCount.decrementAndGet() == 0) {
            health.set("idle");
        }
    }
    
    private ExecutorService delegate;
    private final AtomicInteger runningCommandCount = new AtomicInteger();
    private final Consumer<String> error = claimContract(ERROR);
    private final Consumer<String> output = claimContract(OUTPUT);
    private final AtomicReference<String> health = claimContract(HEALTH);
}
