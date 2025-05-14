package ru.system.library.sql.queries;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MachineSQLQueries { // todo: realise
    public final String EXISTS_MACHINE = """
            SELECT EXISTS(SELECT 1 FROM system.machine WHERE id=:id);
            """;
    public final String GET_ALL_MACHINES = """
            SELECT * FROM system.machine m;
            """;
    public final String GET_ALL_MACHINES_SENSORS = """
            SELECT s.id, s.name, s.type, s.machine_id, maxsj.value as sensor_value, r.id as reference_id, r.name as reference_name, r.value as reference_value
            FROM sensor s
            LEFT JOIN system.reference r ON s.id=r.sensor_id
            JOIN system.sensor_permission sp ON s.id=sp.sensor_id
            JOIN (
                SELECT sj.sensor_id, sj.value FROM sensor_journal sj
                JOIN (
                    SELECT sj.sensor_id, max(sj.time) as max_time FROM system.sensor_journal sj
                    GROUP BY sj.sensor_id
                ) maxsj ON maxsj.sensor_id=sj.sensor_id AND maxsj.max_time=sj.time
            ) maxsj ON maxsj.sensor_id=s.id
            WHERE sp.user_id=:user_id;
            """;
    public final String GET_MACHINE_SENSORS_BY_MACHINE_ID = """
            SELECT s.id, s.name, s.type, maxsj.value as sensor_value, r.id as reference_id, r.name as reference_name, r.value as reference_value
            FROM sensor s
            LEFT JOIN system.reference r ON s.id=r.sensor_id
            JOIN system.sensor_permission sp ON s.id=sp.sensor_id
            JOIN (
                SELECT sj.sensor_id, sj.value FROM sensor_journal sj
                JOIN (
                    SELECT sj.sensor_id, max(sj.time) as max_time FROM system.sensor_journal sj
                    GROUP BY sj.sensor_id
                ) maxsj ON maxsj.sensor_id=sj.sensor_id AND maxsj.max_time=sj.time
            ) maxsj ON maxsj.sensor_id=s.id
            WHERE s.machine_id=:machine_id AND sp.user_id=:user_id;
            """;
    public final String GET_MACHINE_BY_ID = """
            SELECT * FROM system.machine
            WHERE id=:machine_id;
            """;

}
