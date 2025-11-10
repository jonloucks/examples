package io.github.jonloucks.example.server;

import io.github.jonloucks.concurrency.api.Idempotent;
import io.github.jonloucks.concurrency.api.WaitableNotify;
import io.github.jonloucks.contracts.api.AutoOpen;
import io.github.jonloucks.contracts.api.Contract;
import io.github.jonloucks.contracts.api.Contracts;
import io.github.jonloucks.contracts.api.GlobalContracts;

import java.util.function.Supplier;

public interface Server extends AutoOpen {
    Contract<Server> CONTRACT = Contract.create(Server.class);
    
    WaitableNotify<Idempotent> lifeCycleNotify();
    
    interface Config {
        /**
         * The default configuration used when creating a new Server instance
         */
        Config DEFAULT = new Config() {};
        
        /**
         * @return the contracts, some use case have their own Contracts instance.
         */
        default Contracts contracts() {
            return GlobalContracts.getInstance();
        }
        
        /**
         * @return if true, reflection might be used to locate the ServerFactory
         */
        default boolean useReflection() {
            return true;
        }
        
        /**
         * @return the class name to use if reflection is used to find the ServerFactory
         */
        default String reflectionClassName() {
            return "io.github.jonloucks.example.server.ServerFactoryImpl";
        }
        
        /**
         * @return if true, the ServiceLoader might be used to locate the ServerFactory
         */
        default boolean useServiceLoader() {
            return true;
        }
        
        /**
         * @return the class name to load from the ServiceLoader to find the ServerFactory
         */
        default Class<? extends ServerFactory> serviceLoaderClass() {
            return ServerFactory.class;
        }
        
        interface Builder extends Config {
            Contract<Supplier<Config.Builder>> FACTORY = Contract.create("Server Config Builder Factory");
            
            Builder contracts(Contracts contracts);
        }
    }
}
