package com.solr.clientwrapper.usecase.solr.core;

import com.solr.clientwrapper.domain.port.api.SolrCoreServicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReloadSolrCore {

    private final SolrCoreServicePort solrCoreServicePort;

    public ReloadSolrCore(SolrCoreServicePort solrCoreServicePort) {
        this.solrCoreServicePort = solrCoreServicePort;
    }

    public boolean reloadCore(String coreName) {
        solrCoreServicePort.reloadCore(coreName);
        return true;
    }

}
