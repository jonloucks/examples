package io.github.jonloucks.examples.concurrency.basic;

public interface Dispatcher {
    
   void dispatch(Command command);
}
