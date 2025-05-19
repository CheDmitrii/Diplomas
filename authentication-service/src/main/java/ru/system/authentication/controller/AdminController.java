package ru.system.authentication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.system.authentication.DTO.user.CreateUserRequestDTO;
import ru.system.authentication.DTO.user.UpdateUserSensorRequestDTO;
import ru.system.authentication.repository.SensorPermissionRepository;
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

    private final SensorPermissionRepository sensorPermissionRepository;
    private final AdminService adminService;
    private final SensorPermissionService sensorPermissionService;
    private final SensorService sensorService;

    @PostMapping("/create-user") // todo: ready
    public ResponseEntity<Void> createUser(@RequestBody CreateUserRequestDTO createUserRequestDTO) {
        adminService.createUser(createUserRequestDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/update-sensor/{id:.+}") // todo: ready
    public ResponseEntity<Void> updateSensor(@RequestBody UpdateUserSensorRequestDTO updateDTO,
                                                   @PathVariable("id") UUID userId) {
        adminService.changeUserSensors(userId, updateDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sensor/all-sensors") // todo: ready
    public ResponseEntity<List<SensorPairDTO>> getAllSensors() {
        return ResponseEntity.ok(sensorService.getSensors());
    }

    @GetMapping("/user-sensor/{id:.+}") // todo: ready
    public ResponseEntity<List<SensorPairDTO>> getUserSensors(@PathVariable("id") UUID userId) {
        return ResponseEntity.ok(sensorPermissionService.getUserSensor(userId));
    }

    @GetMapping("/not-user-sensor/{id:.+}") // todo: ready
    public ResponseEntity<List<SensorPairDTO>> getNotUserSensors(@PathVariable("id") UUID userId) {
        return ResponseEntity.ok(sensorPermissionService.getNotUserSensor(userId));
    }

    @GetMapping("/test")
    public ResponseEntity<List<SensorPairDTO>> testMethod() { // todo: remove
        return ResponseEntity.ok(sensorPermissionRepository.findAllSensorsOfUser(UUID.fromString("15ad4a35-a925-4b92-b54a-4030a412b846")));
    }
}
