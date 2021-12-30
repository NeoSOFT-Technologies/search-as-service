package com.solr.clientwrapper.domain.port.api;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.dto.solr.collection.SolrGetCapacityPlanDTO;
import com.solr.clientwrapper.domain.dto.solr.collection.SolrGetCollectionsResponseDTO;

public interface SolrCollectionServicePort {

    SolrResponseDTO create(String collectionName, String sku);

    SolrResponseDTO delete(String collectionName);

    SolrResponseDTO rename(String collectionName, String collectionNewName);

    SolrGetCollectionsResponseDTO getCollections();

    SolrGetCapacityPlanDTO capacityPlans();

    SolrResponseDTO isCollectionExists(String collectionName);

}
