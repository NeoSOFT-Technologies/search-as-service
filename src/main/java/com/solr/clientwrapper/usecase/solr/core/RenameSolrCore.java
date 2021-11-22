package com.solr.clientwrapper.usecase.solr.core;

import com.solr.clientwrapper.domain.port.api.SolrCoreServicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RenameSolrCore {

    private final SolrCoreServicePort solrCoreServicePort;

    public RenameSolrCore(SolrCoreServicePort solrCoreServicePort) {
        this.solrCoreServicePort = solrCoreServicePort;
    }

    public boolean renameCore(String coreName,String newName) {
        solrCoreServicePort.renameCore(coreName,newName);
        return true;
    }

}
