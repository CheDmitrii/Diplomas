package ru.system.library.dto.common.sensor;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SensorPairDTO {
    private UUID id;
    private String name;
}
