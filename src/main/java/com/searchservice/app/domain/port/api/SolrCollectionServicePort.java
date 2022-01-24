package com.solr.clientwrapper.domain.port.api;


import com.searchservice.app.domain.dto.solr.SolrResponseDTO;
import com.searchservice.app.domain.dto.solr.collection.SolrGetCapacityPlanDTO;
import com.searchservice.app.domain.dto.solr.collection.SolrGetCollectionsResponseDTO;

import java.util.Map;

public interface SolrCollectionServicePort {

    SolrResponseDTO create(String collectionName, String sku);

    SolrResponseDTO delete(String collectionName);

    //SolrResponseDTO rename(String collectionName, String collectionNewName);

    SolrGetCollectionsResponseDTO getCollections();

    SolrGetCapacityPlanDTO capacityPlans();

    SolrResponseDTO isCollectionExists(String collectionName);

    Map getCollectionDetails(String collectionName);

}
