package ru.system.monitoring.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.system.library.dto.common.ReferenceDTO;
import ru.system.monitoring.dto.RequestUpdateReferenceDTO;
import ru.system.monitoring.service.ClaimService;
import ru.system.monitoring.service.ReferenceService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/reference")
@RequiredArgsConstructor
public class ReferenceController {

    private final ReferenceService referenceService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ClaimService claimService; //todo: start use

    @MessageMapping("/reference/update") // for this annotation doesn't work @RequestMapping (send on "/app" (from socket config) + "/reference/update")
    public Mono<Void> changeReference(@Valid @NotNull RequestUpdateReferenceDTO update) {
        Timestamp time = Timestamp.valueOf(LocalDateTime.now());
        return referenceService
                .saveChanges(update, time)
                .doOnSuccess(v ->
                        messagingTemplate.convertAndSend("/topic/references" + update.getId(), update)
                )
                .then();
    }

    @GetMapping("/history/all")
    public Flux<ReferenceDTO> getReferences() {
//        return Flux.fromIterable(referenceService.getAllReferences());
        return Mono.fromCallable(() ->
                        referenceService.getAllReferences(UUID.fromString("15ad4a35-a925-4b92-b54a-4030a412b846"))
                )
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    @GetMapping("/history/{id:.+}")
    public Mono<ReferenceDTO> getReferenceById(@PathVariable("id") @NotNull final UUID id) {
        return Mono.fromCallable(() ->
                        referenceService.getReference(id, UUID.fromString("15ad4a35-a925-4b92-b54a-4030a412b846"))
                )
                .subscribeOn(Schedulers.boundedElastic());
    }
}
