package ru.system.monitoring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.system.library.dto.common.SensorJournalEntityDTO;
import ru.system.monitoring.repository.repository.SensorJournalRepository;
import ru.system.monitoring.repository.repository.SensorRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JournalService {
    private final SensorJournalRepository sensorJournalRepository;
    private final SensorRepository sensorRepository;

    @Async
    public void saveJournal(SensorJournalEntityDTO sensorJournalEntityDTO) {
        sensorJournalRepository.writeJournal(sensorJournalEntityDTO);
    }

    public boolean isSensorExist(UUID id) {
        return sensorRepository.existsSensor(id);
    }


    public List<SensorJournalEntityDTO> getSensorJournal(UUID sensorId) {
        return sensorJournalRepository.getAllJournalsBySensor(sensorId);
    }

    public List<SensorJournalEntityDTO> getAllSensorsData(UUID userId) {
        return sensorJournalRepository.getAllJournals(userId);
    }
}
