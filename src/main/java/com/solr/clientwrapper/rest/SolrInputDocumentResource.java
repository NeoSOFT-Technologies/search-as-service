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

@RestController
public class SolrInputDocumentResource {

    private final Logger log = LoggerFactory.getLogger(SolrInputDocumentResource.class);

    CreateSolrDocument createSolrDocument;

    public SolrInputDocumentResource(CreateSolrDocument createSolrDocument) {
        this.createSolrDocument=createSolrDocument;
    }

    @PostMapping("/document/{collectionName}")
    @Operation(summary = "/add-document", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> document(@PathVariable String collectionName, @RequestBody String payload) {

        log.debug("Solr document create");

        SolrResponseDTO solrResponseDTO=createSolrDocument.addDocument(collectionName, payload);

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }

    @PostMapping("/documents/{collectionName}")
    @Operation(summary = "/add-documents", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SolrResponseDTO> documents(@PathVariable String collectionName, @RequestBody String payload) {

        log.debug("Solr documents add");

        SolrResponseDTO solrResponseDTO=createSolrDocument.addDocuments(collectionName, payload);

        if(solrResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
        }

    }


}
