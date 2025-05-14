package ru.system.authentication.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.system.authentication.repository.SensorPermissionRepository;

@Service
@RequiredArgsConstructor
public class SensorPermissionService {
    // todo: implement and add to admin controller
    private final SensorPermissionRepository sensorPermissionRepository;
}
