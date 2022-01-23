package com.solr.clientwrapper.usecase.solr.collection;

import com.solr.clientwrapper.domain.port.api.SolrCollectionServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class GetCollectionDetails {

    private final Logger log = LoggerFactory.getLogger(GetCollectionDetails.class);

    private final SolrCollectionServicePort solrCollectionServicePort;

    public GetCollectionDetails(SolrCollectionServicePort solrCollectionServicePort) {
        this.solrCollectionServicePort = solrCollectionServicePort;
    }

    public Map getCollectionDetails(String tableName) {
        log.debug("getCollections");
        return solrCollectionServicePort.getCollectionDetails(tableName);
    }

}
