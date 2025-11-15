package io.github.jonloucks.examples;

import io.github.jonloucks.example.client.Client;
import io.github.jonloucks.examples.common.Command;

import java.util.List;

import static io.github.jonloucks.contracts.api.GlobalContracts.claimContract;
import static io.github.jonloucks.examples.common.Constants.WEATHER;

final class ClientCommand implements Command {
    @Override
    public String execute(List<String> arguments) {
        final Client client = claimContract(Client.CONTRACT);
        final String report1 = client.getWeatherReport();
        final String report2 = claimContract(WEATHER);
        
        if (report1.equals(report2)) {
            return report1;
        } else {
            return "Conflicting reports : " + report1 + " and " + report2;
        }
    }
    
    @Override
    public String getName() {
        return "client";
    }
}
