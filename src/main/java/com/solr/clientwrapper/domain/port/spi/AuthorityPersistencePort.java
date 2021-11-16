package com.solr.clientwrapper.domain.port.spi;

import java.util.List;
import java.util.Optional;

import com.solr.clientwrapper.infrastructure.entity.Authority;


public interface AuthorityPersistencePort {
    List<Authority> findAll();
    Optional<Authority> findById(String id);
}
