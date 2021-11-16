package com.solr.clientwrapper.usecase.sampleentity;

import com.solr.clientwrapper.domain.port.api.SampleEntityServicePort;
import com.solr.clientwrapper.infrastructure.entity.SampleEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReadSampleEntity {

    private final SampleEntityServicePort sampleEntityServicePort;

    public ReadSampleEntity(SampleEntityServicePort sampleEntityServicePort) {
        this.sampleEntityServicePort = sampleEntityServicePort;
    }

    public List<SampleEntity> findAll() {
        return sampleEntityServicePort.findAll();
    }

    public Optional<SampleEntity> findById(Long id) {
        return sampleEntityServicePort.findById(id);
    }

}
