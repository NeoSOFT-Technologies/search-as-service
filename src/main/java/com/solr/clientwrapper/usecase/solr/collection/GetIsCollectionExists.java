package com.solr.clientwrapper.usecase.solr.collection;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrCollectionServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GetIsCollectionExists {

    private final Logger log = LoggerFactory.getLogger(GetIsCollectionExists.class);

    private final SolrCollectionServicePort solrCollectionServicePort;

    public GetIsCollectionExists(SolrCollectionServicePort solrCollectionServicePort) {
        this.solrCollectionServicePort = solrCollectionServicePort;
    }

    public SolrResponseDTO isCollectionExists(String collectionName) {
        log.debug("isCollectionExists");
        return solrCollectionServicePort.isCollectionExists(collectionName);
    }

}
