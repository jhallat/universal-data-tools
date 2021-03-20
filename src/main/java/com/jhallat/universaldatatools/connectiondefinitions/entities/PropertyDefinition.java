package com.jhallat.universaldatatools.connectiondefinitions.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PropertyDefinition {

    private int id;
    private String description;
    private boolean required;
    private boolean masked;

}
