package io.github.jonloucks.example.server;

import io.github.jonloucks.examples.messages.weather.WeatherOuterClass.WeatherReply;
import io.github.jonloucks.examples.messages.weather.WeatherOuterClass.WeatherRequest;
import io.grpc.stub.StreamObserver;

import static io.github.jonloucks.examples.messages.weather.WeatherGrpc.*;

final class WeatherServiceImpl extends WeatherImplBase  {
    
    @Override
    public void sayWeather(WeatherRequest request, StreamObserver<WeatherReply> responseObserver) {
        
        // Generate a greeting message for the original method
        WeatherReply reply = WeatherReply.newBuilder().setReport("Mostly Sunny").build();
        
        // Send the reply back to the client.
        responseObserver.onNext(reply);
        
        // Indicate that no further messages will be sent to the client.
        responseObserver.onCompleted();
    }
}
