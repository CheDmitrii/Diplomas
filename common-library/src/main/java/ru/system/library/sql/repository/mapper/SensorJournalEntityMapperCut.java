package ru.system.library.sql.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.system.library.dto.common.sensor.SensorJournalEntityDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;

public class SensorJournalEntityMapperCut implements RowMapper<SensorJournalEntityDTO> {

    private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    @Override
    public SensorJournalEntityDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return SensorJournalEntityDTO.builder()
                .value(rs.getDouble("value"))
                .time((rs.getTimestamp("time", calendar)))
                .build();
    }
}
