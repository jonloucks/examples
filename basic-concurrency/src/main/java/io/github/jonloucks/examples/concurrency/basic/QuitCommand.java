package io.github.jonloucks.examples.concurrency.basic;

import static io.github.jonloucks.contracts.api.GlobalContracts.claimContract;
import static io.github.jonloucks.examples.concurrency.basic.Constants.IS_QUITTING;

final class QuitCommand implements Command {
    @Override
    public String execute() {
        claimContract(IS_QUITTING).accept(true);
        return "Quit initiated";
    }
    
    @Override
    public boolean foreground() {
        return true;
    }

    QuitCommand() {
    
    }
}
