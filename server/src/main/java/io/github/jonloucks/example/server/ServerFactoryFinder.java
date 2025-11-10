package io.github.jonloucks.example.server;

import java.util.Optional;
import java.util.ServiceLoader;

import static io.github.jonloucks.contracts.api.Checks.configCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;
import static java.util.Optional.ofNullable;

/**
 * Responsible for locating and creating the ServerFactory for a deployment.
 */
public final class ServerFactoryFinder {
    public ServerFactoryFinder(Server.Config config) {
        this.config = configCheck(config);
    }
    
    public ServerFactory get() {
        return find().orElseThrow(this::newNotFoundException);
    }
    
    public Optional<ServerFactory> find() {
        final Optional<ServerFactory> byReflection = createByReflection();
        if (byReflection.isPresent()) {
            return byReflection;
        }
        return createByServiceLoader();
    }
    
    private Optional<ServerFactory> createByServiceLoader() {
        if (config.useServiceLoader()) {
            try {
                for (ServerFactory factory : ServiceLoader.load(getServiceFactoryClass())) {
                    return Optional.of(factory);
                }
            } catch (Throwable ignored) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
    
    private Class<? extends ServerFactory> getServiceFactoryClass() {
        return nullCheck(config.serviceLoaderClass(), "Server Service Loader class must be present.");
    }
    
    private Optional<ServerFactory> createByReflection() {
        if (config.useReflection()) {
            return getReflectionClassName().map(this::createNewInstance);
        }
        return Optional.empty();
    }
    
    private ServerFactory createNewInstance(String className) {
        try {
            return (ServerFactory)Class.forName(className).getConstructor().newInstance();
        } catch (Throwable thrown) {
            return null;
        }
    }

    private Optional<String> getReflectionClassName() {
        return ofNullable(config.reflectionClassName()).filter(x -> !x.isEmpty());
    }
    
    private ServerException newNotFoundException() {
        return new ServerException("Unable to find Server factory.");
    }
    
    private final Server.Config config;
}
