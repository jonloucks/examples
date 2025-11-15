package io.github.jonloucks.example.client;

import java.util.Optional;
import java.util.ServiceLoader;

import static io.github.jonloucks.contracts.api.Checks.configCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;
import static java.util.Optional.ofNullable;

/**
 * Responsible for locating and creating the ClientFactory for a deployment.
 */
public final class ClientFactoryFinder {
    public ClientFactoryFinder(Client.Config config) {
        this.config = configCheck(config);
    }
    
    public ClientFactory get() {
        return find().orElseThrow(this::newNotFoundException);
    }
    
    public Optional<ClientFactory> find() {
        final Optional<ClientFactory> byReflection = createByReflection();
        if (byReflection.isPresent()) {
            return byReflection;
        }
        return createByServiceLoader();
    }
    
    private Optional<ClientFactory> createByServiceLoader() {
        if (config.useServiceLoader()) {
            try {
                for (ClientFactory factory : ServiceLoader.load(getServiceFactoryClass())) {
                    return Optional.of(factory);
                }
            } catch (Throwable ignored) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
    
    private Class<? extends ClientFactory> getServiceFactoryClass() {
        return nullCheck(config.serviceLoaderClass(), "Client Service Loader class must be present.");
    }
    
    private Optional<ClientFactory> createByReflection() {
        if (config.useReflection()) {
            return getReflectionClassName().map(this::createNewInstance);
        }
        return Optional.empty();
    }
    
    private ClientFactory createNewInstance(String className) {
        try {
            return (ClientFactory)Class.forName(className).getConstructor().newInstance();
        } catch (Throwable thrown) {
            return null;
        }
    }

    private Optional<String> getReflectionClassName() {
        return ofNullable(config.reflectionClassName()).filter(x -> !x.isEmpty());
    }
    
    private ClientException newNotFoundException() {
        return new ClientException("Unable to find Client factory.");
    }
    
    private final Client.Config config;
}
