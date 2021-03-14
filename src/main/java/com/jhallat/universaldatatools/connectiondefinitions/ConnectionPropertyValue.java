package com.jhallat.universaldatatools.connectiondefinitions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Data
@IdClass(ConnectionPropertyValueId.class)
@Table(name="connection_property_values")
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionPropertyValue {

    @Id
    private int connectionId;

    @Id
    private int propertyId;

    private String value;

}
