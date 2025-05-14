package ru.system.library.config.mapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.system.library.sql.repository.mapper.*;
import ru.system.library.sql.repository.mapper.machine.MachineMapper;
import ru.system.library.sql.repository.mapper.machine.MachineSensorMapper;

@Configuration
public class MapperConfig {
    @Bean
    public SensorJournalEntityMapper kafkaJournalEntityMapper() {
        return new SensorJournalEntityMapper();
    }

    @Bean
    public ReferenceHistoryMapper referenceHistoryMapper() {
        return new ReferenceHistoryMapper();
    }

    @Bean
    public SensorJournalEntityMapperCut journalEntityMapper() {
        return new SensorJournalEntityMapperCut();
    }

    @Bean
    public SensorMapper sensorMapper() {
        return new SensorMapper();
    }

    @Bean
    public ReferenceMapper referenceMapper() {
        return new ReferenceMapper();
    }

    @Bean
    public MachineMapper machineMapper() {
        return new MachineMapper();
    }

    @Bean
    public MachineSensorMapper machineSensorMapper() {
        return new MachineSensorMapper();
    }
}
