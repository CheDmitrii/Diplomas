package ru.system.library.sql.repository.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.system.library.dto.common.sensor.SensorDTO;
import ru.system.library.sql.queries.SensorSQLQueries;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class SensorRepositoryInterface {

    protected final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public boolean existsSensor(UUID id) {
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(
                SensorSQLQueries.EXIST_SENSOR,
                Map.of("id", id),
                Boolean.class
        ));
    }

    public UUID createSensor(SensorDTO sensorDTO) {
        return namedParameterJdbcTemplate.queryForObject(
                SensorSQLQueries.CREATE_SENSOR,
                new MapSqlParameterSource() // MapSqlParameterSource support null instead Map.of() method
                        .addValue("name", sensorDTO.getName())
                        .addValue("machine_id", sensorDTO.getMachine() != null ? sensorDTO.getMachine().getId() : null)
                        .addValue("description", sensorDTO.getDescription())
                        .addValue("type", sensorDTO.getType()),
                UUID.class
        );
    }
}
