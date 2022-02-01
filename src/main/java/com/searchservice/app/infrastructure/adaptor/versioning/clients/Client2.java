package com.searchservice.app.infrastructure.adaptor.versioning.clients;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.searchservice.app.domain.dto.MyApiResponse2;
import com.searchservice.app.rest.VersioningTestResource;

public class Client2 extends BaseClient {
	private final Logger log = LoggerFactory.getLogger(VersioningTestResource.class);
	
    public Client2() {
        super(2);
    }

    public void doWork() throws IOException {
    	log.info("Client 2 executing........");
        MyApiResponse2 resp = super.getAs(
        		"http://localhost:8080/test-versioning/2", 
        		MyApiResponse2.class);
        log.info("Version {} ApiResponse: {}", 2, resp);
    }
}
