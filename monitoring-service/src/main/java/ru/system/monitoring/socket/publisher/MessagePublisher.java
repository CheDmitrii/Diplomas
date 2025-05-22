package ru.system.monitoring.socket.publisher;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MessagePublisher {
    private final Map<String, Sinks.Many<String>> topicSinks = new ConcurrentHashMap<>();

    public void publish(String topic, String message) {
        topicSinks.computeIfAbsent(topic, key -> Sinks.many().multicast().onBackpressureBuffer())
                .tryEmitNext(message);
    }

    public Sinks.Many<String> getSink(String topic) {
        return topicSinks.computeIfAbsent(topic, key -> Sinks.many().multicast().onBackpressureBuffer());
    }
}
