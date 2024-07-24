package org.example.engine.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Json {

    private static final Logger logger = LoggerFactory.getLogger(Json.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String serializeFirst(T[] args) {
        if (args == null) return null;
        return serialize(args[0]);
    }

    public static <T> String serialize(T object) {
        if (object == null) return null;
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing object: {}", object, e);
            return null;
        }
    }

    public static <T> T deserialize(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            logger.error("Error deserializing string: {}", jsonString, e);
            return null;
        }
    }
}
