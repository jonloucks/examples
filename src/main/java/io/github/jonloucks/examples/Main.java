package io.github.jonloucks.examples;

import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.Repository;
import io.github.jonloucks.example.client.Client;
import io.github.jonloucks.example.client.ClientFactory;
import io.github.jonloucks.example.client.ClientFactoryFinder;
import io.github.jonloucks.example.server.Server;
import io.github.jonloucks.example.server.ServerFactory;
import io.github.jonloucks.example.server.ServerFactoryFinder;
import io.github.jonloucks.examples.common.Common;
import io.github.jonloucks.examples.common.Program;

import java.util.Arrays;
import java.util.function.IntConsumer;

import static io.github.jonloucks.contracts.api.BindStrategy.ALWAYS;
import static io.github.jonloucks.contracts.api.GlobalContracts.claimContract;
import static io.github.jonloucks.examples.common.Common.waitForQuitting;
import static io.github.jonloucks.examples.common.Constants.*;

public final class Main {
    
    /**
     * For testing the smoke application
     * @param consumer for the exit code
     * @return the previous system exit method
     */
    public static IntConsumer setSystemExit(IntConsumer consumer) {
        final IntConsumer save = SYSTEM_EXIT;
        SYSTEM_EXIT = consumer;
        return save;
    }
    
    /**
     * Main entry point.
     * Note. Entry points are where final decisions on dependency inversions are made.
     * Not all decisions must be made in an entry point, but in this example it is exposed
     * for visibility
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            innerMain(args);
            SYSTEM_EXIT.accept(0);
        } catch (Exception thrown) {
            System.err.println(thrown.getMessage());
            SYSTEM_EXIT.accept(1);
        }
    }
    
    private static void innerMain(String[] args) {
        final Repository repository = createMyRepository(args);
        try (AutoClose closeRepository = repository.open()) {
            final Program program = claimContract(PROGRAM);
            installCommand(program);
            program.runCommandLine(c -> {});
            waitForQuitting();
        }
    }
    
    private static void installCommand(Program program) {
        program.keepCommand(new ServerCommand());
        program.keepCommand(new ClientCommand());
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
    private static Repository createMyRepository(String[] args) {
        final Repository repository = claimContract(Repository.FACTORY).get();
        
        Common.install(repository);
        
        installServer(repository);
        installClient(repository);
        
        // Save the command line for later use
        repository.keep(PROGRAM_ARGUMENTS, () -> Arrays.asList(args), ALWAYS);
        
        // Constant string, but could be changed to a localized value without changing uses
        repository.keep(PROGRAM_NAME, () -> "Examples", ALWAYS);
        
        return repository;
    }
    
    private static void installServer(Repository repository) {
        final ServerFactoryFinder finder = new ServerFactoryFinder(Server.Config.DEFAULT);
        final ServerFactory serverFactory = finder.get();
        serverFactory.install(Server.Config.DEFAULT, repository);
    }
    
    private static void installClient(Repository repository) {
        final ClientFactoryFinder finder = new ClientFactoryFinder(Client.Config.DEFAULT);
        final ClientFactory serverFactory = finder.get();
        serverFactory.install(Client.Config.DEFAULT, repository);
    }
    
    private Main() {
    
    }
    
    private static IntConsumer SYSTEM_EXIT = System::exit;
}
