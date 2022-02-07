package com.searchservice.app.rest;


import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.rest.errors.InputDocumentException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/api")
public class InputDocumentResource {

    private final Logger log = LoggerFactory.getLogger(InputDocumentResource.class);

    public final InputDocumentServicePort inputDocumentServicePort;

    public InputDocumentResource(InputDocumentServicePort inputDocumentServicePort) {
        this.inputDocumentServicePort = inputDocumentServicePort;
    }



    @PostMapping("/v1/ingest-nrt/{clientid}/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResponseDTO> documents(@PathVariable String tableName,@PathVariable int clientid, @RequestBody String payload){

        log.debug("Solr documents add");
        tableName = tableName+"_"+clientid;
        Instant start = Instant.now();
        ResponseDTO solrResponseDTO= inputDocumentServicePort.addDocuments(tableName, payload);
        Instant end = Instant.now();      
        Duration timeElapsed = Duration.between(start, end);
        String result="Time taken: "+timeElapsed.toMillis()+" milliseconds";
       log.debug(result);

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }
    

	@PostMapping("/v1/ingest/{clientid}/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))

    public ResponseEntity<ResponseDTO> document(@PathVariable String tableName,@PathVariable int clientid, @RequestBody String payload) {
      

        log.debug("Solr documents add");

        tableName = tableName+"_"+clientid;
        Instant start = Instant.now();
        ResponseDTO solrResponseDTO= inputDocumentServicePort.addDocument(tableName, payload);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        String result="Time taken: "+timeElapsed.toMillis()+" milliseconds";
       log.debug(result);

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
        	throw new InputDocumentException(solrResponseDTO.getStatusCode(),solrResponseDTO.getMessage());
        }
    }


}
