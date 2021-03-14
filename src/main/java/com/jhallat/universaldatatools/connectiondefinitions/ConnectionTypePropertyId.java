package com.jhallat.universaldatatools.connectiondefinitions;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionTypePropertyId implements Serializable {

    private int typeId;
    private int propertyId;
}
