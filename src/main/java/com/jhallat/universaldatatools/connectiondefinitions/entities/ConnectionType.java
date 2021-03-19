package com.jhallat.universaldatatools.connectiondefinitions.entities;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name="CONNECTION_TYPES")
@Data
public class ConnectionType {

    @Id
    @Column(name="ID")
    private int id;

    @Column(name="DESCRIPTION")
    private String description;

    @Column(name="LABEL")
    @Convert(converter= ConnectionLabelConverter.class)
    private ConnectionLabel label;

    @Column(name="FACTORY")
    private String factory;

    @Column(name="PAGE")
    private String page;

    @Transient
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
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
