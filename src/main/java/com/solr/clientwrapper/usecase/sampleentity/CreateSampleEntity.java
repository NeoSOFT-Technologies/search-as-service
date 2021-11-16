package com.solr.clientwrapper.usecase.sampleentity;

import com.solr.clientwrapper.domain.dto.SampleEntityDTO;
import com.solr.clientwrapper.domain.port.api.SampleEntityServicePort;
import com.solr.clientwrapper.infrastructure.entity.SampleEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateSampleEntity {

    private final SampleEntityServicePort sampleEntityServicePort;

    public CreateSampleEntity(SampleEntityServicePort sampleEntityServicePort) {
        this.sampleEntityServicePort = sampleEntityServicePort;
    }

    public SampleEntity save(SampleEntityDTO sampleEntityDTO) {
        return sampleEntityServicePort.save(sampleEntityDTO);
    }

}
