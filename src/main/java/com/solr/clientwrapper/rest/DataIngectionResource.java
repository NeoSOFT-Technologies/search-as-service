package com.solr.clientwrapper.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solr.clientwrapper.domain.service.DataIngectionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/ingection")
public class DataIngectionResource {

	private final Logger log = LoggerFactory.getLogger(DataIngectionResource.class);

	@Autowired
	DataIngectionService dataIngectionService;

	@PostMapping("/batcharray/{collectionName}")
	@Operation(summary = "/add-document", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<String> batcharray(@PathVariable String collectionName, @RequestBody String data) {

		log.debug("json array ingection ");
		String objectdata = dataIngectionService.parseSolrSchemaArray(collectionName, data);
		log.debug("controller :-" + objectdata.length());		
			// File is EXISTS
			return ResponseEntity.status(HttpStatus.OK).body(objectdata);
		

	}

	@PostMapping("/batchcollection/{collectionName}")
	@Operation(summary = "/add-document", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<String> batchcollection(@PathVariable String collectionName, @RequestBody String data) {

		log.debug("json Batch ingection ");
		String objectBatch = dataIngectionService.parseSolrSchemaBatch(collectionName, data);
		log.debug("controller :-" + objectBatch);
			return ResponseEntity.status(HttpStatus.OK).body(objectBatch);

	}
}
