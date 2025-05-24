package ru.system.library.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Timestamp;

public class CustomTimestampDeserializer extends JsonDeserializer<Timestamp> {
    @Override
    public Timestamp deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return Timestamp.valueOf(jsonParser.getText());
    }
}
