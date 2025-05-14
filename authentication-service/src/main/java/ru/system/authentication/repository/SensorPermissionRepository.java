package ru.system.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.system.authentication.entity.permission.SensorPermission;
import ru.system.authentication.entity.permission.SensorPermissionId;
import ru.system.library.dto.common.sensor.SensorPairDTO;

import java.util.List;
import java.util.UUID;

@Repository
public interface SensorPermissionRepository extends JpaRepository<SensorPermission, SensorPermissionId> {
    List<SensorPermission> findByUserId(UUID userId);

    // todo: fix in report
    @Query(value = "SELECT new ru.system.library.dto.common.sensor.SensorPairDTO(sp.sensorId, s.name) FROM SensorPermission sp " +
            "JOIN Sensor s ON s.id=sp.sensorId " +
            "WHERE sp.userId=:user_id")
    List<SensorPairDTO> findAllSensorsOfUser(@Param("user_id") UUID userId);

    @Query(value = "SELECT new ru.system.library.dto.common.sensor.SensorPairDTO(sp.sensorId, s.name) FROM SensorPermission sp " +
            "JOIN Sensor s ON s.id=sp.sensorId " +
            "WHERE sp.userId!=:user_id")
    List<SensorPairDTO> findAllSensorsOfNotUser(@Param("user_id") UUID userId);

    @Query(value = "SELECT new ru.system.library.dto.common.sensor.SensorPairDTO(sp.sensorId, s.name) FROM SensorPermission sp " +
            "JOIN Sensor s ON s.id=sp.sensorId")
    List<SensorPairDTO> findAllSensors();
    @Query(value = "SELECT sp.sensorId FROM SensorPermission sp WHERE sp.userId=:userId")
    List<UUID> findAllSensorIdsByUserId(@Param("user_id") UUID userId);
}
