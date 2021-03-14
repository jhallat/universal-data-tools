package com.jhallat.universaldatatools.connectiondefinitions;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Data
@IdClass(ConnectionTypePropertyId.class)
@Table(name="connection_type_properties")
public class ConnectionTypeProperty {

    @Id
    private int typeId;
    @Id
    private int propertyId;
    private int required;

}
