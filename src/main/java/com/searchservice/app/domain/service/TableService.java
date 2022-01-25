package com.searchservice.app.domain.service;

import com.searchservice.app.config.CapacityPlanProperties;
import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.GetTablesResponseDTO;
import com.searchservice.app.domain.port.api.TableServicePort;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TableService implements TableServicePort {

	private final Logger log = LoggerFactory.getLogger(TableService.class);

	// http://localhost:8983/solr
	@Value("${base-solr-url}")
	private String baseSolrUrl;

	@Autowired
	CapacityPlanProperties capacityPlanProperties;

	@Override
	public GetCapacityPlanDTO capacityPlans() {
		List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();

		return new GetCapacityPlanDTO(capacityPlans);
	}

	@Override
	public ResponseDTO create(String collectionName, String sku) {

		ResponseDTO responseDTO = new ResponseDTO(collectionName);

		List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();
		CapacityPlanProperties.Plan selectedCapacityPlan = null;

		for (CapacityPlanProperties.Plan capacityPlan : capacityPlans) {
			if (capacityPlan.getSku().equals(sku)) {
				selectedCapacityPlan = capacityPlan;
			}
		}

		if (selectedCapacityPlan == null) {
			// INVALD SKU
			responseDTO.setStatusCode(400);
			responseDTO.setMessage("Invalid SKU: " + sku);
			return responseDTO;
		}

		CollectionAdminRequest.Create request = CollectionAdminRequest.createCollection(collectionName,
				selectedCapacityPlan.getShards(), selectedCapacityPlan.getReplicas());

		HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();

		request.setMaxShardsPerNode(selectedCapacityPlan.getShards() * selectedCapacityPlan.getReplicas());

		String constantstring = "Exception";
		try {
			CollectionAdminResponse response = request.process(solrClient);
			responseDTO.setStatusCode(200);
			responseDTO.setMessage("Successfully created Solr Collection: " + collectionName);
		} catch (Exception e) {
			log.error(e.toString());
			responseDTO.setStatusCode(400);
			responseDTO.setMessage("Unable to create Solr Collection: " + collectionName + constantstring);
		}

		return responseDTO;
	}

	@Override
	public ResponseDTO delete(String collectionName) {

		ResponseDTO responseDTO = new ResponseDTO(collectionName);

		CollectionAdminRequest.Delete request = CollectionAdminRequest.deleteCollection(collectionName);
		CollectionAdminRequest.DeleteAlias deleteAliasRequest=CollectionAdminRequest.deleteAlias(collectionName);

		HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();

		try {
			CollectionAdminResponse response = request.process(solrClient);
			CollectionAdminResponse deleteAliasResponse = deleteAliasRequest.process(solrClient);

			responseDTO.setStatusCode(200);
			responseDTO.setMessage("Successfully deleted Solr Collection: " + collectionName);
		} catch (Exception e) {
			log.error(e.toString());
			responseDTO.setStatusCode(400);
			responseDTO.setMessage("Unable to delete Solr Collection: " + collectionName + ". Exception.");
		}

		return responseDTO;
	}

//    @Override
//    public ResponseDTO rename(String collectionName, String collectionNewName) {
//        ResponseDTO solrResponseDTO=new ResponseDTO(collectionName);
//
//        CollectionAdminRequest.Rename request = CollectionAdminRequest.renameCollection(collectionName,collectionNewName);
//        request.setFollowAliases(true);
//
//        HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();
//
//        try {
//            CollectionAdminResponse response = request.process(solrClient);
//            solrResponseDTO.setStatusCode(200);
//            solrResponseDTO.setMessage("Successfully renamed Solr Collection: "+collectionName+" to "+collectionNewName);
//        } catch (Exception e) {
//            log.error(e.toString());
//            solrResponseDTO.setStatusCode(400);
//            solrResponseDTO.setMessage("Unable to rename Solr Collection: "+collectionName+". Exception.");
//        }
//
//        return solrResponseDTO;
//    }
	@Override
	public GetTablesResponseDTO getCollections() {

		CollectionAdminRequest.List request = new CollectionAdminRequest.List();
		HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();

		GetTablesResponseDTO solrGetCollectionsResponseDTO = new GetTablesResponseDTO();

		try {
			CollectionAdminResponse response = request.process(solrClient);

			solrGetCollectionsResponseDTO.setTables((List<String>) response.getResponse().get("collections"));
			solrGetCollectionsResponseDTO.setStatusCode(200);
			solrGetCollectionsResponseDTO.setMessage("Successfully retrieved all Solr Collections");

		} catch (Exception e) {
			log.error(e.toString());

			solrGetCollectionsResponseDTO.setTables(null);
			solrGetCollectionsResponseDTO.setStatusCode(400);
			solrGetCollectionsResponseDTO.setMessage("Unable to retrieve all Solr Collections");
		}

		return solrGetCollectionsResponseDTO;

	}

	@Override
	public ResponseDTO isCollectionExists(String collectionName) {

		ResponseDTO responseDTO = new ResponseDTO(collectionName);

		CollectionAdminRequest.List request = new CollectionAdminRequest.List();
		HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();

		try {
			CollectionAdminResponse response = request.process(solrClient);

			List<String> allCollections = (List<String>) response.getResponse().get("collections");

			if (allCollections.contains(collectionName)) {
				responseDTO.setStatusCode(200);
				responseDTO.setMessage("true");
			} else {
				responseDTO.setStatusCode(200);
				responseDTO.setMessage("false");
			}

		} catch (Exception e) {
			log.error(e.toString());

			responseDTO.setStatusCode(400);
			responseDTO.setMessage("Error!");

		}

		return responseDTO;

	}


    @Override
    public Map getCollectionDetails(String collectionName) {

        HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();

        Map finalResponseMap= new HashMap();

        CollectionAdminRequest.ClusterStatus clusterStatus=new CollectionAdminRequest.ClusterStatus();

        CollectionAdminResponse response=null;

        try {
            response = clusterStatus.process(solrClient);
        } catch (Exception e) {
            log.error(e.toString());
            finalResponseMap.put("Error","Error connecting to cluster.");
            return finalResponseMap;
        }

        Map responseAsMap = response.getResponse().asMap(20);
        Map clusterResponse=(Map)responseAsMap.get("cluster");
        Map collections=(Map) clusterResponse.get("collections");

        if(collections.containsKey(collectionName)){
            finalResponseMap=(Map) collections.get(collectionName);
        }else{
            finalResponseMap.put("Error","Invalid table name.");
            return finalResponseMap;
        }

        return finalResponseMap;

    }

}
