package com.searchservice.app.rest;


import com.searchservice.app.domain.dto.solr.SolrResponseDTO;
import com.searchservice.app.domain.dto.solr.collection.SolrCreateCollectionDTO;
import com.searchservice.app.domain.dto.solr.collection.SolrGetCapacityPlanDTO;
import com.searchservice.app.domain.dto.solr.collection.SolrGetCollectionsResponseDTO;
import com.searchservice.app.usecase.collection.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/table")
public class TableResource {

    private final Logger log = LoggerFactory.getLogger(TableResource.class);

    private final GetCapacityPlans getCapacityPlans;
    private final CreateSolrCollection createSolrCollection;
    private final DeleteSolrCollection deleteSolrCollection;
    //private final RenameSolrCollection renameSolrCollection;
    private final GetSolrCollections getSolrCollections;
    private final GetIsCollectionExists getIsCollectionExists;
    private final GetCollectionDetails getCollectionDetails;


    public TableResource(CreateSolrCollection createSolrCollection, GetCapacityPlans getCapacityPlans, DeleteSolrCollection deleteSolrCollection, GetSolrCollections getSolrCollections, GetIsCollectionExists getIsCollectionExists, GetCollectionDetails getCollectionDetails) {
        this.createSolrCollection = createSolrCollection;
        this.getCapacityPlans = getCapacityPlans;
        this.deleteSolrCollection = deleteSolrCollection;
        //this.renameSolrCollection = renameSolrCollection;
        this.getSolrCollections = getSolrCollections;
        this.getIsCollectionExists = getIsCollectionExists;
        this.getCollectionDetails=getCollectionDetails;
    }

    @GetMapping("/capacity-plans")
    @Operation(summary = "/Get all  the capacity plans.")
    public ResponseEntity<SolrGetCapacityPlanDTO> capacityPlans() {

        log.debug("Get capacity plans");

        SolrGetCapacityPlanDTO solrGetCapacityPlanDTO=getCapacityPlans.capacityPlans();

        return ResponseEntity.status(HttpStatus.OK).body(solrGetCapacityPlanDTO);

    }

    @PostMapping
    @Operation(summary = "/ Associate the Table by passing collectionName and capacity plan and return message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> create(@RequestBody SolrCreateCollectionDTO solrCreateCollectionDTO) {

        log.debug("Solr Collection create");

        SolrResponseDTO solrResponseDTO=createSolrCollection.create(solrCreateCollectionDTO.getCollectionName(), solrCreateCollectionDTO.getSku());

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }

    @DeleteMapping("/{tableName}")
    @Operation(summary = "/ Remove the table by passing tablename and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> delete(@PathVariable String tableName) {

        log.debug("Solr Collection delete");

        SolrResponseDTO solrResponseDTO=deleteSolrCollection.delete(tableName);

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }


//    @PutMapping("/rename")
//    @Operation(summary = "/rename-table", security = @SecurityRequirement(name = "bearerAuth"))
//    public ResponseEntity<SolrResponseDTO> rename(@RequestBody SolrRenameCollectionDTO solrRenameCollectionDTO) {
//
//        log.debug("Solr Collection rename");
//
//        SolrResponseDTO solrResponseDTO=renameSolrCollection.rename(solrRenameCollectionDTO.getCollectionName(),solrRenameCollectionDTO.getCollectionNewName());
//
//        if(solrResponseDTO.getStatusCode()==200){
//            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
//        }else{
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
//        }
//
//    }


    @GetMapping
    @Operation(summary = "/ Get all the tables and it will return statusCode, message and all the collections.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrGetCollectionsResponseDTO> collections() {

        log.debug("Get all collections");

        SolrGetCollectionsResponseDTO solrGetCollectionsResponseDTO=getSolrCollections.getCollections();

        if(solrGetCollectionsResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrGetCollectionsResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrGetCollectionsResponseDTO);
        }

    }

    @GetMapping("/isTableExists/{tableName}")
    @Operation(summary = "/ For check table is exists by passing collectionName and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> isCollectionExits(@PathVariable String tableName) {

        log.debug("isCollectionExits");

        SolrResponseDTO solrResponseDTO=getIsCollectionExists.isCollectionExists(tableName);

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }

    @GetMapping("/details/{tableName}")
    @Operation(summary = "/ Get the table details like Shards, Nodes & Replication Factor.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map> getCollectionDetails(@PathVariable String tableName) {

        log.debug("getCollectionDetails");

        Map responseMap=getCollectionDetails.getCollectionDetails(tableName);

        if(!responseMap.containsKey("Error")){
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        }

    }

}
