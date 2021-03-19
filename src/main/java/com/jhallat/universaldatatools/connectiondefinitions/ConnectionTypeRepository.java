package com.jhallat.universaldatatools.connectiondefinitions;

import com.jhallat.universaldatatools.connectiondefinitions.entities.ConnectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionTypeRepository extends JpaRepository<ConnectionType, Integer> {
}
