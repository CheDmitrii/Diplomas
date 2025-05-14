package ru.system.authentication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.system.authentication.DTO.user.CreateUserRequestDTO;
import ru.system.authentication.DTO.user.UpdateUserSensorRequestDTO;
import ru.system.authentication.repository.SensorPermissionRepository;
import ru.system.library.dto.common.sensor.SensorPairDTO;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final SensorPermissionRepository sensorPermissionRepository;

//    @GetMapping("/roles")
//    public ResponseEntity<Void> testRoles() {
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/create-user")
    public Mono<ResponseEntity<Void>> createUser(@RequestBody CreateUserRequestDTO createUserRequestDTO) {
        return Mono.just(ResponseEntity.ok().build());
    }

    @PostMapping("/user/update-sensor/{id:.+}") // todo: implement
    public Mono<ResponseEntity<Void>> updateSensor(@RequestBody UpdateUserSensorRequestDTO sensors,
                                                   @PathVariable("id") UUID userId) {
        return null;
//        return Mono
//                .fromCallable(
//                )
//                .subscribeOn();
    }

//    public Mono<ResponseEntity<?>> // todo: think

    @GetMapping("/sensor/all-sensors")
    public Mono<ResponseEntity<List<SensorPairDTO>>> getAllSensors() {
        return null;
    }

    @GetMapping("/test")
    public ResponseEntity<List<SensorPairDTO>> testMethod() {
        return ResponseEntity.ok(sensorPermissionRepository.findAllSensorsOfUser(UUID.fromString("15ad4a35-a925-4b92-b54a-4030a412b846")));
    }
}
