package org.example.engine.internal.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class Json {
    private static final Logger logger = LoggerFactory.getLogger(Json.class);
    private static final ObjectMapper objectMapper = initializeObjectMapper();

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
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserialize(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            logger.error("Error deserializing string: {}", jsonString, e);
            throw new RuntimeException(e);
        }
    }

    private static ObjectMapper initializeObjectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(Duration.class, new IsoDurationSerializer());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);

        return objectMapper;
    }
}
