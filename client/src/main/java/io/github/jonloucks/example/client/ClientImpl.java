package io.github.jonloucks.example.client;

import io.github.jonloucks.concurrency.api.Idempotent;
import io.github.jonloucks.concurrency.api.StateMachine;
import io.github.jonloucks.concurrency.api.WaitableNotify;
import io.github.jonloucks.contracts.api.AutoClose;
import io.github.jonloucks.contracts.api.Repository;
import io.github.jonloucks.examples.messages.weather.WeatherGrpc;
import io.github.jonloucks.examples.messages.weather.WeatherOuterClass.WeatherReply;
import io.github.jonloucks.examples.messages.weather.WeatherOuterClass.WeatherRequest;
import io.grpc.*;
import io.grpc.protobuf.services.HealthStatusManager;

import java.util.concurrent.TimeUnit;

import static io.github.jonloucks.concurrency.api.Idempotent.withClose;
import static io.github.jonloucks.concurrency.api.Idempotent.withOpen;
import static io.github.jonloucks.contracts.api.Checks.configCheck;
import static io.github.jonloucks.contracts.api.Checks.nullCheck;
import static io.github.jonloucks.metalog.api.GlobalMetalog.publish;
import static java.util.Optional.ofNullable;

final class ClientImpl implements Client {
    
    @Override
    public AutoClose open() {
        return withOpen(stateMachine, this::realOpen);
    }
    
    @Override
    public WaitableNotify<Idempotent> lifeCycleNotify() {
        return stateMachine;
    }
    
    @Override
    public String getWeatherReport() {
        try {
            final WeatherRequest weatherRequest = WeatherRequest.newBuilder()
                .addLocation("current")
                .build();
            final WeatherReply reply = blockingStub.sayWeather(weatherRequest);
            return reply.getReport();
        } catch (StatusRuntimeException thrown) {
            publish(() -> "RPC failed: " + thrown.getStatus(), b -> b.thrown(thrown));
        }
        return null;
    }
    
    ClientImpl(Config config, Repository repository, boolean openRepository) {
        this.config = configCheck(config);
        final Repository validRepository = nullCheck(repository, "Repository must be present.");
        this.closeRepository = openRepository ? validRepository.open() : AutoClose.NONE;
        this.stateMachine = Idempotent.createStateMachine(config.contracts());
    }
    
    private AutoClose realOpen() {
        final String target = config.hostname() + ":" + config.port();
        channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
            .build();

        blockingStub = WeatherGrpc.newBlockingStub(channel);
        
        return this::exposedClose;
    }

    private void exposedClose() {
        withClose(stateMachine, this::realClose);
    }
    
    private void realClose() {
        ofNullable(channel).ifPresent(c -> {
            c.shutdown();
            try {
                c.awaitTermination(config.shutdownTimeout().toMillis(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        });
        closeRepository.close();
    }
    
    private final Config config;
    private final AutoClose closeRepository;
    private final StateMachine<Idempotent> stateMachine;
    
    private final HealthStatusManager health = new HealthStatusManager();

    private ManagedChannel channel;
    private WeatherGrpc.WeatherBlockingStub blockingStub;
}
