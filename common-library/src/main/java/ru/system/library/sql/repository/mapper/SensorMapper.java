package ru.system.library.sql.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.system.library.dto.common.ReferenceDTO;
import ru.system.library.dto.common.sensor.SensorDTO;
import ru.system.library.dto.common.sensor.SensorDTO.Machine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SensorMapper implements RowMapper<SensorDTO> {
    @Override
    public SensorDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        String referenceId = rs.getString("reference_id");
        return SensorDTO.builder()
                .id(UUID.fromString(rs.getString("id")))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .machine(Machine.builder()
                        .id(UUID.fromString(rs.getString("machine_id")))
                        .name(rs.getString("machine_name"))
                        .build())
                .reference(referenceId != null ?
                        ReferenceDTO.builder()
                                .id(UUID.fromString(referenceId))
                                .name(rs.getString("reference_name"))
                                .value(rs.getDouble("value"))
                                .build() : null)
                .type(rs.getString("type"))
                .build();
    }
}
