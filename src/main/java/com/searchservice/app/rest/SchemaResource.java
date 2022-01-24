package com.searchservice.app.rest;


import com.searchservice.app.domain.dto.SolrSchemaDTO;
import com.searchservice.app.domain.dto.SolrSchemaResponseDTO;
import com.searchservice.app.usecase.solr.schema.CreateSolrSchema;
import com.searchservice.app.usecase.solr.schema.DeleteSolrSchema;
import com.searchservice.app.usecase.solr.schema.GetSolrSchema;
import com.searchservice.app.usecase.solr.schema.UpdateSolrSchema;
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

	private final CreateSolrSchema createSolrSchema;
	private final DeleteSolrSchema deleteSolrSchema;
	private final UpdateSolrSchema updateSolrSchema;
	private final GetSolrSchema getSolarSchema;

	public SchemaResource(CreateSolrSchema createSolrSchema, DeleteSolrSchema deleteSolrSchema,
						  UpdateSolrSchema updateSolrSchema, GetSolrSchema getSolarSchema) {
		this.createSolrSchema = createSolrSchema;
		this.deleteSolrSchema = deleteSolrSchema;
		this.updateSolrSchema = updateSolrSchema;
		this.getSolarSchema = getSolarSchema;
	}			

	@PostMapping
	@Operation(summary = "/ Associate  a new schema by passing tableName, name and attributes it will return created  schema.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<SolrSchemaResponseDTO> create(
			@RequestBody SolrSchemaDTO newSolrSchemaDTO) {
		log.debug("Solr Schema Create");
		log.debug("Received Schema as in Request Body: {}", newSolrSchemaDTO);
		SolrSchemaResponseDTO solrResponseDTO = 
				createSolrSchema.create(
						newSolrSchemaDTO.getTableName(), 
						newSolrSchemaDTO);
		if(solrResponseDTO.getStatusCode() == 200)
			return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
	}

	@DeleteMapping("/{tableName}")
	@Operation(summary = "/ Remove the schema by passing TableName and it will return deleted schema and statusCode. ", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<SolrSchemaResponseDTO> delete(
			@PathVariable String tableName) {
		log.debug("Schema Delete");
		SolrSchemaResponseDTO solrSchemaResponseDTO = deleteSolrSchema.delete(tableName);
		if(solrSchemaResponseDTO.getStatusCode() == 200)
			return ResponseEntity.ok().body(solrSchemaResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrSchemaResponseDTO);
		
	}

	@PutMapping("/{tableName}")
	@Operation(summary = "/ Update schema by passing TableName.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<SolrSchemaResponseDTO> update(
			@PathVariable String tableName,
			@RequestBody SolrSchemaDTO newSolrSchemaDTO) {
		log.debug("Solr schema update");
		log.debug("Received Schema as in Request Body: {}", newSolrSchemaDTO);
		SolrSchemaResponseDTO solrSchemaDTO = updateSolrSchema.update(tableName, newSolrSchemaDTO);
		SolrSchemaResponseDTO solrResponseDTO = new SolrSchemaResponseDTO(solrSchemaDTO);
		if(solrResponseDTO.getStatusCode() == 200)
			return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
	}

	@GetMapping("/{tableName}")
	@Operation(summary = "/ Get schema by passing TableName.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<SolrSchemaResponseDTO> get(
			@PathVariable String tableName) {
		log.debug("get solar schema");
		SolrSchemaResponseDTO solrResponseDTO = getSolarSchema.get(tableName);
		if(solrResponseDTO.getStatusCode() == 200)
			return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
		else
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
	}
}
