package ru.system.monitoring.socket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.system.monitoring.socket.publisher.MessagePublisher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SubscribeMessageHandler implements SocketMessageHandler {

    private final MessagePublisher messagePublisher;

    // Храним подписки: sessionId -> Disposable (отписка)
    private final Map<String, Disposable> subscriptions = new ConcurrentHashMap<>();

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

        // Отписываемся от предыдущей подписки, если есть
        Disposable oldSubscription = subscriptions.remove(session.getId());
        if (oldSubscription != null && !oldSubscription.isDisposed()) {
            oldSubscription.dispose();
        }

        // Подписываемся на поток сообщений из MessagePublisher по топику
        Flux<String> messageFlux = messagePublisher.getSink(topic).asFlux();

        // Отправляем сообщения клиенту
        Disposable subscription = messageFlux
                .flatMap(message -> session.send(Mono.just(session.textMessage(message))))
                .subscribe();

        // Сохраняем подписку, чтобы потом можно было отписаться
        subscriptions.put(session.getId(), subscription);

        return Mono.empty();
    }
}
