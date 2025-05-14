package ru.system.monitoring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.system.library.dto.common.MachineDTO;
import ru.system.library.dto.common.MachineDTO.Sensor;
import ru.system.library.exception.HttpResponseEntityException;
import ru.system.monitoring.repository.repository.MachineRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MachineService {
    private final MachineRepository machineRepository;

    public MachineDTO getMachine(UUID machineId, UUID userId) {
        if (!machineRepository.existsMachine(machineId)) {
            throw new HttpResponseEntityException(HttpStatus.NOT_FOUND,
                    "Machine with this sensorId {%s} doesn't exist".formatted(machineId));
        }
        MachineDTO machine = machineRepository.getMachine(machineId);
        machine.setSensors(machineRepository.getMachineSensors(machineId, userId));
        return machine;
    }

    public List<MachineDTO> getAllMachines(UUID userId) {
        List<MachineDTO> machines = machineRepository.getAllMachines();
        Map<UUID, List<Sensor>> sensors = machineRepository.getAllMachinesSensors(userId)
                .stream()
                .collect(
                        Collectors.groupingBy(
                                MachineDTO.Sensor::getMachineId,
                                Collectors.mapping(
                                        v -> {
                                            v.setMachineId(null);
                                            return v;
                                        },
                                        Collectors.toList()
                                )
                        )
                );
        machines.forEach(v -> v.setSensors(sensors.get(v.getId())));
        return machines;
    }
}
