package ru.system.monitoring.socket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

public interface SocketMessageHandler {
    /**
     * Возвращает тип сообщений, которые обрабатывает этот хендлер.
     * Например: "reference/update", "chat/message" и т.п.
     */
    String getType();

    /**
     * Обрабатывает входящее сообщение.
     *
     * @param messageJson JSON-узел с данными сообщения
     * @param session    WebSocket сессия клиента
     * @return Mono<Void> для асинхронного завершения обработки
     */
    Mono<Void> handle(JsonNode messageJson, WebSocketSession session);
}
