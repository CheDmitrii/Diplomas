package ru.system.library.dto.common.sensor;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.system.library.json.CustomTimestampDeserializer;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorJournalEntityDTO {
    private UUID id;
    private Double value;
//    @JsonDeserialize(using = DateDeserializers.TimestampDeserializer.class)
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Timestamp time;
}
