package io.github.jonloucks.examples.concurrency.basic;

import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.AutoOpen;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.jonloucks.contracts.api.GlobalContracts.claimContract;
import static io.github.jonloucks.examples.concurrency.basic.Constants.*;
import static java.util.Optional.ofNullable;

final class ProgramImpl implements Program, AutoOpen {
    
    @Override
    public AutoClose open() {
        serviceStart = Instant.now();
        dispatcher = claimContract(Constants.RUNNER);
        output.accept("Welcome to " + claimContract(PROGRAM_NAME));
        new Thread(this::commandLoop).start();
        return this::privateClose;
    }

    ProgramImpl() {
    }
    
    void commandLoop() {
        while (!isQuitting()) {
            promptForCommand().ifPresent(dispatcher::dispatch);
        }
    }
    
    private Duration getUptime() {
        return Duration.between(serviceStart, ofNullable(serviceEnd).orElseGet(Instant::now));
    }
    
    private boolean isQuitting() {
        return claimContract(IS_QUITTING).get();
    }
    
    private Optional<Command> promptForCommand() {
        try {
            output.accept("Enter command: ");
            return Optional.of(parseCommand(input.get())); // Read the entire line until a newline character
        } catch (Exception thrown) {
            claimContract(IS_QUITTING).accept(true);
            return Optional.empty();
        }
    }
    
    private Command parseCommand(String commandLine) {
        switch (ofNullable(commandLine).orElse("").toLowerCase()) {
            case "":
            case "help":
            case "?":
                return new HelpCommand();
            case "quit":
                return new QuitCommand();
            default:
                return () -> "Unrecognized command: " + commandLine;
        }
    }
 
    private void privateClose() {
        serviceEnd = Instant.now();
        output.accept("Service Uptime: " + getUptime());
    }
    
    private final Supplier<String> input = claimContract(INPUT);
    private final Consumer<String> output = claimContract(OUTPUT);
    private Instant serviceStart;
    private Instant serviceEnd;
    private Dispatcher dispatcher;
}
