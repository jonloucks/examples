package io.github.jonloucks.example.server;

import io.github.jonloucks.contracts.api.AutoOpen;
import io.github.jonloucks.contracts.api.Contract;
import io.github.jonloucks.contracts.api.Repository;

import java.util.function.Consumer;

/**
 * Responsible for creating new instances of Server
 */
public interface ServerFactory {
    
    /**
     * Used to promise and claim the ServerFactory implementation
     */
    Contract<ServerFactory> CONTRACT = Contract.create(ServerFactory.class);
    
    /**
     * Create a new instance of Server
     * <p>
     *     Note: caller is responsible for calling {@link AutoOpen#open()} and calling
     *     the {@link io.github.jonloucks.contracts.api.AutoClose#close() when done}
     * </p>
     * @param config the Server configuration for the new instance
     * @return the new Server instance
     */
    Server create(Server.Config config);
    
    /**
     * Create a new instance of Server
     *
     * @param builderConsumer the config builder consumer callback
     * @return the new Server instance
     * @throws IllegalArgumentException if builderConsumer is null or when configuration is invalid
     */
    Server create(Consumer<Server.Config.Builder> builderConsumer);
    
    /**
     * Install all the requirements and promises to the given Server Repository.
     * Include Server#CONTRACT which will private a unique
     *
     * @param config the Server config
     * @param repository the repository to add requirements and promises to
     * @throws IllegalArgumentException if config is null, config is invalid, or repository is null
     */
    void install(Server.Config config, Repository repository);
}
