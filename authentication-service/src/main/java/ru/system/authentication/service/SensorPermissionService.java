package ru.system.authentication.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.system.authentication.entity.permission.SensorPermission;
import ru.system.authentication.repository.SensorPermissionRepository;
import ru.system.library.dto.common.sensor.SensorPairDTO;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SensorPermissionService {
    private final SensorPermissionRepository sensorPermissionRepository;

    public List<SensorPairDTO> getUserSensor(UUID userId) {
        return sensorPermissionRepository.findAllSensorsOfUser(userId);
    }
    public List<SensorPairDTO> getNotUserSensor(UUID userId) {
        return sensorPermissionRepository.findAllSensorsOfNotUser(userId);
    }

    public void addSensorsForUser(UUID userId, List<UUID> sensorIds) {
        if (sensorIds == null || sensorIds.isEmpty()) {
            return;
        }
        sensorPermissionRepository.saveAll(
                sensorIds.stream()
                        .map(sensorId -> SensorPermission.builder()
                                .sensorId(sensorId)
                                .userId(userId)
                                .build())
                        .toList());
    }

    public void removeSensorsForUser(UUID userId, List<UUID> sensorIds) {
        if (sensorIds == null || sensorIds.isEmpty()) {
            return;
        }
        sensorPermissionRepository.deleteAll(
                sensorIds.stream()
                        .map(sensorId -> SensorPermission.builder()
                                .sensorId(sensorId)
                                .userId(userId)
                                .build())
                        .toList());
    }
}
