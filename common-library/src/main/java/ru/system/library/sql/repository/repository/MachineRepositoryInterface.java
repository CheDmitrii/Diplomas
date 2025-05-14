package ru.system.library.sql.repository.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.system.library.sql.queries.MachineSQLQueries;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class MachineRepositoryInterface {
    protected final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public boolean existsMachine(UUID id) {
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.queryForObject(
                MachineSQLQueries.EXISTS_MACHINE,
                Map.of("id", id),
                Boolean.class
        ));
    }
}
