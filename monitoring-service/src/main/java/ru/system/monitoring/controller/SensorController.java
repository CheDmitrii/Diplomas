package ru.system.monitoring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.system.library.dto.common.SensorDTO;
import ru.system.monitoring.service.SensorService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sensor")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @GetMapping("/{id:.+}")
    public Mono<ResponseEntity<SensorDTO>> getSensorById(@PathVariable("id") final UUID id) {
        return Mono.just(ResponseEntity.ok(sensorService.getSensorById(id)));
    }

    @GetMapping("/all-sensors")
    public Mono<ResponseEntity<List<SensorDTO>>> getAllSensors() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null && authentication.getCredentials() == null) {
            System.out.println((Jwt) authentication.getCredentials());
        } // todo implements service to get from jwt userid
        return Mono.just(ResponseEntity.ok(sensorService.getAllSensors()));
    }
}
