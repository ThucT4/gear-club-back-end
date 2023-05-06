package com.pw.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ListOfHashMapConverter implements AttributeConverter<List<Map<Integer, Integer>>, String> {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(List<Map<Integer, Integer>> customerInfoListOfMap) {
        String customerInfoJson = null;
        try {
            customerInfoJson = objectMapper.writeValueAsString(customerInfoListOfMap);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }

        return customerInfoJson;
    }

    @Override
    public List<Map<Integer, Integer>> convertToEntityAttribute(String customerInfoJSON) {
        List<Map<Integer, Integer>> customerInfoListOfMap = null;
        try {
            customerInfoListOfMap = objectMapper.readValue(customerInfoJSON,
                    new TypeReference<List<Map<Integer, Integer>>>() {});
        } catch (final IOException e) {
            log.error("JSON reading error", e);
        }

        return customerInfoListOfMap;
    }
}
