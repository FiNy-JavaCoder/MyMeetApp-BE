package MyApp.BE.dto.mapper.converters;

import MyApp.BE.enums.Districts;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Converter
public class DistrictsSetConverter implements AttributeConverter<Set<Districts>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Set<Districts> districts) {
        if (districts == null || districts.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(districts);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting Set<Districts> to JSON", e);
        }
    }

    @Override
    public Set<Districts> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new HashSet<>();
        }
        try {
            Districts[] array = objectMapper.readValue(dbData, Districts[].class);
            return new HashSet<>(Set.of(array));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading JSON to Set<Districts>", e);
        }
    }
}