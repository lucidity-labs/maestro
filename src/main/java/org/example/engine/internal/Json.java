package org.example.engine.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Json {

    private static final Logger logger = Logger.getLogger(Json.class.getName());
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
            logger.log(Level.SEVERE, "Error serializing object: " + object, e);
            return null;
        }
    }

    public static <T> T deserialize(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deserializing string: " + jsonString, e);
            return null;
        }
    }
}
