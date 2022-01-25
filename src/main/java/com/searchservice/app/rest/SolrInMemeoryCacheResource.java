package com.searchservice.app.rest;


import com.searchservice.app.domain.dto.SolrDocumentDTO;
import com.searchservice.app.domain.dto.SolrDocumentResponseDTO;
import com.searchservice.app.domain.port.api.SolrInMemoryCacheServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cacheschema")
public class SolrInMemeoryCacheResource {

	private final Logger log = LoggerFactory.getLogger(SolrInMemeoryCacheResource.class);

	public final SolrInMemoryCacheServicePort inMemoryCacheServicePort;

	public SolrInMemeoryCacheResource(SolrInMemoryCacheServicePort inMemoryCacheServicePort) {
		this.inMemoryCacheServicePort = inMemoryCacheServicePort;
	}


	@PostMapping("/create")
	@Operation(summary = "/create-doc", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<SolrDocumentResponseDTO> create(
			@RequestBody SolrDocumentDTO newSolrdocumentDTO) {
		log.debug("Solr Schema Create");
		log.debug("Received Schema as in Request Body: {}", newSolrdocumentDTO);
		SolrDocumentResponseDTO solrResponseDTO =
				inMemoryCacheServicePort.create(
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
		SolrDocumentResponseDTO solrDocumentResponseDTO = inMemoryCacheServicePort.delete(tableName, name);
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
		SolrDocumentResponseDTO solrSchemaDTO = inMemoryCacheServicePort.update(tableName, name, newSolrDocumentDTO);
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
		SolrDocumentResponseDTO solrResponseDTO = inMemoryCacheServicePort.get(tableName, name);
		if(solrResponseDTO.getStatusCode() == 200)
			return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
	}
}
