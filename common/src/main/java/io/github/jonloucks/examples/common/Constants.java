package io.github.jonloucks.examples.common;

import io.github.jonloucks.concurrency.api.Waitable;
import io.github.jonloucks.contracts.api.Contract;
import io.github.jonloucks.metalog.api.Publisher;

import java.util.List;

/**
 * Contracts can be defined anywhere, placing them here to demonstrate package private access
 */
public final class Constants {
    
    /**
     *   Constant string, but open for uses cases like localization.
     */
    public static final Contract<String> PROGRAM_NAME = Contract.create("Program name contract");
    
    /**
     * The main program implementation, not everything has to be in the class with main()
     */
    public static final Contract<Program> PROGRAM = Contract.create("Program contract");
    
    
    /**
     * Main arguments
     */
    public static final Contract<List<String>> PROGRAM_ARGUMENTS = Contract.create("Program arguments");
    
    /**
     * Example of a configuration setting that is a singleton with lazy evaluation of value
     */
    public static final Contract<Integer> RUNNER_THREAD_COUNT = Contract.create("Number of worker threads contract");
    
    /**
     * Example of a shared executor with auto resource management
     */
    public static final Contract<Dispatcher> DISPATCHER = Contract.create("Dispatcher contract");
    
    /**
     * Set to true when program is quitting
     */
    public static final Contract<Waitable<Boolean>> IS_QUITTING = Contract.create("Quitting contract");
    
    /**
     * The health the program
     */
    public static final Contract<Waitable<String>> HEALTH = Contract.create("Health contract");
    
    /**
     * Command Output
     */
    public static final Contract<Publisher> OUTPUT = Contract.create("Command output contract");
}
