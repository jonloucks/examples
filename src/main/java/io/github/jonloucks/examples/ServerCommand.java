package io.github.jonloucks.examples;

import io.github.jonloucks.example.server.Server;
import io.github.jonloucks.examples.common.Command;
import io.github.jonloucks.examples.common.Common;

import java.util.List;

import static io.github.jonloucks.concurrency.api.Idempotent.CLOSED;
import static io.github.jonloucks.contracts.api.GlobalContracts.claimContract;

final class ServerCommand implements Command {
    @Override
    public String execute(List<String> arguments) {
        final Server server = claimContract(Server.CONTRACT);
        //noinspection resource, scope is full life of server
        server.lifeCycleNotify().notifyIf(s ->  s == CLOSED, s -> Common.setQuitting());
        return "Server started";
    }
    
    @Override
    public String getName() {
        return "server";
    }
}
