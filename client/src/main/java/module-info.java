module io.github.jonloucks.example.client {
    requires transitive io.github.jonloucks.contracts;
    requires transitive io.github.jonloucks.concurrency;
    requires transitive io.github.jonloucks.metalog;
    requires transitive io.github.jonloucks.examples.messages;
    requires transitive io.github.jonloucks.examples.common;
    requires io.grpc.services;
    requires io.grpc.stub;
    requires io.grpc;
    
    uses io.github.jonloucks.contracts.api.ContractsFactory;
    uses io.github.jonloucks.concurrency.api.ConcurrencyFactory;
    uses io.github.jonloucks.metalog.api.MetalogFactory;
    
    provides io.github.jonloucks.example.client.ClientFactory with io.github.jonloucks.example.client.ClientFactoryImpl;
    
    exports io.github.jonloucks.example.client;
}