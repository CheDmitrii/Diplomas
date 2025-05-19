package ru.system.monitoring.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.system.library.sql.repository.repository.SensorRepositoryInterface;

@Component
public class SensorRepository extends SensorRepositoryInterface {

    public SensorRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }
}
