package com.solr.clientwrapper.rest;

import com.solr.clientwrapper.domain.dto.ResponseDTO;
import com.solr.clientwrapper.domain.dto.table.CreateTableDTO;
import com.solr.clientwrapper.domain.dto.table.GetCapacityPlanDTO;
import com.solr.clientwrapper.domain.dto.table.GetTablesResponseDTO;
import com.solr.clientwrapper.domain.port.api.TableServicePort;
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

    private final TableServicePort tableServicePort;

    public TableResource(TableServicePort tableServicePort) {
        this.tableServicePort = tableServicePort;
    }


    @GetMapping("/capacity-plans")
    @Operation(summary = "/Get all  the capacity plans.")
    public ResponseEntity<GetCapacityPlanDTO> capacityPlans() {

        log.debug("Get capacity plans");

        GetCapacityPlanDTO getCapacityPlanDTO= tableServicePort.capacityPlans();

        return ResponseEntity.status(HttpStatus.OK).body(getCapacityPlanDTO);

    }

    @PostMapping
    @Operation(summary = "/ Associate the Table by passing collectionName and capacity plan and return message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResponseDTO> create(@RequestBody CreateTableDTO createTableDTO) {

        log.debug("Solr Collection create");

        ResponseDTO responseDTO = tableServicePort.create(createTableDTO.getTableName(), createTableDTO.getSku());

        if(responseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }

    }

    @DeleteMapping("/{tableName}")
    @Operation(summary = "/ Remove the table by passing tablename and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResponseDTO> delete(@PathVariable String tableName) {

        log.debug("Solr Collection delete");

        ResponseDTO responseDTO = tableServicePort.delete(tableName);

        if(responseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }

    }


//    @PutMapping("/rename")
//    @Operation(summary = "/rename-table", security = @SecurityRequirement(name = "bearerAuth"))
//    public ResponseEntity<ResponseDTO> rename(@RequestBody RenameTableDTO solrRenameCollectionDTO) {
//
//        log.debug("Solr Collection rename");
//
//        ResponseDTO solrResponseDTO=renameSolrCollection.rename(solrRenameCollectionDTO.getCollectionName(),solrRenameCollectionDTO.getCollectionNewName());
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
    public ResponseEntity<GetTablesResponseDTO> collections() {

        log.debug("Get all collections");

        GetTablesResponseDTO getTablesResponseDTO= tableServicePort.getCollections();

        if(getTablesResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(getTablesResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getTablesResponseDTO);
        }

    }

    @GetMapping("/isTableExists/{tableName}")
    @Operation(summary = "/ For check table is exists by passing collectionName and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResponseDTO> isCollectionExits(@PathVariable String tableName) {

        log.debug("isCollectionExits");

        ResponseDTO responseDTO = tableServicePort.isCollectionExists(tableName);

        if(responseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
        }

    }

    @GetMapping("/details/{tableName}")
    @Operation(summary = "/ Get the table details like Shards, Nodes & Replication Factor.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map> getCollectionDetails(@PathVariable String tableName) {

        log.debug("getCollectionDetails");

        Map responseMap= tableServicePort.getCollectionDetails(tableName);

        if(!responseMap.containsKey("Error")){
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        }

    }

}
