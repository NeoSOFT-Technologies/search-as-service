package com.solr.clientwrapper.domain.port.api;

import com.solr.clientwrapper.domain.dto.SampleEntityDTO;
import com.solr.clientwrapper.infrastructure.entity.SampleEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface SampleEntityServicePort {

    SampleEntity save(SampleEntityDTO sampleEntityDTO);

    SampleEntity update(Long id, SampleEntityDTO sampleEntityDTO);

    boolean existsById(Long id);

    List<SampleEntity> findAll();

    Optional<SampleEntity> findById(Long id);

    void deleteById(Long id);

    Optional<SampleEntity> patch(Long id, SampleEntityDTO sampleEntityDTO);

  

}
