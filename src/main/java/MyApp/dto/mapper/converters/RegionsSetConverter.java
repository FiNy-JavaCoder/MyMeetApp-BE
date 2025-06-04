package MyApp.dto.mapper.converters;

import MyApp.enums.Regions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Converter
public class RegionsSetConverter implements AttributeConverter<Set<Regions>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Set<Regions> regions) {
        try {
            return objectMapper.writeValueAsString(regions);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting Set<Regions> to JSON", e);
        }
    }

    @Override
    public Set<Regions> convertToEntityAttribute(String dbData) {
        try {
            Regions[] array = objectMapper.readValue(dbData, Regions[].class);
            return new HashSet<>(Set.of(array));
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading JSON to Set<Regions>", e);
        }
    }
}
