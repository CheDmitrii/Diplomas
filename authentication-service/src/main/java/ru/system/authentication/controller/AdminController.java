package ru.system.authentication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.system.authentication.DTO.user.CreateUserRequestDTO;
import ru.system.authentication.DTO.user.UpdateUserSensorRequestDTO;
import ru.system.authentication.service.AdminService;
import ru.system.authentication.service.SensorPermissionService;
import ru.system.authentication.service.SensorService;
import ru.system.library.dto.common.sensor.SensorPairDTO;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final SensorPermissionService sensorPermissionService;
    private final SensorService sensorService;

    @PostMapping("/create-user")
    public ResponseEntity<Void> createUser(@RequestBody CreateUserRequestDTO createUserRequestDTO) {
        adminService.createUser(createUserRequestDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/update-sensor/{id:.+}")
    public ResponseEntity<Void> updateSensor(@RequestBody UpdateUserSensorRequestDTO updateDTO,
                                                   @PathVariable("id") UUID userId) {
        adminService.changeUserSensors(userId, updateDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sensor/all-sensors")
    public ResponseEntity<List<SensorPairDTO>> getAllSensors() {
        return ResponseEntity.ok(sensorService.getSensors());
    }

    @GetMapping("/user-sensor/{id:.+}")
    public ResponseEntity<List<SensorPairDTO>> getUserSensors(@PathVariable("id") UUID userId) {
        return ResponseEntity.ok(sensorPermissionService.getUserSensor(userId));
    }

    @GetMapping("/not-user-sensor/{id:.+}")
    public ResponseEntity<List<SensorPairDTO>> getNotUserSensors(@PathVariable("id") UUID userId) {
        return ResponseEntity.ok(sensorPermissionService.getNotUserSensor(userId));
    }
}
