package com.solr.clientwrapper.usecase.solr.collection;

import com.solr.clientwrapper.domain.dto.solr.collection.SolrGetCollectionsResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrCollectionServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GetSolrCollections {

    private final Logger log = LoggerFactory.getLogger(GetSolrCollections.class);

    private final SolrCollectionServicePort solrCollectionServicePort;

    public GetSolrCollections(SolrCollectionServicePort solrCollectionServicePort) {
        this.solrCollectionServicePort = solrCollectionServicePort;
    }

    public SolrGetCollectionsResponseDTO getCollections() {
        log.debug("getCollections");
        return solrCollectionServicePort.getCollections();
    }
}
