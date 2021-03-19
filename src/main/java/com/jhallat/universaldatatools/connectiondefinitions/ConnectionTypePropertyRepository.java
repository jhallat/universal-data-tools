package com.jhallat.universaldatatools.connectiondefinitions;

import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionTypeProperty;
import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionTypePropertyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionTypePropertyRepository extends JpaRepository<ConnectionTypeProperty, ConnectionTypePropertyId> {
}
