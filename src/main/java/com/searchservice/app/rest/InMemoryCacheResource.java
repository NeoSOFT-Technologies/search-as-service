package com.searchservice.app.rest;


import com.searchservice.app.domain.dto.document.DocumentDTO;
import com.searchservice.app.domain.dto.document.DocumentResponseDTO;
import com.searchservice.app.domain.port.api.InMemoryCacheServicePort;
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
public class InMemoryCacheResource {

	private final Logger log = LoggerFactory.getLogger(InMemoryCacheResource.class);

	public final InMemoryCacheServicePort inMemoryCacheServicePort;

	public InMemoryCacheResource(InMemoryCacheServicePort inMemoryCacheServicePort) {
		this.inMemoryCacheServicePort = inMemoryCacheServicePort;
	}


	@PostMapping("/create")
	@Operation(summary = "/create-doc", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<DocumentResponseDTO> create(
			@RequestBody DocumentDTO newSolrdocumentDTO) {
		log.debug("Solr Schema Create");
		log.debug("Received Schema as in Request Body: {}", newSolrdocumentDTO);
		DocumentResponseDTO solrResponseDTO =
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
	public ResponseEntity<DocumentResponseDTO> delete(
			@PathVariable String tableName, 
			@PathVariable String name) {
		log.debug("Schema Delete");
		DocumentResponseDTO documentResponseDTO = inMemoryCacheServicePort.delete(tableName, name);
		if(documentResponseDTO.getStatusCode() == 200)
			return ResponseEntity.ok().body(documentResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(documentResponseDTO);
		
	}

	@PutMapping("/update/{tableName}/{name}")
	@Operation(summary = "/update-doc", security = @SecurityRequirement(name = "bearerAuth"))
	//@CachePut(value = "solrcache",key = "#tableName")
	public ResponseEntity<DocumentResponseDTO> update(
			@PathVariable String tableName, 
			@PathVariable String name, 
			@RequestBody DocumentDTO newDocumentDTO) {
		log.debug("Solr doc update");
		log.debug("Received doc as in Request Body: {}", newDocumentDTO);
		DocumentResponseDTO solrSchemaDTO = inMemoryCacheServicePort.update(tableName, name, newDocumentDTO);
		DocumentResponseDTO solrResponseDTO = new DocumentResponseDTO(solrSchemaDTO);
		if(solrResponseDTO.getStatusCode() == 200)
			return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
	}

	@GetMapping("/get/{tableName}/{name}")
	@Operation(summary = "/get-doc", security = @SecurityRequirement(name = "bearerAuth"))
	@Cacheable(value = "solrcache",key = "#tableName")
	public ResponseEntity<DocumentResponseDTO> get(
			@PathVariable String tableName, 
			@PathVariable String name) {
		log.debug("get solar doc");
		DocumentResponseDTO solrResponseDTO = inMemoryCacheServicePort.get(tableName, name);
		if(solrResponseDTO.getStatusCode() == 200)
			return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
	}
}
