package ru.system.monitoring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
public class SensorController {

    private final SensorService sensorService;
    private final ClaimService claimService;
    // todo: add machineDTO - fix

    @GetMapping("/{id:.+}")
    public ResponseEntity<SensorDTO> getSensorById(@PathVariable("id") final UUID id) { // todo: change from id
        if (claimService.hasFullPermission()) {
            return ResponseEntity.ok(sensorService.getSensorById(id));
        }
        return ResponseEntity.ok(sensorService.getSensorById(id, claimService.getUserId()));
    }

    @GetMapping("/all-sensors")
    public ResponseEntity<List<SensorDTO>> getAllSensors() { // todo: change from id
        if (claimService.hasFullPermission()) {
            return ResponseEntity.ok(sensorService.getAllSensors());
        }
        return ResponseEntity.ok(sensorService.getAllSensors(claimService.getUserId()));
    }

    @GetMapping("/check-sensors")
    public ResponseEntity<SensorCheckedDTO[]> checkSensors() {
        if (claimService.hasFullPermission()) {
            return ResponseEntity.ok(sensorService.checkSensor(null));
        }
        return ResponseEntity.ok(sensorService.checkSensor(claimService.getUserId()));
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, UUID>> createSensor(@RequestBody @Valid SensorDTO sensor) {
        if (!claimService.hasFullPermission()) {
            throw new HttpResponseEntityException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return ResponseEntity.ok(Map.of("id", sensorService.createSensor(sensor)));
    }
}
