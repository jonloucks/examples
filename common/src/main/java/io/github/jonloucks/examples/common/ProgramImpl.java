package io.github.jonloucks.examples.common;

import io.github.jonloucks.concurrency.api.OnCompletion;
import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.AutoOpen;
import io.github.jonloucks.metalog.api.Console;

import java.util.*;

import static io.github.jonloucks.concurrency.api.GlobalConcurrency.completeNow;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;
import static io.github.jonloucks.contracts.api.GlobalContracts.claimContract;
import static io.github.jonloucks.examples.common.Constants.*;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

final class ProgramImpl implements Program, AutoOpen {
    
    @Override
    public AutoClose open() {
        dispatcher = claimContract(Constants.DISPATCHER);
        console.output(() -> "Welcome to " + claimContract(PROGRAM_NAME) );
        return this::privateClose;
    }

    ProgramImpl() {
        keepCommand(new HelpCommand());
    }
    
    @Override
    public void runCommandLine(OnCompletion<CharSequence> onCompletion) {
        final List<String> arguments = claimContract(PROGRAM_ARGUMENTS);
        if (arguments.isEmpty()) {
            runCommand("help", emptyList(), onCompletion);
        } else {
            runCommand(arguments.get(0), arguments.subList(1, arguments.size()), onCompletion);
        }
    }
    
    @Override
    public void runCommand(String name, List<String> options, OnCompletion<CharSequence> onCompletion) {
        final OnCompletion<CharSequence> capture = captureResults(onCompletion);
        final Optional<Command> optionalCommand = findCommand(name);
        if (optionalCommand.isPresent()) {
            dispatcher.dispatch(() -> optionalCommand.get().execute(options), capture);
        } else {
            completeNow(capture, () -> {
                throw new IllegalArgumentException("Unknown command: " + name);
            });
        }
    }

    @Override
    public AutoClose addCommand(Command command) {
        final Command validCommand = nullCheck(command, "Command must be present.");
        final String key = toCommandKey(validCommand.getName());
        commands.put(key, validCommand);
        return () -> commands.remove(key, validCommand);
    }
    
    @Override
    public List<Command> getCommands() {
        return commands.values().stream().sorted(comparing(Command::getName)).collect(toList());
    }
    
    private Optional<Command> findCommand(String commandName) {
        return ofNullable(commands.get(toCommandKey(commandName)));
    }
    
    private void privateClose() {
        console.output(() -> "Good bye to " + claimContract(PROGRAM_NAME) );
    }
    
    private OnCompletion<CharSequence> captureResults(OnCompletion<CharSequence> onCompletion) {
        final OnCompletion<CharSequence> validonOnCompletion = nullCheck(onCompletion, "OnCompletion must be present.");
        return completion -> {
            try {
                if (completion.getThrown().isPresent()) {
                    final Throwable thrown = completion.getThrown().get();
                    console.publish(() -> completion.getValue().orElse("Error: " + thrown.getMessage()),
                        b -> b.thrown(thrown).channel("Console.error"));
                } else if (completion.getValue().isPresent()) {
                    console.publish(completion.getValue()::get);
                }
            } finally {
                validonOnCompletion.onCompletion(completion);
            }
        };
    }
    
    private static String toCommandKey(String commandName) {
        final String validName = nullCheck(commandName, "Command name must be present.");
        final String key = validName.trim().toLowerCase();
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Command name must not be empty.");
        }
        return key;
    }
    
    private final Map<String,Command>  commands = new HashMap<>();
    private final Console console = claimContract(Console.CONTRACT);
    private Dispatcher dispatcher;
}
