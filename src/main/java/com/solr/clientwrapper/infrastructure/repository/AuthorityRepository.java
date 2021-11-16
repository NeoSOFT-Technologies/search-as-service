package com.solr.clientwrapper.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solr.clientwrapper.infrastructure.entity.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
