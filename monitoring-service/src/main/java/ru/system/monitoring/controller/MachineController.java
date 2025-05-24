package ru.system.monitoring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.system.library.dto.common.MachineDTO;
import ru.system.monitoring.service.ClaimService;
import ru.system.monitoring.service.MachineService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/machine")
public class MachineController {

    private final MachineService machineService;
    private final ClaimService claimService;

    @GetMapping("/{id:.+}")
    public Mono<ResponseEntity<MachineDTO>> getMachine(@PathVariable("id") UUID machineId) {
//        UUID userId = claimService.getUserId().block(); // todo: when implement flux put it inside mono
        return claimService.getUserId()
                .map(userId -> machineService.getMachine(machineId, userId))
                .subscribeOn(Schedulers.boundedElastic())
                .map(ResponseEntity::ok);
//        return Mono.fromCallable(() ->
//                        ResponseEntity.ok(machineService.getMachine(machineId, userId)))
//                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/all-machines")
    public Mono<ResponseEntity<List<MachineDTO>>> getMachines() {
//        UUID userId = claimService.getUserId().block(); // todo: when implement flux put it inside mono
        return claimService.getUserId()
                .map(machineService::getAllMachines)
                .subscribeOn(Schedulers.boundedElastic())
                .map(ResponseEntity::ok);
//        return Mono.fromCallable(() ->
//                ResponseEntity.ok(machineService.getAllMachines(userId))
//        ).subscribeOn(Schedulers.boundedElastic());
    }
}
