package com.solr.clientwrapper.rest;


import com.solr.clientwrapper.domain.dto.schema.SchemaDTO;
import com.solr.clientwrapper.domain.dto.schema.SchemaResponseDTO;
import com.solr.clientwrapper.domain.port.api.SchemaServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schema")
public class SchemaResource {

	private final Logger log = LoggerFactory.getLogger(SchemaResource.class);

	public final SchemaServicePort schemaServicePort;

	public SchemaResource(SchemaServicePort schemaServicePort) {
		this.schemaServicePort = schemaServicePort;
	}

	@PostMapping
	@Operation(summary = "/ Associate  a new schema by passing tableName, name and attributes it will return created  schema.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<SchemaResponseDTO> create(
			@RequestBody SchemaDTO newSchemaDTO) {
		log.debug("Solr Schema Create");
		log.debug("Received Schema as in Request Body: {}", newSchemaDTO);
		SchemaResponseDTO solrResponseDTO =
				schemaServicePort.create(
						newSchemaDTO.getTableName(),
						newSchemaDTO);
		if(solrResponseDTO.getStatusCode() == 200)
			return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
	}

	@DeleteMapping("/{tableName}")
	@Operation(summary = "/ Remove the schema by passing TableName and it will return deleted schema and statusCode. ", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<SchemaResponseDTO> delete(
			@PathVariable String tableName) {
		log.debug("Schema Delete");
		SchemaResponseDTO schemaResponseDTO = schemaServicePort.delete(tableName);
		if(schemaResponseDTO.getStatusCode() == 200)
			return ResponseEntity.ok().body(schemaResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(schemaResponseDTO);
		
	}

	@PutMapping("/{tableName}")
	@Operation(summary = "/ Update schema by passing TableName.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<SchemaResponseDTO> update(
			@PathVariable String tableName,
			@RequestBody SchemaDTO newSchemaDTO) {
		log.debug("Solr schema update");
		log.debug("Received Schema as in Request Body: {}", newSchemaDTO);
		SchemaResponseDTO solrSchemaDTO = schemaServicePort.update(tableName, newSchemaDTO);
		SchemaResponseDTO solrResponseDTO = new SchemaResponseDTO(solrSchemaDTO);
		if(solrResponseDTO.getStatusCode() == 200)
			return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
	}

	@GetMapping("/{tableName}")
	@Operation(summary = "/ Get schema by passing TableName.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<SchemaResponseDTO> get(
			@PathVariable String tableName) {
		log.debug("get solar schema");
		SchemaResponseDTO solrResponseDTO = schemaServicePort.get(tableName);
		if(solrResponseDTO.getStatusCode() == 200)
			return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
	}
}
