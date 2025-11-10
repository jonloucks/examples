package io.github.jonloucks.examples.common;

import java.util.List;

import static io.github.jonloucks.contracts.api.GlobalContracts.claimContract;
import static io.github.jonloucks.examples.common.Common.setQuitting;
import static io.github.jonloucks.examples.common.Constants.PROGRAM;
import static io.github.jonloucks.examples.common.Constants.PROGRAM_NAME;

final class HelpCommand implements Command {
    @Override
    public String execute(List<String> arguments) {
        final StringBuilder builder = new StringBuilder();
        final Program program = claimContract(PROGRAM);
        final String programName = claimContract(PROGRAM_NAME);
        builder.append(programName).append("\n");
        builder.append("Available commands:\n");
        for (Command command : program.getCommands()) {
            builder.append("  ").append(command.getName()).append("\n");
        }
        setQuitting();
        return builder.toString();
    }
    
    @Override
    public String getName() {
        return "help";
    }

    HelpCommand() {
    
    }
}
