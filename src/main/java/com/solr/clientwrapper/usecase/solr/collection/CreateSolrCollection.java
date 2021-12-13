package com.solr.clientwrapper.usecase.solr.collection;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrCollectionServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateSolrCollection {

    private final Logger log = LoggerFactory.getLogger(CreateSolrCollection.class);

    private final SolrCollectionServicePort solrCollectionServicePort;

    public CreateSolrCollection(SolrCollectionServicePort solrCollectionServicePort) {
        this.solrCollectionServicePort = solrCollectionServicePort;
    }

    public SolrResponseDTO create(String collectionName, String sku) {
        log.debug("create");
        return solrCollectionServicePort.create(collectionName, sku);
    }

}
