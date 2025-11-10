package io.github.jonloucks.examples.common;

import io.github.jonloucks.contracts.api.BindStrategy;
import io.github.jonloucks.contracts.api.Repository;
import io.github.jonloucks.metalog.api.Console;
import io.github.jonloucks.metalog.api.GlobalMetalog;

import static io.github.jonloucks.concurrency.api.GlobalConcurrency.createWaitable;
import static io.github.jonloucks.contracts.api.BindStrategy.IF_ALLOWED;
import static io.github.jonloucks.contracts.api.GlobalContracts.*;
import static io.github.jonloucks.examples.common.Constants.*;
import static java.lang.Boolean.TRUE;

public final class Common {
    
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
    public static void install(Repository repository) {
        
        //noinspection ResultOfMethodCallIgnored
        GlobalMetalog.getInstance();
        
        final BindStrategy strategy = IF_ALLOWED;
        
        // Constant string, but could be changed to a localized value without changing uses
        repository.keep(PROGRAM_NAME, () -> "Unnamed", strategy);
        
        // lifeCycle will create a singleton and detect AutoOpen implementations
        repository.keep(PROGRAM, lifeCycle(ProgramImpl::new), strategy);
        
        repository.keep(OUTPUT, singleton(() -> claimContract(Console.CONTRACT)), strategy);
 
        // lazy evaluated singleton
        repository.keep(RUNNER_THREAD_COUNT, singleton(() -> Runtime.getRuntime().availableProcessors() * 8), strategy);
        
        // lifeCycle will create a singleton and detect AutoOpen implementations
        repository.keep(DISPATCHER, lifeCycle(DispatcherImpl::new), strategy);
        
        // set to true when program is quiting
        repository.keep(IS_QUITTING, singleton(() -> createWaitable(false)), strategy);
        
        // program health
        repository.keep(HEALTH, singleton(() -> createWaitable("idle")), strategy);
    }
    
    public static void setQuitting() {
        claimContract(IS_QUITTING).accept(true);
    }
    
    public static void waitForQuitting() {
        claimContract(IS_QUITTING).getWhen(TRUE::equals);
    }
    
    public static void waitForIdle() {
        claimContract(HEALTH).getWhen("idle"::equals);
    }
}
