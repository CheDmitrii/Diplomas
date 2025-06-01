package ru.system.monitoring.socket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.system.monitoring.socket.SocketSubscriptionManager;
import ru.system.monitoring.socket.publisher.MessagePublisher;

@Component
@RequiredArgsConstructor
public class SubscribeMessageHandler implements SocketMessageHandler {

    private final MessagePublisher messagePublisher;
    private final SocketSubscriptionManager subscriptionManager;

    @Override
    public String getType() {
        return "subscribe";
    }

    @Override
    public Mono<Void> handle(JsonNode messageJson, WebSocketSession session) {
        if (!messageJson.has("topic")) {
            return Mono.empty();
        }
        String topic = messageJson.get("topic").asText();
        String sessionId = session.getId();

        // Подписываемся на поток сообщений по топику
        Flux<String> messageFlux = messagePublisher.getSink(topic).asFlux();

        Disposable subscription = messageFlux
                .flatMap(message -> session.send(Mono.just(session.textMessage(message))))
                .subscribe();

        subscriptionManager.subscribe(sessionId, topic, subscription);

        return Mono.empty();
    }
}
