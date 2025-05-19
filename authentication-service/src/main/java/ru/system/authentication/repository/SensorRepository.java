package ru.system.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.system.authentication.entity.Sensor;
import ru.system.library.dto.common.sensor.SensorPairDTO;

import java.util.List;
import java.util.UUID;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, UUID> {
    @Query(value = "SELECT new ru.system.library.dto.common.sensor.SensorPairDTO(s.id, s.name) FROM Sensor s")
    List<SensorPairDTO> getAllSensorPairs();
}
