package ru.system.monitoring.repository.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.system.library.sql.repository.repository.SensorPermissionRepositoryInterface;

@Component
public class SensorPermissionRepository extends SensorPermissionRepositoryInterface {
    public SensorPermissionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }
}
