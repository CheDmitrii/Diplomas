package ru.system.monitoring.socket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import ru.system.monitoring.socket.SocketSubscriptionManager;

@Component
@RequiredArgsConstructor
public class UnsubscribeMessageHandler implements SocketMessageHandler {
    private final SocketSubscriptionManager socketSubscriptionManager;

    @Override
    public String getType() {
        return "unsubscribe";
    }

    @Override
    public Mono<Void> handle(JsonNode messageJson, WebSocketSession session) {
        if (!messageJson.has("topic")) {
            return Mono.empty();
        }
        String topic = messageJson.get("topic").asText();
        String sessionId = session.getId();

        // Отписываемся от конкретного топика для данного sessionId
        socketSubscriptionManager.unsubscribe(sessionId, topic);

        return Mono.empty();
    }
}
