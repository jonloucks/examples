module io.github.jonloucks.examples {
    requires transitive io.github.jonloucks.contracts;
    requires transitive io.github.jonloucks.concurrency;
    requires transitive io.github.jonloucks.metalog;
    requires transitive io.github.jonloucks.examples.common;
    requires transitive io.github.jonloucks.example.server;
    
    uses io.github.jonloucks.contracts.api.ContractsFactory;
    uses io.github.jonloucks.concurrency.api.ConcurrencyFactory;
    uses io.github.jonloucks.metalog.api.MetalogFactory;
    uses io.github.jonloucks.example.server.ServerFactory;
    
    exports io.github.jonloucks.examples;
}