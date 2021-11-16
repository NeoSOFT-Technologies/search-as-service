package com.solr.clientwrapper.usecase.sampleentity;

import com.solr.clientwrapper.domain.dto.SampleEntityDTO;
import com.solr.clientwrapper.domain.port.api.SampleEntityServicePort;
import com.solr.clientwrapper.infrastructure.entity.SampleEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UpdateSampleEntity {

    private final SampleEntityServicePort sampleEntityServicePort;

    public UpdateSampleEntity(SampleEntityServicePort sampleEntityServicePort) {
        this.sampleEntityServicePort = sampleEntityServicePort;
    }

    public SampleEntity update(Long id, SampleEntityDTO sampleEntityDTO) {
        return sampleEntityServicePort.update(id, sampleEntityDTO);
    }

    public Optional<SampleEntity> patch(Long id, SampleEntityDTO sampleEntityDTO) {
        return sampleEntityServicePort.patch(id, sampleEntityDTO);
    }


}
