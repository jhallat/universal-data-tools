package com.jhallat.universaldatatools.connectiondefinitions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PropertyDefinition {

    private int propertyId;
    private String description;
    private boolean required;
    private boolean masked;

}
