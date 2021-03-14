package com.jhallat.universaldatatools.connectiondefinitions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<ConnectionDefinition, Integer> {

    List<ConnectionDefinition> findByDescriptionStartingWith(String description);

}
