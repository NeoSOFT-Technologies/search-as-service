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
public class SolrInputDocumentResource {

    private final Logger log = LoggerFactory.getLogger(SolrInputDocumentResource.class);

    CreateSolrDocument createSolrDocument;

    public SolrInputDocumentResource(CreateSolrDocument createSolrDocument) {
        this.createSolrDocument=createSolrDocument;
    }

    @PostMapping("/documents/{collectionName}")
    @Operation(summary = "/add-documents", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> documents(@PathVariable String collectionName, @RequestBody String payload, @RequestParam boolean isNRT) {

        log.debug("Solr documents add");

        Instant start = Instant.now();
        SolrResponseDTO solrResponseDTO=createSolrDocument.addDocuments(collectionName, payload, isNRT);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: "+ timeElapsed.toMillis() +" milliseconds or "+(timeElapsed.toMillis()/1000)+" seconds");

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }


}
