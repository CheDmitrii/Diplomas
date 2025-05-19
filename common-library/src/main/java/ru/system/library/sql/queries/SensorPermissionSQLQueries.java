package ru.system.library.sql.queries;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SensorPermissionSQLQueries {// todo: fixed
    public final String IS_ALLOWED_SENSOR = """
            SELECT EXISTS(SELECT 1 FROM system.sensor_permission sp
            WHERE sp.user_id=:user_id AND sp.sensor_id=:sensor_id);""";
    public final String IS_ALLOWED_SENSOR_BY_REFERENCE_ID = """
            SELECT EXISTS(SELECT 1 FROM system.sensor_permission sp
            JOIN system.reference r ON r.sensor_id=sp.sensor_id
            WHERE sp.user_id=:user_id AND r.reference_id=:reference_id);""";
    public final String GET_USER_SENSORS = """
            SELECT sp.sensor_id FROM system.sensor_permission sp
            WHERE sp.user_id=:user_id;
            """;
}
