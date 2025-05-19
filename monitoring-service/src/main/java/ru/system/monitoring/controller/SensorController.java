package ru.system.monitoring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.system.library.dto.common.sensor.SensorCheckedDTO;
import ru.system.library.dto.common.sensor.SensorDTO;
import ru.system.monitoring.service.ClaimService;
import ru.system.monitoring.service.SensorService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/sensor")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;
    private final ClaimService claimService;
    // todo: add machineDTO - fix

    @GetMapping("/{id:.+}")
    public Mono<ResponseEntity<SensorDTO>> getSensorById(@PathVariable("id") final UUID id) { // todo: change from id
        return Mono.fromCallable(() ->
                ResponseEntity.ok(sensorService.getSensorById(id, UUID.fromString("15ad4a35-a925-4b92-b54a-4030a412b846")))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/all-sensors")
    public Mono<ResponseEntity<List<SensorDTO>>> getAllSensors() { // todo: change from id
        return Mono.fromCallable(() ->
                ResponseEntity.ok(sensorService.getAllSensors(UUID.fromString("15ad4a35-a925-4b92-b54a-4030a412b846")))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/check-sensors")
    public Mono<ResponseEntity<SensorCheckedDTO[]>> checkSensors() {
        return Mono.fromCallable(
                () -> {
                    String role = claimService.getRole();
                    if (role != null && role.equalsIgnoreCase("admin")) {
                        return ResponseEntity.ok(sensorService.checkSensor(null));
                    }
                    return ResponseEntity.ok(sensorService.checkSensor(claimService.getUserId()));
                }
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Map<String, UUID>>> createSensor(@RequestBody @Valid SensorDTO sensor) {
        return Mono.fromCallable(
                    () -> sensorService.createSensor(sensor)
                )
                .map(
                        v -> ResponseEntity.ok(Map.of("id", v))
                ).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/test")
    public Integer testCal() throws ExecutionException, InterruptedException {
        return sensorService.getValuetTest();
    }

    @GetMapping("/test-method")
    public SensorCheckedDTO[] testCall() throws ExecutionException, InterruptedException {
        return sensorService.testMethod();
    }
}
