package io.github.jonloucks.examples.concurrency.basic;

@FunctionalInterface
public interface Command {
    
    String execute();
    
    default boolean foreground() {
        return false;
    }

}
