package ru.system.monitoring.repository.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.system.library.sql.queries.SensorPermissionSQLQueries;
import ru.system.library.sql.repository.repository.SensorPermissionRepositoryInterface;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class SensorPermissionRepository extends SensorPermissionRepositoryInterface {
    public SensorPermissionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    public List<UUID> getUserSensors(UUID userId) {
        return namedParameterJdbcTemplate.queryForList(
                SensorPermissionSQLQueries.GET_USER_SENSORS,
                Map.of("user_id", userId),
                UUID.class
        );
    }
}
