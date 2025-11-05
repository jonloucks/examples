package io.github.jonloucks.examples.concurrency.basic;

import io.github.jonloucks.concurrency.api.Waitable;
import io.github.jonloucks.contracts.api.Contract;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Contracts can be defined anywhere, placing them here to demonstrate package private access
 */
final class Constants {
    
    /**
     *   Constant string, but open for uses cases like localization.
     */
    static final Contract<String> PROGRAM_NAME = Contract.create("Program name contract");
    
    /**
     * The main program implementation, not everything has to be in the class with main()
     */
    static final Contract<Program> PROGRAM = Contract.create("Program contract");
    
    /**
     * Example of a configuration setting that is a singleton with lazy evaluation of value
     */
    static final Contract<Integer> RUNNER_THREAD_COUNT = Contract.create("Number of worker threads contract");
    
    /**
     * Example of a shared executor with auto resource management
     */
    static final Contract<Dispatcher> RUNNER = Contract.create("Dispatcher contract");
    
    /**
     * Set to true when program is quitting
     */
    static final Contract<Waitable<Boolean>> IS_QUITTING = Contract.create("Quitting contract");
    
    /**
     * The health the program
     */
    static final Contract<Waitable<String>> HEALTH = Contract.create("Health contract");
    
    /**
     * Command Output
     */
    static final Contract<Consumer<String>> OUTPUT = Contract.create("Command output contract");
    
    /**
     * Command Input
     */
    static final Contract<Supplier<String>> INPUT = Contract.create("Command input contract");
    
    /**
     * Command Output
     */
    static final Contract<Consumer<String>> ERROR = Contract.create("Command error contract");
}
