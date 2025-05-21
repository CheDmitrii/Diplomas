package ru.system.library.sql.queries;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SensorJournalSQLQueries {// todo: fixed
    public final String WRITE_JOURNAL = """
            INSERT INTO system.sensor_journal
            VALUES(uuid_generate_v4(), :sensor_id, :value, :time);
            """;
    public final String GET_JOURNALS_BY_ID = """
            SELECT value, time FROM system.sensor_journal
            WHERE sensor_id=:sensor_id;
            """;
    public final String GET_ALL_JOURNALS = """
            SELECT * FROM system.sensor_journal sj
            JOIN sensor_permission sp ON sp.sensor_id=sj.sensor_id;
            """;
    public final String GET_ALL_JOURNALS_BY_USER_ID = """
            SELECT * FROM system.sensor_journal sj
            JOIN sensor_permission sp ON sp.sensor_id=sj.sensor_id
            WHERE sp.user_id=:user_id;""";
}