package io.github.jonloucks.example.server;

import io.github.jonloucks.concurrency.api.Idempotent;
import io.github.jonloucks.concurrency.api.StateMachine;
import io.github.jonloucks.concurrency.api.WaitableNotify;
import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.Repository;

import static io.github.jonloucks.concurrency.api.Idempotent.withClose;
import static io.github.jonloucks.concurrency.api.Idempotent.withOpen;
import static io.github.jonloucks.contracts.api.Checks.configCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;

final class ServerImpl implements Server {
    
    @Override
    public AutoClose open() {
        return withOpen(stateMachine, this::realOpen);
    }
    
    @Override
    public WaitableNotify<Idempotent> lifeCycleNotify() {
        return stateMachine;
    }
    
    ServerImpl(Server.Config config, Repository repository, boolean openRepository) {
        this.config = configCheck(config);
        final Repository validRepository = nullCheck(repository, "Repository must be present.");
        this.closeRepository = openRepository ? validRepository.open() : AutoClose.NONE;
        this.stateMachine = Idempotent.createStateMachine(config.contracts());
    }
    
    private AutoClose realOpen() {
        return this::exposedClose;
    }
    
    private void exposedClose() {
        withClose(stateMachine, this::realClose);
    }
    
    private void realClose() {
        closeRepository.close();
    }
    
    private final Server.Config config;
    private final AutoClose closeRepository;
    private final StateMachine<Idempotent> stateMachine;
}
