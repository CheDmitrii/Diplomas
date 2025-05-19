package ru.system.monitoring.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.system.library.sql.repository.repository.ReferenceRepositoryInterface;

@Component
public class ReferenceRepository extends ReferenceRepositoryInterface {
    public ReferenceRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }
}
