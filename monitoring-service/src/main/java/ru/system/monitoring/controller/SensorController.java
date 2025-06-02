package ru.system.monitoring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.system.library.dto.common.sensor.SensorCheckedDTO;
import ru.system.library.dto.common.sensor.SensorDTO;
import ru.system.library.exception.HttpResponseEntityException;
import ru.system.monitoring.service.ClaimService;
import ru.system.monitoring.service.SensorService;
import ru.system.monitoring.socket.publisher.MessagePublisher;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/sensor")
@RequiredArgsConstructor
@Slf4j
public class SensorController {

    private final SensorService sensorService;
    private final ClaimService claimService;
    private final MessagePublisher messagePublisher;

    @GetMapping("/{id:.+}")
    public Mono<ResponseEntity<SensorDTO>> getSensorById(@PathVariable("id") final UUID sensorId) {
        return claimService.hasFullPermission()
                .flatMap(fullPermission -> {
                    if (fullPermission) {
                        return Mono.just(ResponseEntity.ok(sensorService.getSensorById(sensorId)));
                    }
                    return claimService.getUserId().map(userId -> ResponseEntity.ok(sensorService.getSensorById(sensorId, userId)));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/all-sensors")
    public Mono<ResponseEntity<List<SensorDTO>>> getAllSensors() {
        return claimService.hasFullPermission()
                .flatMap(fullPermission -> {
                    log.error("------------------ inside all-sensors ------------------");
                    if (fullPermission) {
                        return Mono.just(ResponseEntity.ok(sensorService.getAllSensors()));
                    }
                    return claimService.getUserId().map(userId -> ResponseEntity.ok(sensorService.getAllSensors(userId)));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/check-sensors")
    public Mono<ResponseEntity<SensorCheckedDTO[]>> checkSensors() {
        return claimService.hasFullPermission()
                .flatMap(fullPermission -> {
                    if (fullPermission) {
                        return Mono.just(sensorService.checkSensor(null));
                    }
                    return claimService.getUserId().map(sensorService::checkSensor);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(ResponseEntity::ok);
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<Map<String, UUID>>> createSensor(@RequestBody @Valid SensorDTO sensor) {
        return claimService.hasFullPermission()
                .flatMap(fullPermission -> {
                    if (!fullPermission) {
                        throw new HttpResponseEntityException(HttpStatus.FORBIDDEN, "Access denied");
                    }
                    return Mono.just(ResponseEntity.ok(
                            Map.of("id", sensorService.createSensor(sensor))
                    ));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/send-data/{id:.+}")
    public Mono<Void> testMethodSensData(@PathVariable("id") UUID sensorId, @RequestBody int value) {
        messagePublisher.publish("/topic/journal/" + sensorId, String.valueOf(value));
        return Mono.fromCallable(() -> {
                    messagePublisher.publish("/topic/journal/" + sensorId, String.valueOf(value));
                    return null;
                })
                .onErrorResume(e -> {
                    log.error("Error processing update", e);
                    return Mono.empty();
                }).then();
    }
}
