package com.jhallat.universaldatatools.connectiondefinitions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionTypeRepository extends JpaRepository<ConnectionType, Integer> {
}
