package io.github.jonloucks.examples.contracts.basic;

final class HelpCommand implements Command {
    @Override
    public String execute() {
        return "This is an example...";
    }
    
    @Override
    public boolean foreground() {
        return true;
    }
    
    HelpCommand() {
    
    }
}
