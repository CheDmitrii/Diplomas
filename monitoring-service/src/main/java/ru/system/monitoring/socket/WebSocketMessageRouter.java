package ru.system.monitoring.socket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import ru.system.monitoring.socket.handler.SocketMessageHandler;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WebSocketMessageRouter implements WebSocketHandler {
    private final ReactiveJwtDecoder jwtDecoder;
    private final Map<String, SocketMessageHandler> handlers;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SocketSubscriptionManager subscriptionManager;

    public WebSocketMessageRouter(List<SocketMessageHandler> handlers, SocketSubscriptionManager subscriptionManager, ReactiveJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
        this.subscriptionManager = subscriptionManager;
        this.handlers = handlers.stream().collect(Collectors.toMap(SocketMessageHandler::getType, Function.identity()));
    }


    @Override
    public Mono<Void> handle(WebSocketSession session) {
        URI uri = session.getHandshakeInfo().getUri();
        String token = UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .getFirst("token");

        if (token == null) {
            return session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Missing token"));
        }
        return this.validateJwtToken(token)
                .flatMap(jwt ->
                    session.receive()
                            .map(WebSocketMessage::getPayloadAsText)
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
                            .doFinally(signalType -> subscriptionManager.cleanupSession(session.getId()))
                            .then()
                            .onErrorResume(e -> {
                                log.error("Error processing update", e);
                                // Можно отправить сообщение об ошибке клиенту или просто завершить
                                return Mono.empty();
                            })
                )
                .onErrorResume(e -> session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invallid token")));
    }

    public Mono<Jwt> validateJwtToken(String token) {
        return jwtDecoder.decode(token);
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