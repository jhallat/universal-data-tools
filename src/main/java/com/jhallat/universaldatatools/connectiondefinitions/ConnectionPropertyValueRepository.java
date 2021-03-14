package com.jhallat.universaldatatools.connectiondefinitions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConnectionPropertyValueRepository extends JpaRepository<ConnectionPropertyValue, ConnectionPropertyValueId> {

    List<ConnectionPropertyValue> findByConnectionId(int connectionId);

}
