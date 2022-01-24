package com.searchservice.app.usecase.collection;

import com.searchservice.app.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrCollectionServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteSolrCollection {

    private final Logger log = LoggerFactory.getLogger(DeleteSolrCollection.class);

    private final SolrCollectionServicePort solrCollectionServicePort;

    public DeleteSolrCollection(SolrCollectionServicePort solrCollectionServicePort) {
        this.solrCollectionServicePort = solrCollectionServicePort;
    }

    public SolrResponseDTO delete(String collectionName) {
        log.debug("delete");
        return solrCollectionServicePort.delete(collectionName );
    }
}
