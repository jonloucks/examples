package io.github.jonloucks.example.client;

import io.github.jonloucks.contracts.api.AutoOpen;
import io.github.jonloucks.contracts.api.Contract;
import io.github.jonloucks.contracts.api.Repository;

import java.util.function.Consumer;

/**
 * Responsible for creating new instances of Client
 */
public interface ClientFactory {
    
    /**
     * Used to promise and claim the ClientFactory implementation
     */
    Contract<ClientFactory> CONTRACT = Contract.create(ClientFactory.class);
    
    /**
     * Create a new instance of Client
     * <p>
     *     Note: caller is responsible for calling {@link AutoOpen#open()} and calling
     *     the {@link io.github.jonloucks.contracts.api.AutoClose#close() when done}
     * </p>
     * @param config the Client configuration for the new instance
     * @return the new Client instance
     */
    Client create(Client.Config config);
    
    /**
     * Create a new instance of Client
     *
     * @param builderConsumer the config builder consumer callback
     * @return the new Client instance
     * @throws IllegalArgumentException if builderConsumer is null or when configuration is invalid
     */
    Client create(Consumer<Client.Config.Builder> builderConsumer);
    
    /**
     * Install all the requirements and promises to the given Client Repository.
     * Include Client#CONTRACT which will private a unique
     *
     * @param config the Client config
     * @param repository the repository to add requirements and promises to
     * @throws IllegalArgumentException if config is null, config is invalid, or repository is null
     */
    void install(Client.Config config, Repository repository);
}
