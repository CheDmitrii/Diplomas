package ru.system.library.sql.queries;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReferenceSQLQueries { // todo: fixed
    public final String CREATE_REFERENCE = """
            INSERT INTO system.reference (id, sensor_id, name, value)
            VALUES(uuid_generate_v4(), :sensor_id, :name, :value);
            returning id;""";
    public final String CREATE_REFERENCE_JOURNAL = """
            INSERT INTO system.reference_journal VALUES(uuid_generate_v4(), :reference_id, :old_value, :new_value, :time);""";
    public final String GET_REFERENCE_BY_ID = """
            SELECT * FROM system.reference WHERE id=:id;""";
    public final String GET_ALL_REFERENCES = """
            SELECT * FROM system.reference r
            JOIN system.sensor_permission sp ON sp.sensor_id=r.sensor_id
            WHERE sp.user_id=:user_id
            ORDER BY name""";
    public final String GET_REFERENCE_HISTORY_BY_ID = """
            SELECT * FROM system.reference_journal WHERE reference_id=:reference_id;""";
    public final String GET_ALL_REFERENCE_HISTORY = """
            SELECT * FROM system.reference_journal rj
            JOIN system.sensor_permission sp ON sp.sensor_id=rj.reference_id
            WHERE sp.user_id=:user_id;""";
    public final String UPDATE_VALUE = """
            UPDATE system.reference
            SET value=:value
            WHERE reference_id=:reference_id""";
    public final String EXISTS_REFERENCE = """
            SELECT EXISTS(SELECT 1 FROM system.reference WHERE id=:id);""";
}
