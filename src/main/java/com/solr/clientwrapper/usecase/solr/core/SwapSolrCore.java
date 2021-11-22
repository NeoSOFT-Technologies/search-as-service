package com.solr.clientwrapper.usecase.solr.core;

import com.solr.clientwrapper.domain.port.api.SolrCoreServicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SwapSolrCore {

    private final SolrCoreServicePort solrCoreServicePort;

    public SwapSolrCore(SolrCoreServicePort solrCoreServicePort) {
        this.solrCoreServicePort = solrCoreServicePort;
    }

    public boolean swapCore(String coreOne, String coreTwo) {
        solrCoreServicePort.swapCore(coreOne,coreTwo);
        return true;
    }

}
