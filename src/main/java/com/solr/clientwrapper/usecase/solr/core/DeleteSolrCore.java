package com.solr.clientwrapper.usecase.solr.core;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrCoreServicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteSolrCore {

    private final SolrCoreServicePort solrCoreServicePort;

    public DeleteSolrCore(SolrCoreServicePort solrCoreServicePort) {
        this.solrCoreServicePort = solrCoreServicePort;
    }

    public SolrResponseDTO deleteCore(String coreName, boolean deleteIndex, boolean deleteDataDir, boolean deleteInstanceDir) {
        return solrCoreServicePort.deleteCore(coreName,deleteIndex,deleteDataDir,deleteInstanceDir);
    }

}
