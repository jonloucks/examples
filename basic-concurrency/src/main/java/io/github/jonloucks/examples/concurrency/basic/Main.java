package io.github.jonloucks.examples.concurrency.basic;

import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.BindStrategy;
import io.github.jonloucks.contracts.api.Repository;

import java.util.Scanner;

import static io.github.jonloucks.concurrency.api.GlobalConcurrency.createWaitable;
import static io.github.jonloucks.contracts.api.BindStrategy.IF_NOT_BOUND;
import static io.github.jonloucks.contracts.api.GlobalContracts.*;
import static io.github.jonloucks.examples.concurrency.basic.Constants.*;
import static java.lang.Boolean.TRUE;

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
        try (AutoClose closeRepository = repository.open();
             AutoClose closeHealthNotify = openHealthNotify()) {
            
            final Program program = claimContract(PROGRAM);
            
            waitForQuit();
            
            waitForIdle();
        } // all repository resources will be released when this try block exits
    }
    
    private static void waitForQuit() {
        claimContract(IS_QUITTING).getWhen(TRUE::equals);
    }
    
    private static void waitForIdle() {
       claimContract(HEALTH).getWhen("idle"::equals);
    }
    
    private static AutoClose openHealthNotify() {
        return claimContract(HEALTH).notifyIf("idle"::equals, health -> {
            System.out.println( "All commands have completed.");
        });
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
        repository.keep(PROGRAM_NAME, () -> "Concurrency Example");
        
        // lifeCycle will create a singleton and detect AutoOpen implementations
        repository.keep(PROGRAM, lifeCycle(ProgramImpl::new));
        
        repository.keep(OUTPUT, singleton(() -> text -> System.out.println(text)), strategy);
        
        repository.keep(ERROR, singleton(() -> text -> System.err.println(text)), strategy);
        
        repository.keep(INPUT, singleton(() -> new Scanner(System.in)::nextLine), strategy);
        
        // lazy evaluated singleton
        repository.keep(RUNNER_THREAD_COUNT, singleton(() -> Runtime.getRuntime().availableProcessors() * 8));
        
        // lifeCycle will create a singleton and detect AutoOpen implementations
        repository.keep(RUNNER, lifeCycle(DispatcherImpl::new));
        
        // set to true when program is quiting
        repository.keep(IS_QUITTING, singleton(() -> createWaitable(false)));
   
        // program health
        repository.keep(HEALTH, singleton(() -> createWaitable("ready")));
        
        return repository;
    }
}
