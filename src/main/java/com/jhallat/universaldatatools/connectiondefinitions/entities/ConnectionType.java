package com.jhallat.universaldatatools.connectiondefinitions.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class ConnectionType {

    private String description;
    private String label;
    private String factory;
    private String page;
    private List<PropertyDefinition> propertyDefinitions;

    public ConnectionType() {
        propertyDefinitions = new ArrayList<>();
    }

    public void addPropertyDefinition(PropertyDefinition definition) {
        propertyDefinitions.add(definition);
    }

    public List<PropertyDefinition> getPropertyDefinitions() {
        return Collections.unmodifiableList(propertyDefinitions);
    }
}
