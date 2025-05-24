package ru.system.monitoring.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.system.library.dto.common.reference.ReferenceDTO;
import ru.system.monitoring.service.ClaimService;
import ru.system.monitoring.service.ReferenceService;

import java.util.UUID;

@RestController
@RequestMapping("/reference")
@RequiredArgsConstructor
public class ReferenceController {

    private final ReferenceService referenceService;
    private final ClaimService claimService;

    @GetMapping("/history/all")
    public Flux<ReferenceDTO> getReferences() {
        return claimService.getUserId()
                .map(referenceService::getAllReferences)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    @GetMapping("/history/{id:.+}")
    public Mono<ResponseEntity<ReferenceDTO>> getReferenceById(@PathVariable("id") @NotNull final UUID referenceId) {
        return claimService.getUserId()
                .map(userId -> referenceService.getReference(referenceId, userId))
                .subscribeOn(Schedulers.boundedElastic())
                .map(ResponseEntity::ok);
    }
}
