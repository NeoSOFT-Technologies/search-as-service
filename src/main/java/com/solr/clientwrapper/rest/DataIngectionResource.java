package com.solr.clientwrapper.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.solr.clientwrapper.domain.service.DataIngectionService;


@RestController
@RequestMapping("/ingection")
public class DataIngectionResource {

	 private final Logger log = LoggerFactory.getLogger(DataIngectionResource.class);
		
		
		@Autowired
		DataIngectionService dataIngectionService;
		
		@PostMapping(path = "/data")	
		public ResponseEntity<String> parseArray(@RequestBody String data)  {
			
			log.debug("json array ingection ");
			String objectdata= dataIngectionService.parseSolrSchemaArray(data);
			log.debug("controller :-" + objectdata.length());

			if (!objectdata.isEmpty()) {
				// File is EXISTS
				return ResponseEntity.status(HttpStatus.OK).body(objectdata);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something is Wrong");
			}
								
		}
		
		
		@PostMapping(path = "/batch")	
		public ResponseEntity<String> parseBatch(@RequestBody String data)  {
			

			log.debug("json Batch ingection ");
			String objectBatch= dataIngectionService.parseSolrSchemaBtch(data);
			log.debug("controller :-" + objectBatch);

			if (!objectBatch.isEmpty()) {
				// File is EXISTS
				return ResponseEntity.status(HttpStatus.OK).body(objectBatch);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something is Wrong");
			}
								
		}
}
