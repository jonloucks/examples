package io.github.jonloucks.examples.basic.contracts;

import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.Repository;

import static io.github.jonloucks.contracts.api.GlobalContracts.*;
import static io.github.jonloucks.examples.basic.contracts.Constants.*;

public final class Main {
    
    /**
     * Main entry point
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final Repository repository = createMyRepository();
        try (AutoClose closeRepository = repository.open()) {
            System.out.println("Welcome to " + claimContract(PROGRAM_NAME_CONTRACT));
            final MyProgram myProgram = claimContract(PROGRAM_CONTRACT);
            
            myProgram.runCommand(args);
            
            System.out.println("Service Uptime: " + myProgram.getUptime());
        } // all resources will be released when this try block exits
    }
    
    private static Repository createMyRepository() {
        final Repository repository = claimContract(Repository.FACTORY).get();
        
        // Constant string, but could be changed to a localized value without changing uses
        repository.keep(PROGRAM_NAME_CONTRACT, () -> "Contracts Example");
        
        // lifeCycle will create a singleton and detect AutoOpen implementations
        repository.keep(PROGRAM_CONTRACT, lifeCycle(MyProgramImpl::new));
        
        // lazy evaluated singleton
        repository.keep(WORKER_THREAD_COUNT, singleton(() -> Runtime.getRuntime().availableProcessors() * 8));
        
        // lifeCycle will create a singleton and detect AutoOpen implementations
        repository.keep(EXECUTOR_CONTRACT, lifeCycle(MyExecutor::new));
        
        return repository;
    }
}
