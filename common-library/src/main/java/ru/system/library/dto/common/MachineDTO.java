package ru.system.library.dto.common;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MachineDTO {
    private UUID id;
    private String name;
    private String description;
    private List<Sensor> sensors;

    @Data
    @Builder
    public static class Sensor {
        private UUID id;
        private UUID machineId;
        private String name;
        private Double value;
        private Reference reference;
        private String type;
    }

    @Data
    @Builder
    public static class Reference {
        private UUID id;
        private String name;
        private Double value;
    }
}
