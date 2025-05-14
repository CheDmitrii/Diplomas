package ru.system.monitoring.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.system.library.dto.common.SensorJournalEntityDTO;

@Component
@RequiredArgsConstructor
public class KafkaJournalProducer {
    private final KafkaTemplate<String, SensorJournalEntityDTO> kafkaTemplate;

    public void send(String topic, SensorJournalEntityDTO journal) {
        kafkaTemplate.send(topic, journal.getId().toString(), journal);
    }
}
