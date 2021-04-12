package com.jhallat.universaldatatools.connectionlog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionLogRepository extends JpaRepository<ConnectionLog, Long> {
}
