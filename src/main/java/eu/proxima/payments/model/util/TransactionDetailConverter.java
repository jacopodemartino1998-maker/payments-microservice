package eu.proxima.payments.model.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import eu.proxima.payments.model.generiscsinterface.TransactionGenericsDetailDTO;

@Converter
public class TransactionDetailConverter implements AttributeConverter<TransactionGenericsDetailDTO, String> {

    private final ObjectMapper objectMapper;

    public TransactionDetailConverter() {
        objectMapper = new ObjectMapper();
        BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("eu.proxima.payments")
                .build();
        objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    }

    @Override
    public String convertToDatabaseColumn(TransactionGenericsDetailDTO attribute) {
        try {
            if (attribute == null)
                return null;
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalStateException("Error serializing TransactionGenericsDetailDTO", e);
        }
    }

    @Override
    public TransactionGenericsDetailDTO convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null)
                return null;
            Object obj = objectMapper.readValue(dbData, Object.class);
            return (TransactionGenericsDetailDTO) obj;
        } catch (Exception e) {
            throw new IllegalStateException("Error deserializing TransactionGenericsDetailDTO", e);
        }
    }
}
