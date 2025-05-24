package ru.system.library.dto.common.sensor;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.system.library.dto.common.reference.ReferenceDTO;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SensorDTO {
    private UUID id;
    @Size(max = 255)
    private String name;
    private String description;
    private String type;
    private ReferenceDTO reference;
    private Machine machine;
    private List<SensorJournalEntityDTO> journal;

    @Data
    @Builder
    public static class Machine {
        private UUID id;
        private String name;
    }
}
