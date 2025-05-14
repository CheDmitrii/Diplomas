package ru.system.library.sql.repository.mapper.machine;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.system.library.dto.common.MachineDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class MachineMapper implements RowMapper<MachineDTO> {
    @Override
    public MachineDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return MachineDTO.builder()
                .id(UUID.fromString(rs.getString("id")))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .build();
    }
}
