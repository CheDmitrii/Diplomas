package ru.system.authentication.entity.permission;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sensor_permission")
@Entity
@IdClass(SensorPermissionId.class)
public class SensorPermission {
    @Id
    private UUID userId;

    @Id
    private UUID sensorId;
}
