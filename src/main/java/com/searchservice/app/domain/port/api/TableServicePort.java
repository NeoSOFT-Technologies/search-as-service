package com.searchservice.app.domain.port.api;


import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.GetTablesResponseDTO;

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
