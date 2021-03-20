package com.jhallat.universaldatatools.connectiondefinitions;

import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionType;

import java.util.List;

public interface ConnectionTypeProvider {

    List<ConnectionType> getTypes();

}
