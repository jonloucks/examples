package io.github.jonloucks.examples.contracts.basic;

import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.BindStrategy;
import io.github.jonloucks.contracts.api.Repository;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.jonloucks.contracts.api.BindStrategy.IF_NOT_BOUND;
import static io.github.jonloucks.contracts.api.GlobalContracts.*;
import static io.github.jonloucks.examples.contracts.basic.Constants.*;

@SuppressWarnings("unused")
public final class Main {
    
    /**
     * Main entry point.
     * Note. Entry points are where final decisions on dependency inversions are made.
     * Not all decisions must be made in an entry point, but in this example it is exposed
     * for visibility
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final Repository repository = createMyRepository();
        try (AutoClose closeRepository = repository.open()) {
            
            final Program program = claimContract(PROGRAM);
            
            waitForQuit();
        } // all repository resources will be released when this try block exits
    }
    
    private static void waitForQuit() {
        try {
            claimContract(IS_QUITTING).await();
        } catch (InterruptedException ignored) {
            throw new RuntimeException("Waiting for quit");
        }
    }
    
    /**
     *  For complex projects with many modules and entry points there a many
     *  ways to extend and manage dependency inversions without
     *  have them all defined here.
     *  For example:
     *      each module could have its own Repository.
     *          MyModule.createRepository(contracts)
     *      each module could have a static method to install contracts to a given repository
     *          MyModule.install(repository)
     *      each module could use the ServiceLoader mechanism
     *      I am sure there are more like SpringBoot
     */
    private static Repository createMyRepository() {
        final Repository repository = claimContract(Repository.FACTORY).get();
        
        final BindStrategy strategy = IF_NOT_BOUND;
        
        // Constant string, but could be changed to a localized value without changing uses
        repository.keep(PROGRAM_NAME, () -> "Concurrency Example", strategy);
        
        // lifeCycle will create a singleton and detect AutoOpen implementations
        repository.keep(PROGRAM, lifeCycle(ProgramImpl::new), strategy);
        
        repository.keep(OUTPUT, singleton(() -> text -> System.out.println(text)), strategy);
        
        repository.keep(ERROR, singleton(() -> text -> System.err.println(text)), strategy);
        
        repository.keep(INPUT, singleton(() -> new Scanner(System.in)::nextLine), strategy);
        
        // lazy evaluated singleton
        repository.keep(RUNNER_THREAD_COUNT, singleton(() -> Runtime.getRuntime().availableProcessors() * 8), strategy);
        
        // lifeCycle will create a singleton and detect AutoOpen implementations
        repository.keep(DISPATCHER, lifeCycle(DispatcherImpl::new), strategy);
        
        // set to true when program is quiting
        repository.keep(IS_QUITTING, singleton(() -> new CountDownLatch(1)), strategy);
   
        // program health
        repository.keep(HEALTH, singleton(() -> new AtomicReference<>("ready")), strategy);
        
        return repository;
    }
}
