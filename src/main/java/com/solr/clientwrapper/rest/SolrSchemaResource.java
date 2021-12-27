//package com.solr.clientwrapper.rest;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;
//import com.solr.clientwrapper.domain.dto.solr.SolrSchemaResponseDTO;
//import com.solr.clientwrapper.usecase.solr.schema.CreateSolrSchema;
//import com.solr.clientwrapper.usecase.solr.schema.DeleteSolrSchema;
//import com.solr.clientwrapper.usecase.solr.schema.GetSolrSchema;
//import com.solr.clientwrapper.usecase.solr.schema.UpdateSolrSchema;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//
//@RestController
//@RequestMapping("/schema")
//public class SolrSchemaResource {
//
//	private final Logger log = LoggerFactory.getLogger(SolrSchemaResource.class);
//
//	private final CreateSolrSchema createSolrSchema;
//	private final DeleteSolrSchema deleteSolrSchema;
//	private final UpdateSolrSchema updateSolrSchema;
//	private final GetSolrSchema getSolarSchema;
//
//	public SolrSchemaResource(CreateSolrSchema createSolrSchema, DeleteSolrSchema deleteSolrSchema,
//			UpdateSolrSchema updateSolrSchema, GetSolrSchema getSolarSchema) {
//		this.createSolrSchema = createSolrSchema;
//		this.deleteSolrSchema = deleteSolrSchema;
//		this.updateSolrSchema = updateSolrSchema;
//		this.getSolarSchema = getSolarSchema;
//	}			
//
//	@PostMapping("/create")
//	@Operation(summary = "/create-schema", security = @SecurityRequirement(name = "bearerAuth"))
//	public ResponseEntity<SolrSchemaResponseDTO> create(
//			@RequestBody SolrSchemaDTO newSolrSchemaDTO) {
//		log.debug("Solr Schema Create");
//		log.debug("Received Schema as in Request Body: {}", newSolrSchemaDTO);
//		SolrSchemaResponseDTO solrResponseDTO = 
//				createSolrSchema.create(
//						newSolrSchemaDTO.getTableName(), 
//						newSolrSchemaDTO.getName(), 
//						newSolrSchemaDTO);
//		if(solrResponseDTO.getStatusCode() == 200)
//			return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
//		else
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
//	}
//
//	@DeleteMapping("/delete/{tableName}/{name}")
//	@Operation(summary = "/delete-schema", security = @SecurityRequirement(name = "bearerAuth"))
//	public ResponseEntity<SolrSchemaResponseDTO> delete(
//			@PathVariable String tableName, 
//			@PathVariable String name) {
//		log.debug("Schema Delete");
//		SolrSchemaResponseDTO solrSchemaResponseDTO = deleteSolrSchema.delete(tableName, name);
//		if(solrSchemaResponseDTO.getStatusCode() == 200)
//			return ResponseEntity.ok().body(solrSchemaResponseDTO);
//		else
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrSchemaResponseDTO);
//		
//	}
//
//	@PutMapping("/update/{tableName}/{name}")
//	@Operation(summary = "/update-schema", security = @SecurityRequirement(name = "bearerAuth"))
//	public ResponseEntity<SolrSchemaResponseDTO> update(
//			@PathVariable String tableName, 
//			@PathVariable String name, 
//			@RequestBody SolrSchemaDTO newSolrSchemaDTO) {
//		log.debug("Solr schema update");
//		log.debug("Received Schema as in Request Body: {}", newSolrSchemaDTO);
//		SolrSchemaResponseDTO solrSchemaDTO = updateSolrSchema.update(tableName, name, newSolrSchemaDTO);
//		SolrSchemaResponseDTO solrResponseDTO = new SolrSchemaResponseDTO(solrSchemaDTO);
//		if(solrResponseDTO.getStatusCode() == 200)
//			return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
//		else
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
//	}
//
////	@GetMapping("/get/{tableName}/{name}")
////	@Operation(summary = "/get-schema", security = @SecurityRequirement(name = "bearerAuth"))
////	public ResponseEntity<SolrSchemaResponseDTO> get(
////			@PathVariable String tableName, 
////			@PathVariable String name) {
////		log.debug("get solar schema");
////		SolrSchemaResponseDTO solrResponseDTO = getSolarSchema.get(tableName, name);
////		if(solrResponseDTO.getStatusCode() == 200)
////			return ResponseEntity.status(HttpStatus.OK).body(solrResponseDTO);
////		else
////			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrResponseDTO);
////	}
//}
