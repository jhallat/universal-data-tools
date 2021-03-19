package com.jhallat.universaldatatools.connectiondefinitions;

import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionPropertyValue;
import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionPropertyValueId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConnectionPropertyValueRepository extends JpaRepository<ConnectionPropertyValue, ConnectionPropertyValueId> {

    List<ConnectionPropertyValue> findByConnectionId(int connectionId);

}
