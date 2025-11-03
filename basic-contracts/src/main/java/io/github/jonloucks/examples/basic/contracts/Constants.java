package io.github.jonloucks.examples.basic.contracts;

import io.github.jonloucks.contracts.api.Contract;

import java.util.concurrent.Executor;

/**
 * Contracts can be defined anywhere, placing them here to demonstrate package private access
 */
final class Constants {
    
    /**
     *   Constant string, but open for uses cases like localization.
     */
    static final Contract<String> PROGRAM_NAME_CONTRACT = Contract.create("Program name");
    
    /**
     * The main program implementation, not everything has to be in the class with main()
     */
    static final Contract<MyProgram> PROGRAM_CONTRACT = Contract.create("Program contract");
    
    /**
     * Example of a configuration setting that is a singleton with lazy evaluation of value
     */
    static final Contract<Integer> WORKER_THREAD_COUNT = Contract.create("Number of worker threads contract");
    
    /**
     * Example of a shared executor with auto resource management
     */
    static final Contract<Executor> EXECUTOR_CONTRACT = Contract.create("Executor contract");
}
