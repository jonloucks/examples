package io.github.jonloucks.examples.basic.contracts;

import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.AutoOpen;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.Executor;

import static io.github.jonloucks.contracts.api.GlobalContracts.claimContract;
import static java.util.Optional.ofNullable;

final class MyProgramImpl implements MyProgram, AutoOpen {
    
    @Override
    public AutoClose open() {
        serviceStart = Instant.now();
        executor = claimContract(Constants.EXECUTOR_CONTRACT);
        return this::close;
    }
    
    @Override
    public Duration getUptime() {
        return Duration.between(serviceStart, ofNullable(serviceEnd).orElseGet(Instant::now));
    }
    
    @Override
    public void runCommand(String[] args) {
        if (args.length == 0) {
            executor.execute(() -> System.out.println("Command not specified."));
        } else {
            executor.execute(() -> System.out.println("Unrecognized command: " + Arrays.toString(args)));
        }
    }
    
    MyProgramImpl() {
    }
    
    private void close() {
        serviceEnd = Instant.now();
    }
    
    private Instant serviceStart;
    private Instant serviceEnd;
    private Executor executor;
}
