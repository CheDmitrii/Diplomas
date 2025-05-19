package ru.system.library.sql.queries;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SensorSQLQueries {// todo: fixed
    public final String GET_ALL_SENSORS_IDS = """
            SELECT s.id FROM system.sensor s;
            """;
    public final String GET_ALL_SENSORS = """
            SELECT s.*, r.id as reference_id, r.value, r.name as reference_name, m.name as machine_name FROM system.sensor s
            LEFT JOIN system.reference r ON s.id=r.sensor_id
            LEFT JOIN system.machine m ON s.machine_id=m.id
            JOIN system.sensor_permission sp ON sp.sensor_id=s.id
            WHERE sp.user_id=:user_id;"""; // ref may be null
    public final String GET_SENSOR_BY_ID = """
            SELECT s.*, r.id as reference_id, r.value, r.name as reference_name, m.name as machine_name FROM system.sensor s
            LEFT JOIN system.reference r ON s.id=r.sensor_id
            LEFT JOIN system.machine m ON s.machine_id=m.id
            WHERE s.id=:id;""";
    public final String EXIST_SENSOR = """
            SELECT EXISTS(SELECT 1 FROM system.sensor WHERE id=:id);""";
    public final String CREATE_SENSOR = """
            INSERT INTO system.sensor VALUES(uuid_generate_v4(), :name, :description, :machine_id)
            returning id;""";
}
