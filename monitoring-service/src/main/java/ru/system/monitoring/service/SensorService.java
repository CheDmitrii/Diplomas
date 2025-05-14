package ru.system.monitoring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.system.library.dto.common.SensorJournalEntityDTO;
import ru.system.library.dto.common.sensor.SensorDTO;
import ru.system.library.exception.HttpResponseEntityException;
import ru.system.monitoring.repository.repository.SensorPermissionRepository;
import ru.system.monitoring.repository.repository.SensorRepository;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;
    private final JournalService journalService;
    private final SensorPermissionRepository sensorPermissionRepository;

    public SensorDTO getSensorById(UUID sensorId, UUID userId) {
        if (!sensorRepository.existsSensor(sensorId)) {
            throw new HttpResponseEntityException(HttpStatus.NOT_FOUND,
                    "Sensor with this sensorId {%s} doesn't exist".formatted(sensorId));
        }
        if (!sensorPermissionRepository.isAllowedSensor(userId, sensorId)) {
            throw new HttpResponseEntityException(HttpStatus.FORBIDDEN,
                    "Sensor with this sensorId {%s} doesn't allowed".formatted(sensorId));
        }
        SensorDTO sensorInfo = sensorRepository.getSensorInfo(sensorId);
        sensorInfo.setJournal(journalService.getSensorJournal(sensorId));
        return sensorInfo;
    }

    public List<SensorDTO> getAllSensors(UUID userId) {
        List<SensorDTO> allSensors = sensorRepository.getAllSensors(userId);
        Map<UUID, List<SensorJournalEntityDTO>> collectJournal = journalService.getAllSensorsData(userId)
                .stream()
                .collect(
                        Collectors.groupingBy(
                                SensorJournalEntityDTO::getId,
                                TreeMap::new,
                                Collectors.mapping(
                                        v -> {
                                            v.setId(null);
                                            return v;
                                            }, // JournalEntityDTO.builder().value(v.getValue()).time(v.getTime()).build()
                                        Collectors.toList()
                                )
                        )
                );
        allSensors.forEach(v -> v.setJournal(collectJournal.get(v.getId())));
        return allSensors;
    }
}
