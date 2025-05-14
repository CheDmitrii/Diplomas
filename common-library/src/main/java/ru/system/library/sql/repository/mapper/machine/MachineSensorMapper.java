package ru.system.library.sql.repository.mapper.machine;

import org.springframework.jdbc.core.RowMapper;
import ru.system.library.dto.common.MachineDTO;
import ru.system.library.dto.common.MachineDTO.Sensor;
import ru.system.library.dto.common.MachineDTO.Reference;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MachineSensorMapper implements RowMapper<Sensor> {
    @Override
    public Sensor mapRow(ResultSet rs, int rowNum) throws SQLException {
        String referenceId = rs.getString("reference_id");
        String machineId = rs.getString("machine_id");
        return Sensor.builder()
                .id(UUID.fromString(rs.getString("id")))
                .machineId(machineId != null ? UUID.fromString(machineId) : null)
                .name(rs.getString("name"))
                .type(rs.getString("type"))
                .value(rs.getDouble("sensor_value"))
                .reference(referenceId != null ?
                        Reference.builder()
                                .id(UUID.fromString(referenceId))
                                .name(rs.getString("reference_name"))
                                .value(rs.getDouble("reference_value"))
                                .build()
                        : null)
                .build();
    }
}
