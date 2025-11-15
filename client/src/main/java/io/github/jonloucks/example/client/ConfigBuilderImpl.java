package io.github.jonloucks.example.client;

import io.github.jonloucks.contracts.api.Contracts;

import java.time.Duration;

import static io.github.jonloucks.contracts.api.Checks.contractsCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;

final class ConfigBuilderImpl implements Client.Config.Builder {
    
    @Override
    public Contracts contracts() {
        return contracts;
    }
    
    @Override
    public String hostname() {
        return hostname;
    }
    
    @Override
    public int port() {
        return port;
    }
    
    @Override
    public Duration shutdownTimeout() {
        return shutdownTimeout;
    }
    
    @Override
    public Builder contracts(Contracts contracts) {
        this.contracts = contractsCheck(contracts);
        return this;
    }
    
    @Override
    public Builder port(int port) {
        this.port = port;
        return this;
    }
    
    @Override
    public Builder hostname(String hostname) {
        this.hostname = nullCheck(hostname, "Hostname must be present.");
        return this;
    }
    
    @Override
    public Builder shutdownTimeout(Duration timeout) {
        this.shutdownTimeout = nullCheck(timeout, "Timeout must be present.");
        return this;
    }
    
    ConfigBuilderImpl() {
    }
    
    private Contracts contracts;
    private String hostname = Client.Config.DEFAULT.hostname();
    private int port = Client.Config.DEFAULT.port();
    private Duration shutdownTimeout = Client.Config.DEFAULT.shutdownTimeout();
}
