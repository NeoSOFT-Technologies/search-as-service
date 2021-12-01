package com.solr.clientwrapper.usecase.solr.core;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrCoreServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReloadSolrCore {

    private final Logger log = LoggerFactory.getLogger(ReloadSolrCore.class);

    private final SolrCoreServicePort solrCoreServicePort;

    public ReloadSolrCore(SolrCoreServicePort solrCoreServicePort) {
        this.solrCoreServicePort = solrCoreServicePort;
    }

    public SolrResponseDTO reload(String coreName) {
        log.debug("reload");
        return solrCoreServicePort.reload(coreName);
    }

}
