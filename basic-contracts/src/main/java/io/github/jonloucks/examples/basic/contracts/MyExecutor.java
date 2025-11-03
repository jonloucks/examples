package io.github.jonloucks.examples.basic.contracts;

import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.AutoOpen;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.github.jonloucks.contracts.api.GlobalContracts.claimContract;
import static java.util.Optional.ofNullable;

final class MyExecutor implements Executor, AutoOpen {
    @Override
    public void execute(Runnable command) {
        if (ofNullable(delegate).isPresent()) {
            delegate.execute(command);
        } else {
            command.run();
        }
    }
    
    @Override
    public AutoClose open() {
        delegate = Executors.newFixedThreadPool(claimContract(Constants.WORKER_THREAD_COUNT));
        return this::myClose;
    }
    
    private void myClose() {
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
    
    private ExecutorService delegate;
}
