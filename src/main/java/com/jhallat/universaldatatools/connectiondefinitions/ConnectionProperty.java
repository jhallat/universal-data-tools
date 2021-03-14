package com.jhallat.universaldatatools.connectiondefinitions;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="connection_properties")
@Data
public class ConnectionProperty {

    @Id
    private int id;
    private String description;
    private int masked;
}
