package io.github.jonloucks.example.client;

import io.github.jonloucks.concurrency.api.Concurrency;
import io.github.jonloucks.concurrency.api.ConcurrencyFactory;
import io.github.jonloucks.concurrency.api.StateMachineFactory;
import io.github.jonloucks.concurrency.api.WaitableFactory;
import io.github.jonloucks.contracts.api.Contracts;
import io.github.jonloucks.contracts.api.Promisor;
import io.github.jonloucks.contracts.api.Repository;
import io.github.jonloucks.example.client.Client.Config;
import io.github.jonloucks.metalog.api.Metalog;
import io.github.jonloucks.metalog.api.MetalogFactory;

import java.util.Optional;
import java.util.function.Consumer;

import static io.github.jonloucks.concurrency.api.GlobalConcurrency.findConcurrencyFactory;
import static io.github.jonloucks.contracts.api.BindStrategy.IF_NOT_BOUND;
import static io.github.jonloucks.contracts.api.Checks.*;
import static io.github.jonloucks.contracts.api.GlobalContracts.lifeCycle;
import static io.github.jonloucks.examples.common.Constants.WEATHER;
import static io.github.jonloucks.metalog.api.GlobalMetalog.findMetalogFactory;

public class ClientFactoryImpl implements ClientFactory {
    @Override
    public Client create(Config config) {
        final Config validConfig = enhancedConfigCheck(config);
        final Repository repository = validConfig.contracts().claim(Repository.FACTORY).get();
        
        installConcurrency(validConfig, repository);
        installMetalog(validConfig, repository);
        installCore(validConfig, repository);
        
        final Client server = new ClientImpl(validConfig, repository, true);
        repository.keep(Client.CONTRACT, () -> server);
        return server;
    }
    
    @Override
    public Client create(Consumer<Config.Builder> builderConsumer) {
        final Consumer<Config.Builder> validBuilderConsumer = builderConsumerCheck(builderConsumer);
        final ConfigBuilderImpl configBuilder = new ConfigBuilderImpl();
        validBuilderConsumer.accept(configBuilder);
        return create(configBuilder);
    }
    
    @Override
    public void install(Config config, Repository repository) {
        final Config validConfig = enhancedConfigCheck(config);
        final Repository validRepository = nullCheck(repository, "Repository must be present.");
        
        installConcurrency(validConfig, validRepository);
        installMetalog(validConfig, validRepository);
        installCore(validConfig, validRepository);
        
        final Promisor<Client> serverPromisor = lifeCycle(() -> new ClientImpl(validConfig, validRepository, false));
        
        validRepository.keep(Client.CONTRACT, serverPromisor, IF_NOT_BOUND);
    }
    
    private void installConcurrency(Config config, Repository repository) {
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
    
    private void installMetalog(Config config, Repository repository) {
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
    
    private Config enhancedConfigCheck(Config config) {
        final Config candidateConfig = configCheck(config);
        final Contracts contracts = contractsCheck(candidateConfig.contracts());
        
        if (contracts.isBound(Client.CONTRACT)) {
            throw new ClientException("Client is already bound.");
        }
        
        return candidateConfig;
    }
    
    private void installCore(Config config, Repository repository) {
        repository.require(Repository.FACTORY);
        repository.require(WaitableFactory.CONTRACT);
        repository.require(StateMachineFactory.CONTRACT);
        
        repository.keep(WEATHER, () -> config.contracts().claim(Client.CONTRACT).getWeatherReport());
    }
}
