package com.solr.clientwrapper.rest;

import com.solr.clientwrapper.domain.dto.solr.SolrResponseDTO;
import com.solr.clientwrapper.usecase.solr.document.CreateSolrDocument;
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

    CreateSolrDocument createSolrDocument;

    public InputDocumentResource(CreateSolrDocument createSolrDocument) {
        this.createSolrDocument=createSolrDocument;
    }

    @PostMapping("/documents/{tableName}")
    @Operation(summary = "/ For add documents we have to pass the tableName and isNRT and it will return statusCode and message.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> documents(@PathVariable String tableName, @RequestBody String payload, @RequestParam boolean isNRT) {

        log.debug("Solr documents add");

        Instant start = Instant.now();
        SolrResponseDTO solrResponseDTO=createSolrDocument.addDocuments(tableName, payload, isNRT);
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


}
