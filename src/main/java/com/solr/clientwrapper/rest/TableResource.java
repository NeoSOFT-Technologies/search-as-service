package com.solr.clientwrapper.rest;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.domain.dto.solr.collection.*;
import com.solr.clientwrapper.usecase.solr.collection.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/table")
public class TableResource {

    private final Logger log = LoggerFactory.getLogger(TableResource.class);

    private final GetCapacityPlans getCapacityPlans;
    private final CreateSolrCollection createSolrCollection;
    private final DeleteSolrCollection deleteSolrCollection;
    private final RenameSolrCollection renameSolrCollection;
    private final GetSolrCollections getSolrCollections;
    private final GetIsCollectionExists getIsCollectionExists;


    public TableResource(CreateSolrCollection createSolrCollection, GetCapacityPlans getCapacityPlans, DeleteSolrCollection deleteSolrCollection, RenameSolrCollection renameSolrCollection, GetSolrCollections getSolrCollections, GetIsCollectionExists getIsCollectionExists) {
        this.createSolrCollection = createSolrCollection;
        this.getCapacityPlans = getCapacityPlans;
        this.deleteSolrCollection = deleteSolrCollection;
        this.renameSolrCollection = renameSolrCollection;
        this.getSolrCollections = getSolrCollections;
        this.getIsCollectionExists = getIsCollectionExists;
    }

    @GetMapping("/capacity-plans")
    @Operation(summary = "/capacity-plans")
    public ResponseEntity<SolrGetCapacityPlanDTO> capacityPlans() {

        log.debug("Get capacity plans");

        SolrGetCapacityPlanDTO solrGetCapacityPlanDTO=getCapacityPlans.capacityPlans();

        return ResponseEntity.status(HttpStatus.OK).body(solrGetCapacityPlanDTO);

    }

    @PostMapping
    @Operation(summary = "/create-table", security = @SecurityRequirement(name = "bearerAuth"))
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
    @Operation(summary = "/delete-table", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> delete(@PathVariable String tableName) {

        log.debug("Solr Collection delete");

        SolrResponseDTO solrResponseDTO=deleteSolrCollection.delete(tableName);

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }

    @PutMapping("/rename")
    @Operation(summary = "/rename-table", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> rename(@RequestBody SolrRenameCollectionDTO solrRenameCollectionDTO) {

        log.debug("Solr Collection rename");

        SolrResponseDTO solrResponseDTO=renameSolrCollection.rename(solrRenameCollectionDTO.getCollectionName(),solrRenameCollectionDTO.getCollectionNewName());

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }

    @GetMapping
    @Operation(summary = "/all-table", security = @SecurityRequirement(name = "bearerAuth"))
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
    @Operation(summary = "", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> isCollectionExits(@PathVariable String tableName) {

        log.debug("isCollectionExits");

        SolrResponseDTO solrResponseDTO=getIsCollectionExists.isCollectionExists(tableName);

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }

}
