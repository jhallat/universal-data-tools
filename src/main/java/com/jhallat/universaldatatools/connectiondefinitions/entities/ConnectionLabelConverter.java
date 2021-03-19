package com.jhallat.universaldatatools.connectiondefinitions.entities;

import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionLabel;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ConnectionLabelConverter implements AttributeConverter<ConnectionLabel, String> {

    @Override
    public String convertToDatabaseColumn(ConnectionLabel connectionLabel) {
        for (ConnectionLabel value :ConnectionLabel.values()) {
            if (value.name().equals(connectionLabel)) {
                return value.name();
            }
        }
        return ConnectionLabel.NONE.name();
    }

    @Override
    public ConnectionLabel convertToEntityAttribute(String s) {
        try {
            return ConnectionLabel.valueOf(s);
        } catch (IllegalArgumentException exception) {
            return ConnectionLabel.NONE;
        }
    }
}
