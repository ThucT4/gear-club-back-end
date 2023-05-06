package com.pw.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
@Slf4j
public class HashMapConverter implements AttributeConverter<HashMap<String, String>, String> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(HashMap<String, String> hashMap) {
        String json = null;

        try {
            json = objectMapper.writeValueAsString(hashMap);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }

        return json;
    }

    @Override
    public HashMap<String, String> convertToEntityAttribute(String json) {
        HashMap<String, String> hashMap = null;

        try {
            hashMap = objectMapper.readValue(json,
                    new TypeReference<HashMap<String, String>>() {});
        } catch (final IOException e) {
            log.error("JSON reading error", e);
        }
        return hashMap;
    }
}
