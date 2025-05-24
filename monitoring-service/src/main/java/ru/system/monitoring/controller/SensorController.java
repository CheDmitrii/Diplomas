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

    @GetMapping("/{id:.+}")
    public Mono<ResponseEntity<SensorDTO>> getSensorById(@PathVariable("id") final UUID id) {
//        boolean hasFullPermission = claimService.hasFullPermission(); // todo: when implement flux put it inside mono
//        UUID userId = claimService.getUserId();
//        boolean hasFullPermission = true;
//        UUID userId = UUID.randomUUID();
//        return Mono.fromCallable(() -> {
//                    if (hasFullPermission) {
//                        sensorService.getSensorById(id);
//                    }
//                    return ResponseEntity.ok(sensorService.getSensorById(id, userId));
//                }
//        ).subscribeOn(Schedulers.boundedElastic());
        return claimService.hasFullPermission()
                .flatMap(fullPermission -> {
                    if (fullPermission) {
                        return Mono.just(ResponseEntity.ok(sensorService.getSensorById(id)));
                    }
                    return claimService.getUserId().map(userId -> ResponseEntity.ok(sensorService.getSensorById(id, userId)));
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/all-sensors")
    public Mono<ResponseEntity<List<SensorDTO>>> getAllSensors() {
//        boolean hasFullPermission = claimService.hasFullPermission(); // todo: when implement flux put it inside mono
//        UUID userId = claimService.getUserId();
        return claimService.hasFullPermission()
                .flatMap(fullPermission -> {
                    log.error("------------------ inside all-sensors ------------------");
                    if (fullPermission) {
                        return Mono.just(ResponseEntity.ok(sensorService.getAllSensors()));
                    }
                    return claimService.getUserId().map(userId -> ResponseEntity.ok(sensorService.getAllSensors(userId)));
                })
                .subscribeOn(Schedulers.boundedElastic());
//        return Mono.fromCallable(() -> {
//                    if (hasFullPermission) {
//                        return ResponseEntity.ok(sensorService.getAllSensors());
//                    }
//                    return ResponseEntity.ok(sensorService.getAllSensors(userId));
//                }
//        ).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/check-sensors")
    public Mono<ResponseEntity<SensorCheckedDTO[]>> checkSensors() {
//        boolean hasFullPermission = claimService.hasFullPermission(); // todo: when implement flux put it inside mono
//        UUID userId = claimService.getUserId();
//        boolean hasFullPermission = true;
//        UUID userId = UUID.randomUUID();
//        return Mono.fromCallable(
//                () -> {
//                    if (hasFullPermission) {
//                        return ResponseEntity.ok(sensorService.checkSensor(null));
//                    }
//                    return ResponseEntity.ok(sensorService.checkSensor(userId));
//                }
//        ).subscribeOn(Schedulers.boundedElastic());
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
//        boolean hasFullPermission = claimService.hasFullPermission(); // todo: when implement flux put it inside mono
//        boolean hasFullPermission = true;
//        return Mono.fromCallable(
//                    () -> {
//                        if (!hasFullPermission) {
//                            throw new HttpResponseEntityException(HttpStatus.FORBIDDEN, "Access denied");
//                        }
//                        return sensorService.createSensor(sensor);
//                    }
//                )
//                .map(v -> ResponseEntity.ok(Map.of("id", v)))
//                .subscribeOn(Schedulers.boundedElastic());
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
}
