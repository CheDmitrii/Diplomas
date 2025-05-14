package ru.system.monitoring.repository.repository;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.system.library.dto.common.MachineDTO;
import ru.system.library.dto.common.MachineDTO.Sensor;
import ru.system.library.sql.queries.MachineSQLQueries;
import ru.system.library.sql.repository.mapper.machine.MachineMapper;
import ru.system.library.sql.repository.mapper.machine.MachineSensorMapper;
import ru.system.library.sql.repository.repository.MachineRepositoryInterface;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class MachineRepository extends MachineRepositoryInterface {
    private final MachineMapper machineMapper;
    private final MachineSensorMapper machineSensorMapper;

    public MachineRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                             MachineSensorMapper machineSensorMapper,
                             MachineMapper machineMapper) {
        super(namedParameterJdbcTemplate);
        this.machineMapper = machineMapper;
        this.machineSensorMapper = machineSensorMapper;
    }

    public MachineDTO getMachine(UUID machineId) {
        return namedParameterJdbcTemplate.queryForObject(
                MachineSQLQueries.GET_MACHINE_BY_ID,
                Map.of("machineId", machineId),
                machineMapper
        );
    }

    public List<MachineDTO> getAllMachines() {
        return namedParameterJdbcTemplate.query(
                MachineSQLQueries.GET_ALL_MACHINES,
                machineMapper
        );
    }

    public List<Sensor> getAllMachinesSensors(UUID userId) {
        return namedParameterJdbcTemplate.query(
                MachineSQLQueries.GET_ALL_MACHINES_SENSORS,
                Map.of("user_id", userId),
                machineSensorMapper
        );
    }

    public List<Sensor> getMachineSensors(UUID machineId, UUID userId) {
        return namedParameterJdbcTemplate.query(
                MachineSQLQueries.GET_MACHINE_SENSORS_BY_MACHINE_ID,
                Map.of("machine_id", machineId,
                        "user_id", userId),
                machineSensorMapper
        );
    }
}
