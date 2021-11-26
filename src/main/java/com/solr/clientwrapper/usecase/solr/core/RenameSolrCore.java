package com.solr.clientwrapper.usecase.solr.core;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrCoreServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RenameSolrCore {

    private final Logger log = LoggerFactory.getLogger(RenameSolrCore.class);

    private final SolrCoreServicePort solrCoreServicePort;

    public RenameSolrCore(SolrCoreServicePort solrCoreServicePort) {
        this.solrCoreServicePort = solrCoreServicePort;
    }

    public SolrResponseDTO rename(String coreName, String newName) {
        log.debug("rename");
        return solrCoreServicePort.rename(coreName,newName);
    }

}
