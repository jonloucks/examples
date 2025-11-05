package io.github.jonloucks.examples.contracts.basic;

@FunctionalInterface
public interface Command {
    
    String execute();
    
    default boolean foreground() {
        return false;
    }

}
