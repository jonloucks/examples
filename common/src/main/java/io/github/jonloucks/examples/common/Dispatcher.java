package io.github.jonloucks.examples.common;

import io.github.jonloucks.concurrency.api.OnCompletion;

import java.util.function.Supplier;

public interface Dispatcher {
    
   <T> void dispatch(Supplier<T> supplier, OnCompletion<T> onCompletion);
}
