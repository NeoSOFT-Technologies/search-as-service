package com.solr.clientwrapper.usecase.solr.core;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
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

    public SolrResponseDTO swapCore(String coreOne, String coreTwo) {
        return solrCoreServicePort.swapCore(coreOne,coreTwo);
    }

}
