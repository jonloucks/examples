module io.github.jonloucks.example.server {
    requires transitive io.github.jonloucks.contracts;
    requires transitive io.github.jonloucks.concurrency;
    requires transitive io.github.jonloucks.metalog;
    
    uses io.github.jonloucks.contracts.api.ContractsFactory;
    uses io.github.jonloucks.concurrency.api.ConcurrencyFactory;
    uses io.github.jonloucks.metalog.api.MetalogFactory;
    
    provides io.github.jonloucks.example.server.ServerFactory with io.github.jonloucks.example.server.ServerFactoryImpl;
    
    exports io.github.jonloucks.example.server;
}