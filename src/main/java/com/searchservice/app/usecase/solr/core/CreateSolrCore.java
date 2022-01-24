package com.searchservice.app.usecase.solr.core;


import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.SolrResponseDTO;
import com.searchservice.app.domain.port.api.SolrCoreServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateSolrCore {

    private final Logger log = LoggerFactory.getLogger(CreateSolrCore.class);

    private final SolrCoreServicePort solrCoreServicePort;

    public CreateSolrCore(SolrCoreServicePort solrCoreServicePort) {
        this.solrCoreServicePort = solrCoreServicePort;
    }

    public ResponseDTO create(String coreName) {
        log.debug("create core");
        return solrCoreServicePort.create(coreName);
    }

}
