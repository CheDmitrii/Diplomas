package ru.system.library.sql.repository.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.system.library.dto.common.ReferenceDTO;
import ru.system.library.dto.common.sensor.SensorDTO;
import ru.system.library.sql.queries.ReferenceSQLQueries;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class ReferenceRepositoryInterface {
    protected final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public boolean existsReference(UUID id) {
        return Boolean.TRUE.equals(
                namedParameterJdbcTemplate.queryForObject(
                        ReferenceSQLQueries.EXISTS_REFERENCE,
                        Map.of("id", id),
                        Boolean.class
                )
        );
    }

    public UUID createReference(ReferenceDTO reference, SensorDTO sensorDTO, UUID sensor_id) { // todo: fix after
        return namedParameterJdbcTemplate.queryForObject(
                ReferenceSQLQueries.CREATE_REFERENCE,
                Map.of(
                        "sensor_id", sensor_id,
                        "name", reference.getName(),
                        "value", reference.getValue()
                ),
                UUID.class
        );
    }
}
