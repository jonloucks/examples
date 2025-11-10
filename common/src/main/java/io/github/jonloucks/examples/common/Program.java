package io.github.jonloucks.examples.common;

import io.github.jonloucks.concurrency.api.OnCompletion;
import io.github.jonloucks.contracts.api.AutoClose;

import java.util.List;

public interface Program {
    AutoClose addCommand(Command command);
    
    default void keepCommand(Command command) {
        //noinspection resource
        addCommand(command);
    }
    
    List<Command> getCommands();
    
    void runCommandLine(OnCompletion<CharSequence> onCompletion);
    
    void runCommand(String name, List<String> options, OnCompletion<CharSequence> onCompletion);
}
