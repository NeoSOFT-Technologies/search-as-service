package com.solr.clientwrapper.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.solr.clientwrapper.domain.service.SolrParseDocSerice;

@RestController
@RequestMapping("/ingest")
public class SolrDocParserResource {
	
	
	  private final Logger log = LoggerFactory.getLogger(SolrDocParserResource.class);
	
	
	@Autowired
	SolrParseDocSerice solarParseDocSerice;
	
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> fileUpload(@RequestParam("file") MultipartFile file) {

		log.debug("Multipart File Upload ");
		solarParseDocSerice.multipartUploder(file);
		if (!file.isEmpty()) {
			// File is EXISTS
			return ResponseEntity.status(HttpStatus.OK).body("File Upload Successfully");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is Not Upload Successfully");
		}

	}

}
