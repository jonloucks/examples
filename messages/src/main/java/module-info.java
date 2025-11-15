module io.github.jonloucks.examples.messages {
    requires com.google.protobuf;
    requires io.grpc;
    requires io.grpc.stub;
    requires io.grpc.protobuf;
    requires com.google.common;
    
    exports io.github.jonloucks.examples.messages.weather;
}