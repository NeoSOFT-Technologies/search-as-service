package com.solr.clientwrapper.usecase.solr.core;

import com.solr.clientwrapper.domain.port.api.SolrCoreServicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateSolrCore {

    private final SolrCoreServicePort solrCoreServicePort;

    public CreateSolrCore(SolrCoreServicePort solrCoreServicePort) {
        this.solrCoreServicePort = solrCoreServicePort;
    }

    public boolean createCore(String coreName) {
        solrCoreServicePort.createCore(coreName);
        return true;
    }

}
