package com.searchservice.app.infrastructure.adaptor.versioning.clients;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.searchservice.app.domain.dto.MyApiResponse1;
import com.searchservice.app.rest.VersioningTestResource;


public class Client1 extends BaseClient {
	private final Logger log = LoggerFactory.getLogger(VersioningTestResource.class);
	
    public Client1() {
        super(1);
    }

    public void doWork() throws IOException {
    	log.info("Client 1 executing........");
        MyApiResponse1 resp = super.getAs(
        		"http://localhost:8080/test-versioning/1", 
        		MyApiResponse1.class);
        log.info("Version {} ApiResponse: {}", 1, resp);
    }
}
