package ru.system.monitoring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.system.library.dto.common.reference.ReferenceDTO;
import ru.system.library.dto.common.reference.ReferenceHistoryEntityDTO;
import ru.system.library.exception.HttpResponseEntityException;
import ru.system.monitoring.dto.RequestUpdateReferenceDTO;
import ru.system.monitoring.repository.repository.ReferenceRepository;
import ru.system.monitoring.repository.repository.SensorPermissionRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReferenceService {
    private final ReferenceRepository referenceRepository;
    private final SensorPermissionRepository sensorPermissionRepository;


    public void saveChanges(RequestUpdateReferenceDTO updateReference) {
        if (!referenceRepository.existsReference(updateReference.getId())) {
            throw new HttpResponseEntityException(HttpStatus.NOT_FOUND,
                    "Reference with this id {%s} doesn't exist".formatted(updateReference.getId()));
        }
        referenceRepository.changeValue(updateReference, updateReference.getTime());
    }



    public ReferenceDTO getReference(UUID referenceId, UUID userId) {
        if (!referenceRepository.existsReference(referenceId)) {
            throw new HttpResponseEntityException(HttpStatus.NOT_FOUND,
                    "Reference with this referenceId {%s} doesn't exist".formatted(referenceId));
        }
        if (!sensorPermissionRepository.isAllowedSensorByReferenceId(userId, referenceId)) {
            throw new HttpResponseEntityException(HttpStatus.FORBIDDEN,
                    "Reference with this referenceId {%s} doesn't allowed".formatted(referenceId));
        }
        ReferenceDTO reference = referenceRepository.getReferenceById(referenceId);
        reference.setHistory(referenceRepository.getReferenceHistory(referenceId));
        return reference;
    }

    public List<ReferenceDTO> getAllReferences(UUID userID) {
        List<ReferenceDTO> references = referenceRepository.getAllReferences(userID);
        Map<UUID, List<ReferenceHistoryEntityDTO>> groupedHistory = referenceRepository.getAllReferenceHistory(userID)
                .stream()
                .collect(Collectors.groupingBy(ReferenceHistoryEntityDTO::getId));
        references.forEach(referenceDTO -> referenceDTO.setHistory(groupedHistory.get(referenceDTO.getId())));
        return references;
    }
}
