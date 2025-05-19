package ru.system.library.dto.common.sensor;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import java.util.UUID;

@Data
//@Builder
public class SensorCheckedDTO {
    private UUID id;
    private String name;
    private Status status;

    public enum Status {
        GOOD("GOOD"),
        BAD("BAD"),
        NOT_ACTIVE("NOT_ACTIVE");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        @JsonCreator
        public Status statusFrom(String value) {
            for (Status status : Status.values()) {
                if (status.value.equalsIgnoreCase(value)) {
                    return status;
                }
            }
            return NOT_ACTIVE;
        }
    }
}
