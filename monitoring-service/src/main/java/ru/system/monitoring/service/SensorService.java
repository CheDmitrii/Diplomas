package ru.system.monitoring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.system.library.dto.common.SensorJournalEntityDTO;
import ru.system.library.dto.common.sensor.SensorCheckedDTO;
import ru.system.library.dto.common.sensor.SensorDTO;
import ru.system.library.exception.HttpResponseEntityException;
import ru.system.monitoring.OPCUA.OPCUASubscriber;
import ru.system.monitoring.repository.repository.ReferenceRepository;
import ru.system.monitoring.repository.repository.SensorPermissionRepository;
import ru.system.monitoring.repository.repository.SensorRepository;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;
    private final ReferenceRepository referenceRepository;
    private final JournalService journalService;
    private final SensorPermissionRepository sensorPermissionRepository;
    private final OPCUASubscriber opcuaSubscriber;

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

    public SensorDTO getSensorById(UUID sensorId) {
        if (!sensorRepository.existsSensor(sensorId)) {
            throw new HttpResponseEntityException(HttpStatus.NOT_FOUND,
                    "Sensor with this sensorId {%s} doesn't exist".formatted(sensorId));
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

    public List<SensorDTO> getAllSensors() {
        List<SensorDTO> allSensors = sensorRepository.getAllSensors();
        Map<UUID, List<SensorJournalEntityDTO>> collectJournal = journalService.getAllSensorsData()
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

    public UUID createSensor(SensorDTO createSensor) throws ExecutionException, JsonProcessingException, InterruptedException {
        UUID sensorId = sensorRepository.createSensor(createSensor);
        if (createSensor.getReference() != null) {
            referenceRepository.createReference(createSensor.getReference(), createSensor, sensorId);
        }
        opcuaSubscriber.createSensor(createSensor);
        return sensorId;
    }

    public SensorCheckedDTO[] checkSensor(UUID userId) throws ExecutionException, InterruptedException {
        if (userId == null) {
            return opcuaSubscriber.checkSensors();
        }
        return opcuaSubscriber.checkSensors(sensorPermissionRepository.getUserSensors(userId));
    }
}
