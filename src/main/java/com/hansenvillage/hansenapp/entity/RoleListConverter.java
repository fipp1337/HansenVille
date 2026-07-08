package com.hansenvillage.hansenapp.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Converter
public class RoleListConverter implements AttributeConverter<List<Role>, String> {

    @Override
    public String convertToDatabaseColumn(List<Role> attribute) {
        if (attribute == null || attribute.isEmpty()) return null;
        return String.join(",", attribute.stream().map(Enum::name).toList());
    }

    @Override
    public List<Role> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return Collections.emptyList();
        return Arrays.stream(dbData.split(","))
                .map(Role::valueOf)
                .toList();
    }
}