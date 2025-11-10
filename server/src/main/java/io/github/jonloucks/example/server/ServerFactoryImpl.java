package io.github.jonloucks.example.server;

import io.github.jonloucks.concurrency.api.Concurrency;
import io.github.jonloucks.concurrency.api.ConcurrencyFactory;
import io.github.jonloucks.concurrency.api.StateMachineFactory;
import io.github.jonloucks.concurrency.api.WaitableFactory;
import io.github.jonloucks.contracts.api.Contracts;
import io.github.jonloucks.contracts.api.Promisor;
import io.github.jonloucks.contracts.api.Repository;
import io.github.jonloucks.example.server.Server.Config;
import io.github.jonloucks.metalog.api.*;

import java.util.Optional;
import java.util.function.Consumer;

import static io.github.jonloucks.concurrency.api.GlobalConcurrency.findConcurrencyFactory;
import static io.github.jonloucks.contracts.api.BindStrategy.IF_NOT_BOUND;
import static io.github.jonloucks.contracts.api.Checks.*;
import static io.github.jonloucks.contracts.api.GlobalContracts.lifeCycle;
import static io.github.jonloucks.metalog.api.GlobalMetalog.findMetalogFactory;

public class ServerFactoryImpl implements ServerFactory {
    @Override
    public Server create(Config config) {
        final Server.Config validConfig = enhancedConfigCheck(config);
        final Repository repository = validConfig.contracts().claim(Repository.FACTORY).get();
        
        installConcurrency(validConfig, repository);
        installMetalog(validConfig, repository);
        installCore(validConfig, repository);
        
        final Server server = new ServerImpl(validConfig, repository, true);
        repository.keep(Server.CONTRACT, () -> server);
        return server;
    }
    
    @Override
    public Server create(Consumer<Config.Builder> builderConsumer) {
        final Consumer<Config.Builder> validBuilderConsumer = builderConsumerCheck(builderConsumer);
        final ConfigBuilderImpl configBuilder = new ConfigBuilderImpl();
        validBuilderConsumer.accept(configBuilder);
        return create(configBuilder);
    }
    
    @Override
    public void install(Config config, Repository repository) {
        final Server.Config validConfig = enhancedConfigCheck(config);
        final Repository validRepository = nullCheck(repository, "Repository must be present.");
        
        installConcurrency(validConfig, validRepository);
        installMetalog(validConfig, validRepository);
        installCore(validConfig, validRepository);
        
        final Promisor<Server> serverPromisor = lifeCycle(() -> new ServerImpl(validConfig, validRepository, false));
        
        validRepository.keep(Server.CONTRACT, serverPromisor, IF_NOT_BOUND);
    }
    
    private void installConcurrency(Server.Config config, Repository repository) {
        final Concurrency.Config concurrencyConfig = new Concurrency.Config() {
            @Override
            public Contracts contracts() {
                return config.contracts();
            }
        };
        //noinspection ResultOfMethodCallIgnored
        contractsCheck(concurrencyConfig.contracts());
        final Optional<ConcurrencyFactory> optionalFactory = findConcurrencyFactory(concurrencyConfig);
        
        optionalFactory.ifPresent(f -> f.install(concurrencyConfig, repository));
    }
    
    private void installMetalog(Server.Config config, Repository repository) {
        final Metalog.Config metalogConfig = new Metalog.Config() {
            @Override
            public Contracts contracts() {
                return config.contracts();
            }
        };
        //noinspection ResultOfMethodCallIgnored
        contractsCheck(metalogConfig.contracts());
        final Optional<MetalogFactory> optionalFactory = findMetalogFactory(metalogConfig);
        
        optionalFactory.ifPresent(f -> f.install(metalogConfig, repository));
    }
    
    private Server.Config enhancedConfigCheck(Server.Config config) {
        final Server.Config candidateConfig = configCheck(config);
        final Contracts contracts = contractsCheck(candidateConfig.contracts());
        
        if (contracts.isBound(Server.CONTRACT)) {
            throw new ServerException("Server is already bound.");
        }
        
        return candidateConfig;
    }
    
    private void installCore(Server.Config config, Repository repository) {
        repository.require(Repository.FACTORY);
        repository.require(WaitableFactory.CONTRACT);
        repository.require(StateMachineFactory.CONTRACT);
    }
}
