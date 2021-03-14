package com.jhallat.universaldatatools.connectiondefinitions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionPropertyValueId implements Serializable {

    private int connectionId;
    private int propertyId;

}
