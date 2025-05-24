package ru.system.monitoring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.system.library.dto.common.MachineDTO;
import ru.system.monitoring.service.ClaimService;
import ru.system.monitoring.service.MachineService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/machine")
public class MachineController {

    private final MachineService machineService;
    private final ClaimService claimService;
    // todo: implements

    @GetMapping("/{id:.+}")
    public ResponseEntity<MachineDTO> getMachine(@PathVariable("id") UUID machineId) {
        return ResponseEntity.ok(machineService.getMachine(machineId, claimService.getUserId()));
    }

    @GetMapping("/all-machines")
    public ResponseEntity<List<MachineDTO>> getMachines() {
        return ResponseEntity.ok(machineService.getAllMachines(claimService.getUserId()));
    }
}
