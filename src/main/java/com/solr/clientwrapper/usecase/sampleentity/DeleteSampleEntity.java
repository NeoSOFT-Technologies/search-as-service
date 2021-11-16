package com.solr.clientwrapper.usecase.sampleentity;

import com.solr.clientwrapper.domain.port.api.SampleEntityServicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteSampleEntity {

    private final SampleEntityServicePort sampleEntityServicePort;

    public DeleteSampleEntity(SampleEntityServicePort sampleEntityServicePort) {
        this.sampleEntityServicePort = sampleEntityServicePort;
    }

    public void deleteById(Long id) {
        sampleEntityServicePort.deleteById(id);
    }
}
