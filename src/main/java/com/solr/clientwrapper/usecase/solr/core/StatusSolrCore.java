package com.solr.clientwrapper.usecase.solr.core;

import com.solr.clientwrapper.domain.port.api.SolrCoreServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StatusSolrCore {

    private final Logger log = LoggerFactory.getLogger(StatusSolrCore.class);

    private final SolrCoreServicePort solrCoreServicePort;

    public StatusSolrCore(SolrCoreServicePort solrCoreServicePort) {
        this.solrCoreServicePort = solrCoreServicePort;
    }

    public String status(String coreName) {
        log.debug("status");
        return solrCoreServicePort.status(coreName);
    }

}
