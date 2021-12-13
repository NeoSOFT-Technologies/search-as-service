package com.solr.clientwrapper.usecase.solr.collection;

import com.solr.clientwrapper.domain.dto.solr.collection.SolrGetCapacityPlanDTO;
import com.solr.clientwrapper.domain.port.api.SolrCollectionServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GetCapacityPlans {

    private final Logger log = LoggerFactory.getLogger(GetCapacityPlans.class);

    private final SolrCollectionServicePort solrCollectionServicePort;

    public GetCapacityPlans(SolrCollectionServicePort solrCollectionServicePort) {
        this.solrCollectionServicePort = solrCollectionServicePort;
    }

    public SolrGetCapacityPlanDTO capacityPlans() {
        log.debug("capacityPlans");
        return solrCollectionServicePort.capacityPlans();
    }

}
