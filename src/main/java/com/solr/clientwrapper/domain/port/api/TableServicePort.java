package com.solr.clientwrapper.domain.port.api;


import com.solr.clientwrapper.domain.dto.ResponseDTO;
import com.solr.clientwrapper.domain.dto.table.GetCapacityPlanDTO;
import com.solr.clientwrapper.domain.dto.table.GetTablesResponseDTO;

import java.util.Map;

public interface TableServicePort {

    ResponseDTO create(String collectionName, String sku);

    ResponseDTO delete(String collectionName);

    //ResponseDTO rename(String collectionName, String collectionNewName);

    GetTablesResponseDTO getCollections();

    GetCapacityPlanDTO capacityPlans();

    ResponseDTO isCollectionExists(String collectionName);

    Map getCollectionDetails(String collectionName);

}
