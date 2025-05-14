package ru.system.authentication.entity.permission;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Data
@Table(name = "sensor_permission")
@Entity
@IdClass(SensorPermissionId.class)
public class SensorPermission {
    @Id
    private UUID userId;

    @Id
    private UUID sensorId;
}
