package ru.system.monitoring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.system.library.dto.common.SensorJournalEntityDTO;
import ru.system.library.exception.HttpResponseEntityException;
import ru.system.monitoring.kafka.KafkaJournalProducer;
import ru.system.monitoring.repository.SensorRepository;

@Service
@RequiredArgsConstructor
public class JournalService {
    @Value("${spring.kafka.topics.journal-topic}")
    private String TOPIC_JOURNAL_VALUE;

    private final KafkaJournalProducer journalProducer;
    private final SensorRepository sensorRepository;

    public void sendJournal(SensorJournalEntityDTO sensorJournalEntityDTO) {
        if (!sensorRepository.existsSensor(sensorJournalEntityDTO.getId())) {
            throw new HttpResponseEntityException(
                    HttpStatus.NOT_FOUND,
                    "Sensor with this id {%s} doesn't exist".formatted(sensorJournalEntityDTO.getId())
            );
        }
        journalProducer.send(TOPIC_JOURNAL_VALUE, sensorJournalEntityDTO);
    }
}
