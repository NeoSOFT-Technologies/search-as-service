package com.solr.clientwrapper.domain.port.spi;

import java.util.List;
import java.util.Optional;

import com.solr.clientwrapper.domain.dto.SampleEntityDTO;
import com.solr.clientwrapper.infrastructure.entity.SampleEntity;


public interface SampleEntityPersistencePort {
    List<SampleEntity> findAll();

    Optional<SampleEntity> findById(Long id);

    SampleEntity save(SampleEntityDTO sampleEntityDTO);

    boolean existsById(Long id);

    
    
    
    
    
    void deleteById(Long id);
}