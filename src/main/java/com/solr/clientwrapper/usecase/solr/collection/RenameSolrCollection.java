package com.solr.clientwrapper.usecase.solr.collection;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrCollectionServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RenameSolrCollection {

    private final Logger log = LoggerFactory.getLogger(RenameSolrCollection.class);

    private final SolrCollectionServicePort solrCollectionServicePort;

    public RenameSolrCollection(SolrCollectionServicePort solrCollectionServicePort) {
        this.solrCollectionServicePort = solrCollectionServicePort;
    }

    public SolrResponseDTO rename(String collectionName, String collectionNewName) {
        log.debug("rename");
        return solrCollectionServicePort.rename(collectionName, collectionNewName);
    }

}
