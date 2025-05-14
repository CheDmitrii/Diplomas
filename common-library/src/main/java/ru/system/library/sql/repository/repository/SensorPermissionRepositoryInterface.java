package ru.system.library.sql.repository.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.system.library.sql.queries.SensorPermissionSQLQueries;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class SensorPermissionRepositoryInterface {
    protected final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public boolean isAllowedSensor(UUID userId, UUID sensorId) {
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(
                SensorPermissionSQLQueries.IS_ALLOWED_SENSOR,
                Map.of("user_id", userId,
                        "sensor_id", sensorId),
                Boolean.class
        ));
    }

    public boolean isAllowedSensorByReferenceId(UUID userId, UUID referenceId) {
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(
                SensorPermissionSQLQueries.IS_ALLOWED_SENSOR_BY_REFERENCE_ID,
                Map.of("user_id", userId,
                        "reference_id", referenceId),
                Boolean.class
        ));
    }
}
