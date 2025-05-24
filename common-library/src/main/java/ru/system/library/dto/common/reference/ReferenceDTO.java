package ru.system.library.dto.common.reference;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ReferenceDTO {
    private UUID id;
    private UUID sensor_id;
    @Size(max = 255)
    private String name;
    private Double value;
    private String type;
    List<ReferenceHistoryEntityDTO> history;
}
