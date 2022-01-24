package com.searchservice.app.domain.service;

import com.searchservice.app.config.CapacityPlanProperties;
import com.searchservice.app.domain.dto.solr.SolrResponseDTO;
import com.searchservice.app.domain.dto.solr.collection.SolrGetCapacityPlanDTO;
import com.searchservice.app.domain.dto.solr.collection.SolrGetCollectionsResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrCollectionServicePort;
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
public class SolrCollectionService implements SolrCollectionServicePort {

	private final Logger log = LoggerFactory.getLogger(SolrCollectionService.class);

	// http://localhost:8983/solr
	///
	@Value("${base-solr-url}")
	private String baseSolrUrl;

	@Autowired
	CapacityPlanProperties capacityPlanProperties;

	@Override
	public SolrGetCapacityPlanDTO capacityPlans() {
		List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();

		return new SolrGetCapacityPlanDTO(capacityPlans);
	}

	@Override
	public SolrResponseDTO create(String collectionName, String sku) {

		SolrResponseDTO solrResponseDTO = new SolrResponseDTO(collectionName);

		List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();
		CapacityPlanProperties.Plan selectedCapacityPlan = null;

		for (CapacityPlanProperties.Plan capacityPlan : capacityPlans) {
			if (capacityPlan.getSku().equals(sku)) {
				selectedCapacityPlan = capacityPlan;
			}
		}

		if (selectedCapacityPlan == null) {
			// INVALD SKU
			solrResponseDTO.setStatusCode(400);
			solrResponseDTO.setMessage("Invalid SKU: " + sku);
			return solrResponseDTO;
		}

		CollectionAdminRequest.Create request = CollectionAdminRequest.createCollection(collectionName,
				selectedCapacityPlan.getShards(), selectedCapacityPlan.getReplicas());

		HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();

		request.setMaxShardsPerNode(selectedCapacityPlan.getShards() * selectedCapacityPlan.getReplicas());

		String constantstring = "Exception";
		try {
			CollectionAdminResponse response = request.process(solrClient);
			solrResponseDTO.setStatusCode(200);
			solrResponseDTO.setMessage("Successfully created Solr Collection: " + collectionName);
		} catch (Exception e) {
			log.error(e.toString());
			solrResponseDTO.setStatusCode(400);
			solrResponseDTO.setMessage("Unable to create Solr Collection: " + collectionName + constantstring);
		}

		return solrResponseDTO;
	}

	@Override
	public SolrResponseDTO delete(String collectionName) {

		SolrResponseDTO solrResponseDTO = new SolrResponseDTO(collectionName);

		CollectionAdminRequest.Delete request = CollectionAdminRequest.deleteCollection(collectionName);
		CollectionAdminRequest.DeleteAlias deleteAliasRequest=CollectionAdminRequest.deleteAlias(collectionName);

		HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();

		try {
			CollectionAdminResponse response = request.process(solrClient);
			CollectionAdminResponse deleteAliasResponse = deleteAliasRequest.process(solrClient);

			solrResponseDTO.setStatusCode(200);
			solrResponseDTO.setMessage("Successfully deleted Solr Collection: " + collectionName);
		} catch (Exception e) {
			log.error(e.toString());
			solrResponseDTO.setStatusCode(400);
			solrResponseDTO.setMessage("Unable to delete Solr Collection: " + collectionName + ". Exception.");
		}

		return solrResponseDTO;
	}

//    @Override
//    public SolrResponseDTO rename(String collectionName, String collectionNewName) {
//        SolrResponseDTO solrResponseDTO=new SolrResponseDTO(collectionName);
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
	public SolrGetCollectionsResponseDTO getCollections() {

		CollectionAdminRequest.List request = new CollectionAdminRequest.List();
		HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();

		SolrGetCollectionsResponseDTO solrGetCollectionsResponseDTO = new SolrGetCollectionsResponseDTO();

		try {
			CollectionAdminResponse response = request.process(solrClient);

			solrGetCollectionsResponseDTO.setCollections((List<String>) response.getResponse().get("collections"));
			solrGetCollectionsResponseDTO.setStatusCode(200);
			solrGetCollectionsResponseDTO.setMessage("Successfully retrieved all Solr Collections");

		} catch (Exception e) {
			log.error(e.toString());

			solrGetCollectionsResponseDTO.setCollections(null);
			solrGetCollectionsResponseDTO.setStatusCode(400);
			solrGetCollectionsResponseDTO.setMessage("Unable to retrieve all Solr Collections");
		}

		return solrGetCollectionsResponseDTO;

	}

	@Override
	public SolrResponseDTO isCollectionExists(String collectionName) {

		SolrResponseDTO solrResponseDTO = new SolrResponseDTO(collectionName);

		CollectionAdminRequest.List request = new CollectionAdminRequest.List();
		HttpSolrClient solrClient = new HttpSolrClient.Builder(baseSolrUrl).build();

		try {
			CollectionAdminResponse response = request.process(solrClient);

			List<String> allCollections = (List<String>) response.getResponse().get("collections");

			if (allCollections.contains(collectionName)) {
				solrResponseDTO.setStatusCode(200);
				solrResponseDTO.setMessage("true");
			} else {
				solrResponseDTO.setStatusCode(200);
				solrResponseDTO.setMessage("false");
			}

		} catch (Exception e) {
			log.error(e.toString());

			solrResponseDTO.setStatusCode(400);
			solrResponseDTO.setMessage("Error!");

		}

		return solrResponseDTO;

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
