package ru.system.monitoring.repository.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import ru.system.library.dto.common.SensorJournalEntityDTO;
import ru.system.library.sql.repository.mapper.SensorJournalEntityMapperCut;
import ru.system.library.sql.repository.mapper.SensorJournalEntityMapper;
import ru.system.library.sql.queries.SensorJournalSQLQueries;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SensorJournalRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SensorJournalEntityMapper sensorJournalEntityMapper;
    private final SensorJournalEntityMapperCut sensorJournalEntityMapperCut;

    public void writeJournal(SensorJournalEntityDTO sensorJournalEntityDTO) {
        namedParameterJdbcTemplate.update(
                SensorJournalSQLQueries.WRITE_JOURNAL,
                Map.of(
                        "sensor_id", sensorJournalEntityDTO.getId(),
                        "value", sensorJournalEntityDTO.getValue(),
                        "time", sensorJournalEntityDTO.getTime()
                )
        );
    }

    public List<SensorJournalEntityDTO> getAllJournalsBySensor(UUID sensor_id) {
        List<SensorJournalEntityDTO> result = namedParameterJdbcTemplate.query(
                SensorJournalSQLQueries.GET_JOURNALS_BY_ID,
                Map.of("sensor_id", sensor_id),
                sensorJournalEntityMapperCut
        );
        result.sort(Comparator.comparing(SensorJournalEntityDTO::getTime));
        return result;
    }

    public List<SensorJournalEntityDTO> getAllJournals() {
        return namedParameterJdbcTemplate.query(
                SensorJournalSQLQueries.GET_ALL_JOURNALS,
                Map.of(),
                sensorJournalEntityMapper
        );
    }

    public List<SensorJournalEntityDTO> getAllJournals(UUID userId) {
        return namedParameterJdbcTemplate.query(
                SensorJournalSQLQueries.GET_ALL_JOURNALS_BY_USER_ID,
                Map.of("user_id", userId),
                sensorJournalEntityMapper
        );
    }
}
