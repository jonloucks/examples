package io.github.jonloucks.examples;

import io.github.jonloucks.example.server.Server;
import io.github.jonloucks.examples.common.Command;

import java.util.List;

import static io.github.jonloucks.contracts.api.GlobalContracts.claimContract;

final class ServerCommand implements Command {
    @Override
    public String execute(List<String> arguments) {
        final Server server = claimContract(Server.CONTRACT);

        return "Server is " + (server.getLifeCycleState().isRejecting() ? "rejecting requests" : "excepting requests");
    }
    
    @Override
    public String getName() {
        return "server";
    }
}
