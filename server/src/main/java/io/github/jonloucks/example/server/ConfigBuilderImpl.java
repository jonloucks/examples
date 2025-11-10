package io.github.jonloucks.example.server;

import io.github.jonloucks.contracts.api.Contracts;

import static io.github.jonloucks.contracts.api.Checks.contractsCheck;

final class ConfigBuilderImpl implements Server.Config.Builder {
    
    @Override
    public Contracts contracts() {
        return contracts;
    }
    
    @Override
    public Builder contracts(Contracts contracts) {
        this.contracts = contractsCheck(contracts);
        return this;
    }
    
    ConfigBuilderImpl() {
    
    }
    
    private Contracts contracts;
}
