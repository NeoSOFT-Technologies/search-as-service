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
public class DeleteSolrCore {

    private final Logger log = LoggerFactory.getLogger(DeleteSolrCore.class);

    private final SolrCoreServicePort solrCoreServicePort;

    public DeleteSolrCore(SolrCoreServicePort solrCoreServicePort) {
        this.solrCoreServicePort = solrCoreServicePort;
    }

    public ResponseDTO delete(String coreName) {
        log.debug("delete");
        return solrCoreServicePort.delete(coreName);
    }

}
