package io.github.jonloucks.example.client;

import io.github.jonloucks.concurrency.api.Idempotent;
import io.github.jonloucks.concurrency.api.WaitableNotify;
import io.github.jonloucks.contracts.api.AutoOpen;
import io.github.jonloucks.contracts.api.Contract;
import io.github.jonloucks.contracts.api.Contracts;
import io.github.jonloucks.contracts.api.GlobalContracts;

import java.time.Duration;
import java.util.function.Supplier;

public interface Client extends AutoOpen {
    Contract<Client> CONTRACT = Contract.create(Client.class);
    
    WaitableNotify<Idempotent> lifeCycleNotify();
    
    String getWeatherReport();
    
    interface Config {
        /**
         * The default configuration used when creating a new Client instance
         */
        Config DEFAULT = new Config() {};
        
        /**
         * @return the contracts, some use case have their own Contracts instance.
         */
        default Contracts contracts() {
            return GlobalContracts.getInstance();
        }
        
        /**
         * @return if true, reflection might be used to locate the ClientFactory
         */
        default boolean useReflection() {
            return true;
        }
        
        /**
         * @return the class name to use if reflection is used to find the ClientFactory
         */
        default String reflectionClassName() {
            return "io.github.jonloucks.example.client.ClientFactoryImpl";
        }
        
        /**
         * @return if true, the ServiceLoader might be used to locate the ClientFactory
         */
        default boolean useServiceLoader() {
            return true;
        }
        
        /**
         * @return the class name to load from the ServiceLoader to find the ClientFactory
         */
        default Class<? extends ClientFactory> serviceLoaderClass() {
            return ClientFactory.class;
        }
        
        default int port() {
            return 50052;
        }
        
        default String hostname() {
            return "localhost";
        }
        
        default Duration shutdownTimeout() {
            return Duration.ofSeconds(60);
        }
        
        interface Builder extends Config {
            Contract<Supplier<Builder>> FACTORY = Contract.create("Client Config Builder Factory");
            
            Builder contracts(Contracts contracts);
            
            Builder port(int port);
            
            Builder hostname(String hostname);
            
            Builder shutdownTimeout(Duration shutdownTimeout);
        }
    }
}
