package ru.system.monitoring.socket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import ru.system.monitoring.dto.RequestUpdateReferenceDTO;
import ru.system.monitoring.service.ReferenceService;
import ru.system.monitoring.socket.publisher.MessagePublisher;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReferenceUpdateMessageHandler implements SocketMessageHandler {

    private final ObjectMapper objectMapper;
    private final ReferenceService referenceService;
    private final MessagePublisher messagePublisher; // сервис для публикации сообщений всем подписчикам

    @Override
    public String getType() {
        return "reference/update";
    }

    @Override
    public Mono<Void> handle(JsonNode messageJson, WebSocketSession session) {
        try {
            RequestUpdateReferenceDTO update = objectMapper.treeToValue(messageJson.get("data"), RequestUpdateReferenceDTO.class);
            Timestamp time = Timestamp.valueOf(LocalDateTime.now());

            return referenceService.saveChanges(update, time)
                    .doOnSuccess(v -> {
                        // Публикуем обновление всем подписчикам
                        try {
                            messagePublisher.publish("/topic/references/" + update.getId(),
                                    objectMapper.writeValueAsString(update));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .then();
        } catch (Exception e) {
            e.printStackTrace();
            return Mono.empty();
        }
    }
}
