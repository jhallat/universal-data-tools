package com.jhallat.universaldatatools.connectiondefinitions;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name="CONNECTIONS")
@Data
public class ConnectionDefinition {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="TYPE_ID")
    @NotBlank
    private int typeId;

    @Column(name="DESCRIPTION")
    private String description;

    @Transient
    private List<PropertyValue> properties;
}
