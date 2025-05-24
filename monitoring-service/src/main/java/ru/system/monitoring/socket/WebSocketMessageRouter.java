package ru.system.monitoring.socket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import ru.system.monitoring.socket.handler.SocketMessageHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WebSocketMessageRouter implements WebSocketHandler {
    private final Map<String, SocketMessageHandler> handlers;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebSocketMessageRouter(List<SocketMessageHandler> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(SocketMessageHandler::getType, Function.identity()));
    }


    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.receive()
                .map(webSocketMessage -> webSocketMessage.getPayloadAsText())
                .flatMap(payload -> {
                    try {
                        log.info("--------- socket message received ---------");
                        log.info(payload);
                        JsonNode jsonNode = objectMapper.readTree(payload);
                        String key = jsonNode.has("action") ? jsonNode.get("action").asText()
                                : jsonNode.has("type") ? jsonNode.get("type").asText()
                                : null;
                        if (key == null) {
                            // Неизвестный тип сообщения
                            return Mono.empty();
                        }
                        SocketMessageHandler handler = handlers.get(key);
                        return handler.handle(jsonNode, session);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Mono.empty();
                    }
                })
                .then();
    }
}


//const socket = new WebSocket("ws://localhost:8228/socket"); //  wss if https
// добавить токен при подключении
//socket.onopen = () => {
//        // Отправка сообщения с типом и данными
//        const message = JSON.stringify({
//    type: "reference/update",
//            data: {
//        id: 123,
//        // другие поля
//    }
//});
//        socket.send(message);
//};
//
//socket.onmessage = (event) => {
//        const msg = JSON.parse(event.data);
//// Обработка сообщений обновления
//  console.log("Update received:", msg);
//};