package com.solr.clientwrapper.usecase.solr.core;

import com.solr.clientwrapper.domain.port.api.SolrCoreServicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StatusSolrCore {

    private final SolrCoreServicePort solrCoreServicePort;

    public StatusSolrCore(SolrCoreServicePort solrCoreServicePort) {
        this.solrCoreServicePort = solrCoreServicePort;
    }

    public String coreStatus(String coreName) {
        return solrCoreServicePort.statusCore(coreName);
    }

}
