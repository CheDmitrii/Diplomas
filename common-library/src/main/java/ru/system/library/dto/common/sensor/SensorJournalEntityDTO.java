package ru.system.library.dto.common.sensor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorJournalEntityDTO {
    private UUID id;
    private Double value;
    private Timestamp time;
}
