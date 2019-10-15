package net.rickcee.fix.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {
	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Map<String, Object> additionalData) {

		String result = null;
		try {
			result = objectMapper.writeValueAsString(additionalData);
		} catch (final JsonProcessingException e) {
			log.error("JSON writing error", e);
		}

		return result;
	}

	@Override
	public Map<String, Object> convertToEntityAttribute(String additionalData) {

		Map<String, Object> result = new HashMap<>();
		try {
			if(additionalData != null) {
				result = objectMapper.readValue(additionalData, new MapTypeReference());
			}
		} catch (final IOException e) {
			log.error("JSON reading error", e);
		}
		return result;
	}

	class MapTypeReference extends TypeReference<Map<String,Object>> {
		
	}
}
