package com.solr.clientwrapper.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solr.clientwrapper.domain.dto.solr.SolrDocumentDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrDocumentResponseDTO;
import com.solr.clientwrapper.usecase.solr.InMemoryCache.CreateSolrDocument;
import com.solr.clientwrapper.usecase.solr.InMemoryCache.DeleteSolrDocument;
import com.solr.clientwrapper.usecase.solr.InMemoryCache.GetSolrDocument;
import com.solr.clientwrapper.usecase.solr.InMemoryCache.UpdateSolrDocument;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/cacheschema")
public class SolrInMemeoryCacheResource {

	private final Logger log = LoggerFactory.getLogger(SolrInMemeoryCacheResource.class);

	private final CreateSolrDocument createSolrDocument;
	private final DeleteSolrDocument deleteSolrDocument;
	private final UpdateSolrDocument updateSolrDocument;
	private final GetSolrDocument getSolrDocument;
	
	public SolrInMemeoryCacheResource(CreateSolrDocument createSolrDocument, DeleteSolrDocument deleteSolrDocument,
			UpdateSolrDocument updateSolrDocument, GetSolrDocument getSolrDocument) {
		super();
		this.createSolrDocument = createSolrDocument;
		this.deleteSolrDocument = deleteSolrDocument;
		this.updateSolrDocument = updateSolrDocument;
		this.getSolrDocument = getSolrDocument;
	}

	@PostMapping("/create")
	@Operation(summary = "/create-doc", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<SolrDocumentResponseDTO> create(
			@RequestBody SolrDocumentDTO newSolrdocumentDTO) {
		log.debug("Solr Schema Create");
		log.debug("Received Schema as in Request Body: {}", newSolrdocumentDTO);
		SolrDocumentResponseDTO solrResponseDTO = 
				createSolrDocument.create(
						newSolrdocumentDTO.getTableName(), 
						newSolrdocumentDTO.getName(), 
						newSolrdocumentDTO);
		if(solrResponseDTO.getStatusCode() == 200)
			return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
	}

	@DeleteMapping("/delete/{tableName}/{name}")
	@Operation(summary = "/delete-doc", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<SolrDocumentResponseDTO> delete(
			@PathVariable String tableName, 
			@PathVariable String name) {
		log.debug("Schema Delete");
		SolrDocumentResponseDTO solrDocumentResponseDTO = deleteSolrDocument.delete(tableName, name);
		if(solrDocumentResponseDTO.getStatusCode() == 200)
			return ResponseEntity.ok().body(solrDocumentResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrDocumentResponseDTO);
		
	}

	@PutMapping("/update/{tableName}/{name}")
	@Operation(summary = "/update-doc", security = @SecurityRequirement(name = "bearerAuth"))
	//@CachePut(value = "solrcache",key = "#tableName")
	public ResponseEntity<SolrDocumentResponseDTO> update(
			@PathVariable String tableName, 
			@PathVariable String name, 
			@RequestBody SolrDocumentDTO newSolrDocumentDTO) {
		log.debug("Solr doc update");
		log.debug("Received doc as in Request Body: {}", newSolrDocumentDTO);
		SolrDocumentResponseDTO solrSchemaDTO = updateSolrDocument.update(tableName, name, newSolrDocumentDTO);
		SolrDocumentResponseDTO solrResponseDTO = new SolrDocumentResponseDTO(solrSchemaDTO);
		if(solrResponseDTO.getStatusCode() == 200)
			return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
	}

	@GetMapping("/get/{tableName}/{name}")
	@Operation(summary = "/get-doc", security = @SecurityRequirement(name = "bearerAuth"))
	@Cacheable(value = "solrcache",key = "#tableName")
	public ResponseEntity<SolrDocumentResponseDTO> get(
			@PathVariable String tableName, 
			@PathVariable String name) {
		log.debug("get solar doc");
		SolrDocumentResponseDTO solrResponseDTO = getSolrDocument.get(tableName, name);
		if(solrResponseDTO.getStatusCode() == 200)
			return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
	}
}