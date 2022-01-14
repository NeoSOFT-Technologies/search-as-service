package com.solr.clientwrapper.rest;

import com.solr.clientwrapper.domain.port.api.SolrFileUploadServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class FileUploadResource {
	
	
	private final Logger log = LoggerFactory.getLogger(FileUploadResource.class);

	@Autowired
	SolrFileUploadServicePort solrFileUploadServicePort;
	
	@RequestMapping(value = "/file-upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> fileUpload(@RequestParam("file") MultipartFile file) throws IOException {

		log.debug("Multipart File Upload ");
		solrFileUploadServicePort.multipartUploader(file);
		if (!file.isEmpty()) {
			// File is EXISTS
			return ResponseEntity.status(HttpStatus.OK).body("File Upload Successfully");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is Not Upload Successfully");
		}

	}

}
