package ru.system.authentication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.system.authentication.repository.SensorRepository;
import ru.system.library.dto.common.sensor.SensorPairDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorService {
    private final SensorRepository sensorRepository;

    public List<SensorPairDTO> getSensors() {
        return sensorRepository.getAllSensorPairs();
    }
}
